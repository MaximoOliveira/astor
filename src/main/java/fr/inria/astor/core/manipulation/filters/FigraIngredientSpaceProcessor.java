package fr.inria.astor.core.manipulation.filters;

import spoon.reflect.code.*;

public class FigraIngredientSpaceProcessor extends TargetElementProcessor<CtExpression> {

    public FigraIngredientSpaceProcessor() {
        super();

    }

    @Override
    public void process(CtExpression element) {

        if (element instanceof CtAssignment || element instanceof CtNewArray || element instanceof CtTypeAccess
                || element instanceof CtVariableWrite || element instanceof CtArrayWrite)
            return;
        if (element.getType() != null)
            this.add(element);

    }
}
