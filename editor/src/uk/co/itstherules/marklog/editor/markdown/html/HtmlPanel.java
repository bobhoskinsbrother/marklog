package uk.co.itstherules.marklog.editor.markdown.html;

import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler;
import uk.co.itstherules.marklog.editor.model.PostHeader;
import uk.co.itstherules.marklog.templating.TemplateProvider;

import java.io.File;
import java.io.IOException;

public final class HtmlPanel extends XHTMLPanel {

    private final File root;
    private String html = "";

    public HtmlPanel(File root) {
        this.root = root;
    }

    public void updateWith(String text, PostHeader header) {
        setName("htmlPanel");
        html = makeHtmlDocument(text, header);
        setDocumentFromString(html, rootPath(), new XhtmlNamespaceHandler());
    }

    public String getHtmlText() {
        return html;
    }

    private String makeHtmlDocument(String html, PostHeader header) {
        return new TemplateProvider("simple").posts(html, header.getTitle(), header.getAuthor(), header.getDate(), header.getTags());
    }

    private String rootPath() {
        try {
            return "file:////"+root.getCanonicalPath()+"/";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
