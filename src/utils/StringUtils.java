package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StringUtils {

	/*
		[test] abc [test]
		=>
		test, abc, test

	 */
	public static List<String> splitTextIntoFraments(String text, char start, char end, Optional<String> prefix) {
		List<String> out = new ArrayList<>();

		if(!text.startsWith(start + "") && prefix.isPresent()) {
			text = prefix + text;
		}

		String currentString = "";
		boolean escaped = false;
		for(char c: text.toCharArray()) {
			if(c == start && !escaped) {
				if(currentString.length() > 0) out.add(currentString);
				currentString = "";
				continue;
			} else if(c == end && !escaped) {
				if(currentString.length() > 0) out.add(currentString);
				currentString = "";
				continue;
			} else if(c == '\\' && !escaped) {
				escaped = true;
				continue;
			}

			currentString += c;
			escaped = false;
		}
		if(currentString.length() > 0) out.add(currentString);
		return out;
	}

	/*
		option1 = test, option2 = abc
		delim = ,
		specifier = =

		getOption("option1") => test
	 */
	public static Optional<String> getOption(String allOptions, String optionName, char delim, char specifier) {
		String[] singleOptions = allOptions.split(delim + "");
		for(String op: singleOptions) {
			if(op.trim().startsWith(optionName)) {
				return Optional.of(op.split(specifier + "")[1].trim());
			}
		}

		return Optional.empty();
	}

}
