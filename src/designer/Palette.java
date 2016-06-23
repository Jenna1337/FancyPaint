package designer;

import java.awt.Color;
import javax.swing.JPanel;
import designer.components.screen.EditorScreen;
import designer.components.screen.ScaleablePixelBox;

@SuppressWarnings("serial")
public class Palette extends JPanel
{
	private volatile Color currentcolor;
	public Palette(EditorScreen parent)
	{
		setSelectedColor(Color.BLACK);
		for(Color color : Colors.getColors())
			this.add(new ScaleablePixelBox(new Thread(new Runnable(){
				public void run(){
					setSelectedColor(color);
				}
			}), color, parent));
	}
	public Color getSelectedColor() {
		return this.currentcolor;
	}
	public void setSelectedColor(Color selectedcolor)
	{
		this.currentcolor = selectedcolor;
	}
}
