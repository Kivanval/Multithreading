package com.kivanval.thread;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnalyzerText implements Callable<Map<String, Long>> {

    Collection<Path> srcPaths;

    public AnalyzerText(Collection<Path> paths) {
        this.srcPaths = paths;
    }

    @Override
    public Map<String, Long> call() {
        return srcPaths.parallelStream()
                .flatMap(AnalyzerText::silentFilesLines)
                .map(StringUtils::strip)
                .map(l -> l.split("[\\s\\p{P}]+"))
                .flatMap(Arrays::stream)
                .map(WordUtils::capitalize)
                .collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting()));
    }

    private static Stream<String> silentFilesLines(Path path) {
        try {
            return Files.lines(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
