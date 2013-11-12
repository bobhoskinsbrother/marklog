package uk.co.itstherules.marklog.editor.model;

import org.junit.Before;
import org.junit.Test;
import uk.co.itstherules.marklog.string.MakeString;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.StringContains.containsString;

public class ProjectConfigurationModelTest {

    private final File tempDir = new File(System.getProperty("java.io.tmpdir")+"/tmp_test_dir");
    private final File tempProjectFile = new File(tempDir, "i'm-a-temp-project.marklog");

    @Before
    public void setup() {
        tempDir.delete();
        tempProjectFile.delete();
    }

    @Test
    public void canSaveProject() throws IOException {
        final ProjectConfigurationModel configuration = new ProjectConfigurationModel();
        configuration.setDirectory(tempDir);
        configuration.setName("I'm A Temp Project");
        configuration.setFtpHost("hello.mom");
        configuration.setFtpUsername("fred");
        configuration.setFtpPassword("badgers");

        configuration.save();
        assertThat(tempDir.exists(), is(true));
        assertThat(tempProjectFile.exists(), is(true));

        String reply = MakeString.from(tempProjectFile);

        assertThat(reply, containsString("project.name=I'm A Temp Project"));
        assertThat(reply, containsString("project.directory="+tempDir));
        assertThat(reply, containsString("project.ftp.host=hello.mom"));
        assertThat(reply, containsString("project.ftp.username=fred"));
        assertThat(reply, containsString("project.ftp.password=badgers"));
    }

}
