package uk.co.itstherules.marklog.templating;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import templates.Root;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class TemplateProvider {

    private final String directory;

    private enum TemplateType {

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

    public TemplateProvider(String directory) {
        this.directory = directory;
    }

    public String posts(String... html) {
        return merge(TemplateType.POSTS, "posts", Arrays.asList(html));
    }

    private String merge(TemplateType type, String name, List<?> values) {
        try {
            final Template template = template(type);
            final StringWriter writer = new StringWriter();
            template.createProcessingEnvironment(Collections.singletonMap(name, values), writer).process();
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
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
