package uk.co.itstherules.marklog.editor.model;

import uk.co.itstherules.marklog.filesystem.FilePaths;
import uk.co.itstherules.marklog.string.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static uk.co.itstherules.marklog.editor.model.PostHeader.PostStage.publish;

public final class PostService {

    public static final String POSTS_DIRECTORY = "posts";
    private final File root;
    private File postsDirectory;

    public PostService(File root) {
        this.root = root;
        this.postsDirectory = new File(root, POSTS_DIRECTORY);
    }

    public List<Post> tenNewestPosts() {
        final List<Post> posts = allPosts();
        final List<Post> reply = new ArrayList<>();
        Collections.sort(posts, new NewestPostComparator());
        int count = 0;
        for (Post post : posts) {
            if (post.getHeader().getStage() == publish) {
                reply.add(post);
                count++;
                if (count == 10)
                    break;
            }
        }
        return reply;
    }

    public List<Link> headerLinks() {
        Set<Link> reply = new LinkedHashSet<>();
        final File[] files = root.listFiles();
        if (files != null) {
            for (File file : files) {
                if(file.isDirectory()) {
                    final List<String> names = Arrays.asList(file.list());
                    if(names.contains("index.md")) {
                        final String name = file.getName();
                        reply.add(new Link("/"+ name +"/index.html", name));
                    }
                }
            }
        }
        return new ArrayList<>(reply);
    }

    public List<Link> tagsLinks() {
        Set<Link> reply = new LinkedHashSet<>();
        final List<Post> posts = allPosts();
        for (Post post : posts) {
            final List<String> tags = post.getHeader().getTags();
            for (String tag : tags) {
                String fileName = new FileifyTitle(".html").manipulate(tag);
                tag = new Capitalize().manipulate(tag);
                Link link = new Link("/tags/" + fileName, tag);
                if (!reply.contains(link)) {
                    reply.add(link);
                }
            }
        }
        return new ArrayList<>(reply);
    }

    public List<Link> archivesLinks() {
        Set<Link> interim = new LinkedHashSet<>();
        final List<Post> posts = allPosts();
        for (Post post : posts) {
            Date date = post.getHeader().getDate();
            DateFormat formatter = monthYearFormatter();
            String monthYear = formatter.format(date);
            String fileName = new FileifyTitle(".html").manipulate(monthYear);
            Link link = new Link("/archives/" + fileName, monthYear);
            if (!interim.contains(link)) {
                interim.add(link);
            }
        }
        List<Link> reply = new ArrayList<>(interim);
        Collections.sort(reply, new MonthYearComparator());
        return reply;
    }

    public List<Post> allPosts() {
        return collectPosts(postsDirectory);
    }

    public String pathRelativeToRoot(File file) {
        String rootPath = FilePaths.canonicalFor(root);
        return FilePaths.canonicalFor(file).substring(rootPath.length() + 1);
    }

    public String relativeHtmlPathFor(Post post) {
        String rootPath = FilePaths.canonicalFor(root);
        final File file = post.getFile();
        final String htmlName = markdownAsHtml(file.getName());
        String reply = FilePaths.canonicalFor(file.getParentFile()).substring(rootPath.length());
        return reply + "/" + htmlName;
    }

    public URL projectRoot() {
        try {
            return root.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String markdownAsHtml(String filePath) {
        return new CompositeStringManipulator(new Chomp(".md"), new Append(".html")).manipulate(filePath);
    }

    public List<Post> postsForTag(String tag) {
        final List<Post> posts = allPosts();
        final Set<Post> set = new LinkedHashSet<>();
        for (Post post : posts) {
            final List<String> tags = post.getHeader().getTags();
            for (String postTag : tags) {
                if (postTag.equalsIgnoreCase(tag)) {
                    set.add(post);
                }
            }
            if (tags.contains(tag)) {
                set.add(post);
            }
        }
        List<Post> reply = new ArrayList<>(set);
        Collections.sort(reply, new NewestPostComparator());
        return reply;
    }

    public List<Post> postsForArchive(String archiveText) {
        final List<Post> posts = allPosts();
        final List<Post> reply = new ArrayList<>();
        for (Post post : posts) {
            final Date date = post.getHeader().getDate();
            if (monthYearFormatter().format(date).equals(archiveText)) {
                reply.add(post);
            }
        }
        Collections.sort(reply, new NewestPostComparator());
        return reply;
    }

    private DateFormat monthYearFormatter() {
        return new SimpleDateFormat("MMMMM yyyy");
    }

    private List<Post> collectPosts(File directory) {
        List<Post> posts = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File sourceFile : files) {
                if (sourceFile.isDirectory()) {
                    posts.addAll(collectPosts(sourceFile));
                } else {
                    if (sourceFile.getName().endsWith(".md")) {
                        posts.add(new Post(sourceFile));
                    }
                }
            }
        }
        return posts;
    }

    private static class NewestPostComparator implements Comparator<Post> {

        @Override public int compare(Post post, Post post2) {
            return post.getHeader().getDate().before(post2.getHeader().getDate()) ? 1 : -1;
        }
    }

    private class MonthYearComparator implements Comparator<Link> {

        @Override public int compare(Link link1, Link link2) {
            try {
                Date dateOne = monthYearFormatter().parse(link1.getText());
                Date dateTwo = monthYearFormatter().parse(link2.getText());
                return dateOne.before(dateTwo) ? 1 : -1;
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
