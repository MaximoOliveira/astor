package fr.inria.astor.test.repair.approaches.typesafe;

import fr.inria.astor.core.faultlocalization.gzoltar.GZoltarFaultLocalization;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.astor.test.repair.QuixBugsRepairTest;
import fr.inria.main.CommandSummary;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class QuixBugsTest {

    @Test
    public void testBITCOUNT() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("bitcount");
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "100");
        command.command.put("-flthreshold", "0.1");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "500");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
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

    @Ignore // taking to much time with this setup
    @Test
    public void testLEVENSHTEIN() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("levenshtein");
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "100");
        command.command.put("-flthreshold", "0.1");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "5000");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
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

    // Is not working with 100 generations, need to test more. takes long time
    @Ignore
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

    @Ignore // takes a lot of time here
    @Test
    public void testFIND_FIRST_IN_SORTED() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("find_first_in_sorted");
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "123");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "500");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
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
        command.command.put("-maxgen", "1000");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        command.command.put("-stopfirst", "true");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with typesafe
        assertEquals(1, engine.getSolutions().size());
    }

    @Ignore //TODO test with larger maxgen
    @Test
    public void testQUICKSORT() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("quicksort");
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "100");
        command.command.put("-flthreshold", "0.1");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "15000");
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
        command.command.put("-stopfirst", "true");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with typesafe
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void testKTH() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("kth");
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "400");
        command.command.put("-flthreshold", "0.1");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "8000");
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
    public void testSHORTEST_PATH_LENGTHS() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("shortest_path_lengths");
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "123");
        command.command.put("-flthreshold", "0.1");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "3000");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        command.command.put("-stopfirst", "true");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with typesafe
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void testIS_VALID_PARENTHESIZATION() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("is_valid_parenthesization");
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "123");
        command.command.put("-flthreshold", "0.5");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "3000");
        command.command.put("-population", "1");
        command.command.put("-parameters", "maxCombinationVariableLimit:true:maxVarCombination:1000");
        command.command.put("-scope", scope.toString().toLowerCase());
        command.command.put("-stopfirst", "true");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with typesafe
        assertEquals(1, engine.getSolutions().size());
    }

    @Ignore // is not being fixed in this setup. Works in RepairThemAll
    @Test
    public void testSQRT() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("sqrt");
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "123");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "5000");
        command.command.put("-population", "1");
        command.command.put("-stopfirst", "true");
        command.command.put("-parameters", "tmax1:20000:disablelog:false:maxCombinationVariableLimit:true:maxVarCombination:1000");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with typesafe
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void testFIND_IN_SORTED() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("find_in_sorted");
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "123");
        command.command.put("-flthreshold", "0.1");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "1000");
        command.command.put("-population", "1");
        command.command.put("-parameters", "maxCombinationVariableLimit:true:maxVarCombination:1000");
        command.command.put("-scope", scope.toString().toLowerCase());
        command.command.put("-stopfirst", "true");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with typesafe
        assertEquals(1, engine.getSolutions().size());
    }

    @Ignore
    @Test
    public void testGCD() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("gcd");
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
        command.command.put("-mode", ExecutionMode.TYPESAFE.name());
        command.command.put("-seed", "123");
        command.command.put("-flthreshold", "0.1");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "5000");
        command.command.put("-population", "1");
        command.command.put("-parameters", "maxCombinationVariableLimit:true:maxVarCombination:1000");
        command.command.put("-scope", scope.toString().toLowerCase());
        command.command.put("-stopfirst", "true");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with typesafe
        assertEquals(1, engine.getSolutions().size());
    }

}
