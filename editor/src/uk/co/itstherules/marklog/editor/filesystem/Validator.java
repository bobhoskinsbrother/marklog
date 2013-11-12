package uk.co.itstherules.marklog.editor.filesystem;

public final class Validator {

    private Validator() { }


    public static boolean isLegalFileName(String name) {
        if(name == null ||  "".equals(name)) { return false; }
        char[] illegal = {'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
        for (char illegalChar : illegal) {
            if (name.contains(Character.toString(illegalChar))) {
                return false;
            }
        }
        return true;
    }


}
