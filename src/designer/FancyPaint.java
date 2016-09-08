package designer;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import designer.components.menu.DesignerMenuBar;
import designer.components.screen.EditorScreen;

public class FancyPaint extends JFrame
{
	EditorScreen panel = new EditorScreen();
	JMenuBar menubar = new DesignerMenuBar(this, panel);
	public FancyPaint()
	{
		super("Fancy Paint");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new java.awt.GridBagLayout());
		setContentPane(panel);
		setJMenuBar(menubar);
	}
}