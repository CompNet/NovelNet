package novelnet.pipeline;

import java.util.List;
import java.util.Properties;

import novelnet.table.CooccurrenceTableParagraph;
import novelnet.table.CooccurrenceTableSentence;
import novelnet.table.InteractionTable;
import novelnet.util.CustomCorefChain;
import novelnet.util.NullDocumentException;
import novelnet.book.Book;
import novelnet.book.Chapter;
import novelnet.book.Paragraph;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;


/**
 * Generic class for the creation of dynamic graphs.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
*/
public abstract class WindowingDynamicGraph {

	/**
	 * the Book created from the original text.
	*/ 
	protected Book book;
	/**
	 * the co-occurrence table you want to create the dynamic graphs from.
	*/ 
	protected InteractionTable interactionTable;

	/**
	 * Class Constructor
	 * 
	 * @param book the Book created from the original text.
	 * @param cooccurrenceTable the co-occurrence table you want to create the dynamic graphs from.
	*/
	protected WindowingDynamicGraph(Book book, InteractionTable interactionTable) {
		this.book = book;
		this.interactionTable = interactionTable;
    }
	
	public Book getBook() {
		return this.book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public InteractionTable getCooccurrenceTable() {
		return this.interactionTable;
	}

	public void setCooccurrenceTable(InteractionTable interactionTable) {
		this.interactionTable = interactionTable;
	}

	@Override
	public String toString() {
		return "{" +
			" book='" + getBook() + "'" +
			"}";
	}

	public List<InteractionTable> dynamicTableSentences(int size, int covering){
		return null;
	}
	
	public List<InteractionTable> dynamicTableParagraphs(int size, int covering){
		return null;
	}

	public List<InteractionTable> dynamicTableChapters(int size, int covering){
		return null;
	}

	private static void testGeneratingTableForDynamicGraphsFromParagraphsToSentence() throws NullDocumentException{
		Properties props = new Properties();
    	props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// make an example document
		CoreDocument document = new CoreDocument("Joe Smith is from Seattle. His friend Sara Jackson is from Washigton. She is an accountant for Bill Farmer. This is a buffering.");
		
		// annotate the document
		pipeline.annotate(document);

		// CorefChain Fusion
		List<CustomCorefChain> cccList = CustomCorefChainCreator.makeCustomCorefChains(document);

		CorefChainFuser corefChainFuser = new CorefChainFuser();
		cccList = corefChainFuser.corefChainsClusteringRO(cccList, 0.4);

		// manual book creation
		Book book = new Book(document, cccList);
		WindowingCooccurrenceParagraph wcp = new WindowingCooccurrenceParagraph(2, 1, false, book);

		Paragraph p1 = new Paragraph();
		p1.addSentence(document.sentences().get(0));
		p1.setBeginingSentence(0);
		p1.setEndingSentence(0);
		p1.setParagraphNumber(0);

		Paragraph p2 = new Paragraph();
		p2.addSentence(document.sentences().get(1));
		p2.setBeginingSentence(1);
		p2.setEndingSentence(1);
		p2.setParagraphNumber(1);

		Paragraph p3 = new Paragraph();
		p3.addSentence(document.sentences().get(2));
		p3.setBeginingSentence(2);
		p3.setEndingSentence(2);
		p3.setParagraphNumber(2);

		Paragraph p4 = new Paragraph();
		p4.addSentence(document.sentences().get(3));
		p4.setBeginingSentence(3);
		p4.setEndingSentence(3);
		p4.setParagraphNumber(3);

		Chapter c1 = new Chapter();
		c1.addParagraph(p1);
		c1.addParagraph(p2);

		Chapter c2 = new Chapter();
		c2.addParagraph(p3);
		c2.addParagraph(p4);
		
		book.addChapter(c1);
		book.addChapter(c2);

		//book display
		System.out.println("---");
		book.placeEntitites();
		book.display();

		//table display
		System.out.println("\n--- table co-occurrence Paragraph without chapter limitation ---\n");
		CooccurrenceTableParagraph table = wcp.createTab();
		table.display();

		WindowingDynamicGraphFromParagraphTable dgp = new WindowingDynamicGraphFromParagraphTable(book, table);

		int cpt =0;
		for (InteractionTable t : dgp.dynamicTableSentences(2, 1)){
			System.out.println("\n--- table co-occurrence from Paragraph to Sentences "+ cpt + " ---\n");
			cpt++;
			t.display();
		}

	}

	public static void testGeneratingTableForDynamicGraphsFromParagraphsToParagraphs() throws NullDocumentException{
		Properties props = new Properties();
    	props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// make an example document
		CoreDocument document = new CoreDocument("Joe Smith is from Seattle. His friend Sara Jackson is from Washigton. She is an accountant for Bill Farmer. This is a buffering.");
		
		// annotate the document
		pipeline.annotate(document);

		// CorefChain Fusion
		List<CustomCorefChain> cccList = CustomCorefChainCreator.makeCustomCorefChains(document);

		CorefChainFuser corefChainFuser = new CorefChainFuser();
		cccList = corefChainFuser.corefChainsClusteringRO(cccList, 0.4);

		// manual book creation
		Book book = new Book(document, cccList);
		WindowingCooccurrenceParagraph wcp = new WindowingCooccurrenceParagraph(2, 1, false, book);

		Paragraph p1 = new Paragraph();
		p1.addSentence(document.sentences().get(0));
		p1.setBeginingSentence(0);
		p1.setEndingSentence(0);
		p1.setParagraphNumber(0);

		Paragraph p2 = new Paragraph();
		p2.addSentence(document.sentences().get(1));
		p2.setBeginingSentence(1);
		p2.setEndingSentence(1);
		p2.setParagraphNumber(1);

		Paragraph p3 = new Paragraph();
		p3.addSentence(document.sentences().get(2));
		p3.setBeginingSentence(2);
		p3.setEndingSentence(2);
		p3.setParagraphNumber(2);

		Paragraph p4 = new Paragraph();
		p4.addSentence(document.sentences().get(3));
		p4.setBeginingSentence(3);
		p4.setEndingSentence(3);
		p4.setParagraphNumber(3);

		Chapter c1 = new Chapter();
		c1.addParagraph(p1);
		c1.addParagraph(p2);

		Chapter c2 = new Chapter();
		c2.addParagraph(p3);
		c2.addParagraph(p4);
		
		book.addChapter(c1);
		book.addChapter(c2);

		//book display
		System.out.println("---");
		book.placeEntitites();
		book.display();

		//table display
		System.out.println("\n--- table co-occurrence Paragraph without chapter limitation ---\n");
		CooccurrenceTableParagraph table = wcp.createTab();
		table.display();

		WindowingDynamicGraphFromParagraphTable dgp = new WindowingDynamicGraphFromParagraphTable(book, table);

		int cpt =0;
		for (InteractionTable t : dgp.dynamicTableParagraphs(2, 1)){
			System.out.println("\n--- table co-occurrence from Paragraph to Paragraphs "+ cpt + " ---\n");
			cpt++;
			t.display();
		}

	}

	public static void testGeneratingTableForDynamicGraphsFromParagraphsToChapters() throws NullDocumentException{
		Properties props = new Properties();
    	props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// make an example document
		CoreDocument document = new CoreDocument("Joe Smith is from Seattle. His friend Sara Jackson is from Washigton. She is an accountant for Bill Farmer. This is a buffering.");
		
		// annotate the document
		pipeline.annotate(document);

		// CorefChain Fusion
		List<CustomCorefChain> cccList = CustomCorefChainCreator.makeCustomCorefChains(document);

		CorefChainFuser corefChainFuser = new CorefChainFuser();
		cccList = corefChainFuser.corefChainsClusteringRO(cccList, 0.4);

		// manual book creation
		Book book = new Book(document, cccList);
		WindowingCooccurrenceParagraph wcp = new WindowingCooccurrenceParagraph(1, 0, false, book);

		Paragraph p1 = new Paragraph();
		p1.addSentence(document.sentences().get(0));
		p1.setBeginingSentence(0);
		p1.setEndingSentence(0);
		p1.setParagraphNumber(0);

		Paragraph p2 = new Paragraph();
		p2.addSentence(document.sentences().get(1));
		p2.setBeginingSentence(1);
		p2.setEndingSentence(1);
		p2.setParagraphNumber(1);

		Paragraph p3 = new Paragraph();
		p3.addSentence(document.sentences().get(2));
		p3.setBeginingSentence(2);
		p3.setEndingSentence(2);
		p3.setParagraphNumber(2);

		Paragraph p4 = new Paragraph();
		p4.addSentence(document.sentences().get(3));
		p4.setBeginingSentence(3);
		p4.setEndingSentence(3);
		p4.setParagraphNumber(3);

		Chapter c1 = new Chapter();
		c1.addParagraph(p1);
		c1.addParagraph(p2);

		Chapter c2 = new Chapter();
		c2.addParagraph(p3);
		c2.addParagraph(p4);
		
		book.addChapter(c1);
		book.addChapter(c2);

		//book display
		System.out.println("---");
		book.placeEntitites();
		book.display();

		//table display
		System.out.println("\n--- table co-occurrence Paragraph without chapter limitation ---\n");
		CooccurrenceTableParagraph table = wcp.createTab();
		table.display();

		WindowingDynamicGraphFromParagraphTable dgp = new WindowingDynamicGraphFromParagraphTable(book, table);

		int cpt =0;
		for (InteractionTable t : dgp.dynamicTableChapters(1, 0)){
			System.out.println("\n--- table co-occurrence from Paragraph to Chapters "+ cpt + " ---\n");
			cpt++;
			t.display();
		}

		cpt =0;
		for (InteractionTable t : dgp.dynamicTableChapters(2, 0)){
			System.out.println("\n--- table co-occurrence from Paragraph to Chapters "+ cpt + " ---\n");
			cpt++;
			t.display();
		}

	}

	public static void testGeneratingTableForDynamicGraphsFromSentenceToSentence() throws NullDocumentException{
		Properties props = new Properties();
    	props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// make an example document
		CoreDocument document = new CoreDocument("Joe Smith is from Seattle. His friend Sara Jackson is from Washigton. She is an accountant for Bill Farmer. This is a buffering.");
		
		// annotate the document
		pipeline.annotate(document);

		// CorefChain Fusion
		List<CustomCorefChain> cccList = CustomCorefChainCreator.makeCustomCorefChains(document);

		CorefChainFuser corefChainFuser = new CorefChainFuser();
		cccList = corefChainFuser.corefChainsClusteringRO(cccList, 0.4);

		// manual book creation
		Book book = new Book(document, cccList);
		WindowingCooccurrenceSentence wcs = new WindowingCooccurrenceSentence(2, 1, false, book);

		Paragraph p1 = new Paragraph();
		p1.addSentence(document.sentences().get(0));
		p1.setBeginingSentence(0);
		p1.setEndingSentence(0);
		p1.setParagraphNumber(0);

		Paragraph p2 = new Paragraph();
		p2.addSentence(document.sentences().get(1));
		p2.setBeginingSentence(1);
		p2.setEndingSentence(1);
		p2.setParagraphNumber(1);

		Paragraph p3 = new Paragraph();
		p3.addSentence(document.sentences().get(2));
		p3.setBeginingSentence(2);
		p3.setEndingSentence(2);
		p3.setParagraphNumber(2);

		Paragraph p4 = new Paragraph();
		p4.addSentence(document.sentences().get(3));
		p4.setBeginingSentence(3);
		p4.setEndingSentence(3);
		p4.setParagraphNumber(3);

		Chapter c1 = new Chapter();
		c1.addParagraph(p1);
		c1.addParagraph(p2);

		Chapter c2 = new Chapter();
		c2.addParagraph(p3);
		c2.addParagraph(p4);
		
		book.addChapter(c1);
		book.addChapter(c2);

		//book display
		System.out.println("---");
		book.placeEntitites();
		book.display();

		//table display
		System.out.println("\n--- table co-occurrence Sentence without chapter limitation ---\n");
		CooccurrenceTableSentence table = wcs.createTab();
		table.display();

		WindowingDynamicGraphFromSentenceTable dgs = new WindowingDynamicGraphFromSentenceTable(book, table);

		int cpt =0;
		for (InteractionTable t : dgs.dynamicTableSentences(2, 0)){
			System.out.println("\n--- table co-occurrence from Sentence to Sentence "+ cpt + " ---\n");
			cpt++;
			t.display();
		}

		cpt =0;
		for (InteractionTable t : dgs.dynamicTableSentences(3, 2)){
			System.out.println("\n--- table co-occurrence from Sentence to Sentence "+ cpt + " ---\n");
			cpt++;
			t.display();
		}

	}

	public static void testGeneratingTableForDynamicGraphsFromSentenceToParagraphs() throws NullDocumentException{
		Properties props = new Properties();
    	props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// make an example document
		CoreDocument document = new CoreDocument("Joe Smith is from Seattle. His friend Sara Jackson is from Washigton. She is an accountant for Bill Farmer. This is a buffering.");
		
		// annotate the document
		pipeline.annotate(document);

		// CorefChain Fusion
		List<CustomCorefChain> cccList = CustomCorefChainCreator.makeCustomCorefChains(document);

		CorefChainFuser corefChainFuser = new CorefChainFuser();
		cccList = corefChainFuser.corefChainsClusteringRO(cccList, 0.4);

		// manual book creation
		Book book = new Book(document, cccList);
		WindowingCooccurrenceSentence wcs = new WindowingCooccurrenceSentence(2, 1, false, book);

		Paragraph p1 = new Paragraph();
		p1.addSentence(document.sentences().get(0));
		p1.setBeginingSentence(0);
		p1.setEndingSentence(0);
		p1.setParagraphNumber(0);

		Paragraph p2 = new Paragraph();
		p2.addSentence(document.sentences().get(1));
		p2.setBeginingSentence(1);
		p2.setEndingSentence(1);
		p2.setParagraphNumber(1);

		Paragraph p3 = new Paragraph();
		p3.addSentence(document.sentences().get(2));
		p3.setBeginingSentence(2);
		p3.setEndingSentence(2);
		p3.setParagraphNumber(2);

		Paragraph p4 = new Paragraph();
		p4.addSentence(document.sentences().get(3));
		p4.setBeginingSentence(3);
		p4.setEndingSentence(3);
		p4.setParagraphNumber(3);

		Chapter c1 = new Chapter();
		c1.addParagraph(p1);
		c1.addParagraph(p2);

		Chapter c2 = new Chapter();
		c2.addParagraph(p3);
		c2.addParagraph(p4);
		
		book.addChapter(c1);
		book.addChapter(c2);

		//book display
		System.out.println("---");
		book.placeEntitites();
		book.display();

		//table display
		System.out.println("\n--- table co-occurrence Sentence without chapter limitation ---\n");
		CooccurrenceTableSentence table = wcs.createTab();
		table.display();

		WindowingDynamicGraphFromSentenceTable dgs = new WindowingDynamicGraphFromSentenceTable(book, table);

		int cpt =0;
		for (InteractionTable t : dgs.dynamicTableParagraphs(2, 0)){
			System.out.println("\n--- table co-occurrence from Sentence to Paragraphs "+ cpt + " ---\n");
			cpt++;
			t.display();
		}

		cpt =0;
		for (InteractionTable t : dgs.dynamicTableParagraphs(3, 2)){
			System.out.println("\n--- table co-occurrence from Sentence to Paragraphs "+ cpt + " ---\n");
			cpt++;
			t.display();
		}

	}

	public static void testGeneratingTableForDynamicGraphsFromSentenceToChapters() throws NullDocumentException{
		Properties props = new Properties();
    	props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// make an example document
		CoreDocument document = new CoreDocument("Joe Smith is from Seattle. His friend Sara Jackson is from Washigton. She is an accountant for Bill Farmer. This is a buffering.");
		
		// annotate the document
		pipeline.annotate(document);

		// CorefChain Fusion
		List<CustomCorefChain> cccList = CustomCorefChainCreator.makeCustomCorefChains(document);

		CorefChainFuser corefChainFuser = new CorefChainFuser();
		cccList = corefChainFuser.corefChainsClusteringRO(cccList, 0.4);

		// manual book creation
		Book book = new Book(document, cccList);
		WindowingCooccurrenceSentence wcs = new WindowingCooccurrenceSentence(2, 1, false, book);

		Paragraph p1 = new Paragraph();
		p1.addSentence(document.sentences().get(0));
		p1.setBeginingSentence(0);
		p1.setEndingSentence(0);
		p1.setParagraphNumber(0);

		Paragraph p2 = new Paragraph();
		p2.addSentence(document.sentences().get(1));
		p2.setBeginingSentence(1);
		p2.setEndingSentence(1);
		p2.setParagraphNumber(1);

		Paragraph p3 = new Paragraph();
		p3.addSentence(document.sentences().get(2));
		p3.setBeginingSentence(2);
		p3.setEndingSentence(2);
		p3.setParagraphNumber(2);

		Paragraph p4 = new Paragraph();
		p4.addSentence(document.sentences().get(3));
		p4.setBeginingSentence(3);
		p4.setEndingSentence(3);
		p4.setParagraphNumber(3);

		Chapter c1 = new Chapter();
		c1.addParagraph(p1);
		c1.addParagraph(p2);
		c1.setChapterNumber(0);

		Chapter c2 = new Chapter();
		c2.addParagraph(p3);
		c2.addParagraph(p4);
		c2.setChapterNumber(1);
		
		book.addChapter(c1);
		book.addChapter(c2);

		//book display
		System.out.println("---");
		book.placeEntitites();
		book.display();

		//table display
		System.out.println("\n--- table co-occurrence Sentence without chapter limitation ---\n");
		CooccurrenceTableSentence table = wcs.createTab();
		table.display();

		WindowingDynamicGraphFromSentenceTable dgs = new WindowingDynamicGraphFromSentenceTable(book, table);

		int cpt =0;
		for (InteractionTable t : dgs.dynamicTableChapters(2, 0)){
			System.out.println("\n--- table co-occurrence from Sentence to Chapters "+ cpt + " ---\n");
			cpt++;
			t.display();
		}

		cpt =0;
		for (InteractionTable t : dgs.dynamicTableChapters(1, 0)){
			System.out.println("\n--- table co-occurrence from Sentence to Chapters "+ cpt + " ---\n");
			cpt++;
			t.display();
		}

	}


	public static void main(String[] args) throws NullDocumentException { 
		testGeneratingTableForDynamicGraphsFromParagraphsToSentence();
		/*testGeneratingTableForDynamicGraphsFromParagraphsToParagraphs();
		testGeneratingTableForDynamicGraphsFromParagraphsToChapters();
		testGeneratingTableForDynamicGraphsFromSentenceToSentence();
		testGeneratingTableForDynamicGraphsFromSentenceToParagraphs();
		testGeneratingTableForDynamicGraphsFromSentenceToChapters();*/

	}
}