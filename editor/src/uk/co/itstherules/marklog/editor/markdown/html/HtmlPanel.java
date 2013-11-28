package uk.co.itstherules.marklog.editor.markdown.html;

import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler;
import uk.co.itstherules.marklog.editor.model.Post;
import uk.co.itstherules.marklog.editor.model.PostService;
import uk.co.itstherules.marklog.editor.model.ProjectConfiguration;
import uk.co.itstherules.marklog.filesystem.FilePaths;
import uk.co.itstherules.marklog.publisher.HtmlPublisher;

import java.io.File;

public final class HtmlPanel extends XHTMLPanel {

    private final ProjectConfiguration configuration;
    private final PostService service;
    private String html = "";

    public HtmlPanel(ProjectConfiguration configuration, PostService service) {
        super(new UserAgentCallback(service));
        this.configuration = configuration;
        this.service = service;
        setName("htmlPanel");
    }


    public void updateWith(Post post) {
        html = HtmlPublisher.makePost("simple", configuration.getName(), post, service);
        setDocumentFromString(html, rootPath(post.getFile().getParentFile()), new XhtmlNamespaceHandler());
    }

    public String getHtmlText() {
        return html;
    }

    private String rootPath(File parentFile) {
        final String reply = "file:////" + FilePaths.canonicalFor(parentFile) + "/";
        return reply;
    }

}
