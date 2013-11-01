package uk.co.itstherules.marklog.editor;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.junit.*;
import uk.co.itstherules.marklog.string.MakeString;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

@Ignore
public class MakingANewProjectWithTheMarkdownAppFunctionalTest {

    private FrameFixture window;
    private final File tempDir = new File(System.getProperty("java.io.tmpdir"));
    private final File projectDir = new File(tempDir, "test_project");
    private final File projectFile = new File(projectDir, "MyNewTestProject.marklog");

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUp() {
        assertThat(projectFile.delete(), is(true));
        assertThat(projectDir.delete(), is(true));
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
        final DialogFixture dialog = window.dialog();
        final String projectName = "My New Test Project";
        dialog.textBox().enterText(projectName);
        dialog.panel("directoryChooser").button("chooseDirectoryButton").click();
        dialog.fileChooser().fileNameTextBox().enterText(projectDir.getAbsolutePath());
        dialog.fileChooser().approve();
        dialog.button("createButton").click();
        assertThat(projectFile.exists(), is(true));
        final String projectFileString = MakeString.from(projectFile);
        assertThat(projectFileString, containsString("project.name="+projectName));
        assertThat(projectFileString, containsString("project.directory="+projectDir.getAbsolutePath()));
    }

}