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

public class DeletingADirectoryAndPromoteTheChildrenFunctionalTest {

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
    public void canDeleteDirectoryAndPromoteTheChildren() throws Exception {
        File childDirectory = new File(PROJECT_DIRECTORY, "ImASubFolder");
        File grandchildDirectory = new File(childDirectory, "ImASubSubFolder");
        childDirectory.mkdirs();
        grandchildDirectory.mkdirs();

        File grandchildFile = new File(childDirectory, "ImAFile.md");
        FileUtils.touch(grandchildFile);

        assertThat(grandchildDirectory.exists(), is(true));
        assertThat(grandchildFile.exists(), is(true));

        openProjectIn(window);

        final JPanelFixture fileSystemTree = window.panel("fileSystemTree");
        fileSystemTree.requireVisible();
        final JTreeFixture tree = fileSystemTree.tree();
        tree.rightClickPath("test_project/"+childDirectory.getName());
        window.menuItem("deleteDirectory").click();
        window.dialog().radioButton("moveUpFiles").click();
        window.dialog().button("deleteDirectory").click();

        assertThat(grandchildDirectory.exists(), is(false));
        assertThat(childDirectory.exists(), is(false));
        assertThat(grandchildFile.exists(), is(false));

        assertThat(PROJECT_FILE.exists(), is(true));
        assertThat(PROJECT_DIRECTORY.exists(), is(true));
        assertThat(new File(PROJECT_DIRECTORY, grandchildDirectory.getName()), isCreatedWithin(1000));
        assertThat(new File(PROJECT_DIRECTORY, grandchildFile.getName()), isCreatedWithin(1000));
    }

}