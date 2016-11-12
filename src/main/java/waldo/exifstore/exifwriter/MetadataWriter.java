package waldo.exifstore.exifwriter;

import com.drew.metadata.Metadata;

/**
 * This interface defines the methods available for writing metadata to a store.
 * @author michaelfeldman
 *
 */
public interface MetadataWriter {

	/**
	 * Writes the given metadata to a local store. The local store will be configured in each implementation.
	 * @param metadata
	 */
	public void writeMetadataToStore(String photoFilename, Metadata metadata);
}
