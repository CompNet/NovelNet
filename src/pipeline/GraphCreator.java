package pipeline;

import java.io.IOException;

import graph.*;

public class GraphCreator {

	private GraphCreator() {

	}

	public static Graph createGraph(CooccurrenceTable tab, boolean weighting) {
		Graph graph = new Graph();
		for (int i = 0; i < tab.listCharA.size(); i++) { // Until we reach the size of the list of sentences
			Node nA = new Node(tab.listCharA.get(i)); // We create a new node
			Node nB = new Node(tab.listCharB.get(i)); // We create a new node
			graph.addNode(nA);
			graph.addNode(nB);
			graph.addCooccurrenceEdge(nA, nB, weighting, tab.listDistanceWord.get(i)); // We create a new edge which links the two
																			// nodes with their weight
		}
		return graph;
	}

	public static Graph createGraph(CooccurrenceTable tab, boolean weighting, String name) {
		Graph graph = createGraph(tab, weighting);
		graph.setName(name);
		return graph;
	}

	public static Graph createGraph(InteractionTable tab, String name) {
		Graph graph = new Graph();
		graph.setOriented(true);
		graph.setName(name);
		for (int i = 0; i < tab.subject.size(); i++) { // Until we reach the size of the list of sentences
			Node nA = new Node(tab.subject.get(i)); // We create a new node
			Node nB = new Node(tab.object.get(i)); // We create a new node
			graph.addNode(nA);
			graph.addNode(nB);
			graph.addInteractionEdge(nA, nB, tab.type.get(i)); // We create a new edge which links the two nodes with their weight
		}
		return graph;
	}

	public static void testCooccurrence() throws IOException{
		CooccurrenceTableSentence cts= new CooccurrenceTableSentence();
		cts.add("A", "B", 25, 5, 0, 4);
		cts.add("A", "C", 20, 4, 0, 4);
		cts.add("B", "C", 15, 3, 0, 4);
		cts.add("A", "C", 15, 3, 4, 8);
		cts.add("B", "C", 25, 5, 4, 8);
		cts.add("A", "C", 10, 2, 12, 16);
		cts.add("B", "A", 15, 3, 12, 16);
		cts.add("A", "E", 20, 4, 16, 20);
		cts.add("E", "C", 20, 4, 16, 20);

		GraphCreator gC = new GraphCreator();
		Graph g = gC.createGraph(cts,true,"graph_test_cooccurrence");
		g.graphMLPrinter("res/results");
		System.out.println(g.toString());
	}

	public static void testInteraction() throws IOException{
		InteractionTable it = new InteractionTable();
		it.add("A", "B", 0, "bad");
		it.add("A", "C", 0, "nice");
		it.add("C", "A", 0, "nice");
		it.add("C", "B", 0, "bad");
		it.add("B", "A", 1, "bad");
		it.add("C", "A", 1, "nice");
		it.add("C", "A", 1, "bad");

		InteractionTable itNull = new InteractionTable();
		itNull.add("A", "B", 0);
		itNull.add("A", "C", 0);
		itNull.add("C", "A", 0);
		itNull.add("C", "B", 0);
		itNull.add("B", "A", 1);
		itNull.add("C", "A", 1);
		itNull.add("C", "A", 1);

		it.display();
		itNull.display();

		Graph g = GraphCreator.createGraph(itNull,"graph_test_interaction");
		g.graphMLPrinter("res/results");
		System.out.println(g.toString());
	}

	public static void main(String[] args) throws IOException {
		//testCooccurrence();
		testInteraction();
	}
}
