# JRuby Testing


[![GitHub tag](https://img.shields.io/github/tag/interlok-testing/testing_elastic.svg)](https://github.com/interlok-testing/testing_elastic/tags)
[![license](https://img.shields.io/github/license/interlok-testing/testing_elastic.svg)](https://github.com/interlok-testing/testing_elastic/blob/develop/LICENSE)
[![Actions Status](https://github.com/interlok-testing/testing_elastic/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/interlok-testing/testing_elastic/actions)

## What it does
The adapter demonstrates how to index csv, csv with geo point, json documents in single as well as bulk operation mode.  
The list of endpoints is as follows:

| Method | Endpoint | Description |                                                     
| --- |-----------------------------------------------------------------|---|
| GET | /elastic/simple/json?message=test                               | Indexes a JSON document with the supplied message |
| GET | /elastic/simple/csv?message=test | Indexes a CSV document with the supplied message |
| GET | /elastic/simple/csv-geo?lat=33.87&lon=151.21 | Indexes a CSV document with the supplied latitude and longitude | 
| GET | /elastic/simple/csv-bulk | Bulk indexes multiple lines of a CSV document [^sample] |
| DELETE | /elastic/sdk?index=simple-csv-geo&id=csvgeo-1 | Delete an existing document |                               
| PUT | /elastic/sdk?index=simple-json&message=this is a new message&id=<id> | Update an existing document |                                     

[^sample]:
```
    productuniqueid,productname,crop,productcategory,applicationweek,operationdate,manufacturer,applicationrate,measureunit,growthstagecode,iscanonical,latitude,longitude,recordid,id
    UID-1,*A Simazine,,Insecticides,48,20051122,,1.5,Litres per Hectare,,0,,,5,1
    UID-2,*Axial,,Herbicides,15,20100408,,0.25,Litres per Hectare,,0,,,6,6
    UID-3,*Betanal Maxxim,,Herbicides,18,20130501,,0.07,Litres per Hectare,,0,,,21,21
    UID-4,24-D Amine,Passion Fruit,Herbicides,19,20080506,,2.8,Litres per Hectare,,0,53.37969768091292,-0.18346963126415416,210,209
    UID-5,26N35S,Rape Winter,Fungicides,12,20150314,,200,Kilograms per Hectare,,0,52.71896363632868,-1.2391368098336788,233,217

## Getting Started
An instance of elastic search server is required and the adapter's bootstrap.properties needs to be configured to point to it.
```
elastic.url1=http://elasticsearch-1:9200
```
The functional tests are implemented using a Docker compose file.