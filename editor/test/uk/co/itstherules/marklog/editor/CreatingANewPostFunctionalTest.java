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

public class CreatingANewPostFunctionalTest {

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
    public void canCreateANewPost() throws Exception {
        window.tree().rightClickPath("test_project");
        window.menuItem("addNewPost").click();
        DialogFixture dialog = window.dialog("newPost");
        dialog.textBox("postName").setText("Output from Test");
        dialog.button("createPost").click();
        final File expected = new File(PROJECT_DIRECTORY, "output-from-test.md");
        assertThat(expected, isCreatedWithin(1000));
    }

}