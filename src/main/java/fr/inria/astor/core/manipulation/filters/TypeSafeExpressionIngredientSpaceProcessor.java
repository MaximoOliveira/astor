package fr.inria.astor.core.manipulation.filters;

import spoon.reflect.code.*;

public class TypeSafeExpressionIngredientSpaceProcessor extends TargetElementProcessor<CtExpression> {

    public TypeSafeExpressionIngredientSpaceProcessor() {
        super();

    }

    @Override
    public void process(CtExpression element) {

        if (element instanceof CtAssignment || element instanceof CtNewArray || element instanceof CtTypeAccess)
            return;
        if (element.getType() != null)
            this.add(element);

    }
}
