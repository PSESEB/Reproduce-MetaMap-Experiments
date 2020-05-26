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

import java.util.Objects;

/**
 * Contains annotated CUI list for a specific text identified with an unique id
 * @author Sebastian Hennig
 */
public class AtomStringLabel {
    
    /**
     * Label which should be the same for the corresponding text
     */
    private String term;
    
    /**
     * CUI that was annotated in corresponding text
     */
    private int startOffset;
    
    private int endOffset;

    /**
     * Creates Atom Label that should be compared
     * @param label
     * @param start
     * @param end 
     */
    public AtomStringLabel(String label, int start, int end){
       this.term = label;
       this.startOffset = start;
       this.endOffset = end;
                
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AtomStringLabel)){
            return false;
        }

        AtomStringLabel other_ = (AtomStringLabel) other;

        return this.term.equals(other_.term) && this.startOffset == other_.startOffset && this.endOffset == other_.endOffset;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.term);
        hash = 59 * hash + this.startOffset;
        hash = 59 * hash + this.endOffset;
        return hash;
    }
    
    @Override
    public String toString(){
        return this.startOffset + "|"+this.endOffset + "|"+this.term;
    }
    
    
    
    
}
