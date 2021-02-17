package fr.inria.astor.util.expand;

import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.code.CtUnaryOperatorImpl;

import java.util.HashSet;
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

    /** Given a set of invocations, create all possible invocations with executables that are extended by any class
     *  from which target's type inherits. For instance:
     *  If we have an invocation: myObject.method() then:
     *      the target of this invocation is: myObject
     *  If myObject if of type A and class A only extends Object
     *  then create invocation with executables that exist in class A and class Object
     *  Example of some invocations created:
     *      myClass.clone()
     *      myClass.wait(long, int)
     *      etc...
     *
     * @param invocations .
     * @return .
     */
    public Set<CtInvocationImpl> createInvocationsWithAllPossibleExecutables(Set<CtInvocationImpl> invocations) {
        Set<CtCodeElement> set = new HashSet<>(invocations.size());
        Set<CtInvocationImpl> invocationsWithUniqueTarget = invocations.stream().filter(invocation ->
                invocation.getTarget() != null
                        && set.add((invocation).getTarget())).collect(Collectors.toSet());

        Set<CtInvocationImpl> expandedInvocations = new HashSet<>();
        Set<CtInvocationImpl> expandedInvocationsWithNoParams = invocationExpanderHelper.createInvocationsWithNoArgExecutables(invocationsWithUniqueTarget);
        Set<CtInvocationImpl> expandedInvocationsWithParams = invocationExpanderHelper.createInvocationsWithArgExecutables(invocationsWithUniqueTarget);
        expandedInvocations.addAll(expandedInvocationsWithNoParams);
        expandedInvocations.addAll(expandedInvocationsWithParams);


        return expandedInvocations;
    }



}
