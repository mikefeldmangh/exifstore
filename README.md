
#### Current Functionality

This application performs the following:

1. Reads photos from a network store
  - Currently only implemented to read from an Amazon S3 bucket. The bucket name is "waldo-recruiting".
  - Could be extended to read from another type of network store.
  - There are a couple photos which appear to be private. This problem will be printed out and processing will continue.
1. Extracts the EXIF metadata from the files
  - This library was used: [https://github.com/drewnoakes/metadata-extractor](https://github.com/drewnoakes/metadata-extractor)
1. Writes the metadata to a local store
  - The only type of local store implemented so far is ElasticSearch.

#### Requirements to Run this Application

- Amazon S3 credentials
- ElasticSearch 5.0.0 installed and running
- JRE 1.8

#### To Run this Application

1. Run from an IDE such as Eclipse

Or

1. Build using maven.
1. This will create a zip file called waldo-exif-store-0.0.1-SNAPSHOT-deploy.zip
1. Copy this file to a directory and unzip it.
1. Go to the waldo-exif-store/bin directory.
1. Run the start\_exif\_store.sh file.

#### Problems

- The conversion to JSON data seems to have trouble converting a date in the metadata.
- ElasticSearch has a default field limit per doc index of 1000. After the first 1000 photos, nothing will be added to ES. Something more permanent should be put in place. But for now, after running the application once, you can increase the limit after the doc index has been created with the following at the command line and then run the application again:


```
curl -XPUT 'http://localhost:9200/waldo/_settings' -d '
{
    "index.mapping.total_fields.limit":5000
}'
```

#### Future improvements:

- Use Spring Framework to make the application more configurable.
- Use Log4j more and remove print statements.
- Use of queues so processing can be concurrent.
- Implement more types of local stores such as a relational database.
- If the EXIF directories are not necessary, they can be removed, flattening the data structure some.
- Add some unit tests.

#### To query data using ElasticSearch

Metadata is stored in ElasticSearch by docType, docIndex and docId. The docType is always "EXIF". The docIndex is always "waldo". The docId is the photo filename.

After running this application, you can query the data using REST calls. At the command line you can use curl. Or another application can be used to query the data using the ElasticSearch APIs. 

To see all data for a photo, run the following from the command line (the photo filename is at the end of the URL before the question mark):

    curl -XGET 'http://localhost:9200/waldo/EXIF/012c11ae-2ba7-4d9e-823e-a5b76c96a106.5a816864-d21b-4908-8dd2-ba9ffe4429a9.jpg?pretty'
    
To limit the output to just the data of one EXIF directory, you can tell ElasticSearch what to include from the _source field. For example to only see the "JPEG" information, run this:

    curl -XGET 'http://localhost:9200/waldo/EXIF/012c11ae-2ba7-4d9e-823e-a5b76c96a106.5a816864-d21b-4908-8dd2-ba9ffe4429a9.jpg?pretty&_source_include=JPEG'

The output will look something like this:

    {
      "_index" : "waldo",
      "_type" : "EXIF",
      "_id" : "012c11ae-2ba7-4d9e-823e-a5b76c96a106.5a816864-d21b-4908-8dd2-ba9ffe4429a9.jpg",
      "_version" : 2,
      "found" : true,
      "_source" : {
        "JPEG" : {
          "Component 3" : "Cr component: Quantization table 1, Sampling factors 1 horiz/1 vert",
          "Compression Type" : "Baseline",
          "Data Precision" : "8 bits",
          "Number of Components" : "3",
          "Component 2" : "Cb component: Quantization table 1, Sampling factors 1 horiz/1 vert",
          "Component 1" : "Y component: Quantization table 0, Sampling factors 2 horiz/1 vert",
          "Image Height" : "2008 pixels",
          "Image Width" : "3008 pixels"
        }
      }
    }

To restrict the data even more, you can add the field name or start of the field name you are looking for with a * as a wildcard:

    curl -XGET 'http://localhost:9200/waldo/EXIF/012c11ae-2ba7-4d9e-823e-a5b76c96a106.5a816864-d21b-4908-8dd2-ba9ffe4429a9.jpg?pretty&_source_include=JPEG.Data*'

Then you will only see "Data Precision" in the output:

    {
      "_index" : "waldo",
      "_type" : "EXIF",
      "_id" : "012c11ae-2ba7-4d9e-823e-a5b76c96a106.5a816864-d21b-4908-8dd2-ba9ffe4429a9.jpg",
      "_version" : 2,
      "found" : true,
      "_source" : {
        "JPEG" : {
          "Data Precision" : "8 bits"
        }
      } 
    }

    