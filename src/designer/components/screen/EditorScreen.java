package designer.components.screen;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JPanel;
import designer.components.Palette;

@SuppressWarnings("serial")
public class EditorScreen extends JPanel
{
	private volatile int imgheight=100, imgwidth=100;
	protected volatile boolean mousedown=false;
	public final Palette palette = new Palette(this);
	private int imgtype=BufferedImage.TYPE_4BYTE_ABGR;
	private GridLayout layout;
	private volatile int[] data;
	public EditorScreen()
	{
		resizeCanvas();
		this.setGaps(1, Color.GRAY);
	}
	public void repaintCanvas()
	{
		this.getParent().repaint();
		this.setGaps(1, Color.GRAY);
		//TODO?
	}
	private final EditorScreen editor = this;
	private void resizeCanvas()
	{
		this.removeAll();
		layout=new GridLayout(imgwidth, imgheight);
		this.setLayout(layout);
		System.out.println(imgheight+" "+imgwidth);
		data=new int[imgwidth*imgheight];
		int i=0;
		for(int ix=0; ix<imgheight; ++ix)
		{
			for(int iy=0; iy<imgwidth; ++iy)
			{
				System.out.println(ix+" "+iy+" "+imgheight+" "+imgwidth);
				final int ti=i;
				this.add(new ScaleablePixelBox(new Thread(){
					public void run()
					{
						editor.getComponent(ti).setBackground(palette.getSelectedColor());;
						data[ti] = palette.getSelectedColor().getRGB();
					}
				}, Color.WHITE, this, false));
				data[i++] = Color.WHITE.getRGB();
			}
		}
		this.revalidate();
	}
	public void newImg(int[] wh)
	{
		imgwidth=wh[0];
		imgheight=wh[1];
		Thread.yield();
		this.resizeCanvas();
	}
	public BufferedImage getData()
	{
		BufferedImage img = new BufferedImage(imgwidth, imgheight, imgtype);
		int i=0;
		for(int x=0; x<imgheight; ++x)
		{
			for(int y=0; y<imgwidth; ++y)
			{
				img.setRGB(x, y, data[i++]);
			}
		}
		return img;
	}
	public void setData(BufferedImage img) throws IOException
	{
		imgwidth=img.getWidth();
		imgheight=img.getHeight();
		data = new int[imgwidth*imgheight];
		int i=0;
		for(int x=0; x<imgheight; ++x)
		{
			for(int y=0; y<imgwidth; ++y)
			{
				data[i++] = img.getRGB(x, y);
			}
		}
		this.resizeCanvas();
		this.repaintCanvas();
	}
	public void setGaps(int gap, Color color)
	{
		layout.setHgap(gap);
		layout.setVgap(gap);
		this.setLayout(layout);
		this.setBackground(color);
		this.revalidate();
	}
}
