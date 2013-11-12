package uk.co.itstherules.marklog.string;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class VariableNameTest {

    @Test public void variableName() {
        final VariableName unit = new VariableName();
        assertThat(unit.manipulate("some text \\ to change"), is("someTextToChange"));
        assertThat(unit.manipulate("some:text/to<>change"), is("someTextToChange"));
        assertThat(unit.manipulate("some*text,,to\"?change"), is("someTextToChange"));
        assertThat(unit.manipulate("some.text----to change"), is("someTextToChange"));

    }

}
