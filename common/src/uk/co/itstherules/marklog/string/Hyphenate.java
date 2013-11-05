package uk.co.itstherules.marklog.string;

import java.util.StringTokenizer;

public final class Hyphenate implements StringManipulator {

	public String manipulate(String text) {
		StringBuilder buffer = new StringBuilder();
		StringTokenizer tokenizer = new StringTokenizer(text, " _-\\/:*?\"<>|,\\.", false);
		while (tokenizer.hasMoreElements()) {
			String word = tokenizer.nextToken();
			buffer.append(word);
            if(tokenizer.hasMoreElements()) {
    			buffer.append("-");
            }
		}
		return buffer.toString();
	}
}
