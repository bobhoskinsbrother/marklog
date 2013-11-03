package uk.co.itstherules.marklog.editor;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.itstherules.marklog.string.MakeString;

import java.io.IOException;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static uk.co.itstherules.marklog.editor.CommonProjectActions.*;

public class CreatingANewProjectFunctionalTest {

    private FrameFixture window;

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUp() throws IOException {
        reset();
        window = openApp();
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
        dialog.textBox().setText(projectName);
        dialog.panel("directoryChooser").button("chooseDirectoryButton").click();
        dialog.fileChooser().fileNameTextBox().setText(PROJECT_DIRECTORY.getAbsolutePath());
        dialog.fileChooser().approve();
        dialog.button("createButton").click();
        assertThat(PROJECT_FILE, isCreatedWithin(1000));
        final String projectFileString = MakeString.from(PROJECT_FILE);
        assertThat(projectFileString, containsString("project.name=" + projectName));
        assertThat(projectFileString, containsString("project.directory=" + PROJECT_DIRECTORY.getAbsolutePath()));
    }

}