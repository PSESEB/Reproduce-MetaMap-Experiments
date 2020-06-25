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
import biomed.ner.structure.AnnotatedStringDataPoint;
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
 * Reads and parses i2b2 2010 dataset to the internal structure
 * @author Sebastian Hennig
 */
public class I2B22010Reader implements iDatasetReader{
    
    
    /**
     * Storage of all labels
     */
    private AnnotatedData labelData;
    
    //Datafiles in i2b2 2010 folders
    public List<File> dats;
    
    /**
     * Temporary Storage of Labels before parsing to internal structure
     */
    private Map<String,String> intermediateAnnotations;
    

    
    /**
     * Parsed Input
     * where key identifies the document(document name) and value is the actual text
     */
    private Map<String,String> parsedInput;
    
    
    /**
     * Entity Types that should be considered in the label Set
     * Used to filter only relevant labels
     */
    private String[] entityTypes;
    
    
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
        this.intermediateAnnotations = new HashMap();
        
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
        dats.removeIf(f -> f.getPath().contains("unannotated") || f.isHidden() || !(this.getFileExtension(f).equals("txt") || this.getFileExtension(f).equals("con")));

        for(File f : dats){
            String[] split = f.getName().split("\\.");
            String name = split[0];
            String type = split[1];
                
                try {
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();
                    
                    while(line != null){
                         sb.append(line);
                         sb.append(System.lineSeparator());
                        
                        line = br.readLine();
                    }
                    if(type.equals("txt")){
                    this.parsedInput.put(name, sb.toString());
                        
                    }else if(type.equals("con")){
                        this.intermediateAnnotations.put(name, sb.toString());
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(I2B22010Reader.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(I2B22010Reader.class.getName()).log(Level.SEVERE, null, ex);
                }
            
        }
        assert this.intermediateAnnotations.size() == this.parsedInput.size();
        
         Logger.getLogger(I2B22010Reader.class.getName()).log(Level.INFO, "i2b2 2010 data loaded");

        
    }
    
    

    @Override
    public void parseDataset() {
        
        //PARSE LABELS
        this.labelData = new AnnotatedData();
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
                line = line.replaceAll("\\|\\|t=", "");
                String[] components = line.split("\"");
                if(Arrays.stream(entityTypes).anyMatch(components[components.length-1]::equals)){
                
                    
                    String labelText = components[1];
                    if(components.length > 4){
                        for(int i = 2; i < components.length-2; i++){
                            labelText += "\""+components[i];
                        }
                    }
                    String[] offsets = components[components.length-2].split(" ");
                    String[] finalOffsets = this.calcOffset(id, offsets[1], offsets[2]).split(",");
                    
                    dp.addConcept(new AtomStringLabel(labelText,Integer.parseInt(finalOffsets[0]),Integer.parseInt(finalOffsets[1])));
                   
                    
                }
               
                
            }
            //Add complete label to dataset
            this.labelData.addDatapoint(dp);
        }


        System.out.println("Size Lables "+this.labelData.size());
        
        //PARSE INPUT DATA
        for (Map.Entry<String, String> entry : this.parsedInput.entrySet()) {
          
            this.parsedInput.put(entry.getKey(), entry.getKey()+"\t\t"+entry.getValue());
        }
       
        
        System.out.println("Input Size: "+this.parsedInput.size());
        
        
    }
    
    public String calcOffset(String key, String start, String end){
            
      
        String[] lines = this.parsedInput.get(key).split(System.lineSeparator());
        int[] accLens = new int[lines.length];
        accLens[0] = 0;
        for(int i = 0; i < lines.length-1; i++){
            //Plus one is for space that will be substituted later for the line break
            accLens[i+1] = accLens[i] + lines[i].length() +1;
        }
        String[] startSplit = start.split(":");
        int startLine = Integer.parseInt(startSplit[0])-1;
        int startWord = Integer.parseInt(startSplit[1]);
        
        String[] endSplit = end.split(":");
        int endLine = Integer.parseInt(endSplit[0])-1;
        int endWord = Integer.parseInt(endSplit[1])+1;
        
        int lineoffset = accLens[startLine];
        int interLineOffset = 0;
        
        ArrayList<Integer> accLensLine = new ArrayList();
        accLensLine.add(0);
        for(String word : lines[startLine].split(" ")){
            interLineOffset += word.length()+1;
            accLensLine.add(interLineOffset);
        }
        int startOffset = lineoffset + accLensLine.get(startWord)+1;
        
        int endOffset;
        if(endLine == startLine){
            endOffset = lineoffset + accLensLine.get(endWord);
        }else{
            lineoffset = accLens[endLine];
            interLineOffset = 0;
        
            accLensLine = new ArrayList();
            accLensLine.add(0);
            for(String word : lines[endLine].split(" ")){
                interLineOffset += word.length()+1;
                accLensLine.add(interLineOffset);
            }
            endOffset = lineoffset + accLensLine.get(endWord);
        }
        
        
        return String.join(",", Integer.toString(startOffset), Integer.toString(endOffset));
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
