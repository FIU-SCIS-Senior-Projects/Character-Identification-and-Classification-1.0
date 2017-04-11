import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.stanford.nlp.io.IOUtils;

/**
 * 
 * @author Geeticka Chauhan
 *
 */
public class ExtractSubjectByWord 
{
	static String basePath;
	public ExtractSubjectByWord(String basePath)
	{
		this.basePath = basePath;
	}
	public static void Extract(String args[]) throws IOException, ParserConfigurationException, SAXException
	{
		String files[] = {basePath + "/outputs/Depen-Parse/story1.xml",
				basePath + "/outputs/Depen-Parse/story2.xml",
				basePath + "/outputs/Depen-Parse/story3.xml",
				basePath + "/outputs/Depen-Parse/story4.xml",
				basePath + "/outputs/Depen-Parse/story5.xml",
				basePath + "/outputs/Depen-Parse/story6.xml",
				basePath + "/outputs/Depen-Parse/story7.xml",
				basePath + "/outputs/Depen-Parse/story8.xml",
				basePath + "/outputs/Depen-Parse/story9.xml",
				basePath + "/outputs/Depen-Parse/story10.xml",
				basePath + "/outputs/Depen-Parse/story11.xml",
				basePath + "/outputs/Depen-Parse/story12.xml",
				basePath + "/outputs/Depen-Parse/story13.xml",
				basePath + "/outputs/Depen-Parse/story14.xml",
				basePath + "/outputs/Depen-Parse/story15.xml"};
		// figure out why it doesnt show up beyond File1
	    Map<String, String> hashmap = new LinkedHashMap<String, String>(); // to store the word as key
	    // in order to remove duplicates from the text file
	    
		for(int i=1; i<=15; i++)
		{ // for every file
			   
			// set up optional output files
		    PrintWriter out;
			if (args.length > 1) {
			out = new PrintWriter(args[1]);
			} else {
			out = new PrintWriter(System.out);
			  }
			PrintWriter xmlOut = new PrintWriter(new File(basePath + "/outputs/SubjectsByWord/story" + i + ".txt"));
			/*
			if (args.length > 2) {
			xmlOut = new PrintWriter(args[2]);
		    }
		    */
			// extract within the dep tags, all the dependent ones along with their IDs
			File inputFile = new File(basePath + "/outputs/Depen-Parse/story"+ i + ".xml");
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(inputFile);
	        doc.getDocumentElement().normalize();
	        System.out.println("File: " + i);
	        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
       
			NodeList dependencies = doc.getElementsByTagName("dep"); // find the dependencies within the sentence
			
			for(int j = 0; j < dependencies.getLength(); j++)
			{
				Node dependencyNode = dependencies.item(j);
				if(dependencyNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element dependencyElement = (Element) dependencyNode;
					if(dependencyElement.getAttribute("type").equals("nsubj"))
					{
						NodeList dependent = dependencyElement.getElementsByTagName("dependent");
						String word = dependent.item(0).getTextContent();
	    			//	String index = dependent.item(0).getAttributes().getNamedItem("idx").getNodeValue().toString();
	    				hashmap.put(word, "1");
	    				//xmlOut.println(word + "_" + index);
	    				//System.out.println(word + "_" + index);
					}
				}
				
			}
			Iterator it = hashmap.entrySet().iterator();
	        while(it.hasNext())
	        {
	        	Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
	        	xmlOut.println(pair.getKey());
	        	it.remove();
	        	hashmap.remove(pair.getKey());
	        }
		
			// https://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/ useful resource to understand parsing an xml file
	        System.out.println("----------------------------");
	        IOUtils.closeIgnoringExceptions(out);
		    IOUtils.closeIgnoringExceptions(xmlOut);	
		}   
    }
}

