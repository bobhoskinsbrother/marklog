package uk.co.itstherules.marklog.editor.markdown.html;

import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler;
import uk.co.itstherules.marklog.editor.model.Post;
import uk.co.itstherules.marklog.publisher.HtmlPublisher;

import java.io.File;
import java.io.IOException;

public final class HtmlPanel extends XHTMLPanel {

    private final File root;
    private String html = "";

    public HtmlPanel(File root) {
        this.root = root;
        setName("htmlPanel");
    }


    public void updateWith(Post post) {
        html = HtmlPublisher.makePost("simple", post);
        setDocumentFromString(html, rootPath(), new XhtmlNamespaceHandler());
    }

    public String getHtmlText() {
        return html;
    }

    private String rootPath() {
        try {
            return "file:////"+root.getCanonicalPath()+"/";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
