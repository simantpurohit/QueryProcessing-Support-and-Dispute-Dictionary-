import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream.GetField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import java.sql.*;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import com.mysql.jdbc.Statement;

import edu.smu.tspell.wordnet.AdverbSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;


public class IteratorLevelVerb {
	

	static int iterationLevel = 0;
	static String seedWord=null;
	static String seedWordDispute=null;
	static int k = 0;
	
	Set<WordClassVerb> iterationSet = new LinkedHashSet<WordClassVerb>();
	
	static Set<WordClassVerb> allWords = new LinkedHashSet<WordClassVerb>();
	
	public static String url = "jdbc:mysql://localhost:3306/";
	public static String dbName = "qpschema";
	public static String driver = "com.mysql.jdbc.driver";
	public static String usrname = "root";
	public static String pass = "engineering";
	public static Connection conn = null;
	
	public IteratorLevelVerb(){
		//Default constructor
	}
	
	public IteratorLevelVerb(Set<WordClassVerb> newLevel, int n){
		iterationLevel++;
		WordClassVerb rootWord = new WordClassVerb(seedWord, null);
		WordClassVerb rootWordDispute = new WordClassVerb(seedWordDispute, null);
		allWords.add(rootWord);
		allWords.add(rootWordDispute);
		Iterator<WordClassVerb> newIT = newLevel.iterator();
		
		/*----------------------------------------------------------------------------------------------
		 * 						VERB PART
		 * --------------------------------------------------------------------------------------------*/
		
		while(newIT.hasNext()){
			
			WordClassVerb newWord = newIT.next();
			
			ArrayList<ArrayList<String>> synsetsOfWord = newWord.synsetsOfWordForVerb;			
			//System.out.println("The array list of array list is:"+synsetsOfWord);
			
			for(int i=0;i<synsetsOfWord.size();i++){
				ArrayList<String> synsetList = synsetsOfWord.get(i);
				
				//System.out.println("The synset under consideration is:"+synsetList);
				
				for(int j=0;j<synsetList.size();j++){
					WordClassVerb wordToAdd = new WordClassVerb((String)synsetList.get(j), newWord);					
					
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
		Iterator<WordClassVerb> it = iterationSet.iterator();
		BufferedWriter file_output = null;
		File file=new File("out"+iterationLevel+"VERB.txt");
		
		int count = 0;
		try {
			file_output=new BufferedWriter(new FileWriter(file, true));
			file_output.write("-------------");
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
			WordClassVerb wordToPrint = it.next();
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
			    String sql1 = "Insert into supportdictionary values ('" + seedWord + "','" + wordToPrint.wordName + "'," + wordToPrint.similarityWRTRoot + ",'" + "VERB" + "'," + iterationLevel + ")";
			    //String sql2 = "Insert into disputedictionary values ('" + seedWordDispute + "','" + wordToPrint.wordName + "'," + wordToPrint.similarityWRTRoot + ",'" + "VERB" + "'," + iterationLevel + ")";
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
		Iterator<WordClassVerb> it = iterationSet.iterator();
		BufferedWriter file_output = null;
		File file=new File("outDispute"+iterationLevel+"VERB.txt");
		int count = 0;
		try {
			file_output=new BufferedWriter(new FileWriter(file,true));
			file_output.write("----------------");
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
			WordClassVerb wordToPrint = it.next();
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
			    //String sql1 = "Insert into supportdictionary values ('" + seedWord + "','" + wordToPrint.wordName + "'," + wordToPrint.similarityWRTRoot + ",'" + "VERB" + "'," + iterationLevel + ")";
			    String sql2 = "Insert into disputedictionary values ('" + seedWordDispute + "','" + wordToPrint.wordName + "'," + wordToPrint.similarityWRTRoot + ",'" + "VERB" + "'," + iterationLevel + ")";
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

	public boolean doesContain(WordClassVerb word, Set<WordClassVerb> iterationSet){
		
		Iterator<WordClassVerb> it = iterationSet.iterator();
		boolean returnValue = false;
		while(it.hasNext()){
			if(word.equals((WordClassVerb)it.next())){
				returnValue = true;
				break;
			}
		}
		
		return returnValue;
	}
	
	public Set<WordClassVerb> getSet(){
		return iterationSet;
	}
	
	public void makeHypernym(String hypernymWord, int identifier){
		
		WordClassVerb seedWord = new WordClassVerb(hypernymWord, null); 
		VerbSynset parentSynset = null;
		VerbSynset[] childSynset = null;
		int parentID=0;
		int childID=0;
		
		ArrayList<LinkedList<VerbSynset>> hypernymMatrix = new ArrayList<LinkedList<VerbSynset>>();
		Set<String> wordsInHypernymTree = new HashSet<String>();
		
		VerbSynset[] rootSynsetsArray = new VerbSynset[seedWord.rawVerbSynset.size()];
		Integer[] rootSynsetsArrayID = new Integer[seedWord.rawVerbSynset.size()];
		
		for(int i=0;i<seedWord.rawVerbSynset.size();i++){
			rootSynsetsArray[i] = (VerbSynset)seedWord.rawVerbSynset.get(i);
			rootSynsetsArrayID[i] = seedWord.synsetIdForVerb.get(i);	
		}
		
		int count = 0;
		
		for(int j=0;j<rootSynsetsArray.length;j++){
			
			LinkedList<VerbSynset> newLinkedList = new LinkedList<VerbSynset>();
			VerbSynset verbSyn = rootSynsetsArray[j];
			parentSynset = verbSyn;
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
				if(!isPresentInDatabase(wordsInChild[m]) && !wordsInChild[m].equals(IteratorLevelVerb.seedWord)){
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
					if(!isPresentInDatabase(wordsInChild2[m]) && !wordsInChild2[m].equals(IteratorLevelVerb.seedWord))
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
				LinkedList<VerbSynset> verbSyn = hypernymMatrix.get(i);
				float seedWordRelativeFrequency = seedWord.getRelativeFrequency().get(i);
				
				for(int j=0;j<verbSyn.size();j++){	
					VerbSynset vs = verbSyn.get(j);
					String[] words = vs.getWordForms();
					
					for(int k=0;k<words.length;k++){
						if(words[k].equals(wordToFind)){
							System.out.println("Found the word:"+wordToFind+", in the path:"+(i+1)+", the path length is:"+(j));
							//Generating path matrix for the word under consideration
							for(int n=0;n<verbSyn.size();n++){
								Set<Integer> baseIdSet = new HashSet<Integer>();
								
								//putting all the intermediate id of the current path inside set
								for(int p=1;p<j;p++){
									baseIdSet.add(getSynsetID(verbSyn.get(p)));
								}
								
								for(int q=0;q<hypernymMatrix.size();q++){
									if(q==i)
										continue;
									LinkedList<VerbSynset> toFindIn = hypernymMatrix.get(q);
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
							
							WordClassVerb wordInPath = new WordClassVerb(words[k], null);
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
				sql1 = "Insert into disputedictionary values ('" + seedWord.wordName + "','" + wordToFind + "'," + finalSimilarity + ",'" + "VERB" + "',0)";
			else
				sql1 = "Insert into supportdictionary values ('" + seedWord.wordName + "','" + wordToFind + "'," + finalSimilarity + ",'" + "VERB" + "',0)";
		    //String sql2 = "Insert into disputedictionary values ('" + seedWordDispute + "','" + wordToPrint.wordName + "'," + wordToPrint.similarityWRTRoot + ",'" + "VERB" + "'," + iterationLevel + ")";
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
	
	public int getSynsetID(VerbSynset synset){
		int indexOfAt = synset.toString().indexOf("@");
		int indexEnd = synset.toString().indexOf("[");
		int singleSynsetID = Integer.parseInt(synset.toString().substring(indexOfAt+1, indexEnd));
		return singleSynsetID;
	}
		
	public void iterateVerbDispute(String seedWordDispute, int maxIterationLevel){
		IteratorLevelVerb.k = maxIterationLevel;
		IteratorLevelVerb.iterationLevel = 0;
		IteratorLevelVerb.seedWordDispute = seedWordDispute;
		WordClassVerb newWordDispute = new WordClassVerb(IteratorLevelVerb.seedWordDispute, null);
		Set<WordClassVerb> newListDispute = new LinkedHashSet<WordClassVerb>();
		newListDispute.add(newWordDispute);
		IteratorLevelVerb[] levelDispute = new IteratorLevelVerb[k];
		levelDispute[0] = new IteratorLevelVerb(newListDispute,1);
		
		//Check for adverb synsets
		Iterator<WordClassVerb> iterateForAdverb = newListDispute.iterator();
		while(iterateForAdverb.hasNext()){
			String adverbWord = iterateForAdverb.next().wordName;
			findAdverbSynsetsDispute(adverbWord, maxIterationLevel);
		}
		
		for(int i=1;i<k;i++){
			newListDispute = levelDispute[i-1].getSet();			
			levelDispute[i] = new IteratorLevelVerb(newListDispute,1);
			
			//Check for adverb synsets		
			Set<WordClassVerb> newListAdverb = levelDispute[i].getSet();
			iterateForAdverb = newListAdverb.iterator();
			while(iterateForAdverb.hasNext()){
				String adverbWord = iterateForAdverb.next().wordName;
				findAdverbSynsetsDispute(adverbWord, maxIterationLevel);
			}
		}
		
		levelDispute[0].makeHypernym(seedWordDispute,1);
	}
	
	public void iterateVerbSupport(String seedWord, int  maxIterationLevel){
		
		IteratorLevelVerb.seedWord = seedWord;
		IteratorLevelVerb.iterationLevel = 0;
		WordClassVerb newWord = new WordClassVerb(IteratorLevelVerb.seedWord, null);
		Set<WordClassVerb> newList = new LinkedHashSet<WordClassVerb>();
		newList.add(newWord);
		IteratorLevelVerb.k=maxIterationLevel;
		IteratorLevelVerb[] level = new IteratorLevelVerb[IteratorLevelVerb.k];
		level[0] = new IteratorLevelVerb(newList,0);
		
		//Check for adverb synsets
		Iterator<WordClassVerb> iterateForAdverb = newList.iterator();
		while(iterateForAdverb.hasNext()){
			String adverbWord = iterateForAdverb.next().wordName;
			findAdverbSynsetsSupport(adverbWord, maxIterationLevel);
		}
		
		for(int i=1;i<k;i++){
			newList = level[i-1].getSet();
			//Check for adverb synsets			
			level[i] = new IteratorLevelVerb(newList,0);
			
			//Check for adverb synsets		
			Set<WordClassVerb> newListAdverb = level[i].getSet();
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
			System.out.println("Found support Adverb synset in the verb class word for the word:"+word);
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
			System.out.println("Found Dispute Adverb synset in the verb class word for the word:"+word);
			
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
		
		WordClassVerb newWord = new WordClassVerb(seedWord, null);
		Set<WordClassVerb> newList = new LinkedHashSet<WordClassVerb>();
		newList.add(newWord);
		int k = 2;
		IteratorLevelVerb[] level = new IteratorLevelVerb[k];
		level[0] = new IteratorLevelVerb(newList,0);
		for(int i=1;i<k;i++){
			newList = level[i-1].getSet();
			level[i] = new IteratorLevelVerb(newList,0);
		}
		
		IteratorLevelVerb.iterationLevel = 0;
		
		WordClassVerb newWordDispute = new WordClassVerb(seedWordDispute, null);
		Set<WordClassVerb> newListDispute = new LinkedHashSet<WordClassVerb>();
		newListDispute.add(newWordDispute);
		IteratorLevelVerb[] levelDispute = new IteratorLevelVerb[k];
		levelDispute[0] = new IteratorLevelVerb(newListDispute,1);
		for(int i=1;i<k;i++){
			newListDispute = levelDispute[i-1].getSet();
			levelDispute[i] = new IteratorLevelVerb(newListDispute,1);
		}
		
		level[0].makeHypernym(seedWord, 0);
		level[0].makeHypernym(seedWordDispute, 1);
		
	}	

	
}
