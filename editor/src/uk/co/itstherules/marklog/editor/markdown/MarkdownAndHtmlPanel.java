package uk.co.itstherules.marklog.editor.markdown;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public final class MarkdownAndHtmlPanel extends JSplitPane {

    public MarkdownAndHtmlPanel(File file) {
        HtmlPanel htmlPanel = new HtmlPanel();
        JTextArea markdownArea = new MarkdownTextArea(htmlPanel, file);
        final JScrollPane markdownScroller = new JScrollPane(markdownArea);
        final JScrollPane htmlScroller = new JScrollPane(htmlPanel);
        markdownScroller.setPreferredSize(new Dimension(480, 720));
        htmlScroller.setPreferredSize(new Dimension(480, 720));
        setResizeWeight(0.5);
        setLeftComponent(markdownScroller);
        setRightComponent(htmlScroller);
        setDividerLocation(JSplitPane.HORIZONTAL_SPLIT);
        setPreferredSize(new Dimension(1000, 720));
        setDividerLocation(0.5);
    }
}
