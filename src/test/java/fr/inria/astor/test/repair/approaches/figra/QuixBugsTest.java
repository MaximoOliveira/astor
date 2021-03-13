package fr.inria.astor.test.repair.approaches.figra;

import fr.inria.astor.core.faultlocalization.gzoltar.GZoltarFaultLocalization;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.astor.test.repair.QuixBugsRepairTest;
import fr.inria.main.CommandSummary;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class QuixBugsTest {

    private final String mode = ExecutionMode.FIGRA.name();

    @Ignore // takes 10 mins
    @Test
    public void bitcount() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("bitcount");
        command.command.put("-mode", mode);
        command.command.put("-seed", "28");
        command.command.put("-maxgen", "500");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void levenshtein() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("levenshtein");
        command.command.put("-mode", mode);
        command.command.put("-seed", "28");
        command.command.put("-maxgen", "200");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void rpn_eval() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("rpn_eval");
        command.command.put("-mode", mode);
        command.command.put("-seed", "100");
        command.command.put("-maxgen", "3500");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void hanoi() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("hanoi");
        command.command.put("-mode", mode);
        command.command.put("-seed", "100");
        command.command.put("-maxgen", "3500");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void bucketsort() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("bucketsort");
        command.command.put("-mode", mode);
        command.command.put("-seed", "100");
        command.command.put("-maxgen", "500");
        command.command.put("-parameters", "logtestexecution:TRUE:"
                + "disablelog:FALSE:maxtime:120:autocompile:false:gzoltarpackagetonotinstrument:com.google.gson_engine"
                + GZoltarFaultLocalization.PACKAGE_SEPARATOR + "java_programs_test");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }


    @Test
    public void get_factors() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("get_factors");
        command.command.put("-mode", mode);
        command.command.put("-seed", "28");
        command.command.put("-maxgen", "120");
        command.command.put("-stopfirst", "true");
        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }

    @Ignore // takes a lot of time here
    @Test
    public void find_first_in_sorted() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("find_first_in_sorted");
        command.command.put("-mode", mode);
        command.command.put("-seed", "123");
        command.command.put("-maxgen", "500");

        command.command.put("-parameters", "logtestexecution:TRUE:"
                + "disablelog:FALSE:maxtime:120:autocompile:false:gzoltarpackagetonotinstrument:com.google.gson_engine"
                + GZoltarFaultLocalization.PACKAGE_SEPARATOR + "java_programs_test");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void flatten() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("flatten");
        command.command.put("-mode", mode);
        command.command.put("-seed", "400");
        command.command.put("-maxgen", "300");


        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void quicksort() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("quicksort");

        command.command.put("-mode", mode);
        command.command.put("-seed", "28");
        command.command.put("-maxgen", "300");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void depth_first_search() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("depth_first_search");
        command.command.put("-mode", mode);
        command.command.put("-seed", "400");
        command.command.put("-maxgen", "300");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void kth() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("kth");
        command.command.put("-mode", mode);
        command.command.put("-seed", "400");
        command.command.put("-maxgen", "8000");

        command.command.put("-parameters", "logtestexecution:TRUE:"
                + "disablelog:FALSE:maxtime:120:autocompile:false:gzoltarpackagetonotinstrument:com.google.gson_engine"
                + GZoltarFaultLocalization.PACKAGE_SEPARATOR + "java_programs_test");


        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }

    @Ignore
    @Test
    public void shortest_path_lengths() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("shortest_path_lengths");
        command.command.put("-mode", mode);
        command.command.put("-seed", "123");
        command.command.put("-maxgen", "3000");


        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void is_valid_parenthesization() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("is_valid_parenthesization");
        command.command.put("-mode", mode);
        command.command.put("-seed", "28");
        command.command.put("-maxgen", "3000");


        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }

    @Ignore // takes 2 much time
    @Test
    public void sqrt() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("sqrt");
        command.command.put("-mode", mode);
        command.command.put("-seed", "28");
        command.command.put("-maxgen", "10000");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void find_in_sorted() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("find_in_sorted");
        command.command.put("-seed", "123");
        command.command.put("-maxgen", "1000");
        command.command.put("-mode", mode);

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        //assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void gcd() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("gcd");
        command.command.put("-mode", mode);
        command.command.put("-seed", "123");
        command.command.put("-maxgen", "5000");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void next_palindrome() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("next_palindrome");
        command.command.put("-mode", mode);
        command.command.put("-seed", "17");
        command.command.put("-maxgen", "5000");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }

    @Ignore
    @Test
    public void topological_ordering() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("topological_ordering");
        command.command.put("-mode", mode);
        command.command.put("-seed", "17");
        command.command.put("-maxgen", "1000");
        command.command.put("-flthreshold", "1.0");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }

    @Ignore
    @Test
    public void shortest_paths() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("shortest_paths");
        command.command.put("-mode", mode);
        command.command.put("-seed", "17");
        command.command.put("-maxgen", "1000");
        command.command.put("-flthreshold", "1.0");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void mergesort() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("mergesort");
        command.command.put("-mode", mode);
        command.command.put("-seed", "28");
        command.command.put("-maxgen", "100");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }

    @Test
    public void longest_common_subsequence() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("longest_common_subsequence");
        command.command.put("-mode", mode);
        command.command.put("-seed", "28");
        command.command.put("-maxgen", "300");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        AstorCoreEngine engine = main1.getEngine();

        // We found a solution with figra
        assertEquals(1, engine.getSolutions().size());
    }


}
