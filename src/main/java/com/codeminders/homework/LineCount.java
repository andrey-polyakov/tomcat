package com.codeminders.homework;

import com.codeminders.homework.parser.JavaCommentsRemover;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;


/**
 * This walker ignores special and extra large files, and invokes aggregator for folders and .java files.
 */
public class LineCount {

    public static Optional<BigDecimal> of(File file) throws IOException {
        Path path = Paths.get(file.toURI());
        if (file.isDirectory()) {
            return Optional.empty();
        }
        /**
         *  Uncomment this to put size limit
         *  if (file.length() > 25 * 1024 * 1024) {
         *   throw new IOException("Skipping an extremely large Java file over 25mb: " + file.getAbsolutePath());
        }*/
        String rawContents = new String(Files.readAllBytes(path));
        String processedContents = JavaCommentsRemover.removeComments(rawContents);
        int lines;
        try (LineNumberReader reader = new LineNumberReader(new StringReader(processedContents))) {
            while ((reader.readLine()) != null) ;
            lines = reader.getLineNumber();
        }
        return Optional.of(BigDecimal.valueOf(lines));
    }

}