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
    public void tearDown() throws IOException {
        window.cleanUp();
        reset();
    }

    @Test
    public void canStartANewProject() throws Exception {
        window.menuItemWithPath("File", "New Project...").click();
        final DialogFixture dialog = window.dialog();
        final String ftpHost = "my.host.com";
        final String ftpUserName = "bigdaddy";
        final String ftpPassword = "scareyBadgers";
        dialog.textBox("projectName").setText(PROJECT_NAME);
        dialog.panel("directoryChooser").button("chooseDirectoryButton").click();
        dialog.fileChooser().fileNameTextBox().setText(PROJECT_DIRECTORY.getAbsolutePath());
        dialog.fileChooser().approve();
        dialog.textBox("ftpHost").setText(ftpHost);
        dialog.textBox("ftpUserName").setText(ftpUserName);
        dialog.textBox("ftpPassword").setText(ftpPassword);
        dialog.button("saveProject").click();

        assertThat(PROJECT_FILE, isCreatedWithin(2000));

        String projectFileString = MakeString.from(PROJECT_FILE);
        assertThat(projectFileString, containsString("project.name=" + PROJECT_NAME));
        assertThat(projectFileString, containsString("project.ftp.host=" + ftpHost));
        assertThat(projectFileString, containsString("project.ftp.username=" + ftpUserName));
        assertThat(projectFileString, containsString("project.ftp.password=" + ftpPassword));
        assertThat(projectFileString, containsString("project.directory=" + PROJECT_DIRECTORY.getAbsolutePath()));
    }

}