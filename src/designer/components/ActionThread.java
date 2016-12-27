package designer.components;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

@SuppressWarnings("serial")
public class ActionThread extends AbstractAction
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
}
