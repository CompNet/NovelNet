package implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;

/**
 * @author Quay Baptiste, Lemaire Tewis
 *
 */
public class WindowingCooccurence implements RelationshipExtractionMethod {
	
	CoreDocument document;
	boolean ponderation;
	int windowSize;
	int coveringSize;
	Graph graph;

	/*
	*  paragraph
	*  sentence
	*/
	String optionSize;

	/*
	* SEQUENTIAL
	* SMOOTHING
	*/
	boolean sequentiel;
	 
	public WindowingCooccurence()
	{
		 
	}
	/**
	* 
	* @param document
	* @param graph
	* @param ponderation
	* @param optionSize
	* @param type
	* @param windowSize
	* @param coveringSize
	*/
	public WindowingCooccurence(CoreDocument document,Graph graph, boolean ponderation, String optionSize, boolean sequentiel, int windowSize, int coveringSize)
	{
		this.document = document;
		this.ponderation = ponderation;
		this.windowSize = windowSize;
		this.coveringSize = coveringSize;
		this.optionSize = optionSize;
		this.sequentiel = sequentiel;
		this.graph = graph;
	}

	@Override
	public void mainWork() {
		System.out.println(" ");
		System.out.println("MAIN WORK");

		if ("SENTENCE".equals(optionSize)){
			System.out.println("SENTENCE !");
			corefSent();
		}/*
		if ("PARAGRAPH".equals(optionSize)){
			System.out.println("PARAGRAPH ! ");
			corefParagraph();
		}*/
		
		
		System.out.println("GRAPH :");
		System.out.println(this.graph.toString());
		System.out.println("fin");
		

	}
	
	/**
	 * fenetrage (echelle: phrase) avec ponderation selon option.
	 * 
	 * @author Quay Baptiste, Lemaire Tewis
	 */
	private void corefSent(){
		List<CoreSentence> sentences = document.sentences();
		Map<String,Node> charMap = new HashMap<String, Node>();		
		List<Edge> linkList = new ArrayList<Edge>();
		// glissage de la fenêtre pour toute les phrases du document
		int i=0;
		do
		{
			// intervalle de la fenêtre.
			for (int j=i; j<i+this.windowSize; j++)
			{
				// débordement de fenetre à la fin.
				if (j>=sentences.size()) continue;
				
				// Pour tous les token de la phrase
				for (CoreLabel token : sentences.get(j).tokens())
				{
					// Si c'est une personne
					if (token.ner().equals("PERSON"))
					{
						CorefChain corefEntity = impUtils.corefByToken(document.corefChains(),token);
						
						// si il n'a pas de coréférence on ajoute un neud.
						if (corefEntity == null)
						{
							Node n = new Node(token.word(),token.word(),0);
							charMap.put(n.id, n);
						}else // sinon,
						{
							// on récupère une mention valide de la chaine (prévient de certaines erreurs possible de coref)
							CorefMention mentionV = impUtils.valideRepresentativeMention(corefEntity,document);
							
							// on vérifie s'il éxiste pour augmenter le poid de l'objet éxistant
							if (mentionV!=null)
							{
								if (charMap.containsKey(mentionV.mentionSpan))
								{
									if (this.ponderation)
										charMap.get(mentionV.mentionSpan).addWeight(1);
								}else
								{
									Node n = new Node(mentionV.mentionSpan,mentionV.mentionSpan,0);
									charMap.put(n.id, n);
								}
							}
							
						}
						
					}
					else if (token.ner().equals("O")){	// si ce n'est pas une entité nommé
						CorefChain corefEntity = impUtils.corefByToken(document.corefChains(),token);
						// si il a une coréférence
						if (corefEntity!=null)
						{
							CorefMention mentionV = impUtils.valideRepresentativeMention(corefEntity,document);
							if (mentionV!=null)
							{
								if (charMap.containsKey(mentionV.mentionSpan))
								{
									if (this.ponderation)
										charMap.get(mentionV.mentionSpan).addWeight(1);
								}else
								{
								Node n = new Node(mentionV.mentionSpan,mentionV.mentionSpan,0);
								charMap.put(n.id, n);
								}
							}
							
						}
					}
				}
			}
			//System.out.println("Phrase: "+i);
			//	System.out.println(sentences.get(i).text());
			//	System.out.println("--------------");
			
			//		System.out.println(charMap.toString());
			
			// une fois les noeuds de la fenêtre ajoutés, on crée les arcs entres eux.
			for (Node nodeL : charMap.values())
			{
				for (Node nodeR : charMap.values())
				{
					if (nodeL.equals(nodeR)) continue;
					Edge edge = new Edge(nodeL.id,nodeL,nodeR,false,nodeL.weight+nodeR.weight);
					if (!linkList.contains(edge)&&!containInverseLink(linkList,edge))
					{
						linkList.add(edge);
					}
				}
			}
			
			// on ajoute les noeuds au graph.
			for (Node n : charMap.values())
				this.graph.addNode(n);
			
			// on ajoute les arcs.
			for (Edge e : linkList)
			{
				//System.out.println("EDGE:");
				this.graph.addEdgeWithPonderation(e);
				//System.out.println(this.graph.edgeMap.toString());
			}
			
			//System.out.println("EDGE:");
			//System.out.println(linkList.toString());
			
			// on éfface les objet pour la prochaine fenêtre.
			charMap.clear();
			linkList.clear();
		
			//	System.out.println("COREFCHAINS:");
			//	System.out.println((document.corefChains().toString()));
		
			if (sequentiel) i+=this.windowSize;
			else i+=(this.windowSize-this.coveringSize);
		}while (i<sentences.size());
		//System.out.println("--------------------------------*********//////////////");
		//System.out.println(this.graph.edgeMap.toString());
		
	}
	
	
	private boolean containInverseLink(List<Edge> l, Edge e)
	{
		for (Edge el : l)
		{
			if (el.nodeLeft.equals(e.nodeRight)&&el.nodeRight.equals(e.nodeLeft))
				return true;
		}
		return false;
	}
	
	

}
