package uk.co.itstherules.marklog.editor.model;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.co.itstherules.marklog.string.MakeString;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class PostTest {
    
    private static final File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
    public static final File PROJECT_DIRECTORY = new File(TEMP_DIRECTORY, "test_project");
    

    private static final String HEADER_STRING =
            "#####################\n" +
            "author:admin\n" +
            "date:23/06/2010 13:28\n" +
            "title:Epicenter Dublin Update\n" +
            "tags:Conferences\n" +
            "#####################\n";
    private static final String MARKDOWN_STRING =
            "\n" +
            "\n" +
            "Hi Folks\n" +
            "\n" +
            "It appears that the link to the epicenter session is now down.\n" +
            "\n" +
            "The excerpt for the session was as follows:\n" +
            "\n" +
            "**How A Little Bit of Philosophy Can Affect The Products You Build**\n" +
            "\n" +
            "I will be presenting a look at StoryBoard, a planning tool with a little bit of a difference.\n" +
            "I will be explaining the overall technical approach of distributed, self-contained, collaborative web-applications, built by applying a few simple, adaptable philosophies to produce great results.\n" +
            "A few of which are:\n" +
            "- \"Do The Simplest Thing That Could Possibly Work\" shouldn't mean barely useable\n" +
            "- Write fewer, more elegant features, which are used more often\n" +
            "- Intuition can be taught, if the conventions are simple enough (a.k.a. people aren't dumb)\n";
    private static final String POST_STRING = HEADER_STRING+ MARKDOWN_STRING;

    public static void reset() throws IOException {
        FileUtils.deleteDirectory(PROJECT_DIRECTORY);
        PROJECT_DIRECTORY.mkdirs();
    }
   
    @Before
    public void setUp() throws IOException {
        reset();
    }

    @After
    public void tearDown() throws IOException {
        reset();
    }

    @Test
    public void canSaveAPost() throws Exception {
        File file = new File(PROJECT_DIRECTORY, "im-a-file.md");

        Post unit = new Post(PROJECT_DIRECTORY, "Im A File");
        unit.setText(POST_STRING);
        unit.save();
        String writtenPost = MakeString.from(file);
        assertThat(writtenPost, is(POST_STRING));
    }

    @Test
    public void canLoadAPost() throws Exception {
        File file = new File(PROJECT_DIRECTORY, "im-a-file.md");
        makeFile(file);

        Post unit = new Post(file);
        assertThat(unit.toString(), is(POST_STRING));
        assertThat(unit.getHeader().toString(), is(HEADER_STRING));
        assertThat(unit.getMarkdown(), is(MARKDOWN_STRING));
    }

    private void makeFile(File file) throws IOException {
        final FileWriter writer = new FileWriter(file);
        writer.write(POST_STRING);
        writer.close();
    }

    @Test public void canParseHeader() {
        final Post post = new Post(new File(System.getProperty("user.dir")), "A Post");
        post.setText(POST_STRING);
        assertThat(post.getMarkdown(), is("\n" +
                "\n" +
                "Hi Folks\n" +
                "\n" +
                "It appears that the link to the epicenter session is now down.\n" +
                "\n" +
                "The excerpt for the session was as follows:\n" +
                "\n" +
                "**How A Little Bit of Philosophy Can Affect The Products You Build**\n" +
                "\n" +
                "I will be presenting a look at StoryBoard, a planning tool with a little bit of a difference.\n" +
                "I will be explaining the overall technical approach of distributed, self-contained, collaborative web-applications, built by applying a few simple, adaptable philosophies to produce great results.\n" +
                "A few of which are:\n" +
                "- \"Do The Simplest Thing That Could Possibly Work\" shouldn't mean barely useable\n" +
                "- Write fewer, more elegant features, which are used more often\n" +
                "- Intuition can be taught, if the conventions are simple enough (a.k.a. people aren't dumb)\n"));
        assertThat(post.getHeader().toString(), is("#####################\n" +
                "author:admin\n" +
                "date:23/06/2010 13:28\n" +
                "title:Epicenter Dublin Update\n" +
                "tags:Conferences\n" +
                "#####################\n"));
    }


}
