package uk.co.itstherules.marklog.editor.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static uk.co.itstherules.marklog.editor.model.PostHeader.PostStage.*;

public final class PostHeaderTest {

    @Test public void canParseEmptyHeader() {
        final PostHeader unit = new PostHeader("");
        assertThat(unit.getAuthor(), is(""));
        assertThat(unit.getStage(), is(publish));
        assertThat(unit.getTags().size(), is(0));
        assertThat(unit.getTitle(), is(""));
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        final Calendar postInfoCalendar = Calendar.getInstance();
        postInfoCalendar.setTime(unit.getDate());
        assertThat(postInfoCalendar.get(Calendar.YEAR), is(calendar.get(Calendar.YEAR)));
        assertThat(postInfoCalendar.get(Calendar.MONTH), is(calendar.get(Calendar.MONTH)));
        assertThat(postInfoCalendar.get(Calendar.DAY_OF_MONTH), is(calendar.get(Calendar.DAY_OF_MONTH)));
    }

    @Test public void canParseSimpleHeader() {
        final PostHeader unit = new PostHeader(
                "author: Ben Hoskins\n" +
                "date: 23/06/2010 13:28\n" +
                "stage: draft\n" +
                "title: Epicenter Dublin Update\n" +
                "tags: Uncategorisable");
        assertThat(unit.getAuthor(), is("Ben Hoskins"));
        assertThat(unit.getStage(), is(draft));
        assertThat(unit.getTags().size(), is(1));
        assertThat(unit.getTags().get(0), is("Uncategorisable"));
        assertThat(unit.getTitle(), is("Epicenter Dublin Update"));
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(unit.getDate());
        assertThat(calendar.get(Calendar.YEAR), is(2010));
        assertThat(calendar.get(Calendar.MONTH), is(Calendar.JUNE));
        assertThat(calendar.get(Calendar.DAY_OF_MONTH), is(23));
        assertThat(calendar.get(Calendar.HOUR_OF_DAY), is(13));
        assertThat(calendar.get(Calendar.MINUTE), is(28));
    }

    @Test public void canParseDraftCompletePublish() {
        PostHeader unit = new PostHeader("stage: draft\n");
        assertThat(unit.getStage(), is(draft));
        unit = new PostHeader("stage: complete");
        assertThat(unit.getStage(), is(complete));
        unit = new PostHeader("stage: publish");
        assertThat(unit.getStage(), is(publish));
        unit = new PostHeader("stage: crap");
        assertThat(unit.getStage(), is(publish));
        unit = new PostHeader("stage:");
        assertThat(unit.getStage(), is(publish));
    }

    @Test public void canParseTags() {
        PostHeader unit = new PostHeader("tags: rodger, dodger, the jolly wodger\n");
        List<String> tags = unit.getTags();
        assertThat(tags.size(), is(3));
        assertThat(tags.get(0), is("rodger"));
        assertThat(tags.get(1), is("dodger"));
        assertThat(tags.get(2), is("the jolly wodger"));
        unit = new PostHeader("tags: , , \n");
        tags = unit.getTags();
        assertThat(tags.size(), is(0));
        unit = new PostHeader("tags: , ,fred");
        tags = unit.getTags();
        assertThat(tags.size(), is(1));
        assertThat(tags.get(0), is("fred"));
    }

    @Test public void canOutputDefaultString() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 8);
        calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
        calendar.set(Calendar.YEAR, 2013);
        PostHeader unit = new PostHeader("Title", "Author", calendar.getTime(), publish, Arrays.asList("fred","badgers"));
        String reply = unit.toString();
        assertThat(reply, containsString("#####################"));
        assertThat(reply, containsString("tags:fred,badgers"));
        assertThat(reply, containsString("stage:publish"));
        assertThat(reply, containsString("author:Author"));
        assertThat(reply, containsString("title:Title"));
        assertThat(reply, containsString("date:08/11/2013"));
    }

}
