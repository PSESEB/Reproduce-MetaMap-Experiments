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
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Aggregate Results and write them to CSV
 *
 * @author Sebastian Hennig
 */
public class ResultCSV {

    /**
     * Save Results of meta map lite experiments here
     */
    private Map<String, Map<String, Double>> experiments;

    /**
     *
     */
    private Map<String, Map<String, Map<String, Double>>> dataModelVal;
    /**
     * Save Results of cTakes Meta Map comparison here
     */
    private Map<String, Map<String, Map<String, Double>>> completeDocExperiment;

    /**
     * List of all models used in various experiments
     */
    private SortedSet<String> modelNames;

    /**
     * Constructor to initialize data structures
     */
    public ResultCSV() {
        this.experiments = new HashMap<>();
        this.completeDocExperiment = new HashMap();
    }

    /**
     * Add the results of a specific experiment
     *
     * @param exp in form "model name,dataset name"
     * @param results results returned by the run experiment method
     */
    public void addExperimentResults(Experiment exp, Map<String, Double> results) {

        this.experiments.put(exp.getModel().getModelName() + "," + exp.getDataset().getDatasetName(), results);

    }

    /**
     * Add results for completeDoc Experiment
     *
     * @param results as returned by the runExperiment method
     * @param experimentName
     */
    public void addCompleteDocResults(Map<String, Map<String, Double>> results, String experimentName) {
        this.completeDocExperiment.put(experimentName, results);
    }

    /**
     * Assumes no Experiment (same data and model) is run twice Change
     * datastructure of experiments to table structure so they can be written to
     * CSV easily
     */
    private void parseToOutputFormat() {
        dataModelVal = new HashMap();
        modelNames = new TreeSet();
        //iterate over experiments
        experiments.forEach((k, v) -> {
            Map<String, Map<String, Double>> models;
            //Split into model name and datset name
            String[] keySplit = k.split(",");
            //add model name if not already exisitng
            this.modelNames.add(keySplit[0]);
            //accumulate all values for a specific model
            if (dataModelVal.get(keySplit[1]) == null) {
                models = new HashMap();
            } else {
                models = dataModelVal.get(keySplit[1]);
            }
            //write changes back
            models.put(keySplit[0], v);
            dataModelVal.put(keySplit[1], models);
        });
        System.out.println(dataModelVal.toString());
    }

    /**
     * Writes aggregated Results to various CSV files
     *
     * @param path Path to write CSV to
     */
    public void writeResultsToCSV(String path) {
        //parse the data
        this.parseToOutputFormat();
       
        File f = new File(path);
        //check if path is proper directory
        if (f.exists()) {
            if (f.isDirectory()) {
                //create first csv result file
                File scoresCSV = new File(path + "/scores.csv");

                //intialize headerline
                String headerline1 = "Datasets,,";
                //build headerline
                for (String modelName : this.modelNames) {
                    headerline1 += modelName + ",,,";
                }
                //remove last two commas
                headerline1 = headerline1.substring(0, headerline1.length() - 2);
                //intialize second header line
                String headerline2 = ",";
                //build second headerline
                headerline2 += new String(new char[this.modelNames.size()]).replace("\0", "Precision,Recall,F1,");
                headerline2 = headerline2.substring(0, headerline2.length() - 1);

                FileWriter fr = null;
                BufferedWriter br = null;
                try {
                    //initialize file + writer
                    fr = new FileWriter(scoresCSV);
                    br = new BufferedWriter(fr);
                    //Write headerlines
                    br.write(headerline1);
                    br.write(System.lineSeparator());
                    br.write(headerline2);
                    br.write(System.lineSeparator());
                    //Write the actual data
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
                
                //create second csv file with run times of experiments
                File timesCSV = new File(path + "/times.csv");
                //initialize headerline
                String headerlineTime = "Datasets";
                //build headerline
                for (String modelName : this.modelNames) {
                    headerlineTime += "," + modelName;
                }

                fr = null;
                br = null;

                try {
                    //initialize file + writer
                    fr = new FileWriter(timesCSV);
                    br = new BufferedWriter(fr);
                    //Write header line
                    br.write(headerlineTime);
                    br.write(System.lineSeparator());
                    //write actual data
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

                //Create files for complete Doc Experiments
                for (Map.Entry<String, Map<String, Map<String, Double>>> pair : this.completeDocExperiment.entrySet()) {
                   //Create the result csv file
                    File fullDocCSV = new File(path + "/" + pair.getKey() + ".csv");
                    //parse values to csv writable structure
                    Map<String, Map<String, Map<String, Double>>> entityModelValueMap = new HashMap();
                    for (Map.Entry<String, Map<String, Double>> pair2 : pair.getValue().entrySet()) {
                        String[] entityModelName = pair2.getKey().split(";");
                        Map<String, Map<String, Double>> modelMap;
                        if (entityModelValueMap.get(entityModelName[0]) == null) {
                            modelMap = new HashMap();

                        } else {
                            modelMap = entityModelValueMap.get(entityModelName[0]);
                        }
                        modelMap.put(entityModelName[1], pair2.getValue());
                        entityModelValueMap.put(entityModelName[0], modelMap);

                    }

                    fr = null;
                    br = null;

                    try {
                        //intialize file + writer
                        fr = new FileWriter(fullDocCSV);
                        br = new BufferedWriter(fr);
                        //use old headerline and replace with new values 
                        br.write(headerline1.replace("Datasets", "Entity"));
                        br.write(System.lineSeparator());
                        //write old headerline 2
                        br.write(headerline2);
                        br.write(System.lineSeparator());
                        //write actual data
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

            } else {
                Logger.getLogger(ResultCSV.class.getName()).log(Level.SEVERE, null, "Path " + path + " jeeds to be a directory and not a file!");
            }

        } else {
            Logger.getLogger(ResultCSV.class.getName()).log(Level.SEVERE, null, "Path " + path + " can't be found.");
        }
    }

    /**
     * Actual writing to the csv structure which is similar for every file.
     * @param mapToWrite map with all the data that should be written
     * @param br buffered writer of csv file
     * @param time indicates if times or scores should be written
     * @throws IOException 
     */
    private void writeHelper(Map<String, Map<String, Map<String, Double>>> mapToWrite, BufferedWriter br, boolean time) throws IOException {
        for (Map.Entry<String, Map<String, Map<String, Double>>> entry : mapToWrite.entrySet()) {
            String k = entry.getKey();
            Map<String, Map<String, Double>> v = entry.getValue();
            String line = k;
            for (String model : this.modelNames) {
                Map<String, Double> valueMap = v.get(model);
                if (valueMap == null) {
                    line += time ? ",-" : ",-,-,-";
                } else {
                    if (time) {
                        line += "," + String.format("%.3f", valueMap.get("t"));
                    } else {

                        line += "," + String.format("%.3f", valueMap.get("p"));
                        line += "," + String.format("%.3f", valueMap.get("r"));
                        line += "," + String.format("%.3f", valueMap.get("f1"));
                    }
                }

            }
            br.write(line);
            br.write(System.lineSeparator());
        }
    }

}
