package uk.co.itstherules.marklog.publisher;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import uk.co.itstherules.marklog.actions.UpdateReporter;
import uk.co.itstherules.marklog.editor.model.ProjectConfiguration;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class HtmlPublisherTest {

    public static final File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
    public static final File TARGET_DIRECTORY = new File(TEMP_DIRECTORY + "/.marklog/test");

    public static Matcher<File> existsWithin(final long millis) {
        return new TypeSafeMatcher<File>() {
            @Override public boolean matchesSafely(File file) {
                long currentMillis = System.currentTimeMillis();
                while ((currentMillis + millis) > System.currentTimeMillis()) {
                    if (file.exists()) {
                        return true;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return false;
                    }
                }
                return false;
            }

            @Override public void describeTo(Description description) {
                description.appendText("File not created within " + millis + " milliseconds");
            }
        };
    }

    @Before public void reset() throws Exception {
        FileUtils.deleteDirectory(TARGET_DIRECTORY);
        TARGET_DIRECTORY.mkdirs();
    }

    @Test public void canPublish() {
        File configFile = new File("publisher/test_resource/test_blog/blog.marklog");
        ProjectConfiguration configuration = new ProjectConfiguration();
        configuration.load(configFile);
        HtmlPublisher unit = new HtmlPublisher(configuration, TARGET_DIRECTORY, reporter());
        unit.publishUsingTemplate("simple", true);
        assertThat(file("index.html"), existsWithin(1000));
        assertThat(file("sub/sub.html"), existsWithin(1000));
        assertThat(file("not_ready_for_publishing_yet.html").exists(), is(false));
        assertThat(file("markdown"), existsWithin(1000));
        assertThat(file("markdown/index.md"), existsWithin(1000));
        assertThat(file("markdown/sub/sub.md"), existsWithin(1000));
        assertThat(file("markdown/not_ready_for_publishing_yet.md"), existsWithin(1000));
        assertThat(file("blog.marklog").exists(), is(false));
        assertThat(file("images/sync.png"), existsWithin(1000));
    }

    private UpdateReporter reporter() {
        return new UpdateReporter() {
            @Override public void report(String... toReport) {
                //ignore
            }

            @Override public void error(String... toReport) {
                //ignore
            }

            @Override public void success(String... success) {
                //ignore
            }
        };
    }

    @Test public void canPublishWithoutOriginals() {
        File configFile = new File("publisher/test_resource/test_blog/blog.marklog");
        ProjectConfiguration configuration = new ProjectConfiguration();
        configuration.load(configFile);
        HtmlPublisher unit = new HtmlPublisher(configuration, TARGET_DIRECTORY, reporter());
        unit.publishUsingTemplate("simple", false);
        assertThat(file("index.html"), existsWithin(1000));
        assertThat(file("sub/sub.html"), existsWithin(1000));
        assertThat(file("not_ready_for_publishing_yet.html").exists(), is(false));
        assertThat(file("markdown").exists(), is(false));
        assertThat(file("images/sync.png"), existsWithin(1000));
    }

    private File file(String s) {return new File(TARGET_DIRECTORY, s);}

}
