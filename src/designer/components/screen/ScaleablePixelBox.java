package designer.components.screen;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/**
 * A JPanel that acts similar to a button.
 * 
 * @author jonah.sloan
 * @see #mouseisover
 */
@SuppressWarnings("serial")
public class ScaleablePixelBox extends JPanel implements MouseListener
{
	private static LineBorder border = new LineBorder(Color.GRAY, 1);
	private boolean mouseisover = false;
	private int size = 50;
	private Color color;
	private volatile EditorScreen parent;
	private final Thread thread;
	
	public ScaleablePixelBox(Thread thread, Color colorinit,
			EditorScreen parent, boolean hasborder)
	{
		this.parent = parent;
		this.thread = thread;
		this.setBackground(colorinit);
		if(hasborder)
			this.setBorder(border);
		this.addMouseListener(this);
		// size =
		// Math.min(this.getParent().getWidth(),this.getParent().getHeight());
		this.setSize(size, size);
		// TODO add a set size / ratio --- might not be in this file
	}
	private void runif()
	{
		if(this.mouseisover && parent.mousedown)
			this.thread.run();
	}
	public void mouseClicked(MouseEvent e)
	{
	}
	public void mouseEntered(MouseEvent e)
	{
		this.mouseisover = true;
		runif();
	}
	public void mouseExited(MouseEvent e)
	{
		runif();
		this.mouseisover = false;
	}
	public void mousePressed(MouseEvent e)
	{
		parent.mousedown = true;
		runif();
	}
	public void mouseReleased(MouseEvent e)
	{
		runif();
		parent.mousedown = false;
	}
	public boolean isMouseOver()
	{
		return mouseisover;
	}
	public boolean isMouseDown()
	{
		return parent.mousedown;
	}
	public Color getColor()
	{
		return color;
	}
	public void setColor(Color color)
	{
		this.color = color;
	}
	@Override
	public java.awt.Dimension getPreferredSize()
	{
		return new java.awt.Dimension(size, size);
	}
}
