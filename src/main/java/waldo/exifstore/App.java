package waldo.exifstore;

import java.util.List;

import waldo.exifstore.exifwriter.ElasticSearchMetadataWriter;
import waldo.exifstore.processor.PhotoProcessor;
import waldo.exifstore.processor.S3PhotoProcessor;

/**
 * This application reads in photos from an S3 network store, parses the EXIF data from
 * the photos and writes the EXIF information to a local Elastic Search store.
 * 
 * In the future the application can be expanded to include the ability to read from and write to
 * other types of stores.
 *
 */
public class App 
{
	// Move these to configuration when Spring Framework is added.
	
	public static String BUCKET = "waldo-recruiting";
	
	public static String ES_CLUSTER_NAME = "elasticsearch";
	
	public static String ES_HOSTNAME = "localhost";
	
	public static int ES_PORT = 9300;
	
	
    public static void main( String[] args )
    {
    	ElasticSearchMetadataWriter writer = new ElasticSearchMetadataWriter(ES_CLUSTER_NAME, ES_HOSTNAME, ES_PORT);
    	
    	PhotoProcessor<Object> processor = new S3PhotoProcessor<Object>(BUCKET);
    	processor.setMetadataWriter(writer);
        
        List<Object> photoList = processor.processPhotoData();
    }
}
