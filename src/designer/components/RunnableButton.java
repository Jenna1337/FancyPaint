package designer.components;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

@SuppressWarnings("serial")
public abstract class RunnableButton extends JButton implements ActionListener, Runnable
{
	public RunnableButton(String label)
	{
		super();
		this.addActionListener(this);
		
		this.setAction(new ActionThread(this, new Thread(this)));
		this.setText(label);
		this.setToolTipText(label);
		this.addActionListener(this);
	}
	public RunnableButton(String label, int width, int height)
	{
		this(label);
		this.setSize(width, height);
	}
	public void setIcon(String filename)
	{
		try
		{
			this.setIcon(new ImageIcon(ImageIO.read(new File(filename))));
		}
		catch(Exception e){}
	}
	public void actionPerformed(ActionEvent arg0){}
	public abstract void run();
}