package uk.co.itstherules.marklog.string;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class CombineAFewStringManipulatorsTest {

    @Test public void chainTransform() {
        CompositeStringManipulator unit = new CompositeStringManipulator(new Hyphenate(),new Append(".ext"));
        assertThat(unit.manipulate("some*text,,to\"?change"), is("some-text-to-change.ext"));
    }

}
