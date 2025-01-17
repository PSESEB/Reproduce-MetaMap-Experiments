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
package biomed.ner.models.impl;

import biomed.ner.models.iModel;
import java.util.Properties;
import gov.nih.nlm.nls.ner.MetaMapLite;
import java.util.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.metamap.lite.types.Ev;
import bioc.BioCDocument;
import biomed.ner.structure.AnnotatedDataPoint;
import biomed.ner.structure.AnnotatedStringDataPoint;
import biomed.ner.structure.AtomStringLabel;
import gov.nih.nlm.nls.metamap.document.NCBICorpusDocument;
import gov.nih.nlm.nls.metamap.lite.resultformats.Brat;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * Provides Meta Map Lite Model
 * @author Sebastian Hennig
 */
public class MetaMapLiteModel implements iModel {
    
    private MetaMapLite m_metaMapLiteInst;
    
    /**
     * Instantiate MetaMapLite
     * @param mml_folder 
     * @param semanticGroups 
     */
    public MetaMapLiteModel(String mml_folder, String semanticGroups, boolean linesFlag){
          // Initialization Section
        
        Properties myProperties = new Properties();
        
   
        // Select the 2020AA database
        
        myProperties.setProperty("metamaplite.index.directory", mml_folder + "/data/ivf/2020AA/USAbase/");
        myProperties.setProperty("opennlp.models.directory", mml_folder + "/data/models/");
        myProperties.setProperty("opennlp.en-pos.bin.path", mml_folder + "/data/models/en-pos-maxent.bin");
        myProperties.setProperty("opennlp.en-sent.bin.path", mml_folder + "/data/models/en-sent.bin");
        myProperties.setProperty("opennlp.en-token.bin.path", mml_folder + "/data/models/en-token.bin");
        
        //Add fitting semantic groups
        myProperties.setProperty("metamaplite.semanticgroup", semanticGroups);
       
        if(linesFlag){
            myProperties.setProperty("metamaplite.segmentation.method", "LINES");
            
        }
        
        try {
            m_metaMapLiteInst = new MetaMapLite(myProperties);
        } catch (ClassNotFoundException | InstantiationException | NoSuchMethodException | IllegalAccessException | IOException ex) {
            Logger.getLogger(MetaMapLiteModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public AnnotatedStringDataPoint annotateText(String id,String text, boolean cui) {
        
        //Create empty result set to be filled with suggested labels of Meta Map Lite
        AnnotatedStringDataPoint result = new AnnotatedStringDataPoint(id);
        
        // Each document must be instantiated as a BioC document before processing
        
        
        BioCDocument document = NCBICorpusDocument.instantiateBioCDocument(text);
       // BioCDocument document = FreeText.instantiateBioCDocument(text.split("\t")[2]);
        
        
        // Proccess the document with Metamap
        
        List<Entity> entityList;
        try {
            entityList = m_metaMapLiteInst.processDocument(document);
           
             Brat bratFormattere = new Brat();
            
              // Add each found CUI to result
        
            for (Entity entity: entityList) 
            {
              //Get concept String
              String cmpS = bratFormattere.entityListFormatToString(Collections.singletonList(entity)).split(System.lineSeparator())[0].split("\t")[2];
            
              if(cui){
                  cmpS = entity.getEvList().get(0).getConceptInfo().getCUI();
              }
              //Get Start Offset
              int start = entity.getFieldId().equals("title") ? entity.getOffset() : document.getPassage(0).getText().length()+ entity.getOffset()+1;
              //Get End Offset
              int end = entity.getFieldId().equals("title") ? entity.getOffset()+entity.getLength() : document.getPassage(0).getText().length()+ entity.getOffset()+entity.getLength()+1;
              //Create Label 
              AtomStringLabel asl = new AtomStringLabel(cmpS, start, end);
              result.addConcept(asl);
                
            }
        } catch (InvocationTargetException ex) {
            Logger.getLogger(MetaMapLiteModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MetaMapLiteModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(MetaMapLiteModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
      
    }

    @Override
    public AnnotatedDataPoint annotateTextCUI(String id, String text) {
    
        //Create empty result set to be filled with suggested labels of Meta Map Lite
        AnnotatedDataPoint result = new AnnotatedDataPoint(id, new ArrayList<>());
        
        // Each document must be instantiated as a BioC document before processing
        
        BioCDocument document = NCBICorpusDocument.instantiateBioCDocument(text);
        
        
        
        // Proccess the document with Metamap
        
        List<Entity> entityList;
        try {
            entityList = m_metaMapLiteInst.processDocument(document);
           
            
              // Add each found CUI to result
        
            for (Entity entity: entityList) 
            {
                for (Ev ev: entity.getEvSet()) 
                {
                    // Add new output CUI
                    result.addAnnotatedCUI(ev.getConceptInfo().getCUI());

                }
            }
        } catch (InvocationTargetException ex) {
            Logger.getLogger(MetaMapLiteModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MetaMapLiteModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(MetaMapLiteModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    
    }

    @Override
    public String getModelName() {
        return "Meta Map Lite";
    }
    
    
    
}
