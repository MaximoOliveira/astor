package fr.inria.astor.util.expand;

import one.util.streamex.StreamEx;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtType;
import spoon.support.reflect.code.CtBinaryOperatorImpl;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Expander {

    private final BinaryOperatorExpander binaryOperatorExpander;
    private final InvocationExpander invocationExpander;

    public Expander() {
        invocationExpander = new InvocationExpander();
        binaryOperatorExpander = new BinaryOperatorExpander();
    }

    public List<CtCodeElement> expandIngredientSpace(List<CtCodeElement> ingredients, CtType<?> classToProcess) {
        Set<CtCodeElement> uniqueExpandedIngredients = ingredients.stream().filter(e ->
                !e.toString().equals("super()")).collect(Collectors.toSet());

        Set<CtBinaryOperatorImpl> expandedBinaryOperators = binaryOperatorExpander.createBinaryOperators(uniqueExpandedIngredients);
        Set<CtCodeElement> expandedInvocations = invocationExpander.createInvocations(uniqueExpandedIngredients, classToProcess);
        Set<CtCodeElement> set = Collections.newSetFromMap(new IdentityHashMap<>());
        set.addAll(uniqueExpandedIngredients);
        set.addAll(expandedBinaryOperators);
        set.addAll(expandedInvocations);
        List<CtCodeElement> noNulls = set.stream().filter(i -> !i.toString().equals("null")).collect(Collectors.toList());
        return StreamEx.of(noNulls).distinct(CtCodeElement::toString).toList();
    }

}
