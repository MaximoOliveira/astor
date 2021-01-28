package fr.inria.astor.approaches.typesafe;

import fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSpace;


public class TypeSafeOperatorSpace extends OperatorSpace {

    public TypeSafeOperatorSpace() {
        super.register(new ExpressionReplaceOperator());
    }
}