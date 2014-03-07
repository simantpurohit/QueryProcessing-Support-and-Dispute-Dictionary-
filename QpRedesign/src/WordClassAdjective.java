import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.AdjectiveSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordNetException;


public class WordClassAdjective {

	//The word from which this word was derived
		WordClassAdjective parentWord=null;
		
		//String form of the word
		String wordName;
		
		float similarityWRTParent=0;
		float similarityWRTRoot=1;
		
		
		ArrayList<Synset> rawAdjectiveSynset = new ArrayList<Synset>();		
		ArrayList<Float> relativeFrequencyAdjective = new ArrayList<Float>();
		ArrayList<Integer> synsetIdForAdjective = new ArrayList<Integer>();
		ArrayList<Integer> tagCountsForAdjective = new ArrayList<Integer>();
		ArrayList<ArrayList<String>> synsetsOfWordForAdjective = new ArrayList<ArrayList<String>>();
		
		
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		
		/*-------------------------------------------------------------------------------------------------------------*
		 * -----------------------------------------------CONSTRUCTOR---------------------------------------------*
		 *-------------------------------------------------------------------------------------------------------------*/
		public WordClassAdjective(String wordToMake, WordClassAdjective parentWord){
			
			System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\2.1\\dict");
			
			wordName=wordToMake;
			
			this.parentWord = parentWord;
			setSynsetIDAndSynsetWordsForAdjective(wordToMake);
			
			if(parentWord == null){
				return;
			}
			
			else{
				SimilarityCalculatorAdjective sim = new SimilarityCalculatorAdjective(this, this.parentWord, 0);
				similarityWRTParent = sim.similarityWRTParent;
				similarityWRTRoot = sim.similarityWRTRoot;
			}
		}
		/*-------------------------------------------------------------------------------------------------------------*
		 * ---------------------------MAKE SYNSETS FOR Adjective PART OF SPEECH---------------------------------------------*
		 *-------------------------------------------------------------------------------------------------------------*/
		private void setSynsetIDAndSynsetWordsForAdjective(String word){
			
			//System.out.println("Initializing the Adjective part:"+word);
			if(word == null)
				return;
			Synset[] forTheWord = database.getSynsets((String)word);
			
			
			
			
			for(Synset synset:forTheWord){
				//Adding IDs of each synset to arraylist
				rawAdjectiveSynset.add(synset);
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
				
				tagCountsForAdjective.add(tagCount);
				//System.out.println("Tag count for the synset:"+synset.toString()+" is:"+tagCount+ " for the form:"+ synset.getType().toString()+" for the word:"+wordName);
				
				int singleSynsetID = Integer.parseInt(synset.toString().substring(indexOfAt+1, indexEnd));
				synsetIdForAdjective.add(singleSynsetID);
				
				
				String[] newArray = synset.getWordForms();
				ArrayList<String> newArrayList = new ArrayList<String>();
				
				//Adding each synset to an arraylist
				for(String wordFromSynset:newArray){
					newArrayList.add(wordFromSynset);
				}
				
				//adding the created Arraylist into Arraylist(of Arraylists)
				synsetsOfWordForAdjective.add(newArrayList);
			}
			
			int totalTagCount = 0;
			for(int i=0;i<tagCountsForAdjective.size();i++){
				totalTagCount += tagCountsForAdjective.get(i);
			}
			
			if(totalTagCount == 0){
				totalTagCount = 1;
			}
			
			for(int i=0;i<tagCountsForAdjective.size();i++){
				relativeFrequencyAdjective.add((float)tagCountsForAdjective.get(i)/(float)totalTagCount);
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
			
			WordClassAdjective word2Cmp = (WordClassAdjective)obj;
			
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

		public ArrayList<ArrayList<String>> getAdjectiveSynsets(){
			return synsetsOfWordForAdjective;
		}
		
		public ArrayList<Integer> getAdjectiveSynsetsID(){
			return synsetIdForAdjective;
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
			
			ArrayList<ArrayList<String>> AdjectiveSyn = getAdjectiveSynsets();
			ArrayList<Integer> AdjectiveID = getAdjectiveSynsetsID();
			ArrayList<Integer> AdjectiveTag = this.tagCountsForAdjective;
			
			String toPrint="";
			toPrint += "____________________________________________________________________________\n";
			toPrint += "_____________________________________________________________________________\n";
			toPrint += "______________________________________________________________________________\n\n";
			toPrint += "Word Name:"+word+"\n";
			toPrint += "------------------------------------------------------------------- \n\n";
			
			toPrint+= "Adjective Synsets:\n";
			for(int i=0;i<AdjectiveSyn.size();i++){
				toPrint += "Synset "+i+": "+AdjectiveSyn.get(i).toString()+"\nSynsetID:"+AdjectiveID.get(i)+"\nTag Count:"+AdjectiveTag.get(i)+"\n\n";	
			}
			
			return toPrint;
		}

		public ArrayList<Float> getRelativeFrequency(){
			return relativeFrequencyAdjective;
		}
		
		public ArrayList<Integer> getSynsetID(){
			return synsetIdForAdjective;
		}
}
