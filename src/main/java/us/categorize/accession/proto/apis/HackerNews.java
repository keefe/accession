package us.categorize.accession.proto.apis;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class HackerNews {
	private String apiBase = "https://hacker-news.firebaseio.com/v0/";
	private HttpClient client;
	
	public HackerNews(){
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
	
	public void topStories(){
		String apiLoc = "topstories.json?print=pretty";
		try {
			get(apiLoc);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void get(String loc) throws InterruptedException, ExecutionException, TimeoutException{
		ContentResponse response = client.GET(apiBase+loc);
		System.out.println(response.getContentAsString());
	}
}
