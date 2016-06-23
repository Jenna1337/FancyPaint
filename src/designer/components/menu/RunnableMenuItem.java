package designer.components.menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import designer.components.ActionThread;

@SuppressWarnings("serial")
public abstract class RunnableMenuItem extends JMenuItem implements ActionListener, Runnable
{
	public RunnableMenuItem(String label)
	{
		super();
		this.addActionListener(this);
		
		this.setAction(new ActionThread(this, new Thread(this)));
		this.setText(label);
		this.setToolTipText(label);
		this.addActionListener(this);
	}
	public void actionPerformed(ActionEvent arg0){}
	public abstract void run();
}