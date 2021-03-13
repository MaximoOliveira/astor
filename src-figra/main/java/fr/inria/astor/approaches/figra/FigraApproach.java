package fr.inria.astor.approaches.figra;

import com.martiansoftware.jsap.JSAPException;
import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.FigraExpressionClassTypeIngredientSpace;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.FigraExpressionTypeIngredientSpace;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.main.evolution.ExtensionPoints;

import java.util.List;

public class FigraApproach extends JGenProg {

    public FigraApproach(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
        super(mutatorExecutor, projFacade);
        ConfigurationProperties.setProperty("cleantemplates", "true");

        if (!ConfigurationProperties.hasProperty(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier)) {

            if (ConfigurationProperties.getPropertyBool("probabilistictransformation")) {
                ConfigurationProperties.setProperty(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier,
                        "figra-name-probability-based");
            } else
                ConfigurationProperties.setProperty(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier,
                        "random-variable-replacement");
        }

        ConfigurationProperties.setProperty(ExtensionPoints.TARGET_CODE_PROCESSOR.identifier, "figra-expression");
        ConfigurationProperties.setProperty(ExtensionPoints.OPERATORS_SPACE.identifier, "figra-space");
        setPropertyIfNotDefined(ExtensionPoints.INGREDIENT_SEARCH_STRATEGY.identifier, "figra-name-probability-based");
        ConfigurationProperties.setProperty(ExtensionPoints.SUSPICIOUS_NAVIGATION.identifier, "FIGRA_WEIGHT");

    }

    @Override
    protected void loadIngredientPool() throws Exception {
        List<TargetElementProcessor<?>> ingredientProcessors = this.getTargetElementProcessors();
        FigraExpressionTypeIngredientSpace ingredientspace = new FigraExpressionClassTypeIngredientSpace(ingredientProcessors);
        String scope = ConfigurationProperties.getProperty(ExtensionPoints.INGREDIENT_STRATEGY_SCOPE.identifier);
        if (scope != null) {
            ingredientspace.scope = IngredientPoolScope.valueOf(scope.toUpperCase());
        }
        this.setIngredientPool(ingredientspace);
    }
}
