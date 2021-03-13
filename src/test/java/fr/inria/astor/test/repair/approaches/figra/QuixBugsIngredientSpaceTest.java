package fr.inria.astor.test.repair.approaches.figra;

import fr.inria.astor.approaches.figra.ExpressionReplaceOperator;
import fr.inria.astor.approaches.figra.FigraApproach;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.ingredientSearch.FigraProbabilisticIngredientStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.FigraExpressionTypeIngredientSpace;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.test.repair.QuixBugsRepairTest;
import fr.inria.main.CommandSummary;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;
import org.junit.Test;
import spoon.reflect.declaration.CtElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class to verify that at least one plausible patch exists in the ingredient space of each program
 *
 *      BITCOUNT
 *      BUCKETSORT
 *      DEPTH_FIRST_SEARCH
 *      FIND_FIRST_IN_SORTED
 *      FIND_IN_SORTED
 *      FLATTEN
 *      GCD
 *      GET_FACTORS
 *      HANOI
 *      IS_VALID_PARENTHESIZATION
 *      KNAPSACK
 *      KTH
 *      LEVENSHTEIN
 *      LONGEST_COMMON_SUBSEQUENCE
 *      MERGESORT
 *      NEXT_PALINDROME
 *      NEXT_PERMUTATION
 *      PASCAL
 *      RPN_EVAL
 *      SHORTEST_PATH_LENGTHS
 *      SHORTEST_PATHS
 *      SQRT
 *      TOPOLOGICAL_ORDERING
 */
public class QuixBugsIngredientSpaceTest {

    private final String mode = ExecutionMode.FIGRA.name();

    // Correct
    @Test
    public void test_BITCOUNT_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("bitcount");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figraApproach = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figraApproach
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figraApproach.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar, "n ^ (n - 1)", 15);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        Ingredient ingredient = ingredients.stream().filter(i -> i.toString().equals("_int_0 & (_int_0 - 1)")).findFirst().orElse(null);
        // ingredient template exists
        assert (ingredient != null);

        List<Ingredient> ingredientsAfterTransformation = ingredientsAfterTransformation(figraApproach, modPoint, ingredient);

        // verify that the solution is present
        assert (ingredientsAfterTransformation.stream().anyMatch(i -> i.toString().equals("n & (n - 1)")));

    }

    // Correct
    @Test
    public void test_BUCKETSORT_ingredientSpace() throws Exception {
        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("bucketsort");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar, "arr", 22);

        List<Ingredient> ingredientSpaceForSuspiciousElement = ingredientSpaceForSuspiciousElement(figra, suspiciousElement);
        assert (ingredientSpaceForSuspiciousElement.stream().anyMatch(i -> i.toString().equals("counts")));

    }


    // Overfitted
    @Test
    public void test_DFS_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("depth_first_search");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();


        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar, "successors", 50);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        assert (ingredients.stream().anyMatch(i -> i.toString().equals("predecessors")));

    }

    // Correct
    @Test
    public void test_GCD_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("gcd");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar, "java_programs.GCD.gcd(a % b, b)", 19);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);


        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        Ingredient ingredient = ingredients.stream().filter(i ->
                i.toString().equals("java_programs.GCD.gcd(_int_0, _int_1 % _int_0)")).findFirst().orElse(null);
        // ingredient template exists
        assert (ingredient != null);

        List<Ingredient> ingredientsAfterTransformation = ingredientsAfterTransformation(figra, modPoint, ingredient);

        // verify that the solution is present
        assert (ingredientsAfterTransformation.stream().anyMatch(i -> i.toString().equals("java_programs.GCD.gcd(b, a % b)")));


    }

    // Correct
    @Test
    public void test_RPN_EVAL_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("rpn_eval");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar, "bin_op.apply(a, b)", 34);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        Ingredient ingredient = ingredients.stream().filter(i ->
                i.toString().equals("_BinaryOperator_0.apply(_Double_1, _Double_2)")).findFirst().orElse(null);
        // ingredient template exists
        assert (ingredient != null);

        List<Ingredient> ingredientsAfterTransformation = ingredientsAfterTransformation(figra, modPoint, ingredient);

        // verify that the solution is present
        assert (ingredientsAfterTransformation.stream().anyMatch(i -> i.toString().equals("bin_op.apply(b, a)")));

    }

    // Correct
    @Test
    public void test_HANOI_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("hanoi");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,
                "steps.add(new java_programs.HANOI.Pair<java.lang.Integer, java.lang.Integer>(start, helper))",
                27);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        Ingredient ingredient = ingredients.stream().filter(i ->
                i.toString().equals("_ArrayList_0.add(new java_programs.HANOI.Pair<java.lang.Integer, java.lang.Integer>(_int_1, _int_2))")).findFirst().orElse(null);
        // ingredient template exists
        assert (ingredient != null);

        List<Ingredient> ingredientsAfterTransformation = ingredientsAfterTransformation(figra, modPoint, ingredient);
        // verify that the solution is present
        assert (ingredientsAfterTransformation.stream().anyMatch(i ->
                i.toString().equals("steps.add(new java_programs.HANOI.Pair<java.lang.Integer, java.lang.Integer>(start, end))")));

    }

    // Overfittted
    @Test
    public void test_GET_FACTORS_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("get_factors");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar, "n", 18);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);

        Ingredient ingredient = ingredients.stream().filter(i ->
                i.toString().equals("(_int_0 * _int_1)")).findFirst().orElse(null);
        // ingredient template exists
        assert (ingredient != null);

        List<Ingredient> ingredientsAfterTransformation = ingredientsAfterTransformation(figra, modPoint, ingredient);
        // verify that the solution is present
        assert (ingredientsAfterTransformation.stream().anyMatch(i ->
                i.toString().equals("(n * n)")));

    }

    // Correct
    @Test
    public void test_FIND_IN_SORTED_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("find_in_sorted");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,
                "end", 13);

        // this one is overffited
        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        Ingredient ingredient = ingredients.stream().filter(i ->
                i.toString().equals("start + ((end - start) / 2)")).findFirst().orElse(null);
        // ingredient exists
        assert (ingredient != null);

        CtElement suspiciousElement2 = getSuspiciousElement(pvar,
                "mid", 20);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement2);

        // correct one
        Ingredient ingredient2 = ingredients.stream().filter(i ->
                i.toString().equals("(_int_0 + 1)")).collect(Collectors.toList()).get(0);

        // ingredient exists
        assert (ingredient2 != null);

        // correct one
        List<Ingredient> ingredientsAfterTransformation = ingredientsAfterTransformation(figra, modPoint, ingredient2);
        assert (ingredientsAfterTransformation.stream().anyMatch(i ->
                i.toString().equals("(mid + 1)")));
    }

    // Correct
    @Test
    public void test_FIND_FIRST_IN_SORTED_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("find_first_in_sorted");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar, "lo <= hi", 19);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        Ingredient ingredient = ingredients.stream().filter(i ->
                i.toString().equals("_int_0 != _int_1")).findFirst().orElse(null);
        // ingredient template exists
        assert (ingredient != null);

        List<Ingredient> ingredientsAfterTransformation = ingredientsAfterTransformation(figra, modPoint, ingredient);
        // verify that the solution is present
        assert (ingredientsAfterTransformation.stream().anyMatch(i ->
                i.toString().equals("lo != hi")));

    }

    // Correct
    @Test
    public void test_FLATTEN_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("flatten");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar, "java_programs.FLATTEN.flatten(arr)", 26);
        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        Ingredient ingredient = ingredients.stream().filter(i ->
                i.toString().equals("arr")).findFirst().orElse(null);
        // ingredient exists
        assert (ingredient != null);
    }

    // Correct
    @Test
    public void test_KTH_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("kth");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar, "k", 25);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        Ingredient ingredient = ingredients.stream().filter(i ->
                i.toString().equals("_int_0 - _int_1")).findFirst().orElse(null);
        // ingredient template exists
        assert (ingredient != null);

        List<Ingredient> ingredientsAfterTransformation = ingredientsAfterTransformation(figra, modPoint, ingredient);
        // verify that the solution is present
        assert (ingredientsAfterTransformation.stream().anyMatch(i ->
                i.toString().equals("k - num_lessoreq")));

    }

    // overfitted ?
    @Test
    public void test_SHORTEST_PATH_LENGTHS_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("shortest_path_lengths");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar, "length_by_path", 41);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        assert (ingredients.stream().anyMatch(i -> i.toString().equals("length_by_edge")));
    }

    @Test
    public void test_IS_VALID_PARENTHESIZATION_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("is_valid_parenthesization");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar, "true", 24);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);


        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);


        Ingredient ingredient = ingredients.stream().filter(i ->
                i.toString().equals("_int_0 == 0")).findFirst().orElse(null);
        // ingredient template exists
        assert (ingredient != null);

        List<Ingredient> ingredientsAfterTransformation = ingredientsAfterTransformation(figra, modPoint, ingredient);
        // verify that the solution is present
        assert (ingredientsAfterTransformation.stream().anyMatch(i ->
                i.toString().equals("depth == 0")));

    }

    // overfitted
    @Test
    public void test_SQRT_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("sqrt");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar, "x", 16);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);

        Ingredient ingredient = ingredients.stream().filter(i ->
                i.toString().equals("0.5F * (_float_0 + (_float_1 / _float_0))")).findFirst().orElse(null);
        // ingredient template exists
        assert (ingredient != null);

        List<Ingredient> ingredientsAfterTransformation = ingredientsAfterTransformation(figra, modPoint, ingredient);
        // verify that the solution is present
        assert (ingredientsAfterTransformation.stream().anyMatch(i ->
                i.toString().equals("0.5F * (approx + (x / approx))")));

    }

    // Overfitted
    @Test
    public void test_NEXT_PALINDROME_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("next_palindrome");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,
                "digit_list.length", 35);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);

        Ingredient ingredient = ingredients.stream().filter(i ->
                i.toString().equals("2")).findFirst().orElse(null);
        // ingredient  template exists
        assert (ingredient != null);

    }

    @Test
    public void test_KNAPSACK_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("knapsack");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,
                "weight < j", 30);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);

        Ingredient ingredient = ingredients.stream().filter(i ->
                i.toString().equals("_int_0 <= _int_1")).findFirst().orElse(null);
        // ingredient  template exists
        assert (ingredient != null);

        List<Ingredient> ingredientsAfterTransformation = ingredientsAfterTransformation(figra, modPoint, ingredient);
        // verify that the solution is present
        assert (ingredientsAfterTransformation.stream().anyMatch(i ->
                i.toString().equals("weight <= j")));

    }

    //Correct
    @Test
    public void test_LONGEST_COMMON_SUBSEQUENCE_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("longest_common_subsequence");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,
                "b", 18);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);

        Ingredient ingredient = ingredients.stream()
                .filter(i -> i.toString().equals("_String_0.substring(1)")).findFirst().orElse(null);
        // ingredient  template exists
        assert (ingredient != null);

        List<Ingredient> ingredientsAfterTransformation = ingredientsAfterTransformation(figra, modPoint, ingredient);
        // verify that the solution is present
        assert (ingredientsAfterTransformation.stream().anyMatch(i ->
                i.toString().equals("b.substring(1)")));

    }

    //overfit
    @Test
    public void test_MERGESORT_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("mergesort");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,
                "arr.size()", 38);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);

        Ingredient ingredient = ingredients.stream()
                .filter(i -> i.toString().equals("_ArrayList_0.size() / 2")).findFirst().orElse(null);
        // ingredient  template exists
        assert (ingredient != null);

        List<Ingredient> ingredientsAfterTransformation = ingredientsAfterTransformation(figra, modPoint, ingredient);
        // verify that the solution is present
        assert (ingredientsAfterTransformation.stream().anyMatch(i ->
                i.toString().equals("arr.size() / 2")));

    }

    // correct
    @Test
    public void test_NEXT_PERMUTATION_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("next_permutation");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,
                "perm.get(j) < perm.get(i)", 19);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);

        Ingredient ingredient = ingredients.stream()
                .filter(i -> i.toString().equals("_ArrayList_0.get(_int_1) > _ArrayList_0.get(_int_2)")).findFirst().orElse(null);
        // ingredient  template exists
        assert (ingredient != null);

        List<Ingredient> ingredientsAfterTransformation = ingredientsAfterTransformation(figra, modPoint, ingredient);
        // verify that the solution is present
        assert (ingredientsAfterTransformation.stream().anyMatch(i ->
                i.toString().equals("perm.get(j) > perm.get(i)")));

    }

    // correct
    @Test
    public void test_PASCAL_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("pascal");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,
                "r", 22);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);

        Ingredient ingredient = ingredients.stream()
                .filter(i -> i.toString().equals("_int_0 + 1")).findFirst().orElse(null);
        // ingredient  template exists
        assert (ingredient != null);

        List<Ingredient> ingredientsAfterTransformation = ingredientsAfterTransformation(figra, modPoint, ingredient);
        // verify that the solution is present
        assert (ingredientsAfterTransformation.stream().anyMatch(i ->
                i.toString().equals("c + 1")));

    }

    // Correct
    @Test
    public void test_LEVENSHTEIN_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("levenshtein");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,
                "1 + java_programs.LEVENSHTEIN.levenshtein(source.substring(1), target.substring(1))", 17);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);

        Ingredient ingredient = ingredients.stream()
                .filter(i -> i.toString().equals("java_programs.LEVENSHTEIN.levenshtein(_String_0.substring(1), _String_1.substring(1))")).findFirst().orElse(null);
        // ingredient  template exists
        assert (ingredient != null);

        List<Ingredient> ingredientsAfterTransformation = ingredientsAfterTransformation(figra, modPoint, ingredient);
        // verify that the solution is present
        assert (ingredientsAfterTransformation.stream().anyMatch(i ->
                i.toString().equals("java_programs.LEVENSHTEIN.levenshtein(source.substring(1), target.substring(1))")));

    }

    // Correct
    @Test
    public void test_QUICKSORT_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("quicksort");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,
                "x > pivot", 26);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);

        Ingredient ingredient = ingredients.stream()
                .filter(i -> i.toString().equals("_Integer_0 >= _Integer_1")).findFirst().orElse(null);
        // ingredient  template exists
        assert (ingredient != null);

        List<Ingredient> ingredientsAfterTransformation = ingredientsAfterTransformation(figra, modPoint, ingredient);
        // verify that the solution is present
        assert (ingredientsAfterTransformation.stream().anyMatch(i ->
                i.toString().equals("x >= pivot")));

    }

    // Correct
    @Test
    public void test_SHORTEST_PATHS_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("shortest_paths");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,
                "weight_by_edge.put(edge, update_weight)", 30);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);

        Ingredient ingredient = ingredients.stream()
                .filter(i -> i.toString().equals("_Map_0.put(_List_1.get(1), _int_2)")).findFirst().orElse(null);
        // ingredient  template exists
        assert (ingredient != null);

        List<Ingredient> ingredientsAfterTransformation = ingredientsAfterTransformation(figra, modPoint, ingredient);
        // verify that the solution is present
        assert (ingredientsAfterTransformation.stream().anyMatch(i ->
                i.toString().equals("weight_by_node.put(edge.get(1), update_weight)")));

    }

    // correct
    @Test
    public void test_TOPOLOGICAL_ORDERING_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("topological_ordering");
        command.command.put("-mode", mode);
        command.command.put("-maxgen", "0");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        FigraApproach figra = (FigraApproach) main1.getEngine();

        FigraExpressionTypeIngredientSpace ingredientSpace = (FigraExpressionTypeIngredientSpace) figra
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = figra.getVariants().get(0);

        CtElement suspiciousElement = getSuspiciousElement(pvar,
                "nextNode.getSuccessors()", 17);
        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);

        Ingredient ingredient = ingredients.stream()
                .filter(i -> i.toString().equals("_Node_0.getPredecessors()")).findFirst().orElse(null);
        // ingredient  template exists
        assert (ingredient != null);

        List<Ingredient> ingredientsAfterTransformation = ingredientsAfterTransformation(figra, modPoint, ingredient);
        // verify that the solution is present
        assert (ingredientsAfterTransformation.stream().anyMatch(i ->
                i.toString().equals("nextNode.getPredecessors()")));

    }

    private List<Ingredient> ingredientsAfterTransformation(FigraApproach figra, ModificationPoint mp, Ingredient ingredient) {
        FigraProbabilisticIngredientStrategy ingredientStrategy = (FigraProbabilisticIngredientStrategy) figra.getIngredientSearchStrategy();
        IngredientTransformationStrategy transformationStrategy = ingredientStrategy.getIngredientTransformationStrategy();

        return transformationStrategy.transform(mp, ingredient);
    }


    private List<Ingredient> ingredientSpaceForSuspiciousElement(FigraApproach figra, CtElement suspiciousElement) {

        ProgramVariant pvar = figra.getVariants().get(0);
        FigraProbabilisticIngredientStrategy ingredientStrategy = (FigraProbabilisticIngredientStrategy) figra.getIngredientSearchStrategy();

        ModificationPoint modPoint = pvar.getModificationPoint(suspiciousElement);
        return ingredientStrategy.getNotExhaustedBaseElements(modPoint, new ExpressionReplaceOperator());
    }


    public CtElement getSuspiciousElement(ProgramVariant pvar, String suspiciousElement, int lineNumber) {
        return pvar.getModificationPoints().stream()
                .filter(se -> ((SuspiciousModificationPoint) se).getSuspicious().getLineNumber() == lineNumber
                        && se.getCodeElement().toString().equals(suspiciousElement)).findFirst().get().getCodeElement();
    }

}
