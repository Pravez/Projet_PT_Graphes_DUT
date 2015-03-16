package data;

import java.awt.*;
import java.util.ArrayList;

/**
 * Class to manage the interactions between edges, vertexes and users. It possesses an UndoManager to
 * manage undo actions. It is associated to a file and has the different default settings for the vertexes
 * and the edges.
 */
public class Graph extends Observable {

    private ArrayList<GraphElement> elements;
    private String                  name;

    /**
     * Constructeur de la classe Graph
     */
    public Graph() {
        this.elements = new ArrayList<GraphElement>();
    }

    /**
     * Getter de la liste des GraphElement du Graph
     * @return la liste des GraphElement
     */
    public ArrayList<GraphElement> getGraphElements() {
        return this.elements;
    }

    /**
     * Setter de la liste des GraphElement du graph
     * @param elements la nouvelle liste de GraphElement
     */
    public void setGraphElements(ArrayList<GraphElement> elements) {
        this.elements = elements;
    }
    
    /**
     * Méthode permettant d'ajouter une liste de GraphElement au Graph
     * @param elements la liste de GraphElement à ajouter
     */
    public void addGraphElements(ArrayList<GraphElement> elements) {
    	this.elements.addAll(elements);
    	this.setChanged();
    }
    
    /**
     * Méthode statique pour copier des GraphElement dans une nouvelle ArrayList en créant de nouveaux Vertex et de nouveaux Edge
     * @param elements la liste des GraphElement à copier
     * @return la liste des GraphElement copiés
     */
    public static ArrayList<GraphElement> copyGraphElements(ArrayList<GraphElement> elements) {
    	ArrayList<GraphElement> new_elements = new ArrayList<GraphElement> (elements.size());
    	for (int i = 0 ; i < elements.size() ; i++) {
    		new_elements.add(null);
    	}
    	// on ajoute tous les sommets dans la liste des nouveaux GraphElement
    	for (int i = 0 ; i < elements.size() ; i++) {
    		if (elements.get(i).isVertex()) {
    			new_elements.set(i, new Vertex((Vertex) elements.get(i)));
    		}
    	}
    	// on ajoute les edges une fois que tous les sommets on été ajoutés dans la liste des nouveaux GraphElement
    	for (int i = 0 ; i < elements.size() ; i++) {
    		if (!elements.get(i).isVertex()) {
    			int origin      = elements.indexOf(((Edge)elements.get(i)).getOrigin());
    			int destination = elements.indexOf(((Edge)elements.get(i)).getDestination());
    			new_elements.set(i, new Edge(elements.get(i). getLabel(), elements.get(i).getColor(), (Vertex)new_elements.get(origin), (Vertex)new_elements.get(destination), ((Edge)elements.get(i)).getThickness()));
    		}
    	}
    	return new_elements;
    }
    
    /**
     * Méthode statique pour copier des GraphElement dans une nouvelle ArrayList en créant de nouveaux Vertex et de nouveaux Edge à une position donnée
     * @param elements la liste des GraphElement à copier
     * @param position la position du centre de la zone contenant les GraphElement
     * @return la liste des GraphElement copiés
     */
    public static ArrayList<GraphElement> copyGraphElements(ArrayList<GraphElement> elements, Point position) {
    	ArrayList<GraphElement> new_elements = new ArrayList<GraphElement> (elements.size());
    	Point min = new Point(0, 0);
    	Point max = new Point(0, 0);
    	for(GraphElement e : elements) {
    		if (e.isVertex()) {
    			min = new Point(((Vertex)e).getPosition());
    			max = new Point(min);
    			break;
    		}
    	}
        for (GraphElement element : elements) {
            new_elements.add(null);
            if (element.isVertex()) {
                min.x = ((Vertex) element).getPosition().x < min.x ? ((Vertex) element).getPosition().x : min.x;
                min.y = ((Vertex) element).getPosition().y < min.y ? ((Vertex) element).getPosition().y : min.y;
                max.x = ((Vertex) element).getPosition().x > max.x ? ((Vertex) element).getPosition().x : max.x;
                max.y = ((Vertex) element).getPosition().y > max.y ? ((Vertex) element).getPosition().y : max.y;
            }
        }
    	// on ajoute tous les sommets dans la liste des nouveaux GraphElement
    	for (int i = 0 ; i < elements.size() ; i++) {
    		if (elements.get(i).isVertex()) {
    			new_elements.set(i, new Vertex((Vertex) elements.get(i)));
    			int new_min_x = position.x - (max.x - min.x)/2;
    			int new_min_y = position.y - (max.y - min.y)/2;
    			((Vertex)new_elements.get(i)).move(new_min_x - min.x, new_min_y - min.y);
    		}
    	}
    	// on ajoute les edges une fois que tous les sommets on été ajoutés dans la liste des nouveaux GraphElement
    	for (int i = 0 ; i < elements.size() ; i++) {
    		if (!elements.get(i).isVertex()) {
    			int origin      = elements.indexOf(((Edge)elements.get(i)).getOrigin());
    			int destination = elements.indexOf(((Edge)elements.get(i)).getDestination());
    			new_elements.set(i, new Edge(elements.get(i). getLabel(), elements.get(i).getColor(), (Vertex)new_elements.get(origin), (Vertex)new_elements.get(destination), ((Edge)elements.get(i)).getThickness()));
    		}
    	}
    	return new_elements;
    }

    /**
     * Getter de la liste des Edge du Graph
     * @return la liste des Edge
     */
    public ArrayList<Edge> getEdges() {
    	ArrayList<Edge> edges = new ArrayList<Edge>();
    	for(GraphElement element : this.elements) {
    		if (!element.isVertex()) {
    			edges.add((Edge) element);
    		}
    	}
        return edges;
    }

    /**
     * Getter de la liste des Vertex du Graph
     * @return la liste des Vertex
     */
    public ArrayList<Vertex> getVertexes() {
    	ArrayList<Vertex> vertexes = new ArrayList<Vertex>();
    	for(GraphElement element : this.elements) {
    		if (element.isVertex()) {
    			vertexes.add((Vertex) element);
    		}
    	}
        return vertexes;
    }

    /**
     * Getter du nom du Graph
     * @return le nom du Graph
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter du nom du Graph
     * @param name le nouveau nom du Graph
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Méthode pour ajouter un Vertex au Graph
     * @param color la couleur du Vertex à créer
     * @param position la position du Vertex à créer
     * @param size la taille du Vertex à créer
     * @param shape la forme du Vertex à créer
	 * @return vertex copie du vertex destinée au UndoPanel
     */
	public Vertex createVertex(Color color, Point position, int size, Vertex.Shape shape) {
		Vertex vertex = new Vertex(color, position, size, shape);

		this.elements.add(vertex);
		this.setChanged();
		return vertex;
	}
    /**
     * Creates an edge between two vertexes
     * @param color la couleur de l'Edge à créer
     * @param origin le Vertex d'origine de l'Edge à créer
     * @param destination le Vertex de destination de l'Edge à créer
     * @param thickness l'épaisseur de l'Edge à créer
	 * @return edge copie du vertex destinée au UndoPanel
     */
    public Edge createEdge(Color color, Vertex origin, Vertex destination, int thickness) {
    	Edge edge = new Edge(color, origin, destination, thickness);
        this.elements.add(edge);
        this.setChanged();
		return edge;
    }

    /**
     * Méthode pour changer la position d'un Vertex du Graph
     * @param vertex le Vertex à déplacer
     * @param destination les coordonnées de destination
     */
    public void moveVertex(Vertex vertex, Point destination){
        vertex.setPosition(destination);
        this.setChanged();
    }

    /**
     * Méthode pour déplacer une liste de Vertex dans une certaine direction
     * @param vertexes la liste des Vertex à déplacer
     * @param vectorX la direction du vecteur de déplacement en abscisse
     * @param vectorY la direction du vecteur de déplacement en ordonnée
     */
    public void moveVertexes(ArrayList<Vertex> vertexes, int vectorX, int vectorY){
        for(Vertex vertex : vertexes){
            vertex.move(vectorX,vectorY);
        }
        this.setChanged();
    }
    
    /**
     * Supprime un GraphElement
     * @param element GraphElement qui doit être supprimé
     */
    public void removeGraphElement(GraphElement element) {
    	if (element.isVertex()) {
    		for(Edge e : ((Vertex) element).getEdges()){
                this.elements.remove(e);
                clearLinkedVertices(e);
            }
    	}else{
            clearLinkedVertices((Edge) element);
        }
        this.elements.remove(element);
        this.setChanged();
    }

    /**
     * Méthode nettoyant la présence d'une {@link data.Edge} dans ses Vertices d'origine et de destination
     * @param e L'edge à supprimer des Vertices d'origine et de destination
     */
    public void clearLinkedVertices(Edge e){
        
        int originPosition = e.getDestination().getEdges().indexOf(e);
        int destinationPosition = e.getOrigin().getEdges().indexOf(e);
        
        if(originPosition != -1){
            e.getDestination().getEdges().remove(originPosition);
        }
        if(destinationPosition != -1){
            e.getOrigin().getEdges().remove(destinationPosition);
        }


    }

	/**
	 * Ajoute un GraphElement
	 * @param element GraphElement qui doit être inséré
	 */
	public void createGraphElement(GraphElement element) {

		this.elements.add(element);
		this.setChanged();
	}


	/**
     * (non-Javadoc)
     * @see data.Observable#setChanged()
     */
	@Override
	public void setChanged() {
		this.notifyObservers(this.elements);
	}

	/**
	 * (non-Javadoc)
	 * @see data.Observable#getState()
	 */
	@Override
	public Object getState() {
		return this.elements;
	}

    /**
     * Méthode permettant de récupérer un {@link data.GraphElement} en ne connaissant que son ID, dans un {@link data.Graph}.
     * @param value L'ID (valeur) de l'élément à récupérer.
     * @return Le {@link data.GraphElement} dont l'attribut Value est la value passée en paramètre
     */
    public GraphElement getFromValue(int value){
        for(GraphElement ge : this.elements){
            if(ge.getValue()==value){
                return ge;
            }
        }
        return null;
    }


}