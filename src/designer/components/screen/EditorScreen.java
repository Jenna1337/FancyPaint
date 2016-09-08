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
	private volatile EditorScreen designer=this;
	public volatile boolean mousedown=false;
	public final Palette palette = new Palette(this);
	private int imgtype=BufferedImage.TYPE_4BYTE_ABGR;
	private BufferedImage img=new BufferedImage(100,100, imgtype);
	private GridLayout layout=new GridLayout(this.img.getHeight(), this.img.getWidth());
	
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
	private void resizeCanvas()
	{
		this.removeAll();
		this.setLayout(layout);
		System.out.println(this.img.getHeight()+" "+this.img.getWidth());
		int i=0;
		for(int ix=0; ix<this.img.getHeight(); ++ix)
		{
			for(int iy=0; iy<this.img.getWidth(); ++iy)
			{
				System.out.println(ix+" "+iy+" "+this.img.getHeight()+" "+this.img.getWidth());
				final int cix = iy;
				final int ciy = ix;
				final int ti=i++;
				this.add(new ScaleablePixelBox(new Thread(){
					public void run()
					{
						designer.getComponent(ti).setBackground(palette.getSelectedColor());
						img.setRGB(cix, ciy, palette.getSelectedColor().getRGB());;
					}
				}, new Color(img.getRGB(ix, iy)), this, false));
				designer.getComponent(ti).setBackground(palette.getSelectedColor());
				img.setRGB(cix, ciy, palette.getSelectedColor().getRGB());;
			}
		}
		this.revalidate();
	}
	public void newImg(int[] wh)
	{
		this.img = new BufferedImage(wh[0], wh[1], imgtype);
		Thread.currentThread();
		Thread.yield();
		this.resizeCanvas();
	}
	public BufferedImage getData()
	{
		return this.img;
	}
	public void setData(BufferedImage img) throws IOException
	{
		this.img=img;
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
