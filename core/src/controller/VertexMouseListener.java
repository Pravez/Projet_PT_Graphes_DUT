package controller;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;

import view.VertexView;

/**
 * @author Alexis Dufrenne
 * Classe VertexMouseListener, écouteur des événements souris survenant au niveau des VertexView
 */
public class VertexMouseListener implements MouseListener, MouseMotionListener {
	
	private Controller controller;
	private VertexView vertex;
	private boolean    dragging;
	private Point      initDrag;

	private static VertexView underMouseVertex = null;


	/**
	 * Constructeur de la classe VertexMouseListener
	 * @param controller le Controller
	 * @param vertex le VertexView qu'il écoute
	 */
	public VertexMouseListener(Controller controller, VertexView vertex) {
		this.controller = controller;
		this.vertex     = vertex;
	}

	/**
	 * Initialize a Popup menu with MenuItems
	 * @param menuItems the MenuItems to be present in the popUp menu
	 * @return The popup menu created
	 */
	public JPopupMenu initNewPopupMenu(String [] menuItems){
		JPopupMenu jpm = new JPopupMenu();

		for(String s : menuItems){
			JMenuItem jmi = new JMenuItem(s);
			jmi.addActionListener(new ContextMenuActionListener(jmi, controller, vertex));
			jpm.add(jmi);
		}

		return jpm;
	}

	/**
	 * Méthode récupérant le clic d'une souris
	 * @param e
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		switch(controller.getState()){
			case SELECTION:
				if(e.getButton() == MouseEvent.BUTTON1) {
					if (e.isControlDown()) {
						this.controller.notifyHandleElementSelected(this.vertex);
					} else {
						this.controller.notifyElementSelected(this.vertex);
					}
				}else if (e.getButton() == MouseEvent.BUTTON3) {
					//Création du menu contextuel avec Edit et Delete comme options.
					JPopupMenu contextMenu = initNewPopupMenu(new String[]{"Edit", "Delete"});
					contextMenu.show(this.vertex, e.getX(), e.getY());
				}
				break;
			case ZOOM_IN:
				break;
			case ZOOM_OUT:
				break;
			case CREATE:
				break;
		}
	}

	/**
	 * Méthode appelée lorsque le curseur de la souris entre dans la zone du VertexView
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		underMouseVertex = vertex;
	}

	/**
	 * Méthode appelée lorsque le curseur de la souris quitte la zone du VertexView
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		underMouseVertex = null;
	}

	/**
	 * Méthode appelée lorsque l'on presse un bouton de la souris sur le VertexView
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		this.initDrag = new Point(e.getX(), e.getY());
	}

	/**
	 * Méthode appelée lorsque l'on relâche le bouton pressé de la souris sur le VertexView
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if (this.dragging && this.controller.getState() != Controller.State.CREATE) {
			this.controller.notifyMoveSelectedElements(new Point(e.getX() - initDrag.x, e.getY() - initDrag.y));
		}
		if ( underMouseVertex!=null && underMouseVertex!= vertex){
			System.out.println("Create an Edge please !");
			if (this.vertex.getPosition().x < underMouseVertex.getPosition().x) {
				this.controller.addEdge(vertex.getVertex(), underMouseVertex.getVertex());
			} else {
				this.controller.addEdge(underMouseVertex.getVertex(), vertex.getVertex());
			}
		}
		this.controller.notifyEndDraggingEdge();
		this.dragging = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		this.dragging = true;
		if (this.controller.getState() != Controller.State.CREATE) {
			this.controller.notifyMoveSelectedElements(new Point(e.getX() - initDrag.x, e.getY() - initDrag.y));
			this.controller.notifyRepaintTab();
			this.initDrag = new Point(e.getX(), e.getY());
		} else if (this.controller.getState() == Controller.State.CREATE){
			this.controller.notifyDraggingEdge(this.vertex.getPosition(), e.getPoint());
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) { }
}
