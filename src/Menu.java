import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

import book.Book;
import book.CreateBook;
import book.TextNormalization;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import pipeline.CooccurrenceTable;
import pipeline.CooccurrenceTableParagraph;
import pipeline.CooccurrenceTableSentence;
import pipeline.GraphCreator;
import pipeline.WindowingCooccurrenceParagraph;
import pipeline.WindowingCooccurrenceSentence;
import pipeline.WindowingDynamicGraphFromParagraphTable;
import pipeline.WindowingDynamicGraphFromSentenceTable;
import util.EntityMention;

/**
 * @author Quay Baptiste, Lemaire Tewis
 *
 */
public class Menu {

	/**
	 * @param args
	 * @author Quay Baptiste, Lemaire Tewis
	 * @throws IOException 
	*/
	public static void main(String[] args) throws IOException {
		if (args.length == 0)
		{
			
			Scanner sc = new Scanner(System.in);
			System.out.println("saisir chemin du fichier à traiter:");
			String path = sc.nextLine();

			FileInputStream is = new FileInputStream(path);
			String content = IOUtils.toString(is, StandardCharsets.UTF_8);

			TextNormalization adapt = new TextNormalization(content);
			adapt.addDotEndOfLine();
			content = adapt.getText();

			//String prop="tokenize,ssplit";
			String prop="tokenize,ssplit,pos,lemma,ner,parse,coref";
			System.out.println("les annotateurs séléctionés sont: "+prop);

			Properties props = new Properties();
			props.setProperty("annotators",prop);
			props.setProperty("ner.applyFineGrained", "false");

			StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
			CoreDocument document = new CoreDocument(content);
			pipeline.annotate(document);
			//Annotation annotation = document.annotation();
			// print the result of the annotation in a file
			/*PrintWriter out = new PrintWriter("res/results/"+path.substring(11, path.length()-4)+"_output.txt");
			pipeline.prettyPrint(annotation, out );*/

			Book book = CreateBook.createBook(document, false);

			GraphCreator c = new GraphCreator();

			//Create a table from Sentences 
			WindowingCooccurrenceSentence wcs = new WindowingCooccurrenceSentence(20, 3, false, book);
			CooccurrenceTableSentence table = wcs.createTab();

			//Create the global Sentence graph
			c.createGraph(table, true, "graph_"+path.substring(11, path.length()-4)).graphMLPrinter("res/results");
			
			//Create a dynamic graph sequence from sentences table with Sentence window.
			WindowingDynamicGraphFromSentenceTable dgs = new WindowingDynamicGraphFromSentenceTable(book, table);
			int cpt = 0;
			for (CooccurrenceTable t : dgs.dynamicTableParagraphs(10, 3)){
				cpt++;
				c.createGraph(t, true, "graph_"+path.substring(11, path.length()-4)+"_Paragraphs_"+cpt).graphMLPrinter("res/results");
			}

			cpt = 0;
			for (CooccurrenceTable t : dgs.dynamicTableChapters(1, 0)){
				cpt++;
				c.createGraph(t, true, "graph_"+path.substring(11, path.length()-4)+"_Chapters_"+cpt).graphMLPrinter("res/results");
			}

			

			//Create a table from Paragraphs
			/*WindowingCooccurrenceParagraph wcp = new WindowingCooccurrenceParagraph(5, 1, false, book);
			//List<List<EntityMention>> tmp = wcp.createWindow(document);
			//System.out.println(tmp);
			CooccurrenceTableParagraph tableP = wcp.createTab(document);

			FileWriter fileWriter = new FileWriter("res/results/"+path.substring(11, path.length()-4)+"_bookClass.txt");
			book.printToFile(fileWriter);
			fileWriter.close();

			//Create the global Paragraphs graph
			c.createGraph(tableP, true, "graph_"+path.substring(11, path.length()-4)).graphMLPrinter("res/results");
				
			//Create a dynamic graph sequence from Paragraphs table with Paragraphs window.
			WindowingDynamicGraphFromParagraphTable dgp = new WindowingDynamicGraphFromParagraphTable(book, tableP);
			int cpt = 0;
			for (CooccurrenceTable t : dgp.dynamicTableSentences(20,3)){
				cpt++;
				c.createGraph(t, true, "graph_"+path.substring(11, path.length()-4)+"_"+cpt).graphMLPrinter("res/results");
			}*/
			

			// print book object in a file
			/*FileWriter fileWriter = new FileWriter("res/results/"+path.substring(11, path.length()-4)+"_bookClass.txt");
			book.printToFile(fileWriter);
			fileWriter.close();*/
			
			sc.close();
		}


	}

}