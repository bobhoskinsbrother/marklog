package uk.co.itstherules.marklog.editor;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertThat;
import static uk.co.itstherules.marklog.editor.CommonProjectActions.*;

public class CreatingANewDirectoryFunctionalTest {

    private FrameFixture window;

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUp() throws IOException {
        reset();
        window = openProject();
    }

    @After
    public void tearDown() throws IOException {
        window.cleanUp();
        reset();
    }

    @Test
    public void canCreateANewDirectory() throws Exception {
        window.tree().rightClickPath("test_project");
        window.menuItem("addNewDirectory").click();
        DialogFixture dialog = window.dialog("newDirectory");
        dialog.textBox("directoryName").setText("New Directory");
        dialog.button("createDirectory").click();
        final File expected = new File(PROJECT_DIRECTORY, "New Directory");
        assertThat(expected, isCreatedWithin(1000));
    }

}