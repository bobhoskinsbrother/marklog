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

public class DeletingAPostFunctionalTest {

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
    public void canDeleteAPost() throws Exception {
        final File post = new File(PROJECT_DIRECTORY, "ImAFile.md");
        FileUtils.touch(post);

        openProjectIn(window);

        final JPanelFixture fileSystemTree = window.panel("fileSystemTree");
        fileSystemTree.requireVisible();
        final JTreeFixture tree = fileSystemTree.tree();
        tree.rightClickPath("test_project/ImAFile.md");
        window.menuItem("deletePost").click();
        assertThat(post.exists(), is(false));
    }

}