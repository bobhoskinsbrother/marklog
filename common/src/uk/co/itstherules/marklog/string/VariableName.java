package uk.co.itstherules.marklog.string;


public final class VariableName implements StringManipulator {

	private StringManipulator delegate;

	public VariableName() {
		this.delegate = new CompositeStringManipulator(new CamelCase(), new LowerCaseFirstLetter());
	}
	
	public String manipulate(String text) {
		return delegate.manipulate(text);
	}

}
