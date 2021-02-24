package fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes;

import com.martiansoftware.jsap.JSAPException;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.util.expand.BinaryOperatorExpanderHelper;
import fr.inria.astor.util.expand.Expander;
import fr.inria.astor.util.expand.InvocationExpanderHelper;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeSafeExpressionTypeIngredientSpace extends ExpressionTypeIngredientSpace {

    BinaryOperatorExpanderHelper binaryOperatorHelper;
    Expander expander;
    InvocationExpanderHelper invocationExpanderHelper = new InvocationExpanderHelper();
    protected Map<String, String> keysLocation = new HashMap<>();

    public TypeSafeExpressionTypeIngredientSpace(List<TargetElementProcessor<?>> processors) throws JSAPException {
        super(processors);
        binaryOperatorHelper = new BinaryOperatorExpanderHelper();
        expander = new Expander();
    }

    @Override
    public void defineSpace(ProgramVariant variant) {

        List<CtType<?>> affected = obtainClassesFromScope(variant);
        log.debug("Creating Expression Ingredient space: ");
        for (CtType<?> classToProcess : affected) {

            List<CtCodeElement> ingredients = this.ingredientProcessor.createFixSpace(classToProcess);
            ingredients = expander.expandIngredientSpace(ingredients, classToProcess);
            TargetElementProcessor.mustClone = true;

            for (CtCodeElement originalIngredient : ingredients) {
                Ingredient ingredientOriginal = new Ingredient(originalIngredient);
                String keyLocation = mapKey(originalIngredient);
                if (originalIngredient instanceof CtExpression) {
                    CtExpression ctExpr = (CtExpression) originalIngredient;

                    if (ctExpr.getType() == null) {
                        continue;
                    }
                    List<Ingredient> ingredientsKey = getIngrediedientsFromKey(keyLocation, ctExpr);

                    if (ConfigurationProperties.getPropertyBool("cleantemplates")) {
                        MutationSupporter.getEnvironment().setNoClasspath(true);// ?
                        if (ctExpr instanceof CtLiteral) {
                            CtElement parent = ctExpr.getParent();
                            ctExpr.setParent(parent);
                        }
                        CtCodeElement templateElement = MutationSupporter.clone(ctExpr);
                        if (!(templateElement instanceof CtVariableAccess))
                            invocationExpanderHelper.formatIngredient(templateElement);

                        Ingredient templateIngredient = new Ingredient(templateElement);

                        if (ConfigurationProperties.getPropertyBool("duplicateingredientsinspace")
                                || !ingredientsKey.contains(templateIngredient)) {
                            ingredientsKey.add(templateIngredient);
                            this.allElementsFromSpace.add(templateIngredient);
                        }
                        // We must always link elements, beyond the template is
                        // duplicate or new
                        // linking
                        this.linkTemplateElements.add(templateElement.toString(), ingredientOriginal);

                    } else {

                        if (ConfigurationProperties.getPropertyBool("duplicateingredientsinspace")
                                || !ingredientsKey.contains(originalIngredient)) {
                            // log.debug("Adding ingredient: " +
                            // originalIngredient);
                            ingredientsKey.add(ingredientOriginal);
                            // all
                            this.allElementsFromSpace.add(ingredientOriginal);
                        }
                    }
                }
            }
        }
        int nrIng = 0;
        // Printing summary:
        for (Object ingList : mkp.values()) {
            nrIng += ((List) ingList).size();
        }

        // sort links
        this.linkTemplateElements = this.linkTemplateElements.getSorted();
        log.info(String.format("Ingredient search space info : number keys %d , number values %d ", mkp.keySet().size(),
                nrIng));

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

    @Override
    protected String mapKey(CtElement element) {

        String key = calculateLocation(element);

        if (key == null)
            return null;

        this.keysLocation.putIfAbsent(element.toString(), null);

        return key;
    }


}
