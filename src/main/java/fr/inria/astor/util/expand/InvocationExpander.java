package fr.inria.astor.util.expand;

import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.code.CtUnaryOperatorImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InvocationExpander {

    TypeFactory typeFactory;
    private final CtTypeReference<Boolean> BOOLEAN_PRIMITIVE_TYPE;
    private final CtTypeReference<Integer> INTEGER_PRIMITIVE_TYPE;
    InvocationExpanderHelper invocationExpanderHelper;

    public InvocationExpander(){
        typeFactory = MutationSupporter.getFactory().Type();
        BOOLEAN_PRIMITIVE_TYPE = typeFactory.booleanPrimitiveType();
        INTEGER_PRIMITIVE_TYPE = typeFactory.integerPrimitiveType();
        invocationExpanderHelper = new InvocationExpanderHelper();
    }



    /** Given a set of invocations, return a set of all different permutations for each invocation.
     *  {@link InvocationExpanderHelper#createAllPermutationsFromInvocation(CtInvocationImpl)}
     *
     * @param invocations .
     * @return .
     */
    public Set<Set<CtInvocationImpl>> createAllPermutationsFromInvocations(Set<CtInvocationImpl> invocations) {
        return invocations.stream()
                .map(invocation -> invocationExpanderHelper.createAllPermutationsFromInvocation(invocation)).collect(Collectors.toSet());
    }


    /** Given a set of invocation, return a set of possible negates invocations.
     * We can only negate invocation of boolean type
     * {@link InvocationExpanderHelper#createNegatedInvocation(CtInvocationImpl)}
     * 
     * @param invocations
     * @return
     */
    public Set<CtUnaryOperatorImpl> expandInvocationsWithNegation(Set<CtInvocationImpl> invocations) {
        Set<CtInvocationImpl> invocationsWithBooleanReturnType = invocations.stream()
                .filter(invocation -> invocation.getType().equals(BOOLEAN_PRIMITIVE_TYPE))
                .collect(Collectors.toSet());
        return invocationsWithBooleanReturnType.stream()
                .map(invocation -> invocationExpanderHelper.createNegatedInvocation(invocation)).collect(Collectors.toSet());
    }


}
