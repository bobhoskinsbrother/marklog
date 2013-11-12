package uk.co.itstherules.marklog.editor.markdown;

import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler;
import uk.co.itstherules.marklog.editor.model.PostHeader;
import uk.co.itstherules.marklog.templating.TemplateProvider;

public final class HtmlPanel extends XHTMLPanel {

    private String html = "";

    public void updateWith(String text, PostHeader header) {
        setName("htmlPanel");
        html = makeHtmlDocument(text, header);
        setDocumentFromString(html, "", new XhtmlNamespaceHandler());
    }

    public String getHtmlText() {
        return html;
    }

    private String makeHtmlDocument(String html, PostHeader header) {
        return new TemplateProvider("simple").posts(html, header.getTitle(), header.getAuthor(), header.getDate(), header.getTags());
    }

}
