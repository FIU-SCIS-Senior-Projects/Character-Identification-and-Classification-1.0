
import java.io.*;
import java.util.*;

import javax.print.DocFlavor.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.stanford.nlp.coref.CorefCoreAnnotations;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;
import junit.framework.Test;

/**
 *  This class builds the Stanford CoreNLP pipeline and extracts subjects from dependency parse
 * @author Geeticka Chauhan
 *
 */
public class Main {

  static String projectPath = System.getProperty("user.dir");
  static File projectDir = new File(projectPath);
  /** Usage: java -cp "*" StanfordCoreNlpDemo [inputFile [outputTextFile [outputXmlFile]]] 
 * @throws SAXException 
 * @throws ParserConfigurationException 
 * @throws TransformerException */
  public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, TransformerException {
	  // Step 1 Convert XML files to extract TXT content
	  //XMLToTXT(projectDir.toString());
	  
	  //Step 2 Run the Stanford Pipeline
	// DependencyParse d = new DependencyParse(projectDir.toString());
	 //d.GenerateParse(args);
	//Get the Coreference Chains for Labiba
	 //d.GenerateCoref(args);
	  
	  // Step 3 Extract the subjects in dependency parse with token numbers according to sentence
	  //ExtractSubjectBySentence dd = new ExtractSubjectBySentence(projectDir.toString());
	  //dd.Extract(args);
	  
	  // Step 4 Extract the subjects in the dependency parse without token numbers
	  //ExtractSubjectByWord ddd = new ExtractSubjectByWord(projectDir.toString());
	  //ddd.Extract(args);
	  
	  // Step 5 Extract modifier
	 // ExtractModifier ee = new ExtractModifier(projectDir.toString());
	//  ee.Extract(args);
	  
	  // Wordnet related stuff ie find out the animacy etc
	  WordnetWork.Output();
	  
	  //Extract sentences for stories 15-46 for SRL head purpose
	  //ExtractSentenceAfter15 gg = new ExtractSentenceAfter15(projectDir.toString());
	  //gg.Extract(args);
	  
	  // Get the heads of the referring expressions
	 // RefHeads r = new RefHeads(projectDir.toString());
	 // r.GenerateRefExp1();  // may need to extend to the 46 stories
	 // r.Run(); // may need to extend to the 46 stories
	  
	  
	  
  }
  
  public static void XMLToTXT(String basePath) throws ParserConfigurationException, SAXException, IOException
  {
	for(int i=1; i<=46; i++)
	{ // for every file
		PrintWriter xmlOut = new PrintWriter(new File(basePath + "/src/Data/Text_Files/story" + i + ".txt"));
		/*
		if (args.length > 2) {
		xmlOut = new PrintWriter(args[2]);
		}
		*/
		// extract within the dep tags, all the dependent ones along with their IDs
		File inputFile = new File(basePath + "/src/Data/XML_Files/story" + i + ".xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setIgnoringComments(true);  // to ignore comments
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();
		System.out.println("File: " + i);
		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	    
		NodeList rep = doc.getElementsByTagName("rep");
		for(int j = 0; j < rep.getLength(); j++)
		{
			Node repNode = rep.item(j);
			if(repNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element repElement = (Element) repNode;
				if(repElement.getAttribute("id").equals("edu.mit.story.char"))
				{
					NodeList description = repElement.getElementsByTagName("desc");
					String word = description.item(0).getTextContent();
					// parse out the comments section source http://stackoverflow.com/questions/35735741/how-can-i-ignore-comments-statements-when-i-reading-java-file
					int index =  word.indexOf("/**");

					while(index != -1) {
					    word = word.substring(0, index) + word.substring(word.indexOf("*/")+2);
					    index =  word.indexOf("/*");
					}
					xmlOut.print(word);
					break;
					
				}
			}
		}
		xmlOut.close();
	}
  }

}
