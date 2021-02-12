package fr.inria.astor.test.repair.approaches.typesafe;

import fr.inria.astor.approaches.typesafe.TypeSafeApproach;
import fr.inria.astor.core.manipulation.bytecode.compiler.SpoonClassCompiler;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.TypeSafeExpressionTypeIngredientSpace;
import fr.inria.astor.test.repair.QuixBugsRepairTest;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        command.command.put("-flthreshold", "0.8");
        command.command.put("-maxtime", "60");
        command.command.put("-seed", "400");
        command.command.put("-maxgen", "200");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        command.command.put("-parameters", "maxCombinationVariableLimit:true:disablelog:false");
        command.command.put("-maxVarCombination", "100");
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
        command.command.put("-seed", "100");
        command.command.put("-flthreshold", "0.1");
        command.command.put("-maxtime", "60");
        command.command.put("-maxgen", "200");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        command.command.put("-parameters", "maxCombinationVariableLimit:true:disablelog:false");
        //command.command.put("-parameters", "disablelog:false");
        command.command.put("-maxVarCombination", "1000");
        command.command.put("-stopfirst", "false");
        command.command.put("-javacompliancelevel", "5");

        AstorMain main1 = new AstorMain();
        System.out.println(Arrays.toString(command.flat()));
        main1.execute(command.flat());

        TypeSafeApproach typeSafeApproach = (TypeSafeApproach) main1.getEngine();

        TypeSafeExpressionTypeIngredientSpace ingredientSpace = (TypeSafeExpressionTypeIngredientSpace) typeSafeApproach
                .getIngredientSearchStrategy().getIngredientSpace();
        assertNotNull(ingredientSpace);

        //assertTrue(typeSafeApproach.getSolutions().size() > 0);
        // In this test case we find the solution at generation 74
        //assertTrue(cardumen.getCurrentStat().getGeneralStats().get(Stats.GeneralStatEnum.NR_GENERATIONS).equals(74));
    }

    @Test
    public void testClassDoenstCompileWithCurrentCompiler() throws MalformedURLException {

        String math_70 = "/home/max/Desktop/tese/fork_max/astor/examples/math_32/src/main/java";
        String junitJar = "/home/max/Desktop/tese/original_astor/astor/./examples/libs/junit-4.10.jar";
        URL ulrJar = new File(junitJar).toURI().toURL();
        String fastMathPath = "/home/max/Desktop/tese/fork_max/astor/examples/math_32/src/main/java/org/apache/commons/math3/util/FastMath.java";
        URL urlFastMath = new File(fastMathPath).toURI().toURL();
        URL[] cp = new URL[]{ulrJar, urlFastMath};


        Launcher launcher = new Launcher();
        launcher.addInputResource(math_70);
        launcher.buildModel();

        CtClass ctClass = (CtClass) launcher.getFactory().getModel().getAllTypes()
                .stream().filter(ctType -> ctType.getSimpleName().equals("FastMath")).findFirst().orElse(null);
        List<CtClass> classes = Collections.singletonList(ctClass);
        SpoonClassCompiler spoonClassCompiler = new SpoonClassCompiler();
        List<CtClass> ctClasses = new ArrayList<>(classes);
        CompilationResult compilation2 = spoonClassCompiler.compile(ctClasses, cp);

        assertTrue(compilation2.getErrorList().stream()
                .anyMatch(error -> error.contains("cannot assign a value to final variable")));
    }

    @Test
    public void testClassCompiles() throws MalformedURLException {

        String math_32 = "/home/max/Desktop/tese/original_astor/astor/examples/math_32/src/main/java";

        Launcher launcher = new Launcher();
        launcher.addInputResource(math_32);
        launcher.buildModel();

        JDTBasedSpoonCompiler compiler = (JDTBasedSpoonCompiler) launcher.getModelBuilder();
        List<CategorizedProblem> problems = compiler.getProblems();

        assertTrue(problems.isEmpty());
    }
}
