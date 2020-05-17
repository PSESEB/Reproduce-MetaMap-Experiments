# Reproduce-MetaMap-Experiments

## Map DUI to CUI
https://ii.nlm.nih.gov/MRCOC/MRCOC_Doc_2016.pd  
`grep '|MSH|MH|' MRCONSO.RRF | grep "|ENG|" | cut -d'|' -f1,14,15 > MHcui`

## Map OMIM to CUI
`grep '|OMIM|' MRCONSO.RRF | grep "|ENG|" | cut -d'|' -f1,14,15 | grep -v "|MTHU" | grep -vE "\|[0-9]*\.[0-9]*\|" > OMIMcui`

## Map MSH CUI to CUI
`grep '|MSH|NM|' MRCONSO.RRF | grep "|ENG|" | cut -d'|' -f1,14,15 | grep -E '\|C[0-9]{1,6}\|' > MHCcui
`
