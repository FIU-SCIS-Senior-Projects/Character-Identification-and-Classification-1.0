
import java.util.ArrayList;
import java.util.Hashtable;
import org.w3c.dom.*;


/**
 * 
 * @author Labiba Jahan
 *
 */
public class ParseData{
	
	ArrayList<String> refList = new ArrayList<String>();
	ArrayList<String> refList2 = new ArrayList<String>();
	ArrayList<String> coref = new ArrayList<String>();
	//Parsing coreflist according to reference expression
	public Hashtable<Integer,String> corefList( Hashtable<Integer,String>ref , Hashtable<Integer, String> coref)
	{
		
        for (int i=0; i< coref.size(); i++){
        	String splits[] = coref.get(i).split("[|]");
        	if(splits.length >1){
	        	  String splits2[] = splits[1].split("[,]");
	        	//if(splits2.length >1){
	        	  for (int j=0; j< splits2.length; j++){
	        		   int a = Integer.parseInt(splits2[j]);
						String str=coref.get(i);
						str = str.replace( splits2[j] , ref.get(a));
	        			coref.put(i,str);
	        			//System.out.println(coref.get(i));
	        	 // }
	        	}//else coref.put(i,"null");
	       }
        }
		return coref;
	}
	
	
    // Parsing referring expression from stories in Story Workbench Format
	public Hashtable<Integer, String> parseRef(Document doc){
		//parse reflist with id
		Hashtable<Integer, String> reflist = new Hashtable<Integer, String>();
		
    	NodeList nodeList5=doc.getElementsByTagName("rep");
        for (int i=0; i<nodeList5.getLength(); i++){
        	Element element5 = (Element)nodeList5.item(i);
	        if(element5.getAttribute("id").equals("edu.mit.discourse.rep.refexp")){
	        NodeList nodeList6=element5.getElementsByTagName("desc");
	        for (int j=0; j<nodeList6.getLength(); j++){
		        	Element element6 = (Element)nodeList6.item(j);
		        	int m = Integer.parseInt(element6.getAttribute("id"));
		        	reflist.put(m,element6.getTextContent());
		        	
		        	String splits[] = reflist.get(m).split("[~, \\n]");
		        	String a = "";
		        	if(splits.length>0){
		        	  for (int k=0; k< splits.length; k++){
		        		    String b = splits[k]; 
		        		    if(!b.isEmpty()){
		        		    	NodeList nodeList3=doc.getElementsByTagName("desc");	
			        	        for (int l=0; l<nodeList3.getLength(); l++){
						        	   Element element2 = (Element)nodeList3.item(l);
									   if(element2.getAttribute("id").equals(b)){
							        		String c = element2.getTextContent();
							        		a= a+ " " + c;
							        	}
									   else continue;
						         }
		        		    }
		        	   }
		        	  
		        	}
		        	
		        	reflist.put(m,a);
		        	refList2.add(a);
		        }
	        }
        }
        
        //print reflist with id
   
//        System.out.println("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
//        int count  = 0;
//        for (java.util.Map.Entry<Integer, String> entry : reflist.entrySet()) {
//            System.out.println(entry.getKey() + entry.getValue());
//            count++;
//        }
//        System.out.println(count);
        
//        for(int j=0;j<refList2.size();j++){
//    	System.out.println(refList2.get(j).trim());
//    	}
//        System.out.println( ".....................................................................................");	
        
        return reflist;
        
	}
		

	
	
	
	// Parsing coreference from stories
	public Hashtable<Integer, String> parseCoref(Document doc){

			//ArrayList<String> coref = new ArrayList<String>();
	    	NodeList nodeList=doc.getElementsByTagName("rep");
	        for (int i=0; i<nodeList.getLength(); i++){
	        	    Element element = (Element)nodeList.item(i);
		        	if(element.getAttribute("id").equals("edu.mit.discourse.rep.coref")){
		        		coref.add(element.getTextContent());
		        	}
		      
	        }
	        
	        Hashtable<Integer, String> refexp = new Hashtable<Integer, String>();
	        String refs[]= coref.get(0).split("\\n");
	        for (int k=0; k<refs.length; k++){
	        	refexp.put(k,refs[k]);
	         }
	
	        return refexp;
	       
		}


}
