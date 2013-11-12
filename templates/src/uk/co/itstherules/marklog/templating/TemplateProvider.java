package uk.co.itstherules.marklog.templating;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import templates.Root;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

public final class TemplateProvider {

    private final String directory;

    private enum TemplateType {

        POST("post.ftl");

        private final String path;

        private TemplateType(String path) {
            this.path = path;
        }

        @Override
        public String toString() {
            return path;
        }
    }

    public TemplateProvider(String directory) {
        this.directory = directory;
    }

    public String posts(String html, String title, String author, Date date, List<String> tags) {
        Map<String, Object> map = new HashMap<>();
        map.put("post", html);
        map.put("title", title);
        map.put("author", author);
        map.put("date", date);
        map.put("tags", tags);
        return merge(TemplateType.POST, map);
    }

    private String merge(TemplateType type, Map<String, Object> dataModel) {
        try {
            final Template template = template(type);
            final StringWriter writer = new StringWriter();
            template.createProcessingEnvironment(dataModel, writer).process();
            return writer.toString();
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private Template template(TemplateType templateType) throws IOException {
        Configuration configuration = new Configuration();
        configuration.setEncoding(Locale.UK, "UTF-8");
        configuration.setURLEscapingCharset("UTF-8");
        configuration.setClassForTemplateLoading(Root.class, directory);
        return configuration.getTemplate(templateType.toString());
    }

}
