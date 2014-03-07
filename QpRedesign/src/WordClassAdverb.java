import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.AdverbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordNetException;


public class WordClassAdverb {

	//The word from which this word was derived
		WordClassAdverb parentWord=null;
		
		//String form of the word
		String wordName;
		
		float similarityWRTParent=0;
		float similarityWRTRoot=1;
		
		
		ArrayList<Synset> rawAdverbSynset = new ArrayList<Synset>();		
		ArrayList<Float> relativeFrequencyAdverb = new ArrayList<Float>();
		ArrayList<Integer> synsetIdForAdverb = new ArrayList<Integer>();
		ArrayList<Integer> tagCountsForAdverb = new ArrayList<Integer>();
		ArrayList<ArrayList<String>> synsetsOfWordForAdverb = new ArrayList<ArrayList<String>>();
		
		
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		
		/*-------------------------------------------------------------------------------------------------------------*
		 * -----------------------------------------------CONSTRUCTOR---------------------------------------------*
		 *-------------------------------------------------------------------------------------------------------------*/
		public WordClassAdverb(String wordToMake, WordClassAdverb parentWord){
			
			System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\2.1\\dict");
			
			wordName=wordToMake;
			
			this.parentWord = parentWord;
			setSynsetIDAndSynsetWordsForAdverb(wordToMake);
			
			if(parentWord == null){
				return;
			}
			
			else{
				SimilarityCalculatorAdverb sim = new SimilarityCalculatorAdverb(this, this.parentWord, 0);
				similarityWRTParent = sim.similarityWRTParent;
				similarityWRTRoot = sim.similarityWRTRoot;
			}
		}
		/*-------------------------------------------------------------------------------------------------------------*
		 * ---------------------------MAKE SYNSETS FOR Adverb PART OF SPEECH---------------------------------------------*
		 *-------------------------------------------------------------------------------------------------------------*/
		private void setSynsetIDAndSynsetWordsForAdverb(String word){
			
			//System.out.println("Initializing the Adverb part:"+word);
			if(word == null)
				return;
			Synset[] forTheWord = database.getSynsets((String)word, SynsetType.ADVERB);
			
			
			
			
			for(Synset synset:forTheWord){
				//Adding IDs of each synset to arraylist
				rawAdverbSynset.add(synset);
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
				
				tagCountsForAdverb.add(tagCount);
				//System.out.println("Tag count for the synset:"+synset.toString()+" is:"+tagCount+ " for the form:"+ synset.getType().toString()+" for the word:"+wordName);
				
				int singleSynsetID = Integer.parseInt(synset.toString().substring(indexOfAt+1, indexEnd));
				synsetIdForAdverb.add(singleSynsetID);
				
				
				String[] newArray = synset.getWordForms();
				ArrayList<String> newArrayList = new ArrayList<String>();
				
				//Adding each synset to an arraylist
				for(String wordFromSynset:newArray){
					newArrayList.add(wordFromSynset);
				}
				
				//adding the created Arraylist into Arraylist(of Arraylists)
				synsetsOfWordForAdverb.add(newArrayList);
			}
			
			int totalTagCount = 0;
			for(int i=0;i<tagCountsForAdverb.size();i++){
				totalTagCount += tagCountsForAdverb.get(i);
			}
			
			if(totalTagCount == 0){
				totalTagCount = 1;
			}
			
			for(int i=0;i<tagCountsForAdverb.size();i++){
				relativeFrequencyAdverb.add((float)tagCountsForAdverb.get(i)/(float)totalTagCount);
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
			
			WordClassAdverb word2Cmp = (WordClassAdverb)obj;
			
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

		public ArrayList<ArrayList<String>> getAdverbSynsets(){
			return synsetsOfWordForAdverb;
		}
		
		public ArrayList<Integer> getAdverbSynsetsID(){
			return synsetIdForAdverb;
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
			
			ArrayList<ArrayList<String>> AdverbSyn = getAdverbSynsets();
			ArrayList<Integer> AdverbID = getAdverbSynsetsID();
			ArrayList<Integer> AdverbTag = this.tagCountsForAdverb;
			
			String toPrint="";
			toPrint += "____________________________________________________________________________\n";
			toPrint += "_____________________________________________________________________________\n";
			toPrint += "______________________________________________________________________________\n\n";
			toPrint += "Word Name:"+word+"\n";
			toPrint += "------------------------------------------------------------------- \n\n";
			
			toPrint+= "Adverb Synsets:\n";
			for(int i=0;i<AdverbSyn.size();i++){
				toPrint += "Synset "+i+": "+AdverbSyn.get(i).toString()+"\nSynsetID:"+AdverbID.get(i)+"\nTag Count:"+AdverbTag.get(i)+"\n\n";	
			}
			
			return toPrint;
		}

		public ArrayList<Float> getRelativeFrequency(){
			return relativeFrequencyAdverb;
		}
		
		public ArrayList<Integer> getSynsetID(){
			return synsetIdForAdverb;
		}
}
