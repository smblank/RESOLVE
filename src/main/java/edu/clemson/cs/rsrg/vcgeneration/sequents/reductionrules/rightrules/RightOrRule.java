/*
 * RightOrRule.java
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
package edu.clemson.cs.rsrg.vcgeneration.sequents.reductionrules.rightrules;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.InfixExp;
import edu.clemson.cs.rsrg.vcgeneration.sequents.Sequent;
import edu.clemson.cs.rsrg.vcgeneration.sequents.reductionrules.AbstractReductionRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.sequents.reductionrules.ReductionRuleApplication;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class contains the logic for applying the {@code right or} rule.
 * </p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class RightOrRule extends AbstractReductionRuleApplication
        implements
            ReductionRuleApplication {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a new application of the {@code right or} rule.
     * </p>
     *
     * @param originalSequent The original {@link Sequent} that contains the
     *        expression to be reduced.
     * @param originalExp The {@link Exp} to be reduced.
     */
    public RightOrRule(Sequent originalSequent, Exp originalExp) {
        super(originalSequent, originalExp);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method applies the {@code Sequent Reduction Rule}.
     * </p>
     *
     * @return A list of {@link Sequent Sequents} that resulted from applying
     *         the rule.
     */
    @Override
    public final List<Sequent> applyRule() {
        if (myOriginalExp instanceof InfixExp) {
            InfixExp originalExpAsInfixExp = (InfixExp) myOriginalExp;
            List<Exp> newConsequents = new ArrayList<>();
            for (Exp exp : myOriginalSequent.getConcequents()) {
                if (exp.equals(originalExpAsInfixExp)) {
                    // Replace the original "and" expression with its associated
                    // left and right expressions.
                    if (originalExpAsInfixExp.getOperatorAsString()
                            .equals("or")) {
                        newConsequents.add(copyExp(
                                originalExpAsInfixExp.getLeft(),
                                myOriginalExp.getLocationDetailModel()));
                        newConsequents.add(copyExp(
                                originalExpAsInfixExp.getRight(),
                                myOriginalExp.getLocationDetailModel()));
                    }
                    // This must be an error!
                    else {
                        unexpectedExp();
                    }
                }
                // Don't do anything to the other expressions.
                else {
                    newConsequents.add(exp.clone());
                }
            }

            // Construct a new sequent
            Sequent resultingSequent = new Sequent(
                    myOriginalSequent.getLocation(),
                    copyExpList(myOriginalSequent.getAntecedents(), null),
                    newConsequents);
            myResultingSequents.add(resultingSequent);
        }
        // This must be an error!
        else {
            unexpectedExp();
        }

        return myResultingSequents;
    }

    /**
     * <p>
     * This method returns a description associated with the
     * {@code Sequent Reduction Rule}.
     * </p>
     *
     * @return A string.
     */
    @Override
    public final String getRuleDescription() {
        return "Right Or Rule";
    }

}
