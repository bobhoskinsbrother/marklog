package uk.co.itstherules.marklog.editor;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.fixture.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_F2;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.co.itstherules.marklog.editor.CommonProjectActions.*;

public class RenamingAFileFunctionalTest {

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
    public void canRenameFileDirectly() throws Exception {
        File file = new File(PROJECT_DIRECTORY, "im_a_post.md");
        file.mkdirs();
        assertThat(file, isCreatedWithin(1000));
        openProjectIn(window);
        final JPanelFixture fileSystemTree = window.panel("fileSystemTree");
        fileSystemTree.requireVisible();

        final JTreeFixture tree = fileSystemTree.tree();
        tree.clickPath("test_project/" + file.getName());
        tree.pressKey(VK_F2).releaseKey(VK_F2);

        final JTextComponentFixture textField = window.textBox("textField");
        assertThat(textField.text(), is("im_a_post.md"));

        textField.setText("im_a_renamed_post.md").pressAndReleaseKeys(VK_ENTER);

        assertThat(new File(PROJECT_DIRECTORY, "im_a_renamed_post.md").exists(), is(true));
        assertThat(file.exists(), is(false));
    }

    @Test
    public void canRenameFileViaRightClick() throws Exception {
        File file = new File(PROJECT_DIRECTORY, "im_a_post.md");
        file.mkdirs();
        assertThat(file, isCreatedWithin(1000));

        openProjectIn(window);
        final JPanelFixture fileSystemTree = window.panel("fileSystemTree");
        fileSystemTree.requireVisible();

        final JTreeFixture tree = fileSystemTree.tree();
        tree.rightClickPath("test_project/" + file.getName());
        window.menuItem("renameFile").click();
        final DialogFixture renameFileDialog = window.dialog("renameFileDialog");
        final JTextComponentFixture newFileName = renameFileDialog.textBox("newFileName");
        assertThat(newFileName.text(), is("im_a_post.md"));
        newFileName.setText("im_a_renamed_post.md");
        renameFileDialog.button("renameButton").click();

        assertThat(new File(PROJECT_DIRECTORY, "im_a_renamed_post.md"), isCreatedWithin(1000));
        assertThat(file, isDestroyedWithin(1000));
    }

    @Test
    public void canRenameFileViaDoubleTapF2() throws Exception {
        File file = new File(PROJECT_DIRECTORY, "im_a_post.md");
        file.mkdirs();
        assertThat(file, isCreatedWithin(1000));

        openProjectIn(window);
        final JPanelFixture fileSystemTree = window.panel("fileSystemTree");
        fileSystemTree.requireVisible();

        final JTreeFixture tree = fileSystemTree.tree();
        tree.clickPath("test_project/" + file.getName());

        tree.pressAndReleaseKeys(VK_F2, VK_F2);


        final DialogFixture renameFileDialog = window.dialog("renameFileDialog");
        final JTextComponentFixture newFileName = renameFileDialog.textBox("newFileName");
        assertThat(newFileName.text(), is("im_a_post.md"));
        newFileName.setText("im_a_renamed_post.md");
        renameFileDialog.button("renameButton").click();

        assertThat(new File(PROJECT_DIRECTORY, "im_a_renamed_post.md"), isCreatedWithin(1000));
        assertThat(file, isDestroyedWithin(1000));
    }

}