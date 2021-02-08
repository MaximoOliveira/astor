package fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes;

import com.martiansoftware.jsap.JSAPException;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;

import java.util.List;

public class TypeSafeExpressionClassTypeIngredientSpace extends TypeSafeExpressionTypeIngredientSpace{
    public TypeSafeExpressionClassTypeIngredientSpace(List<TargetElementProcessor<?>> processors) throws JSAPException {
        super(processors);
    }

    @Override
    public List<Ingredient> getIngredients(CtElement element) {
        if (element instanceof CtExpression) {

            String keyLocation = mapKey(element);
            CtExpression ctExpr = (CtExpression) element;
            String typeExpression = ctExpr.getClass().toString();
            String returnTypeExpression = (ctExpr.getType() == null) ? "null" : ctExpr.getType().toString();
            return (List<Ingredient>) mkp.get(keyLocation, typeExpression, returnTypeExpression);
        }
        log.error("Element is not a expression: " + element.getClass().getCanonicalName());
        return null;
    }

    @Override
    protected List<Ingredient> getIngrediedientsFromKey(String keyLocation, CtExpression ctExpr) {
        String typeExpression = ctExpr.getClass().toString();

        String returnTypeExpression = (ctExpr.getType() != null) ? ctExpr.getType().toString() : "null";

        List<Ingredient> ingredientsKey = (List<Ingredient>) mkp.get(keyLocation, typeExpression,
                returnTypeExpression);

        if (!mkp.containsKey(keyLocation, typeExpression, returnTypeExpression)) {
            ingredientsKey = new CacheList<>();
            mkp.put(keyLocation, typeExpression, returnTypeExpression, ingredientsKey);

        }
        return ingredientsKey;
    }
}
