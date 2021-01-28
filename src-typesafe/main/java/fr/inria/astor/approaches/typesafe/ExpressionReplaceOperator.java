package fr.inria.astor.approaches.typesafe;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.*;
import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtConstructorCallImpl;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.code.CtLocalVariableImpl;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.util.HashSet;
import java.util.Set;


public class ExpressionReplaceOperator extends ReplaceOp {

    @Override
    public boolean applyChangesInModel(OperatorInstance opInstance, ProgramVariant p) {

        CtExpression elementToModify = (CtExpression) opInstance.getOriginal();
        CtExpression elementOriginalCloned = (CtExpression) MutationSupporter.clone(elementToModify);

        CtElement elFixIngredient = opInstance.getModified();
        addThrowableIfNeeded(elFixIngredient, elementOriginalCloned);

        CtLocalVariable variableToBeInserted = checkIfRequiresLocalVariable(elFixIngredient, elementToModify);
        if (variableToBeInserted != null) {
            CtBlock block = elFixIngredient.getParent(CtBlock.class);
            if (variableToBeInserted != null) {
                block.insertBegin(variableToBeInserted);
            }
        }

        // we transform the Spoon model

        try {
            opInstance.getModificationPoint().getCodeElement().replace(elFixIngredient);
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

    private boolean addThrowableIfNeeded(CtElement fixIngredient, CtElement elementToModify) {
        CtMethodImpl methodOfFixIngredient = fixIngredient.getParent(CtMethodImpl.class);
        CtMethodImpl methodOfElementToModify = elementToModify.getParent(CtMethodImpl.class);
        if(methodOfFixIngredient == null || methodOfElementToModify == null) return false;
        Set<CtTypeReference> thrownTypesFromFixIngredient = methodOfFixIngredient.getThrownTypes();
        Set<CtTypeReference> thrownTypesFromElementToModify = methodOfElementToModify.getThrownTypes();
        CtType<?> classOfFixIngredient = fixIngredient.getPosition().getCompilationUnit().getMainType();
        CtType<?> classOfMP = elementToModify.getPosition().getCompilationUnit().getMainType();

        if (thrownTypesFromFixIngredient.isEmpty() || classOfMP.equals(classOfMP))
            return false;

        if (thrownTypesFromElementToModify.isEmpty()) {
            thrownTypesFromFixIngredient.forEach(thrownType -> {
                elementToModify.getParent(CtMethodImpl.class).addThrownType(thrownType);
            });
            return true;
        }
        return false;
    }

    /*private void nothing(OperatorInstance opInstance) {
        CtStatement ctst = (CtStatement) opInstance.getOriginal();
        CtStatement fix = (CtStatement) opInstance.getModified();
        StatementOperatorInstance stmtoperator = (StatementOperatorInstance) opInstance;
        CtBlock parentBlock = stmtoperator.getParentBlock();
    }*/

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
