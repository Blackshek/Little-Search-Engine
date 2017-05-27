package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author RU-NB-CS112
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			//System.out.println(docFile);
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) throws FileNotFoundException {
		HashMap<String,Occurrence> ret = new HashMap<String,Occurrence>(1000 , 2.0f);
		Scanner scfile;
		
		try{
			scfile = new Scanner(new File(docFile));
		}catch(FileNotFoundException ex){
			throw new FileNotFoundException("File Name Not Found.");
		}
		
		while(scfile.hasNext()){
			String word = getKeyWord(scfile.next());
			if(word == null){
				continue;
			}
			else{
				if(ret.containsKey(word)){
					Occurrence frq1 = ret.get(word);
					frq1.frequency++;
					ret.put(word, frq1);
				}
				else{
					Occurrence wordOcc = new Occurrence(docFile, 1);
					ret.put(word, wordOcc);
				}
			}
		}
		scfile.close();
		return ret;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		for(String key : kws.keySet()){
			if(keywordsIndex.containsKey(key)){
				ArrayList<Occurrence> arr = keywordsIndex.get(key);
				arr.add(kws.get(key));
				insertLastOccurrence(arr);
				keywordsIndex.put(key, arr);
			}
			else{
				ArrayList<Occurrence> arr = new ArrayList<Occurrence>();
				arr.add(kws.get(key));
				keywordsIndex.put(key, arr);
			}
			//System.out.println("key: "+key+"   frq: "+keywordsIndex.get(key));
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		//System.out.println(word);
		while(Character.isAlphabetic(word.charAt(word.length()-1)) == false){
			if(word.charAt(word.length()-1) == '.' || word.charAt(word.length()-1) == ',' || word.charAt(word.length()-1) == '?' || word.charAt(word.length()-1) == ':' || word.charAt(word.length()-1) == ';' || word.charAt(word.length()-1) == '!'){
				if(word.length() == 1){
					break;
				}else{
					word = word.substring(0, word.length()-1);
				}
			}
			else
				break;
		}
		for(int i=0;i<word.length();i++){
			if(Character.isAlphabetic(word.charAt(i)) == false){
				return null;
			}
		}
		//System.out.println(word.toLowerCase());
		if(noiseWords.containsValue(word))
			return null;
		else
			return word.toLowerCase();
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		if(occs.size() == 0)
			return null;
		//the occurrence obj we are trying to insert
		Occurrence ins = occs.get(occs.size()-1);
		//sequence of mid points
		ArrayList<Integer> intSeq = new ArrayList<Integer>();
		//binary search
		int left, right, mid;
		left = 0;
		right = occs.size()-2;
		mid = (left + right)/2;
		while(left < right){
			intSeq.add(mid);
			if(occs.get(mid).frequency >= ins.frequency){
				left = mid+1;
			}
			else{
				right = mid-1;
			}
			mid = (left + right)/2;
		}
		occs.add(mid, occs.get(occs.size()-1));
		occs.remove(occs.size()-1);
		//System.out.println(occs);
		return intSeq;
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<String> ret = new ArrayList<String>();
		ArrayList<Occurrence> kw1Occ = keywordsIndex.get(kw1);
		ArrayList<Occurrence> kw2Occ = keywordsIndex.get(kw2);
		if(getKeyWord(kw1) == null && getKeyWord(kw2) == null)
			return null;
		else if(getKeyWord(kw1) == null){
			for(int i=0;i<5 && i<kw2Occ.size();i++)
				ret.add(kw2Occ.get(i).document);
			return ret;
		}
		else if(getKeyWord(kw2) == null){
			for(int i=0;i<5 && i<kw1Occ.size();i++)
				ret.add(kw1Occ.get(i).document);
			return ret;
		}
		int count=0;
		int i1=0;
		int i2=0;
		Occurrence Occ1 = null;
		Occurrence Occ2 = null;
		for(;i1<kw1Occ.size() && count<5;i1++){
			if(ret.contains(kw1Occ.get(i1).document)){
				continue;
			}else{
				Occ1=kw1Occ.get(i1);
				for(;i2<kw1Occ.size();i2++){
					if(ret.contains(kw2Occ.get(i2).document)){
						continue;
					}else{
						Occ2=kw2Occ.get(i2);
						if(Occ1.frequency >= Occ2.frequency)
							ret.add(Occ1.document);
						else
							ret.add(Occ2.document);
						count++;
						break;
					}
				}
				
			}
		}
		return ret;
	}
	public static void main(String[] args){
		LittleSearchEngine lse = new LittleSearchEngine();
		try{
			lse.makeIndex("docs.txt","noisewords.txt");
		}catch(FileNotFoundException ex){
			System.out.println("One Of The Files Was Not Found.");
		}
		ArrayList<String> retrnd = lse.top5search("war", "alice");
		System.out.println("Docs:");
		try{
			for(String doc: retrnd)
				System.out.println(doc);
		}catch(NullPointerException ex){
			System.out.println("no docs containing these keywords.");
		}
		System.out.println("done");
	}
}
