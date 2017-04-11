package srlAnimacyUse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

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
 * Get the Start and End token numbers for each sentence and copy that output to a file
 * @author Geeticka Chauhan
 *
 */
public class ExtractSentence {
	
	static String basePath;
	public ExtractSentence(String bp)
	{
		this.basePath = bp;
	}
	/**
	 * Extract the start and end token of each sentence from stories 1-15
	 * @param args
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException
	 */
	public void Extract(String args[]) throws ParserConfigurationException, SAXException, IOException, TransformerException
	{
		/*
		String files[] = {basePath + "/input/Story1.xml",
				basePath + "/input/Story2.xml",
				basePath + "/input/Story3.xml",
				basePath + "/input/Story4.xml",
				basePath + "/input/Story5.xml",
				basePath + "/input/Story6.xml",
				basePath + "/input/Story7.xml",
				basePath + "/input/Story8.xml",
				basePath + "/input/Story9.xml",
				basePath + "/input/Story10.xml",
				basePath + "/input/Story11.xml",
				basePath + "/input/Story12.xml",
				basePath + "/input/Story13.xml",
				basePath + "/input/Story14.xml",
				basePath + "/input/Story15.xml"};
		*/
	    
		for(int i=1; i<=15; i++)
		{ // for every file
			   
		// set up optional output files
	    //PrintWriter out;
		//if (args.length > 1) {
		//out = new PrintWriter(args[1]);
		//} else {
		//out = new PrintWriter(System.out);
		//  }
		// good link to help creating an output XML https://www.mkyong.com/java/how-to-create-xml-file-in-java-dom/
		// manage the creation of an XML file for the output
		DocumentBuilderFactory outputDocFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder outputDocBuilder = outputDocFactory.newDocumentBuilder();
		// root elements
		Document xmlOut = outputDocBuilder.newDocument();
		Element rootElement = xmlOut.createElement("refexp");
		xmlOut.appendChild(rootElement); // its like a pointer that goes down the XML hierarchy, so you can now add children of sentences
		
		
		//PrintWriter xmlOut = new PrintWriter(new File(basePath + "/output/sentences/Story" + i + ".xml"));
		/*
		if (args.length > 2) {
		xmlOut = new PrintWriter(args[2]);
	    }
	    */
		// extract within the dep tags, all the dependent ones along with their IDs
		File inputFile = new File(basePath + "/input/story" + i + ".xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        System.out.println("File: " + i);
        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        // get all the rep tags
        NodeList parses = doc.getElementsByTagName("rep");
        
       Element neededParseElement = null;
      
        for (int temp = 0; temp < parses.getLength(); temp++)  
        { // for every sentence element
    		Node parse = parses.item(temp);
    		//System.out.println("\nCurrent Element :" + nNode.getNodeName());
    		Element parseElement = (Element) parse;
    			// check for whichever rep has the id corresponding to sentences
    		if(parseElement.getAttribute("id").equals("edu.mit.parsing.sentence"))
    		{
    			neededParseElement = parseElement;
    			System.out.println("Found needed parse element");
    			System.out.println(neededParseElement.getAttribute("id"));
    			break;
    		}
    		
        }
        
        NodeList sentences = neededParseElement.getElementsByTagName("desc");
        // for each sentence add the start and end token id number
        for(int j = 0; j < sentences.getLength(); j++)
        {
        	Node sentence = sentences.item(j);
    		Element sentenceElement = (Element) sentence;
    		// add the child of 
    		Element docSentence = xmlOut.createElement("sentence");
    		rootElement.appendChild(docSentence);
    		Attr attr = xmlOut.createAttribute("id");
    		attr.setValue("" + (j+1));
    		docSentence.setAttributeNode(attr);
    	
    		
    		String sentenceString = sentenceElement.getTextContent();
    		String delim = "~";
    		String[] tokens = sentenceString.split(delim);
    		
    		Element start = xmlOut.createElement("start");
    		start.appendChild(xmlOut.createTextNode("" + tokens[0]));
    		docSentence.appendChild(start);
    		
    		Element end = xmlOut.createElement("end");
    		end.appendChild(xmlOut.createTextNode("" + tokens[tokens.length - 1]));
    		docSentence.appendChild(end);
    		//xmlOut.println("<start>" + tokens[0] + "</start>");
    		//xmlOut.println("<end>" + tokens[tokens.length - 1] + "</end>");
    		//xmlOut.println("</sentence>");
        		
        	
        	
        }
        
     

 		System.out.println("File saved!");
        System.out.println("----------------------------");
        
        //out.close();
        //xmlOut.close();
        // IOUtils.closeIgnoringExceptions(out);
        //IOUtils.closeIgnoringExceptions(xmlOut);
        
        // write the content into xml file
  		TransformerFactory transformerFactory = TransformerFactory.newInstance();
  		Transformer transformer = transformerFactory.newTransformer();
  		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
  		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
  		DOMSource source = new DOMSource(xmlOut);
  		StreamResult result = new StreamResult(new File(basePath + "/output/sentences/story" + i + ".xml"));

  		// Output to console for testing
  		// StreamResult result = new StreamResult(System.out);

  		transformer.transform(source, result);
      }
	}
}

