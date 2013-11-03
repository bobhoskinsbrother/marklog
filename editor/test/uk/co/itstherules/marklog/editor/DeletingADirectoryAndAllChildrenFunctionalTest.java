package uk.co.itstherules.marklog.editor;

import org.apache.commons.io.FileUtils;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.co.itstherules.marklog.editor.CommonProjectActions.*;

public class DeletingADirectoryAndAllChildrenFunctionalTest {

    private FrameFixture window;

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUp() throws IOException {
        reset();
        makeProjectFile();
        window = openApp();
    }

    @After
    public void tearDown() throws IOException {
        window.cleanUp();
        reset();
    }

    @Test
    public void canDeleteDirectoryAndAllTheChildren() throws Exception {
        File grandchildDirectory = new File(PROJECT_DIRECTORY, "ImASubFolder/ImASubSubFolder");
        File childDirectory = grandchildDirectory.getParentFile();
        grandchildDirectory.mkdirs();

        final File grandchildFile = new File(childDirectory, "ImAFile.md");
        FileUtils.touch(grandchildFile);

        assertThat(grandchildDirectory, isCreatedWithin(1000));
        assertThat(grandchildFile, isCreatedWithin(1000));

        openProjectIn(window);

        final JPanelFixture fileSystemTree = window.panel("fileSystemTree");
        fileSystemTree.requireVisible();

        final JTreeFixture tree = fileSystemTree.tree();
        tree.rightClickPath("test_project/" + childDirectory.getName());
        window.menuItem("deleteDirectory").click();
        window.dialog().radioButton("deleteFilesUnderneath").click();
        window.dialog().button("deleteDirectory").click();
        assertThat(PROJECT_FILE.exists(), is(true));
        assertThat(PROJECT_DIRECTORY.exists(), is(true));

        assertThat(grandchildDirectory.exists(), is(false));
        assertThat(childDirectory.exists(), is(false));
        assertThat(grandchildFile.exists(), is(false));
    }

}