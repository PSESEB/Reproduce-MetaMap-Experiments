/*
 * Copyright (C) 2016-2017 Universidad Nacional de Educación a Distancia (UNED)
 *
 * This program is free software for non-commercial use:
 * you can redistribute it and/or modify it under the terms of the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * (CC BY-NC-SA 4.0) as published by the Creative Commons Corporation,
 * either version 4 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * section 5 of the CC BY-NC-SA 4.0 License for more details.
 *
 * You should have received a copy of the Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International (CC BY-NC-SA 4.0) 
 * license along with this program. If not,
 * see <http://creativecommons.org/licenses/by-nc-sa/4.0/>.
 *
 */
package ner.methods.reproduction;

import biomed.ner.evaluation.Experiment;
import biomed.ner.evaluation.ExperimentFactory;
import biomed.ner.structure.AnnotatedDataPoint;
import biomed.ner.structure.AnnotatedStringDataPoint;
import biomed.ner.structure.AtomStringLabel;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicStampedReference;



/**
 * Main class to run experiments
 * @author Sebastian Hennig
 */
public class NERMethodsReproduction {

    /**
     * runs the experiments
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
       
    Experiment ncbiMML = ExperimentFactory.getExperiment("NCBI", "MML");
    //ncbiMML.runExperiment();

     AtomStringLabel ast = new AtomStringLabel("hallo",1,2);
     AtomStringLabel ast2 = new AtomStringLabel("hallo",1,23);
     AtomStringLabel ast3 = new AtomStringLabel("hallo",1,2);
     Set<AtomStringLabel> kd = new HashSet();
     kd.add(ast3);
        AnnotatedStringDataPoint blub = new AnnotatedStringDataPoint("hallo", ast);
        System.out.println(blub.getAnnotatedConcepts().size());
        System.out.println("EQUALS "+ast.equals(ast2));
        blub.addConcept(ast2);
        blub.getAnnotatedConcepts().removeAll(kd);
        System.out.println(blub.getAnnotatedConcepts().toString());
        
     
     AnnotatedDataPoint bla = ncbiMML.getModel().annotateText("heppa", "123\tGermline BRCA1 alterations in a population-based series of ovarian cancer cases.\tThe objective of this study was to provide more accurate frequency estimates of breast cancer susceptibility gene 1 (BRCA1) germline alterations in the ovarian cancer population. To achieve this, we determined the prevalence of BRCA1 alterations in a population-based series of consecutive ovarian cancer cases. This is the first population-based ovarian cancer study reporting BRCA1 alterations derived from a comprehensive screen of the entire coding region. One hundred and seven ovarian cancer cases were analyzed for BRCA1 alterations using the RNase mismatch cleavage assay followed by direct sequencing. Two truncating mutations, 962del4 and 3600del11, were identified. Both patients had a family history of breast or ovarian cancer. Several novel as well as previously reported uncharacterized variants were also identified, some of which were associated with a family history of cancer. The frequency distribution of common polymorphisms was determined in the 91 Caucasian cancer cases in this series and 24 sister controls using allele-specific amplification. The rare form of the Q356R polymorphism was significantly (P = 0. 03) associated with a family history of ovarian cancer, suggesting that this polymorphism may influence ovarian cancer risk. In summary, our data suggest a role for some uncharacterized variants and rare forms of polymorphisms in determining ovarian cancer risk, and highlight the necessity to screen for missense alterations as well as truncating mutations in this population.");
        System.out.println(bla.getAnnotatedCUIs().toString());
    }
    
   
}
