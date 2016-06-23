package designer.components.screen;

import java.awt.Color;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputListener;

@SuppressWarnings("serial")
public class ScaleablePixelBox extends JPanel implements MouseInputListener
{
	private static volatile LineBorder border = new LineBorder(Color.GRAY, 1);
	//for use in palette and main image design screen
	private boolean mouseisover=false;
	public volatile int size = 50;
	private Color color;
	private volatile EditorScreen parent;

	Thread thread;

	public ScaleablePixelBox(Thread thread, Color colorinit, EditorScreen parent, boolean hasborder)
	{
		this.parent=parent;
		this.thread = thread;
		this.setBackground(colorinit);
		if(hasborder)
			this.setBorder(border);
		this.addMouseListener(this);
		//size = Math.min(this.getParent().getWidth(),this.getParent().getHeight());
		this.setSize(size, size);
		//TODO add a set size / ratio --- might not be in this file
	}
	private void runif()
	{
		if(this.mouseisover && parent.mousedown)
			this.thread.run();
	}
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e)
	{
		this.mouseisover=true;
		runif();
	}
	public void mouseExited(MouseEvent e)
	{
		runif();
		this.mouseisover=false;
	}
	public void mousePressed(MouseEvent e)
	{
		parent.mousedown=true;
		runif();
	}
	public void mouseReleased(MouseEvent e)
	{
		runif();
		parent.mousedown=false;
	}
	public void mouseDragged(MouseEvent arg0){}
	public void mouseMoved(MouseEvent arg0){}
	public boolean isMouseOver() {
		return mouseisover;
	}
	public boolean isMouseDown() {
		return parent.mousedown;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	@Override
	public java.awt.Dimension getPreferredSize() {
		return new java.awt.Dimension(size, size);
	}
}
