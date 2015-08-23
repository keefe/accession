package us.categorize.accession;

import java.io.IOException;
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
    public static void main( String[] args ) throws JsonParseException, JsonMappingException, InterruptedException, ExecutionException, TimeoutException, IOException
    {
        System.out.println( "Hello World!" );
        HackerNews news = new HackerNews();
        news.readTopStories();
    }
}
