package fr.inria.astor.test.repair.approaches.typesafe;

import fr.inria.astor.core.faultlocalization.gzoltar.GZoltarFaultLocalization;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.astor.test.repair.QuixBugsRepairTest;
import fr.inria.main.CommandSummary;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;
import org.junit.Test;

import java.util.Arrays;

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
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with typesafe
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void testLEVENSHTEIN() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("levenshtein");
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "100");
        command.command.put("-flthreshold", "0.1");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "3500");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        //command.command.put("-parameters", "maxCombinationVariableLimit:true:disablelog:false");
        //command.command.put("-parameters", "disablelog:false");
        command.command.put("-parameters", "logtestexecution:TRUE:"
                + "disablelog:FALSE:maxtime:120:autocompile:false:gzoltarpackagetonotinstrument:com.google.gson_engine"
                + GZoltarFaultLocalization.PACKAGE_SEPARATOR + "java_programs_test");
        command.command.put("-stopfirst", "true");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with typesafe
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void testRPN_EVAL() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("rpn_eval");
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "100");
        command.command.put("-flthreshold", "0.1");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "3500");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        //command.command.put("-parameters", "maxCombinationVariableLimit:true:disablelog:false");
        //command.command.put("-parameters", "disablelog:false");
        command.command.put("-parameters", "logtestexecution:TRUE:"
                + "disablelog:FALSE:maxtime:120:autocompile:false:gzoltarpackagetonotinstrument:com.google.gson_engine"
                + GZoltarFaultLocalization.PACKAGE_SEPARATOR + "java_programs_test");
        command.command.put("-stopfirst", "true");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with typesafe
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void testHANOI() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("hanoi");
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "100");
        command.command.put("-flthreshold", "0.1");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "3500");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        //command.command.put("-parameters", "maxCombinationVariableLimit:true:disablelog:false");
        //command.command.put("-parameters", "disablelog:false");
        command.command.put("-parameters", "logtestexecution:TRUE:"
                + "disablelog:FALSE:maxtime:120:autocompile:false:gzoltarpackagetonotinstrument:com.google.gson_engine"
                + GZoltarFaultLocalization.PACKAGE_SEPARATOR + "java_programs_test");
        command.command.put("-stopfirst", "true");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with typesafe
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void testBUCKETSORT() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("bucketsort");
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
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with typesafe
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void testGET_FACTORS() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("get_factors");
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "100");
        command.command.put("-flthreshold", "0.1");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "100");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        //command.command.put("-parameters", "maxCombinationVariableLimit:true:disablelog:false");
        //command.command.put("-parameters", "disablelog:false");
        command.command.put("-parameters", "logtestexecution:TRUE:"
                + "disablelog:FALSE:maxtime:120:autocompile:false:gzoltarpackagetonotinstrument:com.google.gson_engine"
                + GZoltarFaultLocalization.PACKAGE_SEPARATOR + "java_programs_test");
        command.command.put("-stopfirst", "true");
        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with typesafe
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void testFIND_FIRST_IN_SORTED() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("find_first_in_sorted");
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "400");
        command.command.put("-flthreshold", "0.1");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "3500");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        //command.command.put("-parameters", "maxCombinationVariableLimit:true:disablelog:false");
        //command.command.put("-parameters", "disablelog:false");
        command.command.put("-parameters", "logtestexecution:TRUE:"
                + "disablelog:FALSE:maxtime:120:autocompile:false:gzoltarpackagetonotinstrument:com.google.gson_engine"
                + GZoltarFaultLocalization.PACKAGE_SEPARATOR + "java_programs_test");
        command.command.put("-stopfirst", "true");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with typesafe
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void testFLATTEN() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("flatten");
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "400");
        command.command.put("-flthreshold", "0.1");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "3500");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        //command.command.put("-parameters", "maxCombinationVariableLimit:true:disablelog:false");
        //command.command.put("-parameters", "disablelog:false");
        command.command.put("-parameters", "logtestexecution:TRUE:"
                + "disablelog:FALSE:maxtime:120:autocompile:false:gzoltarpackagetonotinstrument:com.google.gson_engine"
                + GZoltarFaultLocalization.PACKAGE_SEPARATOR + "java_programs_test");
        command.command.put("-stopfirst", "true");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with typesafe
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void testQUICKSORT() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("quicksort");
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "400");
        command.command.put("-flthreshold", "0.1");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "300");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        //command.command.put("-parameters", "maxCombinationVariableLimit:true:disablelog:false");
        //command.command.put("-parameters", "disablelog:false");
        command.command.put("-parameters", "logtestexecution:TRUE:"
                + "disablelog:FALSE:maxtime:120:autocompile:false:gzoltarpackagetonotinstrument:com.google.gson_engine"
                + GZoltarFaultLocalization.PACKAGE_SEPARATOR + "java_programs_test");
        command.command.put("-stopfirst", "true");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with typesafe
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void testDFS() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("depth_first_search");
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "400");
        command.command.put("-flthreshold", "0.1");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "300");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        //command.command.put("-parameters", "maxCombinationVariableLimit:true:disablelog:false");
        //command.command.put("-parameters", "disablelog:false");
        command.command.put("-parameters", "logtestexecution:TRUE:"
                + "disablelog:FALSE:maxtime:120:autocompile:false:gzoltarpackagetonotinstrument:com.google.gson_engine"
                + GZoltarFaultLocalization.PACKAGE_SEPARATOR + "java_programs_test");
        command.command.put("-stopfirst", "true");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with typesafe
        assertEquals(1, engine.getSolutions().size());
    }

}
