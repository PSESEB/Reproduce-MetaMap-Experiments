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
package biomed.ner.datasets.impl;



import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Reads and parses i2b2 2008 dataset to the internal structure
 * @author Sebastian Hennig
 */
public class I2B22008Reader {
    
    
    /**
     * Storage of all labels
     */
    private Map<String,List<String>> labelData;
    
    //Datafiles in i2b2 2010 folders
    public List<File> dats;
    
    /**
     * Temporary Storage of Labels before parsing to internal structure
     */
    private Map<String,List<String>> intermediateAnnotations;
    
    
    private Map<String,String> entityToCUI;
    
    private Map<String,List<String>> entityToCUIs;
    
    /**
     * Parsed Input
     * where key identifies the document(document name) and value is the actual text
     */
    private Map<String,String> parsedInput;
    
    
    /**
     * Entity Types that should be considered in the label Set
     * Used to filter only relevant labels
     */
    private String[] entityTypes;
    
    public I2B22008Reader(){
        
        
          //Initialize
        this.parsedInput = new HashMap();
        this.intermediateAnnotations = new HashMap();
        
        //Mapping of labels in dataset to CUIs as mentioned in Rategui et al. (table 1 col 1
        entityToCUI = new HashMap();
        entityToCUI.put("Asthma", "C0004096");
        entityToCUI.put("CAD", "C1956346");
        entityToCUI.put("CHF", "C0018802");
        entityToCUI.put("Depression", "C0011581");
        entityToCUI.put("Diabetes", "C0011849");
        entityToCUI.put("Gallstones", "C0008320");
        entityToCUI.put("GERD", "C0017168");
        entityToCUI.put("Gout", "C0018099");
        entityToCUI.put("Hypercholesterolemia", "C0020443");
        entityToCUI.put("Hypertension", "C0020538");
        entityToCUI.put("OA", "C0029408");
        entityToCUI.put("OSA", "C0520679");
        entityToCUI.put("PVD", "C0085096");
        entityToCUI.put("Venous Insufficiency", "C0042485");
        
        //Mapping of labels in dataset to group of CUIs as mentioned in Rategui et al. table 1 col 2
        entityToCUIs = new HashMap();
        entityToCUIs.put("Asthma", Arrays.asList("C0004096"));
        entityToCUIs.put("CAD", Arrays.asList("C1956346","C0010054"));
        entityToCUIs.put("CHF", Arrays.asList("C0018802"));
        entityToCUIs.put("Depression", Arrays.asList("C0011570","C0011581"));
        entityToCUIs.put("Diabetes", Arrays.asList("C0011849","C0011854","C0011860"));
        entityToCUIs.put("Gallstones", Arrays.asList("C0008320","C0947622","C0008325","C0008350"));
        entityToCUIs.put("GERD", Arrays.asList("C0017168"));
        entityToCUIs.put("Gout", Arrays.asList("C0018099"));
        entityToCUIs.put("Hypercholesterolemia", Arrays.asList("C0020443","C0020473"));
        entityToCUIs.put("Hypertension", Arrays.asList("C0020538"));
        entityToCUIs.put("OA", Arrays.asList("C0029408"));
        entityToCUIs.put("OSA", Arrays.asList("C0520679"));
        entityToCUIs.put("PVD", Arrays.asList("C0085096"));
        entityToCUIs.put("Venous Insufficiency", Arrays.asList("C0042485","C0277919"));
        
        
    }


    public void loadDataset(String path, String file,String settings){
        
      
        
        //Save different Entity Types that should be scanned for
        String[] entityType;
        if(settings.isEmpty()){
            entityType = new String[0];
        }else{
            entityType = settings.split(",");
        }
        this.entityTypes = entityType;
        
       //create file with input
       File inputFile = new File(path);
       File labelFile = new File(file);
       
       List<String> relevantDocIds = new ArrayList();
       
       DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
           DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
           Document inputsXML = documentBuilder.parse(inputFile);
           Document labelsXML = documentBuilder.parse(labelFile);
          
           NodeList nl = labelsXML.getElementsByTagName("disease");
          for(int i = 0; i < nl.getLength(); i++){
              Node n = nl.item(i);
              
              String diseaseName = n.getAttributes().getNamedItem("name").getNodeValue();
              if(diseaseName.equals("Obesity")){
                  Element eElement = (Element) n;

                  NodeList selectDocs = eElement.getElementsByTagName("doc");
                  for(int j = 0; j < selectDocs.getLength(); j++){
                      Node doc = selectDocs.item(j);
                      if(doc.getAttributes().getNamedItem("judgment").getNodeValue().equals("Y")){
                          relevantDocIds.add(doc.getAttributes().getNamedItem("id").getNodeValue());
                      }
                  }
                 
              }
          }
           
        
        NodeList docList = inputsXML.getElementsByTagName("doc");
          for(int i = 0; i < docList.getLength(); i++){
              Node n = docList.item(i);
              
              if(relevantDocIds.contains(n.getAttributes().getNamedItem("id").getNodeValue())){
                  Element eElement = (Element) n;
                  
                  NodeList selectDocs = eElement.getElementsByTagName("text");
                  this.parsedInput.put(n.getAttributes().getNamedItem("id").getNodeValue(), selectDocs.item(0).getTextContent());
                 
              }
          }
          
          
          for(int i = 0; i < nl.getLength(); i++){
              Node n = nl.item(i);
              
              String diseaseName = n.getAttributes().getNamedItem("name").getNodeValue();
              if(entityToCUI.keySet().contains(diseaseName)){
                  Element eElement = (Element) n;

                  NodeList selectDocs = eElement.getElementsByTagName("doc");
                  for(int j = 0; j < selectDocs.getLength(); j++){
                      Node doc = selectDocs.item(j);
                      if(doc.getAttributes().getNamedItem("judgment").getNodeValue().equals("Y") && relevantDocIds.contains(doc.getAttributes().getNamedItem("id").getNodeValue())){
                          String id = doc.getAttributes().getNamedItem("id").getNodeValue();
                          if(this.intermediateAnnotations.get(diseaseName) == null){
                              List<String> arl = new ArrayList();
                              arl.add(id);
                              this.intermediateAnnotations.put(diseaseName, arl);
                          }else{
                              List<String> value = this.intermediateAnnotations.get(diseaseName);
                              value.add(id);
                              this.intermediateAnnotations.put(diseaseName, value);
                                     
                          }
                      }
                  }
                 
              }
          }
          
          
              } catch (ParserConfigurationException ex) {
            Logger.getLogger(I2B22008Reader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(I2B22008Reader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(I2B22008Reader.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
    }
    
    

    
    public void parseDataset() {
        
        //PARSE LABELS
        this.labelData = this.intermediateAnnotations;
        
       this.parsedInput.forEach((k,v) -> this.parsedInput.put(k, k+"\t\t"+v));
            
        
    }

    public Map<String, String> getInputData() {
            return this.parsedInput;
    }

    public Map<String,List<String>> getLabelData() {
        return this.labelData;
    }

    public Map<String, String> getEntityToCUI() {
        return entityToCUI;
    }

    public Map<String, List<String>> getEntityToCUIs() {
        return entityToCUIs;
    }
    
}
