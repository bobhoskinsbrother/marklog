package uk.co.itstherules.marklog.editor;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.junit.*;
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

@Ignore
public class MakingANewProjectWithTheMarkdownAppFunctionalTest {

    private FrameFixture window;
    private final File tempDir = new File(System.getProperty("java.io.tmpdir"),"test_project");

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUp() {
        tempDir.delete();
        MarklogApp marklogApp = GuiActionRunner.execute(new GuiQuery<MarklogApp>() {
            protected MarklogApp executeInEDT() {
                MarklogApp app = new MarklogApp(null);
                return app;
            }
        });
        window = new FrameFixture(marklogApp);
        window.show();
    }

    @After
    public void tearDown() {
        window.cleanUp();
    }

    @Test
    public void canStartANewProject() throws Exception {
        window.menuItemWithPath("File", "New Project...").click();
        window.dialog().textBox().enterText("My New Test Project");
        assertThat("", containsString(""));
    }


}