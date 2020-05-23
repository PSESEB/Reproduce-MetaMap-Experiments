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
import biomed.ner.structure.AnnotatedDataPoint;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sebastian Hennig
 */
public class NCBIReader implements iDatasetReader{
    
    private ArrayList<String> inputData;
    
    private AnnotatedData labelData;
    
    private String intermediateAnnotations;
    
    private String datasetPath;
    
    private Map<String,String> parsedInput;
    

    @Override
    public void loadDataset(String path, String file){
        
        
        String full_path = path + file;
        this.datasetPath = path;
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(full_path));
       
        
        this.inputData = new ArrayList<>();
        this.labelData = new AnnotatedData();
        //Find abstracts and titles
        Pattern p = Pattern.compile("^[0-9]+\\|(a|t)\\|");
        Matcher m;  
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                m = p.matcher(line);
                if(m.find()){
                    this.inputData.add(line);
                }else{
                    String[] split = line.split("\t");
                    if(split.length > 1){
                        
                        String label = split[split.length -1];
                        if(label.contains("|") || label.contains("+")){
                            String[] splitLabels = label.contains("|") ? label.split("\\|") : label.split("\\+");
                            for(String splitLabel : splitLabels){
                                sb.append(split[0].trim());
                                sb.append(" ");
                                sb.append(splitLabel.trim());
                                sb.append(System.lineSeparator());
                            }
                        
                        }else{
                            sb.append(split[0].trim());
                            sb.append(" ");
                            sb.append(split[split.length -1].trim());
                            sb.append(System.lineSeparator());
                        }
                       
                    }
                }
                line = br.readLine();
            }
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
        String[] mappingFiles = {"MHcui","OMIMcui","MHCcui"};
        for(String mappingFile : mappingFiles){
            try {
               br = new BufferedReader(new FileReader(path+mappingFile));

           try {

               String line = br.readLine();

               while (line != null) {
                   
                   String[] split = line.split("\\|");
                   
                   if(mappingFile.equals("OMIMcui")){
                       split[1] = "OMIM:"+split[1];
                   }
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
        
        //Create HashMap to store MeSH and OMIM identifiers corresponding CUIs
         HashMap<String,Set<String>> cuiMappings = this.loadMappings(this.datasetPath);
         System.out.println("The size of the map is " + cuiMappings.size()); 
        
         //Load different files containing mappings and writing them into the map
        for(String annotation : this.intermediateAnnotations.split(System.lineSeparator())){
            String[] annotationSplit = annotation.split(" ");
            String identifier = annotationSplit[0];
            Set<String> cuis = cuiMappings.get(annotationSplit[1]);
            if(cuis == null){
                System.out.println("CAUTION: No UMLS Concept found for "+annotationSplit[1]);
                System.out.println(annotationSplit[1]+" is therefore removed from label set!");
                continue;
            }
            for(String cui: cuis){
                
                this.labelData.addDatapoint(new AnnotatedDataPoint(identifier, cui));
            }
        }
        
        System.out.println("SizeLables "+this.labelData.size());
        
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
    
}
