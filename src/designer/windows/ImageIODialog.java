package designer.windows;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class ImageIODialog
{
	private static String fileext = ".png";
	private static ExtensionFileFilter filter = new ExtensionFileFilter(
			"Portable Network Graphic", fileext);
	private static final JFileChooser fc = new JFileChooser();
	
	public static void save(Component parent, BufferedImage data)
	{
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);
		int returnVal = fc.showSaveDialog(parent);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			String path = file.getPath();
			if(!file.exists() && !path.endsWith(fileext))
				file = new File(path += fileext);
			try
			{
				ImageIO.write(data, fileext.substring(1).toUpperCase(), file);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	public static BufferedImage open(Component parent)
	{
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(parent);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			try
			{
				return ImageIO.read(file);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
}

/**
 * Modified from
 * http://www.java2s.com/Code/JavaAPI/javax.swing/JFileChoosersetFileFilterFileFilterfilter.htm
 **/
class ExtensionFileFilter extends javax.swing.filechooser.FileFilter
{
	String description;
	String extensions[];
	
	public ExtensionFileFilter(String description, String... extensions)
	{
		if(description == null)
			this.description = extensions[0];
		else
			this.description = description;
		this.extensions = (String[]) extensions.clone();
		toLower(this.extensions);
	}
	private void toLower(String array[])
	{
		for(int i = 0, n = array.length; i < n; i++)
			array[i] = array[i].toLowerCase();
	}
	public String getDescription()
	{
		String desc = description + " (";
		for(int i = 0; i < extensions.length; ++i)
		{
			desc += "*" + extensions[i];
			if(i != extensions.length - 1)
				desc += ", ";
		}
		return desc + ")";
	}
	public boolean accept(File file)
	{
		if(file.isDirectory())
			return true;
		else
		{
			String path = file.getAbsolutePath().toLowerCase();
			for(String extension : extensions)
				if(path.endsWith(extension.toLowerCase()))
					return true;
		}
		return false;
	}
}
