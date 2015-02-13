package data;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import view.EdgeView;
import view.VertexView;
import controller.Controller;
import controller.VertexMouseListener;

import javax.swing.undo.UndoManager;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Corentin Davidenko on 04/02/15.
 * Class to manage the interactions between edges, vertexes and users. It possesses an UndoManager to
 * manage undo actions. It is associated to a file and has the different default settings for the vertexes
 * and the edges.
 */
public class Graph extends Observable {

	protected UndoManager     undo = new UndoManager();
	private Controller        controller;

    private ArrayList<EdgeView>   edges;
    private ArrayList<VertexView> vertexes;

    private ArrayList<EdgeView>   selectedEdges;
    private ArrayList<VertexView> selectedVertexes;
    private String            name;
    private String            file;
    private Color             defaultColor;
    private Color             defaultSelectedColor;
    private int               defaultSelectedThickness;
    private int               defaultThickness;
    private int               defaultWidth;
    private Shape             defaultShape;
    
    /**
     * Default constructor, initializes attributes.
     */
    public Graph(Controller controller) {
    	this.controller               = controller; 
        this.edges                    = new ArrayList<EdgeView>();
        this.selectedEdges            = new ArrayList<EdgeView>();

        this.vertexes                 = new ArrayList<VertexView>();
        this.selectedVertexes         = new ArrayList<VertexView>();
        
        this.defaultColor             = Color.BLACK;
        this.defaultSelectedColor     = Color.BLUE;
        this.defaultThickness         = 1;
        this.defaultSelectedThickness = 2;
        this.defaultWidth             = 10;
        //this.defaultShape             = new Circle(); --> faire avec une enum plutôt
    }

    /**
     * Constructor with copy
     * @param g
     */
    public Graph(Graph g) {
    	this.edges            = new ArrayList<EdgeView>(g.edges);
    	this.selectedEdges    = new ArrayList<EdgeView>(g.selectedEdges);

    	this.vertexes         = new ArrayList<VertexView>(g.vertexes);
    	this.selectedVertexes = new ArrayList<VertexView>(g.selectedVertexes);
    }
    
    public Shape getDefaultShape() { // pourquoi cela ne serait pas dans les Vertex plutôt ? Est-ce que tous les Vertex doivent avoir la même forme ?
        return defaultShape;
    }

    public void setDefaultShape(Shape defaultShape) {
        this.defaultShape = defaultShape;
    }

    public ArrayList<VertexView> getVertexes() {
        return vertexes;
    }

    public void setVertexes(ArrayList<VertexView> vertexes) {
        this.vertexes = vertexes;
    }

    public ArrayList<EdgeView> getEdges() {
        return this.edges;
    }

    public void setEdges(ArrayList<EdgeView> edges) {
        this.edges = edges;
    }

    public void selectedEdge(EdgeView e){
    	e.setColor(this.defaultSelectedColor);
    	e.setThickness(this.defaultSelectedThickness);
        this.selectedEdges.add(e);
    }

    public void unselectedEdge(EdgeView e){
    	e.setColor(this.defaultColor);
    	e.setThickness(this.defaultThickness);
        this.selectedEdges.remove(e);
    }

    public void selectVertex(VertexView v){
    	v.setColor(this.defaultSelectedColor);
    	v.setThickness(this.defaultSelectedThickness);
        this.selectedVertexes.add(v);
    }

    public void unselectedVertex(VertexView v){
    	v.setColor(this.defaultColor);
    	v.setThickness(this.defaultThickness);
        this.selectedVertexes.remove(v);
    }

    public void clearSelectedItem(){
    	for(VertexView v : this.selectedVertexes) {
    		v.setColor(this.defaultColor);
        	v.setThickness(this.defaultThickness);
    	}
    	for(EdgeView e : this.selectedEdges) {
    		e.setColor(this.defaultColor);
        	e.setThickness(this.defaultThickness);
    	}
    	this.selectedVertexes.clear();
        this.selectedEdges.clear();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Color getDefaultColor() {
        return this.defaultColor;
    }

    public void setDefaultColor(Color defaultColor) {
        this.defaultColor = defaultColor;
    }

    public int getDefaultThickness() {
        return this.defaultThickness;
    }

    public void setDefaultThickness(int defaultThickness) {
        this.defaultThickness = defaultThickness;
    }
    
    public int getDefaultWidth() {
        return this.defaultWidth;
    }

    public void setDefaultWidth(int defaultWidth) {
        this.defaultWidth = defaultWidth;
    }

    /**
     * Creates a vertex with default attributes
     * @param x
     * @param y
     */
    public void createVertex(int x, int y){
    	if (canCreateVertex(x, y)) {
    		x -= this.defaultWidth/2;
        	y -= this.defaultWidth/2;
        	VertexView vertex = new VertexView(this.defaultColor, this.defaultThickness, this.defaultWidth, x, y, this.defaultShape);
            vertex.addMouseListener(new VertexMouseListener(this.controller, vertex));
            this.vertexes.add(vertex);
    	}
    }
    
    /**
     * Verifies if we can add a Vertex at the wanted position (no collision with other vertexes)
     * @param x
     * @param y
     * @return
     */
    private boolean canCreateVertex(int x, int y) {
    	for (VertexView v : this.vertexes) {
    		int margin = this.defaultWidth/2;
    		int side   = v.getWidth() + margin*3; 
    		if (new Rectangle(v.getPositionX() - margin, v.getPositionY() - margin, side, side).contains(new Point(x, y)))
    			return false;
    	}
    	return true;
    }

    /**
     * Creates an edge between two vertexes
     * @param origin
     * @param destination
     */
    public void createEdge(VertexView origin, VertexView destination ){
    	EdgeView edge = new EdgeView(this.defaultThickness, this.defaultColor, origin, destination);
        this.edges.add(edge);
    }

    /**
     * Moves a vertex to x and y coordinates
     * @param vertex
     * @param x
     * @param y
     */
    public void moveVertex(VertexView vertex, int x, int y){
        vertex.setPositionX(x);
        vertex.setPositionY(y);
    }

    /**
     * Moves a vertex by a vector
     * @param vectorX
     * @param vectorY
     */
    public void moveSelectedVertex(int vectorX, int vectorY){
        for(VertexView vertex : this.selectedVertexes){
            vertex.move(vectorX,vectorY);
        }
    }

    /**
     * Saves the current graph to an XML doc (XML-like doc)
     * @param fileName
     */
    public void saveToGraphml(String fileName){

        Element root = new Element("Vertexes");
        org.jdom2.Document toBeSaved = new Document(root);

        for(VertexView v : this.vertexes){
            root.addContent(createDocumentElement(v));
        }

        saveXML(fileName, toBeSaved);
    }

    /**
     * Creates an XML element from a vertex
     * @param v
     * @return
     */
    private Element createDocumentElement(VertexView v){

        Element createdElement = new Element("vertex");

        Element name = new Element("name");
        Element color = new Element("color");
        Element thickness = new Element("thickness");
        Element positionX = new Element("positionX");
        Element positionY = new Element("positionY");
        Element shape = new Element("shape");
        Element edges = new Element("edges");

        for(EdgeView e : v.getEdges()){
            Element edge = new Element("edge");
            edge.setText(e.getLabel());
            edges.addContent(edge);
        }

        name.setText(v.getName());
        color.setText(v.getColor().toString());
        thickness.setText(String.valueOf(v.getThickness()));
        positionX.setText(String.valueOf(v.getPositionX()));
        positionY.setText(String.valueOf(v.getPositionY()));
        shape.setText(v.getShape().toString());

        createdElement.addContent(name);
        createdElement.addContent(color);
        createdElement.addContent(thickness);
        createdElement.addContent(positionX);
        createdElement.addContent(positionY);
        createdElement.addContent(shape);

        return createdElement;
    }

    /**
     * Shows an XML document
     * @param document
     */
    private static void showXML(Document document){

        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());

        try {
            out.output(document, System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a document at the "file" destination, in XML format
     * @param file
     * @param document
     */
    private static void saveXML(String file, Document document){
        try
        {
            //On modifie le format d'enregistrement (affichage)
            XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());

            out.output(document, new FileOutputStream(file));
        }
        catch (java.io.IOException e){
            e.printStackTrace();
        }
    }

	@Override
	public void setChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getState() {
		// TODO Auto-generated method stub
		return null;
	}
}
