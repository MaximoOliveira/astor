package fr.inria.astor.util.expand;

import spoon.reflect.code.BinaryOperatorKind;

import java.util.LinkedList;
import java.util.List;

public class BinaryOperatorExpanderHelper {

    private List<BinaryOperatorKind> arithmeticOperators = new LinkedList<>();
    private List<BinaryOperatorKind> booleanOperators = new LinkedList<>();
    private List<BinaryOperatorKind> arithmeticOperatorsWhenReturnTypeIsBoolean = new LinkedList<>();

    public BinaryOperatorExpanderHelper() {
        initializeArithmeticOperators();
        initializeBooleanOperators();
        initializeArithmeticOperatorsWhenReturnTypeIsBoolean();
    }

    private void initializeArithmeticOperatorsWhenReturnTypeIsBoolean() {
        arithmeticOperatorsWhenReturnTypeIsBoolean.add(BinaryOperatorKind.GE);
        arithmeticOperatorsWhenReturnTypeIsBoolean.add(BinaryOperatorKind.GT);
        arithmeticOperatorsWhenReturnTypeIsBoolean.add(BinaryOperatorKind.LE);
        arithmeticOperatorsWhenReturnTypeIsBoolean.add(BinaryOperatorKind.NE);
        arithmeticOperatorsWhenReturnTypeIsBoolean.add(BinaryOperatorKind.EQ);
    }

    private void initializeBooleanOperators() {
        booleanOperators.add(BinaryOperatorKind.AND);
        booleanOperators.add(BinaryOperatorKind.EQ);
        booleanOperators.add(BinaryOperatorKind.NE);
        booleanOperators.add(BinaryOperatorKind.OR);
    }

    private void initializeArithmeticOperators() {
        arithmeticOperators.add(BinaryOperatorKind.PLUS);
        arithmeticOperators.add(BinaryOperatorKind.MINUS);
        arithmeticOperators.add(BinaryOperatorKind.MUL);
        arithmeticOperators.add(BinaryOperatorKind.DIV);
        arithmeticOperators.add(BinaryOperatorKind.MOD);
        arithmeticOperators.add(BinaryOperatorKind.BITAND);
        arithmeticOperators.add(BinaryOperatorKind.BITOR);
        arithmeticOperators.add(BinaryOperatorKind.BITXOR);
        arithmeticOperators.add(BinaryOperatorKind.SL);
        arithmeticOperators.add(BinaryOperatorKind.SR);
        arithmeticOperators.add(BinaryOperatorKind.USR);
    }

    public List<BinaryOperatorKind> getArithmeticOperators() {
        return arithmeticOperators;
    }

    public List<BinaryOperatorKind> getBooleanOperators() {
        return booleanOperators;
    }

    public List<BinaryOperatorKind> getArithmeticOperatorsWhenReturnTypeIsBoolean() {
        return arithmeticOperatorsWhenReturnTypeIsBoolean;
    }
}
