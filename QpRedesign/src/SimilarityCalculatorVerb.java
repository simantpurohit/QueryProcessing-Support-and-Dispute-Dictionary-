import java.util.ArrayList;
import java.util.LinkedList;


public class SimilarityCalculatorVerb {
	
	WordClassVerb word1;
	WordClassVerb word2;
	int iterationNumber;
	float similarityWRTParent = 0;
	float similarityWRTRoot = 0;
	
	public SimilarityCalculatorVerb(WordClassVerb w1, WordClassVerb w2, int iterationNo){
		word1 = w1;
		word2 = w2;
		iterationNumber = iterationNo;
		similarityWRTParent = getSimilarity(w1, w2, iterationNo);
		similarityWRTRoot = getSimilarityWithRoot(w1);
	}
	
	public float getSimilarity(WordClassVerb w1, WordClassVerb w2, int iterationNo){
		ArrayList<Integer> synsetID1 = w1.getSynsetID();
		ArrayList<Float> relative1 = w1.getRelativeFrequency();
		ArrayList<Integer> synsetID2 = w2.getSynsetID();
		ArrayList<Float> relative2 = w2.getRelativeFrequency();
		float similarity = 0;
		for(int i = 0;i<synsetID1.size();i++){
			
			//System.out.println("For synset ID:"+ relative1.get(i));
			for(int j = 0;j<synsetID2.size();j++){
				
				//System.out.println("Comparing with"+ relative2.get(j));
				if(synsetID1.get(i).equals(synsetID2.get(j))){
//					System.out.println("Relative 1:"+relative1.get(i));
//					System.out.println("Relative 2:"+relative2.get(j));
//					System.out.println("Product:"+relative1.get(i)*relative2.get(j));
					similarity += relative1.get(i)*relative2.get(j);
					//similarity = word1.relativeFrequencyVerb.get(i) * word2.relativeFrequencyVerb.get(j);
				}
			}
		}
		
		return similarity;
	}
	
	public float getSimilarityWithRoot(WordClassVerb currentWord){
//		System.out.println("For the word:"+currentWord.wordName);
//		System.out.println("Similarity WRT ROOT:"+similarityWRTParent*currentWord.parentWord.similarityWRTRoot);
		return similarityWRTParent*currentWord.parentWord.similarityWRTRoot;
	}
	
//	public float getHypernymSimilarity(ArrayList<LinkedList<WordClassVerb>> hyperMatrix, WordClassVerb seedWord, WordClassVerb wordToCompare){
//		
//		for()
//	}
	
	public static void main(String args[]) {
		WordClassVerb w1 = new WordClassVerb("believe", null);
		WordClassVerb w2 = new WordClassVerb("think", null);
		
		SimilarityCalculatorVerb sim1 = new SimilarityCalculatorVerb(w1, w2, 0);
		
	}
	

}
