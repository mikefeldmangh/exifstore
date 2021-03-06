package waldo.exifstore.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.drew.metadata.Metadata;

import waldo.exifstore.exifwriter.MetadataWriter;
import waldo.exifstore.metadata.Extractor;

/**
 * This class is the Amazon S3 implementation of PhotoReader.
 * 
 * @author michaelfeldman
 * @param <T>
 *
 */
public class S3PhotoProcessor<T> implements PhotoProcessor<T> {

	private String bucketName;
	
	private AmazonS3 s3client;
	
	private MetadataWriter writer;
	
	
	public S3PhotoProcessor(String bucketName) {
		this.bucketName = bucketName;
		s3client = new AmazonS3Client(new ProfileCredentialsProvider());
	}

	@Override
	public void setMetadataWriter(MetadataWriter writer) {
		this.writer = writer;
	}
	
	/**
	 * Reads photo data from AWS S3 bucket. Extracts the metadata from the photo files. Writes the metadata to a local store.
	 * Code to list keys taken from amazon example: <a href="http://docs.aws.amazon.com/AmazonS3/latest/dev/ListingObjectKeysUsingJava.html</a>
	 */
	@Override
	public void processPhotoData() {
		
		try {
            System.out.println("Listing objects");
            final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withMaxKeys(2);
            ListObjectsV2Result result;
            do {   
               result = s3client.listObjectsV2(req);
               
               for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                   System.out.println(" - " + objectSummary.getKey() + "  " +
                           "(size = " + objectSummary.getSize() + ")");
                   
                   // get object input stream
                   InputStream stream = null;
                   try {
                	   // Get the input stream containing the photo data
                	   stream = getObjectStream(objectSummary.getKey());
                	   
                	   // Extract the metadata from the photo
                	   Metadata metadata = Extractor.extract(stream);
                	   
                	   // Write the metadata to the store
                	   writer.writeMetadataToStore(objectSummary.getKey(), metadata);
                	   
                	   stream.close();
                   } catch (Exception e) {
                	   // Handle an access denied AmazonServiceException or any other type of exception on individual photos and then continue processing
                	   System.out.println("Caught an exception reading object with key: " + objectSummary.getKey());
                	   if (e instanceof AmazonServiceException) {
                		   System.out.println("Error Code: " + ((AmazonServiceException)e).getErrorCode());
                	   }
                	   continue;
                   }
               }
               System.out.println("Next Continuation Token : " + result.getNextContinuationToken());
               req.setContinuationToken(result.getNextContinuationToken());
            } while(result.isTruncated() == true ); 
            
         } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, " +
            		"which means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
            ase.printStackTrace();
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, " +
            		"which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
	}

	/**
	 * Given the key name, this method gets the input stream of the data with the key and the bucket name.
	 * @param key
	 * @return
	 * @throws IOException
	 */
	private InputStream getObjectStream(String key) throws IOException {
		S3Object object = s3client.getObject(new GetObjectRequest(bucketName, key));
		InputStream objectData = object.getObjectContent();
		return objectData;
	}
}
