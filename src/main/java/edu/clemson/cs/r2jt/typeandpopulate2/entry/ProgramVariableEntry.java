package edu.clemson.cs.r2jt.typeandpopulate2.entry;

import edu.clemson.cs.r2jt.absynnew.ResolveAST;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import org.antlr.v4.runtime.Token;

import java.util.Map;

public class ProgramVariableEntry extends SymbolTableEntry {

    private final PTType myType;
    private final MathSymbolEntry myMathSymbolAlterEgo;

    public ProgramVariableEntry(String name,
                                ResolveAST definingElement,
                                ModuleIdentifier sourceModule, PTType type) {
        super(name, definingElement, sourceModule);

        myType = type;

        //TODO: Probably need to recajigger this to correctly account for any
        //      generics in the defining context
        myMathSymbolAlterEgo =
                new MathSymbolEntry(type.getTypeGraph(), name,
                        Quantification.NONE, definingElement, type.toMath(),
                        null, null, null, sourceModule);
    }

    public PTType getProgramType() {
        return myType;
    }

    @Override
    public String getEntryTypeDescription() {
        return "a program variable";
    }

    @Override
    public SymbolTableEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        SymbolTableEntry result;

        PTType instantiatedType =
                myType.instantiateGenerics(genericInstantiations,
                        instantiatingFacility);

        if (instantiatedType != myType) {
            result =
                    new ProgramVariableEntry(getName(), getDefiningElement(),
                            getSourceModuleIdentifier(), instantiatedType);
        }
        else {
            result = this;
        }

        return result;
    }

    public ProgramVariableEntry toProgramVariableEntry(Token l) {
        return this;
    }

    public MathSymbolEntry toMathSymbolEntry(Token l) {
        return myMathSymbolAlterEgo;
    }
}
