import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Scanner;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;


public class TestClass {

	static String seedWordSupport = null;
	static String seedWordDispute = null;
	static int iterationMax = 0;
	
	public static void main(String[] args) {
		
		System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\2.1\\dict");
		TestClass test = new TestClass();
		System.out.println("Please Enter Support Word");
		Scanner read = new Scanner(System.in);
		seedWordSupport = read.next();
		
		System.out.println("Please Enter Support Word");
		seedWordDispute = read.next();
		
		System.out.println("Please Enter number of iterations");
		iterationMax = read.nextInt();
		read.close();
		
		test.produceDictionary(seedWordSupport, seedWordDispute, iterationMax);
	}
	
	public void produceDictionary(String seedWord, String disputeWord, int levelMax){
		produceSupportDictionary(seedWord, levelMax);
		produceDisputeDictionary(disputeWord, levelMax);
	}
	
	
	public void produceSupportDictionary(String seedWord, int levelMax){
		
		LinkedHashSet<String> verbSet = new LinkedHashSet<String>();
		LinkedHashSet<String> nounSet = new LinkedHashSet<String>();
		LinkedHashSet<String> adjectiveSet = new LinkedHashSet<String>();
		LinkedHashSet<String> adverbSet = new LinkedHashSet<String>();
		
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\2.1\\dict");
		Synset[] forTheWord = database.getSynsets(seedWord);
		
		for(Synset y: forTheWord){
			//System.out.println("Synset is:"+y.toString());
			//System.out.println("");
			//System.out.println("Synset number is:"+count);
			//System.out.println("");
			int atPosition = y.toString().indexOf("@");
			String synsetType = y.toString().substring(0,atPosition);
			
			if(synsetType.equals("Verb") && !seedWord.equals(null))
				verbSet.add(seedWord);
			if(synsetType.equals("Noun") && !seedWord.equals(null))
				nounSet.add(seedWord);
			if(synsetType.equals("Adjective") && !seedWord.equals(null))
				adjectiveSet.add(seedWord);
			if(synsetType.equals("Adverb") && !seedWord.equals(null))
				adverbSet.add(seedWord);
			
			WordSense sense[] = y.getDerivationallyRelatedForms(seedWord);
			
			for(WordSense x: sense){
				//System.out.println(x.toString());
				String toString = x.toString();
				int atPosition2 = toString.indexOf("@");
				String type = x.toString().substring(0,atPosition2);
				//System.out.println(type);
				String[] array = type.split(" ");
				String wordType = array[2];
				//System.out.println("Type is:"+wordType);
				
				if(wordType.equals("Verb") && !(x.getWordForm().equals(null)))
					verbSet.add(x.getWordForm());
				if(wordType.equals("Noun") && !(x.getWordForm().equals(null)))
					nounSet.add(x.getWordForm());
				if((wordType.equals("Adjective") || wordType.equals("AdjectiveSatellite")) && !(x.getWordForm().equals(null)))
					adjectiveSet.add(x.getWordForm());
				if(wordType.equals("Adverb") && !(x.getWordForm().equals(null)))
					adverbSet.add(x.getWordForm());
			}
			
			System.out.println("---------------------------------------------");
			System.out.println(" ");
		}
		
		Iterator<String> Iterate = verbSet.iterator();
		while(Iterate.hasNext()){
			String verb = Iterate.next();
			IteratorLevelVerb newVerb = new IteratorLevelVerb();
			System.out.println("Iterating for the support word:"+verb);
			newVerb.iterateVerbSupport(verb, levelMax);
		}
		
		Iterate = nounSet.iterator();
		while(Iterate.hasNext()){
			String noun = Iterate.next();
			IteratorLevelNoun newNoun = new IteratorLevelNoun();
			System.out.println("Iterating for the noun support word:"+noun);
			newNoun.iterateNounSupport(noun, levelMax);
		}
		
		Iterate = adjectiveSet.iterator();
		while(Iterate.hasNext()){
			String adjective = Iterate.next();
			System.out.println("Adjective support word:"+adjective);
			IteratorLevelAdjective newAdjective = new IteratorLevelAdjective();
			newAdjective.iterateAdjectiveSupport(adjective, levelMax);
		}
		
		Iterate = adverbSet.iterator();
		while(Iterate.hasNext()){
			String adverb = Iterate.next();
			System.out.println("Adverb support word:"+adverb);
			IteratorLevelAdverb newAdverb = new IteratorLevelAdverb();
			newAdverb.iterateAdverbSupport(adverb, levelMax);
		}
	}

	public void produceDisputeDictionary(String seedWord, int levelMax){
		
		LinkedHashSet<String> verbSet = new LinkedHashSet<String>();
		LinkedHashSet<String> nounSet = new LinkedHashSet<String>();
		LinkedHashSet<String> adjectiveSet = new LinkedHashSet<String>();
		LinkedHashSet<String> adverbSet = new LinkedHashSet<String>();
		
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\2.1\\dict");
		Synset[] forTheWord = database.getSynsets(seedWord);
		
		for(Synset y: forTheWord){
			System.out.println("Synset is:"+y.toString());
//			System.out.println("");
//			System.out.println("Synset number is:"+count);
//			System.out.println("");
			int atPosition = y.toString().indexOf("@");
			String synsetType = y.toString().substring(0,atPosition);
			System.out.println("Synset Type:"+ synsetType);
			if(synsetType.equals("Verb") && !seedWord.equals(null))
				verbSet.add(seedWord);
			if(synsetType.equals("Noun") && !seedWord.equals(null))
				nounSet.add(seedWord);
			if(synsetType.equals("Adjective") && !seedWord.equals(null))
				adjectiveSet.add(seedWord);
			if(synsetType.equals("Adverb") && !seedWord.equals(null))
				adverbSet.add(seedWord);
			
			WordSense sense[] = y.getDerivationallyRelatedForms(seedWord);
			
			for(WordSense x: sense){
				System.out.println("Sense"+ x.toString());
				String toString = x.toString();
				int atPosition2 = toString.indexOf("@");
				String type = x.toString().substring(0,atPosition2);
				//System.out.println(type);
				String[] array = type.split(" ");
				String wordType = array[2];
				//System.out.println("Type is:"+wordType);
				
				if(wordType.equals("Verb") && !(x.getWordForm().equals(null)))
					verbSet.add(x.getWordForm());
				if(wordType.equals("Noun") && !(x.getWordForm().equals(null)))
					nounSet.add(x.getWordForm());
				if((wordType.equals("Adjective") || wordType.equals("AdjectiveSatellite")) && !(x.getWordForm().equals(null)))
					adjectiveSet.add(x.getWordForm());
				if(wordType.equals("Adverb") && !(x.getWordForm().equals(null)))
					adverbSet.add(x.getWordForm());
			}
			
			System.out.println("---------------------------------------------");
			System.out.println(" ");
		}
		
		Iterator<String> Iterate = verbSet.iterator();
		while(Iterate.hasNext()){
			String verb = Iterate.next();
			IteratorLevelVerb newVerb = new IteratorLevelVerb();
			System.out.println("Iterating for the dispute word:"+verb);
			newVerb.iterateVerbDispute(verb, levelMax);
		}
		
		Iterate = nounSet.iterator();
		while(Iterate.hasNext()){
			String noun = Iterate.next();
			IteratorLevelNoun newNoun = new IteratorLevelNoun();
			System.out.println("Iterating for the noun dispute word:"+noun);
			newNoun.iterateNounDispute(noun, levelMax);
		}
		
		Iterate = adjectiveSet.iterator();
		while(Iterate.hasNext()){
			String adjective = Iterate.next();
			System.out.println("Adjective dispute word:"+adjective);
			IteratorLevelAdjective newAdjective = new IteratorLevelAdjective();
			newAdjective.iterateAdjectiveDispute(adjective, levelMax);
		}
		
		Iterate = adverbSet.iterator();
		while(Iterate.hasNext()){
			String adverb = Iterate.next();
			System.out.println("Adverb dispute word:"+adverb);
			IteratorLevelAdverb newAdverb = new IteratorLevelAdverb();
			newAdverb.iterateAdverbDispute(adverb, levelMax);
		}
	}

}
