package controller.state;

import controller.Controller;
import data.Graph;
import view.Tab;
import view.elements.ElementView;
import view.elements.VertexView;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class ZoomState extends State {

	public ZoomState(Controller controller) {
		super(controller);
	}

	@Override
	public void drag(Tab tab, Graph graph, MouseEvent e) {
		this.dragging = true;
	}

	@Override
	public void drag(VertexView vertex, MouseEvent e) {
		this.dragging = true;
	}
	
	@Override
	public void pressed(Tab tab, Graph grap, MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) { // Clic gauche
			tab.getScrollPane().zoomIn(e.getX(), e.getY());
		} else if (e.getButton() == MouseEvent.BUTTON3) { // Clic droit
            tab.getScrollPane().zoomOut(e.getX(), e.getY());
        }
	}

	@Override
	public void pressed(ElementView element, MouseEvent e) { }
	
	@Override
	public void released(Tab tab, Graph grap, MouseEvent e) {
		this.dragging = false;	
	}

	@Override
	public void released(ElementView element, MouseEvent e) {
		this.dragging = false;	
	}

    @Override
    public void wheel(Tab tab, MouseWheelEvent e) {
        int direction = e.getWheelRotation();
        if (direction > 0) { // on bouge la molette vers le bas : on dézoome
            tab.getScrollPane().zoomOut(e.getX(), e.getY());
        } else { // on bouge la molette vers le haut : on zoome
            tab.getScrollPane().zoomIn(e.getX(), e.getY());
        }
    }
	
	@Override
	public String getMode() {
		return "ZOOM";
	}
}
