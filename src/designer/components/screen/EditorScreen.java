package designer.components.screen;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JPanel;
import designer.Palette;

@SuppressWarnings("serial")
public class EditorScreen extends JPanel
{
	private volatile EditorScreen designer=this;
	public volatile boolean mousedown=false;
	private Palette palette = new Palette(this);
	private int imgtype=BufferedImage.TYPE_4BYTE_ABGR;
	private BufferedImage img=new BufferedImage((short)(10+Short.MIN_VALUE),(short)(10+Short.MIN_VALUE), imgtype);
	
	public EditorScreen()
	{
		resizeCanvas();
	}
	public void repaintCanvas()
	{
		this.getParent().repaint();
		//TODO?
	}
	private void resizeCanvas()
	{
		this.removeAll();
		this.setLayout(new GridLayout(this.img.getHeight(), this.img.getWidth()));
		System.out.println(this.img.getHeight()+" "+this.img.getWidth());
		int i=0;
		for(int ix=0; ix<this.img.getWidth(); ++ix)
		{
			for(int iy=0; iy<this.img.getHeight(); ++iy)
			{
				System.out.println(ix+" "+iy+" "+this.img.getHeight()+" "+this.img.getWidth());
				final int cix = ix;
				final int ciy = iy;
				final int ti=i++;
				this.add(new ScaleablePixelBox(new Thread(){
					public void run()
					{
						designer.getComponent(ti).setBackground(palette.getSelectedColor());
						img.setRGB(cix, ciy, palette.getSelectedColor().getRGB());;
					}
				}, new Color(img.getRGB(ix, iy)), this));
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
}
