package designer;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import designer.components.menu.DesignerMenuBar;
import designer.components.screen.EditorScreen;

public class FancyPaint
{
	JFrame frame = new JFrame("Tiny Image Editor");
	EditorScreen panel = new EditorScreen();
	JMenuBar menubar = new DesignerMenuBar(frame, panel);
	public FancyPaint()
	{
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new java.awt.GridBagLayout());
		frame.setContentPane(panel);
		frame.setJMenuBar(menubar);
	}
	public void setVisible(boolean visibility)
	{
		frame.setVisible(visibility);
	}
}