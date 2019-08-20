package source;
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
	
	private static Pattern dictline = Pattern.compile("([a-z]+):([a-z]+)");

	public static void main(String[] args) {
		try {
			String a = (args[0]);
			a.charAt(1);
		} 
		catch (Exception e) {
			System.out.println("No arguments!");
			System.exit(-1);
		}
		Trie Dictionary = new Trie ();
		try {
			Scanner scanner = new Scanner(new File(args[0]));
			while (scanner.hasNextLine()) {
				String T  = scanner.nextLine();
				Matcher m = dictline.matcher(T);
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
			String line = input.nextLine();
			String [] words = line.split(" ");
			for (String str:words) { //TODO: handle uppercase words correctly, handle double whitespace correctly
				if (str.contains("-")) {
					String [] parts = str.split("-");
					//TODO: handle compound words
				}
				else {
					if(Dictionary.containsNode(str.toLowerCase())) {
						System.out.print(Dictionary.getTranslation(str.toLowerCase())+" ");
					}
					else {
						System.out.print("<" + str + ">");
					}
				}
			}
			System.out.println();
		}
		input.close();
	}
}
