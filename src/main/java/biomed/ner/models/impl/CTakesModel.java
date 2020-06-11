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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.ctakes.typesystem.type.textsem.*;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;


/**
 *
 * @author Sebastian Hennig
 */
public class CTakesModel implements iModel{
    
     List<Class<? extends Annotation>> semClasses = new ArrayList<>();
     private AggregateBuilder aggregateBuilder;
     private AnalysisEngine pipeline;
    
    public CTakesModel(){
        
       
         try {
            aggregateBuilder = Pipeline.getAggregateBuilder();
            pipeline = aggregateBuilder.createAggregate();
         } catch (Exception ex) {
             Logger.getLogger(CTakesModel.class.getName()).log(Level.SEVERE, null, ex);
         }
         
          // CUI types:
             semClasses.add(DiseaseDisorderMention.class);
             semClasses.add(SignSymptomMention.class);
             semClasses.add(ProcedureMention.class);
             semClasses.add(AnatomicalSiteMention.class);
             semClasses.add(MedicationMention.class);
             semClasses.add(EntityMention.class);
             
    }
         
     

    
     public Map<String, List<Response>> parse(JCas jcas) throws Exception {

        Map<String, List<Response>> responseMap = new HashMap<>();
        for(Class<? extends Annotation> semClass : semClasses){
            List<Response> annotations = new ArrayList<>();
            for(Annotation annot : JCasUtil.select(jcas, semClass)){
                Response response = new Response(annot);
                annotations.add(response);
            }
            responseMap.put(semClass.getSimpleName(), annotations);
        }
        return responseMap;
    }

    @Override
    public AnnotatedStringDataPoint annotateText(String id, String text) {
        
         //Create empty result set to be filled with suggested labels of Meta Map Lite
        AnnotatedStringDataPoint result = new AnnotatedStringDataPoint(id);
        
        //Parse NCBI tab seperated text to one single text
        String[] ncbiSplit = text.split("\t");
        //Usually looks like id\tTitle\tAbstract -> We only need title and abstract
        String fullText = ncbiSplit[1]+" "+ncbiSplit[2];
        
        //Create Map to store output of cTakes
         Map<String, List<Response>> resultMap;
        try {
            //Create cTakes Pipeline
            JCas jcas = pipeline.newJCas();
            //Load text to pipeline
	    jcas.setDocumentText(fullText);
            //Process document
            pipeline.process(jcas);
            //Parse the results
            resultMap = parse(jcas);
            jcas.reset();
             
         //Get relevant 
         List<Response> found  = resultMap.get("DiseaseDisorderMention");
         for(Response r : found){
             AtomStringLabel asl = new AtomStringLabel(r.getText(), r.getBegin(), r.getEnd());
             result.addConcept(asl);
         }
         
         } catch (AnalysisEngineProcessException ex) {
             Logger.getLogger(CTakesModel.class.getName()).log(Level.SEVERE, null, ex);
         } catch (Exception ex) {
             Logger.getLogger(CTakesModel.class.getName()).log(Level.SEVERE, null, ex);
         }

         return result;
    }

    @Override
    public AnnotatedDataPoint annotateTextCUI(String id, String text) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
