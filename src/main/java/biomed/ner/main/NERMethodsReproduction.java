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

import biomed.ner.datasets.iDatasetReader;
import biomed.ner.datasets.impl.LHCReader;
import biomed.ner.evaluation.Experiment;
import biomed.ner.evaluation.ExperimentCompleteDoc;
import biomed.ner.evaluation.ExperimentFactory;
import java.util.HashMap;
import java.util.Map;




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
        
      
       
        ResultCSV rcsv = new ResultCSV();
       
        //Share
        Experiment shareMML = ExperimentFactory.getExperiment("share", "MML");
        rcsv.addExperimentResults(shareMML, shareMML.runExperiment());
        
        Experiment shareMM = ExperimentFactory.getExperiment("share", "MM");
        rcsv.addExperimentResults(shareMM, shareMM.runExperiment());
        
        Experiment shareCTakes = ExperimentFactory.getExperiment("share", "ctakes");
        rcsv.addExperimentResults(shareCTakes, shareCTakes.runExperiment());
        
        //NCBI
         Experiment ncbiMML = ExperimentFactory.getExperiment("CustomNCBI", "MML");
        rcsv.addExperimentResults(ncbiMML, ncbiMML.runExperiment());
        
        Experiment ncbiMM = ExperimentFactory.getExperiment("CustomNCBI", "MM");
        rcsv.addExperimentResults(ncbiMM, ncbiMM.runExperiment());
        
        Experiment ncbiCTakes = ExperimentFactory.getExperiment("CustomNCBI", "ctakes");
        rcsv.addExperimentResults(ncbiCTakes, ncbiCTakes.runExperiment());
        
        //i2b2 2010
          Experiment i2b22010MML = ExperimentFactory.getExperiment("i2b22010", "MML");
        rcsv.addExperimentResults(i2b22010MML, i2b22010MML.runExperiment());
        
        Experiment i2b22010MM = ExperimentFactory.getExperiment("i2b22010", "MM");
        rcsv.addExperimentResults(i2b22010MM, i2b22010MM.runExperiment());
        
        Experiment i2b22010CTakes = ExperimentFactory.getExperiment("i2b22010", "ctakes");
        rcsv.addExperimentResults(i2b22010CTakes, i2b22010CTakes.runExperiment());
        
        //LHC bio
        
          Experiment lhcbioMML = ExperimentFactory.getExperiment("lhcbio", "MML");
        rcsv.addExperimentResults(lhcbioMML, lhcbioMML.runExperiment());
        
        Experiment lhcbioMM = ExperimentFactory.getExperiment("lhcbio", "MM");
        rcsv.addExperimentResults(lhcbioMM, lhcbioMM.runExperiment());
        
        Experiment lhcbioCTakes = ExperimentFactory.getExperiment("lhcbio", "ctakes");
        rcsv.addExperimentResults(lhcbioCTakes, lhcbioCTakes.runExperiment());
        
        //LHC clin
          Experiment lhcclinMML = ExperimentFactory.getExperiment("lhcclin", "MML");
        rcsv.addExperimentResults(lhcclinMML, lhcclinMML.runExperiment());
        
        Experiment lhcclinMM = ExperimentFactory.getExperiment("lhcclin", "MM");
        rcsv.addExperimentResults(lhcclinMM, lhcclinMM.runExperiment());
        
        Experiment lhcclinCTakes = ExperimentFactory.getExperiment("lhcclin", "ctakes");
        rcsv.addExperimentResults(lhcclinCTakes, lhcclinCTakes.runExperiment());
     
          
          
        
          ExperimentCompleteDoc expComp = ExperimentFactory.getExperimentCompleteDoc("ctakes");
          
          rcsv.addCompleteDocResults(expComp.runExperiment(false),"SingleCUIs");
          rcsv.addCompleteDocResults(expComp.runExperiment(true),"MultipleCUIs");
          
           ExperimentCompleteDoc expCompMM = ExperimentFactory.getExperimentCompleteDoc("MM");
          
          rcsv.addCompleteDocResults(expCompMM.runExperiment(false),"SingleCUIs");
          rcsv.addCompleteDocResults(expCompMM.runExperiment(true),"MultipleCUIs");
          
           ExperimentCompleteDoc expCompMML = ExperimentFactory.getExperimentCompleteDoc("MML");
          
          rcsv.addCompleteDocResults(expCompMML.runExperiment(false),"SingleCUIs");
          rcsv.addCompleteDocResults(expCompMML.runExperiment(true),"MultipleCUIs");
            
          rcsv.writeResultsToCSV("/home/weenzeal/Documents/MasterArbeit");
            
        
      
    }
    
   
}
