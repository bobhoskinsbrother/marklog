package uk.co.itstherules.marklog.publisher;

import org.markdown4j.Markdown4jProcessor;

import java.io.IOException;

final class MarkdownTransformer {

    private static final Markdown4jProcessor PROCESSOR = new Markdown4jProcessor();

    private MarkdownTransformer(){}


    static String toHtml(String input) {
        try {
            return PROCESSOR.process(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
