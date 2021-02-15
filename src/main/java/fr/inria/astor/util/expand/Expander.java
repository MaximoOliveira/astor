package fr.inria.astor.util.expand;

import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.*;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtBinaryOperatorImpl;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.code.CtUnaryOperatorImpl;

import java.util.*;
import java.util.stream.Collectors;

public class Expander {

    BinaryOperatorHelper binaryOperatorHelper;

    public Expander(){
        binaryOperatorHelper = new BinaryOperatorHelper();
    }


    public List<CtCodeElement> expandIngredients(List<CtCodeElement> ingredients) {
        Set<CtCodeElement> uniqueExpandedIngredients = ingredients.stream().filter(e ->
                !e.toString().equals("super()")).collect(Collectors.toSet());
        List<CtCodeElement> binaryOperators = uniqueExpandedIngredients.stream().filter(i ->
                i.getClass().equals(CtBinaryOperatorImpl.class))
                .collect(Collectors.toList());
        Set<CtBinaryOperatorImpl> expandedBinaryOperators = expandBinaryOperators(binaryOperators);
        List<CtCodeElement> invocations = uniqueExpandedIngredients.stream().filter(i -> i.getClass().equals(CtInvocationImpl.class))
                .collect(Collectors.toList());
        Set<CtInvocationImpl> expandedInvocationsWithExecutables = expandInvocationsWithExecutables(invocations);
        uniqueExpandedIngredients.addAll(expandedBinaryOperators);
        uniqueExpandedIngredients.addAll(expandedInvocationsWithExecutables);
        Set<CtUnaryOperatorImpl> expandedInvocationsWithNegation = expandInvocationsWithNegation(uniqueExpandedIngredients);
        CtCodeElement codeElement = uniqueExpandedIngredients.stream().findFirst().get();
        Set<CtBinaryOperatorImpl> binaryOpsInt = createBinaryOpsInt(codeElement);
        Set<CtCodeElement> set = Collections.newSetFromMap(new IdentityHashMap<>());
        set.addAll(uniqueExpandedIngredients);
        set.addAll(expandedInvocationsWithNegation);
        set.addAll(binaryOpsInt);
        return set.stream().filter(i -> !i.toString().equals("null")).collect(Collectors.toList());
    }

    private Set<CtUnaryOperatorImpl> expandInvocationsWithNegation(Set<CtCodeElement> invocations) {
        TypeFactory typeFactory = new TypeFactory();
        Set<CtCodeElement> invocationsWithBooleanReturnType = invocations.stream()
                .filter(ctInvocation -> ctInvocation instanceof CtInvocationImpl &&
                        ((CtInvocationImpl) ctInvocation).getType().getSimpleName().equals("boolean"))
                .collect(Collectors.toSet());
        Set<CtUnaryOperatorImpl> negatedInvocations = new HashSet<>();
        invocationsWithBooleanReturnType.forEach(invocation -> {
            CtUnaryOperatorImpl ctUnaryOperator = new CtUnaryOperatorImpl();
            CtInvocationImpl clonedInvocation = (CtInvocationImpl) ((CtInvocationImpl) invocation).clone();
            clonedInvocation.setParent(invocation.getParent());
            ctUnaryOperator.setOperand(clonedInvocation);
            ctUnaryOperator.setParent(invocation.getParent());
            CtTypeReference booleanType = typeFactory.booleanPrimitiveType();
            ctUnaryOperator.setType(booleanType);
            ctUnaryOperator.setPosition(invocation.getPosition());
            ctUnaryOperator.setKind(UnaryOperatorKind.NOT);
            // Weird case of spoon. Spoon adds parentheses when ctUnaryOperator's parent is same
            String possibleUnaryWithoutParenthesis = ctUnaryOperator.toString().substring( 1, ctUnaryOperator.toString().length() - 1);
            if(possibleUnaryWithoutParenthesis.equals(ctUnaryOperator.getParent().toString()))
                ctUnaryOperator = (CtUnaryOperatorImpl) ctUnaryOperator.getParent();
            negatedInvocations.add(ctUnaryOperator);
        });

        return negatedInvocations;
    }

    private Set<CtInvocationImpl> expandInvocationsWithExecutables(List<CtCodeElement> invocations) {
        Set<CtCodeElement> set = new HashSet<>(invocations.size());
        Set<CtCodeElement> invocationsWithUniqueTarget = invocations.stream().filter(invocation ->
                ((CtInvocationImpl) invocation).getTarget() != null
                        && set.add(((CtInvocationImpl<?>) invocation).getTarget())).collect(Collectors.toSet());

        Set<CtInvocationImpl> expandedInvocations = new HashSet<>();
        invocationsWithUniqueTarget.forEach(invocation -> {
            Collection<CtExecutableReference<?>> executables = ((CtInvocationImpl) invocation).getTarget().getType().getAllExecutables();
            executables.forEach(executable -> {
                if (!executable.getParameters().isEmpty() && (!executable.getParameters().equals(((CtInvocationImpl<?>) invocation).getExecutable().getParameters())
                        || !executable.getType().equals(((CtInvocationImpl<?>) invocation).getType())))
                    return;
                CtInvocationImpl clonedInvocation = (CtInvocationImpl) ((CtInvocationImpl) invocation).clone();
                clonedInvocation.setParent(invocation.getParent());
                clonedInvocation.setExecutable(executable);
                if (executable.getParameters().isEmpty())
                    clonedInvocation.setArguments(executable.getActualTypeArguments());
                expandedInvocations.add(clonedInvocation);
            });
        });

        return expandedInvocations;
    }

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
        CodeFactory codeFactory = MutationSupporter.factory.Code();
        binaryOperatorHelper.getArithmeticOperators().forEach(ao -> {
            CtBinaryOperatorImpl ctBinaryOperator = new CtBinaryOperatorImpl();
            ctBinaryOperator.setKind(ao);
            ctBinaryOperator.setType(new TypeFactory().integerPrimitiveType());
            CtVariableAccess leftLiteral = createVarFromLiteral(codeFactory.createLiteral(1), "varnname1");
            CtVariableAccess rightLiteral = createVarFromLiteral(codeFactory.createLiteral(2), "varname2");
            ctBinaryOperator.setLeftHandOperand(leftLiteral);
            ctBinaryOperator.setRightHandOperand(rightLiteral);
            ctBinaryOperator.setParent(clonedElement.getParent());
            ctBinaryOperator.setPosition(clonedElement.getPosition());
            set.add(ctBinaryOperator);
        });
        return set;
    }

    private CtVariableAccess createVarFromLiteral(CtLiteral ctLiteral, String varnname) {
        CtTypeReference type = ctLiteral.getType();
        CtLocalVariable local = MutationSupporter.factory.Code().createLocalVariable(type, varnname, ctLiteral);
        return MutationSupporter.factory.Code().createVariableRead(local.getReference(), false);

    }
}
