package uk.co.itstherules.marklog.editor.filesystem.tree.file;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class FileWorker extends SwingWorker<Void, PropertyChangeEvent> {

    private Path directory;
    private WatchService watcher;

    public FileWorker(File file) throws IOException {
        directory = file.toPath();
        watcher = FileSystems.getDefault().newWatchService();
        directory.register(watcher, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_CREATE);
    }

    @Override
    protected Void doInBackground() throws Exception {
        for (; ; ) {
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
                publish(createChangeEvent((WatchEvent<Path>) event, key));
            }
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
        return null;
    }

    protected PropertyChangeEvent createChangeEvent(WatchEvent<Path> event, WatchKey key) {
        Path name = event.context();
        Path child = directory.resolve(name);
        PropertyChangeEvent e = new PropertyChangeEvent(this, event.kind().name(), null, child.toFile());
        return e;
    }

    @Override
    protected void process(List<PropertyChangeEvent> chunks) {
        super.process(chunks);
        for (PropertyChangeEvent event : chunks) {
            getPropertyChangeSupport().firePropertyChange(event);
        }
    }
}