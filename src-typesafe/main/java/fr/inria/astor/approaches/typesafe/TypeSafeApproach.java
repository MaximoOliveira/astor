package fr.inria.astor.approaches.typesafe;

import com.martiansoftware.jsap.JSAPException;
import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.*;
import fr.inria.main.evolution.ExtensionPoints;

import java.util.List;

public class TypeSafeApproach extends JGenProg {

    public TypeSafeApproach(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
        super(mutatorExecutor, projFacade);
        // Default configuration of typesafe mutator:
        ConfigurationProperties.setProperty("cleantemplates", "true");

        if (!ConfigurationProperties.hasProperty(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier)) {

            if (ConfigurationProperties.getPropertyBool("probabilistictransformation")) {
                ConfigurationProperties.setProperty(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier,
                        "typesafe-name-probability-based");
            } else
                ConfigurationProperties.setProperty(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier,
                        "random-variable-replacement");
        }

        ConfigurationProperties.setProperty(ExtensionPoints.TARGET_CODE_PROCESSOR.identifier, "expression");
        ConfigurationProperties.setProperty(ExtensionPoints.OPERATORS_SPACE.identifier, "typesafe-space");
        setPropertyIfNotDefined(ExtensionPoints.INGREDIENT_SEARCH_STRATEGY.identifier, "typesafe-name-probability-based");
        ConfigurationProperties.setProperty(ExtensionPoints.SUSPICIOUS_NAVIGATION.identifier, "TYPESAFE_WEIGHT");

    }

    @Override
    protected void loadIngredientPool() throws JSAPException, Exception {
        List<TargetElementProcessor<?>> ingredientProcessors = this.getTargetElementProcessors();
        TypeSafeExpressionTypeIngredientSpace ingredientspace = new TypeSafeExpressionClassTypeIngredientSpace(ingredientProcessors);
        String scope = ConfigurationProperties.getProperty(ExtensionPoints.INGREDIENT_STRATEGY_SCOPE.identifier);
        if (scope != null) {
            ingredientspace.scope = IngredientPoolScope.valueOf(scope.toUpperCase());
        }
        this.setIngredientPool(ingredientspace);
    }
}
