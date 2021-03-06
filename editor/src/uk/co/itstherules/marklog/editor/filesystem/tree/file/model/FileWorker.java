package uk.co.itstherules.marklog.editor.filesystem.tree.file.model;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWorker extends SwingWorker<Void, PropertyChangeEvent> {

    private final Map<WatchKey, Path> keys;
    private final File root;
    private WatchService watcher;

    public FileWorker(File file) {
        root = file;
        try {
            watcher = FileSystems.getDefault().newWatchService();
            keys = new HashMap<WatchKey, Path>();
            registerAll(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void register(Path directory) {
        try {
            final WatchKey key = directory.register(watcher, ENTRY_DELETE, ENTRY_CREATE, ENTRY_MODIFY);
            keys.put(key, directory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerAll(final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    protected Void doInBackground() throws Exception {
        while (true) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return null;
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }
                Path name = Path.class.cast(event.context());
                Path directory = keys.get(key);
                Path child = directory.resolve(name);
                if (child.toFile().isDirectory()) {
                    registerAll(child);
                }
                publish(createChangeEvent(child, event.kind().name()));
            }
            boolean valid = key.reset();
            if (!valid) {
                final Path path = keys.get(key);
                publish(createChangeEvent(path, ENTRY_DELETE.name()));
                keys.remove(key);
            }
        }
    }

    protected PropertyChangeEvent createChangeEvent(Path child, String eventName) {
        return new PropertyChangeEvent(this, eventName, null, child.toFile());
    }

    @Override
    protected void process(List<PropertyChangeEvent> chunks) {
        super.process(chunks);
        for (PropertyChangeEvent event : chunks) {
            getPropertyChangeSupport().firePropertyChange(event);
        }
    }
}