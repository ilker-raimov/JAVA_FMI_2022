package bg.sofia.uni.fmi.mjt.newsfeed.query;

import bg.sofia.uni.fmi.mjt.newsfeed.exceptions.ApiKeyNotSetException;
import bg.sofia.uni.fmi.mjt.newsfeed.exceptions.IllegalRequestArgumentsException;

import java.util.Set;

public class Query {
    private boolean hasCountryFilter;
    private String countryFilter;
    private boolean hasCategoryFilter;
    private String categoryFilter;
    private int pageSize;
    private boolean hasPage;
    private int page;
    private String apiKey;
    private Set<String> keywordsSet;

    private static final String AND = "&";
    private static final String KEYWORD_PREFIX = "q=";
    private static final String COUNTRY_PREFIX = "country=";
    private static final String CATEGORY_PREFIX = "category=";
    private static final String PAGE_SIZE_PREFIX = "pageSize=";
    private static final String PAGE_PREFIX = "page=";
    private static final String API_KEY_PREFIX = "apiKey=";

    protected Query(QueryBuilder queryBuilder) {
        this.hasCountryFilter = queryBuilder.getHasCountryFilter();
        this.countryFilter = queryBuilder.getCountryFilter();
        this.hasCategoryFilter = queryBuilder.getHasCategoryFilter();
        this.categoryFilter = queryBuilder.getCategoryFilter();
        this.pageSize = queryBuilder.getPageSize();
        this.hasPage = queryBuilder.getHasPage();
        this.page = queryBuilder.getPage();
        this.apiKey = queryBuilder.getApiKey();
        this.keywordsSet = queryBuilder.getKeywordsSet();
    }

    public String createQuery() throws ApiKeyNotSetException, IllegalRequestArgumentsException {
        StringBuilder toCreate = new StringBuilder("");

        if (apiKey == null || apiKey.isEmpty() || apiKey.isBlank()) {
            throw new ApiKeyNotSetException("API key has not been set");
        }

        if (keywordsSet.size() == 0) {
            throw new IllegalRequestArgumentsException("The request must have at least 1 keyword");
        }

        if (!hasCategoryFilter && !hasCountryFilter && keywordsSet.size() == 0) {
            throw new IllegalRequestArgumentsException("The request must have" +
                    "either a keyword, category filter or country filer");
        }

        for (String keyword : keywordsSet) {
            toCreate.append(KEYWORD_PREFIX).append(keyword).append(AND);
        }

        if (hasCountryFilter) {
            toCreate.append(COUNTRY_PREFIX).append(countryFilter).append(AND);
        }

        if (hasCategoryFilter) {
            toCreate.append(CATEGORY_PREFIX).append(categoryFilter).append(AND);
        }

        toCreate.append(PAGE_SIZE_PREFIX).append(pageSize).append(AND);

        if (hasPage) {
            toCreate.append(PAGE_PREFIX).append(page).append(AND);
        }

        toCreate.append(API_KEY_PREFIX).append(apiKey);

        return toCreate.toString();
    }
}
