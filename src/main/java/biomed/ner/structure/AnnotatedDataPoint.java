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

import java.util.ArrayList;
import java.util.List;

/**
 * Contains annotated CUI list for a specific text identified with an unique id
 * @author Sebastian Hennig
 */
public class AnnotatedDataPoint {
    
    /**
     * Label which should be the same for the corresponding text
     */
    private String identifier;
    
    /**
     * CUI that was annotated in corresponding text
     */
    private List<String> annotatedCUIs;

    /**
     * Creates CUI annotation for corresponding text identified with id.
     * @param id identifier of this label
     * @param cui CUI for this label
     */
    public AnnotatedDataPoint(String id, String cui){
        this.identifier = id;
        this.annotatedCUIs = new ArrayList<>();
        this.annotatedCUIs.add(cui);
                
    }
    /**
     * Creates CUIs annotation for corresponding text identified with id.
     * @param id identifier of this label
     * @param cuis list of CUIs for this label
     */
    public AnnotatedDataPoint(String id, List<String> cuis){
        this.identifier = id;
        this.annotatedCUIs = cuis;
    }
    
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<String> getAnnotatedCUIs() {
        return annotatedCUIs;
    }

    public void setAnnotatedCUIs(List<String> annotatedCUIs) {
        this.annotatedCUIs = annotatedCUIs;
    }
    
    public void addAnnotatedCUI(String annotatedCUI){
        this.annotatedCUIs.add(annotatedCUI);
                
    }
    
    
    
}
