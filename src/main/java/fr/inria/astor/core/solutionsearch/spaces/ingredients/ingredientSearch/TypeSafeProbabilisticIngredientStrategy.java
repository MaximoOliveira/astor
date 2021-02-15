package fr.inria.astor.core.solutionsearch.spaces.ingredients.ingredientSearch;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.ingredientbased.IngredientBasedEvolutionaryRepairApproachImpl;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.RandomManager;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.TypeSafeExpressionTypeIngredientSpace;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.util.MapList;
import fr.inria.astor.util.StringUtil;
import fr.inria.astor.util.expand.BinaryOperatorHelper;
import org.apache.log4j.Logger;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtBinaryOperatorImpl;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.code.CtUnaryOperatorImpl;

import java.util.*;
import java.util.stream.Collectors;

public class TypeSafeProbabilisticIngredientStrategy extends IngredientSearchStrategy {

    IngredientTransformationStrategy ingredientTransformationStrategy;

    protected Logger log = Logger.getLogger(this.getClass().getName());
    /**
     * Ingredients already selected
     */
    public Map<String, List<String>> appliedCache = new HashMap<String, List<String>>();
    public Map<String, List<Ingredient>> appliedIngredientsCache = new HashMap<String, List<Ingredient>>();
    public MapList<String, Ingredient> exhaustTemplates = new MapList<>();
    List<String> elements2String = null;
    Map<String, Double> probs = null;
    BinaryOperatorHelper binaryOperatorHelper = new BinaryOperatorHelper();

    public TypeSafeProbabilisticIngredientStrategy(IngredientPool space) {
        super(space);

        try {
            this.ingredientTransformationStrategy = IngredientBasedEvolutionaryRepairApproachImpl
                    .retrieveIngredientTransformationStrategy();
        } catch (Exception e) {
            log.error(e);
        }

    }


    // TODO REMOVE UNSAFE INGREDIENTS FROM LIST
    protected Ingredient getOneIngredientFromList(List<Ingredient> ingredientsAfterTransformation) {

        if (ingredientsAfterTransformation.isEmpty()) {
            log.debug("No more elements from the ingredients space");
            return null;
        }

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

    public List<Ingredient> getNotExhaustedBaseElements(ModificationPoint modificationPoint,
                                                        AstorOperator operationType) {


        List<Ingredient> elements = getNotExhaustedBaseElements2(modificationPoint, operationType);
        if (elements == null) {
            return null;
        }

        if (ConfigurationProperties.getPropertyBool("frequenttemplate")) {
            log.debug("Defining template order for " + modificationPoint);
            TypeSafeExpressionTypeIngredientSpace space = (TypeSafeExpressionTypeIngredientSpace) this.getIngredientSpace();

            // Ingredients from space
            // ingredients to string
            elements2String = new ArrayList<>();
            for (Ingredient cm : elements) {
                elements2String.add(cm.toString());
            }
            // Obtaining counting of templates from the space
            MapList mp = new MapList<>();
            mp.putAll(space.linkTemplateElements);
            mp.keySet().removeIf(e -> !elements2String.contains(e));

            // Obtaining accumulate frequency of elements
            probs = mp.getProb().getProbAccumulative();

        }
        return elements;

    }

    public List<Ingredient> getNotExhaustedBaseElements2(ModificationPoint modificationPoint,
                                                         AstorOperator operationType) {

        String type = null;
        if (operationType instanceof ReplaceOp) {
            type = modificationPoint.getCodeElement().getClass().getSimpleName();
        }

        List<Ingredient> elements = null;
        if (type == null) {
            elements = this.ingredientSpace.getIngredients(modificationPoint.getCodeElement());

        } else {
            elements = this.ingredientSpace.getIngredients(modificationPoint.getCodeElement(), type);
        }

        if (elements == null)
            return null;

        List<Ingredient> uniques = new ArrayList<>(elements);

        String key = getKey(modificationPoint, operationType);
        List<Ingredient> exhaustives = this.exhaustTemplates.get(key);

        if (exhaustives != null) {
            boolean removed = uniques.removeAll(exhaustives);
        }
        return uniques;
    }

    protected Ingredient getRandomStatementFromSpace(List<Ingredient> fixSpace) {

        if (ConfigurationProperties.getPropertyBool("frequenttemplate"))

            return getTemplateByWeighted(fixSpace, elements2String, probs);
        else {
            if (fixSpace == null)
                return null;
            int size = fixSpace.size();
            int index = RandomManager.nextInt(size);
            return fixSpace.get(index);
        }
    }

    private Ingredient getTemplateByWeighted(List<Ingredient> elements, List<String> elements2String,
                                             Map<String, Double> probs) {
        // Random value
        Double randomElement = RandomManager.nextDouble();

        int i = 0;
        for (String template : probs.keySet()) {
            double probTemplate = probs.get(template);
            if (randomElement <= probTemplate) {
                int index = elements2String.indexOf(template);
                Ingredient templateElement = elements.get(index);
                log.debug("BI with prob " + probTemplate + " " + (i++) + " " + templateElement);
                return templateElement;
            }
        }
        return null;
    }

    public String getKey(ModificationPoint modPoint, AstorOperator operator) {
        String lockey = modPoint.getCodeElement().getPosition().toString() + "-" + modPoint.getCodeElement() + "-"
                + operator.toString();
        return lockey;
    }

    public IngredientTransformationStrategy getIngredientTransformationStrategy() {
        return ingredientTransformationStrategy;
    }

    public void setIngredientTransformationStrategy(IngredientTransformationStrategy ingredientTransformationStrategy) {
        this.ingredientTransformationStrategy = ingredientTransformationStrategy;
    }

    public List<Ingredient> getInstancesFromBase(ModificationPoint modificationPoint, AstorOperator operator,
                                                 Ingredient baseIngredient) {
        List<Ingredient> ingredientsAfterTransformation = null;
        String keyBaseIngredient = getBaseIngredientKey(modificationPoint, operator, baseIngredient);

        if (appliedIngredientsCache.containsKey(keyBaseIngredient)) {
            log.debug("Retrieving already calculated transformations");
            ingredientsAfterTransformation = appliedIngredientsCache.get(keyBaseIngredient);

            // We try two cases: null (template cannot be instantiated) or
            // empty (all combination were already tested)
            if (ingredientsAfterTransformation == null) {
                log.debug("Already instantiated template but without valid instance on this MP, update stats "
                        + baseIngredient);
                return null;
            } else if (ingredientsAfterTransformation.isEmpty()) {
                log.debug("All instances were already tried, exit without update stats."
                        + StringUtil.trunc(baseIngredient.getCode()));
                return null;
            } else {
                // We have still ingredients to apply
                Stats.currentStat.getIngredientsStats().addSize(
                        Stats.currentStat.getIngredientsStats().combinationByIngredientSize,
                        ingredientsAfterTransformation.size());
            }

        } else {
            log.debug("Calculating transformations");
            try {
                ingredientsAfterTransformation = ingredientTransformationStrategy.transform(modificationPoint,
                        baseIngredient);
                if (ingredientsAfterTransformation != null && !ingredientsAfterTransformation.isEmpty()) {
                    appliedIngredientsCache.put(keyBaseIngredient, ingredientsAfterTransformation);
                    Stats.currentStat.getIngredientsStats().addSize(
                            Stats.currentStat.getIngredientsStats().combinationByIngredientSize,
                            ingredientsAfterTransformation.size());

                } else {
                    log.debug(
                            "The transformation strategy has not returned any Valid transformed ingredient for ingredient base "
                                    + StringUtil.trunc(baseIngredient.getCode()));

                    appliedIngredientsCache.put(keyBaseIngredient, null);
                    Stats.currentStat.getIngredientsStats()
                            .addSize(Stats.currentStat.getIngredientsStats().combinationByIngredientSize, 0);
                    exhaustTemplates.add(getKey(modificationPoint, operator), baseIngredient);
                }
            } catch (Throwable e) {
                log.equals("errooor mp:" + modificationPoint + " ingredient " + baseIngredient);
            }
        }
        return ingredientsAfterTransformation;
    }

    public Ingredient getNotUsedTransformedElement(ModificationPoint modificationPoint, AstorOperator operator,
                                                   Ingredient baseIngredient) {
        List<Ingredient> ingredientsAfterTransformation = getInstancesFromBase(modificationPoint, operator,
                baseIngredient);
        if (ingredientsAfterTransformation == null) {
            return null;
        }

        return getNotUsedTransformedElement(modificationPoint, operator, baseIngredient,
                ingredientsAfterTransformation);
    }

    private String getBaseIngredientKey(ModificationPoint modificationPoint, AstorOperator operator,
                                        Ingredient baseIngredient) {
        return getKey(modificationPoint, operator) + baseIngredient.toString();
    }

    /**
     * Returns randomly an ingredient
     *
     * @param modificationPoint
     * @param operator
     * @param baseIngredient
     * @return
     */
    private Ingredient getNotUsedTransformedElement(ModificationPoint modificationPoint, AstorOperator operator,
                                                    Ingredient baseIngredient, List<Ingredient> ingredientsAfterTransformation) {

        log.debug(String.format("Valid Transformed ingredients in mp: %s,  base ingr: %s, : size (%d) ",
                StringUtil.trunc(modificationPoint.getCodeElement()), StringUtil.trunc(baseIngredient.getCode()),
                ingredientsAfterTransformation.size()));

        if (ingredientsAfterTransformation.isEmpty()) {
            log.debug("No more combination");
            return null;
        }

        Ingredient transformedIngredient = null;
        int attempts = 0;
        while (attempts <= ingredientsAfterTransformation.size()) {

            transformedIngredient = getOneIngredientFromList(ingredientsAfterTransformation);

            if (transformedIngredient == null) {
                log.debug("transformed ingredient null for " + modificationPoint.getCodeElement());
                continue;
            }

            boolean removed = ingredientsAfterTransformation.remove(transformedIngredient);
            if (!removed) {
                log.debug("Not Removing ingredient from cache");
            } else {
                if (ingredientsAfterTransformation.isEmpty()) {
                    exhaustTemplates.add(getKey(modificationPoint, operator), baseIngredient);
                }
            }

            attempts++;
            log.debug(String.format("\nAttempts In Transformed Ingredient  %d total %d", attempts,
                    ingredientsAfterTransformation.size()));

            // we check if was applied
            boolean alreadyApplied = alreadySelected(modificationPoint, transformedIngredient.getCode(), operator);

            if (!alreadyApplied) {
                return transformedIngredient;
            }

        }
        log.debug(String.format("After %d attempts, we could NOT find an ingredient in a space of size %d", attempts,
                ingredientsAfterTransformation.size()));
        return null;

    }

    /**
     * Check if the ingredient was already used
     *
     * @return
     */
    protected boolean alreadySelected(ModificationPoint gen, CtElement fixElement, AstorOperator operator) {
        // we add the instance identifier to the patch.
        String lockey = getKey(gen, operator);
        String fix = "";
        try {
            fix = fixElement.toString();
        } catch (Exception e) {
            log.error("to string fails");
        }
        List<String> prev = appliedCache.get(lockey);
        // The element does not have any mutation applied
        if (prev == null) {
            prev = new ArrayList<String>();
            prev.add(fix);
            appliedCache.put(lockey, prev);
            log.debug(
                    "\nChache: New Element with new Key: " + StringUtil.trunc(fix) + " in " + StringUtil.trunc(lockey));
            return false;
        } else {
            // The element has mutation applied
            if (prev.contains(fix)) {
                log.debug("\nChache: Already stored: " + StringUtil.trunc(fix) + " in " + (lockey));
                return true;
            } else {
                prev.add(fix);
                log.debug("\nChache: New Element with existing Key: " + StringUtil.trunc(fix) + " in "
                        + StringUtil.trunc(lockey));
                return false;
            }
        }
    }

    public void formatIngredient(CtElement ingredientCtElement) {

        // log.debug("\n------" + ingredientCtElement);
        List<CtVariableAccess> varAccessCollected = VariableResolver.collectVariableAccess(ingredientCtElement, true);
        Map<String, String> varMappings = new HashMap<>();
        int nrvar = 0;
        for (int i = 0; i < varAccessCollected.size(); i++) {
            CtVariableAccess var = varAccessCollected.get(i);

            if (VariableResolver.isStatic(var.getVariable())) {
                continue;
            }

            String abstractName = "";
            if (!varMappings.containsKey(var.getVariable().getSimpleName())) {
                String currentTypeName = var.getVariable().getType().getSimpleName();
                if (currentTypeName.contains("?")) {
                    // Any change in case of ?
                    abstractName = var.getVariable().getSimpleName();
                } else {
                    abstractName = "_" + currentTypeName + "_" + nrvar;
                }
                varMappings.put(var.getVariable().getSimpleName(), abstractName);
                nrvar++;
            } else {
                abstractName = varMappings.get(var.getVariable().getSimpleName());
            }

            var.getVariable().setSimpleName(abstractName);
            // workaround: Problems with var Shadowing
            var.getFactory().getEnvironment().setNoClasspath(true);
            if (var instanceof CtFieldAccess) {
                CtFieldAccess fieldAccess = (CtFieldAccess) var;
                fieldAccess.getVariable().setDeclaringType(null);
            }

        }

    }


}
