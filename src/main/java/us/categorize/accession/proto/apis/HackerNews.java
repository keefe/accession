package us.categorize.accession.proto.apis;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HackerNews {
	private String apiBase = "https://hacker-news.firebaseio.com/v0/";
	private HttpClient client;
	private ObjectMapper mapper;

	public HackerNews(){
		mapper = new ObjectMapper();
		SslContextFactory sslContextFactory = new SslContextFactory();

		client = new HttpClient(sslContextFactory);
		try {
			client.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public String[] topStories() throws InterruptedException, ExecutionException, TimeoutException, JsonParseException, JsonMappingException, IOException{
		String topLoc = "topstories.json";
		ContentResponse response = client.GET(apiBase+topLoc);
		return mapper.readValue(response.getContentAsString(), String[].class);		
	}
	
	public void readTopStories() throws InterruptedException, ExecutionException, TimeoutException, JsonParseException, JsonMappingException, IOException{
		String top[] = topStories();
		for(String story : top){
			String response = client.GET(apiBase + "item/" + story + ".json").getContentAsString();
			System.out.println(response);
		}
	}
	private void get(String loc) throws InterruptedException, ExecutionException, TimeoutException, JsonParseException, JsonMappingException, IOException{
		ContentResponse response = client.GET(apiBase+loc);
		ObjectMapper mapper = new ObjectMapper();
		String newStories[] = mapper.readValue(response.getContentAsString(), String[].class);
		System.out.println("Total New Stories " + newStories.length + " first is " + newStories[0]);
		
	}
}
