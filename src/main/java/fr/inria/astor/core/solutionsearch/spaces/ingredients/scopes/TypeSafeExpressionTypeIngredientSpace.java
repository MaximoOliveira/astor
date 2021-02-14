package fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes;

import com.martiansoftware.jsap.JSAPException;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.util.expand.BinaryOperatorHelper;
import spoon.reflect.code.*;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtBinaryOperatorImpl;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.code.CtLiteralImpl;
import spoon.support.reflect.code.CtUnaryOperatorImpl;
import spoon.support.reflect.cu.position.SourcePositionImpl;

import java.util.*;
import java.util.stream.Collectors;

public class TypeSafeExpressionTypeIngredientSpace extends ExpressionTypeIngredientSpace {

    BinaryOperatorHelper binaryOperatorHelper;

    public TypeSafeExpressionTypeIngredientSpace(List<TargetElementProcessor<?>> processors) throws JSAPException {
        super(processors);
        binaryOperatorHelper = new BinaryOperatorHelper();
    }

    @Override
    public void defineSpace(ProgramVariant variant) {

        List<CtType<?>> affected = obtainClassesFromScope(variant);
        log.debug("Creating Expression Ingredient space: ");
        for (CtType<?> classToProcess : affected) {

            List<CtCodeElement> ingredients = this.ingredientProcessor.createFixSpace(classToProcess);
            TargetElementProcessor.mustClone = true;

            for (CtCodeElement originalIngredient : ingredients) {
                Ingredient ingredientOriginal = new Ingredient(originalIngredient);
                String keyLocation = mapKey(originalIngredient);
                if (originalIngredient instanceof CtExpression) {
                    CtExpression ctExpr = (CtExpression) originalIngredient;
                    // String typeExpression =
                    // ctExpr.getClass().getSimpleName();

                    if (ctExpr.getType() == null) {
                        continue;
                    }
                    List<Ingredient> ingredientsKey = getIngrediedientsFromKey(keyLocation, ctExpr);

                    if (ConfigurationProperties.getPropertyBool("cleantemplates")) {
                        MutationSupporter.getEnvironment().setNoClasspath(true);// ?

                        CtCodeElement templateElement = MutationSupporter.clone(ctExpr);
                        formatIngredient(templateElement);

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

        // this.linkTemplateElements.forEach((e,v) ->
        // log.debug(String.format("k: %s v: %d ", e,v.size())));

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


}

