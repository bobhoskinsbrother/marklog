package uk.co.itstherules.marklog.publisher;

import org.apache.commons.io.FileUtils;
import uk.co.itstherules.marklog.editor.model.Post;
import uk.co.itstherules.marklog.editor.model.PostHeader;
import uk.co.itstherules.marklog.editor.model.ProjectConfiguration;
import uk.co.itstherules.marklog.filesystem.FilePaths;
import uk.co.itstherules.marklog.string.Append;
import uk.co.itstherules.marklog.string.Chomp;
import uk.co.itstherules.marklog.string.CompositeStringManipulator;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

public final class HtmlPublisher {

    private final ProjectConfiguration configuration;
    private final File targetDirectory;

    public HtmlPublisher(ProjectConfiguration configuration, File targetDirectory) {
        this.configuration = configuration;
        this.targetDirectory = targetDirectory;
    }

    public static String makePost(String templateName, Post post) {
        return new TemplateProvider(templateName).makePost(post);
    }

    public void publishUsingTemplate(String templateName, boolean copyOriginals) {
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }
        File directory = configuration.getDirectory();
        String canonicalRoot = FilePaths.canonicalFor(directory);
        walkDirectory(templateName, directory, canonicalRoot, copyOriginals);

    }

    private void walkDirectory(String templateName, File directory, final String canonicalRoot, boolean copyOriginals) {
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override public boolean accept(File file, String s) {
                return file.isDirectory() || file.getName().endsWith(".md");
            }
        });
        for (File sourceFile : files) {
            if (sourceFile.isDirectory()) {
                walkDirectory(templateName, sourceFile, canonicalRoot, copyOriginals);
            } else {
                String sourceFileName = sourceFile.getName();
                String relativePath = relativeFilePath(canonicalRoot, sourceFile);
                if (sourceFileName.endsWith(".md")) {
                    if (copyOriginals) { copyMarkdown(sourceFile, relativePath); }
                    Post post = new Post(sourceFile);
                    writeHtmlFrom(post, templateName, relativePath);
                } else if (!sourceFileName.endsWith(".marklog")) {
                    copyFile(sourceFile, relativePath);
                }
            }
        }

    }

    private void writeHtmlFrom(Post post, String templateName, String relativePath) {
        if (post.getHeader().getStage() == PostHeader.PostStage.publish) {
            String reply = makePost(templateName, post);
            String relativeHtmlFilePath = relativePath;
            relativeHtmlFilePath = switchMdForHtml(relativeHtmlFilePath);
            final File targetFile = new File(targetDirectory, relativeHtmlFilePath);
            makeParentIfNotExists(targetFile);
            writeFile(reply, targetFile);
        }
    }

    private void copyMarkdown(File sourceFile, String relativePath) {
        File targetMdFile = new File(targetDirectory, "markdown/" + relativePath);
        makeParentIfNotExists(targetMdFile);
        copyFile(sourceFile, targetMdFile);
    }

    private void copyFile(File sourceFile, String relativePath) {
        File targetMdFile = new File(targetDirectory, relativePath);
        makeParentIfNotExists(targetMdFile);
        copyFile(sourceFile, targetMdFile);
    }

    private void copyFile(File file, File targetMdFile) {
        try {
            FileUtils.copyFile(file, targetMdFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String switchMdForHtml(String filePath) {
        return new CompositeStringManipulator(new Chomp(".md"), new Append(".html")).manipulate(filePath);
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
}
