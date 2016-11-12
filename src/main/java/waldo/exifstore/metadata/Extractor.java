package waldo.exifstore.metadata;

import java.io.IOException;
import java.io.InputStream;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;

public class Extractor {

	public static Metadata extract(InputStream stream) throws ImageProcessingException, IOException {
		Metadata metadata = ImageMetadataReader.readMetadata(stream);
		return metadata;
	}
}
