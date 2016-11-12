package waldo.exifstore.processor;

import waldo.exifstore.exifwriter.MetadataWriter;

/**
 * This interface defines the methods available for reading in photos from a store.
 * 
 * @author michaelfeldman
 *
 */
public interface PhotoProcessor<T> {

	void processPhotoData();
	
	void setMetadataWriter(MetadataWriter writer);

}
