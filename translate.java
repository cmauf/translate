import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cmauf
 * A Class that translates text files with a restrictive dictionary.
 *
 */
public class translate {
	
	/**
	 * @author baeldung
	 * Trie structure as implemented by baeldung, modified for purposes of our program
	 *
	 */
	 
	static private class Trie {
	    private TrieNode root;

	    Trie() {
	        root = new TrieNode();
	    }
	    private class TrieNode {
	        private final Map<Character, TrieNode> children = new HashMap<>();
	        private boolean endOfWord;
	        private String translation;

	        Map<Character, TrieNode> getChildren() {
	            return children;
	        }

	        boolean isEndOfWord() {
	            return endOfWord;
	        }

	        void setEndOfWord(boolean endOfWord) {
	            this.endOfWord = endOfWord;
	        }
	    }

	    void insert(String word, String translation) {
	        TrieNode current = root;

	        for (int i = 0; i < word.length(); i++) {
	            current = current.getChildren().computeIfAbsent(word.charAt(i), c -> new TrieNode());
	        }
	        current.setEndOfWord(true);
	        current.translation = translation;
	    }

	    boolean delete(String word) {
	        return delete(root, word, 0);
	    }

	    boolean containsNode(String word) {
	        TrieNode current = root;

	        for (int i = 0; i < word.length(); i++) {
	            char ch = word.charAt(i);
	            TrieNode node = current.getChildren().get(ch);
	            if (node == null) {
	                return false;
	            }
	            current = node;
	        }
	        return current.isEndOfWord();
	    }
	    
	    /** Method added by @cmauf, returns the translation of a given String. 
	     * Before calling, check if given String exists in Trie.
	     * @param Word that is looked for
	     * @return translation 
	     */
	    String getTranslation(String word) {
	        TrieNode current = root;

	        for (int i = 0; i < word.length(); i++) {
	            char ch = word.charAt(i);
	            TrieNode node = current.getChildren().get(ch);
	            current = node;
	        }
	        return current.translation;
	    }

	    boolean isEmpty() {
	        return root == null;
	    }

	    private boolean delete(TrieNode current, String word, int index) {
	        if (index == word.length()) {
	            if (!current.isEndOfWord()) {
	                return false;
	            }
	            current.setEndOfWord(false);
	            return current.getChildren().isEmpty();
	        }
	        char ch = word.charAt(index);
	        TrieNode node = current.getChildren().get(ch);
	        if (node == null) {
	            return false;
	        }
	        boolean shouldDeleteCurrentNode = delete(node, word, index + 1) && !node.isEndOfWord();

	        if (shouldDeleteCurrentNode) {
	            current.getChildren().remove(ch);
	            return current.getChildren().isEmpty();
	        }
	        return false;
	    }
	}
	
	/** Regular expression to make sure dictionary input is formatted correctly
	 * 
	 */
	private static Pattern dictline = Pattern.compile("([a-z]+):([a-z]+)"); 

	/** Main Method, takes dictionary and input streamt to translate
	 * @param args source of dictionary file
	 */
	public static void main(String[] args) {
		try {//check if called correctly
			String a = (args[0]);
			a.charAt(1);
		} 
		catch (Exception e) {
			System.out.println("No arguments!");
			System.exit(-1);
		}
		Trie Dictionary = new Trie ();
		try {
			Scanner scanner = new Scanner(new File(args[0])); //read input file for dictionary
			while (scanner.hasNextLine()) { //read line for line
				String T  = scanner.nextLine(); 
				Matcher m = dictline.matcher(T); //check for conformity with format constraints
				if (m.matches()) {
					Dictionary.insert(m.group(1),m.group(2));
				}
				else {
					System.out.println("Input does not match contraints!");
					System.exit(10);
				}
			}
			scanner.close();
		}
		catch (FileNotFoundException k) {
			k.printStackTrace();
		}
		Scanner input = new Scanner(System.in);
		while (input.hasNextLine()){
			String line = input.nextLine();//read input stream line by line
			String [] words = line.split(" ");// split line in words
			for (String str:words) {
				boolean punct = false;
				char end = 0;
				if (str.length()>0 &&!Character.isLetterOrDigit(str.charAt(str.length()-1))) { //split string if ends in punctuation
					end = str.charAt(str.length()-1);
					str = str.substring(0,str.length()-1);
					punct = true;
				}
				if ((str.length() == 1 && (Character.isWhitespace(str.charAt(0)) || !Character.isLetterOrDigit(str.charAt(0)))) || str.length()==0) System.out.print(str); //don't do anything for whitespace or empty lines
				else if (str.length()>0 && Character.isDigit(str.charAt(0)) && Character.isDigit(str.charAt(str.length()-1))) { //word probably is a number
					System.out.print(str);
				}
				else if (str.contains("-")) { //split compund words
					String [] parts = str.split("-");
					for (int i=0; i< parts.length; i++) {
						boolean uppercase = Character.isUpperCase(parts[i].charAt(0)); //check if upper case
						if(Dictionary.containsNode(parts[i].toLowerCase())) {
							String T = Dictionary.getTranslation(parts[i].toLowerCase());
							if (uppercase) System.out.print(T.substring(0,1).toUpperCase() + T.substring(1));
							else System.out.print(T);
						}
						else {
							System.out.print("<" + parts[i] + ">");
						}
						if (i!=parts.length-1) System.out.print("-");
					}
				}
				else {
					boolean uppercase = Character.isUpperCase(str.charAt(0));
					if(Dictionary.containsNode(str.toLowerCase())) {
						String T = Dictionary.getTranslation(str.toLowerCase());
						if (uppercase) System.out.print(T.substring(0,1).toUpperCase() + T.substring(1));
						else System.out.print(T);
					}
					else {
						System.out.print("<" + str + ">");
					}
				}
				if (punct) System.out.print(end);
				if (!words[words.length-1].equals(str+end)) System.out.print(" ");
			}
			
			System.out.println();
		}
		input.close();
	}
}
