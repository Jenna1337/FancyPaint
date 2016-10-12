package designer.components.menu;

import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import designer.components.ActionThread;

@SuppressWarnings("serial")
public abstract class ActionMenuItem extends JMenuItem
{
	private final ActionThread action = new ActionThread(this, new Thread()
	{
		public void run()
		{
			onClick();
		}
	});
	
	public ActionMenuItem(String label)
	{
		super();
		this.setAction(action);
		this.setText(label);
		this.setToolTipText(label);
	}
	public void setIcon(String filename)
	{
		try
		{
			this.setIcon(new ImageIcon(ImageIO.read(new File(filename))));
		}
		catch(Exception e)
		{
		}
	}
	public abstract void onClick();
}
