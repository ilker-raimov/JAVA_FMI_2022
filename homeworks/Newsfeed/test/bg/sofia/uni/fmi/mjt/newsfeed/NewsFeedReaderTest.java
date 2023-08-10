package bg.sofia.uni.fmi.mjt.newsfeed;

import bg.sofia.uni.fmi.mjt.newsfeed.filters.CountryFilter;
import bg.sofia.uni.fmi.mjt.newsfeed.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

//instead of sending real http request testing should be done with stubs and mocks

public class NewsFeedReaderTest {
    private static final String API_KEY = "b3a8f077f93846ddac25b2e41e3f1b16";

    @Test
    void testGetPageByIndex() {
        final String errorMessage1 = "Http Responses should return the same as the requested";

        final String wordToAdd = "response";
        final int pageSize = 1;
        final int pageIndex = 1;
        final int pageCountExpected = 1;

        QueryBuilder queryBuilder = new QueryBuilder()
                .addKeyword(wordToAdd)
                .setPageSize(pageSize)
                .setCountryFilter(CountryFilter.US)
                .setApiKey(API_KEY);

        NewsFeedReader newsFeedReader = new NewsFeedReader();
        newsFeedReader.setQueryBuilder(queryBuilder);
        newsFeedReader.getPage(pageIndex);

        assertEquals(pageCountExpected, newsFeedReader.getPagesCount(), errorMessage1);
        assertEquals(pageSize, newsFeedReader.getPages().get(pageIndex).pageArticles().length, errorMessage1);
    }

    @Test
    void testGetAllNews() {
        final String errorMessage1 = "Http Responses should return the same as the requested";

        final String wordToAdd = "a";
        final int pageSize = 50;
        final int articleCountExpected = 35;

        QueryBuilder queryBuilder = new QueryBuilder()
                .addKeyword(wordToAdd)
                .setPageSize(pageSize)
                .setCountryFilter(CountryFilter.US)
                .setApiKey(API_KEY);

        NewsFeedReader newsFeedReader = new NewsFeedReader();
        newsFeedReader.setQueryBuilder(queryBuilder);
        newsFeedReader.getAllNews();

        assertEquals(articleCountExpected, newsFeedReader.getPages().get(1).pageArticles().length, errorMessage1);
    }

    @Test
    void getFirstNPages() {
        final String errorMessage1 = "Getting first N pages should return pages" +
                "with count equal or less to the requested one";

        final String wordToAdd = "a";
        final int pageSize = 10;
        final int pagesRequested = 5;
        final int pageCountExpected = 2;

        QueryBuilder queryBuilder = new QueryBuilder()
                .addKeyword(wordToAdd)
                .setPageSize(pageSize)
                .setCountryFilter(CountryFilter.US)
                .setApiKey(API_KEY);

        NewsFeedReader newsFeedReader = new NewsFeedReader();
        newsFeedReader.setQueryBuilder(queryBuilder);
        newsFeedReader.getFirstNPages(pagesRequested);

        assertEquals(pageCountExpected, newsFeedReader.getPages().size(), errorMessage1);
    }

}
