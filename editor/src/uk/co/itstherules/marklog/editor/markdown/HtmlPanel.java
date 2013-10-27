package uk.co.itstherules.marklog.editor.markdown;

import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler;
import uk.co.itstherules.marklog.templating.TemplateProvider;

public final class HtmlPanel extends XHTMLPanel {

    private String html = "";

    public void setHtmlText(String text) {
        setName("htmlPanel");
        html = makeHtmlDocument(text);
        setDocumentFromString(html, "", new XhtmlNamespaceHandler());
    }

    public String getHtmlText() {
        return html;
    }

    private String makeHtmlDocument(String html) {
        return new TemplateProvider("simple").posts(html);
    }

}
