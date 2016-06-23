package designer.components;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;

@SuppressWarnings("serial")
public class ActionThread extends AbstractAction implements KeyListener
{
	protected Object source;
	protected Thread thread;
	public ActionThread(Object source, Thread thread)
	{
		this.source = source;
		this.thread = thread;
	}
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if(this.source.equals(arg0.getSource()))
		{
			this.thread.run();
		}
	}
	public void keyPressed(KeyEvent arg0)
	{
		if(this.source.equals(arg0.getSource()))
		{
			this.thread.run();
		}
	}
	@Override
	public void keyReleased(KeyEvent e)
	{
	}
	@Override
	public void keyTyped(KeyEvent e)
	{
	}
}
