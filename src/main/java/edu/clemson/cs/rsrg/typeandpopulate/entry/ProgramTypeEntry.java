/**
 * ProgramTypeEntry.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.entry;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.Map;

/**
 * TODO: Refactor this class
 */
public class ProgramTypeEntry extends SymbolTableEntry {

    public ProgramTypeEntry(TypeGraph g, String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, MTType modelType, PTType programType) {
        super(name, definingElement, sourceModule);
    }

    /**
     * <p>This method returns a description associated with this entry.</p>
     *
     * @return A string.
     */
    @Override
    public String getEntryTypeDescription() {
        return null;
    }

    /**
     * <p>This method converts a generic {@link SymbolTableEntry} to an entry
     * that has all the generic types and variables replaced with actual
     * values.</p>
     *
     * @param genericInstantiations Map containing all the instantiations.
     * @param instantiatingFacility Facility that instantiated this type.
     * @return A {@link SymbolTableEntry} that has been instantiated.
     */
    @Override
    public SymbolTableEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        return null;
    }

    public MathSymbolEntry toMathSymbolEntry(Location l) {
        return null;
    }

}