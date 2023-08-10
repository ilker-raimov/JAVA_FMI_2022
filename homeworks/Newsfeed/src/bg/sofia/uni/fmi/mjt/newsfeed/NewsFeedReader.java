package bg.sofia.uni.fmi.mjt.newsfeed;

import bg.sofia.uni.fmi.mjt.newsfeed.exceptions.ApiKeyNotSetException;
import bg.sofia.uni.fmi.mjt.newsfeed.exceptions.EmptyPageException;
import bg.sofia.uni.fmi.mjt.newsfeed.exceptions.IllegalRequestArgumentsException;
import bg.sofia.uni.fmi.mjt.newsfeed.exceptions.PageDoesNotExistException;
import bg.sofia.uni.fmi.mjt.newsfeed.exceptions.RequestNotMadeException;
import bg.sofia.uni.fmi.mjt.newsfeed.exceptions.UnsuccessfulRequestException;
import bg.sofia.uni.fmi.mjt.newsfeed.page.Page;
import bg.sofia.uni.fmi.mjt.newsfeed.query.Query;
import bg.sofia.uni.fmi.mjt.newsfeed.query.QueryBuilder;
import bg.sofia.uni.fmi.mjt.newsfeed.response.Article;
import bg.sofia.uni.fmi.mjt.newsfeed.response.NewsFeedHttpResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class NewsFeedReader {
    private QueryBuilder queryBuilderToRequest;
    private String stringFormQuery;
    private URI uri;
    private final Gson gsonParser;
    private boolean hasRequestBeenMade;
    private boolean isRequestSuccessful;
    private Map<Integer, Page> pages;
    private int currentPageIndex;

    private static final String SCHEME = "https";
    private static final String AUTHORITY = "newsapi.org";
    private static final String PATH = "/v2/top-headlines";
    private static final int MIN_PAGES_COUNT = 1;


    public NewsFeedReader() {
        this.queryBuilderToRequest = null;
        this.stringFormQuery = null;
        this.uri = null;
        this.gsonParser = new Gson();
        this.hasRequestBeenMade = false;
        this.isRequestSuccessful = false;
        this.pages = new HashMap<>();
        this.currentPageIndex = MIN_PAGES_COUNT;
    }

    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilderToRequest = queryBuilder;
        this.stringFormQuery = null;
        this.uri = null;
        this.hasRequestBeenMade = false;
        this.isRequestSuccessful = false;
        this.pages = new HashMap<>();
        this.currentPageIndex = MIN_PAGES_COUNT;
    }

    public void showCurrentPage() throws UnsuccessfulRequestException,
            PageDoesNotExistException, RequestNotMadeException {
        if (!hasRequestBeenMade) {
            throw new RequestNotMadeException("Request has not been made yet");
        }

        if (!isRequestSuccessful) {
            throw new UnsuccessfulRequestException("The request with the current query has been unsuccessful");
        }

        if (!pages.containsKey(currentPageIndex)) {
            throw new PageDoesNotExistException("Page does not exist");
        }

        try {
            pages.get(currentPageIndex).print();
        } catch (EmptyPageException e) {
            e.printStackTrace();
        }
    }

    public void showNextPage() throws PageDoesNotExistException,
            UnsuccessfulRequestException, RequestNotMadeException {
        currentPageIndex++;

        showCurrentPage();
    }

    public void showPreviousPage() throws PageDoesNotExistException,
            UnsuccessfulRequestException, RequestNotMadeException {
        if (currentPageIndex == MIN_PAGES_COUNT) {
            throw new PageDoesNotExistException("Page does not exist");
        }

        currentPageIndex--;

        showCurrentPage();
    }

    public int getPagesCount() {
        return pages.size();
    }

    public Map<Integer, Page> getPages() {
        return pages;
    }

    private String getPageByIndex(int pageIndex) {
        String result;
        HttpRequest httpRequest;
        HttpClient httpClient = HttpClient.newBuilder().build();
        Query queryToRequest = queryBuilderToRequest.setPage(pageIndex).build();

        try {
            stringFormQuery = queryToRequest.createQuery();
        } catch (ApiKeyNotSetException | IllegalRequestArgumentsException e) {
            throw new RuntimeException(e);
        }

        try {
            uri = new URI(SCHEME, AUTHORITY, PATH, stringFormQuery, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        httpRequest = HttpRequest.newBuilder().uri(uri).build();

        try {
            result = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public void getAllNews() {
        hasRequestBeenMade = true;

        int pageIndexer = MIN_PAGES_COUNT;
        NewsFeedHttpResponse response = null;

        while (true) {
            if (pages.containsKey(pageIndexer)) {
                pageIndexer++;

                continue;
            }

            response = gsonParser.fromJson(getPageByIndex(pageIndexer), NewsFeedHttpResponse.class);

            Article[] articles = response.getArticles();

            if (articles.length == 0) {
                break;
            }

            pages.put(pageIndexer, new Page(articles));

            isRequestSuccessful = true;

            pageIndexer++;

            if (articles.length < queryBuilderToRequest.getPageSize()) {
                break;
            }
        }
    }

    public void getFirstNPages(int count) {
        if (count <= MIN_PAGES_COUNT) {
            throw new IllegalArgumentException("Count of first N pages to get should be higher than 0");
        }

        getPagesFromTo(MIN_PAGES_COUNT, count);
    }

    public void getPage(int index) {
        if (index < MIN_PAGES_COUNT) {
            throw new IllegalArgumentException("Getting page by index should get index higher than 0");
        }

        getPagesFromTo(index, index);
    }

    //end index is included, set of page indexes : [startIndex; endIndex]
    public void getPagesFromTo(int startIndex, int endIndex) {
        if (startIndex < MIN_PAGES_COUNT || endIndex < MIN_PAGES_COUNT) {
            throw new IllegalArgumentException("Illegal page indexes");
        }

        if (startIndex > endIndex) {
            throw new IllegalArgumentException("Page start index is after end index");
        }

        hasRequestBeenMade = true;

        NewsFeedHttpResponse response;

        for (int pageIndexer = startIndex; pageIndexer <= endIndex; pageIndexer++) {
            if (pages.containsKey(pageIndexer)) {
                pageIndexer++;

                continue;
            }

            response = gsonParser.fromJson(getPageByIndex(pageIndexer), NewsFeedHttpResponse.class);

            Article[] articles = response.getArticles();

            if (articles.length == 0) {
                break;
            }

            pages.put(pageIndexer, new Page(articles));

            isRequestSuccessful = true;

            pageIndexer++;

            if (articles.length < queryBuilderToRequest.getPageSize()) {
                break;
            }
        }
    }
}
