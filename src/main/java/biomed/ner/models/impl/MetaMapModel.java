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
import biomed.ner.structure.AnnotatedDataPoint;
import biomed.ner.structure.AnnotatedStringDataPoint;
import biomed.ner.structure.AtomStringLabel;
import gov.nih.nlm.nls.metamap.Ev;
import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Position;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Provides Meta Map Model
 *
 * @author Sebastian Hennig
 */
public class MetaMapModel implements iModel {

    private MetaMapApi api;

    /**
     * Instantiate MetaMapLite
     *
     * @param mml_folder
     * @param semanticGroups
     */
    public MetaMapModel(String semanticGroups) {
        // Initialization Section
        api = new MetaMapApiImpl();

        api.setOptions("-J "+semanticGroups);
    }

    @Override
    public AnnotatedStringDataPoint annotateText(String id, String text, boolean cui) {

          //Create empty result set to be filled with suggested labels of Meta Map Lite
        AnnotatedStringDataPoint result = new AnnotatedStringDataPoint(id);
        
        //Parse NCBI tab seperated text to one single text
        String[] ncbiSplit = text.split("\t");
        //Usually looks like id\tTitle\tAbstract -> We only need title and abstract
        String fullText = ncbiSplit[1]+" "+ncbiSplit[2];
        
        
        List<Result> resultList = api.processCitationsFromString(fullText);

        for (Result res : resultList) {
            try {
                for (Utterance utterance : res.getUtteranceList()) {
                    for (PCM pcm : utterance.getPCMList()) {
                        for (Mapping map : pcm.getMappingList()) {
                            for (Ev mapEv : map.getEvList()) {
                                 //System.out.println("   Concept Id: " + mapEv.getConceptId());
                                 //System.out.println("   Concept Name: " + mapEv.getConceptName());
//                                System.out.println("   Preferred Name: " + mapEv.getPreferredName());
                                String conceptText = String.join(" ",mapEv.getMatchedWords());
                               if(cui){
                                   conceptText = mapEv.getConceptId();
                               }
//                                System.out.println("   Matched Words: " + String.join(" ",mapEv.getMatchedWords()));
//                                System.out.println("   Positional Info: " + mapEv.getPositionalInfo());
                                for(Position pos : mapEv.getPositionalInfo()){
                                    AtomStringLabel asl = new AtomStringLabel(conceptText, pos.getX(), pos.getX()+pos.getY());
                                    result.addConcept(asl);
                                }
                            }
                        }
                    }

                }
            } catch (Exception ex) {
                Logger.getLogger(MetaMapModel.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return result;
    }

    @Override
    public AnnotatedDataPoint annotateTextCUI(String id, String text) {
         //Create empty result set to be filled with suggested labels of Meta Map Lite
        AnnotatedDataPoint result = new AnnotatedDataPoint(id, new ArrayList<>());
        
          
        //Parse NCBI tab seperated text to one single text
        String[] ncbiSplit = text.split("\t");
        //Usually looks like id\tTitle\tAbstract -> We only need title and abstract
        String fullText = ncbiSplit[1]+" "+ncbiSplit[2];
        
        
        List<Result> resultList = api.processCitationsFromString(fullText);

        for (Result res : resultList) {
            try {
               
                for (Utterance utterance : res.getUtteranceList()) {
                    for (PCM pcm : utterance.getPCMList()) {
                        for (Mapping map : pcm.getMappingList()) {
                            for (Ev mapEv : map.getEvList()) {
                          
                                 result.addAnnotatedCUI(mapEv.getConceptId());
                              
                            }
                        }
                    }

                }
            } catch (Exception ex) {
                Logger.getLogger(MetaMapModel.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return result;
    
    }

}
