package controller;

import java.util.*;

import beans.graph.Graph;
import beans.graph.MultiEdge;
import beans.graph.MultiVertex;
import beans.relation.Connection;
import beans.relation.Rectangle;

/**
 * Handles the IO of the Json strings for the concept map
 * @author ingahege
 *@deprecated
 */
public class ConceptMapController {
	/*public static final String PREFIX_PROB = "cmprob_";
	public static final String PREFIX_DDX = "cmddx_";
	public static final String PREFIX_MNG = "cmmng_";
	public static final String PREFIX_CNX = "cmcnx_";
	public static final String PREFIX_TEST = "cmds_";
	
	public static final int TYPE_PROB = 1;
	public static final int TYPE_DDX = 2;
	public static final int TYPE_TEST = 3;
	public static final int TYPE_MNG = 4;
	public static final int TYPE_CNX = 5;*/
	
	
	/*public String getConnsToJson(Map m){
		if(m==null || m.isEmpty()) return "";
		StringBuffer sb = new StringBuffer("[");
		Iterator<Connection> it = m.values().iterator();
		while(it.hasNext()){
			sb.append(it.next().toJson());
			if(it.hasNext()) sb.append(",");
		}
		sb.append("]");
		return sb.toString();
	} */
	
	/*public String getEdgesToJson(Graph g){
		Set<MultiEdge> edges = g.edgeSet();
		if(edges==null || edges.isEmpty()) return "";
		Iterator<MultiEdge> it = edges.iterator();
		StringBuffer sb = new StringBuffer("[");
		while(it.hasNext()){
			MultiEdge edge = it.next();
			
		}
	}*/
	
	/*public String getGraphToJson(Graph g){
		Set<MultiVertex> vertices = g.vertexSet();
		if(vertices==null || vertices.isEmpty()) return ""; 
		Iterator<MultiVertex> it = vertices.iterator();
		StringBuffer sb = new StringBuffer("[");
		
		while(it.hasNext()){
			MultiVertex mv = it.next();
			Rectangle learnerRel = (Rectangle) mv.getLearnerVertex();
			if(learnerRel!=null)
				sb.append(learnerRel.toJson()+",");
		}
		sb.replace(sb.length()-1, sb.length(), ""); //remove the last ","
		sb.append("]");
		return sb.toString();
	}*/

	
	/*public String getRelationsToJson(List l){
		if(l==null || l.isEmpty()) return "";
		StringBuffer sb = new StringBuffer("[");
		for(int i=0; i<l.size(); i++){
			sb.append(((Rectangle)l.get(i)).toJson());
			if(i<l.size()-1) sb.append(",");
		}
		sb.append("]");
		return sb.toString();
	}*/
	
	/*public int getTypeByPrefix(String prefix){
		if(prefix==null) return 0;
		if(prefix.equals(PREFIX_PROB)) return TYPE_PROB;
		if(prefix.equals(PREFIX_DDX)) return TYPE_DDX;
		return 0;
	}
	
	public String getPrefixByType(int type){
		if(type==TYPE_PROB) return PREFIX_PROB;
		if(type==TYPE_DDX) return PREFIX_DDX;
		return "";
	}*/
}
