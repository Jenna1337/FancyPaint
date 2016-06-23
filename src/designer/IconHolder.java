package designer;

import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public interface IconHolder
{
	public default void setIcon(String filename)
	{
		try
		{
			this.setIcon(new ImageIcon(ImageIO.read(new File(filename))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void setIcon(ImageIcon imageIcon);
}
