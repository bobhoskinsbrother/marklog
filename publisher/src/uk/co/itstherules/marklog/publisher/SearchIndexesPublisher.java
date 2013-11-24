package uk.co.itstherules.marklog.publisher;

import uk.co.itstherules.marklog.actions.UpdateReporter;
import uk.co.itstherules.marklog.editor.model.PostService;
import uk.co.itstherules.marklog.string.Append;
import uk.co.itstherules.marklog.string.Chomp;
import uk.co.itstherules.marklog.string.CompositeStringManipulator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class SearchIndexesPublisher {

    private final UpdateReporter reporter;
    private final PostService service;
    private final File targetDirectory;

    public SearchIndexesPublisher(File targetDirectory, UpdateReporter reporter, PostService service) {
        this.targetDirectory = targetDirectory;
        this.reporter = reporter;
        this.service = service;
    }

    public void publish(String templateName) {
        reporter.report("About to publish search indexes from project directory");
        String searchIndexes = makeIndexes(templateName);
        writeJson("posts.json.md", searchIndexes);
        reporter.success("Successfully published search indexes");
    }

    private String makeIndexes(String templateName) {
        return new TemplateProvider(templateName, service).makeSearchIndexes();
    }

    private void writeJson(String relativePath, String reply) {
        File targetFile = new File(targetDirectory, switchJsonMdForJson(relativePath));
        reporter.report("    Json file is: " + targetFile.getAbsolutePath());
        makeParentIfNotExists(targetFile);
        reporter.report("    About to write Json file: " + targetFile.getAbsolutePath());
        writeFile(reply, targetFile);
        reporter.success("    Wrote Json file: " + targetFile.getAbsolutePath());
    }

    private String switchJsonMdForJson(String filePath) {
        return new CompositeStringManipulator(new Chomp(".json.md"), new Append(".json")).manipulate(filePath);
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
