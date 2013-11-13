package uk.co.itstherules.marklog.editor.markdown.html;

import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.swing.AWTFSImage;
import org.xhtmlrenderer.swing.ImageReplacedElement;
import org.xhtmlrenderer.swing.SwingReplacedElementFactory;

import java.awt.*;

public class LocalImagesSwingElementFactory extends SwingReplacedElementFactory {

    protected ReplacedElement replaceImage(UserAgentCallback userAgentCallback, LayoutContext context, Element element, int width, int height) {
        ReplacedElement replacedElement = lookupImageReplacedElement(element);
        if (replacedElement == null) {
            String uri = context.getNamespaceHandler().getImageSourceURI(element);
            if (uri == null || uri.length() == 0) {
                replacedElement = newIrreplaceableImageElement(width, height);
            } else {
                Image image = null;
                FSImage fsImage = userAgentCallback.getImageResource(uri).getImage();
                if (fsImage != null) {
                    image = AWTFSImage.class.cast(fsImage).getImage();
                }

                if (image != null) {
                    replacedElement = new ImageReplacedElement(image, width, height);
                } else {
                    // TODO: Should return "broken" image icon, e.g. "not found"
                    replacedElement = newIrreplaceableImageElement(width, height);
                }
            }
            storeImageReplacedElement(element, replacedElement);
        }
        return replacedElement;
    }

}
