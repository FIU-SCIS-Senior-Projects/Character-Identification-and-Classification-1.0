import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * Extract the sentences from 16-46 stories
 * @author Geeticka Chauhan
 *
 */
public class ExtractSentenceAfter15 
{
	String basePath;
	public ExtractSentenceAfter15(String basePath){
		this.basePath = basePath;
	}
	
	/**
	 * A method to extract the sentences for stories 15-46 (previous were generated using Story Workbench files)
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public void Extract(String args[]) throws FileNotFoundException
	{
		// Add in sentiment
	    Properties props = new Properties();
	    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");

	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    
	    
	    for(int i = 16; i <= 46; i++)
	    {
	    	PrintWriter out = new PrintWriter(basePath + "/outputs/SentencesForSRL/story" + i + ".txt");
	    	
	    	// read the txt files
	    	Scanner read = new Scanner(new File(basePath + "/src/Data/Text_Files/story" + i + ".txt")) ;
	    	String s="";
	    	while(read.hasNext())
	    	{
	    		s+= read.nextLine() + "\n";
	    	}
		   
	    	// Initialize an Annotation with some text to be annotated. The text is the argument to the constructor.
		    Annotation annotation;
		    if (args.length > 0) {
		      annotation = new Annotation(IOUtils.slurpFileNoExceptions(args[0]));
		    } else {
		      annotation = new Annotation(s);
		    }
	
	         // run all Annotators on this text
	         pipeline.annotate(annotation);
	 
	 
	                 // these are all the sentences in this document
			 // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
			 List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
			 sentences.stream().forEach(out::println);
			 out.close();
	    }
	}
}
