package bg.sofia.uni.fmi.mjt.newsfeed.query;

import bg.sofia.uni.fmi.mjt.newsfeed.exceptions.ApiKeyNotSetException;
import bg.sofia.uni.fmi.mjt.newsfeed.exceptions.IllegalRequestArgumentsException;
import bg.sofia.uni.fmi.mjt.newsfeed.filters.CategoryFilter;
import bg.sofia.uni.fmi.mjt.newsfeed.filters.CountryFilter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryTest {
    @Test
    void testApiMissing() {
        final String errorMessage1 = "Creating query with parameters should work correctly";

        final String wordToAdd = "word";
        final String fakeApiKey = "pretendImAnApiKey";
        final String toMatch = "q=word&country=bg&category=entertainment&pageSize=0&page=0&apiKey=pretendImAnApiKey";
        final int pageSizeAndIndex = 0;

        QueryBuilder queryBuilder = new QueryBuilder()
                .addKeyword(wordToAdd)
                .setPageSize(pageSizeAndIndex)
                .setPage(pageSizeAndIndex)
                .setCountryFilter(CountryFilter.BG)
                .setCategoryFilter(CategoryFilter.ENTERTAINMENT)
                .setApiKey(fakeApiKey);

        String result;
        Query query = queryBuilder.build();

        try {
            result = query.createQuery();
        } catch (ApiKeyNotSetException | IllegalRequestArgumentsException e) {
            throw new RuntimeException(e);
        }

        assertEquals(toMatch, result, errorMessage1);
    }

}
