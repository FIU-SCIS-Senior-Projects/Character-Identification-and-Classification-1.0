package srlAnimacyUse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.eventmonitor.srl.iterators.IPredicateInfo;
import com.eventmonitor.srl.iterators.ISentenceInfo;
import com.eventmonitor.srl.labeler.IArgParsePair;
import com.eventmonitor.srl.labeler.SemanticRoleLabeler;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.util.InvalidFormatException;

/**
 * 
 * @author Geeticka Chauhan
 *
 */
public class tester {

	
	static Parser parser = null;
	static SemanticRoleLabeler srl = null;
	static String projectPath = System.getProperty("user.dir");
    static File projectDir = new File(projectPath);
    
	
	public static void main(String[] args) throws InvalidFormatException, IOException, ParserConfigurationException, SAXException, TransformerException {
		// TODO Auto-generated method stub
		// first we must extract the starting and ending tokens of the sentence
		// Step 1
		//ExtractSentence e = new ExtractSentence(projectDir.toString());
		//e.Extract(args);
		
		 //Step 2
		//ExtractSentenceWords ee = new ExtractSentenceWords(projectDir.toString());
		//ee.Extract(args);

		
		srl = new FinlaysonSRLMod();
		
		
	//	String sentence = "\" We have divided the earth , \" said Nikita , \" now let us divide the sea ; else you will say that your water has been taken . \"";
	//	Parse[] tree = Parse(sentence);
		//ISentenceInfo roles = finlayson_srl(tree);
		
		//System.out.println("Roles   " + roles);
		
		// Step 3
		SRLrun();
		
	}
	
	public static void SRLrun() throws InvalidFormatException, IOException
	{
		// from here you print out the semantic subjects in the sentences 
				// duplicates exist - wasn't a big deal this time
		/*
				String files[] = {projectDir.toString() + "/output/sentenceWords/Story1.txt",
						projectDir.toString() + "/output/sentenceWords/Story2.txt",
						projectDir.toString() + "/output/sentenceWords/Story3.txt",
						projectDir.toString() + "/output/sentenceWords/Story4.txt",
						projectDir.toString() + "/output/sentenceWords/Story5.txt",
						projectDir.toString() + "/output/sentenceWords/Story6.txt",
						projectDir.toString() + "/output/sentenceWords/Story7.txt",
						projectDir.toString() + "/output/sentenceWords/Story8.txt",
						projectDir.toString() + "/output/sentenceWords/Story9.txt",
						projectDir.toString() + "/output/sentenceWords/Story10.txt",
						projectDir.toString() + "/output/sentenceWords/Story11.txt",
						projectDir.toString() + "/output/sentenceWords/Story12.txt",
						projectDir.toString() + "/output/sentenceWords/Story13.txt",
						projectDir.toString() + "/output/sentenceWords/Story14.txt",
						projectDir.toString() + "/output/sentenceWords/Story15.txt"};
				
			    */
				for(int i=1; i<=46; i++)
				{ // for every file   
					PrintWriter xmlOut = new PrintWriter(new File(projectDir.toString() + "/output/semanticSubjects/story" + i + ".txt"));
					
					Scanner readFile = new Scanner(new File(projectDir.toString() + "/output/sentenceWords/story" + i + ".txt"));
					while(readFile.hasNext())
					{
						String sentence = readFile.nextLine();
						System.out.println(sentence);
						Parse[] tree = Parse(sentence);
						ISentenceInfo roles = finlayson_srl(tree, xmlOut);
						//System.out.println("Roles   " + roles);
						
					}
					xmlOut.close();	
				}
	}
	
	/**
	 * //http://www.programcreek.com/2012/05/opennlp-tutorial/#parser
	 * Given a sentence, this returns an openNLP parse object,
	 * which is used by the SRL
	 * @param text - input sentence
	 * @return - openNLP parse object
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @author Mark Finlayson
	 */
	public static Parse[] Parse(String text) throws InvalidFormatException, IOException 
	{
		
		setupParser();
	 
		Parse topParses[] = ParserTool.parseLine(text, parser, 1);
	 
//		for (Parse p : topParses)
//			p.show();
	 
		return topParses;
	}
	
	/**
	 * Helper Method for Parse()
	 * This allows the models / parser to be loaded the 
	 * first time Parse() is called. 
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @author Mark Finlayson
	 */
	private static void setupParser() throws InvalidFormatException, IOException 
	{
		if (parser == null) 
		{
			// http://sourceforge.net/apps/mediawiki/opennlp/index.php?title=Parser#Training_Tool
			InputStream is = InputStream.class.getResourceAsStream("/en-parser-chunking.bin");
			ParserModel model = new ParserModel(is);
			parser = ParserFactory.create(model);
			is.close();
		}
	}
	
	
	
	/**
	 * Given a parse, returns semantic role labels
	 * @param p -openNLP parse object
	 * @return -semantic role labes
	 * @author Mark Finlayson
	 */
	public static ISentenceInfo finlayson_srl(Parse[] p, PrintWriter out)
	{
		//public SemanticRoleLabeler(IPredicateFinder predFinder, IPotentialArgFinder argFinder, IArgumentIdentifier argIdentifier, IArgumentLabeler argLabeler){
		
		ISentenceInfo roles = srl.labelRoles(p[0]);
		for(IPredicateInfo pi : roles.getPredicates()){
			//System.out.println("predicate: " + pi.getPredicate().getCoveredText());
			for(IArgParsePair arg : pi.getArguments()){
				if(arg.getArgument().getLabel().equals("ARG0"))
				{
					out.println(arg.getParse().getCoveredText());
				}
			}
		}
		
		return roles;
	}
	
	
	/**
	 * Cleans up text from SRL computations
	 * @param text - text to clean
	 * @return - clean text
	 * @author Mark Finlayson
	 */
	public static String filter(String text)
	{
//		System.out.println(text);
		text = text.replaceAll(",", "");
		text = text.replace("[", "");
//		System.out.println(text);

		return text;
	}

}
