package uk.co.itstherules.marklog.editor.markdown.html;

import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler;
import org.xhtmlrenderer.swing.NaiveUserAgent;
import org.xhtmlrenderer.util.XRLog;
import uk.co.itstherules.marklog.editor.model.Post;
import uk.co.itstherules.marklog.filesystem.FilePaths;
import uk.co.itstherules.marklog.publisher.HtmlPublisher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public final class HtmlPanel extends XHTMLPanel {

    private String html = "";

    public HtmlPanel(File projectRoot) {
        super(new UserAgentCallback(projectRoot));
        setName("htmlPanel");
    }


    public void updateWith(Post post) {
        html = HtmlPublisher.makePost("simple", post);
        setDocumentFromString(html, rootPath(post.getFile().getParentFile()), new XhtmlNamespaceHandler());
    }

    public String getHtmlText() {
        return html;
    }

    private String rootPath(File parentFile) {
        final String reply = "file:////" + FilePaths.canonicalFor(parentFile) + "/";
        return reply;
    }

    private static class UserAgentCallback extends NaiveUserAgent {

        private final URLClassLoader classLoader;

        private UserAgentCallback(File projectRoot) {
            try {
                final URL[] urls = {new File("templates").toURI().toURL(), projectRoot.toURI().toURL()};
                this.classLoader = new URLClassLoader(urls);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String resolveURI(String uri) {
            return uri;
        }

        @Override
        protected InputStream resolveAndOpenStream(String uri) {
            InputStream is = null;
            if(uri.startsWith("/")) { uri = uri.substring(1); }
            URL url = classLoader.getResource(uri);
            if (url == null) {
                XRLog.load("Didn't find resource [" + uri + "].");
                return null;
            }
            try {
                is = url.openStream();
            }
            catch (MalformedURLException e) {
                XRLog.exception("bad URL given: " + uri, e);
            }
            catch (FileNotFoundException e) {
                XRLog.exception("item at URI " + uri + " not found");
            }
            catch (IOException e) {
                XRLog.exception("IO problem for " + uri, e);
            }
            return is;
        }
    }
}
