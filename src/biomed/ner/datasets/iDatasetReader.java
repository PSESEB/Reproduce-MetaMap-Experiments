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
package biomed.ner.datasets;

import biomed.ner.structure.AnnotatedData;
import java.util.Map;

/**
 * Interface for Dataset Readers
 * @author Sebastian Hennig
 */
public interface iDatasetReader {
    
    /**
     * Reads the file/files
     * @param path path to dataset files
     * @param file file name
     */
    public void loadDataset(String path,String file);
    /**
     * parses dataset to internal structure
     */
    public void parseDataset();
    
    /**
     * Returns the parsed Input Data ready to use with model.
     * 
     * @return map where key string is identifier of the text
     * and the value is the actual text that should be run through the model
     * 
     */
    public Map<String,String> getInputData();
    
    /**
     * Returns annotated labels that need to be compared to output of the model
     * @return Annotated dataset
     */
    public AnnotatedData getLabelData();
    
}
