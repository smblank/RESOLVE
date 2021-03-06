/*
 * AntecedentMinimizer.java
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
package edu.clemson.cs.r2jt.rewriteprover.automators;

import edu.clemson.cs.r2jt.rewriteprover.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.rewriteprover.model.Theorem;
import edu.clemson.cs.r2jt.rewriteprover.transformations.ExpandAntecedentBySubstitution;
import edu.clemson.cs.r2jt.rewriteprover.transformations.NoOpLabel;
import edu.clemson.cs.r2jt.rewriteprover.transformations.SubstituteInPlaceInAntecedent;
import edu.clemson.cs.r2jt.rewriteprover.transformations.Transformation;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author hamptos
 */
public class AntecedentMinimizer implements Automator {

    private static final ApplyAll DUMMY_APPLIER =
            new ApplyAll(new NoOpLabel(null, "Dummy"));

    public List<ExpandAntecedentBySubstitution> myReducingTranformations =
            new LinkedList<ExpandAntecedentBySubstitution>();

    private Iterator<ExpandAntecedentBySubstitution> myCurrentRound;
    private ApplyAll myCurrentApplier;
    private boolean myProductiveRoundFlag = false;

    public AntecedentMinimizer(ImmutableList<Theorem> theoremLibrary) {
        for (Theorem theorem : theoremLibrary) {
            for (Transformation t : theorem.getTransformations()) {
                if (t instanceof ExpandAntecedentBySubstitution
                        && !t.introducesQuantifiedVariables()
                        && t.functionApplicationCountDelta() < 0) {
                    myReducingTranformations
                            .add((ExpandAntecedentBySubstitution) t);
                }
            }
        }

        myCurrentRound = myReducingTranformations.iterator();
        myCurrentApplier = DUMMY_APPLIER; // This will never be applied
    }

    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {
        if (myCurrentRound.hasNext()) {
            myProductiveRoundFlag = myProductiveRoundFlag
                    || (myCurrentApplier.getApplicationCount() > 0);

            ExpandAntecedentBySubstitution expander = myCurrentRound.next();
            SubstituteInPlaceInAntecedent substituter =
                    new SubstituteInPlaceInAntecedent(expander.getTheorem(),
                            expander.getMatchPattern(),
                            expander.getTransformationTemplate());

            myCurrentApplier = new ApplyAll(substituter);
            stack.push(myCurrentApplier);
        }
        else {
            if (myProductiveRoundFlag) {
                myCurrentRound = myReducingTranformations.iterator();
                myProductiveRoundFlag = false;
                myCurrentApplier = DUMMY_APPLIER; // This will never be applied
            }
            else {
                stack.pop();
            }
        }
    }

}
