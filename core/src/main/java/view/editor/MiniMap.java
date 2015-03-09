package view.editor;

import data.Edge;
import data.GraphElement;
import data.Observable;
import data.Vertex;
import view.Observer;
import view.editor.elements.EdgeView;
import view.editor.elements.VertexView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.ListIterator;

public class MiniMap extends JComponent implements Observer, AdjustmentListener {

	private static final long serialVersionUID = 1L;
	
	private ScrollPane pane;
	private Sheet sheet;
	
	private ArrayList<EdgeView>    edges;
    private ArrayList<VertexView>  vertexes;
    
    /** Zone **/
    private Rectangle              selectionZone;
    private Color                  selectionColor;
    private Color                  selectionBorderColor;

	/**
	 * Conbstructeur de la classe MiniMap
	 * @param pane le ScrollPane de la window
	 * @param sheet la Sheet, feuillde de dessin du Tab actuel
	 */
    public MiniMap(ScrollPane pane, Sheet sheet) {
        this.pane                 = pane;
        this.sheet                = sheet;
        this.edges                = new ArrayList<EdgeView>();
        this.vertexes             = new ArrayList<VertexView>();

        this.selectionColor       = new Color(172, 211, 244);
        this.selectionBorderColor = new Color(107, 153, 189);
        updateSelectionZone();
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                moveSelectionZone(e);
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                moveSelectionZone(e);
            }
        });
        this.setVisible(true);
    }

	/**
	 * Override de la méthode paintComponent pour dessiner le graph
	 * @param g les Graphics
	 */
	public void paintComponent(Graphics g) {
		g.setColor(this.selectionColor);
		g.fillRect(this.selectionZone.x, this.selectionZone.y, this.selectionZone.width, this.selectionZone.height);
		g.setColor(this.selectionBorderColor);
		g.drawRect(this.selectionZone.x, this.selectionZone.y, this.selectionZone.width, this.selectionZone.height);
        for(EdgeView e : this.edges){
            e.paintComponent(g, 1.0*this.getWidth()/this.sheet.getMaximumSize().width, 1.0*this.getHeight()/this.sheet.getMaximumSize().height);
        }
        for (VertexView v : this.vertexes) {
            v.paintComponent(g, 1.0*this.getWidth()/this.sheet.getMaximumSize().width, 1.0*this.getHeight()/this.sheet.getMaximumSize().height);
        }
    }

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable observable, Object object) {
		this.vertexes.clear();
        this.edges.clear();
		super.removeAll();
		// pour chaque GraphElement du Graph
		for (GraphElement element : (ArrayList<GraphElement>)object) {
			if (element.isVertex()) {
				addVertex((Vertex)element);
			} else {
				VertexView src= null, dst = null;
	            ListIterator<VertexView> search = vertexes.listIterator();

	            while (search.hasNext() && (src == null || dst == null)){
	                VertexView tmp = search.next();
	                if (tmp.getVertex() == ((Edge) element).getOrigin()) src = tmp;
	                else if (tmp.getVertex() == ((Edge) element).getDestination()) dst = tmp;
	            }
	            if ( src != null && dst != null) addEdge((Edge) element, src, dst);
			}
		}
		this.repaint();
	}
	
	/**
     * Méthode permettant d'ajouter un VertexView à la position désirée
     * @param vertex le modèle Vertex du VertexView
     */
    public void addVertex(Vertex vertex){
    	VertexView vertexView = new VertexView(vertex, Color.BLUE);
        this.vertexes.add(vertexView);
        super.add(vertexView);
    }
    
    /**
     * Méthode permettant d'ajouter un EdgeView
     * @param edge le modèle Edge de l'EdgeView
     * @param origin le VertexView d'origine de l'EdgeView
     * @param destination le VertexView de destination de l'EdgeView
     */
    public void addEdge(Edge edge, VertexView origin, VertexView destination ){
    	EdgeView edgeView = new EdgeView(edge, 2, Color.BLUE, origin, destination);
        this.edges.add(edgeView);
        super.add(edgeView);
    }

	@Override
	public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
        updateSelectionZone();
	}

	/**
	 * Méthode permettant de mettre à jour la zone de sélection (viewport actuel) en fonction de la dimension des fenêtres
     * lorsque les scrollbar sont déplacées
	 */
	public void updateSelectionZone() {
        int x      = (int)(1.0*this.getPreferredSize().width*this.pane.getHorizontalScrollBar().getValue()/this.sheet.getPreferredSize().width);
		int y      = (int)(1.0*this.getPreferredSize().height*this.pane.getVerticalScrollBar().getValue()/this.sheet.getPreferredSize().height);
		int width  = (int)(1.0*this.getPreferredSize().width*this.pane.getViewport().getSize().width/this.sheet.getPreferredSize().width);
		int height = (int)(1.0*this.getPreferredSize().height*this.pane.getViewport().getSize().height/this.sheet.getPreferredSize().height);
		this.selectionZone = new Rectangle(x, y, width, height);
        this.repaint();
	}

	/**
	 * Méthode appelée pour mettre à jour la position de la zone de sélection dans le JScrollPane selon la position sur la MiniMap
	 * @param e l'événement souris
	 */
	private void moveSelectionZone(MouseEvent e) {
		Point origin = new Point(e.getX() - this.selectionZone.width/2, e.getY() - this.selectionZone.height/2);
		this.pane.getHorizontalScrollBar().setValue((int) (1.0*origin.x*this.sheet.getSize().width/this.getPreferredSize().width));
		this.pane.getVerticalScrollBar().setValue((int) (1.0*origin.y*this.sheet.getSize().height/this.getPreferredSize().height));
	}

    public void setPosition(Point position, int width, int height) {
        this.pane.getHorizontalScrollBar().setValue((int) (1.0*position.x*this.sheet.getPreferredSize().width/width));
        this.pane.getVerticalScrollBar().setValue((int) (1.0*position.y*this.sheet.getPreferredSize().height/height));
    }
}