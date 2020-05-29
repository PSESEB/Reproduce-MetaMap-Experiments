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
import biomed.ner.datasets.impl.CustomNCBIReader;
import biomed.ner.datasets.impl.NCBIReader;
import biomed.ner.models.iModel;
import biomed.ner.models.impl.MetaMapLiteModel;

/**
 *
 * Creates an experiment with a specific dataset and model
 * @author Sebastian Hennig
 */
public class ExperimentFactory {
    
    /**
     * Creates a specific Experiment according to the dataset and model name.
     * @param dataset String
     * @param model String
     * @return Experiment
     */
    public static Experiment getExperiment(String dataset, String model){
        
        Experiment exp = new Experiment();
        String semanticGroup = "";
        //First check which Dataset is choosen
        switch(dataset){
            case "NCBI":
                //Create NCBI Reader
                iDatasetReader ncbi = new NCBIReader();
                ncbi.loadDataset("/home/weenzeal/Documents/MasterArbeit/Datasets/NCBI_Disease/", "NCBItrainset_corpus.txt","Disorder,GeneralDisorder");
                ncbi.parseDataset();
                exp.setDataset(ncbi);
                //Semantic Groups for NCBI
                semanticGroup = "cgab,acab,inpo,patf,dsyn,anab,neop,mobd,sosy";
                break;
            case "CustomNCBI":
                //Create NCBI Reader
                iDatasetReader ncbiCust = new CustomNCBIReader();
                //Only works for trainset since they only annotated the train set again and not the other ones.
                ncbiCust.loadDataset("/home/weenzeal/Documents/MasterArbeit/Datasets/NCBI_Disease/", "NCBItrainset_corpus.txt","Disorder,GeneralDisorder");
                ncbiCust.parseDataset();
                exp.setDataset(ncbiCust);
                //Semantic Groups for NCBI
                semanticGroup = "cgab,acab,inpo,patf,dsyn,anab,neop,mobd,sosy";
                break;
            default:
                System.out.println("Datset Not Found! No dataset was loaded.");
        }
        //Check which Model should be used for dataset
        switch(model){
            case "MetaMapLite":
            case "MML":
                //instantiate new Meta Map Lite Model
                iModel metaMapLite = new MetaMapLiteModel("/opt/MetaMap/public_mm_lite",semanticGroup);
                exp.setModel(metaMapLite);
                break;
            default:
                System.out.println("Model not found! No model was loaded.");
        }
        
        return exp;
    }
}
