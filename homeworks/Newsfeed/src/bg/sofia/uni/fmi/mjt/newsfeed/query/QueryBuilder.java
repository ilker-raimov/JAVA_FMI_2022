package bg.sofia.uni.fmi.mjt.newsfeed.query;

import bg.sofia.uni.fmi.mjt.newsfeed.filters.CategoryFilter;
import bg.sofia.uni.fmi.mjt.newsfeed.filters.CountryFilter;

import java.util.HashSet;
import java.util.Set;

public class QueryBuilder {
    private boolean hasCountryFilter;
    private String countryFilter;
    private boolean hasCategoryFilter;
    private String categoryFilter;
    private int pageSize;
    private boolean hasPage;
    private int page;
    private String apiKey;
    private Set<String> keywordsSet;

    private static final int DEFAULT_PAGE_SIZE = 50;

    public QueryBuilder() {
        this.hasCountryFilter = false;
        this.hasCategoryFilter = false;
        this.hasPage = false;

        this.apiKey = null;
        this.countryFilter = null;
        this.categoryFilter = null;
        this.pageSize = DEFAULT_PAGE_SIZE;
        this.page = 0;

        this.keywordsSet = new HashSet<>();
    }

    public QueryBuilder addKeyword(String keywordToAdd) {
        if (keywordToAdd == null || keywordToAdd.isEmpty() || keywordToAdd.isBlank()) {
            throw new IllegalArgumentException("Keyword is either null or empty/blank");
        }

        if (keywordsSet.contains(keywordToAdd.toLowerCase())) {
            throw new IllegalArgumentException("Keyword is already in the set of keywords to add"); //change exception ?
        }

        keywordsSet.add(keywordToAdd.toLowerCase());

        return this;
    }

    public QueryBuilder setCountryFilter(CountryFilter countryToAdd) {
        this.countryFilter = countryToAdd.name().toLowerCase();
        this.hasCountryFilter = true;

        return this;
    }

    public QueryBuilder setCategoryFilter(CategoryFilter categoryToAdd) {
        this.categoryFilter = categoryToAdd.name().toLowerCase();
        this.hasCategoryFilter = true;

        return this;
    }

    public QueryBuilder setPageSize(int pageSize) {
        this.pageSize = pageSize;

        return this;
    }

    public QueryBuilder setPage(int page) {
        this.page = page;
        this.hasPage = true;

        return this;
    }

    public QueryBuilder setApiKey(String apiKey) {
        this.apiKey = apiKey;

        return this;
    }

    public boolean getHasCountryFilter() {
        return hasCountryFilter;
    }

    public String getCountryFilter() {
        return countryFilter;
    }

    public boolean getHasCategoryFilter() {
        return hasCategoryFilter;
    }

    public String getCategoryFilter() {
        return categoryFilter;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean getHasPage() {
        return hasPage;
    }

    public int getPage() {
        return page;
    }

    public String getApiKey() {
        return apiKey;
    }

    public Set<String> getKeywordsSet() {
        return keywordsSet;
    }

    public Query build() {
        return new Query(this);
    }
}
