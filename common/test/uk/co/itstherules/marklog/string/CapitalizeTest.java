package uk.co.itstherules.marklog.string;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class CapitalizeTest {

    @Test public void variableName() {
        Capitalize unit = new Capitalize();
        assertThat(unit.manipulate("some text \\ to change"), is("Some Text \\ To Change"));
        assertThat(unit.manipulate("some:text/to<>change"), is("Some:Text/To<>Change"));
        assertThat(unit.manipulate("some*text,,to\"?change"), is("Some*Text,,To\"?Change"));
        assertThat(unit.manipulate("some.text----to change"), is("Some.Text----To Change"));

    }

}
