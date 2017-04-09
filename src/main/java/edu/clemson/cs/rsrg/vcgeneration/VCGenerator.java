/*
 * VCGenerator.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.vcgeneration;

import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.*;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.OperationProcedureDec;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.ProcedureDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ConstantParamDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.absyn.items.programitems.EnhancementSpecRealizItem;
import edu.clemson.cs.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.rsrg.init.flag.Flag;
import edu.clemson.cs.rsrg.init.flag.FlagDependencies;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.rsrg.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.query.EntryTypeQuery;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import edu.clemson.cs.rsrg.vcgeneration.absyn.statements.AssumeStmt;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.declaration.GenericVariableDeclRule;
import edu.clemson.cs.rsrg.vcgeneration.vcs.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.vcs.Sequent;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import java.util.*;

/**
 * <p>This class generates verification conditions (VCs) using the provided
 * RESOLVE abstract syntax tree. This visitor logic is implemented as
 * a {@link TreeWalkerVisitor}.</p>
 *
 * @author Heather Keown Harton
 * @author Yu-Shan Sun
 * @version 3.0
 */
public class VCGenerator extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The symbol table we are currently building.</p> */
    private final MathSymbolTableBuilder myBuilder;

    /**
     * <p>The current job's compilation environment
     * that stores all necessary objects and flags.</p>
     */
    private final CompileEnvironment myCompileEnvironment;

    /**
     * <p>The module scope for the file we are generating
     * {@code VCs} for.</p>
     */
    private ModuleScope myCurrentModuleScope;

    /**
     * <p>This is the math type graph that indicates relationship
     * between different math types.</p>
     */
    private final TypeGraph myTypeGraph;

    /** <p>The mathematical type Z.</p> */
    private MTType Z;

    // -----------------------------------------------------------
    // Operation Declaration-Related
    // -----------------------------------------------------------

    /**
     * <p>While walking a procedure, this is set to the entry for the operation
     * or {@link OperationProcedureDec} that the procedure is attempting to implement.</p>
     */
    private OperationEntry myCorrespondingOperation;

    // -----------------------------------------------------------
    // VC Generation-Related
    // -----------------------------------------------------------

    /**
     * <p>The current {@link AssertiveCodeBlock} that the inner declarations
     * will operate on.</p>
     */
    private AssertiveCodeBlock myCurrentAssertiveCodeBlock;

    /**
     * <p>All the completed {@link AssertiveCodeBlock AssertiveCodeBlocks}
     * that only contain the final {@link Sequent Sequents}.</p>
     */
    private final List<AssertiveCodeBlock> myFinalAssertiveCodeBlocks;

    /**
     * <p>A list that stores all the module level {@code constraint}
     * clauses for the various different declarations.</p>
     */
    private final Map<Dec, List<AssertionClause>> myGlobalConstraints;

    /**
     * <p>A list that stores all the module level {@code requires}
     * clauses.</p>
     */
    private final List<AssertionClause> myGlobalRequires;

    /**
     * <p>All the {@link AssertiveCodeBlock AssertiveCodeBlocks} generated by
     * the various different declaration and statements that still needs more
     * proof rule applications.</p>
     */
    private final Deque<AssertiveCodeBlock> myIncompleteAssertiveCodeBlocks;

    /**
     * <p>A map that stores all the details associated with
     * a particular {@link Location}.</p>
     */
    private final Map<Location, String> myLocationDetails;

    // -----------------------------------------------------------
    // Output-Related
    // -----------------------------------------------------------

    /** <p>String template for the VC generation model.</p> */
    private final ST myModel;

    /** <p>String template for the VC generation details model.</p> */
    private final ST myVCGenDetailsModel;

    /** <p>String template for the each of the assertive code blocks.</p> */
    private final Map<AssertiveCodeBlock, ST> myAssertiveCodeBlockModels;

    /** <p>String template groups for storing all the VC generation details.</p> */
    private final STGroup mySTGroup;

    // ===========================================================
    // Flag Strings
    // ===========================================================

    private static final String FLAG_SECTION_NAME = "VCGenerator";
    private static final String FLAG_DESC_VERIFY_VC = "Generate VCs.";
    private static final String FLAG_DESC_PERF_VC = "Generate Performance VCs";

    // ===========================================================
    // Flags
    // ===========================================================

    /**
     * <p>Tells the compiler to generate VCs.</p>
     */
    public static final Flag FLAG_VERIFY_VC =
            new Flag(FLAG_SECTION_NAME, "VCs", FLAG_DESC_VERIFY_VC);

    /**
     * <p>Tells the compiler to generate performance VCs.</p>
     */
    private static final Flag FLAG_PVCS_VC =
            new Flag(FLAG_SECTION_NAME, "PVCs", FLAG_DESC_PERF_VC);

    /**
     * <p>Add all the required and implied flags for the {@code VCGenerator}.</p>
     */
    public static void setUpFlags() {
        FlagDependencies.addImplies(FLAG_PVCS_VC, FLAG_VERIFY_VC);
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates an object that overrides methods to generate VCs from
     * a {@link ModuleDec}.</p>
     *
     * @param builder A scope builder for a symbol table.
     * @param compileEnvironment The current job's compilation environment
     *                           that stores all necessary objects and flags.
     * @param stGroup The string template group we will be using.
     * @param model The model we are going be generating.
     */
    public VCGenerator(MathSymbolTableBuilder builder,
            CompileEnvironment compileEnvironment, STGroup stGroup, ST model) {
        myAssertiveCodeBlockModels = new LinkedHashMap<>();
        myBuilder = builder;
        myCompileEnvironment = compileEnvironment;
        myFinalAssertiveCodeBlocks = new LinkedList<>();
        myGlobalConstraints = new LinkedHashMap<>();
        myGlobalRequires = new LinkedList<>();
        myIncompleteAssertiveCodeBlocks = new LinkedList<>();
        myLocationDetails = new LinkedHashMap<>();
        myModel = model;
        mySTGroup = stGroup;
        myTypeGraph = myBuilder.getTypeGraph();
        myVCGenDetailsModel = mySTGroup.getInstanceOf("outputVCGenDetails");
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Module Declarations
    // -----------------------------------------------------------

    /**
     * <p>Code that gets executed before visiting a {@link ModuleDec}.</p>
     *
     * @param dec A module declaration.
     */
    @Override
    public final void preModuleDec(ModuleDec dec) {
        // Set the current module scope
        try {
            myCurrentModuleScope =
                    myBuilder.getModuleScope(new ModuleIdentifier(dec));

            // Get "Z" from the TypeGraph
            Z = Utilities.getMathTypeZ(dec.getLocation(), myCurrentModuleScope);

            // Apply the facility declaration rule to imported facility declarations.
            List<FacilityEntry> results =
                    myCurrentModuleScope
                            .query(new EntryTypeQuery<>(
                                    FacilityEntry.class,
                                    ImportStrategy.IMPORT_NAMED,
                                    FacilityStrategy.FACILITY_INSTANTIATE));

            for (SymbolTableEntry s : results) {
                if (s.getSourceModuleIdentifier().compareTo(
                        myCurrentModuleScope.getModuleIdentifier()) != 0) {
                    // Do all the facility declaration logic, but don't add this
                    // to our incomplete assertive code stack. We shouldn't need to
                    // verify facility declarations that are imported.
                    FacilityDec facDec =
                            (FacilityDec) s.toFacilityEntry(dec.getLocation())
                                    .getDefiningElement();

                    // Store all requires/constraint from the imported concept
                    PosSymbol conceptName = facDec.getConceptName();
                    ModuleIdentifier coId = new ModuleIdentifier(conceptName.getName());
                    storeConceptAssertionClauses(conceptName.getLocation(), coId, true);

                    // Store all requires/constraint from the imported concept realization
                    // if it is not externally realized
                    if (!facDec.getExternallyRealizedFlag()) {
                        PosSymbol conceptRealizName = facDec.getConceptRealizName();
                        ModuleIdentifier coRealizId = new ModuleIdentifier(conceptRealizName.getName());
                        storeConceptRealizAssertionClauses(conceptRealizName.getLocation(),
                                coRealizId, true);
                    }

                    for (EnhancementSpecRealizItem specRealizItem : facDec.getEnhancementRealizPairs()) {
                        // Store all requires/constraint from the imported enhancement(s)
                        PosSymbol enhancementName = specRealizItem.getEnhancementName();
                        ModuleIdentifier enId = new ModuleIdentifier(enhancementName.getName());
                        storeEnhancementAssertionClauses(enhancementName.getLocation(),
                                enId, true);

                        // Store all requires/constraint from the imported enhancement realization(s)
                        PosSymbol enhancementRealizName = specRealizItem.getEnhancementRealizName();
                        ModuleIdentifier enRealizId = new ModuleIdentifier(enhancementRealizName.getName());
                        storeEnhancementRealizAssertionClauses(enhancementRealizName.getLocation(),
                                enRealizId, true);
                    }
                }
            }
        }
        catch (NoSuchSymbolException e) {
            Utilities.noSuchModule(dec.getLocation());
        }
    }

    /**
     * <p>Code that gets executed after visiting a {@link ModuleDec}.</p>
     *
     * @param dec A module declaration.
     */
    @Override
    public final void postModuleDec(ModuleDec dec) {
        // Loop through our incomplete assertive code blocks
        for (AssertiveCodeBlock block : myIncompleteAssertiveCodeBlocks) {
            // TODO: Apply statement rules to each of the incomplete blocks
            myVCGenDetailsModel.add("assertiveCodeBlocks",
                    myAssertiveCodeBlockModels.get(block).render());
        }
    }

    // -----------------------------------------------------------
    // Enhancement Realization Module
    // -----------------------------------------------------------

    /**
     * <p>Code that gets executed before visiting an {@link EnhancementRealizModuleDec}.</p>
     *
     * @param enhancementRealization An enhancement realization module declaration.
     */
    @Override
    public final void preEnhancementRealizModuleDec(
            EnhancementRealizModuleDec enhancementRealization) {
        PosSymbol enhancementRealizName = enhancementRealization.getName();

        // Store the enhancement realization requires clause
        storeRequiresClause(enhancementRealizName.getName(),
                enhancementRealization.getRequires());

        // Store all requires/constraint from the imported concept
        PosSymbol conceptName = enhancementRealization.getConceptName();
        ModuleIdentifier coId = new ModuleIdentifier(conceptName.getName());
        storeConceptAssertionClauses(conceptName.getLocation(), coId, false);

        // Store all requires/constraint from the imported enhancement
        PosSymbol enhancementName = enhancementRealization.getEnhancementName();
        ModuleIdentifier enId = new ModuleIdentifier(enhancementName.getName());
        storeEnhancementAssertionClauses(enhancementName.getLocation(), enId,
                false);

        // Add to VC detail model
        ST header =
                mySTGroup.getInstanceOf("outputEnhancementRealizHeader").add(
                        "realizName", enhancementRealizName.getName()).add(
                        "enhancementName", enhancementName.getName()).add(
                        "conceptName", conceptName.getName());
        myVCGenDetailsModel.add("fileHeader", header.render());
    }

    // -----------------------------------------------------------
    // Operation-Related
    // -----------------------------------------------------------

    /**
     * <p>Code that gets executed before visiting a {@link ProcedureDec}.</p>
     *
     * @param dec A procedure declaration.
     */
    @Override
    public final void preProcedureDec(ProcedureDec dec) {
        // Store the associated OperationEntry for future use
        List<PTType> argTypes = new LinkedList<>();
        for (ParameterVarDec p : dec.getParameters()) {
            argTypes.add(p.getTy().getProgramType());
        }
        myCorrespondingOperation =
                Utilities.searchOperation(dec.getLocation(), null, dec
                        .getName(), argTypes, myCurrentModuleScope);

        // TODO: Add the perfomance logic
        // Obtain the performance duration clause
        /*if (myInstanceEnvironment.flags.isFlagSet(FLAG_ALTPVCS_VC)) {
            myCurrentOperationProfileEntry =
                    Utilities.searchOperationProfile(dec.getLocation(), null,
                            dec.getName(), argTypes, myCurrentModuleScope);
        }*/

        // Check to see if this a local operation
        boolean isLocal =
                Utilities.isLocationOperation(dec.getName().getName(),
                        myCurrentModuleScope);

        // Create a new assertive code block
        myCurrentAssertiveCodeBlock =
                new AssertiveCodeBlock(myTypeGraph, dec, dec.getName());

        // Create the top most level assume statement and
        // add it to the assertive code block as the first statement
        AssumeStmt topLevelAssumeStmt = new AssumeStmt(dec.getLocation(),
                Utilities.createTopLevelAssumeExps(dec.getLocation(), myCurrentModuleScope,
                        myCurrentAssertiveCodeBlock, myLocationDetails, myGlobalRequires, myGlobalConstraints,
                        myCorrespondingOperation, isLocal),
                false);
        myCurrentAssertiveCodeBlock.addStatement(topLevelAssumeStmt);

        // Create a new model for this assertive code block
        ST blockModel = mySTGroup.getInstanceOf("outputAssertiveCodeBlock");
        blockModel.add("blockName", dec.getName());
        myAssertiveCodeBlockModels.put(myCurrentAssertiveCodeBlock, blockModel);
    }

    /**
     * <p>Code that gets executed after visiting a {@link ProcedureDec}.</p>
     *
     * @param dec A procedure declaration.
     */
    @Override
    public final void postProcedureDec(ProcedureDec dec) {
        // TODO: Figure out what works and what doesn't!
        /*Exp requires =
                modifyRequiresClause(getRequiresClause(loc, opDec), loc,
                        myCurrentAssertiveCode, opDec, isLocal);
        Exp ensures =
                modifyEnsuresClause(getEnsuresClause(loc, opDec), loc, opDec,
                        isLocal);
        List<Statement> statementList = dec.getStatements();
        List<ParameterVarDec> parameterVarList = dec.getParameters();
        List<VarDec> variableList = dec.getVariables();
        AssertionClause decreasing = dec.getDecreasing();

        // NY YS
        Exp procDur = null;
        Exp varFinalDur = null;
        /*if (myInstanceEnvironment.flags.isFlagSet(FLAG_ALTPVCS_VC)) {
            procDur = myCurrentOperationProfileEntry.getDurationClause();

            // Loop through local variables to get their finalization duration
            for (VarDec v : dec.getVariables()) {
                Exp finalVarDur = Utilities.createFinalizAnyDur(v, BOOLEAN);

                // Create/Add the duration expression
                if (varFinalDur == null) {
                    varFinalDur = finalVarDur;
                }
                else {
                    varFinalDur =
                            new InfixExp((Location) loc.clone(), varFinalDur,
                                    Utilities.createPosSymbol("+"), finalVarDur);
                }
                varFinalDur.setMathType(myTypeGraph.R);
            }
        }*/

        // Apply the procedure declaration rule
        /*applyProcedureDeclRule(loc, name, requires, ensures, decreasing,
                procDur, varFinalDur, parameterVarList, variableList,
                statementList, isLocal);

        // Loop through assertive code stack
        loopAssertiveCodeStack();

        myOperationDecreasingExp = null;
        myCurrentOperationProfileEntry = null;*/

        // Add the different details to the various different output models
        ST stepModel = mySTGroup.getInstanceOf("outputVCGenStep");
        stepModel.add("proofRuleName", "Procedure Declaration Rule").add(
                "currentStateOfBlock", myCurrentAssertiveCodeBlock);
        ST blockModel =
                myAssertiveCodeBlockModels.remove(myCurrentAssertiveCodeBlock);
        blockModel.add("vcGenSteps", stepModel.render());
        myAssertiveCodeBlockModels.put(myCurrentAssertiveCodeBlock, blockModel);

        // Add this as a new incomplete assertive code block
        myIncompleteAssertiveCodeBlocks.add(myCurrentAssertiveCodeBlock);

        myCurrentAssertiveCodeBlock = null;
        myCorrespondingOperation = null;
    }

    // -----------------------------------------------------------
    // Variable Declaration-Related
    // -----------------------------------------------------------

    /**
     * <p>Code that gets executed after visiting a {@link VarDec}.</p>
     *
     * @param dec A variable declaration.
     */
    @Override
    public final void postVarDec(VarDec dec) {
        // Ty should always be a NameTy
        if (dec.getTy() instanceof NameTy) {
            NameTy nameTy = (NameTy) dec.getTy();

            // Query for the type entry in the symbol table
            SymbolTableEntry ste =
                    Utilities.searchProgramType(nameTy.getLocation(), nameTy
                            .getQualifier(), nameTy.getName(),
                            myCurrentModuleScope);
            ProgramTypeEntry typeEntry;
            if (ste instanceof ProgramTypeEntry) {
                typeEntry = ste.toProgramTypeEntry(nameTy.getLocation());
            }
            else {
                typeEntry =
                        ste.toTypeRepresentationEntry(nameTy.getLocation())
                                .getDefiningTypeEntry();
            }
            ProofRuleApplication declRule;
            // Apply known type variable declaration rule

            // Apply generic type variable declaration
            declRule =
                    new GenericVariableDeclRule(dec,
                            myCurrentAssertiveCodeBlock, mySTGroup,
                            myAssertiveCodeBlockModels
                                    .remove(myCurrentAssertiveCodeBlock));

            // Update the current assertive code block and its associated block model.
            myCurrentAssertiveCodeBlock =
                    declRule.getAssertiveCodeBlocks().getFirst();
            myAssertiveCodeBlockModels.put(myCurrentAssertiveCodeBlock,
                    declRule.getBlockModel());
        }
        else {
            // Shouldn't be possible but just in case it ever happens
            // by accident.
            Utilities.tyNotHandled(dec.getTy(), dec.getLocation());
        }
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method returns the completed model with all the {@code VCs}
     * and the generation details.</p>
     *
     * @return String template rendering of the model.
     */
    public final String getCompleteModel() {
        // TODO: Add the VC output
        // Add the VC generation details to the model
        myModel.add("details", myVCGenDetailsModel.render());

        return myModel.render();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>An helper method for storing the imported {@code concept's}
     * {@code requires} clause and its associated location detail for
     * future use.</p>
     *
     * @param loc The location of the imported {@code module}.
     * @param id A {@link ModuleIdentifier} referring to an
     *           importing {@code concept}.
     * @param isFacilityImport A flag that indicates whether or not
     *                         we are storing information that originated
     *                         from a {@link FacilityDec}.
     */
    private void storeConceptAssertionClauses(Location loc,
            ModuleIdentifier id, boolean isFacilityImport) {
        try {
            ConceptModuleDec conceptModuleDec =
                    (ConceptModuleDec) myBuilder.getModuleScope(id)
                            .getDefiningElement();

            // We only need to store these if they are part of a FacilityDec
            if (!isFacilityImport) {
                // Store the concept's requires clause
                storeRequiresClause(conceptModuleDec.getName().getName(),
                        conceptModuleDec.getRequires());

                // Store the concept's type constraints from the module parameters
                storeModuleParameterTypeConstraints(conceptModuleDec
                        .getLocation(), conceptModuleDec.getParameterDecs());
            }

            // Store the concept's module constraints
            if (!conceptModuleDec.getConstraints().isEmpty()) {
                myGlobalConstraints.put(conceptModuleDec, conceptModuleDec
                        .getConstraints());
                myLocationDetails.put(conceptModuleDec.getLocation(),
                        "Constraint Clause for " + conceptModuleDec.getName());
            }
        }
        catch (NoSuchSymbolException e) {
            Utilities.noSuchModule(loc);
        }
    }

    /**
     * <p>An helper method for storing the imported {@code concept realization's}
     * {@code requires} clause and its associated location detail for
     * future use.</p>
     *
     * @param loc The location of the imported {@code module}.
     * @param id A {@link ModuleIdentifier} referring to an
     *           importing {@code concept realization}.
     * @param isFacilityImport A flag that indicates whether or not
     *                         we are storing information that originated
     *                         from a {@link FacilityDec}.
     */
    private void storeConceptRealizAssertionClauses(Location loc,
            ModuleIdentifier id, boolean isFacilityImport) {
        try {
            ConceptRealizModuleDec realizModuleDec =
                    (ConceptRealizModuleDec) myBuilder.getModuleScope(id)
                            .getDefiningElement();

            // We only need to store these if they are part of a FacilityDec
            if (!isFacilityImport) {
                // Store the concept realization's requires clause
                storeRequiresClause(realizModuleDec.getName().getName(),
                        realizModuleDec.getRequires());

                // Store the concept realization's type constraints from the module parameters
                storeModuleParameterTypeConstraints(realizModuleDec
                        .getLocation(), realizModuleDec.getParameterDecs());
            }
        }
        catch (NoSuchSymbolException e) {
            Utilities.noSuchModule(loc);
        }
    }

    /**
     * <p>An helper method for storing the imported {@code enhancement's}
     * {@code requires} clause and its associated location detail for
     * future use.</p>
     *
     * @param loc The location of the imported {@code module}.
     * @param id A {@link ModuleIdentifier} referring to an
     *           importing {@code enhancement}.
     * @param isFacilityImport A flag that indicates whether or not
     *                         we are storing information that originated
     *                         from a {@link FacilityDec}.
     */
    private void storeEnhancementAssertionClauses(Location loc,
            ModuleIdentifier id, boolean isFacilityImport) {
        try {
            EnhancementModuleDec enhancementModuleDec =
                    (EnhancementModuleDec) myBuilder.getModuleScope(id)
                            .getDefiningElement();

            // We only need to store these if they are part of a FacilityDec
            if (!isFacilityImport) {
                // Store the enhancement's requires clause
                storeRequiresClause(enhancementModuleDec.getName().getName(),
                        enhancementModuleDec.getRequires());

                // Store the enhancement's type constraints from the module parameters
                storeModuleParameterTypeConstraints(enhancementModuleDec
                        .getLocation(), enhancementModuleDec.getParameterDecs());
            }
        }
        catch (NoSuchSymbolException e) {
            Utilities.noSuchModule(loc);
        }
    }

    /**
     * <p>An helper method for storing the imported {@code enhancement realization's}
     * {@code requires} clause and its associated location detail for
     * future use.</p>
     *
     * @param loc The location of the imported {@code module}.
     * @param id A {@link ModuleIdentifier} referring to an
     *           importing {@code enhancement realization}.
     * @param isFacilityImport A flag that indicates whether or not
     *                         we are storing information that originated
     *                         from a {@link FacilityDec}.
     */
    private void storeEnhancementRealizAssertionClauses(Location loc,
            ModuleIdentifier id, boolean isFacilityImport) {
        try {
            EnhancementRealizModuleDec realizModuleDec =
                    (EnhancementRealizModuleDec) myBuilder.getModuleScope(id)
                            .getDefiningElement();

            // We only need to store these if they are part of a FacilityDec
            if (!isFacilityImport) {
                // Store the enhancement realization's requires clause
                storeRequiresClause(realizModuleDec.getName().getName(),
                        realizModuleDec.getRequires());

                // Store the enhancement realization's type constraints from the module parameters
                storeModuleParameterTypeConstraints(realizModuleDec
                        .getLocation(), realizModuleDec.getParameterDecs());
            }
        }
        catch (NoSuchSymbolException e) {
            Utilities.noSuchModule(loc);
        }
    }

    /**
     * <p>An helper method for storing all the {@code constraint} clauses
     * for a list of {@link ModuleParameterDec ModuleParameterDecs}.</p>
     *
     * @param loc The location of the {@code module} that contains the
     *            module parameters.
     * @param moduleParameterDecs A list of {@link ModuleParameterDec}.
     */
    private void storeModuleParameterTypeConstraints(Location loc,
            List<ModuleParameterDec> moduleParameterDecs) {
        for (ModuleParameterDec m : moduleParameterDecs) {
            Dec wrappedDec = m.getWrappedDec();
            if (wrappedDec instanceof ConstantParamDec) {
                ConstantParamDec dec = (ConstantParamDec) wrappedDec;
                ProgramTypeEntry typeEntry;

                if (dec.getVarDec().getTy() instanceof NameTy) {
                    NameTy pNameTy = (NameTy) dec.getVarDec().getTy();

                    // Query for the type entry in the symbol table
                    SymbolTableEntry ste =
                            Utilities.searchProgramType(pNameTy.getLocation(),
                                    pNameTy.getQualifier(), pNameTy.getName(),
                                    myCurrentModuleScope);

                    if (ste instanceof ProgramTypeEntry) {
                        typeEntry =
                                ste.toProgramTypeEntry(pNameTy.getLocation());
                    }
                    else {
                        typeEntry =
                                ste.toTypeRepresentationEntry(
                                        pNameTy.getLocation())
                                        .getDefiningTypeEntry();
                    }

                    // Make sure we don't have a generic type
                    if (typeEntry.getDefiningElement() instanceof TypeFamilyDec) {
                        // Obtain the original dec from the AST
                        TypeFamilyDec type =
                                (TypeFamilyDec) typeEntry.getDefiningElement();

                        if (!VarExp.isLiteralTrue(type.getConstraint()
                                .getAssertionExp())) {
                            AssertionClause constraintClause =
                                    type.getConstraint();
                            AssertionClause modifiedConstraint =
                                    Utilities.getTypeConstraintClause(
                                            constraintClause,
                                            dec.getLocation(), null, dec
                                                    .getName(), type
                                                    .getExemplar(), typeEntry
                                                    .getModelType(), null);

                            // Store the constraint and its associated location detail for future use
                            myGlobalConstraints.put(dec, Collections
                                    .singletonList(modifiedConstraint));
                            myLocationDetails.put(modifiedConstraint
                                    .getLocation(), "Constraint Clause for "
                                    + dec.getName());
                        }
                    }
                }
                else {
                    Utilities.tyNotHandled(dec.getVarDec().getTy(), loc);
                }
            }
        }
    }

    /**
     * <p>An helper method for storing a {@code requires} clause and its
     * associated location detail for future use.</p>
     *
     * @param decName Name of the declaration that contains
     *                the {@code requiresClause}.
     * @param requiresClause An {@link AssertionClause} containing a {@code requires} clause.
     */
    private void storeRequiresClause(String decName,
            AssertionClause requiresClause) {
        if (!VarExp.isLiteralTrue(requiresClause.getAssertionExp())) {
            myGlobalRequires.add(requiresClause);

            // Add the location details for both the requires and
            // which_entails expressions (if any).
            myLocationDetails.put(requiresClause.getAssertionExp()
                    .getLocation(), "Requires Clause for " + decName);
            if (requiresClause.getWhichEntailsExp() != null) {
                myLocationDetails.put(requiresClause.getWhichEntailsExp()
                        .getLocation(),
                        "Which_Entails expression for clause located at "
                                + requiresClause.getWhichEntailsExp()
                                        .getLocation());
            }
        }
    }

}