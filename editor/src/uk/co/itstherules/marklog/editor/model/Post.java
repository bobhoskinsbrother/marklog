package uk.co.itstherules.marklog.editor.model;

import uk.co.itstherules.marklog.string.FileifyTitle;
import uk.co.itstherules.marklog.string.MakeString;

import java.io.*;
import java.util.Calendar;
import java.util.Collections;

public final class Post {

    public static final String HEADER_DELIMITER = "#######";
    private final File directory;
    private final String fileName;
    private PostHeader postHeader;
    private String markdown;

    public Post(File postFile) {
        fileName = postFile.getName();
        directory = postFile.getParentFile();
        String postString = loadPost(postFile);
        setText(postString);
    }

    public Post(File directory, String title) {
        this.directory = directory;
        FileifyTitle fileifyTitle = new FileifyTitle(".md");
        fileName = fileifyTitle.manipulate(title);
        postHeader = new PostHeader(title, "", Calendar.getInstance().getTime(), PostHeader.PostStage.publish, Collections.<String>emptyList());
        markdown = title + "\n" + "====";
    }

    public void save() {
        try {
            final FileWriter writer = new FileWriter(getFile());
            writer.write(postHeader.toString());
            writer.write(markdown);
            writer.close();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to store post");
        }
    }

    public void setText(String postString) {
        String lineSeparator = System.getProperty("line.separator");
        BufferedReader reader = new BufferedReader(new StringReader(postString));
        StringBuilder hb = new StringBuilder();
        StringBuilder pb = new StringBuilder();
        String line = "";
        boolean headerOpened = false;
        try {
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(HEADER_DELIMITER)) {
                    hb.append(line).append(lineSeparator);
                    headerOpened = !headerOpened;
                    continue;
                }
                if (headerOpened) {
                    hb.append(line).append(lineSeparator);
                } else {
                    pb.append(line).append(lineSeparator);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        postHeader = new PostHeader(hb.toString());
        markdown = pb.toString();
    }

    private String loadPost(File postFile) {
        try {
            return MakeString.from(postFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File getFile() {return new File(directory, fileName);}

    @Override public String toString() {
        return postHeader.toString() + markdown;
    }

    public String getMarkdown() {
        return markdown;
    }

    public PostHeader getHeader() {
        return postHeader;
    }
}
