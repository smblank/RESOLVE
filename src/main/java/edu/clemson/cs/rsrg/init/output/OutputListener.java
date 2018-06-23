/*
 * OutputListener.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.init.output;

import edu.clemson.cs.rsrg.astoutput.GenerateGraphvizModel;
import edu.clemson.cs.rsrg.init.file.ResolveFile;
import edu.clemson.cs.rsrg.prover.output.Metrics;
import edu.clemson.cs.rsrg.prover.output.PerVCProverModel;
import edu.clemson.cs.rsrg.translation.targets.CTranslator;
import edu.clemson.cs.rsrg.translation.targets.JavaTranslator;
import edu.clemson.cs.rsrg.vcgeneration.VCGenerator;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import java.util.List;

/**
 * <p>A listener that contains methods for retrieving compilation
 * results from the compiler.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public interface OutputListener {

    /**
     * <p>This method outputs the provided {@code Graphviz} model generated
     * from the {@link GenerateGraphvizModel}.</p>
     *
     * @param outputFileName A name for the output file.
     * @param graphvizModel The inner {@code AST} represented in a {@code GraphViz}
     *                      file format.
     */
    void astGraphvizModelResult(String outputFileName, String graphvizModel);

    /**
     * <p>This method outputs the provided the {@code C} translation results
     * from the {@link CTranslator}.</p>
     *
     * @param inputFileName Name of the {@link ResolveFile} we are generating {@code C} translations.
     * @param outputFileName A name for the output file.
     * @param cTranslation The translated {@code C} source code.
     */
    void cTranslationResult(String inputFileName, String outputFileName,
            String cTranslation);

    /**
     * <p>This method outputs the provided the {@code Java} translation results
     * from the {@link JavaTranslator}.</p>
     *
     * @param inputFileName Name of the {@link ResolveFile} we are generating {@code Java} translations.
     * @param outputFileName A name for the output file.
     * @param javaTranslation The translated {@code Java} source code.
     */
    void javaTranslationResult(String inputFileName, String outputFileName,
            String javaTranslation);

    /**
     * <p>This method outputs the provided results
     * from the {@code CCProver}.</p>
     *
     * @param inputFileName Name of the {@link ResolveFile} we are generating proofs.
     * @param outputFileName A name for the output file.
     */
    void proverResult(String inputFileName, String outputFileName);

    /**
     * <p>This method outputs the provided {@link AssertiveCodeBlock AssertiveCodeBlocks}
     * and/or raw output result from the {@link VCGenerator}.</p>
     *
     * @param inputFileName Name of the {@link ResolveFile} we are generating VCs for.
     * @param outputFileName A name for the output file.
     * @param blocks A list of final {@link AssertiveCodeBlock AssertiveCodeBlocks}.
     * @param verboseOutput The verbose output string generated by the {@link VCGenerator}.
     */
    void vcGeneratorResult(String inputFileName, String outputFileName,
            List<AssertiveCodeBlock> blocks, String verboseOutput);

    /**
     * <p>This method outputs the prover results for a given {@code VC}.</p>
     *
     * @param proved {@code true} if the {@code VC} was proved,
     *               {@code false} otherwise.
     * @param finalModel The prover representation for a {@code VC}.
     * @param m The prover generated metrics.
     */
    void vcResult(boolean proved, PerVCProverModel finalModel, Metrics m);

}