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
package biomed.ner.evaluation;

import biomed.ner.datasets.iDatasetReader;
import biomed.ner.models.iModel;
import biomed.ner.structure.AnnotatedStringDataPoint;
import biomed.ner.structure.AtomStringLabel;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * Creates an experiment with a specific dataset and model
 * @author Sebastian Hennig
 */
public class Experiment {
    
    /**
     * Dataset for Experiment
     */
    private iDatasetReader dataset;
    /**
     * Model for Experiment
     */
    private iModel model;

    public iDatasetReader getDataset() {
        return dataset;
    }

    public void setDataset(iDatasetReader dataset) {
        this.dataset = dataset;
    }

    public iModel getModel() {
        return model;
    }

    public void setModel(iModel model) {
        this.model = model;
    }
    
    /**
     * Runs and evaluates Experiment
     */
    public void runExperiment(){
        //check if dataset and model is set
        //Necessary to run experiment.
        assert this.dataset != null : "No datset loaded";
        assert this.model != null : "No model loaded";
        //Create counts for relevant, retrieved and contained in both
        //Later used to calculate Precision and Recall
        int totalNumRelevant = 0;
        int totalNumRetrieved = 0;
        int totalNumIntersection = 0;
        
        //Iterate over Dataset
        for (Map.Entry<String, String> entry : this.dataset.getInputData().entrySet()) {
            Logger.getLogger(Experiment.class.getName()).log(Level.INFO,"Processing Datapoint with ID "+entry.getKey());
            //get id of document
            String key = entry.getKey();
            //Get document
            String value = entry.getValue();
            //Compare result of Meta Map Lite on document to annotated Dataset
            List<Set<AtomStringLabel>> comparisons = this.compare(this.dataset.getLabelData().getDataPoint(key),this.model.annotateText(key, value));
            //Update counts
            totalNumRelevant += this.getNumRelevant(comparisons);
            totalNumRetrieved += this.getNumRetrieved(comparisons);
            totalNumIntersection += this.getNumIntersection(comparisons);
//            System.out.println("rel "+totalNumRelevant+" ret "+totalNumRetrieved+" int "+totalNumIntersection);
        }
        //Calculate Measures
        double precision = this.calcPrecision(totalNumIntersection, totalNumRetrieved);
        double recall = this.calcRecall(totalNumIntersection, totalNumRelevant);
        double f1 = this.calcFMeasure(precision, recall);
        System.out.println("Precision: "+precision);
        System.out.println("Recall: "+recall);
        System.out.println("F1: "+f1);
  
    }
    
    /**
     * Get number of relevant labels.
     * Number of labels in annotated ground truths
     * @param comparisons 0: set of labels only in ground truths 1: set of labels only in model output 2: set of labels that occur in both 
     * @return #only in ground truth + # in both
     */
    private int getNumRelevant(List<Set<AtomStringLabel>> comparisons){
        return comparisons.get(0).size() + comparisons.get(2).size();
    }
    
    /**
     * Get number of retrieved labels.
     * Number of labels that were produced by the model
     * @param comparisons 0: set of labels only in ground truths 1: set of labels only in model output 2: set of labels that occur in both
     * @return #only in model output + # in both
     */
    private int getNumRetrieved(List<Set<AtomStringLabel>> comparisons){
        return comparisons.get(1).size() + comparisons.get(2).size();
    }
    
     /**
     * Get number of intersected labels.
     * Number of labels that are present in the ground truth aswell as in the model output
     * @param comparisons 0: set of labels only in ground truths 1: set of labels only in model output 2: set of labels that occur in both
     * @return # in both
     */
    private int getNumIntersection(List<Set<AtomStringLabel>> comparisons){
        return comparisons.get(2).size();
    }
    
    /**
     * Calculates Precision:
     *  |(relevant ∩ retrieved)| / |retrieved|
     * @param numIntersection |relevant ∩ retrieved| 
     * @param numRetrieved |retrieved|
     * @return precision
     */
    private double calcPrecision(double numIntersection, double numRetrieved){
        if(numRetrieved > 0){
            return numIntersection/numRetrieved;
        }else{
            return 0;
        }
    }
    
       /**
     * Calculates Recall:
     *  |(relevant ∩ retrieved)| / |relevant|
     * @param numIntersection |relevant ∩ retrieved| 
     * @param numRetrieved |relevant|
     * @return recall
     */
    private double calcRecall(double numIntersection, double numRelevant){
        if(numRelevant > 0){
            return numIntersection/numRelevant;
        }else{
            return 0;
        }
    }
    
    /**
     * Calculates FMeasure:
     *  2 * ((precision * recall) / (precision + recall))
     * @param precision 
     * @param recall 
     * @return fMeasure
     */
    private double calcFMeasure(double precision, double recall){
        if(precision+recall > 0){
            return 2 * ((precision * recall) / (precision + recall));
        }else{
            return 0;
        }
    }
    
    /**
     * Compares two sets of CUIs to each other
     * @param labels
     * @param modelOut
     * @return 3 sets namely: {cuis only in labelset}, {cuis only in outputset}, {cuis occuring in both sets}
     */
    private List<Set<AtomStringLabel>> compare(AnnotatedStringDataPoint labels, AnnotatedStringDataPoint modelOut){
        
        //Convert list to set to remove duplicates
        Set<AtomStringLabel> labelSet = labels.getAnnotatedConcepts();
        Set<AtomStringLabel> outSet = modelOut.getAnnotatedConcepts();
        
    
        
        //Need copy since remove operation is inplace and does not return modified version
        Set<AtomStringLabel> labelCopy = new HashSet(labelSet);
        
        //CreateIntersection
        Set<AtomStringLabel> intersection = new HashSet(labelSet);
        intersection.retainAll(outSet);
        
        
        //CreateDiff Labels/Out 
        //Filter for elements that only exist in labelSet
        labelSet.removeAll(outSet);
        
        //CreateDiff Out/Labels
        //Filter for elements that only exist in outSet
        outSet.removeAll(labelCopy);
        
        //Save 3 comparison sets in a list
        List<Set<AtomStringLabel>> setComparisons = new ArrayList();
        setComparisons.add(labelSet);
        setComparisons.add(outSet);
        setComparisons.add(intersection);
        
        return setComparisons;
        
    }
        
        
    
    
   
        
}
