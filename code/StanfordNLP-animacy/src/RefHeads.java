import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * A class to get the heads of the referring expressions
 * @author Geeticka Chauhan
 *
 */
public class RefHeads 
{
	String basePath;
	public RefHeads(String basePath)
	{
		this.basePath = basePath;
	}
	
	/**
	 * Uses ParseData in order to generate the referring expressions with (sentence number ~ ID of referring expression) for each story from Story Workbench files
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void GenerateRefExp1() throws ParserConfigurationException, SAXException, IOException
	{
		for(int i=1; i<=15; i++) // may extend to 46 once Labiba gives chains for rest of the referring expressions
		{ // for every file
			 
			PrintWriter out = new PrintWriter(System.out);
		
			PrintWriter xmlOut = new PrintWriter(new File(basePath + "/outputs/RefExp/story" + i + ".txt"));
			/*
			if (args.length > 2) {
			xmlOut = new PrintWriter(args[2]);
		    }
		    */
			// extract within the dep tags, all the dependent ones along with their IDs
			File inputFile = new File(basePath + "/src/Data/XML_Files/story" + i + ".xml");
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(inputFile);
	        doc.getDocumentElement().normalize();
	        System.out.println("File: " + i);
	        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	        
	        ParseData p = new ParseData();
	        Hashtable<Integer, String> refList = p.parseRef(doc); // ref exp with ID of ref exp
	        Hashtable<String, String> refList2 = new Hashtable<>(); // ref exp with ID of first word in ref exp (~ID of ref exp) to get to the sentence num
	        Hashtable<String, String> refList3 = new Hashtable<>(); // ref exp (~ID of ref exp) with sentence number
	        
	        Set<Integer> keys = refList.keySet();
	        NodeList reps = doc.getElementsByTagName("rep");
	        for(int j = 0; j < reps.getLength(); j++)
	        {
	        	Element repElement = (Element) reps.item(j);
	        	if(repElement.getAttribute("id").equals("edu.mit.discourse.rep.refexp"))
	        	{
	        		NodeList descs = repElement.getElementsByTagName("desc"); 
			        for(Integer k: keys)
			        {
			        	//System.out.println("ID = " + k + " String = " + refList.get(k));
			        	for(int l = 0; l < descs.getLength(); l++)
			        	{
			        		Element descElement = (Element) descs.item(l);
			        		if(descElement.getAttribute("id").equals(k.toString()))
			        		{
			        			String wordList = descElement.getTextContent();
			        			String delim = "~" + "|,";
			            		String[] tokens = wordList.split(delim);
			            		Integer firstDigit = Integer.parseInt(tokens[0]);
			            		refList2.put(firstDigit + "~" + k, refList.get(k));
			        			break;
			        		}
			        		
			        	}
			        	// for each key go into the desc id, find out the first one of its content, ie parse 83 from 83~98
			        	// and then look which sentence it belongs to from that 83 number
			        }
			        
			        break;
	        	}
	        }
	        
	        // check why this is not working anymore
	        Set<String> keys2 = refList2.keySet();
	        for(int j = 0; j < reps.getLength(); j++)
	        {
	        	//System.out.println("Here");
	        	Element repElement = (Element) reps.item(j);
	        	//System.out.println(repElement.getAttribute("id"));
	        	if(repElement.getAttribute("id").equals("edu.mit.parsing.sentence"))
	        	{
	        		NodeList descs = repElement.getElementsByTagName("desc");
	        		for(String k: keys2)
	    	        {
	    	        	//System.out.println("ID = " + k + " String = " + refList2.get(k));
	        			for(int l = 0; l < descs.getLength(); l++)
			        	{ // l will iterate through all the sentences ie descs in this case
	        				
			        		Element descElement = (Element) descs.item(l);
			        		String wordList = descElement.getTextContent();
			        		String delim = "~";
		            		String[] tokens = wordList.split(delim);
		            		
		            		String numList = k;
		            		String[] t = numList.split(delim);
		            		if(Arrays.asList(tokens).contains(t[0])) // instead of looking at k which is ID of first word ~ ID of ref exp 
		            		{
		            			refList3.put((l+1) + "~" + t[1], refList2.get(k).trim()); // trim out the beginning white spaces and give it t[1] which is ID of ref exp
		            			break;
		            		}
			        		
			        	}
	        			
	    	        }
	        		
	        		break;
	        	}
	        }
	        
	        File stanfordFile = new File(basePath + "/outputs/Depen-Parse/story" + i + ".xml");
	        DocumentBuilderFactory sFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder sBuilder = sFactory.newDocumentBuilder();
	        Document sdoc = sBuilder.parse(stanfordFile);
	        sdoc.getDocumentElement().normalize();
	        System.out.println("File: " + i);
	        System.out.println("Root element :" + sdoc.getDocumentElement().getNodeName());
	        // must generate it like ref exp & sentence num ~ ID of ref exp 
	        Set<String> keys3 = refList3.keySet();
	        for(String aa: keys3)
	        {
	        	String delim = "~";
	        	String[] words = aa.split(delim);
	        	int stanfordSentenceNum = FindSentenceNum(refList3.get(aa), sdoc, Integer.parseInt(words[0])); // sentence number according to stanford
	        	xmlOut.println(refList3.get(aa) + "&" + aa + "&" + stanfordSentenceNum);
	        }
	        
	        xmlOut.close();    
		}
	}
	
	/**
	 * Find the sentence number from the Stanford formats of NLP Pipeline
	 * @param refExp The referring expression
	 * @param doc The document that you are finding the referring expression for - the stanford NLP pipeline's XML files
	 * @param init The sentence number according to story workbench
	 */
	public int FindSentenceNum(String refExp, Document doc, int init)
	{
		Scanner refWords = new Scanner(refExp);
	     String[] refs = new String[70]; // assuming there is not more than 70 words in a ref exp (may need to extend), this will store the words of ref exp
	     int i = 0; int size = 0;
	     
	     while(refWords.hasNext())
	     {
	    	 refs[i++] = refWords.next();
	    	 size++; 
	     }
		 NodeList sentences = doc.getElementsByTagName("sentences");
		 Element sentencesElement = (Element) sentences.item(0);
		 
		 NodeList sentence = sentencesElement.getElementsByTagName("sentence"); // within the sentences go to sentence tag
		 if(init - 1 < sentence.getLength()) // only if the init suggested by story workbench is less than sentence num
		 {
			 // init -1 because the init represents sentence ID but the actual sentence num in array is subtracted by 1
			 Element sentenceElement = (Element) sentence.item(init-1); // go to the sentence suggested by Story Workbench
		     
			 NodeList tokens = sentenceElement.getElementsByTagName("tokens");
		     Element tokensElement = (Element) tokens.item(0);
		     NodeList token = tokensElement.getElementsByTagName("token"); // now this is an actual list we will have to go through
		     
		     
		     for(int j = 0; j < token.getLength(); j++)
		     {
		    	 Element tokenElement = (Element) token.item(j); 
		    	 String firstWord = tokenElement.getElementsByTagName("word").item(0).getTextContent();
		    	 
		    	 if(firstWord.equals(refs[0])) // j will be at first word of ref exp
		    	 {
		    		 int k = j + 1; // k will start at the next token after j
		    		 int f = 1; // marker for the token of referring expression
		    		 Boolean exists = true;
		    		 while(f < size && k < token.getLength()) // only keep going as long as you have a next word to look at from ref exp
		    		 {
		    			 Element nexttoken = (Element)token.item(k);
		    			 String word = nexttoken.getElementsByTagName("word").item(0).getTextContent();
		    			 
		    			 if(!word.equals(refs[f++]))
		    			 {
		    				 exists = false;
		    				 break;
		    			 }
		    			 k++;
		    		 }
		    		 if(exists == true) // if the ref exp exists within the sentence
		    		 {
		    			 return init;
		    		 }	 
		    	 }
		     }
			 
		}
	     
	     // above we checked if the ref exp existed in the sentence mentioned by story workbench
	     // now we are going to search starting 5 sentences behind Story Workbench one to see if we can find it
	     
	     int sentenceID = init - 5;
	     Boolean exists = true;
	     int j = 0; 
	     if(init < 6)
	     {
	    	 j = 0;
	    	 sentenceID = 1;
	     }
	     else 
	     {
	    	 j = init - 6;
	    	 sentenceID = j + 1; 
	     }
		 for(; j < sentence.getLength(); j++)
		 {
			 // below will not work - fix it
			 Element sElement = (Element) sentence.item(j); // go to the sentence suggested by Story Workbench
			 sentenceID = Integer.parseInt(sElement.getAttribute("id"));
			 NodeList tokenss = sElement.getElementsByTagName("tokens");
		     Element tokenssElement = (Element) tokenss.item(0);
		     NodeList sToken = tokenssElement.getElementsByTagName("token"); // now this is an actual list we will have to go through
		     
		     // go through the tokens
		     for(int l = 0; l < sToken.getLength(); l++)
		     {
		    	 Element tokenElement = (Element) sToken.item(l); 
		    	 String firstWord = tokenElement.getElementsByTagName("word").item(0).getTextContent();
		    	 
		    	 if(firstWord.equals(refs[0])) // l will be at first word of ref exp
		    	 {
		    		 int k = l + 1; // k will start at the next token after l
		    		 int f = 1; // marker for the token of referring expression
		    		 exists = true;
		    		 while(f< size && k < sToken.getLength())
		    		 {
		    			 Element nexttoken = (Element) sToken.item(k);
		    			 String word = nexttoken.getElementsByTagName("word").item(0).getTextContent();
		    			 if(!word.equals(refs[f++]))
		    			 {
		    				 exists = false;
		    				 break;
		    			 }
		    			 k++;
		    		 }
		    		 if(exists == true) // if the ref exp exists within the sentence
		    		 {
		    			 return sentenceID;
		    		 }	 
		    	 }
		     
		     }
		 }
		     return init;   // return init when you fix above block of code
	}
	
	/**
	 * Parse out the referring expressions first to get RefExp by sentence and story and then use this to read through those files
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws TransformerException 
	 */
	public void Run() throws SAXException, IOException, ParserConfigurationException, TransformerException
	{
		/*
		File inputFile = new File(basePath + "/outputs/Depen-Parse/story" + "2" + ".xml"); // get the actual dependency parse
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        System.out.println("File: " + "2");
        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
   
		System.out.println(Extract("this trouble", doc, "38"));
		*/
		
		// to get the referring expressions
		for(int i=1; i<=15; i++) // can extend to 46 after this 
		{
			// good link to help creating an output XML https://www.mkyong.com/java/how-to-create-xml-file-in-java-dom/
			// manage the creation of an XML file for the output
			DocumentBuilderFactory outputDocFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder outputDocBuilder = outputDocFactory.newDocumentBuilder();
			// root elements
			Document xmlOut = outputDocBuilder.newDocument();
			Element rootElement = xmlOut.createElement("refexp");
			xmlOut.appendChild(rootElement); // child of root is sentences
			
			
			Scanner refExp = new Scanner(new File(basePath + "/outputs/RefExp/story" + i + ".txt")) ; // get referring expressions
			
			// to get the xml file generated by Stanford 
			File inputFile = new File(basePath + "/outputs/Depen-Parse/story" + i + ".xml"); // get the actual dependency parse
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(inputFile);
	        doc.getDocumentElement().normalize();
	        System.out.println("File: " + i);
	        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	   
	        //System.out.println(Extract("the fate of the tsar 's daughter to go to the dragon", doc, "2"));
	      //  System.out.println(Extract("the city of kiev", doc, "11"));  // the lower case thing might be a problem
	        // remember that when you do a remove action inside the extract function, dependent is being converted to lower case
		
	        while(refExp.hasNext())
	        {
	        	String line = refExp.nextLine();
        		String delim1 = "&";
        		String[] tokens = line.split(delim1); // first thing will be ref exp, other thing will be the data
        		String refexp = tokens[0]; // referring expression
        		String stanfordSentenceID = tokens[2];
        		
        		String delim2 = "~";
        		String tokens2[] = tokens[1].split(delim2); // we are now splitting the two numbers\
        		String sentenceID = tokens2[0]; // ID of the sentence according to Story Workbench
        		String refexpID = tokens2[1]; // ID of the referring expression
        		String refHead = Extract(refexp, doc, stanfordSentenceID); // head of the referring expression
        		
        		Element ref = xmlOut.createElement("ref");
        		rootElement.appendChild(ref);
        		Attr attr = xmlOut.createAttribute("id");
        		attr.setValue("" + refexpID);
        		ref.setAttributeNode(attr);
        		
        		Element value = xmlOut.createElement("value");
        		value.appendChild(xmlOut.createTextNode(refexp));
        		ref.appendChild(value);
        		
        		Element head = xmlOut.createElement("head");
        		head.appendChild(xmlOut.createTextNode(refHead.trim()));
        		ref.appendChild(head);
        		
        		Element sentence = xmlOut.createElement("sentence");
        		sentence.appendChild(xmlOut.createTextNode(sentenceID));
        		ref.appendChild(sentence);
        		
        		Element stanfordSentence = xmlOut.createElement("stanfordSentence");
        		stanfordSentence.appendChild(xmlOut.createTextNode(stanfordSentenceID));
        		ref.appendChild(stanfordSentence);
        		
    		}
	        
	     // write the content into xml file
	  		TransformerFactory transformerFactory = TransformerFactory.newInstance();
	  		Transformer transformer = transformerFactory.newTransformer();
	  		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	  		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	  		DOMSource source = new DOMSource(xmlOut);
	  		StreamResult result = new StreamResult(new File(basePath + "/outputs/RefHeads/story" + i + ".xml"));

	  		// Output to console for testing
	  		// StreamResult result = new StreamResult(System.out);

	  		transformer.transform(source, result);
		}
	
	}
	
	/**
	 * Inputs the referring expression to output the head
	 * @param refExp The String of the referring expression
	 * @param doc The doc of xml file generated by stanford to look inside for the referring expression
	 * @param sentenceID the id of the sentence to which the referring expression belongs
	 */
	public String Extract(String refExp, Document doc, String sentenceID)
	{
		String head = "";
		NodeList sentencesTop = doc.getElementsByTagName("sentences"); // get the list of sentence tags
		Element sentencesElement = (Element) sentencesTop.item(0);
		NodeList sentences = sentencesElement.getElementsByTagName("sentence");
		for(int j = 0; j < sentences.getLength(); j++)
		{
			Node sentenceNode = sentences.item(j);
			if(sentenceNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element sentenceElement = (Element) sentenceNode;
				if(sentenceElement.getAttribute("id").equals(sentenceID)) // get to the sentence with the ID
				{
					NodeList dependencies = sentenceElement.getElementsByTagName("dependencies"); // get the dependencies tag 
					//System.out.println("dependencies " + dependencies);
					for(int i = 0; i < dependencies.getLength(); i++)
					{
						Node dependencyNode = dependencies.item(i);
						if(dependencyNode.getNodeType() == Node.ELEMENT_NODE)
						{
							Element dependencyElement = (Element) dependencyNode;
							if(dependencyElement.getAttribute("type").equals("basic-dependencies")) // get to basic dependencies
							{
								NodeList deps = dependencyElement.getElementsByTagName("dep"); // get the individual dependency dep tag
								
								List<Element> nDeps = new ArrayList<>(); // these are going to be the dependency elements matching all words from RE
								
								// iterate through each dep to find out which ones have both governor and dependent in the needed list
								for(int k = 0; k < deps.getLength(); k++)
								{
									Node depNode = deps.item(k);
								//	System.out.println("depNode " + depNode);
									if(depNode.getNodeType() == Node.ELEMENT_NODE)
									{
										Element depElement = (Element) depNode;
										NodeList governor = depElement.getElementsByTagName("governor");
										NodeList dependent = depElement.getElementsByTagName("dependent");
										String gov = governor.item(0).getTextContent();
										String dep = dependent.item(0).getTextContent();
										
										// go through word by word of referring expression
										Boolean govMatches = false, depMatches = false;
										Scanner wordScan = new Scanner(refExp);
										while(wordScan.hasNext())
										{
											
											String nextWord = wordScan.next();
											if(nextWord.equals(gov)) // was ignore case
											{
												govMatches = true;
											}
											if(nextWord.equals(dep)) // was ignore case
											{
												depMatches = true;
											}
											if(govMatches == true && depMatches == true)
											{
												//System.out.println("gov dep " + gov + " " + dep);
												nDeps.add(depElement); // governor goes first and then goes dependent
												break;
											}
										}
									}
								}
								// cut out every dependent from the list of words inside RE and whoever is left is the governor of the whole thing ie the head 
								ArrayList<String> wordsInRef = new ArrayList<>(); // to get the seperate tokens of RE
								Scanner wordScan = new Scanner(refExp);
								while(wordScan.hasNext())
								{
									wordsInRef.add(wordScan.next());
								}
								for(int k = 0; k < nDeps.size(); k++)
								{
									Element depElement = nDeps.get(k);
									String dependent = depElement.getElementsByTagName("dependent").item(0).getTextContent();
									wordsInRef.remove(dependent); // was to lower case
								}
								
								
								for(String e: wordsInRef)
								{
									head += e + " ";
								}
								break;
							}
						}
					}
					
					break;
				}
			}
		}
		
		return head;
	}
}
