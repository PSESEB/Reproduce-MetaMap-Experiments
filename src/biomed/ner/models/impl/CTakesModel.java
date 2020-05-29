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
import java.util.*;

import org.apache.ctakes.drugner.type.*;
import org.apache.ctakes.typesystem.type.textsem.*;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;


/**
 *
 * @author Sebastian Hennig
 */
public class CTakesModel implements iModel{
    
    
    public CTakesModel(JCas jcas){
         List<Class<? extends Annotation>> semClasses = new ArrayList<>();

   
        // CUI types:
        semClasses.add(DiseaseDisorderMention.class);
        semClasses.add(SignSymptomMention.class);
        semClasses.add(ProcedureMention.class);
        semClasses.add(AnatomicalSiteMention.class);
        semClasses.add(MedicationMention.class);

        // Temporal types:
        semClasses.add(TimeMention.class);
        semClasses.add(DateAnnotation.class);

        // Drug-related types:
        semClasses.add(FractionStrengthAnnotation.class);
        semClasses.add(DrugChangeStatusAnnotation.class);
        semClasses.add(StrengthUnitAnnotation.class);
        semClasses.add(StrengthAnnotation.class);
        semClasses.add(RouteAnnotation.class);
        semClasses.add(FrequencyUnitAnnotation.class);
        semClasses.add(MeasurementAnnotation.class);
        
  
     
    }

    @Override
    public AnnotatedStringDataPoint annotateText(String id, String text) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AnnotatedDataPoint annotateTextCUI(String id, String text) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
