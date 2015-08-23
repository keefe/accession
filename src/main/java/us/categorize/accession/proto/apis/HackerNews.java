package us.categorize.accession.proto.apis;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.categorize.accession.App;

public class HackerNews {
	private String apiBase = "https://hacker-news.firebaseio.com/v0/";
	private HttpClient client;
	private HttpClient categorizeUs;

	private ObjectMapper mapper;
	private static final String SESSIONID="608AFEF6FA27E13C22DEEB484FBAA585";

	public HackerNews(){
		mapper = new ObjectMapper();
		SslContextFactory sslContextFactory = new SslContextFactory();

		client = new HttpClient(sslContextFactory);
		
		categorizeUs = new HttpClient(sslContextFactory);
		try {
			client.start();
			categorizeUs.start();
			categorizeUs.getCookieStore().add(URI.create("http://categorize.us:8080"), App.cookie);

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
			response.replaceAll("\"text\"", "\"body\"");
			ContentResponse categorizeUsResponse =categorizeUs.POST("http://categorize.us:8080/thread")
											.content(new StringContentProvider(response), "application/json")
											.send();
			System.out.println(response+ " ||| " + categorizeUsResponse.getContentAsString());
		}
	}
	private void get(String loc) throws InterruptedException, ExecutionException, TimeoutException, JsonParseException, JsonMappingException, IOException{
		ContentResponse response = client.GET(apiBase+loc);
		ObjectMapper mapper = new ObjectMapper();
		String newStories[] = mapper.readValue(response.getContentAsString(), String[].class);
		System.out.println("Total New Stories " + newStories.length + " first is " + newStories[0]);
		
	}
}
