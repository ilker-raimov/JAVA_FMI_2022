package bg.sofia.uni.fmi.mjt.sentiment;

public enum SentimentType {
    UNK(-1.0, "unknown"),
    NGT(0.0, "negative"),
    SN(1.0, "somewhat negative"),
    NEUT(2.0, "neutral"),
    SP(3.0, "somewhat positive"),
    POS(4.0, "positive");

    private final double sentimentValue;
    private final String sentimentName;

    private SentimentType(Double value, String name) {
        this.sentimentValue = value;
        this.sentimentName = name;
    }

    public static String getName(double value) {
        for (SentimentType ST : SentimentType.values()) {
            if (ST.sentimentValue == value) {
                return ST.sentimentName;
            }
        }

        return null;
    }
}
