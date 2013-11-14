package uk.co.itstherules.marklog.publisher;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.markdown4j.Markdown4jProcessor;
import templates.Root;
import uk.co.itstherules.marklog.editor.model.Post;
import uk.co.itstherules.marklog.editor.model.PostHeader;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

final class TemplateProvider {

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

    TemplateProvider(String directory) {
        this.directory = directory;
    }

    String makePost(Post post) {
        final PostHeader header = post.getHeader();
        Map<String, Object> map = new HashMap<>();
        map.put("post", htmlFrom(post));
        map.put("title", header.getTitle());
        map.put("author", header.getAuthor());
        map.put("date", header.getDate());
        map.put("tags", header.getTags());
        return merge(TemplateType.POST, map);
    }

    private String htmlFrom(Post post) {
        try {
            return new Markdown4jProcessor().process(new StringReader(post.getMarkdown()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
