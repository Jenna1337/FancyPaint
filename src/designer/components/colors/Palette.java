package designer.components.colors;

import java.awt.Color;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import designer.components.screen.EditorScreen;

@SuppressWarnings("serial")
public class Palette extends JPanel
{
	private volatile Color currentcolor;
	private static RecentSwatchPanel p=new RecentSwatchPanel();
	private final Palette THIS=this;
	public Palette(EditorScreen parent)
	{
		setSelectedColor(Color.BLACK);
		AbstractColorChooserPanel[] choosers = ColorChooserComponentFactory.getDefaultChooserPanels();
		JColorChooser choose=new JColorChooser();
		choose.setUI(new BasicColorChooserUI());
		choose.setChooserPanels(new AbstractColorChooserPanel[]{choosers[3], choosers[1], choosers[2], choosers[4]});
		choose.getSelectionModel().addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt)
			{
				if((evt.getSource()!=null) && (evt.getSource() instanceof ColorSelectionModel))
					THIS.setSelectedColor((Color) ((ColorSelectionModel)evt.getSource()).getSelectedColor());
			}
		});
		this.add(choose);
		this.add(p);
		/*for(Colors color : Colors.values())
		{
			ScaleablePixelBox newbox = new ScaleablePixelBox(new Thread(new Runnable(){
				public void run(){
					setSelectedColor(color.getColor());
				}
			}), color.getColor(), parent, true);
			newbox.setToolTipText(color.toString());
			this.add(newbox);
		}*/
	}
	public Color getSelectedColor() {
		return this.currentcolor;
	}
	public void setSelectedColor(Color selectedcolor)
	{
		this.currentcolor = selectedcolor;
		p.setMostRecentColor(currentcolor);
	}
}
