/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ner.methods.reproduction;

import biomed.ner.evaluation.Experiment;
import biomed.ner.evaluation.ExperimentFactory;



/**
 *
 * @author weenzeal
 */
public class NERMethodsReproduction {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
       
    Experiment ncbiMML = ExperimentFactory.getExperiment("NCBI", "MML");
    ncbiMML.runExperiment();

       
    }
    
   
}
