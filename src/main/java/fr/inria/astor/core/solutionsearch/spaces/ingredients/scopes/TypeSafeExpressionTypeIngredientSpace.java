package fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes;

import com.martiansoftware.jsap.JSAPException;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.util.expand.BinaryOperatorHelper;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtBinaryOperatorImpl;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.code.CtLiteralImpl;
import spoon.support.reflect.code.CtUnaryOperatorImpl;

import java.util.*;
import java.util.stream.Collectors;

public class TypeSafeExpressionTypeIngredientSpace extends ExpressionTypeIngredientSpace {

    BinaryOperatorHelper binaryOperatorHelper;

    public TypeSafeExpressionTypeIngredientSpace(List<TargetElementProcessor<?>> processors) throws JSAPException {
        super(processors);
        binaryOperatorHelper = new BinaryOperatorHelper();
    }

    @Override
    public void defineSpace(ProgramVariant variant) {

        List<CtType<?>> affected = obtainClassesFromScope(variant);
        log.debug("Creating Expression Ingredient space: ");
        for (CtType<?> classToProcess : affected) {

            List<CtCodeElement> ingredients = this.ingredientProcessor.createFixSpace(classToProcess);
            ingredients = expandIngredients(ingredients);
            TargetElementProcessor.mustClone = true;

            for (CtCodeElement originalIngredient : ingredients) {
                Ingredient ingredientOriginal = new Ingredient(originalIngredient);
                String keyLocation = mapKey(originalIngredient);
                if (originalIngredient instanceof CtExpression) {
                    CtExpression ctExpr = (CtExpression) originalIngredient;
                    // String typeExpression =
                    // ctExpr.getClass().getSimpleName();

                    if (ctExpr.getType() == null) {
                        continue;
                    }
                    List<Ingredient> ingredientsKey = getIngrediedientsFromKey(keyLocation, ctExpr);

                    if (ConfigurationProperties.getPropertyBool("cleantemplates")) {
                        MutationSupporter.getEnvironment().setNoClasspath(true);// ?

                        if (ctExpr instanceof CtLiteral) {
                            CtTypeReference type = ((CtLiteral<?>) ctExpr).getType();
                            CtLiteralImpl ctLiteral = (CtLiteralImpl) ctExpr;
                            CtLocalVariable local = MutationSupporter.factory.Code().createLocalVariable(type, "varname", ctLiteral);
                            CtElement parent = ctExpr.getParent();
                            ctExpr = MutationSupporter.factory.Code().createVariableRead(local.getReference(), false);
                            ctExpr.setParent(parent);
                        }
                        CtCodeElement templateElement = MutationSupporter.clone(ctExpr);
                        formatIngredient(templateElement);

                        Ingredient templateIngredient = new Ingredient(templateElement);

                        if (ConfigurationProperties.getPropertyBool("duplicateingredientsinspace")
                                || !ingredientsKey.contains(templateIngredient)) {
                            ingredientsKey.add(templateIngredient);
                            this.allElementsFromSpace.add(templateIngredient);
                        }
                        // We must always link elements, beyond the template is
                        // duplicate or new
                        // linking
                        this.linkTemplateElements.add(templateElement.toString(), ingredientOriginal);

                    } else {

                        if (ConfigurationProperties.getPropertyBool("duplicateingredientsinspace")
                                || !ingredientsKey.contains(originalIngredient)) {
                            // log.debug("Adding ingredient: " +
                            // originalIngredient);
                            ingredientsKey.add(ingredientOriginal);
                            // all
                            this.allElementsFromSpace.add(ingredientOriginal);
                        }
                    }
                }
            }
        }
        int nrIng = 0;
        // Printing summary:
        for (Object ingList : mkp.values()) {
            nrIng += ((List) ingList).size();
        }

        // sort links
        this.linkTemplateElements = this.linkTemplateElements.getSorted();
        log.info(String.format("Ingredient search space info : number keys %d , number values %d ", mkp.keySet().size(),
                nrIng));

        // this.linkTemplateElements.forEach((e,v) ->
        // log.debug(String.format("k: %s v: %d ", e,v.size())));

    }

    private List<CtCodeElement> expandIngredients(List<CtCodeElement> ingredients) {
        Set<CtCodeElement> uniqueExpandedIngredients = ingredients.stream().filter(e ->
                !e.toString().equals("super()")).collect(Collectors.toSet());
        List<CtCodeElement> binaryOperators = uniqueExpandedIngredients.stream().filter(i ->
                i.getClass().equals(CtBinaryOperatorImpl.class))
                .collect(Collectors.toList());
        Set<CtBinaryOperatorImpl> expandedBinaryOperators = expandBinaryOperators(binaryOperators);
        List<CtCodeElement> invocations = uniqueExpandedIngredients.stream().filter(i -> i.getClass().equals(CtInvocationImpl.class))
                .collect(Collectors.toList());
        Set<CtInvocationImpl> expandedInvocationsWithExecutables = expandInvocationsWithExecutables(invocations);
        uniqueExpandedIngredients.addAll(expandedBinaryOperators);
        uniqueExpandedIngredients.addAll(expandedInvocationsWithExecutables);
        Set<CtUnaryOperatorImpl> expandedInvocationsWithNegation = expandInvocationsWithNegation(uniqueExpandedIngredients);
        Set<CtCodeElement> set = Collections.newSetFromMap(new IdentityHashMap<>());
        set.addAll(uniqueExpandedIngredients);
        set.addAll(expandedInvocationsWithNegation);
        return set.stream().filter(i -> !i.toString().equals("null")).collect(Collectors.toList());
    }

    private Set<CtUnaryOperatorImpl> expandInvocationsWithNegation(Set<CtCodeElement> invocations) {
        TypeFactory typeFactory = new TypeFactory();
        Set<CtCodeElement> invocationsWithBooleanReturnType = invocations.stream()
                .filter(ctInvocation -> ctInvocation instanceof CtInvocationImpl &&
                        ((CtInvocationImpl) ctInvocation).getType().getSimpleName().equals("boolean"))
                .collect(Collectors.toSet());
        Set<CtUnaryOperatorImpl> negatedInvocations = new HashSet<>();
        invocationsWithBooleanReturnType.forEach(invocation -> {
            CtUnaryOperatorImpl ctUnaryOperator = new CtUnaryOperatorImpl();
            CtInvocationImpl clonedInvocation = (CtInvocationImpl) ((CtInvocationImpl) invocation).clone();
            clonedInvocation.setParent(invocation.getParent());
            ctUnaryOperator.setOperand(clonedInvocation);
            ctUnaryOperator.setParent(invocation.getParent());
            ctUnaryOperator.setKind(UnaryOperatorKind.NOT);
            CtTypeReference booleanType = typeFactory.booleanPrimitiveType();
            ctUnaryOperator.setType(booleanType);
            ctUnaryOperator.setPosition(invocation.getPosition());
            negatedInvocations.add(ctUnaryOperator);
        });

        return negatedInvocations;
    }

    private Set<CtInvocationImpl> expandInvocationsWithExecutables(List<CtCodeElement> invocations) {
        Set<CtCodeElement> set = new HashSet<>(invocations.size());
        Set<CtCodeElement> invocationsWithUniqueTarget = invocations.stream().filter(invocation ->
                ((CtInvocationImpl) invocation).getTarget() != null
                        && set.add(((CtInvocationImpl<?>) invocation).getTarget())).collect(Collectors.toSet());

        Set<CtInvocationImpl> expandedInvocations = new HashSet<>();
        invocationsWithUniqueTarget.forEach(invocation -> {
            Collection<CtExecutableReference<?>> executables = ((CtInvocationImpl) invocation).getTarget().getType().getAllExecutables();
            executables.forEach(executable -> {
                if (!executable.getParameters().isEmpty() && (!executable.getParameters().equals(((CtInvocationImpl<?>) invocation).getExecutable().getParameters())
                        || !executable.getType().equals(((CtInvocationImpl<?>) invocation).getType())))
                    return;
                CtInvocationImpl clonedInvocation = (CtInvocationImpl) ((CtInvocationImpl) invocation).clone();
                clonedInvocation.setParent(invocation.getParent());
                clonedInvocation.setExecutable(executable);
                if (executable.getParameters().isEmpty())
                    clonedInvocation.setArguments(executable.getActualTypeArguments());
                expandedInvocations.add(clonedInvocation);
            });
        });

        return expandedInvocations;
    }

    private Set<CtBinaryOperatorImpl> expandBinaryOperators(List<CtCodeElement> binaryOperators) {
        Set<CtBinaryOperatorImpl> allOperators = new HashSet<>();
        binaryOperators.forEach(element -> {
            CtBinaryOperatorImpl binaryOperator = ((CtBinaryOperatorImpl) element);
            CtExpression leftExpression = binaryOperator.getLeftHandOperand();
            CtExpression rightExpression = binaryOperator.getRightHandOperand();
            CtTypeReference type = leftExpression.getType();
            CtTypeReference returnType = binaryOperator.getType();
            if (returnType.getSimpleName().equals("boolean") && type.getSimpleName().equals("int")) {
                binaryOperatorHelper.getArithmeticOperatorsWhenReturnTypeIsBoolean().forEach(bo -> {
                    CtBinaryOperatorImpl synthesizedBinaryOperator =
                            (CtBinaryOperatorImpl) MutationSupporter.factory
                                    .createBinaryOperator(leftExpression, rightExpression, bo);
                    synthesizedBinaryOperator.setPosition(element.getPosition());
                    synthesizedBinaryOperator.setParent(element.getParent());
                    synthesizedBinaryOperator.setType(((CtBinaryOperatorImpl<?>) element).getType());
                    allOperators.add(synthesizedBinaryOperator);
                });
            } else if (type.getSimpleName().equals("int")) {
                binaryOperatorHelper.getArithmeticOperators().forEach(ao -> {
                    CtBinaryOperatorImpl synthesizedBinaryOperator =
                            (CtBinaryOperatorImpl) MutationSupporter.factory
                                    .createBinaryOperator(leftExpression, rightExpression, ao);
                    synthesizedBinaryOperator.setPosition(element.getPosition());
                    synthesizedBinaryOperator.setParent(element.getParent());
                    synthesizedBinaryOperator.setType(((CtBinaryOperatorImpl<?>) element).getType());
                    allOperators.add(synthesizedBinaryOperator);
                });
            } else if (type.getSimpleName().equals("boolean")) {
                binaryOperatorHelper.getBooleanOperators().forEach(ao -> {
                    CtBinaryOperatorImpl synthesizedBinaryOperator =
                            (CtBinaryOperatorImpl) MutationSupporter.factory
                                    .createBinaryOperator(leftExpression, rightExpression, ao);
                    synthesizedBinaryOperator.setPosition(element.getPosition());
                    synthesizedBinaryOperator.setParent(element.getParent());
                    synthesizedBinaryOperator.setType(((CtBinaryOperatorImpl<?>) element).getType());
                    allOperators.add(synthesizedBinaryOperator);
                });
            }
        });
        return allOperators;
    }

    @Override
    public List<Ingredient> getIngredients(CtElement element) {
        if (element instanceof CtExpression) {

            String keyLocation = mapKey(element);
            CtExpression ctExpr = (CtExpression) element;
            String returnTypeExpression = (ctExpr.getType() == null) ? "null" : ctExpr.getType().toString();
            List<Ingredient> ingredients = (List<Ingredient>) mkp.get(keyLocation, returnTypeExpression);

            return ingredients;
        }
        log.error("Element is not a expression: " + element.getClass().getCanonicalName());
        return null;
    }


}

