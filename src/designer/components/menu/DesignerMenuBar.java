package designer.components.menu;

import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import designer.ImageIODialog;
import designer.SizePopUp;
import designer.components.screen.EditorScreen;

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
		this.add(panel.palette);
	}
	
}
