import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class WordGame implements Serializable{
	
	protected HashMap<String, ArrayList<String>> words;
	protected ArrayList<String> categories;
	protected String wordToGuess;
	
	public WordGame() {
		initializeCategories();
		initializeWords();
	}
	
	private void initializeCategories() {
		this.categories = new ArrayList<String>();
		this.categories.add("animal");
		this.categories.add("fruit");
		this.categories.add("country");
	}
	
	 private void initializeWords() {
	        words = new HashMap<>();
	        ArrayList<String> animalsWords = new ArrayList<>();
	        animalsWords.add("dog");
	        animalsWords.add("cat");
	        animalsWords.add("elephant");
	        
	        ArrayList<String> fruitsWords = new ArrayList<>();
	        fruitsWords.add("apple");
	        fruitsWords.add("banana");
	        fruitsWords.add("cherry");
	        
	        ArrayList<String> countriesWords = new ArrayList<>();
	        countriesWords.add("usa");
	        countriesWords.add("canada");
	        countriesWords.add("france");
	        
	        // Store the ArrayLists in the HashMap
	        words.put("animal", animalsWords);
	        words.put("fruit", fruitsWords);
	        words.put("country", countriesWords);
	    }
	 
	 //gets a random word, from the specified category and sets the wordToGuess data member as such.
	 public String getRandomWord(String category) {
		 Random rand = new Random();
		 int random = rand.nextInt(3);
		 wordToGuess = words.get(category).get(random);
		 return words.get(category).get(random);
	 }
	 
	 //return index of letter in word, -1 if not in word
	 public ArrayList<Integer> isLetterInWord(char c) {
		 ArrayList<Integer> idxs = new ArrayList<Integer>();
		 for (int i = 0; i < wordToGuess.length(); i++) {
			 if (wordToGuess.charAt(i) == c) {
				 idxs.add(i);
			 }
		 }
		 return idxs;
	 }
}
