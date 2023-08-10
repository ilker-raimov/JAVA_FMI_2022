package bg.sofia.uni.fmi.mjt.sentiment.parsers;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Set;

public class StopwordsParser {
    public static void parseStopwords(Reader stopwordsIn, Set<String> stopwordsSet) {
        BufferedReader BR = new BufferedReader(stopwordsIn);

        BR.lines().forEach(i -> stopwordsSet.add(i.toLowerCase()));
    }
}
