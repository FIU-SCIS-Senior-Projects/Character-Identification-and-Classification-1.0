import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.tokensregex.types.Value;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.TypesafeMap.Key;

/**
 * Dependency Parse
 * @author Geeticka Chauhan
 *
 */
public class DependencyParse 
{
	static String basePath;
	public DependencyParse(String basePath)
	{
		this.basePath = basePath;
	}
	public void GenerateParse(String args[]) throws IOException{
		// Create a CoreNLP pipeline. To build the default pipeline, you can just use:
	    //   StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    // Here's a more complex setup example:
	    //   Properties props = new Properties();
	    //   props.put("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
	    //   props.put("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
	    //   props.put("ner.applyNumericClassifiers", "false");
	    //   StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

	    // Add in sentiment
	    Properties props = new Properties();
	    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");

	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	// make sure you add the Data folder to your classpath and the Data folder is in the build path project folder
	  //  java.net.URL url = Test.class.getResource("/Data/Text_Files/story1.txt"); //getClassLoader().
	  //  System.out.println(url); //url.getPath()
	    
	    //specify where the input text files are - these files are .txt and only have the text of the stories
	    /*
	    String files[] = {basePath + "/src/Data/Text_Files/story1.txt",
	    		basePath + "/src/Data/Text_Files/story2.txt",
	    		basePath + "/src/Data/Text_Files/story3.txt",
	    		basePath + "/src/Data/Text_Files/story4.txt",
	    		basePath + "/src/Data/Text_Files/story5.txt",
	    		basePath + "/src/Data/Text_Files/story6.txt",
	    		basePath + "/src/Data/Text_Files/story7.txt",
	    		basePath + "/src/Data/Text_Files/story8.txt",
	    		basePath + "/src/Data/Text_Files/story9.txt",
	    		basePath + "/src/Data/Text_Files/story10.txt",
	    		basePath + "/src/Data/Text_Files/story11.txt",
	    		basePath + "/src/Data/Text_Files/story12.txt",
	    		basePath + "/src/Data/Text_Files/story13.txt",
	    		basePath + "/src/Data/Text_Files/story14.txt",
	    		basePath + "/src/Data/Text_Files/story15.txt"};
	    		*/
	   
	   for(int i=1; i<=46; i++)
	   {
		   System.out.println("File " + i);
		// set up optional output files
		    PrintWriter out;
		  //  if (args.length > 1) {
		   //   out = new PrintWriter(args[1]);
		    //} else {
		     // out = new PrintWriter(System.out);
		   // }
		    PrintWriter xmlOut = new PrintWriter(basePath + "/outputs/Depen-Parse/story" + i + ".xml");
		    /*
		    if (args.length > 2) {
		      xmlOut = new PrintWriter(args[2]);
		    }
		    */
		    
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
		
		    // run all the selected Annotators on this text
		    pipeline.annotate(annotation);
		
		    // this prints out the results of sentence analysis to file(s) in good formats
		  //  pipeline.prettyPrint(annotation, out);
		    if (xmlOut != null) {
		      pipeline.xmlPrint(annotation, xmlOut);
		    }
		
		    // Access the Annotation in code
		    // The toString() method on an Annotation just prints the text of the Annotation
		    // But you can see what is in it with other methods like toShorterString()
		   // out.println();
		  //  out.println("The top level annotation");
		   // out.println(annotation.toShorterString());
		   // out.println();
		
		    // An Annotation is a Map with Class keys for the linguistic analysis types.
		    // You can get and use the various analyses individually.
		    // For instance, this gets the parse tree of the first sentence in the text.
		    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		    if (sentences != null && ! sentences.isEmpty()) {
		    	//do anything to every sentence
		    	for(CoreMap sentence: sentences)
		    	{
		    		
		    	}
		    	 
		  /* 	// comment start here
		      CoreMap sentence = sentences.get(0);
		      out.println("The keys of the first sentence's CoreMap are:");
		      out.println(sentence.keySet());
		      out.println();
		      out.println("The first sentence is:");
		      out.println(sentence.toShorterString());
		      out.println();
		      out.println("The first sentence tokens are:");
		      for (CoreMap token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
		        out.println(token.toShorterString());
		      }
		   //   Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
		    //  out.println();
		    //  out.println("The first sentence parse tree is:");
		     // tree.pennPrint(out);
		      //out.println();
		      out.println("The first sentence basic dependencies are:");
		      out.println(sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class).toString(SemanticGraph.OutputFormat.LIST));
		      out.println("The first sentence collapsed, CC-processed dependencies are:");
		      SemanticGraph graph =  (SemanticGraph) sentence.get((Class<? extends Key<Value>>) SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
		      out.println(graph.toString(SemanticGraph.OutputFormat.LIST));
		
		      // Access coreference. In the coreference link graph,
		      // each chain stores a set of mentions that co-refer with each other,
		      // along with a method for getting the most representative mention.
		      // Both sentence and token offsets start at 1!
		     
		      out.println("Coreference information");
		      Map<Integer, CorefChain> corefChains =
		          annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
		      if (corefChains == null) { return; }
		      for (Map.Entry<Integer,CorefChain> entry: corefChains.entrySet()) {
		        out.println("Chain " + entry.getKey());
		        for (CorefChain.CorefMention m : entry.getValue().getMentionsInTextualOrder()) {
		          // We need to subtract one since the indices count from 1 but the Lists start from 0
		          List<CoreLabel> tokens = sentences.get(m.sentNum - 1).get(CoreAnnotations.TokensAnnotation.class);
		          // We subtract two for end: one for 0-based indexing, and one because we want last token of mention not one following.
		          out.println("  " + m + ", i.e., 0-based character offsets [" + tokens.get(m.startIndex - 1).beginPosition() +
		                  ", " + tokens.get(m.endIndex - 2).endPosition() + ")");
		        }
		      }
		   
		      //out.println();
		
		    //  out.println("The first sentence overall sentiment rating is " + sentence.get(SentimentCoreAnnotations.SentimentClass.class));
		*/ // comment end here
		    }
		   // IOUtils.closeIgnoringExceptions(out);
		    IOUtils.closeIgnoringExceptions(xmlOut);
		 
		  }
	}
	
	
	public void GenerateCoref(String args[]) throws IOException
	{

		// Create a CoreNLP pipeline. To build the default pipeline, you can just use:
	    //   StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    // Here's a more complex setup example:
	    //   Properties props = new Properties();
	    //   props.put("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
	    //   props.put("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
	    //   props.put("ner.applyNumericClassifiers", "false");
	    //   StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

	    // Add in sentiment
	    Properties props = new Properties();
	    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");

	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	// make sure you add the Data folder to your classpath and the Data folder is in the build path project folder
	  //  java.net.URL url = Test.class.getResource("/Data/Text_Files/story1.txt"); //getClassLoader().
	  //  System.out.println(url); //url.getPath()
	    
	    //specify where the input text files are - these files are .txt and only have the text of the stories
	    /*
	    String files[] = {basePath + "/src/Data/Text_Files/story1.txt",
	    		basePath + "/src/Data/Text_Files/story2.txt",
	    		basePath + "/src/Data/Text_Files/story3.txt",
	    		basePath + "/src/Data/Text_Files/story4.txt",
	    		basePath + "/src/Data/Text_Files/story5.txt",
	    		basePath + "/src/Data/Text_Files/story6.txt",
	    		basePath + "/src/Data/Text_Files/story7.txt",
	    		basePath + "/src/Data/Text_Files/story8.txt",
	    		basePath + "/src/Data/Text_Files/story9.txt",
	    		basePath + "/src/Data/Text_Files/story10.txt",
	    		basePath + "/src/Data/Text_Files/story11.txt",
	    		basePath + "/src/Data/Text_Files/story12.txt",
	    		basePath + "/src/Data/Text_Files/story13.txt",
	    		basePath + "/src/Data/Text_Files/story14.txt",
	    		basePath + "/src/Data/Text_Files/story15.txt"};
	    		*/
	   
	   for(int i=1; i<=46; i++)
	   {
		   
		// set up optional output files
		    PrintWriter out;
		   // if (args.length > 1) {
		    //  out = new PrintWriter(args[1]);
		   // } else {
		    //  out = new PrintWriter(System.out);
		   // }
		    PrintWriter xmlOut = new PrintWriter(basePath + "/outputs/Coref-Chains/story" + i + ".txt");
		    /*
		    if (args.length > 2) {
		      xmlOut = new PrintWriter(args[2]);
		    }
		    */
		    
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
		
		    // run all the selected Annotators on this text
		    pipeline.annotate(annotation);
		
		    // this prints out the results of sentence analysis to file(s) in good formats
		    /*
		    pipeline.prettyPrint(annotation, out);
		    if (xmlOut != null) {
		      pipeline.xmlPrint(annotation, xmlOut);
		    }
		    */
		
		    // Access the Annotation in code
		    // The toString() method on an Annotation just prints the text of the Annotation
		    // But you can see what is in it with other methods like toShorterString()
		  //  out.println();
		   // out.println("The top level annotation");
		   // out.println(annotation.toShorterString());
		   // out.println();
		
		    
		    // An Annotation is a Map with Class keys for the linguistic analysis types.
		    // You can get and use the various analyses individually.
		    // For instance, this gets the parse tree of the first sentence in the text.
		    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		    if (sentences != null && ! sentences.isEmpty()) {
		    	//do anything to every sentence
		    	for(CoreMap sentence: sentences)
		    	{
		    		
		    	}
		    	for (CorefChain cc : annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
		    	      xmlOut.println("\t" + cc);
		    	    }
		    	 
		    	/*
		    	
		    	out.println("Coreference information");
			      Map<Integer, CorefChain> corefChains =
			          annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
			      if (corefChains == null) { return; }
			      for (Map.Entry<Integer,CorefChain> entry: corefChains.entrySet()) {
			        out.println("Chain " + entry.getKey());
			        for (CorefChain.CorefMention m : entry.getValue().getMentionsInTextualOrder()) {
			          // We need to subtract one since the indices count from 1 but the Lists start from 0
			          List<CoreLabel> tokens = sentences.get(m.sentNum - 1).get(CoreAnnotations.TokensAnnotation.class);
			          // We subtract two for end: one for 0-based indexing, and one because we want last token of mention not one following.
			          out.println("  " + m + ", i.e., 0-based character offsets [" + tokens.get(m.startIndex - 1).beginPosition() +
			                  ", " + tokens.get(m.endIndex - 2).endPosition() + ")");
			        }
			      }
			      */
		  /* 	// comment start here
		      CoreMap sentence = sentences.get(0);
		      out.println("The keys of the first sentence's CoreMap are:");
		      out.println(sentence.keySet());
		      out.println();
		      out.println("The first sentence is:");
		      out.println(sentence.toShorterString());
		      out.println();
		      out.println("The first sentence tokens are:");
		      for (CoreMap token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
		        out.println(token.toShorterString());
		      }
		   //   Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
		    //  out.println();
		    //  out.println("The first sentence parse tree is:");
		     // tree.pennPrint(out);
		      //out.println();
		      out.println("The first sentence basic dependencies are:");
		      out.println(sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class).toString(SemanticGraph.OutputFormat.LIST));
		      out.println("The first sentence collapsed, CC-processed dependencies are:");
		      SemanticGraph graph =  (SemanticGraph) sentence.get((Class<? extends Key<Value>>) SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
		      out.println(graph.toString(SemanticGraph.OutputFormat.LIST));
		
		      // Access coreference. In the coreference link graph,
		      // each chain stores a set of mentions that co-refer with each other,
		      // along with a method for getting the most representative mention.
		      // Both sentence and token offsets start at 1!
		     
		      out.println("Coreference information");
		      Map<Integer, CorefChain> corefChains =
		          annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
		      if (corefChains == null) { return; }
		      for (Map.Entry<Integer,CorefChain> entry: corefChains.entrySet()) {
		        out.println("Chain " + entry.getKey());
		        for (CorefChain.CorefMention m : entry.getValue().getMentionsInTextualOrder()) {
		          // We need to subtract one since the indices count from 1 but the Lists start from 0
		          List<CoreLabel> tokens = sentences.get(m.sentNum - 1).get(CoreAnnotations.TokensAnnotation.class);
		          // We subtract two for end: one for 0-based indexing, and one because we want last token of mention not one following.
		          out.println("  " + m + ", i.e., 0-based character offsets [" + tokens.get(m.startIndex - 1).beginPosition() +
		                  ", " + tokens.get(m.endIndex - 2).endPosition() + ")");
		        }
		      }
		   
		      //out.println();
		
		    //  out.println("The first sentence overall sentiment rating is " + sentence.get(SentimentCoreAnnotations.SentimentClass.class));
		*/ // comment end here
		    }
		//    IOUtils.closeIgnoringExceptions(out);
		    IOUtils.closeIgnoringExceptions(xmlOut);
		 
		  }
	
	}
	
}
