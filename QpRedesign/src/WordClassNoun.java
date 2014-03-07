import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordNetException;


public class WordClassNoun {

	//The word from which this word was derived
		WordClassNoun parentWord=null;
		
		//String form of the word
		String wordName;
		
		float similarityWRTParent=0;
		float similarityWRTRoot=1;
		
		
		ArrayList<Synset> rawNounSynset = new ArrayList<Synset>();
				
		ArrayList<Float> relativeFrequencyNoun = new ArrayList<Float>();
		
		ArrayList<Integer> synsetIdForNoun = new ArrayList<Integer>();
		ArrayList<Integer> tagCountsForNoun = new ArrayList<Integer>();
		ArrayList<ArrayList<String>> synsetsOfWordForNoun = new ArrayList<ArrayList<String>>();
		
		
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		
		/*-------------------------------------------------------------------------------------------------------------*
		 * -----------------------------------------------CONSTRUCTOR---------------------------------------------*
		 *-------------------------------------------------------------------------------------------------------------*/
		public WordClassNoun(String wordToMake, WordClassNoun parentWord){
			
			System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\2.1\\dict");
			
			wordName=wordToMake;
			
			this.parentWord = parentWord;
			setSynsetIDAndSynsetWordsForNoun(wordToMake);
			
			if(parentWord == null){
				return;
			}
			
			else{
				SimilarityCalculatorNoun sim = new SimilarityCalculatorNoun(this, this.parentWord, 0);
				similarityWRTParent = sim.similarityWRTParent;
				similarityWRTRoot = sim.similarityWRTRoot;
			}
		}
	
		/*-------------------------------------------------------------------------------------------------------------*
		 * ---------------------------MAKE SYNSETS FOR Noun PART OF SPEECH---------------------------------------------*
		 *-------------------------------------------------------------------------------------------------------------*/
		private void setSynsetIDAndSynsetWordsForNoun(String word){
			
			//System.out.println("Initializing the Noun part:"+word);
			if(word == null)
				return;
			Synset[] forTheWord = database.getSynsets((String)word, SynsetType.NOUN);
			
			
			for(Synset synset:forTheWord){
				//Adding IDs of each synset to arraylist
				rawNounSynset.add(synset);
				int indexOfAt = synset.toString().indexOf("@");
				int indexEnd = synset.toString().indexOf("[");
				int tagCount;
				
				try{
					tagCount = synset.getTagCount(this.wordName);
				}
				catch(WordNetException e){
					//SOPLN("This synset for the word "+wordName+" does not have a tag count.");
					//SOPLN("Setting it to zero");
					tagCount = 0;
				}
				
				tagCountsForNoun.add(tagCount);
				//System.out.println("Tag count for the synset:"+synset.toString()+" is:"+tagCount+ " for the form:"+ synset.getType().toString()+" for the word:"+wordName);
				
				int singleSynsetID = Integer.parseInt(synset.toString().substring(indexOfAt+1, indexEnd));
				synsetIdForNoun.add(singleSynsetID);
				
				
				String[] newArray = synset.getWordForms();
				ArrayList<String> newArrayList = new ArrayList<String>();
				
				//Adding each synset to an arraylist
				for(String wordFromSynset:newArray){
					newArrayList.add(wordFromSynset);
				}
				
				//adding the created Arraylist into Arraylist(of Arraylists)
				synsetsOfWordForNoun.add(newArrayList);
			}
			
			int totalTagCount = 0;
			for(int i=0;i<tagCountsForNoun.size();i++){
				totalTagCount += tagCountsForNoun.get(i);
			}
			
			if(totalTagCount == 0){
				totalTagCount = 1;
			}
			
			for(int i=0;i<tagCountsForNoun.size();i++){
				relativeFrequencyNoun.add((float)tagCountsForNoun.get(i)/(float)totalTagCount);
			}
						
			
			
		}
		
		/*-------------------------------------------------------------------------------------------------------------*
		 * ---------------------------------OVERRIDING EQUALS AND HASHCODE---------------------------------------------*
		 *-------------------------------------------------------------------------------------------------------------*/
		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			if(this.wordName == null)
				return false;
			
			if(obj == null){
				return false;
			}
			
			WordClassNoun word2Cmp = (WordClassNoun)obj;
			
			if(this.wordName.equals(word2Cmp.wordName))
				return true;
			else
				return false;
		}

		@Override
		public int hashCode() {
			if(this.wordName == null)
				return 31;
			else
				return (this.wordName.hashCode())+31;
		}
		/*-------------------------------------------------------------------------------------------------------------*
		 * -----------------------------------------------HELPER FUNCTIONS---------------------------------------------*
		 *-------------------------------------------------------------------------------------------------------------*/

		public ArrayList<ArrayList<String>> getNounSynsets(){
			return synsetsOfWordForNoun;
		}
		
		public ArrayList<Integer> getNounSynsetsID(){
			return synsetIdForNoun;
		}
		
		public int getNoOfSynsets(ArrayList<ArrayList<String>> synsetToMeasure){
			return synsetToMeasure.size();
		}

		
		//Get id of a Synset when passing a raw Synset Object to this function
		public int getSynsetID(Synset toGetIdOf){
			int indexOfAt = toGetIdOf.toString().indexOf("@");
			int indexEnd = toGetIdOf.toString().indexOf("[");
			int singleSynsetID = Integer.parseInt(toGetIdOf.toString().substring(indexOfAt+1, indexEnd));
			return singleSynsetID;
		}
		
		
		//returns a set of words in all the synsets of the current word - passing a ArrayList of Synset
		public Set<String> getWordsInSynset(ArrayList<String> synsetToMeasure){
			Set<String> toReturn = new HashSet<String>();
			for(int i=0;i<synsetToMeasure.size();i++){
				toReturn.add(synsetToMeasure.get(i));
			}
			
			return toReturn;
		}
		
		public void SOPLN(String toPrint){
			System.out.println(toPrint);
		}
		
		public String toString() {
			
			String word = wordName;
			
			ArrayList<ArrayList<String>> NounSyn = getNounSynsets();
			ArrayList<Integer> NounID = getNounSynsetsID();
			ArrayList<Integer> NounTag = this.tagCountsForNoun;
			
			String toPrint="";
			toPrint += "____________________________________________________________________________\n";
			toPrint += "_____________________________________________________________________________\n";
			toPrint += "______________________________________________________________________________\n\n";
			toPrint += "Word Name:"+word+"\n";
			toPrint += "------------------------------------------------------------------- \n\n";
			
			toPrint+= "Noun Synsets:\n";
			for(int i=0;i<NounSyn.size();i++){
				toPrint += "Synset "+i+": "+NounSyn.get(i).toString()+"\nSynsetID:"+NounID.get(i)+"\nTag Count:"+NounTag.get(i)+"\n\n";	
			}
			
			return toPrint;
		}

		public ArrayList<Float> getRelativeFrequency(){
			return relativeFrequencyNoun;
		}
		
		public ArrayList<Integer> getSynsetID(){
			return synsetIdForNoun;
		}
		
		public void getHypernym(WordClassNoun toGet){
			
			NounSynset Nounsyn = (NounSynset)toGet.rawNounSynset.get(2);
			System.out.println("NounSynset: "+ Nounsyn);
			NounSynset[] NounArray = Nounsyn.getHypernyms();
			for(NounSynset x:NounArray){
				System.out.println("Hypernym:"+x.toString());
			}
			
		}
}
