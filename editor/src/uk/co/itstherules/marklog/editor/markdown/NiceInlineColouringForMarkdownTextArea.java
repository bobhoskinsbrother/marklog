package uk.co.itstherules.marklog.editor.markdown;

import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NiceInlineColouringForMarkdownTextArea extends BasicTextAreaUI {

    public View create(Element elem) {
        return new MarkdownView(elem);
    }

    private static class MarkdownView extends PlainView {

        private static HashMap<Pattern, Color> patternColors;
        private static String HEADER_TAG = "(#{1,6}.*\\w.+\\s.*)";
        private static String BLOCK_QUOTE = "^(>.\\w*\\s.*)";
        private static String LIST_TAG = "^([\\*|\\+|-]\\s)";
        private static String HORIZONTAL_RULE = "^([\\*|-]{3})";
        private static String INLINE_LINK = "(\\[\\w.+\\]\\(\\w.+\\))";
        private static String REFERENCE_LINK = "(\\[\\w.+\\].*\\[\\w.*\\])";
        private static String REFERENCED_LINK = "(\\[\\w.+\\]:\\s.*http://\\w.+)";
        private static String REFERENCED_IMAGE = "((\\[[\\w\\s]+\\]:)([\\w\\s/\\\"]+))";
        private static String EM_TAG = "([\\*_]\\w.+?[\\*_])";
        private static String STRONG_TAG = "((\\*\\*|__)([\\w\\s\\.]*?)(\\*\\*|__))";
        private static String CODE_TAG = "(`.+([\\w\\s\\.]*?)`)";
        private static String INLINE_IMAGE = "(!\\[[\\w\\s]+\\])(\\([\\w\\s/\\.\\\"]+\\))";
        private static String REFERENCE_IMAGE = "(!\\[[\\w\\s]+\\])(\\[[\\w\\s/\\.\\\"]+\\])";
        private static Color HEADER_TAG_COLOR = new Color(139, 66, 214);
        private static Color BLOCK_QUOTE_COLOR = new Color(35, 139, 73);
        private static Color LIST_TAG_COLOR = new Color(255, 120, 0);
        private static Color HORIZONTAL_RULE_COLOR = new Color(255,0,0);
        private static Color INLINE_LINK_COLOR = new Color(0, 144, 223);
        private static Color REFERENCE_LINK_COLOR = new Color(0, 94, 145);
        private static Color REFERENCED_LINK_COLOR = new Color(255, 139, 0);
        private static Color EM_TAG_COLOR = new Color(188, 47, 66);
        private static Color CODE_TAG_COLOR = new Color(166, 156, 0);
        private static Color INLINE_IMAGE_COLOR = new Color(166, 90, 0);
        private static Color REFERENCE_IMAGE_COLOR = new Color(255, 107, 64);
        private static Color REFERENCED_IMAGE_COLOR = new Color(71, 3, 111);

        static {
            patternColors = new HashMap<Pattern, Color>();
            patternColors.put(Pattern.compile(HEADER_TAG), HEADER_TAG_COLOR);
            patternColors.put(Pattern.compile(LIST_TAG), LIST_TAG_COLOR);
            patternColors.put(Pattern.compile(BLOCK_QUOTE), BLOCK_QUOTE_COLOR);
            patternColors.put(Pattern.compile(HORIZONTAL_RULE), HORIZONTAL_RULE_COLOR);
            patternColors.put(Pattern.compile(INLINE_LINK), INLINE_LINK_COLOR);
            patternColors.put(Pattern.compile(REFERENCE_LINK), REFERENCE_LINK_COLOR);
            patternColors.put(Pattern.compile(REFERENCED_LINK), REFERENCED_LINK_COLOR);
            patternColors.put(Pattern.compile(EM_TAG), EM_TAG_COLOR);
            patternColors.put(Pattern.compile(CODE_TAG), CODE_TAG_COLOR);
            patternColors.put(Pattern.compile(INLINE_IMAGE), INLINE_IMAGE_COLOR);
            patternColors.put(Pattern.compile(REFERENCE_IMAGE), REFERENCE_IMAGE_COLOR);
            patternColors.put(Pattern.compile(REFERENCED_IMAGE), REFERENCED_IMAGE_COLOR);
        }

        public MarkdownView(Element element) {
            super(element);
            getDocument().putProperty(PlainDocument.tabSizeAttribute, 4);
        }

        @Override
        protected int drawUnselectedText(Graphics graphics, int x, int y, int p0, int p1) throws BadLocationException {
            Document doc = getDocument();
            String text = doc.getText(p0, p1 - p0);
            Segment segment = getLineBuffer();
            SortedMap<Integer, Integer> startMap = new TreeMap<Integer, Integer>();
            SortedMap<Integer, Color> colorMap = new TreeMap<Integer, Color>();
            for (Map.Entry<Pattern, Color> entry : patternColors.entrySet()) {
                Matcher matcher = entry.getKey().matcher(text);
                while (matcher.find()) {
                    int startPos = matcher.start(1);
                    int endPos = matcher.end();
                    startMap.put(startPos, endPos);
                    colorMap.put(startPos, entry.getValue());
                }
            }
            int i = 0;
            for (Map.Entry<Integer, Integer> entry : startMap.entrySet()) {
                int start = entry.getKey();
                int end = entry.getValue();
                if (i < start) {
                    graphics.setColor(Color.black);
                    doc.getText(p0 + i, start - i, segment);
                    x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
                }
                graphics.setColor(colorMap.get(start));
                i = end;
                doc.getText(p0 + start, i - start, segment);
                x = Utilities.drawTabbedText(segment, x, y, graphics, this, start);
            }
            // Paint possible remaining text black
            if (i < text.length()) {
                graphics.setColor(Color.black);
                doc.getText(p0 + i, text.length() - i, segment);
                x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
            }
            return x;
        }

    }
}
