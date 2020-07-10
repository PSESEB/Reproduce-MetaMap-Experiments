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

import biomed.ner.datasets.impl.CustomNCBIReader;
import biomed.ner.evaluation.Experiment;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;






/**
 * Main class to run experiments
 * @author Sebastian Hennig
 */
public class ResultCSV {

    private Map<String,Map<String,Double>> experiments;
    
    private Map<String,Map<String,Map<String,Double>>> dataModelVal;
    
    private Map<String,Map <String,Map<String,Double>>> completeDocExperiment;
    
    private SortedSet<String> modelNames;
    
    
    public ResultCSV(){
        this.experiments = new HashMap<>();
        this.completeDocExperiment = new HashMap();
    }
    
    public void addExperimentResults(Experiment exp, Map<String,Double> results){
        
        this.experiments.put(exp.getModel().getModelName()+","+exp.getDataset().getDatasetName(), results);
        
    }
    
    public void addCompleteDocResults(Map <String,Map<String,Double>> results,String experimentName)       
    {
        this.completeDocExperiment.put(experimentName, results);
    }
    
    /**
     * Assumes no Experiment (same data and model) is run twice
     */
    private void parseToOutputFormat(){
        dataModelVal = new HashMap();
        modelNames = new TreeSet();
        
        experiments.forEach((k,v) -> {
            Map<String,Map<String,Double>> models;
            String[] keySplit = k.split(",");
            this.modelNames.add(keySplit[0]);
            if(dataModelVal.get(keySplit[1]) == null){
                models = new HashMap();
            }else{
                models = dataModelVal.get(keySplit[1]);
            }
            models.put(keySplit[0], v);
            dataModelVal.put(keySplit[1], models);
        });
        System.out.println(dataModelVal.toString());
    }
    
    /**
     * Path to write CSV to
     * @param path 
     */
    public void writeResultsToCSV(String path){
        this.parseToOutputFormat();
        File f = new File(path);
        if(f.exists()){
            if(f.isDirectory()){
                File scoresCSV = new File(path+"/scores.csv");
                
                
                String headerline1 = "Datasets,,";
                for(String modelName : this.modelNames){
                    headerline1 += modelName+",,,";
                }
                headerline1 = headerline1.substring(0, headerline1.length()-2);
                String headerline2 = ",";
                headerline2 += new String(new char[this.modelNames.size()]).replace("\0", "Precision,Recall,F1,");
                headerline2 = headerline2.substring(0, headerline2.length()-1);
                
                FileWriter fr = null;
                BufferedWriter br = null;
                try{
                    fr = new FileWriter(scoresCSV);
                    br = new BufferedWriter(fr);
                    br.write(headerline1);
                    br.write(System.lineSeparator());
                    br.write(headerline2);
                    br.write(System.lineSeparator());
                   this.writeHelper(dataModelVal, br, false);
      
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    br.close();
                    fr.close();
                } catch (IOException ex) {
                    Logger.getLogger(ResultCSV.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                File timesCSV = new File(path+"/times.csv");
                String headerlineTime = "Datasets";
                for(String modelName : this.modelNames){
                    headerlineTime += ","+modelName;
                }
              
                
                fr = null;
                br = null;

                try {
                    fr = new FileWriter(timesCSV);
                    br = new BufferedWriter(fr);
                    br.write(headerlineTime);
                    br.write(System.lineSeparator());
                    
                 this.writeHelper(dataModelVal, br, true);
                } catch (IOException ex) {
                    Logger.getLogger(ResultCSV.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                 try {
                    br.close();
                    fr.close();
                } catch (IOException ex) {
                    Logger.getLogger(ResultCSV.class.getName()).log(Level.SEVERE, null, ex);
                }
           
                
                 
                for(Map.Entry<String, Map<String,Map<String,Double>>> pair : this.completeDocExperiment.entrySet() ){
                       File fullDocCSV = new File(path+"/"+pair.getKey()+".csv");
                       Map<String, Map<String,Map<String,Double>>> entityModelValueMap = new HashMap();
                        for(Map.Entry<String,Map<String,Double>> pair2 : pair.getValue().entrySet() ){
                                String[] entityModelName = pair2.getKey().split(";");
                                 Map<String,Map<String,Double>> modelMap;
                                if(entityModelValueMap.get(entityModelName[0]) == null){
                                    modelMap = new HashMap();
                                   
                                }else{
                                    modelMap = entityModelValueMap.get(entityModelName[0]);
                                }
                                modelMap.put(entityModelName[1], pair2.getValue());
                                entityModelValueMap.put(entityModelName[0], modelMap);
                                
                    }
                    
                    fr = null;
                    br = null;

               
                    try {
                        fr = new FileWriter(fullDocCSV);
                        br = new BufferedWriter(fr);
                        br.write(headerline1.replace("Datasets", "Entity"));
                        br.write(System.lineSeparator());
                        br.write(headerline2);
                        br.write(System.lineSeparator());
                        
                   this.writeHelper(entityModelValueMap, br, false);
                        
                    } catch (IOException ex) {
                        Logger.getLogger(ResultCSV.class.getName()).log(Level.SEVERE, null, ex);
                    }
                        
                        try {
                                br.close();
                                fr.close();
                        } catch (IOException ex) {
                            Logger.getLogger(ResultCSV.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                } 
                 
                 
            }else{
                 Logger.getLogger(ResultCSV.class.getName()).log(Level.SEVERE, null, "Path "+path+" jeeds to be a directory and not a file!");
            }
            
        }else{
             Logger.getLogger(ResultCSV.class.getName()).log(Level.SEVERE, null, "Path "+path+" can't be found.");
        }
    }
    
    
    private void writeHelper(Map<String, Map<String,Map<String,Double>>> mapToWrite, BufferedWriter br,boolean time) throws IOException{
          for (Map.Entry<String, Map<String,Map<String,Double>>> entry : mapToWrite.entrySet()) {
                        String  k= entry.getKey();
                        Map<String,Map<String,Double>> v = entry.getValue();
                         String line = k;
                        for(String model : this.modelNames){
                            Map<String,Double> valueMap = v.get(model);
                            if(valueMap == null){
                               line += time ? ",-" :  ",-,-,-"; 
                            }else{
                                if(time){
                                 line += ","+ String.format("%.3f",valueMap.get("t"));
                                }else{
                                    
                                line += ","+ String.format("%.3f",valueMap.get("p"));
                                line += ","+ String.format("%.3f",valueMap.get("r"));
                                line += ","+ String.format("%.3f",valueMap.get("f1"));
                                }
                            }
                            
                           
                        }
                        br.write(line);
                        br.write(System.lineSeparator());
                     }
    }
    
}
