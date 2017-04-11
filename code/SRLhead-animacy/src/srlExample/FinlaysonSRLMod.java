package srlExample;
//By Mark Finlayson


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;

import com.eventmonitor.srl.finder.IPotentialArgFinder;
import com.eventmonitor.srl.finder.IPredicateFinder;
import com.eventmonitor.srl.finder.PotentialArgFinder;
import com.eventmonitor.srl.finder.PredicateFinder;
import com.eventmonitor.srl.identifier.ArgumentIdentifier;
import com.eventmonitor.srl.identifier.IArgumentIdentifier;
import com.eventmonitor.srl.iterators.IPredicateInfo;
import com.eventmonitor.srl.iterators.ISentenceInfo;
import com.eventmonitor.srl.iterators.ISentenceWrapper;
import com.eventmonitor.srl.iterators.PredicateInfo;
import com.eventmonitor.srl.iterators.SentenceInfo;
import com.eventmonitor.srl.iterators.SentenceWrapper;
import com.eventmonitor.srl.labeler.ArgumentLabeler;
import com.eventmonitor.srl.labeler.IArgumentLabeler;
import com.eventmonitor.srl.labeler.SemanticRoleLabeler;
import com.eventmonitor.srl.parser.ParseUtils;
import com.eventmonitor.srl.propbank.Arg;
import com.eventmonitor.srl.propbank.ArgLoc;
import com.eventmonitor.srl.propbank.IArg;
import com.eventmonitor.srl.propbank.IPredicateFeatures;
import com.eventmonitor.srl.propbank.Predicate;
import com.eventmonitor.srl.propbank.PredicateFeatures;

import opennlp.tools.parser.Parse;

public class FinlaysonSRLMod extends SemanticRoleLabeler {
    
    private final IPredicateFinder predFinder;
    private final IPotentialArgFinder argFinder;
    private final IArgumentIdentifier argIdentifier;
    private final IArgumentLabeler argLabeler;
    
    public FinlaysonSRLMod(){
        this(PredicateFinder.getInstance(), PotentialArgFinder.getInstance(), new ArgumentIdentifier(), new ArgumentLabeler());
    }
    
    public FinlaysonSRLMod(IPredicateFinder predFinder, IPotentialArgFinder argFinder, IArgumentIdentifier argIdentifier, IArgumentLabeler argLabeler){
        if(predFinder == null) throw new NullPointerException();
        if(argFinder == null) throw new NullPointerException();
        if(argIdentifier == null) throw new NullPointerException();
        if(argLabeler == null) throw new NullPointerException();
        
        this.predFinder = predFinder;
        this.argFinder = argFinder;
        this.argIdentifier = argIdentifier;
        this.argLabeler = argLabeler;
    }
    
    @Override
    /* (non-Javadoc) @see com.eventmonitor.srl.labeler.ISemanticRoleLabeler#labelRoles(opennlp.tools.parser.Parse) */
    public ISentenceInfo labelRoles(Parse sentence) {
        
        List<Parse> preds = predFinder.findPredicates(sentence);
        if(preds.isEmpty()) return new SentenceInfo(sentence, Collections.<IPredicateInfo>emptyList());
        
        // this will be the list backing the ISentenceInfo predicate list
        List<IPredicateInfo> predInfos = new ArrayList<IPredicateInfo>(preds.size());
        
        // copy class variables to local frame variable list 
        IPotentialArgFinder localArgFinder = this.argFinder;
        IArgumentIdentifier localArgIdentifier = this.argIdentifier;
        IArgumentLabeler localArgLabeler = this.argLabeler;
        
        // loop variables
        ISentenceWrapper wrapper = new SentenceWrapper(sentence);
        IPredicateFeatures predFeatures;
        List<Parse> args;
        List<IArg> labeledArgs = new LinkedList<IArg>();
        
        // search variables
        Map<Parse, Set<Parse>> conflicting;
        Set<Parse> argConflicts, otherConflicts, ancestors;
        Map<Parse, Map<String, Double>> logPs;
        Map<String, Double> pMap;
        BestFirstSearch search;
        Queue<FinlaysonSRLMod.BestFirstSearch.Result> queue;
        FinlaysonSRLMod.BestFirstSearch.Result result;
        int limit, increment = 10000;

        for(Parse pred : preds){
            // get predicate features
            predFeatures = PredicateFeatures.extractFeatures(pred, wrapper);
            
            // find potential arguments
            args = localArgFinder.findArguments(pred);
            if(args.isEmpty()) continue;
            
//        // ====================>  temporary!!!!!!!!!!!!!!! 
//        if(args.size() > 10){
//            System.out.println(args.size() + " args --> ignoring");
//            System.out.println(ParseUtils.getTaggedText(sentence));
//            continue;
//        }
//        // ====================>  temporary!!!!!!!!!!!!!!! 
            
            // the conflicting map contains all the other arguments
            // that cannot be given a label if an argument is given a label
            conflicting = new LinkedHashMap<Parse, Set<Parse>>(args.size());

            // the probability map contains the log probability of each label and null, normalized to one
            // this means the probability of not being an argument is assigned to null, and the probability
            // of being an argument is distributed among the labels in proportion to their probabilities
            logPs = new LinkedHashMap<Parse, Map<String, Double>>(args.size());
            
            // for each potential argument, collect label probabilities and conflicting arguments
            for(Parse arg : args){
                
                // get label probabilities for the argument
                pMap = localArgLabeler.evaluateAllLabels(arg, pred, predFeatures);
                
                // convert probability maps to log(prob) maps
                for(Entry<String, Double> e : pMap.entrySet()) e.setValue(Math.log(e.getValue()));
                
                // add null (meaning not an argument) and normalize log(prob) map 
                normalize(localArgIdentifier.isArgumentProb(arg, pred, predFeatures), pMap);
                logPs.put(arg, pMap);

                // figure out which arguments conflict with this one
                argConflicts = getConflictSet(arg, conflicting);
                ancestors = new HashSet<Parse>(ParseUtils.getAncestorChain(arg));
                for(Parse other : args){
                    if(arg == other) continue;
                    if(ancestors.contains(other)){
                        argConflicts.add(other);
                        otherConflicts = getConflictSet(other, conflicting);
                        otherConflicts.add(arg);
                    }
                }
            }

            // do a search to find the optimal assignment of labels to arguments
            search = new BestFirstSearch(logPs, conflicting, localArgLabeler.getSingleLabels(), localArgLabeler.getMultipleLabels());
            queue = search.getQueue();
            result = null;
            limit = increment;

            while(!queue.isEmpty()){
                // I don't know what this nonsense is, but I'm changing it.
                
//            //======================== Start Debug
                if(queue.size() > limit){
                    if (limit >= 100000) {
                        break;
                    }
//                    StringBuilder sb = new StringBuilder();
//                    if(limit == increment){
//                        sb.append('\n');
//                        sb.append(pred.getText());
//                        sb.append('\n');
//                    }
//                    sb.append("pred=");
//                    sb.append(pred.toString());
//                    sb.append(", ");
//                    sb.append(Integer.toString(args.size()));
//                    sb.append("args, ");
//                    sb.append(Integer.toString(queue.size()));
//                    sb.append(" choices, ");
//                    sb.append(" best=");
//                    sb.append(queue.peek().p);
//                    System.out.println(sb);
                    limit += increment;
                }
//            //======================== End Debug
                
                result = queue.poll();
                if(result.isComplete()) break;
                queue.addAll(result.getChildren());
            }
            
            // add the labeled arguments to the list
            labeledArgs.clear();
            for(Entry<Parse, String> label : result.choices.entrySet()){
                if(label.getValue() == null) continue;
                labeledArgs.add(new Arg(label.getValue(), ArgLoc.createArgLoc(wrapper, label.getKey())));
            }
            if(labeledArgs.isEmpty()) continue;
            
            // create the predicate info object and add it to the list
            predInfos.add(new PredicateInfo(wrapper, new Predicate(predFeatures, new ArrayList<IArg>(labeledArgs))));
        }
        
        return new SentenceInfo(sentence, predInfos);
    }
    
protected class BestFirstSearch {
        
        final List<Parse> args;
        final Map<Parse, Set<Parse>> conflicting;
        final Map<Parse, Map<String, Double>> logPs;
        final Set<String> exclusiveLabels, nonExclusiveLabels;
        
        private Queue<Result> queue;
        
        public BestFirstSearch(Map<Parse, Map<String, Double>> logPs, 
                               Map<Parse, Set<Parse>> conflicting, 
                               Set<String> exclusiveLabels, 
                               Set<String> nonExclusiveLabels){
            this.args = new ArrayList<Parse>(logPs.keySet());
            this.logPs = logPs;
            this.conflicting = conflicting;
            this.exclusiveLabels = exclusiveLabels;
            this.nonExclusiveLabels = nonExclusiveLabels;
        }
        
        public Queue<Result> getQueue(){
            if(queue == null){
                queue = new PriorityQueue<Result>();
                queue.add(new Result());
            }
            return queue;
        }
        
        protected class Result implements Comparable<Result> {
            
            public final double p;
            public final int nextArgIdx;
            public final Set<String> remainingLabels;
            public final Map<Parse, String> choices;
            
            public Result(){
                p = 0;
                nextArgIdx = 0;
                choices = new HashMap<Parse, String>();
                remainingLabels = new HashSet<String>(exclusiveLabels);
            }
            
            public Result(Result parent, String chosenLabel){
                
                // parent shouldn't be complete (or null, for that matter)
                if(parent.isComplete()) throw new IllegalArgumentException();
                
                // find the argument to which the chosen label applies
                Parse chosenArg = args.get(parent.nextArgIdx);
                
                // add choice
                choices = new HashMap<Parse, String>(parent.choices);
                choices.put(chosenArg, chosenLabel);
                
                // null out conflicting nodes if necessary
                if(exclusiveLabels.contains(chosenLabel)){
                    for(Parse cArg : conflicting.get(chosenArg)) choices.put(cArg, null);
                }
                
                // assign next index for descendants
                int nextIdx = parent.nextArgIdx+1;
                while(nextIdx < args.size() && parent.choices.containsKey(args.get(nextIdx))) nextIdx++;
                this.nextArgIdx = nextIdx;
                
                // calculate probability
                p = parent.p + logPs.get(chosenArg).get(chosenLabel);
                
                // assemble remaining labels set
                remainingLabels = new HashSet<String>(parent.remainingLabels);
                remainingLabels.remove(chosenLabel);
            }
            
            public List<Result> getChildren(){
                List<Result> children = new LinkedList<Result>();
                for(String label : remainingLabels) children.add(new Result(this, label));
                for(String label : nonExclusiveLabels) children.add(new Result(this, label));
                children.add(new Result(this, null));
                return children;
            }
        
            public boolean isComplete(){
                return nextArgIdx == args.size();
            }
    
            /* (non-Javadoc) @see java.lang.Comparable#compareTo(java.lang.Object) */
            public int compareTo(Result o) {
                return -1*Double.compare(p, o.p);
            }
            
            public String toString(){
                StringBuilder sb = new StringBuilder();
                sb.append("\tp=" + p);
                sb.append("\n\tdone?\t" + isComplete());
                sb.append('\n');
                for(Entry<Parse, String> entry : choices.entrySet()){
                    sb.append("\t" + entry.getValue() + "\t" + entry.getKey().toString());
                    sb.append('\n');
                }
                return sb.toString();
            }

            /* (non-Javadoc) @see java.lang.Object#hashCode() */
            @Override
            public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + choices.hashCode();
                result = prime * result + nextArgIdx;
                result = prime * result + remainingLabels.hashCode();
                return result;
            }

            /* (non-Javadoc) @see java.lang.Object#equals(java.lang.Object) */
            @Override
            public boolean equals(Object obj) {
                if (this == obj) return true;
                if (obj == null) return false;
                if (getClass() != obj.getClass()) return false;
                final Result other = (Result) obj;
                if (nextArgIdx != other.nextArgIdx) return false;
                if (!choices.equals(other.choices)) return false;
                if (!remainingLabels.equals(other.remainingLabels)) return false;
                return true;
            }
        }
    }

}
