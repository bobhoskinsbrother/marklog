package uk.co.itstherules.marklog.editor;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.itstherules.marklog.editor.markdown.HtmlPanel;
import uk.co.itstherules.marklog.string.MakeString;

import java.io.File;
import java.io.IOException;

import static java.awt.event.KeyEvent.*;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static uk.co.itstherules.marklog.editor.CommonProjectActions.*;
import static uk.co.itstherules.marklog.editor.StrictXHtmlMatcher.isValidStrictXHtml;

public class EditingTextFunctionalTest {

    private FrameFixture window;
    private final File file = new File(PROJECT_DIRECTORY, "OutputFromTest.md");

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUp() throws IOException {
        reset();
        window = openProject();
        newPostIn(window);
    }

    @After
    public void tearDown() throws IOException {
        window.cleanUp();
        reset();
    }

    @Test
    public void previewPanelIsPopulatedWithConvertedMarkdown() throws Exception {
        String input = "\nfred\n====";
        window.textBox("markdownTextArea").enterText(input);
        final HtmlPanel htmlPanel = HtmlPanel.class.cast(window.panel("htmlPanel").component());
        final String xhtmlText = htmlPanel.getHtmlText();
        assertThat(xhtmlText, isValidStrictXHtml());
        assertThat(xhtmlText, containsString("<h1>fred</h1>"));
    }

    @Test
    public void previewPanelIsPopulatedWithASmallerHeadline() throws Exception {
        String input = "\nfred\n----\n----";
        window.textBox("markdownTextArea").enterText(input);
        final HtmlPanel htmlPanel = HtmlPanel.class.cast(window.panel("htmlPanel").component());
        final String xhtmlText = htmlPanel.getHtmlText();
        assertThat(xhtmlText, isValidStrictXHtml());
        assertThat(xhtmlText, containsString("<h2>fred</h2>"));
        assertThat(xhtmlText, containsString("<hr />"));
    }

    @Test
    public void fileIsAutomaticallyWritten() throws Exception {
        String input = "\nfred\n====";
        JTextComponentFixture markdownTextArea = window.textBox("markdownTextArea");
        markdownTextArea.enterText(input);
        assertThat(file, isCreatedWithin(1000));
        String markdownText = MakeString.from(file);
        assertThat(markdownText, containsString("fred"));
        assertThat(markdownText, containsString("===="));
    }

    @Test
    public void canUndoText() throws Exception {
        //TODO: check this test works cross platform i.e. run it in your mac
        String input = "\nfred\n====";
        JTextComponentFixture markdownTextArea = window.textBox("markdownTextArea");
        markdownTextArea.enterText(input);
        markdownTextArea.enterText("\n");
        markdownTextArea.enterText("bleh i vant to bite your neck");
        String markdownText = MakeString.from(file);
        assertThat(markdownText, containsString("fred"));
        assertThat(markdownText, containsString("===="));
        assertThat(markdownText, containsString("bleh i vant to bite your neck"));

        markdownTextArea.pressKey(VK_CONTROL).pressAndReleaseKeys(VK_Z,VK_Z,VK_Z,VK_Z).releaseKey(VK_CONTROL);
        markdownText = MakeString.from(file);
        assertThat(markdownText, containsString("fred"));
        assertThat(markdownText, containsString("===="));
        assertThat(markdownText, not(containsString("bleh i vant to bite your neck")));
        assertThat(markdownText, containsString("bleh i vant to bite your "));

    }

    @Test
    public void canUndoRedoText() throws Exception {
        //TODO: check this test works cross platform i.e. run it in your mac
        String input = "\nfred\n====";
        JTextComponentFixture markdownTextArea = window.textBox("markdownTextArea");
        markdownTextArea.enterText(input);
        markdownTextArea.enterText("\n");
        markdownTextArea.enterText("bleh i vant to bite your neck");
        String markdownText = MakeString.from(file);
        assertThat(markdownText, containsString("fred"));
        assertThat(markdownText, containsString("===="));
        assertThat(markdownText, containsString("bleh i vant to bite your neck"));

        markdownTextArea.pressKey(VK_CONTROL).pressAndReleaseKeys(VK_Z, VK_Z, VK_Z, VK_Z).releaseKey(VK_CONTROL);
        markdownText = MakeString.from(file);
        assertThat(markdownText, containsString("fred"));
        assertThat(markdownText, containsString("===="));
        assertThat(markdownText, not(containsString("bleh i vant to bite your neck")));
        assertThat(markdownText, containsString("bleh i vant to bite your "));
        markdownTextArea.pressKey(VK_CONTROL).pressAndReleaseKeys(VK_Y,VK_Y,VK_Y).releaseKey(VK_CONTROL);
        markdownText = MakeString.from(file);
        assertThat(markdownText, not(containsString("bleh i vant to bite your neck")));
        assertThat(markdownText, containsString("bleh i vant to bite your nec"));

    }

}