package bg.sofia.uni.fmi.mjt.escaperoom.room;

public record Review(int rating, String reviewText) {
    public Review {
        if (rating < 0 || rating > 10 || reviewText == null || reviewText.length() > 200) {
            throw new IllegalArgumentException();
        }
    }

    public int getRating() {
        return rating;
    }
}
