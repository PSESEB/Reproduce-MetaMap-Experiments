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
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author Sebastian Hennig
 */
public class NCBIReader implements iDatasetReader{
    
    private ArrayList<String> inputData;
    
    private ArrayList<AnnotatedData> labelData;
    

    @Override
    public void loadDataset(String path, String file) {
        
        
        String full_path = path + file;
        BufferedReader br = new BufferedReader(new FileReader(full_path));
        
        this.inputData = new ArrayList<String>();
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
                    sb.append(line);
                    sb.append(System.lineSeparator());
                }
                line = br.readLine();
            }
            String annotatedData = sb.toString();
        } finally {
            br.close();
        }
    }

    @Override
    public void parseDataset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
