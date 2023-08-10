package bg.sofia.uni.fmi.mjt.sentiment.parsers;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SentimentWordsParser {
    public static void parseSentimentWords(Reader reviewIn, Map<String, Double> sentimentWordsMap,
                                           Map<String, Integer> wordFrequency, Set<String> stopwords) {
        final String whiteSpace = " ";
        final String regexToSplit = "[^a-zA-Z0-9']";

        BufferedReader BR = new BufferedReader(reviewIn);

        Map<String, Integer> reviewsAndScores = new HashMap<>();
        Set<String> uniqueWords = new HashSet<>();

        BR.lines().forEach(i -> reviewsAndScores.put(i.split(whiteSpace, 2)[1],
                Integer.parseInt(i.split(whiteSpace, 2)[0])));

        reviewsAndScores.forEach((i, j) -> uniqueWords.addAll(Arrays.asList(i.toLowerCase().split(regexToSplit))));

        Iterator<String> iter = uniqueWords.iterator();

        while (iter.hasNext()) {
            String element = iter.next();
            if (stopwords.contains(element.toLowerCase())) {
                iter.remove();
            } else if (element.length() < 2) {
                iter.remove();
            }
        }

        for (String word : uniqueWords) {
            wordFrequency.put(word, 0);
        }

        reviewsAndScores.keySet()
                .forEach(i -> Arrays.asList(i.toLowerCase().split(regexToSplit))
                        .forEach(j -> {
                            wordFrequency.computeIfPresent(j.toLowerCase(), (k, v) -> v + 1);
                        }));

        for (String word : uniqueWords) {
            int sum = 0;
            int count = 0;

            for (String review : reviewsAndScores.keySet()) {
                if (Arrays.asList(review.toLowerCase().split(regexToSplit)).contains(word)) {
                    sum += reviewsAndScores.get(review);
                    count++;
                }
            }

            sentimentWordsMap.put(word, (double) sum / count);
        }
    }
}
