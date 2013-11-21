package uk.co.itstherules.marklog.publisher;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.markdown4j.Markdown4jProcessor;
import uk.co.itstherules.marklog.editor.model.Post;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

final class TemplateProvider {

    private final String directory;

    TemplateProvider(String directory) {
        this.directory = directory;
    }

    String makePosts(String title, List<Post> posts) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("convert", new Convert());
        map.put("posts", posts);
        return merge(TemplateType.POSTS, map);
    }

    String makePost(Post post) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", post.getHeader().getTitle());
        map.put("convert", new Convert());
        map.put("post", post);
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
        configuration.setDirectoryForTemplateLoading(new File(System.getProperty("user.dir")));
        return configuration.getTemplate("templates/"+directory+"/"+templateType.toString());
    }

    private enum TemplateType {
        POST("post.ftl"),
        POSTS("posts.ftl");
        private final String path;

        private TemplateType(String path) {
            this.path = path;
        }

        @Override
        public String toString() {
            return path;
        }
    }

    public class Convert {

        public String toHtml(String markdown) {
            try {
                return new Markdown4jProcessor().process(new StringReader(markdown));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
