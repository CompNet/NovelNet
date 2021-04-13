package performance.ner;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.LinkedList;
import java.util.List;
import java.util.Comparator;
import java.util.Properties;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.apache.commons.io.IOUtils;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import novelnet.util.CustomEntityMention;

public class EntityContainer {

	/**
	 * the list of mentions in the container
	*/
    List<CustomEntityMention> entities;

 	/**
	 * Class Constructor with an empty list of mentions
	*/
    public EntityContainer() {
        entities = new LinkedList<>();
    }

	/**
	 * Creates a CorefChainContainer from a list of mentions
	 *
     * @param entities the list of mentions
	*/
    public EntityContainer(List<CustomEntityMention> entities) {
        this.entities = entities;
    }

	/**
	 * Build an EntityContainer from an .xml file
	 * 
	 * @param pathToXml path to the .xml file
     * @return the Container
	 */
    public static EntityContainer buildFromXml(String pathToXml) throws IOException{
        EntityContainer result = new EntityContainer();
		SAXBuilder builder = new SAXBuilder();
		FileInputStream is = new FileInputStream(pathToXml);     
	    try {
	    	Document document = (Document) builder.build(is);
	        Element rootNode = document.getRootElement();
	        List<Element> list = rootNode.getChildren("mention");
	        for (int i = 0; i < list.size(); i++) {
	        	Element node = (Element) list.get(i);
				if (node.getChildText("mentionType").equals("explicit")){
					result.entities.add(new CustomEntityMention(node.getChildText("text"), 
	        			Integer.parseInt(node.getChildText("sentence")), 
	        			Integer.parseInt(node.getChildText("start")),
	        			Integer.parseInt(node.getChildText("end"))));
				}
	        }
	    } catch (IOException io) {
	    	System.out.println(io.getMessage());
	    } catch (JDOMException jdomex) {
	    	System.out.println(jdomex.getMessage());
	    }
        return result;
	}

	/**
	 * Build an EntityContainer from a .txt file using Stanford CoreNLP
	 * 
	 * @param pathToText path to the .txt file
     * @return the Container
	 */
    public static EntityContainer buildFromTxt(String pathToText) throws IOException {
        EntityContainer result = new EntityContainer();
		FileInputStream is = new FileInputStream(pathToText);     
		String content = IOUtils.toString(is, "UTF-8");
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
		props.setProperty("ner.applyFineGrained", "false");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		CoreDocument document = new CoreDocument(content);
		pipeline.annotate(document);
		boolean person;
		for (CoreEntityMention e : document.entityMentions()){
			person = false;
			if (e.entityType().equals("PERSON")) {
				for(CoreLabel token : e.tokens()){
					if (token.ner().equals("PERSON")){
						person = true;
					}
				}
				if (person) result.entities.add(new CustomEntityMention(e));
			}
		}
		result.entities.sort(Comparator.comparing(CustomEntityMention::getSentenceNumber).thenComparing(CustomEntityMention::getWindowBegining));
        return result;
    }
	

    public List<CustomEntityMention> getEntities() {
        return this.entities;
    }

    public void setEntities(List<CustomEntityMention> entities) {
        this.entities = entities;
    }

    @Override
    public String toString() {
        return "{" +
            " entities='" + getEntities() + "'" +
            "}";
    }

    public void display(){
        System.out.print("{ ");
        for (CustomEntityMention ce : entities){
            System.out.print(ce.text() + ", ");
        }
        System.out.print(" }\n");
    }
}