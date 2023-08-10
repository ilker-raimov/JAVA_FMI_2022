package bg.sofia.uni.fmi.mjt.sentiment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MovieReviewSentimentAnalyzerTest {

    //can be read from a file instead
    private static final String toParse = "1 baba dqdo\n" +
            "2 baba chicho\n" +
            "3 dqdo lelq\n" +
            "4 baba lelq bulka\n";
    private static final String stopwords = "a\n" + "about\n" + "above\n" + "after\n" + "again\n" +
            "against\n" + "all\n" + "am\n" + "an\n" + "and\n" + "any\n" + "are\n" + "aren't\n" +
            "as\n" + "at\n" + "be\n" + "because\n" + "been\n" + "before\n" + "being\n" + "below\n" +
            "between\n" + "both\n" + "but\n" + "by\n" + "can't\n" + "cannot\n" + "could\n" +
            "couldn't\n" + "did\n" + "didn't\n" + "do\n" + "does\n" + "doesn't\n" + "doing\n" +
            "don't\n" + "down\n" + "during\n" + "each\n" + "few\n" + "for\n" + "from\n" +
            "further\n" + "had\n" + "hadn't\n" + "has\n" + "hasn't\n" + "have\n" + "haven't\n" +
            "having\n" + "he\n" + "he'd\n" + "he'll\n" + "he's\n" + "her\n" + "here\n" + "here's\n" +
            "hers\n" + "herself\n" + "him\n" + "himself\n" + "his\n" + "how\n" + "how's\n" +
            "i\n" + "i'd\n" + "i'll\n" + "i'm\n" + "i've\n" + "if\n" + "in\n" +
            "into\n" + "is\n" + "isn't\n" + "it\n" + "it's\n" + "its\n" + "itself\n" + "let's\n" +
            "me\n" + "more\n" + "most\n" + "mustn't\n" + "my\n" + "myself\n" + "no\n" + "nor\n" +
            "not\n" + "of\n" + "off\n" + "on\n" + "once\n" + "only\n" + "or\n" + "other\n" + "ought\n" +
            "our\n" + "ours\n" + "ourselves\n" + "out\n" + "over\n" + "own\n" + "same\n" + "shan't\n" +
            "she\n" + "she'd\n" + "she'll\n" + "she's\n" + "should\n" + "shouldn't\n" + "so\n" + "some\n" +
            "such\n" + "than\n" + "that\n" + "that's\n" + "the\n" + "their\n" + "theirs\n" + "them\n" +
            "themselves\n" + "then\n" + "there\n" + "there's\n" + "these\n" + "they\n" + "they'd\n" +
            "they'll\n" + "they're\n" + "they've\n" + "this\n" + "those\n" + "through\n" + "to\n" + "too\n" +
            "under\n" + "until\n" + "up\n" + "very\n" + "was\n" + "wasn't\n" + "we\n" + "we'd\n" + "we'll\n" +
            "we're\n" + "we've\n" + "were\n" + "weren't\n" + "what\n" + "what's\n" + "when\n" +
            "when's\n" + "where\n" + "where's\n" + "which\n" + "while\n" + "who\n" + "who's\n" + "whom\n" +
            "why\n" + "why's\n" + "with\n" + "won't\n" + "would\n" + "wouldn't\n" + "you\n" + "you'd\n" +
            "you'll\n" + "you're\n" + "you've\n" + "your\n" + "yours\n" + "yourself\n" + "yourselves";

    private static StringReader sentimentSR;
    private static StringReader stopwordsSR;

    @BeforeEach
    void setUpEach() {
        sentimentSR = new StringReader(toParse);
        stopwordsSR = new StringReader(stopwords);
    }

    @Test
    void testGetReviewSentimentAsName() {
        final String check1 = "lelq";
        final String check2 = "dqdo chicho bulka";
        final String check3 = "unknownWord and anotherUnknownWord";
        final String result1 = "positive";
        final String result2 = "somewhat positive";
        final String result3 = "unknown";
        final String errorMessageTest1 = "Reviewing text of review should return correct type name";

        MovieReviewSentimentAnalyzer MRSA = new MovieReviewSentimentAnalyzer(stopwordsSR, sentimentSR,
                BufferedWriter.nullWriter());

        assertEquals(result1, MRSA.getReviewSentimentAsName(check1),
                errorMessageTest1);
        assertEquals(result2, MRSA.getReviewSentimentAsName(check2),
                errorMessageTest1);
        assertEquals(result3, MRSA.getReviewSentimentAsName(check3),
                errorMessageTest1);
    }

    @Test
    void testGetWordSentiment() {
        final String check1 = "lelq";
        final String check2 = "bulka";
        final double result1 = 3.5d;
        final double result2 = 4.0d;
        final String errorMessageTest2 = "Getting or/and calculating word sentiment doesn't work properly";

        MovieReviewSentimentAnalyzer MRSA = new MovieReviewSentimentAnalyzer(stopwordsSR, sentimentSR,
                BufferedWriter.nullWriter());

        assertEquals(result1, MRSA.getWordSentiment(check1), errorMessageTest2);
        assertEquals(result2, MRSA.getWordSentiment(check2), errorMessageTest2);
    }

    @Test
    void testGetWordFrequency() {
        final String check1 = "lelq";
        final String check2 = "baba";
        final int result1 = 2;
        final int result2 = 3;
        final String errorMessageTest3 = "Getting or/and calculating word frequency doesn't work properly";

        MovieReviewSentimentAnalyzer MRSA = new MovieReviewSentimentAnalyzer(stopwordsSR, sentimentSR,
                BufferedWriter.nullWriter());

        assertEquals(result1, MRSA.getWordFrequency(check1), errorMessageTest3);
        assertEquals(result2, MRSA.getWordFrequency(check2), errorMessageTest3);
    }

    @Test
    void testGetMostFrequentWords() {
        final int illegalResult = -1;
        final int result1 = 3;
        final int check3 = 100;
        final int result3 = 5;
        final List<String> result2 = Arrays.asList("baba", "dqdo", "lelq");
        final String errorMessageTest4illegal = "Getting negative count of most frequent words" +
                "should throw an exception";
        final String errorMessageTest4subtest1 = "Getting most frequent words doesn't return correct count of words";
        final String errorMessageTest4subtest2 = "Getting most frequent words doesn't return the correct set of words";

        MovieReviewSentimentAnalyzer MRSA = new MovieReviewSentimentAnalyzer(stopwordsSR, sentimentSR,
                BufferedWriter.nullWriter());

        assertThrows(IllegalArgumentException.class, () -> MRSA.getMostFrequentWords(illegalResult),
                errorMessageTest4illegal);
        assertEquals(result1, MRSA.getMostFrequentWords(result1).size(), errorMessageTest4subtest1);
        assertEquals(result3, MRSA.getMostFrequentWords(check3).size(), errorMessageTest4subtest1);
        assertIterableEquals(result2, MRSA.getMostFrequentWords(result1), errorMessageTest4subtest2);
    }

    @Test
    void testGetMostPositiveWords() {
        final int illegalResult = -1;
        final int result1 = 2;
        final int check3 = 100;
        final int result3 = 5;
        final List<String> result2 = Arrays.asList("bulka", "lelq");
        final String errorMessageTest5illegal = "Getting negative count of most positive words" +
                "should throw an exception";
        final String errorMessageTest5subtest1 = "Getting most positive words doesn't return correct count of words";
        final String errorMessageTest5subtest2 = "Getting most positive words doesn't return the correct set of words";

        MovieReviewSentimentAnalyzer MRSA = new MovieReviewSentimentAnalyzer(stopwordsSR, sentimentSR,
                BufferedWriter.nullWriter());

        assertThrows(IllegalArgumentException.class, () -> MRSA.getMostPositiveWords(illegalResult),
                errorMessageTest5illegal);
        assertEquals(result1, MRSA.getMostPositiveWords(result1).size(), errorMessageTest5subtest1);
        assertEquals(result3, MRSA.getMostPositiveWords(result3).size(), errorMessageTest5subtest1);
        assertIterableEquals(result2, MRSA.getMostPositiveWords(result1), errorMessageTest5subtest2);
    }

    @Test
    void testGetMostNegativeWords() {
        final int illegalResult = -1;
        final int result1 = 3;
        final List<String> result2 = Arrays.asList("chicho", "dqdo", "baba");
        final String errorMessageTest6illegal = "Getting negative count of most negative words" +
                "should throw an exception";
        final String errorMessageTest6subtest1 = "Getting most negative words doesn't return correct count of words";
        final String errorMessageTest6subtest2 = "Getting most negative words doesn't return the correct set of words";

        MovieReviewSentimentAnalyzer MRSA = new MovieReviewSentimentAnalyzer(stopwordsSR, sentimentSR,
                BufferedWriter.nullWriter());

        assertThrows(IllegalArgumentException.class, () -> MRSA.getMostNegativeWords(illegalResult),
                errorMessageTest6illegal);
        assertEquals(result1, MRSA.getMostNegativeWords(result1).size(), errorMessageTest6subtest1);
        assertIterableEquals(result2, MRSA.getMostNegativeWords(result1), errorMessageTest6subtest2);
    }

    @Test
    void testAppendReview() {
        final String review1 = null;
        final String review2 = "A very good review indeed";
        final int sentiment1 = 5;
        final int sentiment2 = 2;
        StringWriter result = new StringWriter(10);
        final String errorMessageTest7illegal = "Appending review with sentiment lower than 0" +
                "or higher than 4 should throw an exception";
        final String errorMessageTest7subtest1 = "Appending a legal review should return true";

        MovieReviewSentimentAnalyzer MRSA = new MovieReviewSentimentAnalyzer(stopwordsSR, sentimentSR,
                result);

        assertThrows(IllegalArgumentException.class, () -> MRSA.appendReview(review1, sentiment2),
                errorMessageTest7illegal);
        assertThrows(IllegalArgumentException.class, () -> MRSA.appendReview(review1, sentiment1),
                errorMessageTest7illegal);
        assertTrue(MRSA.appendReview(review2, sentiment2), errorMessageTest7subtest1);
    }

    @Test
    void testGetSentimentDictionarySize() {
        final int result1 = 5;
        final String errorMessageTest8 = "Getting or/and calculating word frequency doesn't work properly";

        MovieReviewSentimentAnalyzer MRSA = new MovieReviewSentimentAnalyzer(stopwordsSR, sentimentSR,
                BufferedWriter.nullWriter());

        assertEquals(result1, MRSA.getSentimentDictionarySize(), errorMessageTest8);
    }

    @Test
    void testIsStopWord() {
        final String check1 = "and";
        final String check2 = "randomWordJABA";
        final String check3 = "baba";

        final String errorMessageTest9 = "Checking whether a word is a stop word or not doesn't work properly";

        MovieReviewSentimentAnalyzer MRSA = new MovieReviewSentimentAnalyzer(stopwordsSR, sentimentSR,
                BufferedWriter.nullWriter());

        assertTrue(MRSA.isStopWord(check1), errorMessageTest9);
        assertFalse(MRSA.isStopWord(check2), errorMessageTest9);
        assertFalse(MRSA.isStopWord(check3), errorMessageTest9);
    }
}
