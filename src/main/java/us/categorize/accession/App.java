package us.categorize.accession;

import us.categorize.accession.proto.apis.HackerNews;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        HackerNews news = new HackerNews();
        news.topStories();
    }
}
