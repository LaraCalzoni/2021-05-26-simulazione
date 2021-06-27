package it.polito.tdp.yelp.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private SimpleDirectedWeightedGraph <Business, DefaultWeightedEdge> grafo;
	private YelpDao dao;
	private Map <String, Business> idMap;
	
	public Model() {
		dao = new YelpDao();
		idMap = new HashMap<>();
		dao.getAllBusiness(idMap);
		
	}
	
	public void creaGrafo (int anno, String citta) {
		
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		 //aggiungo vertici
		Graphs.addAllVertices(grafo, this.dao.getVertici(anno, citta, idMap));
		
		//aggiungo archi
		for(Adiacenza aa : dao.getAdiacenze(anno, citta, idMap)) {
			
			//se la media di b1 è maggiore di quella di b2, l’arco sarà direzionato da b2 verso b1;
			 if(aa.getB1().getMedia()>aa.getB2().getMedia()) {
				Graphs.addEdgeWithVertices(grafo,aa.getB2(),aa.getB1(), Math.abs(aa.getDifferenza()));
			}
			 
			//se la media di b2 è maggiore di quella di b1, l’arco sarà direzionato da b1 verso b2;
			 if(aa.getB2().getMedia()>aa.getB1().getMedia()) {
				Graphs.addEdgeWithVertices(grafo,aa.getB1(),aa.getB2(), Math.abs(aa.getDifferenza()));
			}
		}
		
		System.out.println("grafo creato!"+grafo.vertexSet().size()+" "+grafo.edgeSet().size());
		
		
	}
	
	public Collection <Business> getTuttiBusiness(){
		return this.idMap.values();
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List <String> getCitta(){
		return dao.getCitta();
	}
	/*
	 Trovare nel grafo e stampare a video il locale commerciale migliore dove trascorrere la serata.
	 Tale locale è definito come il vertice del grafo per cui la somma dei pesi dei suoi archi entranti
	 meno la somma del peso dei suoi archi uscenti sia massima
	 */
	public Business getMigliore(int anno, String citta) {
		double sommaEntranti = 0.0;
		double sommaUscenti = 0.0;
		double somma = 0.0;
		Business migliore = null;
		for(Business b : this.grafo.vertexSet() ) {
			for(DefaultWeightedEdge e : grafo.incomingEdgesOf(b)) {
				sommaEntranti+= grafo.getEdgeWeight(e);
			}
			for(DefaultWeightedEdge e : grafo.outgoingEdgesOf(b)) {
				sommaUscenti+= grafo.getEdgeWeight(e);
			}
			b.setSommaPesi(sommaEntranti-sommaUscenti);
		}
		
		for(Business b : this.grafo.vertexSet() ) {
			if(b.getSommaPesi()>somma) {
				migliore = b;
				somma = b.getSommaPesi();
			}
		}
	 return migliore;	
	}
}
