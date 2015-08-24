package us.categorize.accession.proto.apis;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.categorize.accession.App;

public class HackerNews {
	private String apiBase = "https://hacker-news.firebaseio.com/v0/";
	private HttpClient client;
	private HttpClient categorizeUs;

	private ObjectMapper mapper;
	private static final String SESSIONID="608AFEF6FA27E13C22DEEB484FBAA585";
	private LinkedList<String> storyStack = new LinkedList<String>();
	private Set<String> seenStory = new HashSet<String>();
	private Map<String, String> external2internal = new HashMap<String, String>();
	
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
			storyStack.add(story);
		}
		readStack();
	}

	private void readStack() throws JsonParseException, JsonMappingException, InterruptedException, ExecutionException, TimeoutException, IOException {
		while(!storyStack.isEmpty())
			parseStory(storyStack.removeFirst());
	}

	private void parseStory(String story) throws InterruptedException, ExecutionException, TimeoutException,
			IOException, JsonParseException, JsonMappingException {
		if(seenStory.contains(story)) return;
		seenStory.add(story);
		String response = client.GET(apiBase + "item/" + story + ".json").getContentAsString();
		response.replaceAll("\"text\"", "\"body\"");
		Map<String,Object> map = mapper.readValue(response, 
			    new TypeReference<HashMap<String,Object>>(){});
//	String kids[] = mapper.readValue(map.get("kids")[0], String[].class);
		String kids[] = {};
		List kido = (List) map.get("kids");
		if(kido!=null){
			kids = new String[kido.size()];
			for(int i=0; i<kids.length;i++){
				kids[i] =kido.get(i).toString();
			}
		}
		String id = map.get("id").toString();
		String firstKid = kids.length>0?kids[0]:"";
		if(map.containsKey("parent")){
			String parent = map.get("parent").toString();
			String internalParent = external2internal.get(parent);
			if(internalParent!=null){
				response = response.replaceFirst("\\{", "{\"repliesTo\":\""+internalParent+"\",");
			}else{
				System.err.println(parent + " has no internal mapping but this is DFS so how that possible?");
			}
		}
		if(map.containsKey("title")){
			String title = map.get("title").toString();
			List<String> words = Arrays.asList(title.split(" "));
			Collections.sort(words, new Comparator<String>(){

				public int compare(String o1, String o2) {
					if(o1.length()<o2.length()) return 1;
					if(o1.length()>o2.length()) return -1;
					return 0;
				}
				
			});
			String tagString = "";
			for(int i=0; i<words.size();i++){
				tagString = tagString+=words.get(i).toLowerCase();
				if(i!=words.size()-1)
					tagString += " ";
			}
			response = response.replaceFirst("\\{", "{\"tagString\":\""+tagString+"\",");

		}
		for(String kid : kids){
			storyStack.addFirst(kid);
		}
		
		ContentResponse categorizeUsResponse =categorizeUs.POST("http://categorize.us:8080/thread")
										.content(new StringContentProvider(response), "application/json")
										.send();
		Map<String,Object> responseMap = mapper.readValue(categorizeUsResponse.getContentAsString(), 
			    new TypeReference<HashMap<String,Object>>(){});
		String newId = responseMap.get("_id").toString();
		external2internal.put(id, newId);
		System.out.println(map.get("id") +" aka " + newId + " is " + map.get("title")+ " has kids " + kids.length +" first one " + firstKid);

	}
	private void get(String loc) throws InterruptedException, ExecutionException, TimeoutException, JsonParseException, JsonMappingException, IOException{
		ContentResponse response = client.GET(apiBase+loc);
		ObjectMapper mapper = new ObjectMapper();
		String newStories[] = mapper.readValue(response.getContentAsString(), String[].class);
		System.out.println("Total New Stories " + newStories.length + " first is " + newStories[0]);
		
	}
}
