/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ner.methods.reproduction;
import gov.nih.nlm.nls.ner.MetaMapLite;
import java.util.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.metamap.lite.types.Ev;
import bioc.BioCDocument;
import gov.nih.nlm.nls.metamap.document.FreeText;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        
        NERMethodsReproduction ner = new NERMethodsReproduction();
        try {
            ner.loadMetamapLite();
            
            String out = ner.annotateSentence("lung cancer bitches D008661 Inborn Errors of Metabolism");
            System.out.println(out);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(NERMethodsReproduction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(NERMethodsReproduction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(NERMethodsReproduction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(NERMethodsReproduction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NERMethodsReproduction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(NERMethodsReproduction.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
    }
    
         /**
     * This function loads the Metamap Lite instance before executing the queries.
     */
    
    private void loadMetamapLite() throws ClassNotFoundException, 
                                    InstantiationException, NoSuchMethodException, 
                                    IllegalAccessException, IOException
    {
        // Initialization Section
        
        Properties myProperties = new Properties();
        
        String mm_lite_folder = "/opt/MetaMap/public_mm_lite";
   
        // Select the 2020AA database
        
        myProperties.setProperty("metamaplite.index.directory", mm_lite_folder + "/data/ivf/2020AA/USAbase/");
        myProperties.setProperty("opennlp.models.directory", mm_lite_folder + "/data/models/");
        myProperties.setProperty("opennlp.en-pos.bin.path", mm_lite_folder + "/data/models/en-pos-maxent.bin");
        myProperties.setProperty("opennlp.en-sent.bin.path", mm_lite_folder + "/data/models/en-sent.bin");
        myProperties.setProperty("opennlp.en-token.bin.path", mm_lite_folder + "/data/models/en-token.bin");
        
       // myProperties.setProperty("metamaplite.sourceset", "MSH");



        m_metaMapLiteInst = new MetaMapLite(myProperties);
    }

       /**
     * This function annotates a sentence with CUI codes replacing 
     * keywords with codes in the same sentence.
     * 
     * 
     * @return 
     */
    
    private String annotateSentence(
            String sentence) throws InvocationTargetException, IOException, Exception
    {
        // Initialize the result
        
        String annotatedSentence = sentence;
        
        // Processing Section

 

        // Each document must be instantiated as a BioC document before processing
        
        BioCDocument document = FreeText.instantiateBioCDocument(sentence);
        
        // Proccess the document with Metamap
        
        List<Entity> entityList = m_metaMapLiteInst.processDocument(document);

 

        // For each keyphrase, select the first CUI candidate and replace in text.
        
        for (Entity entity: entityList) 
        {
            for (Ev ev: entity.getEvSet()) 
            {
                // Replace in text
                
                annotatedSentence = annotatedSentence.replaceAll(entity.getMatchedText(), ev.getConceptInfo().getCUI()+" "+ev.getConceptInfo().toString());
                
            }
        }
        
        // Return the result
        
        return annotatedSentence;
    }
}
