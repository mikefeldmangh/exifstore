package waldo.exifstore.processor;

import java.util.List;

/**
 * This interface defines the methods available for reading in photos from a store.
 * 
 * @author michaelfeldman
 *
 */
public interface PhotoReader<T> {

	List<T> readPhotoData();
}