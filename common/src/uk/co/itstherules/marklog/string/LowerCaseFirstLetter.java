package uk.co.itstherules.marklog.string;

public final class LowerCaseFirstLetter implements StringManipulator {

    @Override public String manipulate(String text) {
        if(text != null && text.length()>0) {
            return text.substring(0,1).toLowerCase() + text.substring(1);
        }
        return "";
    }
}
