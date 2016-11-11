package waldo.exifstore;

import java.util.List;

import waldo.exifstore.photoreader.PhotoReader;
import waldo.exifstore.photoreader.S3PhotoReader;

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
	// Move this to configuration when Spring Framework is added.
//	public static String BUCKET = "http://s3.amazonaws.com/waldo-recruiting";
	public static String BUCKET = "waldo-recruiting";
	
    public static void main( String[] args )
    {
        PhotoReader<Object> reader = new S3PhotoReader<Object>(BUCKET);
        
        List<Object> photoList = reader.readPhotoData();
    }
}
