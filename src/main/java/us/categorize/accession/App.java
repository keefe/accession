package us.categorize.accession;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import us.categorize.accession.proto.apis.HackerNews;

/**
 * Hello world!
 *
 */
public class App 
{
	public static HttpCookie cookie;
    public static void main( String[] args ) throws JsonParseException, JsonMappingException, InterruptedException, ExecutionException, TimeoutException, IOException
    {
    	//to retrieve this value, loging to the server locally and check the logs
        System.out.println( "Hello World!" );
        cookie = new HttpCookie("JSESSIONID", "66F8DAC1864934806CC93874CF275806");
//        cookie.setDomain("");
//        cookie.setMaxAge(123);
        HackerNews news = new HackerNews();
        news.readTopStories();
    }
    
   
}
