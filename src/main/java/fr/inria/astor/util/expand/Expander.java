package fr.inria.astor.util.expand;

import fr.inria.astor.core.manipulation.MutationSupporter;
import one.util.streamex.StreamEx;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtBinaryOperatorImpl;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.code.CtUnaryOperatorImpl;

import java.util.*;
import java.util.stream.Collectors;

public class Expander {

    private final BinaryOperatorHelper binaryOperatorHelper;
    private final CodeFactory codeFactory;
    private final InvocationExpander invocationExpander;

    public Expander() {
        binaryOperatorHelper = new BinaryOperatorHelper();
        codeFactory = MutationSupporter.factory.Code();
        invocationExpander = new InvocationExpander();
    }


    // TODO refactor this as well
    public List<CtCodeElement> expandIngredients(List<CtCodeElement> ingredients) {
        Set<CtCodeElement> uniqueExpandedIngredients = ingredients.stream().filter(e ->
                !e.toString().equals("super()")).collect(Collectors.toSet());
        List<CtCodeElement> binaryOperators = uniqueExpandedIngredients.stream().filter(i ->
                i.getClass().equals(CtBinaryOperatorImpl.class))
                .collect(Collectors.toList());
        Set<CtBinaryOperatorImpl> expandedBinaryOperators = expandBinaryOperators(binaryOperators);
        Set<CtInvocationImpl> invocations = uniqueExpandedIngredients.stream().filter(i -> i instanceof CtInvocationImpl)
                .map(i -> (CtInvocationImpl) i)
                .collect(Collectors.toSet());
        Set<CtInvocationImpl> expandedInvocationsWithExecutables = invocationExpander.createInvocationsWithAllPossibleExecutables(invocations);
        uniqueExpandedIngredients.addAll(expandedBinaryOperators);
        uniqueExpandedIngredients.addAll(expandedInvocationsWithExecutables);
        Set<CtUnaryOperatorImpl> expandedInvocationsWithNegation = invocationExpander.expandInvocationsWithNegation(invocations);
        CtCodeElement codeElement = uniqueExpandedIngredients.stream().findFirst().get();
        Set<CtBinaryOperatorImpl> binaryOpsInt = createBinaryOpsInt(codeElement);
        Set<Set<CtInvocationImpl>> permutatedInvocations = invocationExpander.createAllPermutationsFromInvocations(invocations);
        Set<CtCodeElement> set = Collections.newSetFromMap(new IdentityHashMap<>());
        set.addAll(uniqueExpandedIngredients);
        set.addAll(expandedInvocationsWithNegation);
        set.addAll(binaryOpsInt);
        set.addAll(permutatedInvocations.stream().flatMap(Set::stream).collect(Collectors.toSet()));
        List<CtCodeElement> noNulls = set.stream().filter(i -> !i.toString().equals("null")).collect(Collectors.toList());
        return StreamEx.of(noNulls).distinct(CtCodeElement::toString).toList();
    }

    //TODO REFACTOR THIS INTO SEVERAL METHODS and into class BinaryOperatorExpander
    private Set<CtBinaryOperatorImpl> expandBinaryOperators(List<CtCodeElement> binaryOperators) {
        Set<CtBinaryOperatorImpl> allOperators = new HashSet<>();
        binaryOperators.forEach(element -> {
            CtBinaryOperatorImpl binaryOperator = ((CtBinaryOperatorImpl) element);
            CtExpression leftExpression = binaryOperator.getLeftHandOperand();
            CtExpression rightExpression = binaryOperator.getRightHandOperand();
            CtTypeReference type = leftExpression.getType();
            CtTypeReference returnType = binaryOperator.getType();
            if (returnType.getSimpleName().equals("boolean") && type.getSimpleName().equals("int")) {
                binaryOperatorHelper.getArithmeticOperatorsWhenReturnTypeIsBoolean().forEach(bo -> {
                    CtBinaryOperatorImpl synthesizedBinaryOperator =
                            (CtBinaryOperatorImpl) MutationSupporter.factory
                                    .createBinaryOperator(leftExpression, rightExpression, bo);
                    synthesizedBinaryOperator.setPosition(element.getPosition());
                    synthesizedBinaryOperator.setParent(element.getParent());
                    synthesizedBinaryOperator.setType(((CtBinaryOperatorImpl<?>) element).getType());
                    allOperators.add(synthesizedBinaryOperator);
                });
            } else if (type.getSimpleName().equals("int")) {
                binaryOperatorHelper.getArithmeticOperators().forEach(ao -> {
                    CtBinaryOperatorImpl synthesizedBinaryOperator =
                            (CtBinaryOperatorImpl) MutationSupporter.factory
                                    .createBinaryOperator(leftExpression, rightExpression, ao);
                    synthesizedBinaryOperator.setPosition(element.getPosition());
                    synthesizedBinaryOperator.setParent(element.getParent());
                    synthesizedBinaryOperator.setType(((CtBinaryOperatorImpl<?>) element).getType());
                    allOperators.add(synthesizedBinaryOperator);
                });
            } else if (type.getSimpleName().equals("boolean")) {
                binaryOperatorHelper.getBooleanOperators().forEach(ao -> {
                    CtBinaryOperatorImpl synthesizedBinaryOperator =
                            (CtBinaryOperatorImpl) MutationSupporter.factory
                                    .createBinaryOperator(leftExpression, rightExpression, ao);
                    synthesizedBinaryOperator.setPosition(element.getPosition());
                    synthesizedBinaryOperator.setParent(element.getParent());
                    synthesizedBinaryOperator.setType(((CtBinaryOperatorImpl<?>) element).getType());
                    allOperators.add(synthesizedBinaryOperator);
                });
            }
        });
        return allOperators;
    }

    private Set<CtBinaryOperatorImpl> createBinaryOpsInt(CtCodeElement codeElement) {
        Set<CtBinaryOperatorImpl> set = new HashSet<>();
        CtCodeElement clonedElement = MutationSupporter.clone(codeElement);

        binaryOperatorHelper.getArithmeticOperators().forEach(ao -> {
            CtBinaryOperatorImpl ctBinaryOperator = new CtBinaryOperatorImpl();
            ctBinaryOperator.setKind(ao);
            ctBinaryOperator.setType(new TypeFactory().integerPrimitiveType());
            CtVariableAccess leftLiteral = createVarFromExpression(codeFactory.createLiteral(1), "varnname1");
            CtVariableAccess rightLiteral = createVarFromExpression(codeFactory.createLiteral(2), "varname2");
            ctBinaryOperator.setLeftHandOperand(leftLiteral);
            ctBinaryOperator.setRightHandOperand(rightLiteral);
            ctBinaryOperator.setParent(clonedElement.getParent());
            ctBinaryOperator.setPosition(clonedElement.getPosition());
            set.add(ctBinaryOperator);
        });
        return set;
    }

    private CtVariableAccess createVarFromExpression(CtExpression ctExpression, String varnname) {
        CtTypeReference type = ctExpression.getType();
        CtLocalVariable local = MutationSupporter.factory.Code().createLocalVariable(type, varnname, ctExpression);
        return MutationSupporter.factory.Code().createVariableRead(local.getReference(), false);

    }

}
