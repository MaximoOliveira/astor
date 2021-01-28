package fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations;

import com.martiansoftware.jsap.JSAPException;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.sourcecode.VarCombinationForIngredient;
import fr.inria.astor.core.manipulation.sourcecode.VarMapping;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TypeSafeProbabilisticTransformationStrategy extends ProbabilisticTransformationStrategy {


    @Override
    public List<Ingredient> transform(ModificationPoint modificationPoint, Ingredient baseIngredient) {

        if (this.alreadyTransformed(modificationPoint, baseIngredient)) {
            return getCachedTransformations(modificationPoint, baseIngredient);
        }

        if (!this.ngramManager.initialized()) {

            logger.debug("Initializing probabilistics");
            try {
                calculateGramsProbs();
            } catch (JSAPException e) {
                logger.error(e);
                return null;
            }
        }

        List<Ingredient> transformedIngredientsResults = new ArrayList<>();

        CtCodeElement codeElementToModifyFromBase = (CtCodeElement) baseIngredient.getCode();

        if (modificationPoint.getContextOfModificationPoint().isEmpty()) {
            logger.debug("The modification point  has not any var in scope");
        }

        List<CtVariable> localVarsWithNullValue = modificationPoint.getContextOfModificationPoint().stream()
                .filter(ctVariable -> ctVariable instanceof CtLocalVariable && ctVariable.getDefaultExpression() == null).collect(Collectors.toList());
        List<CtVariable> validContext = modificationPoint.getContextOfModificationPoint().stream().collect(Collectors.toList());
        validContext.removeAll(localVarsWithNullValue);
        VarMapping mapping = VariableResolver.mapVariablesFromContext(validContext,
                codeElementToModifyFromBase);
        // if we map all variables
        if (mapping.getNotMappedVariables().isEmpty()) {
            if (mapping.getMappedVariables().isEmpty()) {
                // nothing to transform, accept the ingredient
                logger.debug("Any transf sucessful: The var Mapping is empty, we keep the ingredient");
                transformedIngredientsResults.add(baseIngredient);
            } else {// We have mappings between variables
                logger.debug("Ingredient before transformation: " + baseIngredient.getCode() + " mined from "
                        + baseIngredient.getCode().getParent(CtType.class).getQualifiedName());

                List<VarCombinationForIngredient> allCombinations = findAllVarMappingCombinationUsingProbab(
                        mapping.getMappedVariables(), modificationPoint, baseIngredient);

                if (allCombinations.size() > 0) {

                    for (VarCombinationForIngredient varCombinationForIngredient : allCombinations) {

                        DynamicIngredient ding = new DynamicIngredient(varCombinationForIngredient, mapping,
                                codeElementToModifyFromBase);
                        transformedIngredientsResults.add(ding);
                    }
                }
            }
        } else {
            logger.debug("Any transformation was sucessful: Vars not mapped: " + mapping.getNotMappedVariables());
            String varContext = "";
            for (CtVariable context : modificationPoint.getContextOfModificationPoint()) {
                varContext += context.getSimpleName() + " " + context.getType().getQualifiedName() + ", ";
            }
            logger.debug("context " + varContext);
            for (CtVariableAccess ingredient : mapping.getNotMappedVariables()) {
                logger.debug("---out_of_context: " + ingredient.getVariable().getSimpleName() + ": "
                        + ingredient.getVariable().getType().getQualifiedName());
            }
        }

        this.storingIngredients(modificationPoint, baseIngredient, transformedIngredientsResults);

        return transformedIngredientsResults;
    }

}
