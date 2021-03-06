/*
 * IterativeExp.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;

public class IterativeExp extends Exp {

    // ===========================================================
    // Constants
    // ===========================================================

    public static final int SUM = 1;
    public static final int PRODUCT = 2;
    public static final int CONCATENATION = 3;
    public static final int UNION = 4;
    public static final int INTERSECTION = 5;

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The operator member. */
    private int operator;

    /** The var member. */
    private MathVarDec var;

    /** The where member. */
    private Exp where;

    /** The body member. */
    private Exp body;

    // ===========================================================
    // Constructors
    // ===========================================================

    public IterativeExp() {};

    public IterativeExp(Location location, int operator, MathVarDec var,
            Exp where, Exp body) {
        this.location = location;
        this.operator = operator;
        this.var = var;
        this.where = where;
        this.body = body;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the location variable. */
    public Location getLocation() {
        return location;
    }

    /** Returns the value of the operator variable. */
    public int getOperator() {
        return operator;
    }

    /** Returns the value of the var variable. */
    public MathVarDec getVar() {
        return var;
    }

    /** Returns the value of the where variable. */
    public Exp getWhere() {
        return where;
    }

    /** Returns the value of the body variable. */
    public Exp getBody() {
        return body;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the operator variable to the specified value. */
    public void setOperator(int operator) {
        this.operator = operator;
    }

    /** Sets the var variable to the specified value. */
    public void setVar(MathVarDec var) {
        this.var = var;
    }

    /** Sets the where variable to the specified value. */
    public void setWhere(Exp where) {
        this.where = where;
    }

    /** Sets the body variable to the specified value. */
    public void setBody(Exp body) {
        this.body = body;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new IterativeExp(location, operator, var,
                substitute(where, substitutions),
                substitute(body, substitutions));
    }

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitIterativeExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("IterativeExp\n");

        printSpace(indent + increment, sb);
        sb.append(printConstant(operator) + "\n");

        if (var != null) {
            sb.append(var.asString(indent + increment, increment));
        }

        if (where != null) {
            sb.append(where.asString(indent + increment, increment));
        }

        if (body != null) {
            sb.append(body.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /**
     * Returns true if the variable is found in any sub expression of this one.
     **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        Boolean found = false;
        if (where != null) {
            found = where.containsVar(varName, IsOldExp);
        }
        if (!found && body != null) {
            found = body.containsVar(varName, IsOldExp);
        }
        return found;
    }

    private String printConstant(int k) {
        StringBuffer sb = new StringBuffer();
        switch (k) {
        case 1:
            sb.append("SUM");
            break;
        case 2:
            sb.append("PRODUCT");
            break;
        case 3:
            sb.append("CONCATENATION");
            break;
        case 4:
            sb.append("UNION");
            break;
        case 5:
            sb.append("INTERSECTION");
            break;
        default:
            sb.append(k);
        }
        return sb.toString();
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        list.add(where);
        list.add(body);
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        switch (index) {
        case 0:
            where = e;
            break;
        case 1:
            body = e;
            break;
        }
    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof IterativeExp)) {
            return false;
        }
        if (operator != ((IterativeExp) e2).getOperator()) {
            return false;
        }
        return true;
    }

    public String toString(int indent) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        if (operator == SUM)
            sb.append("Sum ");
        else if (operator == PRODUCT)
            sb.append("Product ");
        else if (operator == CONCATENATION)
            sb.append("Concatenation ");
        else if (operator == UNION)
            sb.append("Union ");
        else
            sb.append("Intersection ");
        sb.append(var.toString(0) + " where ");

        sb.append(where.toString(0) + ", ");
        sb.append(body.toString(0));

        return sb.toString();

    }

    public void prettyPrint() {
        if (operator == SUM)
            System.out.print("Sum ");
        else if (operator == PRODUCT)
            System.out.print("Product ");
        else if (operator == CONCATENATION)
            System.out.print("Concatenation ");
        else if (operator == UNION)
            System.out.print("Union ");
        else
            System.out.print("Intersection ");
        var.prettyPrint();
        System.out.print(", ");
        where.prettyPrint();
        System.out.print(", ");
        body.prettyPrint();
    }

    public Exp copy() {
        int newOperator = operator;
        MathVarDec newVar = var.copy();
        Exp newWhere = null;
        if (where != null)
            newWhere = Exp.copy(where);
        Exp newBody = Exp.copy(body);
        return new IterativeExp(null, newOperator, newVar, newWhere, newBody);
    }

    public Exp clone() {
        int newOperator = operator;
        MathVarDec newVar = (MathVarDec) var.copy();
        Exp newWhere = null;
        if (where != null)
            newWhere = Exp.copy(where);
        Exp newBody = Exp.copy(body);
        return new IterativeExp(null, newOperator, newVar, newWhere, newBody);
    }

    public Exp replace(Exp old, Exp replacement) {
        if (!(old instanceof QuantExp)) {
            if (where != null) {
                Exp whr = Exp.replace(where, old, replacement);
                if (whr != null)
                    this.setWhere(whr);
            }
            if (body != null) {
                Exp bdy = Exp.replace(body, old, replacement);
                if (bdy != null)
                    this.setBody(bdy);
                // Not used anywhere below. - YS
                // String str = bdy.toString(0, 0);
            }
            return this;
        }
        else
            return this;
    }

}
