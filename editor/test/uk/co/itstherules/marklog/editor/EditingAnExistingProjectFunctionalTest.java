package uk.co.itstherules.marklog.editor;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.itstherules.marklog.string.MakeString;

import java.io.IOException;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static uk.co.itstherules.marklog.editor.CommonProjectActions.*;

public class EditingAnExistingProjectFunctionalTest {

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
    public void canEditAnExistingProject() throws Exception {
        makeProjectFile();
        String projectFileString = MakeString.from(PROJECT_FILE);
        assertThat(projectFileString, containsString("project.name=" + PROJECT_NAME));
        assertThat(projectFileString, containsString("project.ftp.host="));
        assertThat(projectFileString, containsString("project.ftp.username="));
        assertThat(projectFileString, containsString("project.ftp.password="));

        window.menuItemWithPath("File", "Open Project...").click();
        window.fileChooser().fileNameTextBox().setText(PROJECT_FILE.getAbsolutePath());
        window.fileChooser().approve();

        JPanelFixture fileSystemTree = window.panel("fileSystemTree");
        fileSystemTree.requireVisible();
        JTreeFixture tree = fileSystemTree.tree();
        tree.rightClickPath("test_project/"+PROJECT_FILE.getName());
        window.menuItem("editProjectSettings").click();


        DialogFixture dialog = window.dialog();
        String ftpHost = "my.host.com";
        String ftpUserName = "bigdaddy";
        String ftpPassword = "scareyBadgers";

        dialog.textBox("projectName").setText(PROJECT_NAME);
        dialog.textBox("ftpHost").setText(ftpHost);
        dialog.textBox("ftpUserName").setText(ftpUserName);
        dialog.textBox("ftpPassword").setText(ftpPassword);
        dialog.button("saveProject").click();

        assertThat(PROJECT_FILE, isCreatedWithin(2000));

        projectFileString = MakeString.from(PROJECT_FILE);
        assertThat(projectFileString, containsString("project.name=" + PROJECT_NAME));
        assertThat(projectFileString, containsString("project.ftp.host=" + ftpHost));
        assertThat(projectFileString, containsString("project.ftp.username=" + ftpUserName));
        assertThat(projectFileString, containsString("project.ftp.password=" + ftpPassword));
    }

}