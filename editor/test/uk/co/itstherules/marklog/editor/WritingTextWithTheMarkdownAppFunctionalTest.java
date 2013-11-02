package uk.co.itstherules.marklog.editor;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.itstherules.marklog.editor.markdown.HtmlPanel;
import uk.co.itstherules.marklog.editor.model.ProjectConfigurationModel;
import uk.co.itstherules.marklog.string.MakeString;

import java.awt.*;
import java.io.File;

import static java.awt.event.KeyEvent.VK_Z;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static uk.co.itstherules.marklog.editor.StrictXHtmlMatcher.isValidStrictXHtml;

public class WritingTextWithTheMarkdownAppFunctionalTest {

    private FrameFixture window;
    private final File projectDirectory = new File(System.getProperty("java.io.tmpdir"),"test_project");
    private final File file = new File(projectDirectory, "OutputFromTest.md");

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUp() {
        projectDirectory.delete();
        file.delete();

        final ProjectConfigurationModel configuration = new ProjectConfigurationModel(projectDirectory, "Test Project");
        configuration.save();

        MarklogApp marklogApp = GuiActionRunner.execute(new GuiQuery<MarklogApp>() {
            protected MarklogApp executeInEDT() {
                MarklogApp app = new MarklogApp(configuration);
                app.getController().addNewPost(projectDirectory, "Output from Test");
                return app;
            }
        });
        window = new FrameFixture(marklogApp);
        window.show();
    }

    @After
    public void tearDown() {
        window.cleanUp();
        file.delete();
        projectDirectory.delete();
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
        window.textBox("markdownTextArea").enterText(input);
        final String markdownText = MakeString.from(file);
        assertThat(markdownText, containsString("fred"));
        assertThat(markdownText, containsString("===="));
    }

    @Test
    public void canUndoText() throws Exception {
        String input = "\nfred\n====";
        JTextComponentFixture markdownTextArea = window.textBox("markdownTextArea");
        markdownTextArea.enterText(input);
        markdownTextArea.enterText("\n");
        markdownTextArea.enterText("bleh i vant to bite your neck");
        String markdownText = MakeString.from(file);
        assertThat(markdownText, containsString("fred"));
        assertThat(markdownText, containsString("===="));
        assertThat(markdownText, containsString("bleh i vant to bite your neck"));

        //TODO: how the hell do I send a ctrl + z?
        int menuShortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        markdownTextArea.pressKey(menuShortcutKeyMask).pressAndReleaseKeys(VK_Z).releaseKey(menuShortcutKeyMask);
        markdownText = MakeString.from(file);
        assertThat(markdownText, containsString("fred"));
        assertThat(markdownText, containsString("===="));
        assertThat(markdownText, not(containsString("bleh i vant to bite your neck")));

    }

}