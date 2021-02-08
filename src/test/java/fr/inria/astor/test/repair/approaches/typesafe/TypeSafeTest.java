package fr.inria.astor.test.repair.approaches.typesafe;

import fr.inria.astor.approaches.typesafe.TypeSafeApproach;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.ExpressionTypeIngredientSpace;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class TypeSafeTest {

    @Test
    public void testTypeSafeM74() throws Exception {
        CommandSummary command = MathCommandsTests.getMath74Command();

        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;

        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-flthreshold", "0.5");
        command.command.put("-maxtime", "60");
        command.command.put("-seed", "400");
        command.command.put("-maxgen", "8");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        command.command.put("-parameters", "maxCombinationVariableLimit:true:disablelog:false");
        command.command.put("-maxVarCombination", "100");
        command.command.put("-stopfirst", "false");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafeApproach = (TypeSafeApproach) main1.getEngine();

    }

    @Test
    public void testTypeSafeM57() throws Exception {
        CommandSummary command = MathCommandsTests.getMath57Command();

        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;

        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-flthreshold", "0.1");
        command.command.put("-maxtime", "60");
        command.command.put("-seed", "400");
        command.command.put("-maxgen", "100");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        command.command.put("-parameters", "maxCombinationVariableLimit:true:disablelog:false");
        command.command.put("-maxVarCombination", "1000");
        command.command.put("-stopfirst", "false");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafeApproach = (TypeSafeApproach) main1.getEngine();

    }

    @Test
    public void testTypeSafeM20() throws Exception {
        CommandSummary command = MathCommandsTests.getMath20Command();

        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;

        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-flthreshold", "0.5");
        command.command.put("-maxtime", "60");
        command.command.put("-seed", "400");
        command.command.put("-maxgen", "100");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        command.command.put("-parameters", "maxCombinationVariableLimit:true:disablelog:false");
        command.command.put("-maxVarCombination", "100");
        command.command.put("-stopfirst", "false");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafeApproach = (TypeSafeApproach) main1.getEngine();

    }

    @Test
    public void testTypeSafeM32() throws Exception {
        CommandSummary command = MathCommandsTests.getMath32Command();

        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;

        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-flthreshold", "0.1");
        command.command.put("-maxtime", "60");
        command.command.put("-seed", "400");
        command.command.put("-maxgen", "2000");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        command.command.put("-parameters", "maxCombinationVariableLimit:true:disablelog:false");
        command.command.put("-maxVarCombination", "1000");
        command.command.put("-stopfirst", "false");
        command.command.put("-javacompliancelevel", "8");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafeApproach = (TypeSafeApproach) main1.getEngine();

    }

    @Test
    public void testTypeSafeM70() throws Exception {
        CommandSummary command = MathCommandsTests.getMath70Command();

        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;

        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-flthreshold", "0.01");
        command.command.put("-maxtime", "60");
        command.command.put("-seed", "400");
        command.command.put("-maxgen", "872");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        command.command.put("-parameters", "maxCombinationVariableLimit:true:disablelog:false");
        command.command.put("-maxVarCombination", "100");
        command.command.put("-stopfirst", "true");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafeApproach = (TypeSafeApproach) main1.getEngine();

        ExpressionTypeIngredientSpace ingredientSpace = (ExpressionTypeIngredientSpace) typeSafeApproach
                .getIngredientSearchStrategy().getIngredientSpace();
        assertNotNull(ingredientSpace);

        //assertTrue(typeSafeApproach.getSolutions().size() > 0);
        // In this test case we find the solution at generation 74
        //assertTrue(cardumen.getCurrentStat().getGeneralStats().get(Stats.GeneralStatEnum.NR_GENERATIONS).equals(74));
    }
}
