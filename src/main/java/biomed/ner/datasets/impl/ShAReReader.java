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


import biomed.ner.datasets.iDatasetReader;
import biomed.ner.structure.AnnotatedData;
import biomed.ner.structure.AtomStringLabel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Reads and parses LHC NCBI datasets to the internal structure needed
 * The input is still loaded from the original NCBI dataset.
 * But the Annotations are loaded from the .ann files provided by LHC
 * @author Sebastian Hennig
 */
public class ShAReReader implements iDatasetReader{
    

    
    /**
     * Storage of all labels
     */
    private AnnotatedData labelData;
    
     //Datafiles in Share folder
    public List<File> dats;
    
    /**
     * Temporary Storage of Labels before parsing to internal structure
     */
    private List<String> intermediateAnnotations;
    
    /**
     * Path do dataset
     */
    private String datasetPath;
    
    /**
     * Parsed Input
     */
    private Map<String,String> parsedInput;
    
    
    /**
     * Entity Types that should be considered in the label Set
     * Used to filter only relevant labels
     */
    private String[] entityTypes;
    
    private boolean cuiOut = true;
    
    
     private void listf(String directoryName, List<File> files) {
    File directory = new File(directoryName);

    // Get all files from a directory.
    File[] fList = directory.listFiles();
    if(fList != null)
        for (File file : fList) {      
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                listf(file.getAbsolutePath(), files);
            }
        }
    }

    
    
    
    
    private String getFileExtension(File file) {
    String name = file.getName();
    int lastIndexOf = name.lastIndexOf(".");
    if (lastIndexOf == -1) {
        return ""; // empty extension
    }
    return name.substring(lastIndexOf+1);
}
    

    @Override
    public void loadDataset(String path, String file,String settings){
        
          //Initialize
        this.parsedInput = new HashMap();
        this.intermediateAnnotations = new ArrayList();
        
        //Save different Entity Types that should be scanned for
        String[] entityType;
        if(settings.isEmpty()){
            entityType = new String[0];
        }else{
            entityType = settings.split(",");
        }
        this.entityTypes = entityType;
        
        //Build Path
        dats = new ArrayList();
        this.listf(path, dats);
        dats.removeIf(f -> f.isHidden() || !(this.getFileExtension(f).equals("txt") ));
        

      
        for(File f:dats){
            
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                
                    while(line != null){
                         sb.append(line);
                         sb.append(System.lineSeparator());
                        
                        line = br.readLine();
                    }
                
                   //Label
            if(f.toString().contains(".pipe.")){
               this.intermediateAnnotations.add(sb.toString());
               
            }else{//Input text
                this.parsedInput.put(f.getName(), sb.toString().replace("\t", " ").replace(System.lineSeparator()+System.lineSeparator()
                        , " "+System.lineSeparator()));
               
                
            }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ShAReReader.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ShAReReader.class.getName()).log(Level.SEVERE, null, ex);
            }
            
         
        }
        
        
    }
    
    

    @Override
    public void parseDataset() {
        
        //PARSE LABELS
        this.labelData = new AnnotatedData();
        for(String s: this.intermediateAnnotations){
            for(String line : s.split(System.lineSeparator())){
                String[] singleComponents = line.split("\\|");
               
                
                if(singleComponents[1].contains(",") || singleComponents[2].equals("CUI-less") ){
                    
                    continue;
                }
                String[] positions = singleComponents[1].split("-");
                //Offset is shifted 1 pos left so we add 1 here
                AtomStringLabel asl = new AtomStringLabel(singleComponents[2], Integer.parseInt(positions[0])+1, Integer.parseInt(positions[1])+1);
                this.labelData.addDatapoint(singleComponents[0], asl);
                
            }
           
        }
        Set<String> cmp1 = new HashSet(this.labelData.getAllDatapoints().keySet());
        Set<String> cmp2 = new HashSet(this.parsedInput.keySet());
       
        cmp2.removeAll(cmp1);
        
        this.parsedInput.keySet().removeAll(cmp2);
        
      
        
        //PARSE INPUT DATA
        this.parsedInput.forEach((k, v) ->  this.parsedInput.put(k, k+"\t\t"+v));
        
        
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
            return "ShARe";
    }
}
