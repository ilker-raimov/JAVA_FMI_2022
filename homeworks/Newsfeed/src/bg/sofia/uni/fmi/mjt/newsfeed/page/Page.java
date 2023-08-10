package bg.sofia.uni.fmi.mjt.newsfeed.page;

import bg.sofia.uni.fmi.mjt.newsfeed.exceptions.EmptyPageException;
import bg.sofia.uni.fmi.mjt.newsfeed.response.Article;

public record Page(Article[] pageArticles) {

    private static final String WHITESPACE = " ";

    public void print() throws EmptyPageException {
        if (pageArticles == null || pageArticles.length == 0) {
            throw new EmptyPageException("Cannot print empty page");
        }

        for (Article article : pageArticles) {
            System.out.println(article.getTitle() + WHITESPACE + article.getAuthor());
        }
    }
}
