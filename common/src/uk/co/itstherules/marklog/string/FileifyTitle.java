package uk.co.itstherules.marklog.string;


public final class FileifyTitle implements StringManipulator {

	private StringManipulator delegate;

	public FileifyTitle(String extension) {
		this.delegate = new CompositeStringManipulator(new CamelCase(), new Append(extension));
	}
	
	public String manipulate(String text) {
		return delegate.manipulate(text);
	}

}
