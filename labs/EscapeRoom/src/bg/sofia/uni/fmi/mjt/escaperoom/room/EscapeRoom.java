package bg.sofia.uni.fmi.mjt.escaperoom.room;

import bg.sofia.uni.fmi.mjt.escaperoom.rating.Ratable;

import java.util.Arrays;
import java.util.Objects;

public class EscapeRoom implements Ratable {
    private final String name;
    private final Theme theme;
    private final Difficulty difficulty;
    private final int maxTimeToEscape;
    private final double priceToPay;

    private Review[] reviews;
    private int reviewsSize;
    private final int maxReviewsCount;
    private int totalReviewsCount;

    private int ratingsSum;



    public EscapeRoom(String name, Theme theme, Difficulty difficulty, int maxTimeToEscape, double priceToPlay,
                      int maxReviewsCount) {
        this.name = name;
        this.theme = theme;
        this.difficulty = difficulty;
        this.maxTimeToEscape = maxTimeToEscape;
        this.priceToPay = priceToPlay;
        this.maxReviewsCount = maxReviewsCount;

        reviews = new Review[4];
        reviewsSize = 0;
        totalReviewsCount = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EscapeRoom that = (EscapeRoom) o;
        return maxTimeToEscape == that.maxTimeToEscape && Double.compare(that.priceToPay, priceToPay) == 0
                && maxReviewsCount == that.maxReviewsCount && name.equals(that.name) && theme == that.theme
                && difficulty == that.difficulty;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, theme, difficulty, maxTimeToEscape, priceToPay, maxReviewsCount);
    }

    @Override
    public double getRating() {
        if(totalReviewsCount == 0) {
            return 0;
        }

        return (double)ratingsSum / (double)totalReviewsCount;
    }

    public String getName() {
        return name;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getMaxTimeToEscape() {
        return maxTimeToEscape;
    }

    public Review[] getReviews() {

        Review[] cutReviews = Arrays.copyOf(reviews, reviewsSize);
        return cutReviews;
    }

    public void addReview(Review review) {
        if(review == null) {
            return;
        }

        ratingsSum += review.getRating();
        totalReviewsCount++;

        if(reviewsSize == maxReviewsCount) {
            rearrange();
            reviews[0] = review;

            return;
        }

        if(reviewsSize == reviews.length) {
            resizeReviews();
        }

        rearrange();
        reviews[0] = review;
        reviewsSize++;
    }

    private void rearrange() {
        int endIndex = reviewsSize;

        if(reviewsSize == maxReviewsCount) {
                endIndex--;
        }

        for(int i = endIndex; i > 0; i--) {
            reviews[i] = reviews[i - 1];
        }
    }

    private void resizeReviews() {
        int nextSize;

        if(reviews.length * 2 > maxReviewsCount) {
            nextSize = maxReviewsCount;
        }
        else {
            nextSize = reviewsSize * 2;
        }

        Review[] newReviews = new Review[nextSize];
        for(int i = 0; i < reviewsSize; i++) {
            newReviews[i] = reviews[i];
        }

        reviews = Arrays.copyOf(newReviews, newReviews.length);
    }
}
