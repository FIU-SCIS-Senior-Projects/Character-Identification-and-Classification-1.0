package srlExample;
import java.io.IOException;
import java.io.InputStream;

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

public class tester {

	
	static Parser parser = null;
	static SemanticRoleLabeler srl = null;

	
	public static void main(String[] args) throws InvalidFormatException, IOException {
		// TODO Auto-generated method stub
		

		
		srl = new FinlaysonSRLMod();
		
		String sentence = "A dragon appeared near Kiev; he took heavy tribute from the people - a lovely maiden from every house, whom he then devoured.";
		Parse[] tree = Parse(sentence);
		ISentenceInfo roles = finlayson_srl(tree);
		
		//System.out.println("Roles   " + roles);
		

		
		
		
	}
	
	/**
	 * //http://www.programcreek.com/2012/05/opennlp-tutorial/#parser
	 * Given a sentence, this returns an openNLP parse object,
	 * which is used by the SRL
	 * @param text - input sentence
	 * @return - openNLP parse object
	 * @throws InvalidFormatException
	 * @throws IOException
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
	 */
	public static ISentenceInfo finlayson_srl(Parse[] p)
	{
		//public SemanticRoleLabeler(IPredicateFinder predFinder, IPotentialArgFinder argFinder, IArgumentIdentifier argIdentifier, IArgumentLabeler argLabeler){
		
		ISentenceInfo roles = srl.labelRoles(p[0]);
		for(IPredicateInfo pi : roles.getPredicates()){
			System.out.println("predicate: " + pi.getPredicate().getCoveredText());
			for(IArgParsePair arg : pi.getArguments()){
				System.out.println(arg.getParse().getCoveredText() + " : " + arg.getArgument().getLabel());
			}
		}
		
		return roles;
	}
	
	
	/**
	 * Cleans up text from SRL computations
	 * @param text - text to clean
	 * @return - clean text
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
