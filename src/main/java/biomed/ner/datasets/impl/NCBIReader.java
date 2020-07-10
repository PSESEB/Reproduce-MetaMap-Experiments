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
package biomed.ner.datasets.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import biomed.ner.datasets.iDatasetReader;
import biomed.ner.structure.AnnotatedData;
import biomed.ner.structure.AtomStringLabel;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Reads and parses NCBI datasets to the internal structure needed
 * @author Sebastian Hennig
 */
public class NCBIReader implements iDatasetReader{
    
    /**
     * Temporary Storage for file input
     */
    private ArrayList<String> inputData;
    
    /**
     * Parsed Labels
     */
    private AnnotatedData labelData;
    
    /**
     * Temporary Storage for labels loaded from file
     */
    private String intermediateAnnotations;
    
    /**
     * Path to dataset
     */
    private String datasetPath;
    
    /**
     * Final parsed Input
     */
    private Map<String,String> parsedInput;
    
    private boolean cuiOut = false;
    
    

    @Override
    public void loadDataset(String path, String file,String settings){
        
        //Build Path
        String full_path = path + file;
        this.datasetPath = path;
        BufferedReader br;
        //Save different Entity Types that should be scanned for
        String[] entityTypes;
        if(settings.isEmpty()){
            entityTypes = new String[0];
        }else{
            entityTypes = settings.split(",");
        }
        
        //Read Dataset File
        try {
            br = new BufferedReader(new FileReader(full_path));
       
        
        this.inputData = new ArrayList<>();
        this.labelData = new AnnotatedData();
        //Find abstracts and titles with regex
        Pattern p = Pattern.compile("^[0-9]+\\|(a|t)\\|");
        Matcher m;  
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                m = p.matcher(line);
                if(m.find()){
                    //if regex matches: add to input
                    this.inputData.add(line);
                }else{
                    //else add to labels
                    String[] split = line.split("\t");
                    if(split.length > 1){
                        
                        AtomStringLabel asl = new AtomStringLabel(split[3], Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                        
                        labelData.addDatapoint(split[0], asl);
                        //Parse Labels
                        String label = split[split.length -1];
                        //Multi Label eg. C123|C456 or C123+C456
                        if(label.contains("|") || label.contains("+")){
                            String[] splitLabels = label.contains("|") ? label.split("\\|") : label.split("\\+");
                            //Add all labels
                            for(String splitLabel : splitLabels){
                                sb.append(split[0].trim());
                                sb.append(" ");
                                sb.append(splitLabel.trim());
                                sb.append(System.lineSeparator());
                            }
                        
                        }else{//Single Label
                            sb.append(split[0].trim());
                            sb.append(" ");
                            sb.append(split[split.length -1].trim());
                            sb.append(System.lineSeparator());
                        }
                       
                    }
                }
                line = br.readLine();
            }
            //Add labels to temporary storage
            this.intermediateAnnotations = sb.toString();
        } finally {
            br.close();
        }
         } catch (FileNotFoundException ex) {
            Logger.getLogger(NCBIReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NCBIReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
    }
    
    /**
     * 
     * Maps NCBI labels to CUIs
     * @param path Path to the mapping files. Should be same as dataset
     * @return HashMap mapping NCBI tags to CUIs
     */
    private HashMap<String,Set<String>> loadMappings(String path)
    {
        //NCBI tag as key CUI as value
        HashMap<String, Set<String>> cuiMapping = new HashMap<>();
        BufferedReader br;
        //Define Mapping Files (see github readme on how to produce these)
        String[] mappingFiles = {"MHcui","OMIMcui","MHCcui"};
        for(String mappingFile : mappingFiles){
            try {
                //Read mapping files
               br = new BufferedReader(new FileReader(path+mappingFile));

           try {

               String line = br.readLine();

               while (line != null) {
                   
                   String[] split = line.split("\\|");
                   
                   //Put OMIM identifiers in correct syntax
                   if(mappingFile.equals("OMIMcui")){
                       split[1] = "OMIM:"+split[1];
                   }
                   //Add mapping to a key value map
                   //Keys are OMIM,MeSH identifiers and values are corresponding cui(s)
                   Set<String> currVal = cuiMapping.get(split[1]);             
                   if(currVal == null){
                       Set<String> newSet = new HashSet<>();
                       newSet.add(split[0]);
                       cuiMapping.put(split[1], newSet);
                       
                   }else{
                       currVal.add(split[0]);
                       cuiMapping.put(split[1], currVal);
                   }
                  

                    line = br.readLine();
               }

           } finally {
               br.close();
           }
            } catch (FileNotFoundException ex) {
               Logger.getLogger(NCBIReader.class.getName()).log(Level.SEVERE, null, ex);
           } catch (IOException ex) {
               Logger.getLogger(NCBIReader.class.getName()).log(Level.SEVERE, null, ex);
           }
        }
        return cuiMapping;
        
    }

    @Override
    public void parseDataset() {
        
        //PARSE LABELS
        
        System.out.println("Size Lables "+this.labelData.size());
        
        //PARSE INPUT DATA
        this.parsedInput = new HashMap<>();
        for(String input: this.inputData){
            String[] split = input.split("\\|");
            if(this.parsedInput.get(split[0]) == null){
                //Parse in NCBI format
                this.parsedInput.put(split[0],split[0]+"\t"+split[2]);
            }else{
                this.parsedInput.put(split[0], this.parsedInput.get(split[0])+"\t"+split[2]);
            }
        }
        
        System.out.println("Input Size: "+this.parsedInput.size());
        
        
    }

    @Override
    public Map<String, String> getInputData() {
            return this.parsedInput;
    }

    @Override
    public AnnotatedData getLabelData() {
        return this.labelData;
    }
    
     @Override
    public boolean isCUIoutput() {
        return this.cuiOut;
    }
    
    @Override
    public String getDatasetName() {
            return "NCBI";
    }
    
    
}
