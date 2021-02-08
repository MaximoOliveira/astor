package fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes;

import com.martiansoftware.jsap.JSAPException;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;

import java.util.List;

public class TypeSafeExpressionTypeIngredientSpace extends ExpressionTypeIngredientSpace {
    public TypeSafeExpressionTypeIngredientSpace(List<TargetElementProcessor<?>> processors) throws JSAPException {
        super(processors);
    }

    @Override
    public List<Ingredient> getIngredients(CtElement element) {
        if (element instanceof CtExpression) {

            String keyLocation = mapKey(element);
            CtExpression ctExpr = (CtExpression) element;
            String returnTypeExpression = (ctExpr.getType() == null) ? "null" : ctExpr.getType().toString();
            List<Ingredient> ingredients = (List<Ingredient>) mkp.get(keyLocation, returnTypeExpression);

            return ingredients;
        }
        log.error("Element is not a expression: " + element.getClass().getCanonicalName());
        return null;
    }

    protected List<Ingredient> getIngrediedientsFromKey(String keyLocation, CtExpression ctExpr) {

        String returnTypeExpression = (ctExpr.getType() != null) ? ctExpr.getType().toString() : "null";

        List<Ingredient> ingredientsKey = (List<Ingredient>) mkp.get(keyLocation, returnTypeExpression);

        if (!mkp.containsKey(keyLocation, returnTypeExpression)) {
            ingredientsKey = new CacheList<>();
            mkp.put(keyLocation, returnTypeExpression, ingredientsKey);

        }
        return ingredientsKey;
    }


}

