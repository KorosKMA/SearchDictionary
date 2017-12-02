import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class SearchDictionary {

	private Node first = new Node();
	private int wordCount;
	
	public SearchDictionary() {}

	public void addWord(String word) {
		addWord(first, word = word.toLowerCase(Locale.ROOT), word.length(), 0);
	}

	private void addWord(Node node, String word, int wordLength, int charIndex) {
		if (charIndex >= wordLength){
			node.add('\0');
			wordCount++;
		}else
			addWord(node.add(word.charAt(charIndex)), word, wordLength, charIndex + 1);
	}

	public String delWord(String word) {
		delWord(first, word.toLowerCase(Locale.ROOT), word.length(), 0);
		return word;
	}

	private boolean delWord(Node node, String word, int wordLength, int charIndex) {
		char currentChar;
		boolean toDelete = (charIndex >= wordLength) ? 
				node.contains(currentChar='\0') : 
				node.contains(currentChar=word.charAt(charIndex)) && delWord(node.add(currentChar), word, wordLength, charIndex+1);
		if(toDelete){
				node.delete(currentChar);
				if(currentChar=='\0')
					wordCount--;
				return node.letterCount<1;
			}
		return false;
	}

	public boolean hasWord(String word) {
		return hasWord(first, word.toLowerCase(Locale.ROOT), word.length(), 0);
	}

	private boolean hasWord(Node node, String word, int wordLength, int charIndex) {
		if (charIndex >= wordLength)
			return node.contains('\0');
		if(node.contains(word.charAt(charIndex)))
			return hasWord(node.add(word.charAt(charIndex)), word, wordLength, charIndex + 1);
		return false;
	}

	public Iterable<String> query(String query) {
		query = query.toLowerCase(Locale.ROOT);
		boolean isAsterisk = (query.charAt(query.length()-1) == '*') ?
				true : false;
		List<String> list = new ArrayList<String>();
		if(isAsterisk)
			query = query.substring(0, query.length()-1);
		Node node = first;
		String result = "";
		char ch;
		for(int i=0, length = query.length(); i<length; i++){
			ch = query.charAt(i);
			if(node.contains(ch)){
				node = node.add(ch);
				result+=ch;
			}else return null;
		}
		if(!isAsterisk){
			if(!node.contains('\0'))
				return null;
			list.add(result);
		}else{
			gatherWords(node, list, result);
		}
		return list;
	}

	private void gatherWords(Node node, List<String> list, String result) {
		int letterCount = node.letterCount;
		if(node.nextShortNodes[0] != null){
			list.add(result);
			letterCount--;
		}
		for(int i=1; letterCount>0 && i<128; i++){
			if(node.nextShortNodes[i] != null){
				gatherWords(node.add((char)i), list, result+(char)i);
				letterCount--;
			}
		}
		if(letterCount>0){
			Iterator<LongNode> it = node.nextLongNodes.keySet().iterator();
			while(letterCount>0 && it.hasNext()){
				node = it.next();
				if(node!=null){
					gatherWords(node, list, result+((LongNode)node).letter);
					letterCount--;
				}
			}
		}
	}

	public int countWords() {
		return wordCount;
	}

	private class Node {
		protected ShortNode[] nextShortNodes = null;
		private HashMap<LongNode, LongNode> nextLongNodes = null;
		private int letterCount;

		private Node() {
			nextShortNodes = new ShortNode[128];
		}

		private Node add(char letter) {
			if(letter > 127 || letter < 0){
				if (nextLongNodes == null)
					nextLongNodes = new HashMap<LongNode, LongNode>(8);
				LongNode newNode = new LongNode(letter);
				if (!nextLongNodes.containsKey(newNode)){
					letterCount++;
					nextLongNodes.put(newNode, newNode);
					return newNode;
				}
				return nextLongNodes.get(newNode);
			}
			if (nextShortNodes[letter] == null) {
				nextShortNodes[letter] = new ShortNode(letter);
				letterCount++;
			}
			return nextShortNodes[letter];
		}

		private boolean delete(char letter) {
			if(letter > 127 || letter < 0){
				if (nextLongNodes == null)
					return false;
				if (nextLongNodes.remove(new LongNode(letter)) != null) {
					letterCount--;
					return true;
				}
				return false;
			}
			if (nextShortNodes[letter] != null) {
				nextShortNodes[letter] = null;
				letterCount--;
				return true;
			}
			return false;
		}

		private boolean contains(char letter) {
			if(letter > 127 || letter < 0){
				if (nextLongNodes == null)
					return false;
				return nextLongNodes.containsKey(new LongNode(letter));
			}
			if (nextShortNodes[letter] != null)
				return true;
			return false;
		}
	}

	private class ShortNode extends Node {

		private final char letter;

		private ShortNode(char letter) {
			if (letter <= 0 || letter > 127)
				this.letter = 0;
			else {
				this.letter = letter;
				nextShortNodes = new ShortNode[128];
			}
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return letter+"";
		}
	}

	private class LongNode extends Node {

		private final Character letter;

		private LongNode(Character letter) {
			this.letter = letter;
			nextShortNodes = new ShortNode[128];
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return letter+"";
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 17;
			result = prime * result + ((letter == null) ? 0 : letter.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LongNode other = (LongNode) obj;
			if (letter == null) {
				if (other.letter != null)
					return false;
			} else if (!letter.equals(other.letter))
				return false;
			return true;
		}
	}

}