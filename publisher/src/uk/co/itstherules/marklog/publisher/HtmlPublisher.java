package uk.co.itstherules.marklog.publisher;

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

    public void publishUsingTemplate(String templateName) {
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }
        File directory = configuration.getDirectory();
        String canonicalRoot = FilePaths.canonicalFor(directory);
        walkDirectory(templateName, directory, canonicalRoot);

    }

    private void walkDirectory(String templateName, File directory, final String canonicalRoot) {
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override public boolean accept(File file, String s) {
                return file.isDirectory() || file.getName().endsWith(".md");
            }
        });
        for (File file : files) {
            if (file.isDirectory()) {
                walkDirectory(templateName, file, canonicalRoot);
            } else if (file.getName().endsWith(".md")) {
                Post post = new Post(file);
                if(post.getHeader().getStage() == PostHeader.PostStage.publish) {
                    String reply = makePost(templateName, post);
                    String relativeFilePath = relativeFilePath(canonicalRoot, file);
                    relativeFilePath = switchMdForHtml(relativeFilePath);
                    final File targetFile = new File(targetDirectory, relativeFilePath);
                    makeParentIfNotExists(targetFile);
                    writeFile(reply, targetFile);
                }
            }
        }

    }

    private String switchMdForHtml(String filePath) {
        return new CompositeStringManipulator(new Chomp(".md"), new Append(".html")).manipulate(filePath);
    }

    private String relativeFilePath(String canonicalRoot, File file) {
        return FilePaths.canonicalFor(file).substring(canonicalRoot.length());
    }

    public static String makePost(String templateName, Post post) {
        return new TemplateProvider(templateName).makePost(post);
    }

    private void makeParentIfNotExists(File targetFile) {
        File parentFile = targetFile.getParentFile();
        if (!parentFile.exists()) { parentFile.mkdirs(); }
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
