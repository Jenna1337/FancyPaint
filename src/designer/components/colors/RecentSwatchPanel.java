package designer.components.colors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
@SuppressWarnings("serial")
public class RecentSwatchPanel extends SwatchPanel {
    protected void initValues() {
        swatchSize = UIManager.getDimension("ColorChooser.swatchesRecentSwatchSize", getLocale());
        numSwatches = new Dimension( 5, 7 );
        gap = new Dimension(1, 1);
    }


    protected void initColors() {
        Color defaultRecentColor = UIManager.getColor("ColorChooser.swatchesDefaultRecentColor", getLocale());
        int numColors = numSwatches.width * numSwatches.height;

        colors = new Color[numColors];
        for (int i = 0; i < numColors ; i++) {
            colors[i] = defaultRecentColor;
        }
    }

    public void setMostRecentColor(Color c) {

        System.arraycopy( colors, 0, colors, 1, colors.length-1);
        colors[0] = c;
        repaint();
    }

}

@SuppressWarnings("serial")
class SwatchPanel extends JPanel {

    protected Color[] colors;
    protected Dimension swatchSize;
    protected Dimension numSwatches;
    protected Dimension gap;

    private int selRow;
    private int selCol;

    public SwatchPanel() {
        initValues();
        initColors();
        setToolTipText(""); // register for events
        setOpaque(true);
        setBackground(Color.white);
        setFocusable(true);
        setInheritsPopupMenu(true);

        addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                repaint();
            }

            public void focusLost(FocusEvent e) {
                repaint();
            }
        });

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int typed = e.getKeyCode();
                switch (typed) {
                    case KeyEvent.VK_UP:
                        if (selRow > 0) {
                            selRow--;
                            repaint();
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (selRow < numSwatches.height - 1) {
                            selRow++;
                            repaint();
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (selCol > 0 && SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                            selCol--;
                            repaint();
                        } else if (selCol < numSwatches.width - 1
                                && !SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                            selCol++;
                            repaint();
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (selCol < numSwatches.width - 1
                                && SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                            selCol++;
                            repaint();
                        } else if (selCol > 0 && !SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                            selCol--;
                            repaint();
                        }
                        break;
                    case KeyEvent.VK_HOME:
                        selCol = 0;
                        selRow = 0;
                        repaint();
                        break;
                    case KeyEvent.VK_END:
                        selCol = numSwatches.width - 1;
                        selRow = numSwatches.height - 1;
                        repaint();
                        break;
                }
            }
        });
    }

    public Color getSelectedColor() {
        return getColorForCell(selCol, selRow);
    }

    protected void initValues() {

    }

    public void paintComponent(Graphics g) {
         g.setColor(getBackground());
         g.fillRect(0,0,getWidth(), getHeight());
         for (int row = 0; row < numSwatches.height; row++) {
            int y = row * (swatchSize.height + gap.height);
            for (int column = 0; column < numSwatches.width; column++) {
                Color c = getColorForCell(column, row);
                g.setColor(c);
                int x;
                if (!this.getComponentOrientation().isLeftToRight()) {
                    x = (numSwatches.width - column - 1) * (swatchSize.width + gap.width);
                } else {
                    x = column * (swatchSize.width + gap.width);
                }
                g.fillRect( x, y, swatchSize.width, swatchSize.height);
                g.setColor(Color.black);
                g.drawLine( x+swatchSize.width-1, y, x+swatchSize.width-1, y+swatchSize.height-1);
                g.drawLine( x, y+swatchSize.height-1, x+swatchSize.width-1, y+swatchSize.height-1);

                if (selRow == row && selCol == column && this.isFocusOwner()) {
                    Color c2 = new Color(c.getRed() < 125 ? 255 : 0,
                            c.getGreen() < 125 ? 255 : 0,
                            c.getBlue() < 125 ? 255 : 0);
                    g.setColor(c2);

                    g.drawLine(x, y, x + swatchSize.width - 1, y);
                    g.drawLine(x, y, x, y + swatchSize.height - 1);
                    g.drawLine(x + swatchSize.width - 1, y, x + swatchSize.width - 1, y + swatchSize.height - 1);
                    g.drawLine(x, y + swatchSize.height - 1, x + swatchSize.width - 1, y + swatchSize.height - 1);
                    g.drawLine(x, y, x + swatchSize.width - 1, y + swatchSize.height - 1);
                    g.drawLine(x, y + swatchSize.height - 1, x + swatchSize.width - 1, y);
                }
            }
         }
    }

    public Dimension getPreferredSize() {
        int x = numSwatches.width * (swatchSize.width + gap.width) - 1;
        int y = numSwatches.height * (swatchSize.height + gap.height) - 1;
        return new Dimension( x, y );
    }

    protected void initColors() {


    }

    public String getToolTipText(MouseEvent e) {
        Color color = getColorForLocation(e.getX(), e.getY());
        return color.getRed()+", "+ color.getGreen() + ", " + color.getBlue();
    }

    public void setSelectedColorFromLocation(int x, int y) {
        if (!this.getComponentOrientation().isLeftToRight()) {
            selCol = numSwatches.width - x / (swatchSize.width + gap.width) - 1;
        } else {
            selCol = x / (swatchSize.width + gap.width);
        }
        selRow = y / (swatchSize.height + gap.height);
        repaint();
    }

    public Color getColorForLocation( int x, int y ) {
        int column;
        if (!this.getComponentOrientation().isLeftToRight()) {
            column = numSwatches.width - x / (swatchSize.width + gap.width) - 1;
        } else {
            column = x / (swatchSize.width + gap.width);
        }
        int row = y / (swatchSize.height + gap.height);
        return getColorForCell(column, row);
    }

    private Color getColorForCell( int column, int row) {
        return colors[ (row * numSwatches.width) + column ]; // (STEVE) - change data orientation here
    }




}
