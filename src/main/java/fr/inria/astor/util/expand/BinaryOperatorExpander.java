package fr.inria.astor.util.expand;

import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtVariableAccess;
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

    TypeFactory typeFactory = MutationSupporter.factory.Type();
    CodeFactory codeFactory = MutationSupporter.factory.Code();
    BinaryOperatorExpanderHelper binaryOperatorHelper = new BinaryOperatorExpanderHelper();

    public Set<CtBinaryOperatorImpl> createBinaryOperators(Set<CtCodeElement> ingredientSpace){
        List<CtCodeElement> binaryOperators = ingredientSpace.stream().filter(i ->
                i.getClass().equals(CtBinaryOperatorImpl.class))
                .collect(Collectors.toList());
        Set<CtBinaryOperatorImpl> expandedBinaryOperators = expandBinaryOperators(binaryOperators);
        CtCodeElement codeElement = ingredientSpace.stream().findFirst().get();
        Set<CtBinaryOperatorImpl> binaryOpsReturnTypeInt = createBinaryOpsReturnTypeInt(codeElement);
        Set<CtBinaryOperatorImpl> binaryOpsBooleanReturnTypeBoolean = createBinaryOpsReturnTypeBoolean(codeElement);
        Set<CtBinaryOperatorImpl> allBinaryOperators = new HashSet<>();
        allBinaryOperators.addAll(expandedBinaryOperators);
        allBinaryOperators.addAll(binaryOpsReturnTypeInt);
        allBinaryOperators.addAll(binaryOpsBooleanReturnTypeBoolean);
        return allBinaryOperators;
    }

    public Set<CtBinaryOperatorImpl> expandBinaryOperators(List<CtCodeElement> binaryOperators) {
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

    public Set<CtBinaryOperatorImpl> createBinaryOpsReturnTypeInt(CtCodeElement codeElement) {
        Set<CtBinaryOperatorImpl> set = new HashSet<>();
        CtCodeElement clonedElement = MutationSupporter.clone(codeElement);

        binaryOperatorHelper.getArithmeticOperators().forEach(ao -> {
            CtBinaryOperatorImpl ctBinaryOperator = new CtBinaryOperatorImpl();
            ctBinaryOperator.setKind(ao);
            ctBinaryOperator.setType(new TypeFactory().integerPrimitiveType());
            CtVariableAccess leftLiteral = createVarFromType(typeFactory.integerPrimitiveType(), "varnname1");
            CtVariableAccess rightLiteral = createVarFromType(typeFactory.integerPrimitiveType(), "varname2");
            ctBinaryOperator.setLeftHandOperand(leftLiteral);
            ctBinaryOperator.setRightHandOperand(rightLiteral);
            ctBinaryOperator.setParent(clonedElement.getParent());
            ctBinaryOperator.setPosition(clonedElement.getPosition());
            set.add(ctBinaryOperator);
        });
        return set;
    }

    private Set<CtBinaryOperatorImpl> createBinaryOpsReturnTypeBoolean(CtCodeElement codeElement) {
        Set<CtBinaryOperatorImpl> set = new HashSet<>();
        CtCodeElement clonedElement = MutationSupporter.clone(codeElement);

        binaryOperatorHelper.getArithmeticOperatorsWhenReturnTypeIsBoolean().forEach(bo -> {
            CtBinaryOperatorImpl ctBinaryOperator = new CtBinaryOperatorImpl();
            ctBinaryOperator.setKind(bo);
            ctBinaryOperator.setType(new TypeFactory().booleanPrimitiveType());
            CtVariableAccess leftLiteral = createVarFromType(typeFactory.integerPrimitiveType(), "varnname1");
            CtVariableAccess rightLiteral = createVarFromType(typeFactory.integerPrimitiveType(), "varname2");
            ctBinaryOperator.setLeftHandOperand(leftLiteral);
            ctBinaryOperator.setRightHandOperand(rightLiteral);
            ctBinaryOperator.setParent(clonedElement.getParent());
            ctBinaryOperator.setPosition(clonedElement.getPosition());
            set.add(ctBinaryOperator);
        });
        return set;
    }

    private CtVariableAccess createVarFromType(CtTypeReference ctTypeReference, String name) {
        CtLocalVariableReference varReference = codeFactory.createLocalVariableReference(ctTypeReference, name);
        return codeFactory.createVariableRead(varReference, false);
    }
}
