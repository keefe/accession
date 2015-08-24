package us.categorize.accession;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
        BufferedReader reader = new BufferedReader(new FileReader(new File("/tmp/categorizeus")));
        String lastSession = reader.readLine();
        System.out.println("Connecting with session " + lastSession);
        cookie = new HttpCookie("JSESSIONID", lastSession);
//        cookie.setDomain("");
//        cookie.setMaxAge(123);
        HackerNews news = new HackerNews();
        news.readTopStories();
    }
    
   
}
