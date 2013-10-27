package uk.co.itstherules.marklog.editor;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import uk.co.itstherules.marklog.editor.markdown.HtmlPanel;
import uk.co.itstherules.marklog.editor.markdown.MarkdownEditorApp;
import uk.co.itstherules.marklog.string.MakeString;

import java.io.File;

import static org.junit.Assert.assertThat;
import static uk.co.itstherules.marklog.editor.StrictXHtmlMatcher.isValidStrictXHtml;

public class MarkdownEditorFunctionalTest {

    private FrameFixture window;
    private final File[] file = {new File(System.getProperty("java.io.tmpdir") + "/output_from_test.md")};

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUp() {
        file[0].delete();
        MarkdownEditorApp markdownEditor = GuiActionRunner.execute(new GuiQuery<MarkdownEditorApp>() {
            protected MarkdownEditorApp executeInEDT() {
                return new MarkdownEditorApp(file[0]);
            }
        });
        window = new FrameFixture(markdownEditor);
        window.show();
    }

    @After
    public void tearDown() {
        window.cleanUp();
    }

    @Test
    public void previewPanelIsPopulatedWithConvertedMarkdown() throws Exception {
        String input = "fred\n====";
        window.textBox("markdownTextArea").enterText(input);
        final HtmlPanel htmlPanel = HtmlPanel.class.cast(window.panel("htmlPanel").component());
        final String xhtmlText = htmlPanel.getHtmlText();
        assertThat(xhtmlText, isValidStrictXHtml());
        assertThat(xhtmlText, contains("<h1>fred</h1>"));
    }

    @Test
    public void previewPanelIsPopulatedWithASmallerHeadline() throws Exception {
        String input = "fred\n----\n----";
        window.textBox("markdownTextArea").enterText(input);
        final HtmlPanel htmlPanel = HtmlPanel.class.cast(window.panel("htmlPanel").component());
        final String xhtmlText = htmlPanel.getHtmlText();
        assertThat(xhtmlText, isValidStrictXHtml());
        assertThat(xhtmlText, contains("<h2>fred</h2>"));
        assertThat(xhtmlText, contains("<hr />"));
    }

    @Test
    public void fileIsAutomaticallyWritten() throws Exception {
        String input = "fred\n====";
        window.textBox("markdownTextArea").enterText(input);
        final String markdownText = MakeString.from(file[0]);
        assertThat(markdownText, contains("fred"));
        assertThat(markdownText, contains("===="));
    }

    private Matcher<String> contains(final String candidate) {
        return new TypeSafeMatcher<String>() {
            @Override
            public boolean matchesSafely(String s) {
                return s.contains(candidate);
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue(candidate);
            }
        };
    }

}