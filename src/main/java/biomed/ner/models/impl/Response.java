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


import org.apache.ctakes.typesystem.type.refsem.UmlsConcept;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.cas.FSArray;


public class Response {
    public int begin;
    public int end;
    public String text;
    public int polarity;
    public List<Map<String,String>> conceptAttributes = new ArrayList<>();

    public Response(Annotation annotation){
        begin = annotation.getBegin();
        end = annotation.getEnd();
        text = annotation.getCoveredText();
        
        this.extractFeatures((FeatureStructure) annotation);
        


        if(annotation instanceof IdentifiedAnnotation) {
            IdentifiedAnnotation ia = (IdentifiedAnnotation) annotation;
            
            polarity = ia.getPolarity();
            if(ia.getOntologyConceptArr() != null) {
                for (UmlsConcept concept : JCasUtil.select(ia.getOntologyConceptArr(), UmlsConcept.class)) {
                    Map<String, String> atts = new HashMap<>();
                    atts.put("codingScheme", concept.getCodingScheme());
                    atts.put("cui", concept.getCui());
                    atts.put("prefText", concept.getPreferredText());
                    
                    conceptAttributes.add(atts);
                }
            }
        }
    }
    
    
    private void extractFeatures(FeatureStructure fs){
        List<?> plist = fs.getType().getFeatures();
        for (Object obj : plist) {
			if (obj instanceof Feature) {
				Feature feature = (Feature) obj;
				String val = "";
				if (feature.getRange().isPrimitive()) {
					val = fs.getFeatureValueAsString(feature);
				} else if (feature.getRange().isArray()) {
					// Flatten the Arrays
					FeatureStructure featval = fs.getFeatureValue(feature);
					if (featval instanceof FSArray) {
						FSArray valarray = (FSArray) featval;
						for (int i = 0; i < valarray.size(); ++i) {
							FeatureStructure temp = valarray.get(i);
							extractFeatures(temp);
						}
					}
				}
				if (feature.getName() != null
						&& val != null
						&& val.trim().length() > 0
						&& !"confidence".equalsIgnoreCase(feature
								.getShortName())) {
                                    System.out.println("FEATURE: "+feature.getShortName()+"|"+val);
					
				}
			}
		}
    }
    
    
    @Override
    public String toString(){
        return Integer.toString(this.begin)+"|"+Integer.toString(this.end)+"|"+Integer.toString(this.polarity)+"|"+this.text+"|"+this.conceptAttributes.toString();
    }
}
