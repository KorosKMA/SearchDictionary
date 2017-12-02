import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Scanner;

public class Tester {

	/*
	 * Summary of the implemented structure:
	 * A tree whose each node contains 2 collections.
	 * 
	 * 1st - array of all ASCII characters.
	 * 1st array is treated like a HashSet:
	 * every char corresponds to a unique cell in an array
	 * index of which is dictated by integer representation of given character.
	 * 
	 * 2nd - HashMap of characters.
	 * Both keys and values are chars.
	 * Every key corresponds to a value 
	 * which is the same object as the key.
	 * It was made so because there is no way
	 * to get an object from a HashSet
	 * that is equal to a given one.
	 * I didn't have enough mental strength to 
	 * write my own implementation of HashSet.
	 * 
	 * 2nd collection is used so that
	 * non-ASCII characters could also be added
	 * to the dictionary.
	 * 
	 * There are no other differences between 2 collections,
	 * so all further explanation can be applied
	 * to both of them.
	 * 
	 * Each cell of an array contains
	 * a pointer to a node which represents
	 * the next letter in a word.
	 * A tree is constructed in a way
	 * so that the first node contains 
	 * all the first letters of all the words
	 * that have been added to a dictionary,
	 * every next node contains next letters of 
	 * all words that have a common prefix of previous nodes.
	 * 
	 * This kind of structure was chosen
	 * because it allows to insert and retrieve
	 * any individual word in O(n) time, 
	 * where n - length of a word.
	 * 
	 * Can be lazily complemented to support
	 * mid-word asterisk
	 * by adding "last" node
	 * which contains all the last letters.
	 * Then searching for suffixes will be the same
	 * as searching for prefixes,
	 * which means the only task left
	 * is to find all the possible paths
	 * between the last node of a prefix
	 * and the last node (first letter) of a suffix.
	 * */
	
	private static Scanner wordInput;
	private static final Scanner in = new Scanner(System.in);
	private static final PrintWriter out = new PrintWriter(System.out);

	public static void main(String[] args) throws FileNotFoundException {
		wordInput = new Scanner(new File("words.txt"));
		out.println("Loading...");
		out.flush();
		SearchDictionary dictionary = new SearchDictionary();
		while (wordInput.hasNext()) {
			dictionary.addWord(wordInput.next());
		}
		String word;
		Iterable<String> iterable;
		Iterator<String> it;
		out.println("Loading has completed.\nThis dictionary contains " + dictionary.countWords() + " words.");
		while (true) {
			out.println("Enter word (x..x / x...x*): ");
			out.flush();
			if (!in.hasNext())
				break;
			word = in.next();
			if (word.charAt(word.length() - 1) != '*')
				if (dictionary.hasWord(word))
					out.println("Such word exists here (" + word + ").");
				else
					out.println("No such word.");
			else {
				iterable = dictionary.query(word);
				if (iterable != null && (it = iterable.iterator()) != null) {
					while (it.hasNext())
						out.println(it.next());
				} else {
					out.println("No such words. ");
				}
			}
			out.print("To exit enter Ctrl+Z. ");
		}
		in.close();
	}

}
