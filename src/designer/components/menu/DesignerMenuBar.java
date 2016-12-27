package designer.components.menu;

import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import designer.components.screen.EditorScreen;
import designer.windows.ImageIODialog;
import designer.windows.SizePopUp;

@SuppressWarnings("serial")
public class DesignerMenuBar extends JMenuBar
{
	public DesignerMenuBar(JFrame frame, EditorScreen panel)
	{
		JMenu file = new JMenu("File");
		JMenuItem save = new RunnableMenuItem("Save As..."){
					public void run(){
						ImageIODialog.save(frame, panel.getData());}};
		JMenuItem load = new ActionMenuItem("Open File...", new Thread(
				new Runnable(){
					public void run(){
						try{
							panel.setData(ImageIODialog.open(frame));
						} catch (IOException e) {
							JOptionPane.showOptionDialog(frame, "Error: could not read the file. \n Click OK to continue", "Warning", 
									JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,null, 
									new String[]{"OK"}, "OK");
						}}}));
		JMenuItem NEW = new ActionMenuItem("New", new Thread(
				new Runnable(){
					public void run(){
						SizePopUp szpop = new SizePopUp(frame);
						szpop.waitfor();
						/*Wait...*/
						int[] wh = szpop.getSizeInts();
						szpop.dispose();
						if(wh!=null)
							panel.newImg(wh);}}));
		file.add(NEW);
		file.add(load);
		file.add(save);
		this.add(file);
		
		//TODO
		final JFrame ccfrm = new JFrame("Palette");
		ccfrm.add(panel.palette);
		RunnableMenuItem palmenu = new RunnableMenuItem("Show Palette"){
			public void run()
			{
				boolean visible = ccfrm.isVisible();
				ccfrm.setVisible(!visible);
				this.setText((visible ? "Show" : "Hide") + " Palette");
			}
		};
		this.add(palmenu);
	}
	
}
