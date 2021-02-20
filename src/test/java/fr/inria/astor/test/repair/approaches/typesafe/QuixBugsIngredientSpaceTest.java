package fr.inria.astor.test.repair.approaches.typesafe;

import fr.inria.astor.approaches.typesafe.TypeSafeApproach;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.TypeSafeExpressionTypeIngredientSpace;
import fr.inria.astor.test.repair.QuixBugsRepairTest;
import fr.inria.main.CommandSummary;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;
import org.junit.Test;
import spoon.reflect.declaration.CtElement;

import java.util.List;

public class QuixBugsIngredientSpaceTest {

    private final String mode = ExecutionMode.TYPESAFE.name();


    @Test
    public void test_DFS_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("depth_first_search");
        command.command.put("-mode", mode);
        command.command.put("-seed", "400");
        command.command.put("-maxgen", "300");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafe = (TypeSafeApproach) main1.getEngine();

        TypeSafeExpressionTypeIngredientSpace ingredientSpace = (TypeSafeExpressionTypeIngredientSpace) typeSafe
                .getIngredientSearchStrategy().getIngredientSpace();


        ProgramVariant pvar = typeSafe.getVariants().get(0);

        CtElement suspiciousElement = pvar.getModificationPoints().stream()
                .filter(e -> e.getCodeElement().toString().equals("successors")).findFirst().get()
                .getCodeElement();

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        assert(ingredients.stream().anyMatch(i -> i.toString().equals("predecessors")));

    }

    @Test
    public void test_GCD_ingredientSpace() throws Exception {

        CommandSummary command = QuixBugsRepairTest.getQuixBugsCommand("gcd");
        command.command.put("-mode", mode);
        command.command.put("-seed", "123");
        command.command.put("-maxgen", "300");

        AstorMain main1 = new AstorMain();
        main1.execute(command.flat());

        TypeSafeApproach typeSafe = (TypeSafeApproach) main1.getEngine();

        TypeSafeExpressionTypeIngredientSpace ingredientSpace = (TypeSafeExpressionTypeIngredientSpace) typeSafe
                .getIngredientSearchStrategy().getIngredientSpace();

        ProgramVariant pvar = typeSafe.getVariants().get(0);

        CtElement suspiciousElement = pvar.getModificationPoints().stream()
                .filter(e -> e.getCodeElement().toString().equals("java_programs.GCD.gcd(a % b, b)")).findFirst().get()
                .getCodeElement();

        List<Ingredient> ingredients = ingredientSpace.getIngredients(suspiciousElement);
        assert(ingredients.stream().anyMatch(i -> i.toString().equals("java_programs.GCD.gcd(b, a % b)")));

    }

}
