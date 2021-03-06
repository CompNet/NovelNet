package performance;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import novelnet.pipeline.TextNormalization;
import novelnet.util.ImpUtils;

/**
 * a tool to manually annotate texts with StanfordCoreNLP tokenization and sentence splitting
 * 
 * @author Quay Baptiste
 * @author Lemaire Tewis
*/
public class AnnotationTool {

    /**
     *  Class Constructor
    */
    private AnnotationTool(){
        
    }
    
    /**
     * actual function to split the text into sentences and then tokens and display the result,
    */
    public static void decompose(String path, String language, int sentenceJump, Boolean dispPunctuation) throws IOException{
        FileInputStream is = new FileInputStream(path);
        String content = IOUtils.toString(is, StandardCharsets.UTF_8);

        content = TextNormalization.preProcess(content, "fr");

        System.out.println("decomposition for analyse.");
        Properties props = new Properties();
        if (language == "fr" ) props = ImpUtils.getFrenchProperties();
        props.setProperty("annotators", "tokenize,ssplit");
        
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        CoreDocument document = new CoreDocument(content);
        pipeline.annotate(document);
        Scanner sc = new Scanner(System.in);
        for (int i = 0; i < document.sentences().size(); i++){
            if (i != 0 && i%sentenceJump == 0){
                System.out.println(" press enter to continue ");
                sc.nextLine();
            }
            System.out.println("Sentence number "+(i+1));
            for (CoreLabel token : document.sentences().get(i).tokens()){
                if (dispPunctuation || !ImpUtils.isPonctuation(token)){
                    System.out.println("Text : " + token.originalText() + "\t | Position : " + (document.sentences().get(i).tokens().lastIndexOf(token)+1));
                }
            }
            System.out.println();
        }
        sc.close();

    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("enter the language of the text (fr, en) : ");
        String language = sc.nextLine();
        System.out.println("enter the path to the file to analyse : ");
        String path = sc.nextLine();
        System.out.println("enter \"p\" for punctuation display anything else if you don't want punctuation to be displayed : ");
        String dispP = sc.nextLine();
        System.out.println("enter the number of sentences to show before asking to continue : ");
        int sentenceJump = sc.nextInt();

        sc.nextLine();
		AnnotationTool.decompose(path, language, sentenceJump, dispP.equals("p"));
        sc.close();
	}
}
