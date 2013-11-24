package uk.co.itstherules.marklog.publisher;

import org.apache.commons.io.FileUtils;
import uk.co.itstherules.marklog.actions.UpdateReporter;
import uk.co.itstherules.marklog.editor.model.Link;
import uk.co.itstherules.marklog.editor.model.Post;
import uk.co.itstherules.marklog.editor.model.PostService;
import uk.co.itstherules.marklog.editor.model.ProjectConfiguration;
import uk.co.itstherules.marklog.filesystem.FilePaths;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import static uk.co.itstherules.marklog.editor.model.PostHeader.PostStage.publish;

public final class HtmlPublisher {

    private final ProjectConfiguration configuration;
    private final File targetDirectory;
    private final UpdateReporter reporter;
    private final PostService service;

    public HtmlPublisher(ProjectConfiguration configuration, File targetDirectory, UpdateReporter reporter, PostService service) {
        this.configuration = configuration;
        this.targetDirectory = targetDirectory;
        this.reporter = reporter;
        this.service = service;
    }

    public String makePosts(String templateName, String title, List<Post> posts) {
        return new TemplateProvider(templateName, service).makePosts(title, posts);
    }

    public static String makePost(String templateName, Post post, PostService service) {
        return new TemplateProvider(templateName, service).makePost(post);
    }

    public void publishUsingTemplate(String templateName, boolean copyOriginals) {
        File projectDirectory = configuration.getDirectory();

        reporter.report("Starting to publish your blog");
        resetTargetDirectory();

        publishTop10Posts(templateName);
        publishTags(templateName);
        publishArchives(templateName);
        publishIndividualPosts(templateName, projectDirectory, FilePaths.canonicalFor(projectDirectory), copyOriginals);
        copyTemplateAssets();

        reporter.success("Successfully published your blog!");
    }

    private void copyTemplateAssets() {
        reporter.report("Copying template assets");
        final File templates;
        try {
            templates = new File("templates").getCanonicalFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final File[] files = templates.listFiles(new FilenameFilter() {
            @Override public boolean accept(File file, String s) {
                return !file.getName().endsWith(".ftl");
            }
        });
        for (File source : files) {
            File targetFile = new File(targetDirectory, relativeFilePath(FilePaths.canonicalFor(templates), source));
            try {
                if(source.isDirectory()) {
                    FileUtils.copyDirectory(source, targetFile);
                } else {
                    FileUtils.copyFile(source, targetFile);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        reporter.success("Successfully copied template assets");
    }

    private void resetTargetDirectory() {
        deleteTargetIfExists();
        targetDirectory.mkdirs();
        reporter.success("Made target project directory");
    }

    private void publishTags(String templateName) {
        reporter.report("Publish all tag pages from project directory");
        final List<Link> links = service.tagsLinks();
        for (Link link : links) {
            final String tag = link.getText();
            String postsString = makePosts(templateName, configuration.getName(), service.postsForTag(tag));
            File targetFile = new File(targetDirectory, link.getLocation());
            writeFileWithReport(postsString, targetFile);
        }
        reporter.success("Successfully published all tag pages");
    }

    private void publishArchives(String templateName) {
        reporter.report("Publish all archives from project directory");
        final List<Link> links = service.archivesLinks();
        for (Link link : links) {
            final String archiveText = link.getText();
            String postsString = makePosts(templateName, configuration.getName(), service.postsForArchive(archiveText));
            File targetFile = new File(targetDirectory, link.getLocation());
            writeFileWithReport(postsString, targetFile);
        }
        reporter.success("Successfully published all archives pages");
    }

    private void publishTop10Posts(String templateName) {
        reporter.report("Publish top 10 posts from project directory");
        String postsString = makePosts(templateName, configuration.getName(), service.tenNewestPosts());
        writeHtml("index.md", postsString);
        reporter.success("Successfully published top 10 posts");
    }

    private void deleteTargetIfExists() {
        if (targetDirectory.exists()) {
            reporter.report("Going to delete existing target directory (at ", FilePaths.canonicalFor(targetDirectory), ")");
            deleteTarget();
            reporter.report("Deleted existing target directory");
        }
    }

    private void deleteTarget() {
        try {
            FileUtils.deleteDirectory(targetDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void publishIndividualPosts(String templateName, File directory, final String canonicalRoot, boolean copyOriginals) {
        reporter.report("About to publish individual posts from project directory");
        if (copyOriginals) {
            reporter.report("The original .md files will also be copied");
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File sourceFile : files) {
                if (sourceFile.isDirectory()) {
                    publishIndividualPosts(templateName, sourceFile, canonicalRoot, copyOriginals);
                } else {
                    String sourceFileName = sourceFile.getName();
                    String relativePath = relativeFilePath(canonicalRoot, sourceFile);
                    reporter.success("Found file: ", relativePath);
                    if (sourceFileName.endsWith(".md")) {
                        reporter.report("    It's a markdown file, so we'll transform it to html");
                        if (copyOriginals) {
                            copyMarkdown(sourceFile, relativePath);
                        }
                        Post post = new Post(sourceFile);
                        writeHtmlFrom(post, templateName, relativePath);
                    } else if (!sourceFileName.endsWith(".marklog")) {
                        reporter.report("    It's not a markdown file, so we'll copy it");
                        copyFile(sourceFile, relativePath);
                    } else {
                        reporter.error("    It's a marklog file, so we won't copy it");
                    }
                }
            }
        }
        reporter.success("Successfully published individual posts");
    }


    private void writeHtmlFrom(Post post, String templateName, String relativePath) {
        if (post.getHeader().getStage() == publish) {
            String reply = makePost(templateName, post, service);
            writeHtml(relativePath, reply);
        }
    }

    private void writeHtml(String relativePath, String reply) {
        File targetFile = new File(targetDirectory, switchMdForHtml(relativePath));
        writeFileWithReport(reply, targetFile);
    }

    private void writeFileWithReport(String reply, File targetFile) {
        reporter.report("    Html file is: " + targetFile.getAbsolutePath());
        makeParentIfNotExists(targetFile);
        reporter.report("    About to write html file: " + targetFile.getAbsolutePath());
        writeFile(reply, targetFile);
        reporter.success("    Wrote html file: " + targetFile.getAbsolutePath());
    }

    private void copyMarkdown(File sourceFile, String relativePath) {
        File targetMdFile = new File(targetDirectory, "markdown/" + relativePath);
        makeParentIfNotExists(targetMdFile);
        copyFile(sourceFile, targetMdFile);
    }

    private void copyFile(File sourceFile, String relativePath) {
        File targetFile = new File(targetDirectory, relativePath);
        reporter.report("    About to copy file: " + FilePaths.canonicalFor(sourceFile) +" to: "+ FilePaths.canonicalFor(targetFile));
        makeParentIfNotExists(targetFile);
        copyFile(sourceFile, targetFile);
        reporter.success("    File successfully copied");
    }

    private void copyFile(File file, File targetMdFile) {
        try {
            FileUtils.copyFile(file, targetMdFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String switchMdForHtml(String filePath) {
        return service.markdownAsHtml(filePath);
    }

    private String relativeFilePath(String canonicalRoot, File file) {
        return FilePaths.canonicalFor(file).substring(canonicalRoot.length());
    }

    private void makeParentIfNotExists(File targetFile) {
        File parentFile = targetFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
    }

    private void writeFile(String reply, File targetFile) {
        try {
            final FileWriter writer = new FileWriter(targetFile);
            writer.write(reply);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            throw new RuntimeException("Usage: uk.co.itstherules.marklog.publisher.HtmlPublisher <marklog configuration file> <target directory> <!optional copy original md files>");
        }
        File confFile = new File(args[0]);
        if (!confFile.exists()) {
            throw new RuntimeException("Project Configuration file (" + FilePaths.canonicalFor(confFile) + ") doesn't exist");
        }
        boolean copyOriginals = (args.length == 3 ? Boolean.getBoolean(args[3]) : false);
        ProjectConfiguration configuration = new ProjectConfiguration();
        configuration.load(confFile);
        PostService service = new PostService(configuration.getDirectory());
        HtmlPublisher publisher = new HtmlPublisher(configuration, new File(args[1]), sysOutReporter(), service);
        publisher.publishUsingTemplate("simple", copyOriginals);
    }

    private static UpdateReporter sysOutReporter() {
        return new UpdateReporter() {
            @Override public void report(String... toReport) {
                for (String s : toReport) {
                    System.out.println(s);
                }
                System.out.println("\n");
            }

            @Override public void error(String... toReport) {
                for (String s : toReport) {
                    System.err.println(s);
                }
                System.err.println("\n");
            }

            @Override public void success(String... success) {
                report(success);
            }
        };
    }


}
