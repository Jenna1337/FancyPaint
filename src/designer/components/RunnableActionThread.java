package designer.components;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractAction;

@SuppressWarnings("serial")
public abstract class RunnableActionThread extends AbstractAction implements KeyListener, Runnable
{
	public RunnableActionThread()
	{
	}
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if(this.equals(arg0.getSource()))
		{
			this.run();
		}
	}
	public void keyPressed(KeyEvent arg0)
	{
		if(this.equals(arg0.getSource()))
		{
			this.run();
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
	public abstract void run();
}
