package uk.co.itstherules.marklog.editor.markdown;

import org.xhtmlrenderer.simple.FSScrollPane;
import uk.co.itstherules.marklog.editor.markdown.html.HtmlPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public final class MarkdownAndHtmlPanel extends JPanel {

    private final HtmlPanel htmlPanel;
    private final JTextArea markdownArea;

    public MarkdownAndHtmlPanel(File root, File file) {
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(1024, 720));
        htmlPanel = new HtmlPanel(root);
        markdownArea = new MarkdownTextAreaBuilder(htmlPanel, file).ok();
        splitPane(htmlScroller(), markdownScroller());
    }

    public void setText(String text) {
        markdownArea.setText(text);
    }

    private JScrollPane markdownScroller() {
        final JScrollPane markdownScroller = new JScrollPane(markdownArea);
        markdownScroller.setPreferredSize(new Dimension(480, 720));
        return markdownScroller;
    }

    private JScrollPane htmlScroller() {
        final JScrollPane htmlScroller = new FSScrollPane(htmlPanel);
        htmlScroller.setPreferredSize(new Dimension(480, 720));
        return htmlScroller;
    }

    private void splitPane(JScrollPane htmlScroller, JScrollPane markdownScroller) {
        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.5);
        splitPane.setLeftComponent(markdownScroller);
        splitPane.setRightComponent(htmlScroller);
        splitPane.setDividerLocation(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setPreferredSize(new Dimension(1000, 720));
        splitPane.setDividerLocation(0.5);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        add(splitPane, constraints);
    }
}
