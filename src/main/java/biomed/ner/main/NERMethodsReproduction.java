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
package biomed.ner.main;

import biomed.ner.evaluation.Experiment;
import biomed.ner.evaluation.ExperimentFactory;
import biomed.ner.models.impl.Pipeline;
import biomed.ner.structure.AnnotatedStringDataPoint;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.sentdetect.*;
import org.apache.ctakes.core.resource.FileLocator;
import org.apache.ctakes.core.sentence.EndOfSentenceScannerImpl;
import org.apache.ctakes.core.sentence.SentenceDetectorCtakes;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AggregateBuilder;




/**
 * Main class to run experiments
 * @author Sebastian Hennig
 */
public class NERMethodsReproduction {

    /**
     * runs the experiments
     * @param args the command line arguments
     */
    public static void main(String[] args){
        
   
        
        AggregateBuilder aggregateBuilder;
        AnalysisEngine pipeline;
        try {
            aggregateBuilder = Pipeline.getAggregateBuilder();
            pipeline = aggregateBuilder.createAggregate();
        } catch (Exception ex) {
            Logger.getLogger(NERMethodsReproduction.class.getName()).log(Level.SEVERE, null, ex);
        }
       // CTakesModel ctm = new CTakesModel("./src/biomed/resources/pipers/Default.piper");
            Experiment ncbiMML = ExperimentFactory.getExperiment("CustomNCBI", "MML");
           // ncbiMML.runExperiment();
            
    AnnotatedStringDataPoint abc =  ncbiMML.getModel().annotateText("heppa", "123\tasdf\tAspartylglucosaminuria (AGU) is a recessive autosomally inherited lysosomal storage disorder due to deficiency of the enzyme aspartylglucosaminidase (AGA). The structural gene for this human enzyme (AGA) has been assigned to the region 4q21----qter. We determined the AGA activity in cultured fibroblasts of a girl with a 46, XX, del (4) (q33) karyotype. The results indicate that the girl is a hemizygote for AGA, permitting the assignment of human AGA to the region 4q33----qter.. ");
        System.out.println(abc.getAnnotatedConcepts().toString());
      
    }
    
   
}