package designer.windows;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.ListModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import designer.components.ActionButton;

@SuppressWarnings("serial")
public class NewImagePopUp extends JDialog implements WindowListener
{
	private JSpinner h, w;
	private JComboBox<?> t;
	/**
	 * Set on close
	 */
	protected int[] wh;
	private byte choice;
	
	public NewImagePopUp(JFrame parent)
	{
		super(parent);
		this.init();
	}
	private void init()
	{
		this.setTitle("New Image");
		this.addWindowListener(this);
		
		this.setLayout(new BorderLayout());
		this.h = new JSpinner(new SpinnerNumberModel(10, 0, 9999, 1));
		this.w = new JSpinner(new SpinnerNumberModel(10, 0, 9999, 1));
		this.t = new JComboBox<Integer>(new Integer[]{BufferedImage.TYPE_4BYTE_ABGR, BufferedImage.TYPE_BYTE_BINARY});
		
		JPanel pane_btn = new JPanel(), pane_in = new JPanel();
		
		pane_in.add(h);
		pane_in.add(w);
		this.getContentPane().add(pane_in, BorderLayout.NORTH);
		
		this.getContentPane().add(t, BorderLayout.CENTER);
		
		NewImagePopUp mywindow = this;
		ActionButton button_ok = new ActionButton("OK")
		{
			public void onClick()
			{
				mywindow.choice = 0;
				active = false;
				mywindow.dispose();
			}
		};
		ActionButton button_cancel = new ActionButton("Cancel")
		{
			public void onClick()
			{
				mywindow.choice = 1;
				active = false;
				mywindow.dispose();
			}
		};
		
		pane_btn.add(button_ok);
		pane_btn.add(button_cancel);
		this.getContentPane().add(pane_btn, BorderLayout.SOUTH);
		
		this.setModal(true);
		this.pack();
		this.setVisible(true);
	}
	
	public volatile boolean active = true;
	
	public byte waitfor()
	{
		try
		{
			while(active)
				Thread.sleep(1000);
			this.dispose();
			return 0;
		}
		catch(Exception e)
		{
			this.dispose();
			return 1;
		}
	}
	/**
	 * 
	 * @return An array of shorts containing only width
	 *         and height, in their respective order.
	 **/
	public int[] getSizeInts()
	{
		if(this.choice != 0)
			return null;
		int[] sa = new int[2];
		sa[0] = getInt(this.w);
		sa[1] = getInt(this.h);
		return sa;
	}
	public void windowActivated(WindowEvent e)
	{
	}
	public void windowClosed(WindowEvent e)
	{
	}
	public void windowClosing(WindowEvent e)
	{
		wh = this.getSizeInts();
		active = false;
	}
	public void windowDeactivated(WindowEvent e)
	{
	}
	public void windowDeiconified(WindowEvent e)
	{
	}
	public void windowIconified(WindowEvent e)
	{
	}
	public void windowOpened(WindowEvent e)
	{
	}
	
	public int getInt(JSpinner field)
	{
		return Integer.parseInt("" + ((Integer) field.getValue()));
	}
}
