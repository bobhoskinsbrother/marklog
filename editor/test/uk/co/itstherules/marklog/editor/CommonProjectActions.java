package uk.co.itstherules.marklog.editor;

import org.apache.commons.io.FileUtils;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;
import uk.co.itstherules.marklog.editor.model.ProjectConfigurationModel;

import java.io.File;
import java.io.IOException;

public final class CommonProjectActions {

    private static final String PROJECT_NAME = "My New Test Project";
    private static final File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
    public static final File PROJECT_DIRECTORY = new File(TEMP_DIRECTORY, "test_project");
    public static final File PROJECT_FILE = new File(PROJECT_DIRECTORY, "my-new-test-project.marklog");


    private CommonProjectActions(){}

    public static void reset() throws IOException {
        FileUtils.deleteDirectory(PROJECT_DIRECTORY);
        PROJECT_DIRECTORY.mkdirs();
    }

    public static void makeProjectFile() throws IOException {
        final ProjectConfigurationModel configuration = new ProjectConfigurationModel();
        configuration.setDirectory(PROJECT_DIRECTORY);
        configuration.setName(PROJECT_NAME);
        configuration.setFtpUrl("");
        configuration.setFtpUsername("");
        configuration.setFtpPassword("");
        configuration.save();
    }

    public static FrameFixture openProject() throws IOException {
        makeProjectFile();
        FrameFixture window = openApp();
        openProjectIn(window);
        return window;
    }

    public static void openProjectIn(FrameFixture window) {
        window.menuItemWithPath("File", "Open Project...").click();
        window.fileChooser().fileNameTextBox().setText(PROJECT_FILE.getAbsolutePath());
        window.fileChooser().approve();
    }

    public static void newPostIn(FrameFixture window) {
        window.tree().rightClickPath("test_project");
        window.menuItem("addNewPost").click();
        DialogFixture dialog = window.dialog("newPost");
        dialog.textBox("postName").setText("Output from Test");
        dialog.button("createPost").click();
    }

    public static FrameFixture openApp() throws IOException {
        MarklogApp app = GuiActionRunner.execute(new GuiQuery<MarklogApp>() {
            protected MarklogApp executeInEDT() {
                MarklogApp app = new MarklogApp();
                return app;
            }
        });
        FrameFixture window = new FrameFixture(app);
        window.show();
        return window;
    }


    public static Matcher<File> isCreatedWithin(final long millis) {
        return new TypeSafeMatcher<File>() {
            @Override public boolean matchesSafely(File file) {
                long currentMillis = System.currentTimeMillis();
                while((currentMillis+millis) > System.currentTimeMillis()) {
                    if(file.exists()) { return true; }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return false;
                    }
                }
                return false;
            }

            @Override public void describeTo(Description description) {
                description.appendText("File not created within "+millis+" milliseconds");
            }
        };
    }

}
