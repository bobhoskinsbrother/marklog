package uk.co.itstherules.marklog.string;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class AppendTest {

    @Test public void append() {
        final Append unit = new Append(".ext");
        assertThat(unit.manipulate("some_change"), is("some_change.ext"));
    }

}
