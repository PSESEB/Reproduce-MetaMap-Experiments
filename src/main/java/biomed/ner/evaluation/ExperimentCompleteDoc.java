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

import biomed.ner.datasets.impl.I2B22008Reader;
import biomed.ner.models.iModel;
import biomed.ner.structure.AnnotatedDataPoint;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * Creates an experiment according to rategui et al.
 * @author Sebastian Hennig
 */
public class ExperimentCompleteDoc {
    
    /**
     * Dataset for Experiment
     */
    private I2B22008Reader dataset;
    /**
     * Model for Experiment
     */
    private iModel model;

    public I2B22008Reader getDataset() {
        return dataset;
    }

    public void setDataset(I2B22008Reader dataset) {
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
     * @param singleCUI
     * @return 
     */
    public Map <String,Map<String,Double>> runExperiment(boolean singleCUI){
        //check if dataset and model is set
        //Necessary to run experiment.
        assert this.dataset != null : "No datset loaded";
        assert this.model != null : "No model loaded";
        //Create counts for relevant, retrieved and contained in both
       
        
        Map<String,Set<String>> outs = new HashMap();
        
        //Iterate over Dataset
        for (Map.Entry<String, String> entry : this.dataset.getInputData().entrySet()) {
            Logger.getLogger(ExperimentCompleteDoc.class.getName()).log(Level.INFO,"Processing Datapoint with ID "+entry.getKey());
            //get id of document
            String key = entry.getKey();
            //Get document
            String value = entry.getValue();
            //Compare result of Meta Map Lite on document to annotated Dataset
            AnnotatedDataPoint adp = this.model.annotateTextCUI(key, value);
            
            if(singleCUI){
                for(Map.Entry<String, String> entityToCUI : this.dataset.getEntityToCUI().entrySet()) {
                if(adp.getAnnotatedCUIs().contains(entityToCUI.getValue())){
                      if(outs.get(entityToCUI.getKey()) == null){
                              Set<String> docs = new HashSet();
                              docs.add(key);
                              outs.put(entityToCUI.getKey(), docs);
                          }else{
                              Set<String> docsAlreadyExisting = outs.get(entityToCUI.getKey());
                              docsAlreadyExisting.add(key);
                              outs.put(entityToCUI.getKey(), docsAlreadyExisting);
                                     
                          }
                }
            }
                
            }else{
                 for(Map.Entry<String, List<String>> entityToCUI : this.dataset.getEntityToCUIs().entrySet()) {
                     Set<String> truth = new HashSet(entityToCUI.getValue());
                     Set<String> model = new HashSet(adp.getAnnotatedCUIs());
                     truth.retainAll(model);
                     if(truth.size() > 0){
                         if(outs.get(entityToCUI.getKey()) == null){
                              Set<String> docs = new HashSet();
                              docs.add(key);
                              outs.put(entityToCUI.getKey(), docs);
                          }else{
                              Set<String> docsAlreadyExisting = outs.get(entityToCUI.getKey());
                              docsAlreadyExisting.add(key);
                              outs.put(entityToCUI.getKey(), docsAlreadyExisting);
                                     
                          }
                     }
                 }
            }
           
           
        }
        Map <String,Map<String,Double>> entityResults = new HashMap();
         for(Map.Entry<String, List<String>> labelEntry : this.dataset.getLabelData().entrySet() ){
                
                System.out.println("Results for Entity: "+labelEntry.getKey());
                List<Set<String>> cmpResults = this.compare(new HashSet(labelEntry.getValue()), outs.get(labelEntry.getKey()));
                 //Later used to calculate Precision and Recall
                int totalNumRelevant = this.getNumRelevant(cmpResults);
                int totalNumRetrieved = this.getNumRetrieved(cmpResults);
                int totalNumIntersection = this.getNumIntersection(cmpResults);
                //Calculate Measures
                double precision = this.calcPrecision(totalNumIntersection, totalNumRetrieved);
                double recall = this.calcRecall(totalNumIntersection, totalNumRelevant);
                double f1 = this.calcFMeasure(precision, recall);
                System.out.println("Precision: "+precision);
                System.out.println("Recall: "+recall);
                System.out.println("F1: "+f1);
                
                    //Give back results to caller
                Map<String,Double> results = new HashMap();
                results.put("p", precision);
                results.put ("r",recall);
                results.put("f1", f1);
                entityResults.put(labelEntry.getKey()+";"+this.getModel().getModelName(), results);
            }
         
  
       return entityResults;
        
  
    }
    
    /**
     * Get number of relevant labels.
     * Number of labels in annotated ground truths
     * @param comparisons 0: set of labels only in ground truths 1: set of labels only in model output 2: set of labels that occur in both 
     * @return #only in ground truth + # in both
     */
    private int getNumRelevant(List<Set<String>> comparisons){
        return comparisons.get(0).size() + comparisons.get(2).size();
    }
    
    /**
     * Get number of retrieved labels.
     * Number of labels that were produced by the model
     * @param comparisons 0: set of labels only in ground truths 1: set of labels only in model output 2: set of labels that occur in both
     * @return #only in model output + # in both
     */
    private int getNumRetrieved(List<Set<String>> comparisons){
        return comparisons.get(1).size() + comparisons.get(2).size();
    }
    
     /**
     * Get number of intersected labels.
     * Number of labels that are present in the ground truth aswell as in the model output
     * @param comparisons 0: set of labels only in ground truths 1: set of labels only in model output 2: set of labels that occur in both
     * @return # in both
     */
    private int getNumIntersection(List<Set<String>> comparisons){
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
    private List<Set<String>> compare(Set<String> labels, Set<String> modelOut){
        
        if(modelOut == null){
            modelOut = new HashSet();
        }
       
        //Need copy since remove operation is inplace and does not return modified version
        Set<String> labelCopy = new HashSet(labels);
        
        //CreateIntersection
        Set<String> intersection = new HashSet(labels);
        intersection.retainAll(modelOut);
        
        
        //CreateDiff Labels/Out 
        //Filter for elements that only exist in labelSet
        labels.removeAll(modelOut);
        
        //CreateDiff Out/Labels
        //Filter for elements that only exist in outSet
        modelOut.removeAll(labelCopy);
        
        //Save 3 comparison sets in a list
        List<Set<String>> setComparisons = new ArrayList();
        setComparisons.add(labels);
        setComparisons.add(modelOut);
        setComparisons.add(intersection);
        
        return setComparisons;
        
    }
        
        
    
    
   
        
}
