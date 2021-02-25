package fr.inria.astor.util.expand;

import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.*;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtBinaryOperatorImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BinaryOperatorExpander {

    private final TypeFactory typeFactory = MutationSupporter.factory.Type();
    private final CodeFactory codeFactory = MutationSupporter.factory.Code();
    private final BinaryOperatorExpanderHelper binaryOperatorHelper = new BinaryOperatorExpanderHelper();
    private final TypeInfo typeInfo = new TypeInfo();

    public Set<CtBinaryOperatorImpl> createBinaryOperators(Set<CtCodeElement> ingredientSpace) {
        List<CtCodeElement> binaryOperators = ingredientSpace.stream().filter(i ->
                i.getClass().equals(CtBinaryOperatorImpl.class))
                .collect(Collectors.toList());
        Set<CtBinaryOperatorImpl> expandedBinaryOperators = expandBinaryOperators(binaryOperators);
        CtCodeElement codeElement = ingredientSpace.stream().findFirst().get();
        Set<CtBinaryOperatorImpl> binaryOpsReturnTypeInt = createBinaryOpsReturnTypeInt(codeElement);
        Set<CtBinaryOperatorImpl> binaryOpsBooleanReturnTypeBoolean = createBinaryOpsReturnTypeBoolean(codeElement);
        Set<CtBinaryOperatorImpl> binaryOpsWithLiteral = createBinaryOpsWithLiteral(codeElement);
        Set<CtBinaryOperatorImpl> allBinaryOperators = new HashSet<>();
        allBinaryOperators.addAll(expandedBinaryOperators);
        allBinaryOperators.addAll(binaryOpsReturnTypeInt);
        allBinaryOperators.addAll(binaryOpsBooleanReturnTypeBoolean);
        allBinaryOperators.addAll(binaryOpsWithLiteral);
        return allBinaryOperators;
    }

    //TODO refactor
    public Set<CtBinaryOperatorImpl> expandBinaryOperators(List<CtCodeElement> binaryOperators) {
        Set<CtBinaryOperatorImpl> allOperators = new HashSet<>();
        binaryOperators.forEach(element -> {
            CtBinaryOperatorImpl binaryOperator = ((CtBinaryOperatorImpl) element);
            CtExpression leftExpression = binaryOperator.getLeftHandOperand();
            CtExpression rightExpression = binaryOperator.getRightHandOperand();
            CtTypeReference type = leftExpression.getType();
            CtTypeReference returnType = binaryOperator.getType();
            if (returnType.equals(typeFactory.booleanPrimitiveType()) && typeInfo.getArithmeticTypes().contains(type)) {
                binaryOperatorHelper.getArithmeticOperatorsWhenReturnTypeIsBoolean().forEach(bo -> {
                    CtBinaryOperatorImpl synthesizedBinaryOperator =
                            (CtBinaryOperatorImpl) MutationSupporter.factory
                                    .createBinaryOperator(leftExpression, rightExpression, bo);
                    synthesizedBinaryOperator.setPosition(element.getPosition());
                    synthesizedBinaryOperator.setParent(element.getParent());
                    synthesizedBinaryOperator.setType(((CtBinaryOperatorImpl<?>) element).getType());
                    allOperators.add(synthesizedBinaryOperator);
                });
            } else if (typeInfo.getArithmeticTypes().contains(type)) {
                binaryOperatorHelper.getArithmeticOperators().forEach(ao -> {
                    CtBinaryOperatorImpl synthesizedBinaryOperator =
                            (CtBinaryOperatorImpl) MutationSupporter.factory
                                    .createBinaryOperator(leftExpression, rightExpression, ao);
                    synthesizedBinaryOperator.setPosition(element.getPosition());
                    synthesizedBinaryOperator.setParent(element.getParent());
                    synthesizedBinaryOperator.setType(((CtBinaryOperatorImpl<?>) element).getType());
                    allOperators.add(synthesizedBinaryOperator);
                });
            } else if (type.equals(typeFactory.booleanPrimitiveType())) {
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

    // this can probably be removed
    public Set<CtBinaryOperatorImpl> createBinaryOpsReturnTypeInt(CtCodeElement codeElement) {
        Set<CtBinaryOperatorImpl> set = new HashSet<>();
        CtCodeElement clonedElement = MutationSupporter.clone(codeElement);

        binaryOperatorHelper.getArithmeticOperators().forEach(kind -> {
            CtVariableAccess leftExpression = createVarFromType(typeFactory.integerPrimitiveType(), "varnname1");
            CtVariableAccess rightExpression = createVarFromType(typeFactory.integerPrimitiveType(), "varname2");
           CtBinaryOperatorImpl ctBinaryOperator = createBinaryOperator(clonedElement, leftExpression, rightExpression, kind);
            ctBinaryOperator.setType(typeFactory.integerPrimitiveType());
            set.add(ctBinaryOperator);
        });
        return set;
    }

    // this can probably be removed
    private Set<CtBinaryOperatorImpl> createBinaryOpsReturnTypeBoolean(CtCodeElement codeElement) {
        Set<CtBinaryOperatorImpl> set = new HashSet<>();
        CtCodeElement clonedElement = MutationSupporter.clone(codeElement);

        binaryOperatorHelper.getArithmeticOperatorsWhenReturnTypeIsBoolean().forEach(kind -> {
            CtVariableAccess leftExpression = createVarFromType(typeFactory.integerPrimitiveType(), "varnname1");
            CtVariableAccess rightExpression = createVarFromType(typeFactory.integerPrimitiveType(), "varname2");
           CtBinaryOperatorImpl ctBinaryOperator = createBinaryOperator(clonedElement, leftExpression, rightExpression, kind);
           ctBinaryOperator.setType(typeFactory.booleanPrimitiveType());
            set.add(ctBinaryOperator);
        });
        return set;
    }

    private Set<CtBinaryOperatorImpl> createBinaryOpsWithLiteral(CtCodeElement codeElement) {
        Set<CtBinaryOperatorImpl> set = new HashSet<>();
        Set<BinaryOperatorKind> binOps = new HashSet<>();
        binOps.add(BinaryOperatorKind.PLUS);
        binOps.add(BinaryOperatorKind.MINUS);
        CtCodeElement clonedElement = MutationSupporter.clone(codeElement);
        binOps.forEach(kind -> {
            CtVariableAccess leftExpression = createVarFromType(typeFactory.integerPrimitiveType(), "varnname1");
            CtLiteral rightLiteral = codeFactory.createLiteral(1);
            CtBinaryOperatorImpl ctBinaryOperator = createBinaryOperator(clonedElement, leftExpression, rightLiteral, kind);
            ctBinaryOperator.setType(typeFactory.integerPrimitiveType());
            set.add(ctBinaryOperator);
        });
        return set;
    }

    private CtBinaryOperatorImpl createBinaryOperator(CtCodeElement codeElement, CtExpression<?> leftExpression,
                                                      CtExpression<?> rightExpression, BinaryOperatorKind kind) {
        CtBinaryOperatorImpl binaryOperator = (CtBinaryOperatorImpl) codeFactory.createBinaryOperator(leftExpression, rightExpression, kind);
        binaryOperator.setParent(codeElement.getParent());
        binaryOperator.setPosition(codeElement.getPosition());
        return binaryOperator;
    }


    private CtVariableAccess createVarFromType(CtTypeReference ctTypeReference, String name) {
        CtLocalVariableReference varReference = codeFactory.createLocalVariableReference(ctTypeReference, name);
        return codeFactory.createVariableRead(varReference, false);
    }
}
