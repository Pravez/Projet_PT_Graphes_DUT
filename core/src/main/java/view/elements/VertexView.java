package view.elements;

import data.Graph;
import data.GraphElement;
import data.Vertex;
import view.frames.VertexViewEditor;

import java.awt.*;

/**
 * @author Alexis Dufrenne
 * Classe étant la "représentation graphique" d'un {@link data.Vertex}. Elle récupère ses données et les rend visibles.
 */
public class VertexView extends ElementView {

	private static final long serialVersionUID = 1L;
	private Vertex vertex;
    private Color  color;
    private Color  hoverColor;

    //rajouter des statics pour les paramètres par défaut

    /**
     * Constructeur de la classe VertexView
     * @param vertex le modèle Vertex à afficher
     * @param hoverColor couleur du VertexView lorsqu'il est sélectionné
     */
    public VertexView(Vertex vertex, Color hoverColor) {
    	this.vertex     = vertex;
        this.color      = vertex.getColor();
        this.hoverColor = hoverColor;
    }

    @Override
    public boolean contains(int x, int y) {
        int size = this.vertex.getSize();
        int posx = this.vertex.getPosition().x - size/2;
        int posy = this.vertex.getPosition().y - size/2;
        return new Rectangle(posx, posy, size, size).contains(x, y);
    }
    
    /**
     * Override de la méthode paintComponent pour dessiner le VertexView dans le Tab
     * (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    public void paintComponent(Graphics g) {
    	g.setFont(super.getFont());
		
		Graphics2D     g2d         = ((Graphics2D) g);
		RenderingHints renderHints = new RenderingHints (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setRenderingHints(renderHints);
		g.setColor(this.color);
		
		int size = this.vertex.getSize();
		int x    = this.vertex.getPosition().x - size/2;
		int y    = this.vertex.getPosition().y - size/2;
		
		switch (this.vertex.getShape()) {
		case SQUARE :
			g.fillRect(x, y, size, size);
			break;
		case CIRCLE :
			g.fillOval(x, y, size, size);
			break;
		case TRIANGLE :
			g.fillPolygon(new Polygon(new int[] {x + size/2, x + size, x}, new int[] {y, y + size, y + size}, 3));
			break;
		case CROSS :
			Stroke oldStroke = ((Graphics2D) g).getStroke();
			int thickness = size/3;
			((Graphics2D) g).setStroke(new BasicStroke(thickness));
			g.drawLine(x, y, x + size, y + size);
			g.drawLine(x, y + size, x + size, y);
			((Graphics2D) g).setStroke(oldStroke);
			break;
		default:
			break;
		}
    }
    
    /**
     * Setter de la couleur du VertexView
     * @param color nouvelle couleur du VertexView
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /**
     * Setter de la couleur du VertexView lorsqu'il est sélectionné
     * @param color nouvelle couleur du VertexView lorsqu'il est sélectionné
     */
    public void setHoverColor(Color color) {
    	this.hoverColor = color;
    }
    
    /**
     * Setter de la position du VertexView
     * @param position la nouvelle position
     */
    public void setPosition(Point position) {
    	this.vertex.setPosition(position);
    }
    
    /**
     * Getter de la couleur du VertexView
     * @return la couleur du VertexView
     */
    public Color getColor() {
        return this.color;
    }
    
    /**
     * Getter de la couleur du VertexView lorsqu'il est sélectionné
     * @return la couleur du VertexView lorsqu'il est sélectionné
     */
    public Color getHoverColor() {
        return this.hoverColor;
    }
       
    /**
     * Getter de la position du VertexView
     * @return la position du VertexView
     */
    public Point getPosition() {
    	return this.vertex.getPosition();
    }
    
    /**
     * Méthode appelée pour mettre à jour les paramètres d'affichage du VertexView s'il est sélectionné ou non
     * @param isHover boolean si le VertexView est sélectionné ou non
     */
    @Override
    public void updateHover(boolean isHover) {
		this.color = (isHover) ? this.hoverColor : this.vertex.getColor();
	}

    /**
     * Méthode appellée pour invoquer un JDialog permettant de modifier les informations d'un VertexView dans le détail.
     */
    @Override
    public void modify(Graph graph){
        VertexViewEditor edit = new VertexViewEditor(this.vertex);

        Vertex newVertex = edit.getModifiedVertex();

        this.vertex.setSize(newVertex.getSize());
        this.vertex.setPosition(newVertex.getPosition());
        this.vertex.setLabel(newVertex.getLabel());
    }

    /**
     * Getter permettant de récupérer le {@link data.Vertex} associé au VertexView
     * @return
     */
    public Vertex getVertex() {
        return vertex;
    }

    /**
     * Méthode renvoyant le {@link data.Vertex} mais au format d'un {@link data.GraphElement}
     * @return
     */
	@Override
	public GraphElement getGraphElement() {
		return this.vertex;
	}

    /**
     * Méthode changeant la position ({@link java.awt.Point}) d'un {@link data.Vertex}.
     * @param vector Le nouveau {@link java.awt.Point} où se situera le {@link data.Vertex}
     */
	public void move(Point vector) {
		this.vertex.setPosition(new Point(this.vertex.getPosition().x + vector.x, this.vertex.getPosition().y + vector.y));		
	}
}