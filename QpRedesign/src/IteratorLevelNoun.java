import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import java.sql.*;
import com.mysql.jdbc.Statement;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;


public class IteratorLevelNoun {
	

	static int iterationLevel = 0;
	static String seedWord=null;
	static String seedWordDispute=null;
	static int k = 0;
	
	Set<WordClassNoun> iterationSet = new LinkedHashSet<WordClassNoun>();
	
	static Set<WordClassNoun> allWords = new LinkedHashSet<WordClassNoun>();
	
	public static String url = "jdbc:mysql://localhost:3306/";
	public static String dbName = "qpschema";
	public static String driver = "com.mysql.jdbc.driver";
	public static String usrname = "root";
	public static String pass = "engineering";
	public static Connection conn = null;
	
	public IteratorLevelNoun(){
		//Default constructor
	}
	
	public IteratorLevelNoun(Set<WordClassNoun> newLevel, int n){
		iterationLevel++;
		WordClassNoun rootWord = new WordClassNoun(seedWord, null);
		WordClassNoun rootWordDispute = new WordClassNoun(seedWordDispute, null);
		allWords.add(rootWord);
		allWords.add(rootWordDispute);
		Iterator<WordClassNoun> newIT = newLevel.iterator();
		
		/*----------------------------------------------------------------------------------------------
		 * 						Noun PART
		 * --------------------------------------------------------------------------------------------*/
		
		while(newIT.hasNext()){
			
			WordClassNoun newWord = newIT.next();
			
			ArrayList<ArrayList<String>> synsetsOfWord = newWord.synsetsOfWordForNoun;			
			//System.out.println("The array list of array list is:"+synsetsOfWord);
			
			for(int i=0;i<synsetsOfWord.size();i++){
				ArrayList<String> synsetList = synsetsOfWord.get(i);
				
				//System.out.println("The synset under consideration is:"+synsetList);
				
				for(int j=0;j<synsetList.size();j++){
					WordClassNoun wordToAdd = new WordClassNoun((String)synsetList.get(j), newWord);					
					
					if((!doesContain(wordToAdd,iterationSet))&& (!doesContain(wordToAdd,allWords))){
						iterationSet.add(wordToAdd);
						allWords.add(wordToAdd);
						//System.out.println(wordToAdd.wordName+" was added to the list");
					}
					
					else{
						//System.out.println(wordToAdd.wordName+" was already in the list");
					}
					
				}
			}
			
		}
		
		if(n==0)
			printTheSet();
		else if(n == 1)
			printTheSetDispute();
	}

	public void printTheSet(){
		Iterator<WordClassNoun> it = iterationSet.iterator();
		BufferedWriter file_output = null;
		File file=new File("out"+iterationLevel+"Noun.txt");
		int count = 0;
		try {
			file_output=new BufferedWriter(new FileWriter(file, true));
			file_output.write("---------------------");
			file_output.newLine();
			file_output.write("Seed Word:"+seedWord);
			file_output.newLine();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			file_output.write("Iteration level:"+iterationLevel);
			file_output.newLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Connection conn = connectDB();
				
		while(it.hasNext()){			
			WordClassNoun wordToPrint = it.next();
			count++;
			//System.out.println("Word in the set:"+ wordToPrint.wordName);
			try {
				file_output.write(count+": "+wordToPrint.wordName);
				file_output.newLine();
				file_output.write("From parent:"+wordToPrint.parentWord.wordName);
				file_output.newLine();
				file_output.write("Similarity:"+wordToPrint.similarityWRTParent);
				file_output.newLine();
				file_output.write("Similarity with root:"+wordToPrint.similarityWRTRoot);
				file_output.newLine();
				file_output.newLine();	
				
				Statement stmt = null;
			    String sql1 = "Insert into supportdictionary values ('" + seedWord + "','" + wordToPrint.wordName + "'," + wordToPrint.similarityWRTRoot + ",'" + "Noun" + "'," + iterationLevel + ")";
			    //String sql2 = "Insert into disputedictionary values ('" + seedWordDispute + "','" + wordToPrint.wordName + "'," + wordToPrint.similarityWRTRoot + ",'" + "Noun" + "'," + iterationLevel + ")";
	     	    try {
	     	    	 	stmt = (Statement) conn.createStatement();
	     	    	 	stmt.execute(sql1);
	     	    	 	//stmt.execute(sql2);
	     	    	 	conn.setAutoCommit(true);
	     	    	 	stmt.close();
	     	     	} catch (SQLException e) {
//	     	     		System.out.println("Data already exists");
//	     	     		System.out.println(wordToPrint.wordName);
	     	     		}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
					
		try {
			file_output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//----Close the connection------------//
		try 
        {
            if (conn != null) {
                conn.close();
                System.out.println("Close the connection to database!");
            	}
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
	}
	
	public void printTheSetDispute(){
		Iterator<WordClassNoun> it = iterationSet.iterator();
		BufferedWriter file_output = null;
		File file=new File("outDispute"+iterationLevel+"Noun.txt");
		int count = 0;
		try {
			file_output=new BufferedWriter(new FileWriter(file,true));
			file_output.write("-----------------------------");
			file_output.newLine();
			file_output.write("Seed Word:"+seedWordDispute);
			file_output.newLine();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			file_output.write("Iteration level:"+iterationLevel);
			file_output.newLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Connection conn = connectDB();
				
		while(it.hasNext()){			
			WordClassNoun wordToPrint = it.next();
			count++;
			//System.out.println("Word in the set:"+ wordToPrint.wordName);
			try {
				file_output.write(count+": "+wordToPrint.wordName);
				file_output.newLine();
				file_output.write("From parent:"+wordToPrint.parentWord.wordName);
				file_output.newLine();
				file_output.write("Similarity:"+wordToPrint.similarityWRTParent);
				file_output.newLine();
				file_output.write("Similarity with root:"+wordToPrint.similarityWRTRoot);
				file_output.newLine();
				file_output.newLine();	
				
				Statement stmt = null;
			    //String sql1 = "Insert into supportdictionary values ('" + seedWord + "','" + wordToPrint.wordName + "'," + wordToPrint.similarityWRTRoot + ",'" + "Noun" + "'," + iterationLevel + ")";
			    String sql2 = "Insert into disputedictionary values ('" + seedWordDispute + "','" + wordToPrint.wordName + "'," + wordToPrint.similarityWRTRoot + ",'" + "Noun" + "'," + iterationLevel + ")";
	     	    try {
	     	    	 	stmt = (Statement) conn.createStatement();
	     	    	 	//stmt.execute(sql1);
	     	    	 	stmt.execute(sql2);
	     	    	 	conn.setAutoCommit(true);
	     	    	 	stmt.close();
	     	     	} catch (SQLException e) {
//	     	     		System.out.println("Data already exists");
//	     	     		System.out.println(wordToPrint.wordName);
	     	     		}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
					
		try {
			file_output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//----Close the connection------------//
		try 
        {
            if (conn != null) {
                conn.close();
                System.out.println("Close the connection to database!");
            	}
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
	}
		
	public Connection connectDB() 
	    {
	        String url = "jdbc:mysql://localhost:3306/";
	        String dbName = "qpschema";
	        String driver = "com.mysql.jdbc.Driver";
	        String userName = "root";
	        String password = "engineering";
	        Connection conn = null;
	        try 
	        {
	            Class.forName(driver).newInstance();
	            conn = DriverManager.getConnection(url + dbName, userName, password);
	            //System.out.println("Connected to the database");
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	        return conn;
	    }
		
	public void closeConnection(Connection conn) 
    {
        try 
        {
            if (conn != null) {
                conn.close();
                //System.out.println("Close the connection to database!");
            	}
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	public boolean doesContain(WordClassNoun word, Set<WordClassNoun> iterationSet){
		
		Iterator<WordClassNoun> it = iterationSet.iterator();
		boolean returnValue = false;
		while(it.hasNext()){
			if(word.equals((WordClassNoun)it.next())){
				returnValue = true;
				break;
			}
		}
		
		return returnValue;
	}
	
	public Set<WordClassNoun> getSet(){
		return iterationSet;
	}
	
	public void makeHypernym(String hypernymWord, int identifier){
		
		WordClassNoun seedWord = new WordClassNoun(hypernymWord, null); 
		NounSynset parentSynset = null;
		NounSynset[] childSynset = null;
		int parentID=0;
		int childID=0;
		
		ArrayList<LinkedList<NounSynset>> hypernymMatrix = new ArrayList<LinkedList<NounSynset>>();
		Set<String> wordsInHypernymTree = new HashSet<String>();
		
		NounSynset[] rootSynsetsArray = new NounSynset[seedWord.rawNounSynset.size()];
		Integer[] rootSynsetsArrayID = new Integer[seedWord.rawNounSynset.size()];
		
		for(int i=0;i<seedWord.rawNounSynset.size();i++){
			rootSynsetsArray[i] = (NounSynset)seedWord.rawNounSynset.get(i);
			rootSynsetsArrayID[i] = seedWord.synsetIdForNoun.get(i);	
		}
		
		int count = 0;
		
		for(int j=0;j<rootSynsetsArray.length;j++){
			
			LinkedList<NounSynset> newLinkedList = new LinkedList<NounSynset>();
			NounSynset NounSyn = rootSynsetsArray[j];
			parentSynset = NounSyn;
			parentID = getSynsetID(parentSynset);
			childSynset = parentSynset.getHypernyms();
			if(childSynset.length == 0)
				break;
			childID = getSynsetID(childSynset[0]);
			count++;
			
			newLinkedList.add(parentSynset);
			newLinkedList.add(childSynset[0]);
			
			String wordsInChild[] = childSynset[0].getWordForms();
			
			for(int m=0;m<wordsInChild.length;m++){
				System.out.println("WOrd under consideration:"+wordsInChild[m]);
				if(!isPresentInDatabase(wordsInChild[m]) && !wordsInChild[m].equals(IteratorLevelNoun.seedWord)){
					wordsInHypernymTree.add(wordsInChild[m]);
				}
			}
			
			System.out.println("Before entering while loop");
			System.out.println("Parent Synset:"+parentSynset.toString()+"\nParentID:"+parentID+"\nChild Synset:"+childSynset[0].toString()+"\nChildID:"+childID);
			System.out.println("-------------------------------");
			while(true){
				parentSynset = childSynset[0];
				childSynset = parentSynset.getHypernyms();
				if(childSynset.length == 0)
					break;
				
				newLinkedList.add(childSynset[0]);
				parentID = getSynsetID(parentSynset);
				childID = getSynsetID(childSynset[0]);
				
				String wordsInChild2[] = childSynset[0].getWordForms();
				
				System.out.println(wordsInChild2.toString());
				for(int m=0;m<wordsInChild2.length;m++){
					System.out.println("WOrd under consideration:"+wordsInChild2[m]);
					if(!isPresentInDatabase(wordsInChild2[m]) && !wordsInChild2[m].equals(IteratorLevelNoun.seedWord))
						wordsInHypernymTree.add(wordsInChild2[m]);
				}
				
				System.out.println("Count:"+count);
				System.out.println("Parent Synset:"+parentSynset.toString()+"\nParentID:"+parentID+"\nChild Synset:"+childSynset[0].toString()+"\nChildID:"+childID);
				System.out.println("@-------------------------------");
			}
			
			hypernymMatrix.add(newLinkedList);
			System.out.println("After exiting while loop");
		}
		
		for(int k=0;k<hypernymMatrix.size();k++){
			System.out.println("Hypernym path "+(k+1)+" :"+hypernymMatrix.get(k).toString());	
		}
		
		Iterator<String> iterateWords = wordsInHypernymTree.iterator();
		
		while(iterateWords.hasNext()){
			String wordToFind = iterateWords.next();
			float similarity[] = new float[hypernymMatrix.size()];
			float similarityFromMatrix[] = new float[hypernymMatrix.size()];
			int pathMatrix[][] = new int[hypernymMatrix.size()][hypernymMatrix.size()];
			
			for(int i=0;i<pathMatrix.length;i++)
				for(int j=0;j<pathMatrix.length;j++)
					pathMatrix[i][j] = 0;
			
			for(int i=0;i<hypernymMatrix.size();i++){
				LinkedList<NounSynset> NounSyn = hypernymMatrix.get(i);
				float seedWordRelativeFrequency = seedWord.getRelativeFrequency().get(i);
				
				for(int j=0;j<NounSyn.size();j++){	
					NounSynset vs = NounSyn.get(j);
					String[] words = vs.getWordForms();
					
					for(int k=0;k<words.length;k++){
						if(words[k].equals(wordToFind)){
							System.out.println("Found the word:"+wordToFind+", in the path:"+(i+1)+", the path length is:"+(j));
							//Generating path matrix for the word under consideration
							for(int n=0;n<NounSyn.size();n++){
								Set<Integer> baseIdSet = new HashSet<Integer>();
								
								//putting all the intermediate id of the current path inside set
								for(int p=1;p<j;p++){
									baseIdSet.add(getSynsetID(NounSyn.get(p)));
								}
								
								for(int q=0;q<hypernymMatrix.size();q++){
									if(q==i)
										continue;
									LinkedList<NounSynset> toFindIn = hypernymMatrix.get(q);
									for(int r=1; r<toFindIn.size();r++)
									{
										boolean check = baseIdSet.add(getSynsetID(toFindIn.get(r)));
										if(check){
											baseIdSet.remove(getSynsetID(toFindIn.get(r)));
										}
										
										if(!check){
											pathMatrix[i][q] = 1;
											break;
										}
									}
								}
							}
							
							WordClassNoun wordInPath = new WordClassNoun(words[k], null);
							ArrayList<Integer> synsetIds = wordInPath.getSynsetID();
							int synsetUnderConsiderationID = getSynsetID(vs);
							float relativeFrequencyForTheWordUnderConsideration = 0;
							for(int m=0;m<synsetIds.size();m++){
								if(synsetUnderConsiderationID == synsetIds.get(m)){
									relativeFrequencyForTheWordUnderConsideration = wordInPath.getRelativeFrequency().get(m);
								}
							}
							
							similarity[i] = relativeFrequencyForTheWordUnderConsideration * seedWordRelativeFrequency * (1/(float)(Math.pow(j, j)+1));
							System.out.println("Similarity for the current word:"+ similarity[i]);
							
						}
					}
				}
				
			}
			
			System.out.println("Path matrix for the word:"+wordToFind);
			for(int i=0;i<pathMatrix.length;i++){
				similarityFromMatrix[i] = similarity[i];
				for(int j=0;j<pathMatrix.length;j++){
					System.out.print(pathMatrix[i][j]+" ");
					if(pathMatrix[i][j] == 1){
						similarityFromMatrix[i]+=similarity[j];
					}
				}
				
				System.out.println("");
			}
			
			float finalSimilarity = similarityFromMatrix[0];
			for(int i=1;i<similarityFromMatrix.length;i++){
				if(finalSimilarity < similarityFromMatrix[i])
					finalSimilarity = similarityFromMatrix[i];
			}
			
			System.out.println("Final Similarity:"+finalSimilarity);
			
			conn = connectDB();
			Statement stmt = null;
			String sql1;
			if(identifier==1)
				sql1 = "Insert into disputedictionary values ('" + seedWord.wordName + "','" + wordToFind + "'," + finalSimilarity + ",'" + "Noun" + "',0)";
			else
				sql1 = "Insert into supportdictionary values ('" + seedWord.wordName + "','" + wordToFind + "'," + finalSimilarity + ",'" + "Noun" + "',0)";
		    //String sql2 = "Insert into disputedictionary values ('" + seedWordDispute + "','" + wordToPrint.wordName + "'," + wordToPrint.similarityWRTRoot + ",'" + "Noun" + "'," + iterationLevel + ")";
     	    try {
     	    	 	stmt = (Statement) conn.createStatement();
     	    	 	stmt.execute(sql1);
     	    	 	//stmt.execute(sql2);
     	    	 	conn.setAutoCommit(true);
     	    	 	stmt.close();
     	     	} catch (SQLException e) {
     	     		}

			closeConnection(conn);
			
			
		}
		
		//Iterating through the hypernyms to find paths 
		
		
	}

	public boolean isPresentInDatabase(String word){
		Connection conn = connectDB();
		ResultSet resultSet;
		Statement stmt;
		boolean isPresent = false;
		try {
			stmt = (Statement) conn.createStatement();
			resultSet = stmt.executeQuery("select supportword from qpschema.supportdictionary where SupportWord='"+word+"'");
			isPresent = resultSet.next();
			//System.out.println("Present:"+isPresent);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
		closeConnection(conn);
		
		return isPresent;
		
	}
	
	public int getSynsetID(NounSynset synset){
		int indexOfAt = synset.toString().indexOf("@");
		int indexEnd = synset.toString().indexOf("[");
		int singleSynsetID = Integer.parseInt(synset.toString().substring(indexOfAt+1, indexEnd));
		return singleSynsetID;
	}
		
	public void iterateNounDispute(String seedWordDispute, int maxIterationLevel){
		IteratorLevelNoun.k = maxIterationLevel;
		IteratorLevelNoun.iterationLevel = 0;
		IteratorLevelNoun.seedWordDispute = seedWordDispute;
		WordClassNoun newWordDispute = new WordClassNoun(IteratorLevelNoun.seedWordDispute, null);
		Set<WordClassNoun> newListDispute = new LinkedHashSet<WordClassNoun>();
		newListDispute.add(newWordDispute);
		IteratorLevelNoun[] levelDispute = new IteratorLevelNoun[k];
		levelDispute[0] = new IteratorLevelNoun(newListDispute,1);
		
		//Check for adverb synsets
		Iterator<WordClassNoun> iterateForAdverb = newListDispute.iterator();
		while(iterateForAdverb.hasNext()){
			String adverbWord = iterateForAdverb.next().wordName;
			findAdverbSynsetsDispute(adverbWord, maxIterationLevel);
		}
		
		for(int i=1;i<k;i++){
			newListDispute = levelDispute[i-1].getSet();			
			levelDispute[i] = new IteratorLevelNoun(newListDispute,1);
			
			//Check for adverb synsets		
			Set<WordClassNoun> newListAdverb = levelDispute[i].getSet();
			iterateForAdverb = newListAdverb.iterator();
			while(iterateForAdverb.hasNext()){
				String adverbWord = iterateForAdverb.next().wordName;
				findAdverbSynsetsDispute(adverbWord, maxIterationLevel);
			}
		}
		
		levelDispute[0].makeHypernym(seedWordDispute,1);
	}
		
	public void iterateNounSupport(String seedWord, int  maxIterationLevel){
		
		IteratorLevelNoun.seedWord = seedWord;
		IteratorLevelNoun.iterationLevel = 0;
		WordClassNoun newWord = new WordClassNoun(IteratorLevelNoun.seedWord, null);
		Set<WordClassNoun> newList = new LinkedHashSet<WordClassNoun>();
		newList.add(newWord);
		IteratorLevelNoun.k=maxIterationLevel;
		IteratorLevelNoun[] level = new IteratorLevelNoun[IteratorLevelNoun.k];
		level[0] = new IteratorLevelNoun(newList,0);
		
		//Check for adverb synsets
		Iterator<WordClassNoun> iterateForAdverb = newList.iterator();
		while(iterateForAdverb.hasNext()){
			String adverbWord = iterateForAdverb.next().wordName;
			findAdverbSynsetsSupport(adverbWord, maxIterationLevel);
		}
		
		for(int i=1;i<k;i++){
			newList = level[i-1].getSet();			
			level[i] = new IteratorLevelNoun(newList,0);
			
			//Check for adverb synsets		
			Set<WordClassNoun> newListAdverb = level[i].getSet();
			iterateForAdverb = newListAdverb.iterator();
			while(iterateForAdverb.hasNext()){
				String adverbWord = iterateForAdverb.next().wordName;
				findAdverbSynsetsSupport(adverbWord, maxIterationLevel);
			}
		}
		
		level[0].makeHypernym(seedWord,0);
	}
	
	public void findAdverbSynsetsSupport(String word, int maxIterationLevel){
		//Testing for adverb synsets for this word
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] forTheWordAdverb = database.getSynsets(word, SynsetType.ADVERB);
		if(forTheWordAdverb.length>0){
			
			System.out.println("----------------------------------------------------------------------------------------");
			System.out.println("Found support Adverb synset in the noun class word for the word:"+word);
			//Support
			IteratorLevelAdverb newAdverb = new IteratorLevelAdverb();
			newAdverb.iterateAdverbSupport(word, maxIterationLevel);
			
			System.out.println("----------------------------------------------------------------------------------------");
			
		}
	}
	
	public void findAdverbSynsetsDispute(String word, int maxIterationLevel){
		//Testing for adverb synsets for this word
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] forTheWordAdverb = database.getSynsets(word, SynsetType.ADVERB);
		if(forTheWordAdverb.length>0){
			System.out.println("----------------------------------------------------------------------------------------");
			System.out.println("Found Dispute Adverb synset in the noun class word for the word:"+word);
			
			//Dispute
			IteratorLevelAdverb newAdverb = new IteratorLevelAdverb();
			newAdverb.iterateAdverbDispute(word, maxIterationLevel);
			System.out.println("----------------------------------------------------------------------------------------");
		}
	}
	
	public static void main(String args[]){
		
		System.out.println("Please Enter Support Word");
		
		Scanner read = new Scanner(System.in);
		seedWord = read.next();
		//read.close();
		

		System.out.println("Please Enter Dispute Word");
		
		//Scanner readDispute = new Scanner(System.in);
		seedWordDispute = read.next();
		read.close();
		
		WordClassNoun newWord = new WordClassNoun(seedWord, null);
		Set<WordClassNoun> newList = new LinkedHashSet<WordClassNoun>();
		newList.add(newWord);
		int k = 2;
		IteratorLevelNoun[] level = new IteratorLevelNoun[k];
		level[0] = new IteratorLevelNoun(newList,0);
		for(int i=1;i<k;i++){
			newList = level[i-1].getSet();
			level[i] = new IteratorLevelNoun(newList,0);
		}
		
		IteratorLevelNoun.iterationLevel = 0;
		
		WordClassNoun newWordDispute = new WordClassNoun(seedWordDispute, null);
		Set<WordClassNoun> newListDispute = new LinkedHashSet<WordClassNoun>();
		newListDispute.add(newWordDispute);
		IteratorLevelNoun[] levelDispute = new IteratorLevelNoun[k];
		levelDispute[0] = new IteratorLevelNoun(newListDispute,1);
		for(int i=1;i<k;i++){
			newListDispute = levelDispute[i-1].getSet();
			levelDispute[i] = new IteratorLevelNoun(newListDispute,1);
		}
		
		level[0].makeHypernym(seedWord, 0);
		level[0].makeHypernym(seedWordDispute, 1);
		
	}	

	
}
