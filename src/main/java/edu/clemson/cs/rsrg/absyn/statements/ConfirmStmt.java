/**
 * ConfirmStmt.java
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
package edu.clemson.cs.rsrg.absyn.statements;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.absyn.Statement;
import edu.clemson.cs.rsrg.parsing.data.Location;

/**
 * <p>This is the class for all the confirm statements
 * that the compiler builds from the ANTLR4 AST tree or
 * generated during the VC Generation step.</p>
 *
 * @version 2.0
 */
public class ConfirmStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The confirm assertion expression</p> */
    private final Exp myAssertion;

    /** <p>This flag indicates if this confirm can be simplified or not</p> */
    private boolean mySimplify;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a confirm statement.</p>
     *
     * @param l A {@link Location} representation object.
     * @param assertion A {@link Exp} representing the confirm statement's
     *                  assertion expression.
     * @param simplify A flag to indicate whether or not this is a
     *                 confirm statement that can be simplified. In general,
     *                 the only confirm statements that can be simplified are
     *                 the special ones generated by the VC generator.
     */
    public ConfirmStmt(Location l, Exp assertion, boolean simplify) {
        super(l);
        myAssertion = assertion;
        mySimplify = simplify;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method creates a special indented
     * text version of the class as a string.</p>
     *
     * @param indentSize The base indentation to the first line
     *                   of the text.
     * @param innerIndentSize The additional indentation increment
     *                        for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    @Override
    public String asString(int indentSize, int innerIndentSize) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append("ConfirmStmt\n");
        sb.append(myAssertion.asString(indentSize + innerIndentSize,
                innerIndentSize));

        return sb.toString();
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link ConfirmStmt} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof ConfirmStmt) {
            ConfirmStmt eAsConfirmStmt = (ConfirmStmt) o;
            result = myLoc.equals(eAsConfirmStmt.myLoc);

            if (result) {
                result = myAssertion.equals(eAsConfirmStmt.myAssertion);
                result &= (mySimplify == eAsConfirmStmt.mySimplify);
            }
        }

        return result;
    }

    /**
     * <p>This method returns a deep copy of the confirm assertion expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public final Exp getAssertion() {
        return myAssertion.clone();
    }

    /**
     * <p>This method returns whether this confirm statement can be simplified.</p>
     *
     * @return True if it is a confirm statement we can simplify, false otherwise.
     */
    public final boolean getSimplify() {
        return mySimplify;
    }

    /**
     * <p>Returns the statement in string format.</p>
     *
     * @return Statement as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Confirm " + myAssertion.toString());

        return sb.toString();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>Implemented by this concrete subclass of {@link Statement} to
     * manufacture a copy of themselves.</p>
     *
     * @return A new {@link Statement} that is a deep copy of the original.
     */
    @Override
    protected Statement copy() {
        return new ConfirmStmt(new Location(myLoc), getAssertion(),
                getSimplify());
    }

}