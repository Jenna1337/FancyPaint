package designer.components.menu;

import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import designer.components.screen.EditorScreen;
import designer.components.screen.ScaleablePixelBox;
import designer.windows.ImageIODialog;
import designer.windows.SizePopUp;

@SuppressWarnings("serial")
public class DesignerMenuBar extends JMenuBar
{
	
	public DesignerMenuBar(JFrame frame, EditorScreen panel)
	{
		JMenu file = new JMenu("File");
		JMenuItem save = new ActionMenuItem("Save As...")
		{
			public void onClick()
			{
				ImageIODialog.save(frame, panel.getData());
			}
		};
		JMenuItem load = new ActionMenuItem("Open File...")
		{
			public void onClick()
			{
				try
				{
					panel.setData(ImageIODialog.open(frame));
				}
				catch(IOException e)
				{
					JOptionPane.showOptionDialog(frame,
							"Error: could not read the file. \n Click OK to continue",
							"Warning", JOptionPane.DEFAULT_OPTION,
							JOptionPane.ERROR_MESSAGE, null, new String[]{"OK"},
							"OK");
				}
			}
		};
		JMenuItem NEW = new ActionMenuItem("New")
		{
			public void onClick()
			{
				SizePopUp szpop = new SizePopUp(frame);
				szpop.waitfor();
				/* Wait... */
				int[] wh = szpop.getSizeInts();
				szpop.dispose();
				if(wh != null)
					panel.newImg(wh);
			}
		};
		file.add(NEW);
		file.add(load);
		file.add(save);
		this.add(file);
		
		// TODO
		JPanel palpal = new JPanel();
		for(Component c : panel.palette.getComponents())
			if(c.getClass().equals(ScaleablePixelBox.class))
				palpal.add(c);
		palpal.setPreferredSize(new Dimension(700, 700));
		JScrollPane palscroll = new JScrollPane(palpal,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JMenu palmenu = new JMenu("Palette");
		palmenu.add(palscroll);
		this.add(palmenu);
	}
	
}
