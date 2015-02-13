/**
 * ModuleAST.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absynnew;

import edu.clemson.cs.r2jt.absynnew.decl.ModuleParameterAST;
import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import org.antlr.v4.runtime.Token;

import java.util.Collections;
import java.util.List;

/**
 * <p>The parent class of all <tt>RESOLVE</tt> module types.</p>
 */
public abstract class ModuleAST extends ResolveAST {

    private final Token myName;
    private final List<ModuleParameterAST> myModuleParams;
    private final ImportBlockAST myImportBlock;
    private final ExprAST myRequires;
    private final ModuleBlockAST myBodyBlock;

    public ModuleAST(Token start, Token stop, Token name, ImportBlockAST uses,
            List<ModuleParameterAST> params, ExprAST req, ModuleBlockAST block) {
        super(start, stop);
        myName = name;

        myModuleParams = params;
        myImportBlock = uses;
        myRequires = req;
        myBodyBlock = block;
    }

    public Token getName() {
        return myName;
    }

    public List<ModuleParameterAST> getParameters() {
        return myModuleParams;
    }

    public ImportBlockAST getImportBlock() {
        return myImportBlock;
    }

    public ExprAST getRequires() {
        return myRequires;
    }

    public ModuleBlockAST getBodyBlock() {
        return myBodyBlock;
    }

    public boolean appropriateForTranslation() {
        return true;
    }

    public boolean appropriateForImport() {
        return true;
    }

    /**
     * <p>A <code>ConceptAST</code> is <tt>RESOLVE</tt>'s abstract syntax
     * encapsulation of an 'interface-like-module' containing formal
     * specifications for user defined types and operations.</p>
     */
    public static class ConceptAST extends ModuleAST {

        private ConceptAST(ConceptBuilder builder) {
            super(builder.getStart(), builder.getStop(), builder.getName(),
                    builder.usesBlock, builder.moduleParameters,
                    builder.requires, builder.block);
        }

        public static class ConceptBuilder
                extends
                    ModuleBuilderExtension<ConceptBuilder> {

            public ConceptBuilder(Token start, Token stop, Token name) {
                super(start, stop, name);
            }

            @Override
            public ConceptAST build() {
                return new ConceptAST(this);
            }
        }
    }

    public static class FacilityAST extends ModuleAST {

        private FacilityAST(FacilityBuilder builder) {
            super(builder.getStart(), builder.getStop(), builder.getName(),
                    builder.usesBlock, Collections
                            .<ModuleParameterAST> emptyList(),
                    builder.requires, builder.block);
        }

        public static class FacilityBuilder
                extends
                    ModuleBuilderExtension<FacilityBuilder> {

            public FacilityBuilder(Token start, Token stop, Token name) {
                super(start, stop, name);
            }

            @Override
            public FacilityAST build() {
                return new FacilityAST(this);
            }
        }
    }

    public static class PrecisAST extends ModuleAST {

        private PrecisAST(PrecisBuilder builder) {
            super(builder.getStart(), builder.getStop(), builder.getName(),
                    builder.usesBlock, Collections
                            .<ModuleParameterAST> emptyList(), null,
                    builder.block);
        }

        public static class PrecisBuilder
                extends
                    ModuleBuilderExtension<PrecisBuilder> {

            public PrecisBuilder(Token start, Token stop, Token name) {
                super(start, stop, name);
            }

            @Override
            public PrecisAST build() {
                return new PrecisAST(this);
            }
        }
    }
}