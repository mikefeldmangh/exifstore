package waldo.exifstore;

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
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}
