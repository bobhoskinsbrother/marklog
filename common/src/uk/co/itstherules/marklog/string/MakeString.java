package uk.co.itstherules.marklog.string;

import java.io.*;

public final class MakeString {

    public static final String from(File file) throws IOException {
        final FileReader reader = new FileReader(file);
        StringWriter writer = new StringWriter();
        copy(reader,writer);
        return writer.toString();
    }

    private static void copy(Reader reader, Writer writer) throws IOException {
        char[] buffer = new char[1024];
        int n = 0;
        while (-1 != (n = reader.read(buffer))) {
            writer.write(buffer, 0, n);
        }
    }

}
