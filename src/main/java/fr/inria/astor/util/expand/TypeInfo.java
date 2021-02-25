package fr.inria.astor.util.expand;

import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;

import java.util.LinkedList;
import java.util.List;

public class TypeInfo {

    private final TypeFactory typeFactory = MutationSupporter.getFactory().Type();
    private final List<CtTypeReference> arithmeticTypes =  new LinkedList<>();


    public TypeInfo(){
        initializeArithmeticTypes();
    }


    public  List<CtTypeReference> getArithmeticTypes(){
        return arithmeticTypes;
    }

    private void initializeArithmeticTypes() {
        arithmeticTypes.add(typeFactory.integerPrimitiveType());
        arithmeticTypes.add(typeFactory.longPrimitiveType());
        arithmeticTypes.add(typeFactory.floatPrimitiveType());
        arithmeticTypes.add(typeFactory.doubleType());
        arithmeticTypes.add(typeFactory.integerType());
        arithmeticTypes.add(typeFactory.longType());
        arithmeticTypes.add(typeFactory.floatType());
        arithmeticTypes.add(typeFactory.doubleType());
    }

}
