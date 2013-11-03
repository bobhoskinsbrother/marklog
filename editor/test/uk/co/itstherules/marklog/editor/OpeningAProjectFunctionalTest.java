package uk.co.itstherules.marklog.editor;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static uk.co.itstherules.marklog.editor.CommonProjectActions.*;

public class OpeningAProjectFunctionalTest {

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
    public void canOpenProject() throws Exception {
        window.menuItemWithPath("File", "Open Project...").click();
        window.fileChooser().fileNameTextBox().setText(PROJECT_FILE.getAbsolutePath());
        window.fileChooser().approve();
        window.panel("fileSystemTree").requireVisible();
    }

}