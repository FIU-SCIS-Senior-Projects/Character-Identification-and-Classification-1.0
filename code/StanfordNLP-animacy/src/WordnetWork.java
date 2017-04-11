import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IPointer;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.sussex.nlp.jws.AdaptedLesk;
import edu.sussex.nlp.jws.JWS;
import edu.sussex.nlp.jws.JiangAndConrath;
import edu.sussex.nlp.jws.Lin;

/**
 * 
 * @author Geeticka Chauhan
 *
 */
public class WordnetWork 
{
	static String projectPath = System.getProperty("user.dir");
    static File projectDir = new File(projectPath);
    static File wordnetProj = new File(projectDir, "lib/WordNet"); // had to remove projectDir.getParent()
    static File dictDir = new File(wordnetProj, "/3.0/dict");
	public static void Output() throws IOException
	{
		//https://github.com/evanmiltenburg/gwc2016-adjective-similarity
		// get lemma, get derivationally related form and then get noun synsets and then measure the distance between them	
		// construct the URL to the Wordnet dictionary directory
		String wnhome = System.getenv("WNHOME");
		String path = wnhome + File.separator + "dict"; 
		  
		//System.out.println(wordnetProj.toString());
		URL url = new URL("file", null, dictDir.toString()); // from java.net
		
		// construct the dictionary object and open it
		IDictionary dict = new Dictionary(url); 
		dict.open();
		
		getModifierDistance("alive", dict);
		getAnimacy(dict, "table");
		
	}
	
	//gotten from tutorial
	public static void getAnimacy(IDictionary dict, String text)
	{
		// get the synset
		 IIndexWord idxWord = dict.getIndexWord (text, POS.NOUN );
		 System.out.println("Word to detect animacy for: " + text);
		 if(idxWord == null)
		 {
			 System.out.println("Sorry, this word does not exist in WordNet");
			 return;
		 }
		 IWordID wordID = idxWord.getWordIDs().get(0) ; // 1st meaning
		 IWord word = dict.getWord (wordID);
		 ISynset synset = word.getSynset();
		
		 // get the hypernyms
		 ISynsetID hypernymID =
		 synset.getRelatedSynsets(Pointer.HYPERNYM ).get(0);
		
		 // print out each h y p e r n y m s id and synonyms
		IWord word1 = dict.getSynset(hypernymID).getWords().get(0);
		//System.out.println(word1.getLemma());
		Boolean animate = false, entity = false;
		while(word1.getSynset() != null)
		{
			
			synset = word1.getSynset();
			hypernymID = synset.getRelatedSynsets(Pointer.HYPERNYM).get(0);
			word1 = dict.getSynset(hypernymID).getWords().get(0);
			//System.out.println(word1.getLemma());
			if(word1.getLemma().equals("living_thing"))
			{
				animate = true;
				break;
			}
			else if(word1.getLemma().equals("entity"))
			{
				entity = true;
				break;
			}
			
		}
		
		if(animate == true)
		{
			System.out.println("This is an animate being");
		}
		else if(entity == true)
		{
			System.out.println("This is an inanimate being");
		}
		 
		 
		 /*
		 for(ISynsetID sid : hypernyms)
		 {
			 IWord word1 = dict.getSynset(sid).getWords().get(0);
			 System.out.println(word1);
			 ISynset synset1 = word1.getSynset();
			 ISynsetID id = synset.getRelatedSynsets(Pointer.HYPERNYM).get(0);
			 IWord word2 = dict.getSynset(id).getWords().get(0);
			 System.out.println(word2);
			 /*
			 words = dict.getSynset (sid).getWords();
			 System.out.print (sid + " {");
			 for(Iterator<IWord> i = words.iterator(); i.hasNext();){
				 System.out.print (i.next().getLemma());
				 if(i.hasNext())
					 System.out.print (", ");
			 }
			 System.out.println ("}");
			 */
		 //}
	
	}
	/**
	 * A method to take care of all the modifier distance work
	 * @param modifier
	 * @param dict
	 */
	public static void getModifierDistance(String modifier, IDictionary dict)
	{
		// look up first sense of the word "dog"
				IIndexWord idxWord = dict.getIndexWord(modifier, POS.ADJECTIVE); 
				IWordID wordID = idxWord.getWordIDs().get(0); // gets the most common sense of the word
				IWord word = dict.getWord(wordID);
				System.out.println("Id = " + wordID); 
				System.out.println("Lemma = " + word.getLemma()); 
				System.out.println("Gloss = " + word.getSynset().getGloss());
				// below gives the related synsets
				
				/*
				// trying to get derivationally related forms
				String lString = word.getLemma(); // lemma String
				IIndexWord lIdxWord = dict.getIndexWord(lString, POS.ADJECTIVE);
				IWordID lWordID = lIdxWord.getWordIDs().get(0);
				IWord lWord = dict.getWord(lWordID);
				
				Set<IPointer> Synsets = lIdxWord.getPointers();
				//System.out.println("Synsets" + Synsets);
				IPointer derRelForm = null;
				for(IPointer a: Synsets)
				{
					if(a.getName().equals("Derivationally related form"))
					{
						derRelForm = a;
					}
					//System.out.println(a.getName());
					
				}
				
				if(derRelForm != null)
				{
					List<IWordID> relatedForms = lWord.getRelatedWords(derRelForm);
					for(IWordID a: relatedForms)
					{
						IWord tempWord = dict.getWord(a);
						System.out.println(tempWord.getLemma());
					}
				}
				*/
				
				ISynset Synset = word.getSynset();
				List<ISynsetID> List = Synset.getRelatedSynsets();
				for(ISynsetID a: List)
				{
					// must check if its adjectives, cause we only want the kinds that are adjectives
					if(dict.getSynset(a).isAdjectiveSatellite() || dict.getSynset(a).isAdjectiveHead())
					{
					// get Synset type from ISenseKey and check if its an adjective
					for(IWord b : dict.getSynset(a).getWords())
					{
						
						//b.getSenseKey().getHeadWord(); returns the head word in case this is an adjective cluster
						// below does the same thing that checking for adjective satellite or head does
						//if(b.getSenseKey().getSynsetType() == 3 || b.getSenseKey().getSynsetType() == 5)
						//{
						// how to get the head word to tread through wordnet
						// getHeadWord will return null if b is already the head word
						System.out.println(b.getSenseKey().getHeadWord() + " " +  b.getLemma());
						
						// trying to get derivationally related forms
						String lString = b.getLemma(); // lemma String
						IIndexWord lIdxWord = dict.getIndexWord(lString, POS.ADJECTIVE);
						IWordID lWordID = lIdxWord.getWordIDs().get(0);
						IWord lWord = dict.getWord(lWordID);
						
						Set<IPointer> Synsets = lIdxWord.getPointers();
						//System.out.println("Synsets" + Synsets);
						IPointer derRelForm = null;
						for(IPointer c: Synsets)
						{
							if(c.getName().equals("Derivationally related form"))
							{
								derRelForm = c;
								break;
							}
							//System.out.println(a.getName());
							
						}
						
						if(derRelForm != null)
						{
							List<IWordID> relatedForms = lWord.getRelatedWords(derRelForm); // from the IPointer, get the actual word
							for(IWordID c: relatedForms)
							{
								IWord tempWord = dict.getWord(c);
								System.out.println(tempWord.getLemma());
								CalculateDistance(tempWord.getLemma(), "aliveness");
								break;
							}
						}
						
						//}
					}
					}
				}
				
				
				//CalculateDistance();
	}
	public static void CalculateDistance(String a, String b)
	{
		
		// aliveness and vitality reveal similarity - works for nouns
		// but if you give it adjectives such as alive or living and dead, we get a null pointer exception
		// 1. SET UP:
			String dir = wordnetProj.toString();	
		JWS	ws = new JWS(dir, "3.0");
			
	// 2. EXAMPLES OF USE:

		/*
			AdaptedLesk ad = ws.getAdaptedLesk();
			System.out.println("Adapted Lesk\n");
			System.out.println("\nhighest score\t=\t" + ad.max("aliveness", "vitality", "n") + "\n\n\n");
			*/
	// 2.1 [JIANG & CONRATH MEASURE]
			JiangAndConrath jcn = ws.getJiangAndConrath();
			System.out.println("Jiang & Conrath\n");
	// all senses
		//	TreeMap<String, Double> 	scores1	=	jcn.jcn("living", "dead", "a");			// all senses
			//TreeMap<String, Double> 	scores1	=	jcn.jcn("apple", 1, "banana", "n"); 	// fixed;all
			//TreeMap<String, Double> 	scores1	=	jcn.jcn("apple", "banana", 2, "n"); 	// all;fixed
			//for(String s : scores1.keySet())
			//	System.out.println(s + "\t" + scores1.get(s));
	// specific senses
		//	System.out.println("\nspecific pair\t=\t" + jcn.jcn("living", 1, "dead", 1, "a") + "\n");
	// max.
			System.out.println("\nhighest score\t=\t" + jcn.max(a, b, "n") + "\n\n\n");


	// 2.2 [LIN MEASURE]
			Lin lin = ws.getLin();
			System.out.println("Lin\n");
	// all senses
		//	TreeMap<String, Double> 	scores2	=	lin.lin("living", "dead", "a");			// all senses
			//TreeMap<String, Double> 	scores2	=	lin.lin("apple", 1, "banana", "n"); 	// fixed;all
			//TreeMap<String, Double> 	scores2	=	lin.lin("apple", "banana", 2, "n"); 	// all;fixed
		//	for(String s : scores2.keySet())
		//		System.out.println(s + "\t" + scores2.get(s));
	// specific senses
		//	System.out.println("\nspecific pair\t=\t" + lin.lin("living", 1, "dead", 1, "a") + "\n");
	// max.
			System.out.println("\nhighest score\t=\t" + lin.max(a, b, "n") + "\n\n\n");

	// ... and so on for any other measure
	}
	
}


