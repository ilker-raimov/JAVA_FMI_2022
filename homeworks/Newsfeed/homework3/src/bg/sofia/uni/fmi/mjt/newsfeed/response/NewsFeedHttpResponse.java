package bg.sofia.uni.fmi.mjt.newsfeed.response;

public class NewsFeedHttpResponse {
    private String status;
    private int totalResults;
    private Article[] articles;

    public String getStatus() {
        return status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public Article[] getArticles() {
        return articles;
    }
}
