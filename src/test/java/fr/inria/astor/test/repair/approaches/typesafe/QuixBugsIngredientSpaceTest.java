package fr.inria.astor.test.repair.approaches.typesafe;

import fr.inria.astor.approaches.typesafe.TypeSafeApproach;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.TypeSafeExpressionTypeIngredientSpace;
import fr.inria.astor.test.repair.QuixBugsRepairTest;
import fr.inria.main.CommandSummary;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;
import org.junit.Test;
import spoon.reflect.declaration.CtElement;

import java.util.List;

/**
 *  Class to verify that the plausible patch exists in the ingredient space
 */
public class QuixBugsIngredientSpaceTest {

    private final String mode = ExecutionMode.TYPESAFE.name();

    // Correct
    @Test
    public void test_BITCOUNT_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("bitcount");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafe = (TypeSafeApproach) main1.getEngine();

        TypeSafeExpressionTypeIngredientSpace ingredientSpace = (TypeSafeExpressionTypeIngredientSpace) typeSafe
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = typeSafe.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,"n ^ (n - 1)",15);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        assert(ingredients.stream().anyMatch(i -> i.toString().equals("_int_0 & (_int_0 - 1)")));

    }

    // Correct
    @Test
    public void test_BUCKETSORT_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("bucketsort");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafe = (TypeSafeApproach) main1.getEngine();

        TypeSafeExpressionTypeIngredientSpace ingredientSpace = (TypeSafeExpressionTypeIngredientSpace) typeSafe
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = typeSafe.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar, "arr", 22);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        assert(ingredients.stream().anyMatch(i -> i.toString().equals("counts")));

    }


    // Overfitted
    @Test
    public void test_DFS_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("depth_first_search");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafe = (TypeSafeApproach) main1.getEngine();

        TypeSafeExpressionTypeIngredientSpace ingredientSpace = (TypeSafeExpressionTypeIngredientSpace) typeSafe
                .getIngredientSearchStrategy().getIngredientSpace();


        ProgramVariant pvar = typeSafe.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar, "successors", 54);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        assert(ingredients.stream().anyMatch(i -> i.toString().equals("predecessors")));

    }

    // Correct
    @Test
    public void test_GCD_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("gcd");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafe = (TypeSafeApproach) main1.getEngine();

        TypeSafeExpressionTypeIngredientSpace ingredientSpace = (TypeSafeExpressionTypeIngredientSpace) typeSafe
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = typeSafe.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar, "java_programs.GCD.gcd(a % b, b)", 19);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        assert(ingredients.stream().anyMatch(i -> i.toString().equals("java_programs.GCD.gcd(_int_0, _int_1 % _int_0)")));

    }

    // Correct
    @Test
    public void test_RPN_EVAL_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("rpn_eval");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafe = (TypeSafeApproach) main1.getEngine();

        TypeSafeExpressionTypeIngredientSpace ingredientSpace = (TypeSafeExpressionTypeIngredientSpace) typeSafe
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = typeSafe.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar, "bin_op.apply(a, b)", 34);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        assert(ingredients.stream().anyMatch(i -> i.toString().equals("_BinaryOperator_0.apply(_Double_1, _Double_2)")));

    }

    // Correct
    @Test
    public void test_HANOI_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("hanoi");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafe = (TypeSafeApproach) main1.getEngine();

        TypeSafeExpressionTypeIngredientSpace ingredientSpace = (TypeSafeExpressionTypeIngredientSpace) typeSafe
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = typeSafe.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,
                "steps.add(new java_programs.HANOI.Pair<java.lang.Integer, java.lang.Integer>(start, helper))",
                27);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        assert(ingredients.stream().anyMatch(i -> i.toString()
                .equals("_ArrayList_0.add(new java_programs.HANOI.Pair<java.lang.Integer, java.lang.Integer>(_int_1, _int_2))")));

    }

    // Overfittted
    @Test
    public void test_GET_FACTORS_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("get_factors");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafe = (TypeSafeApproach) main1.getEngine();

        TypeSafeExpressionTypeIngredientSpace ingredientSpace = (TypeSafeExpressionTypeIngredientSpace) typeSafe
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = typeSafe.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,"n",18);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        assert(ingredients.stream().anyMatch(i -> i.toString().equals("(_int_0 * _int_1)")));

    }

    // overfitted
    @Test
    public void test_FIND_IN_SORTED_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("find_in_sorted");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafe = (TypeSafeApproach) main1.getEngine();

        TypeSafeExpressionTypeIngredientSpace ingredientSpace = (TypeSafeExpressionTypeIngredientSpace) typeSafe
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = typeSafe.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,"mid",20);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        assert(ingredients.stream().anyMatch(i -> i.toString().equals("(_int_0 - _int_1)")));

    }

    // Correct
    @Test
    public void test_FIND_FIRST_IN_SORTED_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("find_first_in_sorted");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafe = (TypeSafeApproach) main1.getEngine();

        TypeSafeExpressionTypeIngredientSpace ingredientSpace = (TypeSafeExpressionTypeIngredientSpace) typeSafe
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = typeSafe.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,"lo <= hi",19);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        assert(ingredients.stream().anyMatch(i -> i.toString().equals("_int_0 != _int_1")));

    }

    // Correct
    @Test
    public void test_FLATTEN_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("flatten");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafe = (TypeSafeApproach) main1.getEngine();

        TypeSafeExpressionTypeIngredientSpace ingredientSpace = (TypeSafeExpressionTypeIngredientSpace) typeSafe
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = typeSafe.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,"java_programs.FLATTEN.flatten(arr)",26);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        assert(ingredients.stream().anyMatch(i -> i.toString().equals("((java.util.ArrayList) (_Object_0))")));

    }

    // Correct
    @Test
    public void test_KTH_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("kth");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafe = (TypeSafeApproach) main1.getEngine();

        TypeSafeExpressionTypeIngredientSpace ingredientSpace = (TypeSafeExpressionTypeIngredientSpace) typeSafe
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = typeSafe.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,"k",25);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        assert(ingredients.stream().anyMatch(i -> i.toString().equals("_int_0 - _int_1")));

    }

    // overfitted ?
    @Test
    public void test_SHORTEST_PATH_LENGTHS_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("shortest_path_lengths");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafe = (TypeSafeApproach) main1.getEngine();

        TypeSafeExpressionTypeIngredientSpace ingredientSpace = (TypeSafeExpressionTypeIngredientSpace) typeSafe
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = typeSafe.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,"length_by_path",41);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        assert(ingredients.stream().anyMatch(i -> i.toString().equals("length_by_edge")));

    }

    @Test
    public void test_IS_VALID_PARENTHESIZATION_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("is_valid_parenthesization");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafe = (TypeSafeApproach) main1.getEngine();

        TypeSafeExpressionTypeIngredientSpace ingredientSpace = (TypeSafeExpressionTypeIngredientSpace) typeSafe
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = typeSafe.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,"true",24);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        assert(ingredients.stream().anyMatch(i -> i.toString().equals("_int_0 == _int_1")));

    }

    // overfitted
    @Test
    public void test_SQRT_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("sqrt");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafe = (TypeSafeApproach) main1.getEngine();

        TypeSafeExpressionTypeIngredientSpace ingredientSpace = (TypeSafeExpressionTypeIngredientSpace) typeSafe
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = typeSafe.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,"x - approx",16);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        assert(ingredients.stream().anyMatch(i -> i.toString().equals("(_float_0 / _float_1)")));

    }


    public CtElement getSuspiciousElement(ProgramVariant pvar, String suspiciousElement, int lineNumber){
        return pvar.getModificationPoints().stream()
                .filter(se -> ((SuspiciousModificationPoint) se).getSuspicious().getLineNumber() == lineNumber
                        && se.getCodeElement().toString().equals(suspiciousElement)).findFirst().get().getCodeElement();
    }


}
