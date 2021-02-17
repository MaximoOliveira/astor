package fr.inria.astor.util.expand;

import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import org.paukov.combinatorics3.Generator;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtBinaryOperatorImpl;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.code.CtUnaryOperatorImpl;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Expander {

    private final BinaryOperatorHelper binaryOperatorHelper;
    private TypeFactory typeFactory;
    private final CodeFactory codeFactory;
    private final InvocationExpander invocationExpander;

    public Expander() {
        binaryOperatorHelper = new BinaryOperatorHelper();
        typeFactory = new TypeFactory();
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
        Set<CtInvocationImpl> expandedInvocationsWithExecutables = expandInvocationsWithExecutables(invocations);
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
        return set.stream().filter(i -> !i.toString().equals("null")).collect(Collectors.toList());
    }

    private Set<CtInvocationImpl> expandInvocationsWithExecutables(Set<CtInvocationImpl> invocations) {
        Set<CtCodeElement> set = new HashSet<>(invocations.size());
        Set<CtCodeElement> invocationsWithUniqueTarget = invocations.stream().filter(invocation ->
                invocation.getTarget() != null
                        && set.add((invocation).getTarget())).collect(Collectors.toSet());

        Set<CtInvocationImpl> expandedInvocations = new HashSet<>();
        Set<CtInvocationImpl> expandedInvocationsWithNoParams = getExpandedInvocationsWithNoParams(invocationsWithUniqueTarget);
        Set<CtInvocationImpl> expandedInvocationsWithParams = getExpandedInvocationsWithParams(invocationsWithUniqueTarget);
        expandedInvocations.addAll(expandedInvocationsWithNoParams);
        expandedInvocations.addAll(expandedInvocationsWithParams);


        return expandedInvocations;
    }


    private Set<CtInvocationImpl> getExpandedInvocationsWithParams(Set<CtCodeElement> invocationsWithUniqueTarget) {
        Set<CtInvocationImpl> expandedInvocationsWithParams = new HashSet<>();
        invocationsWithUniqueTarget.forEach(invocation -> {
            Collection<CtExecutableReference<?>> executables = getExecutablesWithParams((CtInvocationImpl) MutationSupporter.clone(invocation));
            executables.forEach(executable -> {
                CtInvocationImpl clonedInvocation = (CtInvocationImpl) MutationSupporter.clone(invocation);
                clonedInvocation.setExecutable(executable);
                List<CtExpression<?>> templatedArgumentsFromInvocation = getTemplatedArgumentsFromInvocation(clonedInvocation);
                clonedInvocation.setArguments(templatedArgumentsFromInvocation);
                formatIngredient(clonedInvocation);
                expandedInvocationsWithParams.add(clonedInvocation);
            });
        });
        return expandedInvocationsWithParams;
    }

    /**
     * Given a invocation myClass.method(a, b) return a List of expressions in the form ["var_0" , "var_1"]
     * Where each expression has the same type of the original invocation's corresponding argument
     * If in this case both arguments of the invocation are of type double then restun a list of expressions with
     * type double
     *
     * @param invocation the invocation from where we create a template
     * @return the templated invocation
     */
    private List<CtExpression<?>> getTemplatedArgumentsFromInvocation(CtInvocationImpl invocation) {
        List<CtExpression<?>> invocationArguments = invocation.getArguments();
        List<CtExpression<?>> templateArguments = new LinkedList<>();
        AtomicInteger nrVars = new AtomicInteger(0); // we want a different var name for each argument
        invocationArguments.forEach(arg -> {
            String varNumber = String.valueOf(nrVars.getAndIncrement());
            CtExpression<?> clonedArg = (CtExpression<?>) MutationSupporter.clone(arg);
            CtVariableAccess newTemplate = createVarFromExpression(clonedArg, "var_" + varNumber);
            newTemplate.setParent(clonedArg.getParent());
            templateArguments.add(newTemplate);
        });

        return templateArguments;
    }

    private Set<CtInvocationImpl> getExpandedInvocationsWithNoParams(Set<CtCodeElement> invocationsWithUniqueTarget) {
        Set<CtInvocationImpl> expandedInvocationsWithNoParams = new HashSet<>();
        invocationsWithUniqueTarget.forEach(invocation -> {
            Collection<CtExecutableReference<?>> executables = getExecutablesWithNoParams((CtInvocationImpl) invocation);
            executables.forEach(executable -> {
                CtInvocationImpl clonedInvocation = (CtInvocationImpl) ((CtInvocationImpl) invocation).clone();
                clonedInvocation.setParent(invocation.getParent());
                clonedInvocation.setExecutable(executable);
                if (executable.getParameters().isEmpty())
                    clonedInvocation.setArguments(executable.getActualTypeArguments());
                expandedInvocationsWithNoParams.add(clonedInvocation);
            });
        });

        return expandedInvocationsWithNoParams;
    }

    private Set<CtExecutableReference<?>> getExecutablesWithNoParams(CtInvocationImpl ctInvocation) {
        return ctInvocation.getExecutable().getDeclaringType().getAllExecutables()
                .stream().filter(executable -> executable.getParameters().isEmpty()).collect(Collectors.toSet());
    }

    private Set<CtExecutableReference<?>> getExecutablesWithParams(CtInvocationImpl ctInvocation) {
        return ctInvocation.getExecutable().getDeclaringType().getAllExecutables()
                .stream().filter(executable -> !executable.getParameters().isEmpty()).collect(Collectors.toSet());
    }

    //TODO REFACTOR THIS INTO SEVERAL METHODS
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

    public void formatIngredient(CtElement ingredientCtElement) {

        List<CtVariableAccess> varAccessCollected = VariableResolver.collectVariableAccess(ingredientCtElement, true);
        Map<String, String> varMappings = new HashMap<>();
        int nrvar = 0;
        for (int i = 0; i < varAccessCollected.size(); i++) {
            CtVariableAccess var = varAccessCollected.get(i);

            if (VariableResolver.isStatic(var.getVariable())) {
                continue;
            }

            String abstractName = "";
            if (!varMappings.containsKey(var.getVariable().getSimpleName())) {
                String currentTypeName = var.getVariable().getType().getSimpleName();
                if (currentTypeName.contains("?")) {
                    // Any change in case of ?
                    abstractName = var.getVariable().getSimpleName();
                } else {
                    abstractName = "_" + currentTypeName + "_" + nrvar;
                }
                varMappings.put(var.getVariable().getSimpleName(), abstractName);
                nrvar++;
            } else {
                abstractName = varMappings.get(var.getVariable().getSimpleName());
            }

            var.getVariable().setSimpleName(abstractName);
            // workaround: Problems with var Shadowing
            var.getFactory().getEnvironment().setNoClasspath(true);
            if (var instanceof CtFieldAccess) {
                CtFieldAccess fieldAccess = (CtFieldAccess) var;
                fieldAccess.getVariable().setDeclaringType(null);
            }

        }

    }
}
