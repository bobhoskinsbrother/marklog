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

public class RenamingADirectoryFunctionalTest {

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
    public void canRenameDirectoryDirectly() throws Exception {
        File directory = new File(PROJECT_DIRECTORY, "ImASubFolder");
        directory.mkdirs();
        assertThat(directory, isCreatedWithin(1000));
        openProjectIn(window);
        final JPanelFixture fileSystemTree = window.panel("fileSystemTree");
        fileSystemTree.requireVisible();

        final JTreeFixture tree = fileSystemTree.tree();
        tree.clickPath("test_project/" + directory.getName());
        tree.pressKey(VK_F2).releaseKey(VK_F2);

        final JTextComponentFixture textField = window.textBox("textField");
        assertThat(textField.text(), is("ImASubFolder"));

        textField.setText("new_directory_name").pressAndReleaseKeys(VK_ENTER);

        assertThat(new File(PROJECT_DIRECTORY, "new_directory_name"), isCreatedWithin(1000));
        assertThat(directory, isDestroyedWithin(1000));
    }

    @Test
    public void canRenameDirectoryViaRightClick() throws Exception {
        File directory = new File(PROJECT_DIRECTORY, "ImASubFolder");
        directory.mkdirs();
        assertThat(directory, isCreatedWithin(1000));

        openProjectIn(window);
        final JPanelFixture fileSystemTree = window.panel("fileSystemTree");
        fileSystemTree.requireVisible();

        final JTreeFixture tree = fileSystemTree.tree();
        tree.rightClickPath("test_project/" + directory.getName());
        window.menuItem("renameFile").click();
        final DialogFixture renameFileDialog = window.dialog("renameFileDialog");
        final JTextComponentFixture newFileName = renameFileDialog.textBox("newFileName");
        assertThat(newFileName.text(), is("ImASubFolder"));
        newFileName.setText("new_directory_name");
        renameFileDialog.button("renameButton").click();

        assertThat(new File(PROJECT_DIRECTORY, "new_directory_name"), isCreatedWithin(1000));
        assertThat(directory, isDestroyedWithin(1000));
    }

    @Test
    public void canRenameDirectoryViaDoubleTapF2() throws Exception {
        File directory = new File(PROJECT_DIRECTORY, "ImASubFolder");
        directory.mkdirs();
        assertThat(directory, isCreatedWithin(1000));

        openProjectIn(window);
        final JPanelFixture fileSystemTree = window.panel("fileSystemTree");
        fileSystemTree.requireVisible();

        final JTreeFixture tree = fileSystemTree.tree();
        tree.clickPath("test_project/" + directory.getName());

        tree.pressAndReleaseKeys(VK_F2,VK_F2);


        final DialogFixture renameFileDialog = window.dialog("renameFileDialog");
        final JTextComponentFixture newFileName = renameFileDialog.textBox("newFileName");
        assertThat(newFileName.text(), is("ImASubFolder"));
        newFileName.setText("new_directory_name");
        renameFileDialog.button("renameButton").click();

        assertThat(new File(PROJECT_DIRECTORY, "new_directory_name"), isCreatedWithin(1000));
        assertThat(directory, isDestroyedWithin(1000));
    }

}