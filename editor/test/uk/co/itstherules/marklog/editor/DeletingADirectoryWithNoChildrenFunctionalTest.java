package uk.co.itstherules.marklog.editor;

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

public class DeletingADirectoryWithNoChildrenFunctionalTest {

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
    public void canDeleteDirectoryWithNoChildren() throws Exception {
        String subFolder = "ImASubFolder";
        File childDirectory = new File(PROJECT_DIRECTORY, subFolder);
        childDirectory.mkdirs();
        assertThat(childDirectory.exists(), is(true));

        openProjectIn(window);

        final JPanelFixture fileSystemTree = window.panel("fileSystemTree");
        fileSystemTree.requireVisible();
        final JTreeFixture tree = fileSystemTree.tree();
        tree.rightClickPath("test_project/" + subFolder);
        window.menuItem("deleteDirectory").click();
        assertThat(childDirectory.exists(), is(false));
    }

}