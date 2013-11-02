package uk.co.itstherules.marklog.editor;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.itstherules.marklog.editor.model.ProjectConfigurationModel;

import java.io.File;

public class OpeningAProjectWithTheMarkdownAppFunctionalTest {

    private FrameFixture window;
    private final File tempDir = new File(System.getProperty("java.io.tmpdir"));
    private final File projectDir = new File(tempDir, "test_project");
    private final File projectFile = new File(projectDir, "MyNewTestProject.marklog");
    private String projectName;

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUp() {
        projectFile.delete();
        projectDir.delete();
        projectName = "My New Test Project";
        new ProjectConfigurationModel(projectDir, projectName).save();
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
        projectFile.delete();
        projectDir.delete();
    }

    @Test
    public void canOpenProject() throws Exception {
        window.menuItemWithPath("File", "Open Project...").click();
        window.fileChooser().fileNameTextBox().enterText(projectFile.getAbsolutePath());
        window.fileChooser().approve();
        window.panel("fileSystemTree").requireVisible();
    }

}