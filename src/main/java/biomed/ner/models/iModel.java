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
package biomed.ner.models;

import biomed.ner.structure.AnnotatedDataPoint;
import biomed.ner.structure.AnnotatedStringDataPoint;

/**
 * Interface for NER Models
 * @author Sebastian Hennig
 */
public interface iModel {
    /**
     * Annotates given text.
     * An id needs to be provided so it can later be mapped to the corresponding label.
     * @param id Identifier of the String that should be annotated
     * @param text Text to be annotated
     * @return annotations as suggested by model 
     */
    public AnnotatedStringDataPoint annotateText(String id, String text, boolean cui);
    
     /**
     * Annotates given text.
     * An id needs to be provided so it can later be mapped to the corresponding label.
     * @param id Identifier of the String that should be annotated
     * @param text Text to be annotated
     * @return annotations as suggested by model 
     */
    public AnnotatedDataPoint annotateTextCUI(String id, String text);
    
    public String getModelName();
}
