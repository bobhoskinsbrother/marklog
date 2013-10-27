package uk.co.itstherules.marklog.execute;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public final class Execute {

    private Execute() {}

    public static Process application(List<String> classpath, String classToBeExecuted) {
        final String sep = File.separator;
        final Iterator<String> iterator = classpath.iterator();
        final StringBuilder builder = new StringBuilder();
        final String java = System.getProperty("java.home");
        builder.append(java).append(sep).append("bin").append(sep).append("java ");

        if(!classpath.isEmpty()) {
            builder.append("-classpath");
            while (iterator.hasNext()) {
                builder.append(" ").append(iterator.next());
                if(iterator.hasNext()) { builder.append(File.pathSeparator); }
            }
        }
        final String argument = builder.append(" ").append(classToBeExecuted).toString();
        try {
            return Runtime.getRuntime().exec(argument);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

