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
       
        Experiment shareMML = ExperimentFactory.getExperiment("share", "MM");
        Experiment shareMM = ExperimentFactory.getExperiment("share", "MML");
        //shareMML.runExperiment();
        
          Map<String,Double> test = new HashMap();
        test.put("p", 0.4);
        test.put ("r",6.5);
        test.put("f1", 100.0);
        test.put("t", 40.4);
        
        rcsv.addExperimentResults(shareMML, test);
        Map<String,Double> test2 = new HashMap();
         test2.put("p", 0.1);
        test2.put ("r",1.225);
        test2.put("f1", 10.0);
        test2.put("t", 40.4);
         rcsv.addExperimentResults(shareMM, test2);
        
        
        
        //ExperimentCompleteDoc compMML = ExperimentFactory.getExperimentCompleteDoc("MML");
        //compMML.runExperiment(false);
       
        
        
            Experiment i2b2MML = ExperimentFactory.getExperiment("i2b2 2010", "MML");
             Experiment i2b2MM = ExperimentFactory.getExperiment("i2b2 2010", "MM");
             Experiment i2b2ct = ExperimentFactory.getExperiment("i2b2 2010", "ctakes");
            //i2b2MML.runExperiment();
            Map<String,Double> test3 = new HashMap();
           test3.put("p", 9.332);
        test3.put ("r",0.1234);
        test3.put("f1", 0.011);
        test3.put("t", 40.4);
          rcsv.addExperimentResults(i2b2MML, test3);
          
          Map<String,Double> test4 = new HashMap();
           test4.put("p", 1.14);
        test4.put ("r",0.999);
        test4.put("f1", 999.0);
        test4.put("t", 40.4);
          rcsv.addExperimentResults(i2b2MM, test4);
          rcsv.addExperimentResults(i2b2ct, test4);
          
          
        
          ExperimentCompleteDoc expComp = ExperimentFactory.getExperimentCompleteDoc("ctakes");
          
          rcsv.addCompleteDocResults(expComp.runExperiment(false),"SingleCUIs");
            
          rcsv.writeResultsToCSV("/home/weenzeal/Documents/MasterArbeit");
           // Experiment ncbiMML = ExperimentFactory.getExperiment("CustomNCBI", "MML");
           // ncbiMML.runExperiment();
           
       ////  ncbiCTakes.runExperiment();
          
         // Experiment ncbiMM = ExperimentFactory.getExperiment("CustomNCBI", "MM");
         // ncbiMM.runExperiment();
            
  
      
    }
    
   
}
