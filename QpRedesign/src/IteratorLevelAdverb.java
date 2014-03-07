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
import com.mysql.jdbc.Statement;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.AdverbSynset;


public class IteratorLevelAdverb {
	

	static int iterationLevel = 0;
	static String seedWord=null;
	static String seedWordDispute=null;
	static int k = 0;
	
	Set<WordClassAdverb> iterationSet = new LinkedHashSet<WordClassAdverb>();
	
	static Set<WordClassAdverb> allWords = new LinkedHashSet<WordClassAdverb>();
	
	public static String url = "jdbc:mysql://localhost:3306/";
	public static String dbName = "qpschema";
	public static String driver = "com.mysql.jdbc.driver";
	public static String usrname = "root";
	public static String pass = "engineering";
	public static Connection conn = null;
	
	public IteratorLevelAdverb(){
		//Default constructor
	}
	
	public IteratorLevelAdverb(Set<WordClassAdverb> newLevel, int n){
		iterationLevel++;
		WordClassAdverb rootWord = new WordClassAdverb(seedWord, null);
		WordClassAdverb rootWordDispute = new WordClassAdverb(seedWordDispute, null);
		allWords.add(rootWord);
		allWords.add(rootWordDispute);
		Iterator<WordClassAdverb> newIT = newLevel.iterator();
		
		/*----------------------------------------------------------------------------------------------
		 * 						Adverb PART
		 * --------------------------------------------------------------------------------------------*/
		
		while(newIT.hasNext()){
			
			WordClassAdverb newWord = newIT.next();
			
			ArrayList<ArrayList<String>> synsetsOfWord = newWord.synsetsOfWordForAdverb;			
			//System.out.println("The array list of array list is:"+synsetsOfWord);
			
			for(int i=0;i<synsetsOfWord.size();i++){
				ArrayList<String> synsetList = synsetsOfWord.get(i);
				
				//System.out.println("The synset under consideration is:"+synsetList);
				
				for(int j=0;j<synsetList.size();j++){
					WordClassAdverb wordToAdd = new WordClassAdverb((String)synsetList.get(j), newWord);					
					
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
		Iterator<WordClassAdverb> it = iterationSet.iterator();
		BufferedWriter file_output = null;
		File file=new File("out"+iterationLevel+"Adverb.txt");
		int count = 0;
		try {
			file_output=new BufferedWriter(new FileWriter(file, true));
			file_output.write("-----------------------------");
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
			WordClassAdverb wordToPrint = it.next();
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
			    String sql1 = "Insert into supportdictionary values ('" + seedWord + "','" + wordToPrint.wordName + "'," + wordToPrint.similarityWRTRoot + ",'" + "Adverb" + "'," + iterationLevel + ")";
			    //String sql2 = "Insert into disputedictionary values ('" + seedWordDispute + "','" + wordToPrint.wordName + "'," + wordToPrint.similarityWRTRoot + ",'" + "Adverb" + "'," + iterationLevel + ")";
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
		Iterator<WordClassAdverb> it = iterationSet.iterator();
		BufferedWriter file_output = null;
		File file=new File("outDispute"+iterationLevel+"Adverb.txt");
		int count = 0;
		try {
			file_output=new BufferedWriter(new FileWriter(file,true));
			file_output.write("------------------");
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
			WordClassAdverb wordToPrint = it.next();
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
			    //String sql1 = "Insert into supportdictionary values ('" + seedWord + "','" + wordToPrint.wordName + "'," + wordToPrint.similarityWRTRoot + ",'" + "Adverb" + "'," + iterationLevel + ")";
			    String sql2 = "Insert into disputedictionary values ('" + seedWordDispute + "','" + wordToPrint.wordName + "'," + wordToPrint.similarityWRTRoot + ",'" + "Adverb" + "'," + iterationLevel + ")";
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
	
	public boolean doesContain(WordClassAdverb word, Set<WordClassAdverb> iterationSet){
		
		Iterator<WordClassAdverb> it = iterationSet.iterator();
		boolean returnValue = false;
		while(it.hasNext()){
			if(word.equals((WordClassAdverb)it.next())){
				returnValue = true;
				break;
			}
		}
		
		return returnValue;
	}
	
	public Set<WordClassAdverb> getSet(){
		return iterationSet;
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
	
	public int getSynsetID(AdverbSynset synset){
		int indexOfAt = synset.toString().indexOf("@");
		int indexEnd = synset.toString().indexOf("[");
		int singleSynsetID = Integer.parseInt(synset.toString().substring(indexOfAt+1, indexEnd));
		return singleSynsetID;
	}
		
	public void iterateAdverbDispute(String seedWordDispute, int maxIterationLevel){
		IteratorLevelAdverb.k = maxIterationLevel;
		IteratorLevelAdverb.iterationLevel = 0;
		IteratorLevelAdverb.seedWordDispute = seedWordDispute;
		WordClassAdverb newWordDispute = new WordClassAdverb(IteratorLevelAdverb.seedWordDispute, null);
		Set<WordClassAdverb> newListDispute = new LinkedHashSet<WordClassAdverb>();
		newListDispute.add(newWordDispute);
		IteratorLevelAdverb[] levelDispute = new IteratorLevelAdverb[k];
		levelDispute[0] = new IteratorLevelAdverb(newListDispute,1);
		for(int i=1;i<k;i++){
			newListDispute = levelDispute[i-1].getSet();
			levelDispute[i] = new IteratorLevelAdverb(newListDispute,1);
		}
		
		//levelDispute[0].makeHypernym(seedWordDispute,1);
	}
		
	public void iterateAdverbSupport(String seedWord, int  maxIterationLevel){
		
		IteratorLevelAdverb.seedWord = seedWord;
		IteratorLevelAdverb.iterationLevel = 0;
		WordClassAdverb newWord = new WordClassAdverb(IteratorLevelAdverb.seedWord, null);
		Set<WordClassAdverb> newList = new LinkedHashSet<WordClassAdverb>();
		newList.add(newWord);
		IteratorLevelAdverb.k=maxIterationLevel;
		IteratorLevelAdverb[] level = new IteratorLevelAdverb[IteratorLevelAdverb.k];
		level[0] = new IteratorLevelAdverb(newList,0);
		for(int i=1;i<k;i++){
			newList = level[i-1].getSet();
			level[i] = new IteratorLevelAdverb(newList,0);
		}
		
		//level[0].makeHypernym(seedWord,0);
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
		
		WordClassAdverb newWord = new WordClassAdverb(seedWord, null);
		Set<WordClassAdverb> newList = new LinkedHashSet<WordClassAdverb>();
		newList.add(newWord);
		int k = 2;
		IteratorLevelAdverb[] level = new IteratorLevelAdverb[k];
		level[0] = new IteratorLevelAdverb(newList,0);
		for(int i=1;i<k;i++){
			newList = level[i-1].getSet();
			level[i] = new IteratorLevelAdverb(newList,0);
		}
		
		IteratorLevelAdverb.iterationLevel = 0;
		
		WordClassAdverb newWordDispute = new WordClassAdverb(seedWordDispute, null);
		Set<WordClassAdverb> newListDispute = new LinkedHashSet<WordClassAdverb>();
		newListDispute.add(newWordDispute);
		IteratorLevelAdverb[] levelDispute = new IteratorLevelAdverb[k];
		levelDispute[0] = new IteratorLevelAdverb(newListDispute,1);
		for(int i=1;i<k;i++){
			newListDispute = levelDispute[i-1].getSet();
			levelDispute[i] = new IteratorLevelAdverb(newListDispute,1);
		}
	}	

	
}
