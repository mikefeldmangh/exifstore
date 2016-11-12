package waldo.exifstore.exifwriter;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.drew.metadata.Metadata;

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
	public void writeMetadataToStore(Metadata metadata) {
		// TODO Auto-generated method stub
		
	}

}
