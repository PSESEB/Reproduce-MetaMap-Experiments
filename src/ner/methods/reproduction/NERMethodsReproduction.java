/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ner.methods.reproduction;
import gov.nih.nlm.nls.ner.MetaMapLite;

import biomed.ner.datasets.iDatasetReader;
import biomed.ner.datasets.impl.NCBIReader;
import biomed.ner.models.iModel;
import biomed.ner.models.impl.MetaMapLiteModel;
import biomed.ner.structure.AnnotatedData;
import biomed.ner.structure.AnnotatedDataPoint;


/**
 *
 * @author weenzeal
 */
public class NERMethodsReproduction {

    
    private MetaMapLite m_metaMapLiteInst;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        iDatasetReader ncbi = new NCBIReader();
        
        ncbi.loadDataset("/home/weenzeal/Documents/MasterArbeit/Datasets/NCBI_Disease/", "NCBItestset_corpus.txt");
        ncbi.parseDataset();
        
        
        iModel metaMapLite = new MetaMapLiteModel("/opt/MetaMap/public_mm_lite");
       
        AnnotatedDataPoint out = metaMapLite.annotateText("heppa","lung cancer bitches D008661 Inborn Errors of Metabolism");
      
        for(String ad : out.getAnnotatedCUIs()){
           
//          System.out.println(out.getIdentifier()+" <-> "+ad);
        }
        
       
    }
    
   
}
