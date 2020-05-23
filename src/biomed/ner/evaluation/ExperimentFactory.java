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
package biomed.ner.evaluation;

import biomed.ner.datasets.iDatasetReader;
import biomed.ner.datasets.impl.NCBIReader;
import biomed.ner.models.iModel;
import biomed.ner.models.impl.MetaMapLiteModel;

/**
 *
 * Creates an experiment with a specific dataset and model
 * @author Sebastian Hennig
 */
public class ExperimentFactory {
    
    
    public static Experiment getExperiment(String dataset, String model){
        
        Experiment exp = new Experiment();
        String semanticGroup = "";
        switch(dataset){
            case "NCBI":
                iDatasetReader ncbi = new NCBIReader();
                ncbi.loadDataset("/home/weenzeal/Documents/MasterArbeit/Datasets/NCBI_Disease/", "NCBItestset_corpus.txt");
                ncbi.parseDataset();
                exp.setDataset(ncbi);
                semanticGroup = "cgab,acab,inpo,patf,dsyn,anab,neop,mobd,sosy";
                break;
            default:
                System.out.println("Datset Not Found! No dataset was loaded.");
        }
        
        switch(model){
            case "MetaMapLite":
            case "MML":
                iModel metaMapLite = new MetaMapLiteModel("/opt/MetaMap/public_mm_lite",semanticGroup);
                exp.setModel(metaMapLite);
                break;
            default:
                System.out.println("Model not found! No model was loaded.");
        }
        
        return exp;
    }
}
