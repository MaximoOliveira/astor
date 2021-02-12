package fr.inria.astor.test.repair.approaches.typesafe;

import fr.inria.astor.core.faultlocalization.gzoltar.GZoltarFaultLocalization;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.astor.test.repair.QuixBugsRepairTest;
import fr.inria.main.CommandSummary;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class QuixBugsTest {

    @Test
    public void testQuixBugsBitcount() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("bitcount");
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "100");
        command.command.put("-flthreshold", "0.1");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "500");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        //command.command.put("-parameters", "maxCombinationVariableLimit:true:disablelog:false");
        //command.command.put("-parameters", "disablelog:false");
        command.command.put("-parameters", "logtestexecution:TRUE:"
                + "disablelog:FALSE:maxtime:120:autocompile:false:gzoltarpackagetonotinstrument:com.google.gson_engine"
                + GZoltarFaultLocalization.PACKAGE_SEPARATOR + "java_programs_test");
        command.command.put("-stopfirst", "true");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with typesafe
        assertEquals(1 ,engine.getSolutions().size());
    }

    @Test
    public void testBFS() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("breadth_first_search");
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "400");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "2000000000");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        //command.command.put("-parameters", ":maxCombinationVariableLimit:true:disablelog:false");
        command.command.put("-parameters", "maxmodificationpoints:5:skipfitnessinitialpopulation:true:maxCombinationVariableLimit:true:disablelog:false");
        //command.command.put("-parameters", "disablelog:false");
        command.command.put("-skipfaultlocalization","true");
        command.command.put("-stopfirst", "true");
        command.command.put("-failing", "java_testcases.BREADTH_FIRST_SEARCH_TEST");
        //command.command.put("-maxmodificationpoints", "25");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());
    }
}
