package novelnet.pipeline;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import novelnet.book.Book;
import novelnet.graph.*;

import novelnet.table.CooccurrenceTableSentence;
import novelnet.table.DirectInteractionTable;
import novelnet.table.InteractionTable;
import novelnet.util.CustomCorefChain;
import novelnet.util.ImpUtils;
import novelnet.util.NullDocumentException;
import performance.clustering.ClusterContainer;

/**
 * Create a DirectInteractionTable
 * 
 * @author Quay Baptiste, Lemaire Tewis
*/
public class GraphCreator {

	/**
     * Class Constructor 
    */
	private GraphCreator() {

	}

	/**
     * Create the graph, oriented should be true if the table is a DirectInteractionTable, false otherwise
	 * 
	 * @param tab The interaction table to make the graph from
	 * @param name the name of the graph
	 * @param oriented true if you want an oriented graph false otherwise
	 * @param weighting true to weight your graph false otherwise
	 * @return The graph built
    */
	public static Graph createGraph(InteractionTable tab, String name ,boolean oriented, boolean weighting) {
		Graph graph = new Graph(name, oriented, weighting);
		for (int i = 0; i < tab.getListCharA().size(); i++) { // Until we reach the size of the list characters
			Node nA = new Node(tab.getListCharA().get(i)); // We create a new node
			Node nB = new Node(tab.getListCharB().get(i)); // We create a new node
			graph.addNode(nA);
			graph.addNode(nB);
			graph.addEdge(nA, nB, tab.getListDistanceWord().get(i), tab.getListType().get(i)); 	// We create a new edge which links the two
																								// nodes with their weight
		}
		return graph;
	}

	/**
     * Create a graph directly from a text file (mostly used for performances purposes)
	 * 
	 * @param evaluationFilePath the path to the text file
	 * @param dbScanDist the distance used for the dbscan algorithm (should be between 0 and 1, 0.40 is a good value overall)
	 * @param sentNumber size of the window in sentences
	 * @param covering covering between two windows in sentences
	 * @return The graph built
    */
	public static Graph buildCoOcSentFromTxt(String evaluationFilePath, double dbScanDist, int sentNumber, int covering) throws IOException, NullDocumentException {

		//importing the text 
		FileInputStream is = new FileInputStream(evaluationFilePath);
		String content = IOUtils.toString(is, StandardCharsets.UTF_8);

		//normalizing the text
		content = TextNormalization.addDotEndOfLine(content);

		//creating stanford's pipeline
		String prop="tokenize,ssplit,pos,lemma,ner,parse,coref";

		Properties props = new Properties();
		props.setProperty("annotators",prop);
		props.setProperty("ner.applyFineGrained", "false");
		
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		ImpUtils.setDocument(document);
		pipeline.annotate(document);

		//creating the corefChains and clustering them
		List<CustomCorefChain> cccList = CustomCorefChainCreator.makeCustomCorefChains(document);
		CorefChainFuser corefChainFuser = new CorefChainFuser();
		cccList = corefChainFuser.corefChainsClusteringRO(cccList, dbScanDist);

		//creating the book
		Book book = CreateBook.createBook(document, false, cccList);

		//Create a table from Sentences 
		WindowingCooccurrenceSentence wcs = new WindowingCooccurrenceSentence(sentNumber, covering, false, book);
		CooccurrenceTableSentence table = wcs.createTab();

		//Create the global Sentence graph
		String graphTitle = "evaluationGraph";
		return GraphCreator.createGraph(table, graphTitle, false, true);
	}

	/**
     * Create a graph directly from an manually annotated xml file (mostly used for performances purposes)
	 * 
	 * @param evaluationFilePath the path to the xml file
	 * @param dbScanDist the distance used for the dbscan algorithm (should be between 0 and 1, 0.40 is a good value overall)
	 * @param sentNumber size of the window in sentences
	 * @param covering covering between two windows in sentences
	 * @return The graph built
    */
    public static Graph buildCoOcSentFromXml(String referenceFilePath, String evaluationFilePath, int sentNumber, int covering) throws IOException {

		//Creating the document
		FileInputStream is = new FileInputStream(evaluationFilePath);
		String content = IOUtils.toString(is, StandardCharsets.UTF_8);

		//normalizing the text
		content = TextNormalization.addDotEndOfLine(content);

		//creating stanford's pipeline for the book creation
		String prop="tokenize,ssplit";

		Properties props = new Properties();
		props.setProperty("annotators",prop);
		
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		ImpUtils.setDocument(document);
		pipeline.annotate(document);

		//Making corefChains from XML
		ClusterContainer tempCorefChains = ClusterContainer.buildClusterContainerFromXML(referenceFilePath, document);

		List<List<CustomCorefChain>> tmpClusters = new LinkedList<>();

		for (int i = 0; i <= tempCorefChains.getLastCluster(); i++) {
			tmpClusters.add(new LinkedList<>());
			for (CustomCorefChain ccc : tempCorefChains.getCorefChains()) {
				if (ccc.getClusterID() == i){
					tmpClusters.get(i).add(ccc);
				}
			}
		}

		List<CustomCorefChain> finalCorefChains = new LinkedList<>();
		CorefChainFuser corefChainFuser = new CorefChainFuser();

		for (List<CustomCorefChain> cccList : tmpClusters){
			finalCorefChains.add(corefChainFuser.customCorefChainFusion(cccList));
		}

		//making the book
        Book book = CreateBook.createBook(document, false, finalCorefChains);

		//Create a table from Sentences 
		WindowingCooccurrenceSentence wcs = new WindowingCooccurrenceSentence(sentNumber, covering, false, book);
		CooccurrenceTableSentence table = wcs.createTab();

		//Create the global Sentence graph
		String graphTitle = "referenceGraph";
		return GraphCreator.createGraph(table, graphTitle, false, true);
    }







	
	//Tests
	
	public static void main(String[] args) throws IOException {
		//testCooccurrence();
		testInteraction();
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

		Graph g = GraphCreator.createGraph(cts,"graph_test_cooccurrence", false, true);
		g.graphMLPrinter("res/results");
		System.out.println(g.toString());
	}

	public static void testInteraction() throws IOException{
		DirectInteractionTable it = new DirectInteractionTable();
		it.add("A", "B", 0, "bad");
		it.add("A", "C", 0, "nice");
		it.add("C", "A", 0, "nice");
		it.add("C", "B", 0, "bad");
		it.add("B", "A", 1, "bad");
		it.add("C", "A", 1, "nice");
		it.add("C", "A", 1, "bad");

		DirectInteractionTable itNull = new DirectInteractionTable();
		itNull.add("A", "B", 0);
		itNull.add("A", "C", 0);
		itNull.add("C", "A", 0);
		itNull.add("C", "B", 0);
		itNull.add("B", "A", 1);
		itNull.add("C", "A", 1);
		itNull.add("C", "A", 1);

		it.display();
		itNull.display();

		Graph g = GraphCreator.createGraph(itNull, "graph_test_interaction", true, true);
		g.graphMLPrinter("res/results");
		System.out.println(g.toString());
	}
}

