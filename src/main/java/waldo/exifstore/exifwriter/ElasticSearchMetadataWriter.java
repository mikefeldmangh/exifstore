package waldo.exifstore.exifwriter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

/**
 * Elastic Search implementation of MetadataWriter.
 * This class will create a connection to Elastic Search with a given configuration.
 * It will also be able to write given metadata to Elastic Search.
 * 
 * @author michaelfeldman
 *
 */
public class ElasticSearchMetadataWriter implements MetadataWriter {

	public TransportClient client;
	
	public ElasticSearchMetadataWriter(String clusterName, String hostName, Integer port) {
		// Set up elastic search host settings
		Settings esSettings = Settings.builder()
				.put("cluster.name", clusterName)
				.build();
		
		// Open client connection
		try {
			client = new PreBuiltTransportClient(esSettings);
			client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostName), port));
			System.out.println("Created ES Client: " + client.toString());
		} catch (UnknownHostException e) {
			System.out.println("Unknown host when creating Elastic Search client: " + hostName);
		} catch (Exception e) {
			System.out.println("Exception when creating Elastic Search client.");
			e.printStackTrace();
		}
	}
	
	@Override
	public void writeMetadataToStore(String photoFilename, Metadata metadata) {
		
		Map<String, Map<String, String>> map = convertMetadataToMap(metadata);
		
		try {
			String jsonString = buildJsonFromMap(map);
			System.out.println(jsonString);
			
			
		} catch (Exception e) {
			System.out.println("Failed to save metadata for photo " + photoFilename);
			e.printStackTrace();
		}
	}

	private Map<String, Map<String, String>> convertMetadataToMap(Metadata metadata) {
		
		Map<String, Map<String, String>> resultMap = new HashMap<>();
		
		
		for (Directory directory : metadata.getDirectories()) {
			Map<String, String> tagMap = new HashMap<>();
			
			String directoryName = directory.getName();
			System.out.println("Directory: " + directoryName);
			for (Tag tag : directory.getTags()) {
		        System.out.println("Tag name = " + tag.getTagName() + ", Tag description = " + tag.getDescription());
		        tagMap.put(tag.getTagName(), tag.getDescription());
		    }
			resultMap.put(directoryName, tagMap);
		}
		
		return resultMap;
	}
	
	private String buildJsonFromMap (Map<String, Map<String, String>> mapToSave) throws IOException {
		
		XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
		
		for (Map.Entry<String, Map<String, String>> entry : mapToSave.entrySet()) {
			builder = builder.field(entry.getKey(), entry.getValue());
		}
		
		builder.endObject();
		
		return builder.string();
	}
}
