package uk.co.itstherules.marklog.filesystem;

import java.io.File;
import java.io.IOException;

public final class FilePaths {

    private FilePaths() { }


    public static String rootRelativePath(File root, File file) {
        final String canonical = canonicalFor(file);
        return canonical.substring(canonicalFor(root).length() + 1);
    }


    public static String canonicalFor(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isImage(File file) {
        final String name = file.getName();
        return name.endsWith(".png") || name.endsWith(".gif") || name.endsWith(".jpg");
    }

    public static boolean isMarkdown(File file) {
        final String name = file.getName();
        return name.endsWith(".md");
    }
}
