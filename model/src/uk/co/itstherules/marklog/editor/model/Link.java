package uk.co.itstherules.marklog.editor.model;

public final class Link {

    private final String location;
    private final String text;

    public Link(String location, String text) {
        this.location = location;
        this.text = text;
    }

    public String getLocation() {
        return location;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Link)) {
            return false;
        }
        Link link = (Link) o;
        if (location != null ? !location.equals(link.location) : link.location != null) {
            return false;
        }
        if (text != null ? !text.equals(link.text) : link.text != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = location != null ? location.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }
}
