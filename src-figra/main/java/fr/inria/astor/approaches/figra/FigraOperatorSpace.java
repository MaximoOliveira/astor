package fr.inria.astor.approaches.figra;

import fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSpace;


public class FigraOperatorSpace extends OperatorSpace {

    public FigraOperatorSpace() {
        super.register(new ExpressionReplaceOperator());
    }
}