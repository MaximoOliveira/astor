package fr.inria.astor.approaches.typesafe;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.*;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.solutionsearch.population.ProgramVariantFactory;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.*;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.util.*;
import java.util.stream.Collectors;


public class ExpressionReplaceOperator extends ReplaceOp {

    ProgramVariantFactory programVariantFactory = new ProgramVariantFactory();

    @Override
    public boolean applyChangesInModel(OperatorInstance opInstance, ProgramVariant p) {

        CtExpression elementToModify = (CtExpression) opInstance.getOriginal();
        CtExpression elementOriginalCloned = (CtExpression) MutationSupporter.clone(elementToModify);

        CtElement elFixIngredient = opInstance.getModified();
        List<CtTypeReference> exceptions = needsTryCatch(elFixIngredient, elementToModify);

        // we transform the Spoon model

        try {
            opInstance.getModificationPoint().getCodeElement().replace(elFixIngredient);
            if (!exceptions.isEmpty()) {
                addTryCatchIfNeeded(opInstance, exceptions);
            }
        } catch (Exception e) {
            log.error("error to modify " + elementOriginalCloned + " to " + elFixIngredient);
            log.equals(e);
            opInstance.setExceptionAtApplied(e);
            return false;
        }

        // I save the original instance
        opInstance.setOriginal(elementOriginalCloned);
        // Finally, we update the modification point (i.e., Astor
        // Representation)
        opInstance.getModificationPoint().setCodeElement(elFixIngredient);

        boolean change = !opInstance.getModificationPoint().getCodeElement().toString()
                .equals(elementOriginalCloned.toString());

        if (!change)
            log.error("Replacement does not work for  modify " + elementOriginalCloned + " to " + elFixIngredient);

        return true;
    }

    private void addTryCatchIfNeeded(OperatorInstance opInstance, List<CtTypeReference> xcx) {
        // Create try catch
        CtMethod methodOfETM = opInstance.getModificationPoint().getCodeElement().getParent(CtMethodImpl.class);
        CtBlockImpl blockCode = (CtBlockImpl) methodOfETM.getBody().clone();
        CtTryImpl ctTry = createTryCatch(blockCode, xcx, methodOfETM.getType());
        CtBlockImpl newBlock = new CtBlockImpl();
        newBlock.addStatement(ctTry);
        CtMethod newMethod = methodOfETM.clone();
        newMethod.setBody(newBlock);
        opInstance.getModificationPoint().getCodeElement().getParent(CtMethodImpl.class).replace(newMethod);
    }

    private CtTryImpl createTryCatch(CtBlock bodyOfTry, List<CtTypeReference> catchedExceptions,
                                     CtTypeReference returnType) {
        // Prepare Catched Exceptions
        CtCatchImpl ctCatch = new CtCatchImpl();
        CtCatchVariableImpl ctCatchVariable = new CtCatchVariableImpl();
        ctCatchVariable.setMultiTypes(catchedExceptions);
        ctCatchVariable.setSimpleName("eee");
        ctCatch.setParameter(ctCatchVariable);

        //Prepare Catch Body
        CtLiteral ctLiteral;
        if (returnType.toString().equals("void")) {
            ctLiteral = null;
        } else if (returnType.toString().equals("boolean")) {
            ctLiteral = MutationSupporter.factory.Code().createLiteral(Boolean.FALSE);
        } else if (returnType.isPrimitive()) {
            ctLiteral = MutationSupporter.factory.Code().createLiteral(0);
        } else {
            ctLiteral = MutationSupporter.factory.Code().createLiteral(null);
        }
        CtReturnImpl ctReturn = new CtReturnImpl();
        ctReturn.setReturnedExpression(ctLiteral);
        ctCatch.setBody(ctReturn);

        // Prepare Try Body
        CtTryImpl ctTry = new CtTryImpl();
        ctTry.setBody(bodyOfTry);
        ctTry.addCatcher(ctCatch);
        return ctTry;
    }


    private void addThrowsToMethodsThatNeed(CtExpression elementToModify, CtElement elFixIngredient) {
        //List<CtInvocationImpl> invocations = MutationSupporter.getInvocations();
        //List<CtInvocationImpl> correctInvocations = getInvocationsEqualTo(invocations, ((CtInvocationImpl) elementToModify).getTarget());
        //Set<CtMethodImpl> methodsThatUseInvocations = getMethodsThatUseInvocations(correctInvocations);
        Set<CtMethod<?>> allMethods = MutationSupporter.getAllMethods();
        CtMethodImpl methodOfFixIngredient = elFixIngredient.getParent(CtMethodImpl.class);
        Set<CtTypeReference> thrownTypesFromFixIngredient = methodOfFixIngredient.getThrownTypes();
        addThrowsToMethods(allMethods, thrownTypesFromFixIngredient);
    }

    private Set<CtMethodImpl> getMethodsThatUseInvocations(List<CtInvocationImpl> correctInvocations) {
        return correctInvocations.stream()
                .map(ctInvocation -> ctInvocation.getParent(CtMethodImpl.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private List<CtInvocationImpl> getInvocationsEqualTo(List<CtInvocationImpl> invocations, CtExpression target) {
        return invocations.stream().filter(ctInvocation -> ctInvocation.getTarget() != null
                && ctInvocation.getTarget().toString().equals(target.toString())).collect(Collectors.toList());
    }


    private void addThrowsToMethods(Set<CtMethod<?>> methodsOfInvocations, Set<CtTypeReference> thrownTypesFromFixIngredient) {
        methodsOfInvocations.forEach(ctMethod -> {
            thrownTypesFromFixIngredient.forEach(ctMethod::addThrownType);
        });

    }

    private List<CtTypeReference> needsTryCatch(CtElement fixIngredient, CtElement elementToModify) {

        CtClass ctClass = fixIngredient.getParent(CtClass.class);
        CtInvocationImpl invocation = fixIngredient.getElements(new TypeFilter<>(CtInvocationImpl.class)).stream().findFirst().orElse(null);
        if (invocation == null) return new ArrayList<>();
        CtExecutableReference executable = invocation.getExecutable();
        List<CtTypeReference<?>> myList = executable.getParameters();
        CtTypeReference<?>[] zz = myList.toArray(new CtTypeReference<?>[0]);
        CtMethod methodOfFixIngredient = ctClass.getMethod(executable.getType(), executable.getSimpleName(), zz);
        if (methodOfFixIngredient == null) return new ArrayList<>();
        CtMethodImpl methodOfElementToModify = elementToModify.getParent(CtMethodImpl.class);
        if (methodOfFixIngredient == null || methodOfElementToModify == null) return new ArrayList<>();
        Set<CtTypeReference> thrownTypesFromFixIngredient = methodOfFixIngredient.getThrownTypes();
        Set<CtTypeReference> thrownTypesFromElementToModify = methodOfElementToModify.getThrownTypes();

        Set<CtTypeReference> candidatesToBeAdded = new HashSet<>(thrownTypesFromFixIngredient);
        candidatesToBeAdded.removeAll(thrownTypesFromElementToModify);

        Set<CtTypeReference> supClassesFromETM =
                thrownTypesFromElementToModify.stream().map(CtTypeReference::getSuperclass)
                        .collect(Collectors.toSet());

        candidatesToBeAdded = candidatesToBeAdded.stream().filter(ctTypeReference ->
                !supClassesFromETM.contains(ctTypeReference.getSuperclass()))
                .collect(Collectors.toSet());

        return new ArrayList<>(candidatesToBeAdded);
    }

    private boolean addThrowableIfNeeded(CtElement fixIngredient, CtElement elementToModify) {

        CtClass ctClass = fixIngredient.getParent(CtClass.class);
        CtInvocationImpl invocation = fixIngredient.getElements(new TypeFilter<>(CtInvocationImpl.class)).stream().findFirst().orElse(null);
        if (invocation == null) return false;
        CtExecutableReference executable = invocation.getExecutable();
        List<CtTypeReference<?>> myList = executable.getParameters();
        CtTypeReference<?>[] zz = myList.toArray(new CtTypeReference<?>[0]);
        CtMethod methodOfFixIngredient = ctClass.getMethod(executable.getType(), executable.getSimpleName(), zz);
        if (methodOfFixIngredient == null) return false;
        //ctClass.getMethod(invocation.getExecutable().getType(),invocation.getExecutable().getSimpleName(),invocation.getExecutable().getParameters());
        CtMethodImpl methodOfElementToModify = elementToModify.getParent(CtMethodImpl.class);
        if (methodOfFixIngredient == null || methodOfElementToModify == null) return false;
        Set<CtTypeReference> thrownTypesFromFixIngredient = methodOfFixIngredient.getThrownTypes();
        Set<CtTypeReference> thrownTypesFromElementToModify = methodOfElementToModify.getThrownTypes();

        Set<CtTypeReference> candidatesToBeAdded = new HashSet<>(thrownTypesFromFixIngredient);
        candidatesToBeAdded.removeAll(thrownTypesFromElementToModify);

        Set<CtTypeReference> supClassesFromETM =
                thrownTypesFromElementToModify.stream().map(CtTypeReference::getSuperclass)
                        .collect(Collectors.toSet());

        candidatesToBeAdded = candidatesToBeAdded.stream().filter(ctTypeReference ->
                !supClassesFromETM.contains(ctTypeReference.getSuperclass()))
                .collect(Collectors.toSet());


        if (candidatesToBeAdded.isEmpty())
            return false;

        candidatesToBeAdded.forEach(thrownType -> {
            elementToModify.getParent(CtMethodImpl.class).addThrownType(thrownType);
        });
        return true;
    }

    private boolean thrownTypeHasSameSuperClass(Set<CtTypeReference> ctTypeReferences, CtTypeReference ctTypeReference) {
        return ctTypeReferences.stream().map(CtTypeReference::getSuperclass)
                .anyMatch(superclass -> superclass.equals(ctTypeReference.getSuperclass()));
    }

    private CtLocalVariable checkIfRequiresLocalVariable(CtElement elFixIngredient, CtExpression elementToModify) {
        if (!fixIngredientClassIsSubtypeOrSameTypeOfETM(elFixIngredient, elementToModify)
                && !ingredientHasTarget(elFixIngredient)
                && !executableOfIngredientIsPrivate(elFixIngredient)) {
            return createInstanceOfClass(elFixIngredient);
        }
        return null;
    }

    /**
     * @param fixIngredient   the fix ingredient
     * @param elementToModify the element to modify
     * @return true if the fix ingredient belongs to a class that is subtype of the class from the element to modify
     */
    private boolean fixIngredientClassIsSubtypeOrSameTypeOfETM(CtElement fixIngredient, CtElement elementToModify) {
        CtTypeReference<?> classOfElementToModify = elementToModify.getPosition().getCompilationUnit().getMainType().getReference();
        CtTypeReference<?> classOfFixIngredientClass = fixIngredient.getPosition().getCompilationUnit().getMainType().getReference();
        return classOfFixIngredientClass.isSubtypeOf(classOfElementToModify)
                || classOfElementToModify.equals(classOfFixIngredientClass);
    }

    public boolean executableOfIngredientIsPrivate(CtElement fixIngredient) {
        if (fixIngredient instanceof CtInvocationImpl) {
            return ((CtModifiable) ((CtInvocation<?>) fixIngredient).getExecutable().getDeclaration()).isPrivate();
        }
        return false;
    }

    private boolean ingredientHasTarget(CtElement fixIngredient) {
        if (fixIngredient instanceof CtInvocation) {
            // if the target is "", it means that it wont have a prefix
            return !((CtInvocationImpl) fixIngredient).getTarget().toString().isEmpty();
        }
        return true;
    }

    private void addTargetToFixIngredient(CtInvocation fixIngredient, CtExpression target) {
        fixIngredient.setTarget(target);
    }

    private CtLocalVariable createInstanceOfClass(CtElement fixIngredient) {
        //CodeFactory codeFactory = MutationSupporter.getFactory().Code();
        CtTypeReference<?> classOfFixIngredient = fixIngredient.getPosition().getCompilationUnit().getMainType().getReference();
        CtConstructorCall ctConstructorCall = new CtConstructorCallImpl();
        ctConstructorCall.setType(classOfFixIngredient);
        CtLocalVariable ctLocalVariable = new CtLocalVariableImpl();
        ctLocalVariable.setType(classOfFixIngredient);
        ctLocalVariable.setSimpleName("testClass");
        ctLocalVariable.setAssignment(ctConstructorCall);
        return ctLocalVariable;
    }

    @Override
    public boolean undoChangesInModel(OperatorInstance opInstance, ProgramVariant p) {

        // We update the spoon Model
        opInstance.getModificationPoint().getCodeElement().replace(opInstance.getOriginal());
        // Finally, we update the modification point (i.e., Astor
        // Representation)
        opInstance.getModificationPoint().setCodeElement(opInstance.getOriginal());
        return true;
    }

    @Override
    public boolean updateProgramVariant(OperatorInstance opInstance, ProgramVariant p) {
        // We dont need to update the variant here
        return false;
    }

    @Override
    public boolean canBeAppliedToPoint(ModificationPoint point) {

        return (point.getCodeElement() instanceof CtExpression);
    }

    @Override
    protected OperatorInstance createOperatorInstance(ModificationPoint mp) {
        OperatorInstance operation = new OperatorInstance(mp, this, mp.getCodeElement(), null);
        return operation;
    }

    @Override
    protected OperatorInstance createOperatorInstance(ModificationPoint mp, Ingredient ingredient) {
        CtElement toModif = mp.getCodeElement();
        CtElement ingredCOde = ingredient.getCode();

        // Or both statement or both not statement
        boolean isStmtToModif = isStatement(toModif);
        boolean isStmtnIngr = isStatement(ingredCOde);
        if (isStmtToModif ^ isStmtnIngr)
            return null;

        return super.createOperatorInstance(mp, ingredient);
    }

    public boolean isStatement(CtElement toModif) {

        if (!(toModif instanceof CtStatement))
            return false;

        if (toModif.getParent() instanceof CtBlock)
            return true;

        CtRole roleInParent = toModif.getRoleInParent();

        if (CtRole.BODY.equals(roleInParent) || CtRole.THEN.equals(roleInParent) || CtRole.ELSE.equals(roleInParent))
            return true;

        return false;
    }

}
