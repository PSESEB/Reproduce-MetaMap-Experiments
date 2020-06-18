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
import biomed.ner.models.impl.MetaMapModel;
import biomed.ner.structure.AnnotatedStringDataPoint;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;


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
        
//            Experiment ncbiMML = ExperimentFactory.getExperiment("CustomNCBI", "MML");
           // ncbiMML.runExperiment();
           
          // Experiment ncbiCTakes = ExperimentFactory.getExperiment("CustomNCBI", "cTakes");
          // ncbiCTakes.runExperiment();
          
           Experiment ncbiMM = ExperimentFactory.getExperiment("CustomNCBI", "MM");
           ncbiMM.runExperiment();
            
  
      
    }
    
   
}
