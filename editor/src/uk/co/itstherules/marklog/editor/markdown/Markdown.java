package uk.co.itstherules.marklog.editor.markdown;

public final class Markdown {

    private Markdown() { }

    public static String image(String path) {
        return "!" + link(path);
    }

    public static String link(String path) {
        String name = nameFrom(path);
        return "[" + name + "](" + path + ")";
    }

    private static String nameFrom(String path) {
        int beginIndex = path.lastIndexOf("/");
        if (beginIndex == -1) {
            beginIndex++;
        }
        return path.substring(beginIndex);
    }

}
