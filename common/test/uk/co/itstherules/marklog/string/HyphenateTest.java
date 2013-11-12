package uk.co.itstherules.marklog.string;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class HyphenateTest {

    @Test public void hyphenate() {
        final Hyphenate unit = new Hyphenate();
        assertThat(unit.manipulate("some text \\ to change"), is("some-text-to-change"));
        assertThat(unit.manipulate("some:text/to<>change"), is("some-text-to-change"));
        assertThat(unit.manipulate("some*text,,to\"?change"), is("some-text-to-change"));
        assertThat(unit.manipulate("some.text----to change"), is("some-text-to-change"));

    }

}
