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
import biomed.ner.structure.AnnotatedStringDataPoint;
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
public class CustomNCBIReader implements iDatasetReader{
    
    private ArrayList<String> inputData;
    
    private AnnotatedData labelData;
    
    private Map<String,String> intermediateAnnotations;
    
    private String datasetPath;
    
    private Map<String,String> parsedInput;
    
    private String[] entityTypes;
    
    

    @Override
    public void loadDataset(String path, String file,String settings){
        
        
        String full_path = path + file;
        this.datasetPath = path;
        String annotationPath = path +"lhc_annotations/";
        BufferedReader br;
        //Save different Entity Types that should be scanned for
        String[] entityType;
        if(settings.isEmpty()){
            entityType = new String[0];
        }else{
            entityType = settings.split(",");
        }
        this.entityTypes = entityType;
        
        try {
            br = new BufferedReader(new FileReader(full_path));
       
        
        this.inputData = new ArrayList<>();
        this.labelData = new AnnotatedData();
        this.intermediateAnnotations = new HashMap();
        //Find abstracts and titles with regex
        Pattern p = Pattern.compile("^[0-9]+\\|(a|t)\\|");
        Matcher m;  
        try {
           
            String line = br.readLine();

            while (line != null) {
                m = p.matcher(line);
                if(m.find()){
                    //if regex matches: add to input
                    this.inputData.add(line);
                }else{
                    //else add to labels
                   if(line.split("\t").length > 1){
                        String identifier = line.split("\t")[0];
                        if(this.intermediateAnnotations.get(identifier) == null){
                            BufferedReader bra = new BufferedReader(new FileReader(annotationPath+"PMID-"+identifier+".ann"));
                             StringBuilder sb = new StringBuilder();
                             String aline = bra.readLine();
                             while(aline != null){
                                 sb.append(aline);
                                 sb.append(System.lineSeparator());
                                 aline = bra.readLine();
                             }
                             bra.close();
                             this.intermediateAnnotations.put(identifier,sb.toString());
                        }
                        
                    }
                }
                line = br.readLine();
            }
        } finally {
            br.close();
        }
         } catch (FileNotFoundException ex) {
            Logger.getLogger(CustomNCBIReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CustomNCBIReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    

    @Override
    public void parseDataset() {
        
        //PARSE LABELS
        
        //Iterate over loaded labelset
        for (Map.Entry<String, String> entry : this.intermediateAnnotations.entrySet()) {
            //retrieve document identifier
            String id = entry.getKey();
            //Create new DataPoint
            AnnotatedStringDataPoint dp = new AnnotatedStringDataPoint(id);
            //Split .ann labels into its single lines
            String[] lines = entry.getValue().split(System.lineSeparator());
            //For every line check if they are T values
            for(String line : lines){
                String[] components = line.split("\t");
               //If relevant T value -> further process this entry
                if(components[0].contains("T")){
                    
                    //Split label furth to identify Entity aswell as offsets
                    String[] detailledComponents = components[1].split(" ");
                    if(Arrays.stream(this.entityTypes).anyMatch(detailledComponents[0]::equals)){
                        AtomStringLabel asl;
                        //In case of non composite lables
                        if(detailledComponents.length < 4){
                            asl = new AtomStringLabel(components[2], Integer.parseInt(detailledComponents[1]), Integer.parseInt(detailledComponents[2]));
                      
                        //Case of composite lables   
                       }else{
                         
                            asl = new AtomStringLabel(components[2], Integer.parseInt(detailledComponents[1]), Integer.parseInt(detailledComponents[detailledComponents.length-1]));
                        }
                        //Add concept to labelset
                        dp.addConcept(asl);
                    }
                }
            }
            //Add complete label to dataset
            this.labelData.addDatapoint(dp);
        }


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
    
}
