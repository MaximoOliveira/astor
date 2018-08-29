package fr.inria.astor.test.repair.evaluation.extensionpoints.ingredients;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.filters.ExpressionIngredientSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.MethodInvocationFixSpaceProcessor;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPoolLocationType;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import fr.inria.main.evolution.ExtensionPoints;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class IngredientProcessorTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testM70DefaultStatement() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);

		ProgramVariant pv = solutions.get(0);

		JGenProg jgp = (JGenProg) main1.getEngine();
		IngredientPoolLocationType ingSpace = (IngredientPoolLocationType) jgp.getIngredientSearchStrategy()
				.getIngredientSpace();

		List ingredients = ingSpace.getAllIngredients();
		assertTrue(ingredients.size() > 0);

		checkModificationPointTypes(solutions, CtStatement.class);
		checkIngredientTypes(ingSpace, CtStatement.class);
	}

	@Test
	public void testM70MethodInvocation() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-parameters", ExtensionPoints.TARGET_CODE_PROCESSOR.identifier + File.pathSeparator
				+ MethodInvocationFixSpaceProcessor.class.getCanonicalName());
		command.command.put("-maxgen", "0");

		AstorMain main1 = new AstorMain();

		main1.execute(command.flat());
		List<ProgramVariant> variantss = main1.getEngine().getVariants();
		assertTrue(variantss.size() > 0);

		ProgramVariant pv = variantss.get(0);

		JGenProg jgp = (JGenProg) main1.getEngine();
		IngredientPoolLocationType ingSpace = (IngredientPoolLocationType) jgp.getIngredientSearchStrategy()
				.getIngredientSpace();
		checkModificationPointTypes(variantss, CtInvocation.class);
		checkIngredientTypes(ingSpace, CtInvocation.class);
	}

	@Test
	public void testM70Expression() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-parameters", ExtensionPoints.TARGET_CODE_PROCESSOR.identifier + File.pathSeparator
				+ ExpressionIngredientSpaceProcessor.class.getCanonicalName());
		command.command.put("-maxgen", "0");// Avoid evolution

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		List<ProgramVariant> variantss = main1.getEngine().getVariants();
		assertTrue(variantss.size() > 0);

		JGenProg jgp = (JGenProg) main1.getEngine();
		IngredientPoolLocationType ingSpace = (IngredientPoolLocationType) jgp.getIngredientSearchStrategy()
				.getIngredientSpace();

		checkModificationPointTypes(variantss, CtExpression.class);
		checkIngredientTypes(ingSpace, CtExpression.class);

	}

	@Test
	public void testM70DifferentGranularities() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();

		command.command.put(ExtensionPoints.TARGET_CODE_PROCESSOR.argument(), "statements");
		command.command.put(ExtensionPoints.TARGET_INGREDIENT_CODE_PROCESSOR.argument(),
				ExpressionIngredientSpaceProcessor.class.getCanonicalName());

		command.command.put("-maxgen", "0");// Avoid evolution

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		List<ProgramVariant> variantss = main1.getEngine().getVariants();
		assertTrue(variantss.size() > 0);

		JGenProg jgp = (JGenProg) main1.getEngine();
		IngredientPoolLocationType ingSpace = (IngredientPoolLocationType) jgp.getIngredientSearchStrategy()
				.getIngredientSpace();

		checkModificationPointTypes(variantss, CtStatement.class);
		checkIngredientTypes(ingSpace, CtExpression.class);

	}

	public void checkModificationPointTypes(List<ProgramVariant> variantss, Class classToProcess) {

		ProgramVariant pv = variantss.get(0);
		for (ModificationPoint modificationPoint : pv.getModificationPoints()) {
			CtElement elementFromPoint = modificationPoint.getCodeElement();
			assertTrue(classToProcess.isInstance(elementFromPoint));
		}
	}

	public void checkIngredientTypes(IngredientPoolLocationType ingSpace, Class classToProcess) {

		List<Ingredient> ingredients = ingSpace.getAllIngredients();
		assertTrue(ingredients.size() > 0);

		for (Ingredient ingredient : ingredients) {
			assertTrue(classToProcess.isInstance(ingredient.getCode()));
		}
	}

}
