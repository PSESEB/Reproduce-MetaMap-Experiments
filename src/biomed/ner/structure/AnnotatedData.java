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
package biomed.ner.structure;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Structure to store annotated labels
 * @author Sebastian Hennig
 */
public class AnnotatedData {
    
    /**
     * Dataset containing all annotated labels.
     * Keys are ids also used for corresponding text
     * Values are is the annotatedDatapoint for a specific text.
     */
    private Map<String,AnnotatedStringDataPoint> datapoints;

    public AnnotatedData(){
        this.datapoints = new HashMap<>();
    }
    
    /**
     * Adds Datapoint to dataset
     * @param adp 
     */
    public void addDatapoint(AnnotatedStringDataPoint adp){
        
        AnnotatedStringDataPoint existingAdp = this.datapoints.get(adp.getIdentifier());
        if(existingAdp == null){
            this.datapoints.put(adp.getIdentifier(), adp);
        }else{
            System.out.println("Size before: "+existingAdp.getAnnotatedConcepts().size());
            existingAdp.getAnnotatedConcepts().addAll(adp.getAnnotatedConcepts());
            System.out.println("Size after: "+existingAdp.getAnnotatedConcepts().size());
            
            this.datapoints.put(existingAdp.getIdentifier(),existingAdp);
        }
    }
    /**
     * Creates datapoint and adds it to dataset
     * @param identifier
     * @param asl
     */
    public void addDatapoint(String identifier, AtomStringLabel asl){
        AnnotatedStringDataPoint existingAdp = this.datapoints.get(identifier);
          if(existingAdp == null){
            this.datapoints.put(identifier, new AnnotatedStringDataPoint(identifier, asl));
        }else{
            existingAdp.addConcept(asl);
            this.datapoints.put(existingAdp.getIdentifier(),existingAdp);
        }
         
    }
    /**
     * Gives all datapoints
     * @return all datapoints
     */
    public Map<String,AnnotatedStringDataPoint> getAllDatapoints(){
        return this.datapoints;
    }
    
    /**
     * Get specific Datapoint
     * @param identifier
     * @return AnnotatedDataPoint
     */
    public AnnotatedStringDataPoint getDataPoint(String identifier){
        return this.datapoints.get(identifier);
        
    }
    
    /**
     * Returns number of datapoints in dataset
     * @return 
     */
    public int size(){
        return this.datapoints.size();
                
    }
    
}
