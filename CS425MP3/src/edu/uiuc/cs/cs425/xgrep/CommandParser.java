package edu.uiuc.cs.cs425.xgrep;

/**
 * This class parse the user input into an array which contains the command
 * words for transmission.
 * 
 * @author wwang84
 * @version 1.3
 */
public class CommandParser {

	/**
	 * This method parse a Xgrep line to a String array for ClientProxy to
	 * transmit return null if the command line is illegal.
	 * 
	 * @param Xgrep
	 *            : command line that user input
	 * @return String[]: an array contains split command words
	 */
	public static String[] parseXgrepToGrep(String Xgrep) {
		String argument = "";
		String keyString = "";
		String valueString = "";
		String[] result;
		String[] content = Xgrep.split("\\s");

		//if command line is too short
		if (content.length <= 1) {
			return null;
		} else {
			//support Xgrep command
			if (content[0].startsWith("Xgrep")) {
				for (int i = 1; i < content.length;) {
					if (content[i].startsWith("-")) {
						if (content[i].equals("-key")) {
							//find -key's search string
							int endPos = findSearchKeyword(content, i);
							if (endPos != i) {
								for (int j = i + 1; j <= endPos; j++) {
									if (j != endPos)
										keyString += content[j] + " ";
									else
										keyString += content[j];
								}
								i = endPos + 1;
							} else {
								return null;
							}
						} else if (content[i].equals("-value")) {
							//find -value's search string
							int endPos = findSearchKeyword(content, i);
							if (endPos != i) {
								for (int j = i + 1; j <= endPos; j++) {
									if (j != endPos)
										valueString += content[j] + " ";
									else
										valueString += content[j];
								}
								i = endPos + 1;
							} else {
								return null;
							}

						} else {
							argument += " " + content[i];
							i++;
						}

					} else {
						return null;
					}
				}
			}
			//support grep command
			else if(content[0].startsWith("grep")){
				return content;
			}
			else{
				return null;
			}
		}
		String searchString = StringRegex(keyString, valueString);
		if (searchString == null) {
			return null;
		}
		//constructs a string array contains all the search information
		if (!argument.trim().equals("")) {
			String[] argumentArray=argument.trim().split("\\s");
			result = new String[3+argumentArray.length];
			result[0] = "grep";
			result[1] = searchString;
			result[2] = "";
			for(int i=3;i<result.length;i++){
				result[i]=argumentArray[i-3];
			}
		} else {
			result = new String[3];
			result[0] = "grep";
			result[1] = searchString;
			result[2] = "";
		}

		return result;
	}

	private static String StringRegex(String keyString, String valueString) {
		if (keyString.startsWith("'")) {
			keyString = keyString.substring(1, keyString.length() - 1);
		}
		if (valueString.startsWith("'")) {
			valueString = valueString.substring(1, valueString.length() - 1);
		}
		if (!keyString.equals("") && !valueString.equals("")) {
			return keyString + ".*:.*" + valueString;
		} else if (!keyString.equals("") && valueString.equals("")) {
			return keyString + ".*:";
		} else if (keyString.equals("") && !valueString.equals("")) {
			return ":.*" + valueString;
		} else {
			return null;
		}
	}

	/**
	 * This method finds the search key words last position in the array. For
	 * example, it return a's position in "Xgrep -key a" command, and return
	 * "a b c"'s last position in command "Xgrep -key "a b c" ". If it returns
	 * the posistion same as its argument, it means there is no keyword or an
	 * illegal input.
	 * 
	 * @param String[]:
	 * 			     an array contains split command words
	 * @param start:
	 * 				the position where key words starts
	 * @return int:
	 *              the last position where the key words end.
	 */
	
	private static int findSearchKeyword(String[] content, int start) {
		int end = start;
		if ((start + 1) < content.length && !content[start + 1].startsWith("-")) {
			if (content[start + 1].startsWith("'")) {
				for (int i = start + 1; i < content.length; i++) {
					if (content[i].endsWith("'")) {
						return i;
					}
				}
			} else {
				end++;
			}
		} else {// a illegal word following -key
			return end;
		}
		return end;
	}
}
