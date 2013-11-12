package uk.co.itstherules.marklog.editor;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.itstherules.marklog.editor.model.Post;
import uk.co.itstherules.marklog.string.MakeString;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.StringContains.containsString;
import static uk.co.itstherules.marklog.editor.CommonProjectActions.*;

public class OpeningAndEditingAPostFunctionalTest {

    private FrameFixture window;
    private final File postFile = new File(PROJECT_DIRECTORY, "wizards-are-all-evil.md");

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUp() throws IOException {
        reset();
        new Post(PROJECT_DIRECTORY, "Wizards are all Evil").save();
        window = openProject();
    }

    @After
    public void tearDown() throws IOException {
        window.cleanUp();
        reset();
    }

    @Test
    public void canEditExistingPost() throws Exception {
        final JPanelFixture fileSystemTree = window.panel("fileSystemTree");
        fileSystemTree.requireVisible();
        final JTreeFixture tree = fileSystemTree.tree();
        tree.doubleClickPath("test_project/wizards-are-all-evil.md");
        window.textBox("markdownTextArea").enterText("\nEspecially Sauron");
        final String fileContents = MakeString.from(postFile);
        assertThat(fileContents, containsString("Wizards are all Evil"));
        assertThat(fileContents, containsString("Especially Sauron"));
    }

}