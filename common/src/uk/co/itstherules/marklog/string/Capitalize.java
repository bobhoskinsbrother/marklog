package uk.co.itstherules.marklog.string;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public final class Capitalize implements StringManipulator {

	public String manipulate(String text) {
		StringBuffer buffer = new StringBuffer();
        final String delimiters = " _-\\/:*?\"<>|,\\.";
        final List<String> delimitersList = Arrays.asList(delimiters.split(""));
        StringTokenizer tokenizer = new StringTokenizer(text, delimiters, true);
		while (tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken();
            if(delimitersList.contains(token)) {
    			buffer.append(token);
            } else {
                String firstLetter = token.substring(0, 1);
                buffer.append(firstLetter.toUpperCase());
                buffer.append(token.substring(1, token.length()));
            }
		}
		return buffer.toString();
	}
}
