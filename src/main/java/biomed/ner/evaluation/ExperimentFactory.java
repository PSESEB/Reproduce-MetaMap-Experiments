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
import biomed.ner.datasets.impl.I2B22010Reader;
import biomed.ner.datasets.impl.NCBIReader;
import biomed.ner.models.iModel;
import biomed.ner.models.impl.CTakesModel;
import biomed.ner.models.impl.MetaMapLiteModel;
import biomed.ner.models.impl.MetaMapModel;

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
        String semanticGroup = "cgab,acab,inpo,patf,dsyn,anab,neop,mobd,sosy";
        boolean linesFlag = false;
        //First check which Dataset is choosen
        switch(dataset){
            case "NCBI":
                //Create NCBI Reader
                iDatasetReader ncbi = new NCBIReader();
                ncbi.loadDataset("/home/weenzeal/Documents/MasterArbeit/Datasets/NCBI_Disease/", "NCBItrainset_corpus.txt","Disorder,GeneralDisorder");
                ncbi.parseDataset();
                exp.setDataset(ncbi);
                break;
            case "CustomNCBI":
                //Create NCBI Reader
                iDatasetReader ncbiCust = new CustomNCBIReader();
                //Only works for trainset since they only annotated the train set again and not the other ones.
                ncbiCust.loadDataset("/home/weenzeal/Documents/MasterArbeit/Datasets/NCBI_Disease/", "NCBItrainset_corpus.txt","Disorder,GeneralDisorder");
                ncbiCust.parseDataset();
                exp.setDataset(ncbiCust);
                break;
            case "i2b2 2010":
            case "i2b22010":
            case "2010 i2b2":
                //Create i2b2 2010 reader
                iDatasetReader i2b22010 = new I2B22010Reader();
                //Only take problems as labels same as Meta Map Lite authors
                i2b22010.loadDataset("/home/weenzeal/Documents/MasterArbeit/Datasets/i2b2_2010", "", "problem");
                i2b22010.parseDataset();
                exp.setDataset(i2b22010);
                linesFlag = true;
                break;
            default:
                System.out.println("Datset Not Found! No dataset was loaded.");
        }
        //Check which Model should be used for dataset
        switch(model){
            case "MetaMapLite":
            case "MML":
                //instantiate new Meta Map Lite Model
                iModel metaMapLite = new MetaMapLiteModel("/opt/MetaMap/public_mm_lite",semanticGroup,linesFlag);
                exp.setModel(metaMapLite);
                break;
                
            case "cTakes":
            case "ctakes":
                //instantiate new cTakes Model
                iModel cTakes = new CTakesModel();
                exp.setModel(cTakes);
                break;
            case "MetaMap":
            case "MM":
                //instantiate new MetaMapModel
                iModel metaMap = new MetaMapModel(semanticGroup);
                exp.setModel(metaMap);
                break;
            default:
                System.out.println("Model not found! No model was loaded.");
        }
        
        return exp;
    }
}
