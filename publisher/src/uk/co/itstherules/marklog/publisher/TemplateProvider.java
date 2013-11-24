package uk.co.itstherules.marklog.publisher;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.markdown4j.Markdown4jProcessor;
import uk.co.itstherules.marklog.editor.model.Link;
import uk.co.itstherules.marklog.editor.model.Post;
import uk.co.itstherules.marklog.editor.model.PostService;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

final class TemplateProvider {

    private final String templateDirectory;
    private final PostService service;
    private final List<Link> tagsLinks;
    private final List<Link> archivesLinks;

    TemplateProvider(String templateDirectory, PostService service) {
        this.templateDirectory = templateDirectory;
        this.service = service;
        tagsLinks = service.tagsLinks();
        archivesLinks = service.archivesLinks();
    }

    String makeSearchIndexes() {
        List<Post> posts = service.allPosts();
        Map<String, Object> map = new HashMap<>();
        map.put("posts", posts);
        map.put("link_resolver", new LinkResolver());
        map.put("last_updated", System.currentTimeMillis());
        return merge(TemplateType.JSON_POSTS, map);
    }

    String makePosts(String title, List<Post> posts) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("convert", new Convert());
        map.put("posts", posts);
        map.put("tags_links", tagsLinks);
        map.put("archives_links", archivesLinks);
        map.put("link_resolver", new LinkResolver());
        return merge(TemplateType.POSTS, map);
    }

    String makePost(Post post) {
        return makePosts(post.getHeader().getTitle(), Arrays.asList(post));
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
        configuration.setEncoding(Locale.UK, "utf8");
        configuration.setURLEscapingCharset("utf8");
        configuration.setDirectoryForTemplateLoading(new File(System.getProperty("user.dir")));
        return configuration.getTemplate("templates/"+ templateDirectory +"/"+templateType.toString());
    }

    private enum TemplateType {
        POSTS("posts.ftl"),
        JSON_POSTS("posts.json.ftl");

        private final String path;

        private TemplateType(String path) {
            this.path = path;
        }

        @Override
        public String toString() {
            return path;
        }
    }

    public class LinkResolver {
        public String resolve(Post post) {
            return service.relativeHtmlPathFor(post);
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
