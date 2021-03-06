package novelnet.pipeline;

import java.util.LinkedList;
import java.util.List;

import novelnet.book.Book;
import novelnet.table.InteractionTable;

/**
 * Create dynamic graphs from a CooccurrenceTable where the window dimension is in Sentence.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
*/
public class WindowingDynamicGraphFromSentenceTable extends WindowingDynamicGraph {

	/**
	 * Class Constructor
	 * 
	 * @param book the Book created from the original text.
	 * @param interactionTable the co-occurrence table you want to create the dynamic graphs from.
	*/
    public WindowingDynamicGraphFromSentenceTable(Book book, InteractionTable interactionTable) {
		super(book, interactionTable);
    }

	/**
	 * Create a list of interactionTable to make graphs from.
	 * 
	 * @param size size of the window (in sentences) used create the dynamic graphs.
	 * @param covering size of the covering between 2 windows. Set to 0 for sequential graphs.
	*/
	@Override
	public List<InteractionTable> dynamicTableSentences(int size, int covering){
		List<InteractionTable> result = new LinkedList<>();
		boolean done = false;
		boolean searchingEnd = false;
		boolean whileEnd;
		int i;
		int begin = 0;
		int cpt = 0;
		int windowBegin = 0;
		int windowEnd = 0;
		int dynamicCpt = 0;
		int dynamicGraphBegin = 0;
		int dynamicGraphEnd = 0;
		while(!done){
			i = 0;
			whileEnd = false;
			dynamicGraphBegin = dynamicCpt*size - dynamicCpt*covering;
			dynamicGraphEnd = (dynamicCpt + 1)*size - dynamicCpt*covering -1;
			while( i < interactionTable.getListCharA().size() && !whileEnd){
				windowBegin = interactionTable.getListBeginingWindow().get(i);
				windowEnd = interactionTable.getListEndingWindow().get(i);
				if ((windowBegin+windowEnd)/2 >= dynamicGraphBegin){
					if( (windowBegin+windowEnd)/2 <= dynamicGraphEnd){
						if (searchingEnd) {
							cpt ++;
						}
						else {
							searchingEnd = true;
							begin = i;
						}
						if (i == interactionTable.getListCharA().size()-1){
							result.add(interactionTable.subTable(begin, i));
							done = true;
						}
					}
					else{
						if (searchingEnd){
							result.add(interactionTable.subTable(begin, begin+cpt));
							searchingEnd = false;
							cpt = 0;
						}
						whileEnd = true;
					}
				}
				i++;
			}
			dynamicCpt++;
		}
		return result;
	}
	
	/**
	 * Create a list of interactionTable to make graphs from.
	 * 
	 * @param size size of the window (in Paragraphs) used create the dynamic graphs.
	 * @param covering size of the covering between 2 windows. Set to 0 for sequential graphs.
	*/
	@Override
	public List<InteractionTable> dynamicTableParagraphs(int size, int covering){
		List<InteractionTable> result = new LinkedList<>();
		boolean done = false;
		boolean searchingEnd = false;
		boolean whileEnd;
		int i;
		int begin = 0;
		int cpt = 0;
		int windowBegin = 0;
		int windowEnd = 0;
		int dynamicCpt = 0;
		int dynamicGraphBegin = 0;
		int dynamicGraphEnd = 0;
		while(!done){
			i = 0;
			whileEnd = false;
			dynamicGraphBegin = book.getBeginIndexOfParagraph(dynamicCpt*size - dynamicCpt*covering);
			dynamicGraphEnd = book.getEndIndexOfParagraph((dynamicCpt + 1)*size - dynamicCpt*covering -1);
			if (dynamicGraphEnd==-1) dynamicGraphEnd = book.getEndIndexOfParagraph(book.getEndingParagraphNumber());
			while( i < interactionTable.getListCharA().size() && !whileEnd){
				windowBegin = interactionTable.getListBeginingWindow().get(i);
				windowEnd = interactionTable.getListEndingWindow().get(i);
				if ((windowBegin+windowEnd)/2 >= dynamicGraphBegin){
					if( (windowBegin+windowEnd)/2 <= dynamicGraphEnd){
						if (searchingEnd) {
							cpt ++;
						}
						else {
							searchingEnd = true;
							begin = i;
						}
						if (i == interactionTable.getListCharA().size()-1){
							result.add(interactionTable.subTable(begin, i));
							done = true;
						}
					}
					else{
						if (searchingEnd){
							result.add(interactionTable.subTable(begin, begin+cpt));
							searchingEnd = false;
							cpt = 0;
						}
						whileEnd = true;
					}
				}
				i++;
			}
			dynamicCpt++;
		}
		return result;
	}

	/**
	 * Create a list of interactionTable to make graphs from.
	 * 
	 * @param size size of the window (in Chapters) used create the dynamic graphs.
	 * @param covering size of the covering between 2 windows. Set to 0 for sequential graphs.
	*/
	@Override
	public List<InteractionTable> dynamicTableChapters(int size, int covering){
		List<InteractionTable> result = new LinkedList<>();
		boolean done = false;
		boolean searchingEnd = false;
		boolean whileEnd;
		int i;
		int begin = 0;
		int cpt = 0;
		int windowBegin = 0;
		int windowEnd = 0;
		int dynamicCpt = 0;
		int dynamicGraphBegin = 0;
		int dynamicGraphEnd = 0;
		while(!done){
			i = 0;
			whileEnd = false;
			dynamicGraphBegin = book.getBeginIndexOfChapter(dynamicCpt*size - dynamicCpt*covering);
			dynamicGraphEnd = book.getEndIndexOfChapter((dynamicCpt + 1)*size - dynamicCpt*covering -1);
			if (dynamicGraphEnd==-1) dynamicGraphEnd = book.getEndingSentenceIndex();
			while( i < interactionTable.getListCharA().size() && !whileEnd){
				windowBegin = interactionTable.getListBeginingWindow().get(i);
				windowEnd = interactionTable.getListEndingWindow().get(i);
				if ((windowBegin+windowEnd)/2 >= dynamicGraphBegin){
					if( (windowBegin+windowEnd)/2 <= dynamicGraphEnd){
						if (searchingEnd) {
							cpt ++;
						}
						else {
							searchingEnd = true;
							begin = i;
						}
						if (i == interactionTable.getListCharA().size()-1){
							result.add(interactionTable.subTable(begin, i));
							done = true;
						}
					}
					else{
						if (searchingEnd){
							result.add(interactionTable.subTable(begin, begin+cpt));
							searchingEnd = false;
							cpt = 0;
						}
						whileEnd = true;
					}
				}
				i++;
			}
			dynamicCpt++;
		}
		return result;
	}
}
