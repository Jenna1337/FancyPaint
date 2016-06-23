package designer;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public final class IconReader
{
	private IconReader(){}
	
	public static ImageIcon readIcon(String filename)
	{
		try
		{
			return new ImageIcon(ImageIO.read(new File(filename)));
		}
		catch(IOException e)
		{
			throw new IllegalArgumentException(e);
		}
	}
}
