# Reproduce-MetaMap-Experiments

This repro allows to reproduce the Experiments carried out in the following publications:  
[MetaMap Lite](https://academic.oup.com/jamia/article/24/4/841/2961848)  
[Comparison of MetaMap and cTAKES for entity extraction in clinical notes](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC6157281/)    

[//]: # "## Map DUI to CUI"
[//]: # "https://ii.nlm.nih.gov/MRCOC/MRCOC_Doc_2016.pd " 
[//]: # "`grep '|MSH|MH|' MRCONSO.RRF | grep |ENG| | cut -d'|' -f1,14,15 > MHcui`"

[//]: # "## Map OMIM to CUI"
[//]: # "`grep '|OMIM|' MRCONSO.RRF | grep |ENG| | cut -d'|' -f1,14,15 | grep -v |MTHU | grep -vE \|[0-9]*\.[0-9]*\| > OMIMcui`"

[//]: # "## Map MSH CUI to CUI"
[//]: # "`grep '|MSH|NM|' MRCONSO.RRF | grep |ENG| | cut -d'|' -f1,14,15 | grep -E '\|C[0-9]{1,6}\|' > MHCcui"
`

[//]: # "# TODOS"

[//]: # "Snomed Resources `org/apache/ctakes/dictionary/lookup/fast/sno_rx_16ab.xml`"
[//]: # "needs to be changed line :  `<property key=jdbcUrl value=jdbc:hsqldb:file:src/main/resources/org/apache/ctakes/dictionary/lookup/fast/sno_rx_16ab/sno_rx_16ab/>`"
