package bg.sofia.uni.fmi.mjt.sentiment;

import bg.sofia.uni.fmi.mjt.sentiment.parsers.SentimentWordsParser;
import bg.sofia.uni.fmi.mjt.sentiment.parsers.StopwordsParser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class MovieReviewSentimentAnalyzer implements SentimentAnalyzer {
    private Set<String> stopwordsSet;
    private Map<String, Double> sentimentWordsMap;
    private Map<String, Integer> wordFrequency;
    private Reader stopwordsInReader;
    private Reader reviewsInReader;
    private Writer reviewsOutWriter;

    private final double UNKNOWN = -1.0;

    private void updateStopwords() {
        stopwordsSet.clear();

        StopwordsParser.parseStopwords(stopwordsInReader, stopwordsSet);
    }

    private void updateSentimentWords() {
        sentimentWordsMap.clear();

        SentimentWordsParser.parseSentimentWords(reviewsInReader, sentimentWordsMap, wordFrequency, stopwordsSet);
    }

    public MovieReviewSentimentAnalyzer(Reader stopwordsIn, Reader reviewsIn, Writer reviewsOut) {
        this.stopwordsSet = new TreeSet<>();
        this.sentimentWordsMap = new TreeMap<>();
        this.wordFrequency = new HashMap<>();
        this.stopwordsInReader = stopwordsIn;
        this.reviewsInReader = reviewsIn;
        this.reviewsOutWriter = reviewsOut;

        updateStopwords();
        updateSentimentWords();
    }

    @Override
    public double getReviewSentiment(String review) {
        double sum = 0;
        int count = 0;
        final String regexToSplit = "[^a-zA-Z0-9']";

        String[] wordsInReview = review.toLowerCase().split(regexToSplit);

        for (String word : wordsInReview) {
            if (sentimentWordsMap.containsKey(word)) {
                sum += sentimentWordsMap.get(word);
                count++;
            }
        }

        if (count == 0) {
            return UNKNOWN;
        }

        return (double) sum / count;
    }

    @Override
    public String getReviewSentimentAsName(String review) {
        return SentimentType.getName(Math.round(getReviewSentiment(review)));
    }

    @Override
    public double getWordSentiment(String word) {
        if (sentimentWordsMap.containsKey(word.toLowerCase())) {
            return sentimentWordsMap.get(word.toLowerCase());
        }

        return UNKNOWN;
    }

    @Override
    public int getWordFrequency(String word) {
        if (wordFrequency.containsKey(word.toLowerCase())) {
            return wordFrequency.get(word.toLowerCase());
        }

        return 0;
    }

    @Override
    public List<String> getMostFrequentWords(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }

        if (n > wordFrequency.size()) {
            n = wordFrequency.size();
        }

        List<String> result = wordFrequency
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey)
                .limit(n)
                .collect(Collectors.toList());

        return result;
    }

    @Override
    public List<String> getMostPositiveWords(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }

        if (n > wordFrequency.size()) {
            n = wordFrequency.size();
        }

        List<String> result = sentimentWordsMap
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey)
                .limit(n)
                .collect(Collectors.toList());

        return result;
    }

    @Override
    public List<String> getMostNegativeWords(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }

        if (n > wordFrequency.size()) {
            n = wordFrequency.size();
        }

        List<String> result = sentimentWordsMap
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .limit(n)
                .collect(Collectors.toList());

        return result;
    }

    @Override
    public boolean appendReview(String review, int sentiment) {
        final int sentimentMin = 0;
        final int sentimentMax = 4;

        if (review == null || review.isEmpty() || review.isBlank()) {
            throw new IllegalArgumentException();
        }

        if (sentiment < sentimentMin || sentiment > sentimentMax) {
            throw new IllegalArgumentException();
        }

        try (BufferedWriter BW = new BufferedWriter(reviewsOutWriter);) {
            BW.write(sentiment);
            BW.write(" " + review + System.lineSeparator());
        } catch (IOException e) {
            return false;
        }

        updateSentimentWords();

        return true;
    }

    @Override
    public int getSentimentDictionarySize() {
        return sentimentWordsMap.size();
    }

    @Override
    public boolean isStopWord(String word) {
        return stopwordsSet.contains(word.toLowerCase());
    }
}
