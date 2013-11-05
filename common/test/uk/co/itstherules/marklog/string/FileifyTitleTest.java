package uk.co.itstherules.marklog.string;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class FileifyTitleTest {

    @Test public void escapingSlashesAndWhatnot() {
        final FileifyTitle unit = new FileifyTitle(".md");
        assertThat(unit.manipulate("some text \\ to change"), is("some-text-to-change.md"));
        assertThat(unit.manipulate("some:text/to<>change"), is("some-text-to-change.md"));
        assertThat(unit.manipulate("some*text,,to\"?change"), is("some-text-to-change.md"));
        assertThat(unit.manipulate("some.text----to change"), is("some-text-to-change.md"));

    }

}
