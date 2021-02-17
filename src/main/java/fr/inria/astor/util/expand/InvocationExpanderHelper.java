package fr.inria.astor.util.expand;

import fr.inria.astor.core.manipulation.MutationSupporter;
import org.paukov.combinatorics3.Generator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.factory.TypeFactory;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.code.CtUnaryOperatorImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *  Helper class for {@link InvocationExpander}
 */
public class InvocationExpanderHelper {

    TypeFactory typeFactory = MutationSupporter.factory.Type();

    /**
     * Given an invocation, create all possible permutations (of the arguments) from that invocations
     * Example: Given the CtInvocation classA.getMethod(a, b ,c) return:
     * classA.getMethod(a, b ,c)
     * classA.getMethod(a, c ,b)
     * classA.getMethod(b, a ,c)
     * classA.getMethod(b, c ,a)
     * classA.getMethod(c, b ,a)
     * classA.getMethod(c, a ,b)
     *
     * @param ctInvocation .
     * @return .
     */
    public Set<CtInvocationImpl> createAllPermutationsFromInvocation(CtInvocationImpl ctInvocation) {
        List<CtExpression<?>> arguments = ctInvocation.getArguments();
        Set<List<CtExpression<?>>> argumentsPermutations = getAllPermutations(arguments);
        return argumentsPermutations.stream().map(permutation -> createInvocationWithArguments(ctInvocation, permutation)).collect(Collectors.toSet());
    }

    /**
     * Given a list of arguments in the form (a , b ,c) then return all possible permutations:
     * (a, c ,b)
     * (b, a ,c)
     * (b, c ,a)
     * (c, b ,a)
     * (c, a ,b)
     *
     * @param arguments .
     * @return .
     */
    private Set<List<CtExpression<?>>> getAllPermutations(List<CtExpression<?>> arguments) {
        return Generator.permutation(arguments)
                .simple()
                .stream().collect(Collectors.toSet());
    }

    private CtInvocationImpl createInvocationWithArguments(CtInvocationImpl ctInvocation, List<CtExpression<?>> arguments) {
        CtInvocationImpl clonedInvocation = (CtInvocationImpl) MutationSupporter.clone(ctInvocation);
        clonedInvocation.setArguments(arguments);
        return clonedInvocation;
    }

    /** Given an invocation, return a negated version of this invocation.
     * It returns  negated invocation if this invocation if of boolean type.
     *
     * @param invocation .
     * @return .
     */
    public CtUnaryOperatorImpl createNegatedInvocation(CtInvocationImpl invocation){
        CtUnaryOperatorImpl ctUnaryOperator = new CtUnaryOperatorImpl();
        CtInvocationImpl clonedInvocation = (CtInvocationImpl) MutationSupporter.clone(invocation);
        clonedInvocation.setParent(invocation.getParent());
        ctUnaryOperator.setOperand(clonedInvocation);
        ctUnaryOperator.setParent(invocation.getParent());
        ctUnaryOperator.setType(typeFactory.booleanPrimitiveType());
        ctUnaryOperator.setKind(UnaryOperatorKind.NOT);
        // Weird case of spoon. Spoon adds parentheses when ctUnaryOperator's parent is same
        String possibleUnaryWithoutParenthesis = ctUnaryOperator.toString().substring(1, ctUnaryOperator.toString().length() - 1);
        if (possibleUnaryWithoutParenthesis.equals(ctUnaryOperator.getParent().toString()))
            ctUnaryOperator = (CtUnaryOperatorImpl) ctUnaryOperator.getParent();
        return ctUnaryOperator;
    }


}
