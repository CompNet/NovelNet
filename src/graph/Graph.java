package graph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Quay Baptiste, Lemaire Tewis
 *
 */
public class Graph {
	
	protected Map<String,Node> nodeMap;
	protected Map<String, Edge> edgeMap;
	String name;
	Boolean oriented;
	
	public Graph()
	{
		nodeMap = new HashMap<String,Node>();
		edgeMap = new HashMap<String, Edge>();
		oriented = false;
	}
	
	public Graph(String name)
	{
		nodeMap = new HashMap<String,Node>();
		edgeMap = new HashMap<String, Edge>();
		this.name = name;
		oriented = false;
	}

	public Graph(String name, Boolean oriented)
	{
		nodeMap = new HashMap<String,Node>();
		edgeMap = new HashMap<String, Edge>();
		this.name = name;
		this.oriented = oriented;
	}
	
	public Graph(HashMap<String,Node> nodeMap, HashMap<String, Edge> edgeMap, String name, Boolean oriented)
	{
		this.nodeMap = nodeMap;
		this.edgeMap = edgeMap;
		this.name = name;
		this.oriented = oriented;
	}
	
	public Node getNodeById(String id)
	{
		return nodeMap.get(id);
	}
	
	public boolean addNode(Node node)
	{
		if (nodeMap.containsKey(node.id))
			return false;
		nodeMap.put(node.id, node);
		return true;
	}
	
	public Edge getEdgeById(String id){
		return edgeMap.get(id);
	}
	
	public boolean addCooccurrenceEdge(Node charA, Node charB, boolean weighting, float ponderation)
	{
		String id = findEdge(charA, charB);
		if (id != null) {	
			if (weighting){
				edgeMap.get(id).addPonderation(1/ponderation);
				return true;
			}
			else return false;
		}
		Edge tmpEdge;
		if (weighting) tmpEdge = new Edge(charA.id+ " " + charB.id, charA, charB, 1/ponderation);
		else tmpEdge = new Edge(charA.id, charA, charB);
		edgeMap.put(tmpEdge.id, tmpEdge);
		return true;
	}

	public void addInteractionEdge(Node charA, Node charB, String type){
		String id = findEdge(charA, charB, type);		
		if (id != null) {	
			edgeMap.get(id).addPonderation();
		}
		else {
			Edge tmpEdge;
			tmpEdge = new Edge(charA.name + " " + type + " " + charB.name, charA, charB, type);
			edgeMap.put(tmpEdge.id, tmpEdge);
		}
	}
	
	public String findEdge(Node nodeA, Node nodeB){
		for (Edge e : edgeMap.values()) { // For each edge in the list
			if ((e.nodeLeft.getName() == nodeA.getName() && e.nodeRight.getName() == nodeB.getName()) || (e.nodeLeft.getName() == nodeB.getName() && e.nodeRight.getName() == nodeA.getName())){
				return e.id;
			}
		}
		return null;
	}

	public String findEdge(Node nodeA, Node nodeB, String type){
		for (Edge e : edgeMap.values()) { // For each edge in the list
			if (e.nodeLeft.getName() == nodeA.getName() && e.nodeRight.getName() == nodeB.getName() && e.type == type){
				return e.id;
			}
		}
		return null;
	}
	
	/*protected Edge returnInverseLink(Edge e)
	{
		for (Edge el : edgeMap.values()) // For each edge in the list
		{
			if (el.nodeLeft.equals(e.nodeRight) && el.nodeRight.equals(e.nodeLeft)) // Checks if the left node is equal to the right node and if the right node is equal to the left one
				return el; // Returns true if yes
		}
		return null; //Returns false if conditions are not met
	}*/
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public String getOriented()
	{
		return name;
	}
	
	public void setOriented(Boolean oriented)
	{
		this.oriented = oriented;
	}
	
	public void graphMLPrinter(String path) throws IOException
	{
		

		if (nodeMap.isEmpty())
		{
			System.out.println("Erreur nodeMapeMpty !");
			return;
		}
		
		if (edgeMap.isEmpty())
		{
			System.out.println("Erreur edgeMapeMpty !");
			return;
		}
		
		String pathDest = name+".graphml";
		
		if (!"".equals(path))
			pathDest = path+"/"+pathDest;
		
		
		FileWriter fw = new FileWriter(pathDest);
		BufferedWriter buffer = new BufferedWriter(fw);
		
		buffer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.newLine();
		buffer.write("<graphml>");
		buffer.newLine();
		buffer.write("<key id=\"keyNode\" for=\"node\" attr.name=\"characterName\" attr.type=\"string\">");
		buffer.newLine();
		buffer.write("</key>");
		buffer.write("<key id=\"keyEdge\" for=\"edge\" attr.name=\"weight\" attr.type=\"float\"/>");
		buffer.newLine();

		String direction = "undirected";
		if (oriented) direction = "directed";

		buffer.write("<graph id=\""+name+"\" edgedefault=\""+ direction + "\">");
		
		buffer.newLine();
		
		for (Node node : nodeMap.values())
		{
			buffer.write("<node id=\""+node.id+"\">");
			buffer.newLine();
			buffer.write("<data key=\"keyNode\">"+node.name+"</data>");
			buffer.newLine();
			buffer.write("</node>");
			buffer.newLine();
		}
		for (Edge edge : edgeMap.values())
		{
			buffer.write("<edge id=\""+edge.id+"\" source=\""+edge.nodeLeft.id+"\" target=\""+edge.nodeRight.id+"\">");
			buffer.newLine();
			buffer.write("<data key=\"keyEdge\">"+edge.ponderation+"</data>");
			buffer.newLine();
			buffer.write("</edge>");
			buffer.newLine();
		}
		
		buffer.write("</graph>");
		buffer.newLine();
		buffer.write("</graphml>");
		buffer.flush();
		buffer.close();
		
		System.out.println(pathDest+" printed !");
	}
	
	public String toString()
	{
		String ret="Graph name: "+name+"\n";

		for (Node node : nodeMap.values())
		{
			ret+="Node ID = "+node.id+" Name = "+node.name+"\n";
		}
		
		for (Edge edge : edgeMap.values())
		{
			ret+=" Ponderation = "+edge.ponderation+" NodeLeft = "+edge.nodeLeft.name+" NodeRight = "+edge.nodeRight.name;
			if (edge.type != null) ret+= " type = " + edge.type + "\n";
			else ret += "\n";
		}	
		
		return ret;
	}

}