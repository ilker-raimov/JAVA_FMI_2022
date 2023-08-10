package bg.sofia.uni.fmi.mjt.newsfeed.query;

import bg.sofia.uni.fmi.mjt.newsfeed.filters.CategoryFilter;
import bg.sofia.uni.fmi.mjt.newsfeed.filters.CountryFilter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueryBuilderTest {

    @Test
    void testAddKeywordNullWord() {
        final String errorMessage1 = "Adding null keyword should throw exception";
        QueryBuilder queryBuilder;

        assertThrows(IllegalArgumentException.class,
                () -> new QueryBuilder().addKeyword(null), errorMessage1);
    }

    @Test
    void testAddKeywordSameWords() {
        final String errorMessage1 = "Adding the same keyword again should throw exception";

        final String wordToAdd = "word";

        QueryBuilder queryBuilder;

        assertThrows(IllegalArgumentException.class, () -> new QueryBuilder()
                .addKeyword(wordToAdd).addKeyword(wordToAdd), errorMessage1);
    }

    @Test
    void testAddKeywordLegalArguments() {
        final String errorMessage1 = "Setting query builder parameters should work correctly";

        final String wordToAdd = "word";
        final String fakeApiKey = "ImAnApiKey";
        final int pageSizeAndIndex = 0;

        QueryBuilder queryBuilder = new QueryBuilder()
                .addKeyword(wordToAdd)
                .setPageSize(pageSizeAndIndex)
                .setPage(pageSizeAndIndex)
                .setCountryFilter(CountryFilter.BG)
                .setCategoryFilter(CategoryFilter.ENTERTAINMENT)
                .setApiKey(fakeApiKey);

        assertEquals(fakeApiKey, queryBuilder.getApiKey(), errorMessage1);
        assertEquals(pageSizeAndIndex, queryBuilder.getPageSize(), errorMessage1);
        assertTrue(queryBuilder.getKeywordsSet().contains(wordToAdd), errorMessage1);
        assertTrue(queryBuilder.getHasPage(), errorMessage1);
        assertTrue(queryBuilder.getHasCategoryFilter(), errorMessage1);
        assertTrue(queryBuilder.getHasCountryFilter(), errorMessage1);
    }
}
