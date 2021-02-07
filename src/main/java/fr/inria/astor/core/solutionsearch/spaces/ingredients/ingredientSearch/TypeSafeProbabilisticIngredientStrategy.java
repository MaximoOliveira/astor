package fr.inria.astor.core.solutionsearch.spaces.ingredients.ingredientSearch;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.util.StringUtil;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtInvocationImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TypeSafeProbabilisticIngredientStrategy extends ProbabilisticIngredientStrategy {
    public TypeSafeProbabilisticIngredientStrategy(IngredientPool space) {
        super(space);
    }


    // TODO REMOVE UNSAFE INGREDIENTS FROM LIST
    @Override
    protected Ingredient getOneIngredientFromList(List<Ingredient> ingredientsAfterTransformation) {

        if (ingredientsAfterTransformation.isEmpty()) {
            log.debug("No more elements from the ingredients space");
            return null;
        }
        log.debug(String.format("Obtaining the best element out of %d: %s", ingredientsAfterTransformation.size(),
                ingredientsAfterTransformation.get(0).getCode()));
        // Return the first one because its the one with the highest probability
        return ingredientsAfterTransformation.get(0);
    }

    @Override
    public Ingredient getFixIngredient(ModificationPoint modificationPoint, AstorOperator operationType) {

        int attemptsBaseIngredients = 0;

        List<Ingredient> baseElements = getNotExhaustedBaseElements(modificationPoint, operationType);
        CtType<?> classOfMP = modificationPoint.getCodeElement().getPosition().getCompilationUnit().getMainType();
        baseElements = getValidFixIngredients(baseElements, classOfMP);

        if (baseElements == null || baseElements.isEmpty()) {
            log.debug("Any template available for mp " + modificationPoint);
            List usedElements = this.exhaustTemplates.get(getKey(modificationPoint, operationType));
            if (usedElements != null)
                log.debug("#templates already used: " + usedElements.size());
            return null;
        }

        int elementsFromFixSpace = baseElements.size();
        log.debug("Templates availables: " + elementsFromFixSpace);

        Stats.currentStat.getIngredientsStats().addSize(Stats.currentStat.getIngredientsStats().ingredientSpaceSize,
                baseElements.size());

        while (attemptsBaseIngredients < elementsFromFixSpace) {

            log.debug(String.format("Attempts Base Ingredients  %d total %d", attemptsBaseIngredients,
                    elementsFromFixSpace));

            Ingredient baseIngredient = getRandomStatementFromSpace(baseElements);

            if (baseIngredient == null || baseIngredient.getCode() == null) {

                return null;
            }

            Ingredient refinedIngredient = getNotUsedTransformedElement(modificationPoint, operationType,
                    baseIngredient);

            attemptsBaseIngredients++;

            if (refinedIngredient != null) {

                refinedIngredient.setDerivedFrom(baseIngredient.getCode());
                return refinedIngredient;
            }

        } // End while

        log.debug("--- no mutation left to apply in element "
                + StringUtil.trunc(modificationPoint.getCodeElement().getShortRepresentation())
                + ", search space size: " + elementsFromFixSpace);
        return null;

    }

    private List<Ingredient> getValidFixIngredients(List<Ingredient> baseElements, CtType<?> classOfMP) {
        List<Ingredient> ctInvocations = baseElements.stream()
                .filter(ingredient -> ingredient.getCode() instanceof CtInvocationImpl).collect(Collectors.toList());
        List<Ingredient> privateFieldsFromForeignClass = getIngredientsWithPrivateFieldsAndFromForeignClass(ctInvocations, classOfMP);
        List<Ingredient> privateMethodFromInvalidClass = getIngredientsWithPrivateMethodsAndFromInvalidClass(ctInvocations, classOfMP);
        List<Ingredient> validIngredients = new ArrayList<>(baseElements);
        validIngredients.removeAll(privateFieldsFromForeignClass);
        validIngredients.removeAll(privateMethodFromInvalidClass);
        return validIngredients.stream().filter(ingredient -> !ingredient.toString().isEmpty())
                .collect(Collectors.toList());


    }

    /**
     * Get the ingredients that represent, private or non-static methods, from a class that is different or isnt
     * a sub-class from the class of the MP. The class of the ingredient can also not be Abstract.
     *
     * @param ctInvocations
     * @param classOfMP
     * @return
     */
    private static List<Ingredient> getIngredientsWithPrivateMethodsAndFromInvalidClass(List<Ingredient> ctInvocations,
                                                                                        CtType<?> classOfMP) {
        return ctInvocations.stream()
                .filter(ingredient -> {
                    CtExecutable executable = ((CtInvocation) ingredient.getCode()).getExecutable().getDeclaration();
                    if (executable == null) return false;
                    boolean isPrivate = ((CtModifiable) executable).isPrivate();
                    boolean isStatic = ((CtModifiable) executable).isStatic();
                    boolean classIsAbstract = ingredient.getCode().getPosition().getCompilationUnit().getMainType().isAbstract();
                    CtType<?> classOfIngredient = ingredient.getCode().getPosition().getCompilationUnit().getMainType();
                    boolean fromDifferentClass = !classOfIngredient.equals(classOfMP);
                    boolean isSubtype = classOfIngredient.isSubtypeOf(classOfMP.getReference());
                    // if the method is (private or non-static) AND from a different class
                    return (isPrivate || !isStatic) && (fromDifferentClass || !isSubtype || classIsAbstract);
                }).collect(Collectors.toList());
    }

    private static List<Ingredient> getIngredientsWithPrivateFieldsAndFromForeignClass(List<Ingredient> ctInvocations,
                                                                                       CtType<?> classOfMP) {
        return ctInvocations.stream()
                .filter(ingredient -> {
                    CtTypeAccess ctTypeAccess = ingredient.getCode().getElements(new TypeFilter<>(CtTypeAccess.class))
                            .stream().findFirst().orElse(null);
                    CtFieldRead field = null;
                    if (ctTypeAccess != null)
                        field = ctTypeAccess.getParent().getElements(new TypeFilter<>(CtFieldRead.class))
                                .stream().findFirst().orElse(null);
                    if (field != null && !((CtInvocationImpl) ingredient.getCode()).getArguments().contains(field)) {
                        if (field.getVariable().getFieldDeclaration() == null) return false;
                        boolean isPrivate = field.getVariable().getFieldDeclaration().isPrivate();
                        CtType<?> classOfIngredient = ingredient.getCode().getPosition().getCompilationUnit().getMainType();
                        boolean fromDifferentClass = !classOfIngredient.equals(classOfMP);
                        return isPrivate && fromDifferentClass;
                    }
                    return false;
                }).collect(Collectors.toList());
    }


}
