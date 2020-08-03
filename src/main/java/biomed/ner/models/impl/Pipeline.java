/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package biomed.ner.models.impl;

import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory;
import org.apache.ctakes.dictionary.lookup2.ae.*;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;


/**
 * Creates cTakes Pipeline
 * 
 */
public class Pipeline {	
        
        /**
         * Build cTakes processing pipeline
         * @return Pipeline
         * @throws Exception 
         */
	public static AggregateBuilder getAggregateBuilder() throws Exception {
		AggregateBuilder builder = new AggregateBuilder();
	        builder.add( ClinicalPipelineFactory.getTokenProcessingPipeline() );
	     // builder.add( AnalysisEngineFactory.createEngineDescription("src/main/resources/desc/ctakes-clinical-pipeline/desc/analysis_engine/AggregatePlaintextUMLSProcessor"));
	        
                builder.add( AnalysisEngineFactory.createEngineDescription( DefaultJCasTermAnnotator.class,
	               AbstractJCasTermAnnotator.PARAM_WINDOW_ANNOT_KEY,
	               "org.apache.ctakes.typesystem.type.textspan.Sentence",
	               JCasTermAnnotator.DICTIONARY_DESCRIPTOR_KEY,
	               "org/apache/ctakes/dictionary/lookup/fast/sno_rx_16ab.xml" )
	         );	
		return builder;
	}

}
