package fr.inria.astor.util.expand;

import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.code.CtTypeAccessImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InvocationExpander {

    private final TypeFactory typeFactory = MutationSupporter.getFactory().Type();
    private final CodeFactory codeFactory = MutationSupporter.getFactory().Code();
    private final CtTypeReference<Boolean> BOOLEAN_PRIMITIVE_TYPE = typeFactory.booleanPrimitiveType();
    private final InvocationExpanderHelper invocationExpanderHelper = new InvocationExpanderHelper();


    public Set<CtCodeElement> createInvocations(Set<CtCodeElement> ingredientSpace, CtType<?> classToProcess) {
        Set<CtInvocationImpl> invocations = ingredientSpace.stream().filter(i -> i instanceof CtInvocationImpl)
                .map(i -> (CtInvocationImpl) i)
                .collect(Collectors.toSet());
        Set<CtExpression> expandedInvocationsWithNegation = expandInvocationsWithNegation(invocations);
        Set<Set<CtInvocationImpl>> permutatedInvocations = createAllPermutationsFromInvocations(invocations);
        Set<CtInvocationImpl> invocationsFromClass = getAllInvocationsFromClass(classToProcess);

        Set<CtCodeElement> expandedInvocations = new HashSet<>();
        expandedInvocations.addAll(expandedInvocationsWithNegation);
        expandedInvocations.addAll(permutatedInvocations.stream().flatMap(Set::stream).collect(Collectors.toSet()));
        expandedInvocations.addAll(invocationsFromClass);
        return expandedInvocations;
    }


    /**
     * Given a set of invocations, return a set of all different permutations for each invocation.
     * {@link InvocationExpanderHelper#createAllPermutationsFromInvocation(CtInvocationImpl)}
     *
     * @param invocations .
     * @return .
     */
    public Set<Set<CtInvocationImpl>> createAllPermutationsFromInvocations(Set<CtInvocationImpl> invocations) {
        return invocations.stream()
                .filter(invocationExpanderHelper::parameterTypesAllEqual)
                .map(invocationExpanderHelper::createAllPermutationsFromInvocation).collect(Collectors.toSet());
    }


    /**
     * Given a set of invocations, return a set of possible negated invocations.
     * We can only negate invocation of boolean type
     * {@link InvocationExpanderHelper#createNegatedInvocation(CtInvocationImpl)}
     *
     * @param invocations .
     * @return .
     */
    public Set<CtExpression> expandInvocationsWithNegation(Set<CtInvocationImpl> invocations) {
        Set<CtInvocationImpl> invocationsWithBooleanReturnType = invocations.stream()
                .filter(invocation -> invocation.getType().equals(BOOLEAN_PRIMITIVE_TYPE))
                .collect(Collectors.toSet());
        return invocationsWithBooleanReturnType.stream()
                .map(invocationExpanderHelper::createNegatedInvocation).collect(Collectors.toSet());
    }


    public Set<CtInvocationImpl> getAllInvocationsFromClass(CtType<?> clazz) {
        Set<CtMethod<?>> allMethods = clazz.getAllMethods();
        return allMethods.stream().map(method -> createInvocationFromMethod(method, clazz)).collect(Collectors.toSet());
    }

    private CtInvocationImpl createInvocationFromMethod(CtMethod<?> method, CtType<?> clazz) {
        CtTypeReference<?> typeReference = clazz.getReference();
        CtExecutableReference<?> executableReference = method.getReference();
        CtTypeAccessImpl ctTypeAccess = (CtTypeAccessImpl) codeFactory.createTypeAccess(typeReference);
        CtInvocationImpl invocation = (CtInvocationImpl) codeFactory.createInvocation(ctTypeAccess, executableReference);
        invocation.setParent(clazz);
        List<CtExpression<?>> arguments = invocationExpanderHelper.getTemplatedArgumentsFromInvocation(invocation);
        invocation.setArguments(arguments);
        invocation.setPosition(clazz.getPosition());
        invocationExpanderHelper.formatIngredient(invocation);
        return invocation;
    }


}
