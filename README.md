# Reproduce Biomedical NER Experiments

This repro allows to reproduce the Experiments carried out in the following publications:  
[MetaMap Lite](https://academic.oup.com/jamia/article/24/4/841/2961848)  
[Comparison of MetaMap and cTAKES for entity extraction in clinical notes](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC6157281/)   
  
The reproduction protocol and the docker container can be found here. <- to do insert linke

## Dependencies
To run the maven build successfully you need a complete MetaMap, MetaMap Lite and cTAKES installation.
* MetaMap installation instructions can be found [here](https://metamap.nlm.nih.gov/JavaApi.shtml). Once MetaMap is installed you need to start the server so the java API can access MetaMap as explained [here](https://metamap.nlm.nih.gov/Docs/README_javaapi.shtml).

* MetaMap Lite installation instructions can be found [here](https://metamap.nlm.nih.gov/MetaMapLite.shtml). Originally it was installed under `/opt/MetaMap/public_mm_lite`. If an other installation folder is chosen, the paths need to be updated in the [ExperimentFactory.java](src/main/java/biomed/ner/evaluation/ExperimentFactory.java) file.

* cTAKES installation instructions can be found [here](https://cwiki.apache.org/confluence/display/CTAKES/cTAKES+4.0+User+Install+Guide). After the installation a link to the cTAKES resources needs to be created in the repo as seen [here](src/main/resources).


## Datasets

For the necessary datasets we refer to the reproduction protocol, that links to a docker-container that contains the NCBI and LHC datasets.
The NCBI and LHC datasets can be extracted from the docker-container. Depending on where they are stored, the dataset paths in [ExperimentFactory.java](src/main/java/biomed/ner/evaluation/ExperimentFactory.java) need to be updated.  
The reproduction-protocol also contains instructions on how to get access to the other datasets. 

[//]: # "## Map DUI to CUI"
[//]: # "https://ii.nlm.nih.gov/MRCOC/MRCOC_Doc_2016.pd " 
[//]: # "`grep '|MSH|MH|' MRCONSO.RRF | grep |ENG| | cut -d'|' -f1,14,15 > MHcui`"

[//]: # "## Map OMIM to CUI"
[//]: # "`grep '|OMIM|' MRCONSO.RRF | grep |ENG| | cut -d'|' -f1,14,15 | grep -v |MTHU | grep -vE \|[0-9]*\.[0-9]*\| > OMIMcui`"

[//]: # "## Map MSH CUI to CUI"
[//]: # "`grep '|MSH|NM|' MRCONSO.RRF | grep |ENG| | cut -d'|' -f1,14,15 | grep -E '\|C[0-9]{1,6}\|' > MHCcui"

[//]: # "# TODOS"

[//]: # "Snomed Resources `org/apache/ctakes/dictionary/lookup/fast/sno_rx_16ab.xml`"
[//]: # "needs to be changed line :  `<property key=jdbcUrl value=jdbc:hsqldb:file:src/main/resources/org/apache/ctakes/dictionary/lookup/fast/sno_rx_16ab/sno_rx_16ab/>`"
