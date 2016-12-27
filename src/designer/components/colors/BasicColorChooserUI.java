/*
 * Copyright (c) 1997, 2012, Oracle and/or its affiliates. All rights reserved.
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

package designer.components.colors;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.LookAndFeel;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ColorChooserUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import sun.swing.DefaultLookup;

/**
 * Provides the basic look and feel for a JColorChooser.
 * <p>
 * @author Tom Santos
 * @author Steve Wilson
 */

public class BasicColorChooserUI extends ColorChooserUI
{
    /**
     * JColorChooser this BasicColorChooserUI is installed on.
     *
     * @since 1.5
     */
    protected JColorChooser chooser;

    JTabbedPane tabbedPane;
    JPanel singlePanel;

    JPanel previewPanelHolder;
    JComponent previewPanel;
    boolean isMultiPanel = false;
    private static TransferHandler defaultTransferHandler = new ColorTransferHandler();

    protected ColorChooserPanel[] defaultChoosers;

    protected ChangeListener previewListener;
    protected PropertyChangeListener propertyChangeListener;
    private Handler handler;

    public BasicColorChooserUI()
	{
    	System.out.println("new BasicColorChooserUI()");
	}
    public static ComponentUI createUI(JComponent c) {
        return new BasicColorChooserUI();
    }

    protected ColorChooserPanel[] createDefaultChoosers() {
        ColorChooserPanel[] panels = new ColorChooserPanel[] {
                new ColorChooserPanel(new ColorModel()),
                new ColorChooserPanel(new ColorModelHSV()),
                new ColorChooserPanel(new ColorModelHSL()),
                new ColorChooserPanel(new ColorModelCMYK()),
        };
        return panels;
    }

    protected void uninstallDefaultChoosers() {
        AbstractColorChooserPanel[] choosers = chooser.getChooserPanels();
        for( int i = 0 ; i < choosers.length; i++) {
            chooser.removeChooserPanel( choosers[i] );
        }
    }

    public void installUI( JComponent c ) {
        chooser = (JColorChooser)c;

        super.installUI( c );

        installDefaults();
        installListeners();

        tabbedPane = new JTabbedPane();
        tabbedPane.setName("ColorChooser.tabPane");
        tabbedPane.setInheritsPopupMenu(true);
        tabbedPane.getAccessibleContext().setAccessibleDescription(tabbedPane.getName());
        singlePanel = new JPanel(new CenterLayout());
        singlePanel.setName("ColorChooser.panel");
        singlePanel.setInheritsPopupMenu(true);

        chooser.setLayout( new BorderLayout() );

        defaultChoosers = createDefaultChoosers();
        chooser.setChooserPanels(defaultChoosers);

        previewPanelHolder = new JPanel(new CenterLayout());
        previewPanelHolder.setName("ColorChooser.previewPanelHolder");

        if (DefaultLookup.getBoolean(chooser, this,
                                  "ColorChooser.showPreviewPanelText", true)) {
            String previewString = UIManager.getString(
                "ColorChooser.previewText", chooser.getLocale());
            previewPanelHolder.setBorder(new TitledBorder(previewString));
        }
        previewPanelHolder.setInheritsPopupMenu(true);

        installPreviewPanel();
        chooser.applyComponentOrientation(c.getComponentOrientation());
    }

    public void uninstallUI( JComponent c ) {
        chooser.remove(tabbedPane);
        chooser.remove(singlePanel);
        chooser.remove(previewPanelHolder);

        uninstallDefaultChoosers();
        uninstallListeners();
        uninstallPreviewPanel();
        uninstallDefaults();

        previewPanelHolder = null;
        previewPanel = null;
        defaultChoosers = null;
        chooser = null;
        tabbedPane = null;

        handler = null;
    }

    protected void installPreviewPanel() {
        JComponent previewPanel = this.chooser.getPreviewPanel();
        if (previewPanel == null) {
            previewPanel = ColorChooserComponentFactory.getPreviewPanel();
        }
        else if (JPanel.class.equals(previewPanel.getClass()) && (0 == previewPanel.getComponentCount())) {
            previewPanel = null;
        }
        this.previewPanel = previewPanel;
        if (previewPanel != null) {
            chooser.add(previewPanelHolder, BorderLayout.SOUTH);
            previewPanel.setForeground(chooser.getColor());
            previewPanelHolder.add(previewPanel);
            previewPanel.addMouseListener(getHandler());
            previewPanel.setInheritsPopupMenu(true);
        }
    }

    /**
     * Removes installed preview panel from the UI delegate.
     *
     * @since 1.7
     */
    protected void uninstallPreviewPanel() {
        if (this.previewPanel != null) {
            this.previewPanel.removeMouseListener(getHandler());
            this.previewPanelHolder.remove(this.previewPanel);
        }
        this.chooser.remove(this.previewPanelHolder);
    }

    protected void installDefaults() {
        LookAndFeel.installColorsAndFont(chooser, "ColorChooser.background",
                                              "ColorChooser.foreground",
                                              "ColorChooser.font");
        LookAndFeel.installProperty(chooser, "opaque", Boolean.TRUE);
        TransferHandler th = chooser.getTransferHandler();
        if (th == null || th instanceof UIResource) {
            chooser.setTransferHandler(defaultTransferHandler);
        }
    }

    protected void uninstallDefaults() {
        if (chooser.getTransferHandler() instanceof UIResource) {
            chooser.setTransferHandler(null);
        }
    }


    protected void installListeners() {
        propertyChangeListener = createPropertyChangeListener();
        chooser.addPropertyChangeListener( propertyChangeListener );

        previewListener = getHandler();
        chooser.getSelectionModel().addChangeListener(previewListener);
    }

    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }

    protected PropertyChangeListener createPropertyChangeListener() {
        return getHandler();
    }

    protected void uninstallListeners() {
        chooser.removePropertyChangeListener( propertyChangeListener );
        chooser.getSelectionModel().removeChangeListener(previewListener);
        previewListener = null;
    }

    private void selectionChanged(ColorSelectionModel model) {
        JComponent previewPanel = this.chooser.getPreviewPanel();
        if (previewPanel != null) {
            previewPanel.setForeground(model.getSelectedColor());
            previewPanel.repaint();
        }
        AbstractColorChooserPanel[] panels = this.chooser.getChooserPanels();
        if (panels != null) {
            for (AbstractColorChooserPanel panel : panels) {
                if (panel != null) {
                    panel.updateChooser();
                }
            }
        }
    }

    private class Handler implements ChangeListener, MouseListener,
            PropertyChangeListener {
        //
        // ChangeListener
        //
        public void stateChanged(ChangeEvent evt) {
            selectionChanged((ColorSelectionModel) evt.getSource());
        }

        //
        // MouseListener
        public void mousePressed(MouseEvent evt) {
            if (chooser.getDragEnabled()) {
                TransferHandler th = chooser.getTransferHandler();
                th.exportAsDrag(chooser, evt, TransferHandler.COPY);
            }
        }
        public void mouseReleased(MouseEvent evt) {}
        public void mouseClicked(MouseEvent evt) {}
        public void mouseEntered(MouseEvent evt) {}
        public void mouseExited(MouseEvent evt) {}

        //
        // PropertyChangeListener
        //
        public void propertyChange(PropertyChangeEvent evt) {
            String prop = evt.getPropertyName();

            if (prop == JColorChooser.CHOOSER_PANELS_PROPERTY) {
                AbstractColorChooserPanel[] oldPanels =
                    (AbstractColorChooserPanel[])evt.getOldValue();
                AbstractColorChooserPanel[] newPanels =
                		 (AbstractColorChooserPanel[])evt.getNewValue();

                for (int i = 0; i < oldPanels.length; i++) {  // remove old panels
                   Container wrapper = oldPanels[i].getParent();
                    if (wrapper != null) {
                      Container parent = wrapper.getParent();
                      if (parent != null)
                          parent.remove(wrapper);  // remove from hierarchy
                      oldPanels[i].uninstallChooserPanel(chooser); // uninstall
                    }
                }

                int numNewPanels = newPanels.length;
                if (numNewPanels == 0) {  // removed all panels and added none
                    chooser.remove(tabbedPane);
                    return;
                }
                else if (numNewPanels == 1) {  // one panel case
                    chooser.remove(tabbedPane);
                    JPanel centerWrapper = new JPanel( new CenterLayout() );
                    centerWrapper.setInheritsPopupMenu(true);
                    centerWrapper.add(newPanels[0]);
                    singlePanel.add(centerWrapper, BorderLayout.CENTER);
                    chooser.add(singlePanel);
                }
                else {   // multi-panel case
                    if ( oldPanels.length < 2 ) {// moving from single to multiple
                        chooser.remove(singlePanel);
                        chooser.add(tabbedPane, BorderLayout.CENTER);
                    }

                    for (int i = 0; i < newPanels.length; i++) {
                        JPanel centerWrapper = new JPanel( new CenterLayout() );
                        centerWrapper.setInheritsPopupMenu(true);
                        String name = newPanels[i].getDisplayName();
                        int mnemonic = newPanels[i].getMnemonic();
                        centerWrapper.add(newPanels[i]);
                        tabbedPane.addTab(name, centerWrapper);
                        if (mnemonic > 0) {
                            tabbedPane.setMnemonicAt(i, mnemonic);
                            int index = newPanels[i].getDisplayedMnemonicIndex();
                            if (index >= 0) {
                                tabbedPane.setDisplayedMnemonicIndexAt(i, index);
                            }
                        }
                    }
                }
                chooser.applyComponentOrientation(chooser.getComponentOrientation());
                for (int i = 0; i < newPanels.length; i++) {
                    newPanels[i].installChooserPanel(chooser);
                }
            }
            else if (prop == JColorChooser.PREVIEW_PANEL_PROPERTY) {
                uninstallPreviewPanel();
                installPreviewPanel();
            }
            else if (prop == JColorChooser.SELECTION_MODEL_PROPERTY) {
                ColorSelectionModel oldModel = (ColorSelectionModel) evt.getOldValue();
                oldModel.removeChangeListener(previewListener);
                ColorSelectionModel newModel = (ColorSelectionModel) evt.getNewValue();
                newModel.addChangeListener(previewListener);
                selectionChanged(newModel);
            }
            else if (prop == "componentOrientation") {
                ComponentOrientation o =
                    (ComponentOrientation)evt.getNewValue();
                JColorChooser cc = (JColorChooser)evt.getSource();
                if (o != (ComponentOrientation)evt.getOldValue()) {
                    cc.applyComponentOrientation(o);
                    cc.updateUI();
                }
            }
        }
    }

    /**
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of {@code BasicColorChooserUI}.
     */
    public class PropertyHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            getHandler().propertyChange(e);
        }
    }

    static class ColorTransferHandler extends TransferHandler implements UIResource {

        ColorTransferHandler() {
            super("color");
        }
    }
}
/**
 * Center-positioning layout manager.
 * @author Tom Santos
 * @author Steve Wilson
 */
class CenterLayout implements LayoutManager, Serializable {
   public void addLayoutComponent(String name, Component comp) { }
   public void removeLayoutComponent(Component comp) { }

   public Dimension preferredLayoutSize( Container container ) {
       Component c = container.getComponent( 0 );
       if ( c != null ) {
           Dimension size = c.getPreferredSize();
           Insets insets = container.getInsets();
           size.width += insets.left + insets.right;
           size.height += insets.top + insets.bottom;
           return size;
       }
       else {
           return new Dimension( 0, 0 );
       }
   }

   public Dimension minimumLayoutSize(Container cont) {
       return preferredLayoutSize(cont);
   }

   public void layoutContainer(Container container) {
       try {
          Component c = container.getComponent( 0 );

          c.setSize( c.getPreferredSize() );
          Dimension size = c.getSize();
          Dimension containerSize = container.getSize();
          Insets containerInsets = container.getInsets();
          containerSize.width -= containerInsets.left + containerInsets.right;
          containerSize.height -= containerInsets.top + containerInsets.bottom;
          int componentLeft = (containerSize.width / 2) - (size.width / 2);
          int componentTop = (containerSize.height / 2) - (size.height / 2);
          componentLeft += containerInsets.left;
          componentTop += containerInsets.top;

           c.setBounds( componentLeft, componentTop, size.width, size.height );
        }
        catch( Exception e ) {
        }
   }
}
