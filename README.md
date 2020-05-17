# Reproduce-MetaMap-Experiments

## Map DUI to CUI
https://ii.nlm.nih.gov/MRCOC/MRCOC_Doc_2016.pd  
grep '|MSH|MH|' MRCONSO.RRF | grep "|ENG|" | cut -d'|' -f1,14,15 > MHcui

