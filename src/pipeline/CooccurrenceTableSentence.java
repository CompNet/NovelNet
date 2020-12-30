package pipeline;

/**
 * A class wich represent a table where all the co-occurrences are stocked.
 * Each list represent a column of the table.
 * The indexes of begining and ending of windows are in Sentences.
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
 */
public class CooccurrenceTableSentence extends CooccurrenceTable {

     /**
	 * Constructor for the class WindowingCooccurrenceSentence.
	 * 
	 */
	public CooccurrenceTableSentence() {
        super();
    }

    /**
	 * Constructor for the class WindowingCooccurrenceSentence specifying the window's size.
	 * 
	 * @param windowSize
	 */
	public CooccurrenceTableSentence(int windowSize) {
        super();
		this.windowSize = windowSize;
    }
    
    @Override
    public CooccurrenceTable subTable(int begin, int end){
		CooccurrenceTable result = new CooccurrenceTableSentence();
		for (int i = begin; i <= end; i++){
			result.add(listCharA.get(i), listCharB.get(i), listDistanceChar.get(i), listDistanceWord.get(i), listBeginingWindow.get(i), listEndingWindow.get(i));
		}
		return result;
    }
    
    /**
	 * Displays the table
	 * 
	 * 
	 */
    @Override
	public void display(){
		for (int i = 0; i < listCharA.size(); i++){ // For the size of the list of character A
			System.out.print("Char A : "+listCharA.get(i)); // Prints the list of character A
			System.out.print("\t| Char B : "+listCharB.get(i)); // Prints the list of character B   
			System.out.print("\t| Dist letter : "+listDistanceChar.get(i)); // Prints the list of distance in characters      
			System.out.print("\t| Dist words : "+listDistanceWord.get(i)); // Prints the list of distance in words 
			System.out.print(" | window begin (s) : "+listBeginingWindow.get(i)); 
			System.out.println(" | window end (s) : "+listEndingWindow.get(i));
		}
	}
    
}
