package uk.co.itstherules.marklog.editor.model;

import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class PostHeader {

    public static final String HEADER_DELIMITER = "#######";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private Map<String, String> map;

    public PostHeader(String title, String author, Date date, PostStage stage, List<String> tags) {
        map = new LinkedHashMap<>();
        map.put("title", title);
        map.put("author", author);
        map.put("date", makeDateFormatter().format(date));
        map.put("stage", stage.name());
        map.put("tags", printList(tags));
    }

    public PostHeader(String header) {
        map = makeMapFromHeader(header);
    }

    @Override public String toString() {
        final StringWriter writer = new StringWriter();
        writer.write(HEADER_DELIMITER + HEADER_DELIMITER + HEADER_DELIMITER);
        writer.write(LINE_SEPARATOR);
        for (String key : map.keySet()) {
            writer.write(key);
            writer.write(":");
            writer.write(map.get(key));
            writer.write(LINE_SEPARATOR);
        }
        writer.write(HEADER_DELIMITER + HEADER_DELIMITER + HEADER_DELIMITER);
        writer.write(LINE_SEPARATOR);
        return writer.toString();
    }

    public String getAuthor() {
        return get("author", "");
    }

    private String get(String key, String defaultValue) {
        final String value = map.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public PostStage getStage() {
        return findStage();
    }

    public Date getDate() {
        return findDate();
    }

    public String getTitle() {
        return get("title", "");
    }

    public List<String> getTags() {
        return findTags();
    }

    private Date findDate() {
        final SimpleDateFormat formatter = makeDateFormatter();
        final Date now = Calendar.getInstance().getTime();
        String date = get("date", "");
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            return now;
        }
    }

    private SimpleDateFormat makeDateFormatter() {return new SimpleDateFormat("dd/MM/yyyy HH:mm");}

    private PostStage findStage() {
        final PostStage publish = PostStage.publish;
        try {
            final String stageString = get("stage", publish.name());
            return PostStage.valueOf(stageString);
        } catch (IllegalArgumentException e) {
            return publish;
        }
    }

    private List<String> findTags() {
        List<String> tags = new ArrayList<>();
        final String tagsString = get("tags", "");
        for (String tag : tagsString.split(",")) {
            final String trimmed = tag.trim();
            if (!"".equals(trimmed)) {
                tags.add(trimmed);
            }
        }
        return tags;
    }

    private Map<String, String> makeMapFromHeader(String header) {
        Map<String, String> properties = new LinkedHashMap<>();
        final String[] lines = header.split(LINE_SEPARATOR);
        for (String line : lines) {
            if (!line.startsWith(HEADER_DELIMITER)) {
                final int index = line.indexOf(":");
                if (index > -1) {
                    String key = line.substring(0, index).trim();
                    String value = line.substring(index + 1).trim();
                    properties.put(key, value);
                }
            }
        }
        return properties;
    }

    private String printList(List<String> tags) {
        final Iterator<String> iterator = tags.iterator();
        StringBuilder b = new StringBuilder();
        while (iterator.hasNext()) {
            b.append(iterator.next());
            if (iterator.hasNext()) {
                b.append(",");
            }
        }
        return b.toString();
    }

    public enum PostStage {draft, complete, publish}

}
