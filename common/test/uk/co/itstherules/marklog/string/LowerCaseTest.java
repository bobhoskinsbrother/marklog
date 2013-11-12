package uk.co.itstherules.marklog.string;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class LowerCaseTest {

    @Test public void lowerCase() {
        final LowerCase unit = new LowerCase();
        assertThat(unit.manipulate("some text \\ to chAnGe"), is("some text \\ to change"));
        assertThat(unit.manipulate("soMe:tExt/to<>change"), is("some:text/to<>change"));
        assertThat(unit.manipulate("Some*texT,,to\"?change"), is("some*text,,to\"?change"));
        assertThat(unit.manipulate("some.text----To chaNge"), is("some.text----to change"));

    }

}
