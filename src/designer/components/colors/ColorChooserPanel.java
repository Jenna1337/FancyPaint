/*
 * Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.border.EmptyBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import static java.util.Locale.ENGLISH;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.DocumentFilter;

final class ColorChooserPanel extends AbstractColorChooserPanel implements PropertyChangeListener {

    private static final int MASK = 0xFF000000;
    private final ColorModel model;
    private final ColorPanel panel;
    private final DiagramComponent slider;
    private final DiagramComponent diagram;
    private final JFormattedTextField text;
    private final JLabel label;

    ColorChooserPanel(ColorModel model) {
        this.model = model;
        this.panel = new ColorPanel(this.model);
        this.slider = new DiagramComponent(this.panel, false);
        this.diagram = new DiagramComponent(this.panel, true);
        this.text = new JFormattedTextField();
        this.label = new JLabel(null, null, SwingConstants.RIGHT);
        ValueFormatter.init(6, true, this.text);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setEnabled(this, enabled);
    }

    private static void setEnabled(Container container, boolean enabled) {
        for (Component component : container.getComponents()) {
            component.setEnabled(enabled);
            if (component instanceof Container) {
                setEnabled((Container) component, enabled);
            }
        }
    }

    @Override
    public void updateChooser() {
        Color color = getColorFromModel();
        if (color != null) {
            this.panel.setColor(color);
            this.text.setValue(Integer.valueOf(color.getRGB()));
            this.slider.repaint();
            this.diagram.repaint();
        }
    }

    @Override
    protected void buildChooser() {
        if (0 == getComponentCount()) {
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();

            gbc.gridx = 3;
            gbc.gridwidth = 2;
            gbc.weighty = 1.0;
            gbc.anchor = GridBagConstraints.NORTH;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets.top = 10;
            gbc.insets.right = 10;
            add(this.panel, gbc);
/*
            gbc.gridwidth = 1;
            gbc.weightx = 1.0;
            gbc.weighty = 0.0;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets.right = 5;
            gbc.insets.bottom = 10;
            add(this.label, gbc);

            gbc.gridx = 4;
            gbc.weightx = 0.0;
            gbc.insets.right = 10;
            add(this.text, gbc);

            gbc.gridx = 2;
            gbc.gridheight = 2;
            gbc.anchor = GridBagConstraints.NORTH;
            gbc.ipadx = this.text.getPreferredSize().height;
            gbc.ipady = getPreferredSize().height;
            add(this.slider, gbc);

            gbc.gridx = 1;
            gbc.insets.left = 10;
            gbc.ipadx = gbc.ipady;
            add(this.diagram, gbc);*/

            this.label.setLabelFor(this.text);
            this.text.addPropertyChangeListener("value", this); // NON-NLS: the property name
            this.slider.setBorder(this.text.getBorder());
            this.diagram.setBorder(this.text.getBorder());

            setInheritsPopupMenu(this, true); // CR:4966112
        }
        String label = this.model.getText(this, "HexCode"); // NON-NLS: suffix
        boolean visible = label != null;
        this.text.setVisible(visible);
        this.text.getAccessibleContext().setAccessibleDescription(label);
        this.label.setVisible(visible);
        if (visible) {
            this.label.setText(label);
            int mnemonic = this.model.getInteger(this, "HexCodeMnemonic"); // NON-NLS: suffix
            if (mnemonic > 0) {
                this.label.setDisplayedMnemonic(mnemonic);
                mnemonic = this.model.getInteger(this, "HexCodeMnemonicIndex"); // NON-NLS: suffix
                if (mnemonic >= 0) {
                    this.label.setDisplayedMnemonicIndex(mnemonic);
                }
            }
        }
        this.panel.buildPanel();
    }

    @Override
    public String getDisplayName() {
        return this.model.getText(this, "Name"); // NON-NLS: suffix
    }

    @Override
    public int getMnemonic() {
        return this.model.getInteger(this, "Mnemonic"); // NON-NLS: suffix
    }

    @Override
    public int getDisplayedMnemonicIndex() {
        return this.model.getInteger(this, "DisplayedMnemonicIndex"); // NON-NLS: suffix
    }

    @Override
    public Icon getSmallDisplayIcon() {
        return null;
    }

    @Override
    public Icon getLargeDisplayIcon() {
        return null;
    }

    public void propertyChange(PropertyChangeEvent event) {
        ColorSelectionModel model = getColorSelectionModel();
        if (model != null) {
            Object object = event.getNewValue();
            if (object instanceof Integer) {
                int value = MASK & model.getSelectedColor().getRGB() | (Integer) object;
                model.setSelectedColor(new Color(value, true));
            }
        }
        this.text.selectAll();
    }

    /**
     * Allows to show context popup for all components recursively.
     *
     * @param component  the root component of the tree
     * @param value      whether or not the popup menu is inherited
     */
    private static void setInheritsPopupMenu(JComponent component, boolean value) {
        component.setInheritsPopupMenu(value);
        for (Object object : component.getComponents()) {
            if (object instanceof JComponent) {
                setInheritsPopupMenu((JComponent) object, value);
            }
        }
    }
}
final class ColorPanel extends JPanel implements ActionListener {

    private final SlidingSpinner[] spinners = new SlidingSpinner[5];
    private final float[] values = new float[this.spinners.length];

    private final ColorModel model;
    private Color color;
    private int x = 1;
    private int y = 2;
    private int z;

    ColorPanel(ColorModel model) {
        super(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 1;
        ButtonGroup group = new ButtonGroup();
        EmptyBorder border = null;
        for (int i = 0; i < this.spinners.length; i++) {
            if (i < 3) {
                JRadioButton button = new JRadioButton();
                if (i == 0) {
                    Insets insets = button.getInsets();
                    insets.left = button.getPreferredSize().width;
                    border = new EmptyBorder(insets);
                    button.setSelected(true);
                    gbc.insets.top = 5;
                }
                add(button, gbc);
                group.add(button);
                button.setActionCommand(Integer.toString(i));
                button.addActionListener(this);
                this.spinners[i] = new SlidingSpinner(this, button);
            }
            else {
                JLabel label = new JLabel();
                add(label, gbc);
                label.setBorder(border);
                label.setFocusable(false);
                this.spinners[i] = new SlidingSpinner(this, label);
            }
        }
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        gbc.insets.top = 0;
        gbc.insets.left = 5;
        for (SlidingSpinner spinner : this.spinners) {
            add(spinner.getSlider(), gbc);
            gbc.insets.top = 5;
        }
        gbc.gridx = 3;
        gbc.weightx = 0.0;
        gbc.insets.top = 0;
        for (SlidingSpinner spinner : this.spinners) {
            add(spinner.getSpinner(), gbc);
            gbc.insets.top = 5;
        }
        setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy());
        setFocusTraversalPolicyProvider(true);
        setFocusable(false);

        this.model = model;
    }

    public void actionPerformed(ActionEvent event) {
        try {
            this.z = Integer.parseInt(event.getActionCommand());
            this.y = (this.z != 2) ? 2 : 1;
            this.x = (this.z != 0) ? 0 : 1;
            getParent().repaint();
        }
        catch (NumberFormatException exception) {
        }
    }

    void buildPanel() {
        int count = this.model.getCount();
        this.spinners[4].setVisible(count > 4);
        for (int i = 0; i < count; i++) {
            String text = this.model.getLabel(this, i);
            Object object = this.spinners[i].getLabel();
            if (object instanceof JRadioButton) {
                JRadioButton button = (JRadioButton) object;
                button.setText(text);
                button.getAccessibleContext().setAccessibleDescription(text);
            }
            else if (object instanceof JLabel) {
                JLabel label = (JLabel) object;
                label.setText(text);
            }
            this.spinners[i].setRange(this.model.getMinimum(i), this.model.getMaximum(i));
            this.spinners[i].setValue(this.values[i]);
            this.spinners[i].getSlider().getAccessibleContext().setAccessibleName(text);
            this.spinners[i].getSpinner().getAccessibleContext().setAccessibleName(text);
            DefaultEditor editor = (DefaultEditor) this.spinners[i].getSpinner().getEditor();
            editor.getTextField().getAccessibleContext().setAccessibleName(text);
            this.spinners[i].getSlider().getAccessibleContext().setAccessibleDescription(text);
            this.spinners[i].getSpinner().getAccessibleContext().setAccessibleDescription(text);
            editor.getTextField().getAccessibleContext().setAccessibleDescription(text);
        }
    }

    void colorChanged() {
        this.color = new Color(getColor(0), true);
        Object parent = getParent();
        if (parent instanceof ColorChooserPanel) {
            ColorChooserPanel chooser = (ColorChooserPanel) parent;
            chooser.getColorSelectionModel().setSelectedColor(this.color);
            chooser.repaint();
        }
    }

    float getValueX() {
        return this.spinners[this.x].getValue();
    }

    float getValueY() {
        return 1.0f - this.spinners[this.y].getValue();
    }

    float getValueZ() {
        return 1.0f - this.spinners[this.z].getValue();
    }

    void setValue(float z) {
        this.spinners[this.z].setValue(1.0f - z);
        colorChanged();
    }

    void setValue(float x, float y) {
        this.spinners[this.x].setValue(x);
        this.spinners[this.y].setValue(1.0f - y);
        colorChanged();
    }

    int getColor(float z) {
        setDefaultValue(this.x);
        setDefaultValue(this.y);
        this.values[this.z] = 1.0f - z;
        return getColor(3);
    }

    int getColor(float x, float y) {
        this.values[this.x] = x;
        this.values[this.y] = 1.0f - y;
        setValue(this.z);
        return getColor(3);
    }

    void setColor(Color color) {
        if (!color.equals(this.color)) {
            this.color = color;
            this.model.setColor(color.getRGB(), this.values);
            for (int i = 0; i < this.model.getCount(); i++) {
                this.spinners[i].setValue(this.values[i]);
            }
        }
    }

    private int getColor(int index) {
        while (index < this.model.getCount()) {
            setValue(index++);
        }
        return this.model.getColor(this.values);
    }

    private void setValue(int index) {
        this.values[index] = this.spinners[index].getValue();
    }

    private void setDefaultValue(int index) {
        float value = this.model.getDefault(index);
        this.values[index] = (value < 0.0f)
                ? this.spinners[index].getValue()
                : value;
    }
}
class ColorModel {

    private final String prefix;
    private final String[] labels;

    ColorModel(String name, String... labels) {
        this.prefix = "ColorChooser." + name; // NON-NLS: default prefix
        this.labels = labels;
    }

    ColorModel() {
        this("rgb", "Red", "Green", "Blue", "Alpha"); // NON-NLS: components
    }

    void setColor(int color, float[] model) {
        model[0] = normalize(color >> 16);
        model[1] = normalize(color >> 8);
        model[2] = normalize(color);
        model[3] = normalize(color >> 24);
    }

    int getColor(float[] model) {
        return to8bit(model[2]) | (to8bit(model[1]) << 8) | (to8bit(model[0]) << 16) | (to8bit(model[3]) << 24);
    }

    int getCount() {
        return this.labels.length;
    }

    int getMinimum(int index) {
        return 0;
    }

    int getMaximum(int index) {
        return 255;
    }

    float getDefault(int index) {
        return 0.0f;
    }

    final String getLabel(Component component, int index) {
        return getText(component, this.labels[index]);
    }

    private static float normalize(int value) {
        return (float) (value & 0xFF) / 255.0f;
    }

    private static int to8bit(float value) {
        return (int) (255.0f * value);
    }

    final String getText(Component component, String suffix) {
        return UIManager.getString(this.prefix + suffix + "Text", component.getLocale()); // NON-NLS: default postfix
    }

    final int getInteger(Component component, String suffix) {
        Object value = UIManager.get(this.prefix + suffix, component.getLocale());
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            }
            catch (NumberFormatException exception) {
            }
        }
        return -1;
    }
}
final class SlidingSpinner implements ChangeListener {

    private final ColorPanel panel;
    private final JComponent label;
    private final SpinnerNumberModel model = new SpinnerNumberModel();
    private final JSlider slider = new JSlider();
    private final JSpinner spinner = new JSpinner(this.model);
    private float value;
    private boolean internal;

    SlidingSpinner(ColorPanel panel, JComponent label) {
        this.panel = panel;
        this.label = label;
        this.slider.addChangeListener(this);
        this.spinner.addChangeListener(this);
        DefaultEditor editor = (DefaultEditor) this.spinner.getEditor();
        ValueFormatter.init(3, false, editor.getTextField());
        editor.setFocusable(false);
        this.spinner.setFocusable(false);
    }

    JComponent getLabel() {
        return this.label;
    }

    JSlider getSlider() {
        return this.slider;
    }

    JSpinner getSpinner() {
        return this.spinner;
    }

    float getValue() {
        return this.value;
    }

    void setValue(float value) {
        int min = this.slider.getMinimum();
        int max = this.slider.getMaximum();
        this.internal = true;
        this.slider.setValue(min + (int) (value * (float) (max - min)));
        this.spinner.setValue(Integer.valueOf(this.slider.getValue()));
        this.internal = false;
        this.value = value;
    }

    void setRange(int min, int max) {
        this.internal = true;
        this.slider.setMinimum(min);
        this.slider.setMaximum(max);
        this.model.setMinimum(Integer.valueOf(min));
        this.model.setMaximum(Integer.valueOf(max));
        this.internal = false;
    }

    void setVisible(boolean visible) {
        this.label.setVisible(visible);
        this.slider.setVisible(visible);
        this.spinner.setVisible(visible);
    }

    public void stateChanged(ChangeEvent event) {
        if (!this.internal) {
            if (this.spinner == event.getSource()) {
                Object value = this.spinner.getValue();
                if (value instanceof Integer) {
                    this.internal = true;
                    this.slider.setValue((Integer) value);
                    this.internal = false;
                }
            }
            int value = this.slider.getValue();
            this.internal = true;
            this.spinner.setValue(Integer.valueOf(value));
            this.internal = false;
            int min = this.slider.getMinimum();
            int max = this.slider.getMaximum();
            this.value = (float) (value - min) / (float) (max - min);
            this.panel.colorChanged();
        }
    }
}
final class DiagramComponent extends JComponent implements MouseListener, MouseMotionListener {

    private final ColorPanel panel;
    private final boolean diagram;

    private final Insets insets = new Insets(0, 0, 0, 0);

    private int width;
    private int height;

    private int[] array;
    private BufferedImage image;

    DiagramComponent(ColorPanel panel, boolean diagram) {
        this.panel = panel;
        this.diagram = diagram;
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        getInsets(this.insets);
        this.width = getWidth() - this.insets.left - this.insets.right;
        this.height = getHeight() - this.insets.top - this.insets.bottom;

        boolean update = (this.image == null)
                || (this.width != this.image.getWidth())
                || (this.height != this.image.getHeight());
        if (update) {
            int size = this.width * this.height;
            if ((this.array == null) || (this.array.length < size)) {
                this.array = new int[size];
            }
            this.image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
        }
        {
            float dx = 1.0f / (float) (this.width - 1);
            float dy = 1.0f / (float) (this.height - 1);

            int offset = 0;
            float y = 0.0f;
            for (int h = 0; h < this.height; h++, y += dy) {
                if (this.diagram) {
                    float x = 0.0f;
                    for (int w = 0; w < this.width; w++, x += dx, offset++) {
                        this.array[offset] = this.panel.getColor(x, y);
                    }
                }
                else {
                    int color = this.panel.getColor(y);
                    for (int w = 0; w < this.width; w++, offset++) {
                        this.array[offset] = color;
                    }
                }
            }
        }
        this.image.setRGB(0, 0, this.width, this.height, this.array, 0, this.width);
        g.drawImage(this.image, this.insets.left, this.insets.top, this.width, this.height, this);
        if (isEnabled()) {
            this.width--;
            this.height--;
            g.setXORMode(Color.WHITE);
            g.setColor(Color.BLACK);
            if (this.diagram) {
                int x = getValue(this.panel.getValueX(), this.insets.left, this.width);
                int y = getValue(this.panel.getValueY(), this.insets.top, this.height);
                g.drawLine(x - 8, y, x + 8, y);
                g.drawLine(x, y - 8, x, y + 8);
            }
            else {
                int z = getValue(this.panel.getValueZ(), this.insets.top, this.height);
                g.drawLine(this.insets.left, z, this.insets.left + this.width, z);
            }
            g.setPaintMode();
        }
    }

    public void mousePressed(MouseEvent event) {
        mouseDragged(event);
    }

    public void mouseReleased(MouseEvent event) {
    }

    public void mouseClicked(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }

    public void mouseMoved(MouseEvent event) {
    }

    public void mouseDragged(MouseEvent event) {
        if (isEnabled()) {
            float y = getValue(event.getY(), this.insets.top, this.height);
            if (this.diagram) {
                float x = getValue(event.getX(), this.insets.left, this.width);
                this.panel.setValue(x, y);
            }
            else {
                this.panel.setValue(y);
            }
        }
    }

    private static int getValue(float value, int min, int max) {
        return min + (int) (value * (float) (max));
    }

    private static float getValue(int value, int min, int max) {
        if (min < value) {
            value -= min;
            return (value < max)
                    ? (float) value / (float) max
                    : 1.0f;
        }
        return 0.0f;
    }
}
final class ValueFormatter extends AbstractFormatter implements FocusListener, Runnable {

    static void init(int length, boolean hex, JFormattedTextField text) {
        ValueFormatter formatter = new ValueFormatter(length, hex);
        text.setColumns(length);
        text.setFormatterFactory(new DefaultFormatterFactory(formatter));
        text.setHorizontalAlignment(SwingConstants.RIGHT);
        text.setMinimumSize(text.getPreferredSize());
        text.addFocusListener(formatter);
    }

    private final DocumentFilter filter = new DocumentFilter() {
        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if (isValid(fb.getDocument().getLength() - length)) {
                fb.remove(offset, length);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet set) throws BadLocationException {
            if (isValid(fb.getDocument().getLength() + text.length() - length) && isValid(text)) {
                fb.replace(offset, length, text.toUpperCase(ENGLISH), set);
            }
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet set) throws BadLocationException {
            if (isValid(fb.getDocument().getLength() + text.length()) && isValid(text)) {
                fb.insertString(offset, text.toUpperCase(ENGLISH), set);
            }
        }
    };

    private final int length;
    private final int radix;

    private JFormattedTextField text;

    ValueFormatter(int length, boolean hex) {
        this.length = length;
        this.radix = hex ? 16 : 10;
    }

    @Override
    public Object stringToValue(String text) throws ParseException {
        try {
            return Integer.valueOf(text, this.radix);
        }
        catch (NumberFormatException nfe) {
            ParseException pe = new ParseException("illegal format", 0);
            pe.initCause(nfe);
            throw pe;
        }
    }

    @Override
    public String valueToString(Object object) throws ParseException {
        if (object instanceof Integer) {
            if (this.radix == 10) {
                return object.toString();
            }
            int value = (Integer) object;
            int index = this.length;
            char[] array = new char[index];
            while (0 < index--) {
                array[index] = Character.forDigit(value & 0x0F, this.radix);
                value >>= 4;
            }
            return new String(array).toUpperCase(ENGLISH);
        }
        throw new ParseException("illegal object", 0);
    }

    @Override
    protected DocumentFilter getDocumentFilter() {
        return this.filter;
    }

    public void focusGained(FocusEvent event) {
        Object source = event.getSource();
        if (source instanceof JFormattedTextField) {
            this.text = (JFormattedTextField) source;
            SwingUtilities.invokeLater(this);
        }
    }

    public void focusLost(FocusEvent event) {
    }

    public void run() {
        if (this.text != null) {
            this.text.selectAll();
        }
    }

    private boolean isValid(int length) {
        return (0 <= length) && (length <= this.length);
    }

    private boolean isValid(String text) {
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char ch = text.charAt(i);
            if (Character.digit(ch, this.radix) < 0) {
                return false;
            }
        }
        return true;
    }
}
final class ColorModelHSV extends ColorModel {

    ColorModelHSV() {
        super("hsv", "Hue", "Saturation", "Value", "Transparency"); // NON-NLS: components
    }

    @Override
    void setColor(int color, float[] space) {
        super.setColor(color, space);
        RGBtoHSV(space, space);
        space[3] = 1.0f - space[3];
    }

    @Override
    int getColor(float[] space) {
        space[3] = 1.0f - space[3];
        HSVtoRGB(space, space);
        return super.getColor(space);
    }

    @Override
    int getMaximum(int index) {
        return (index == 0) ? 360 : 100;
    }

    @Override
    float getDefault(int index) {
        return (index == 0) ? -1.0f : 1.0f;
    }

    /**
     * Converts HSV components of a color to a set of RGB components.
     *
     * @param hsv  a float array with length equal to
     *             the number of HSV components
     * @param rgb  a float array with length of at least 3
     *             that contains RGB components of a color
     * @return a float array that contains RGB components
     */
    private static float[] HSVtoRGB(float[] hsv, float[] rgb) {
        if (rgb == null) {
            rgb = new float[3];
        }
        float hue = hsv[0];
        float saturation = hsv[1];
        float value = hsv[2];

        rgb[0] = value;
        rgb[1] = value;
        rgb[2] = value;

        if (saturation > 0.0f) {
            hue = (hue < 1.0f) ? hue * 6.0f : 0.0f;
            int integer = (int) hue;
            float f = hue - (float) integer;
            switch (integer) {
                case 0:
                    rgb[1] *= 1.0f - saturation * (1.0f - f);
                    rgb[2] *= 1.0f - saturation;
                    break;
                case 1:
                    rgb[0] *= 1.0f - saturation * f;
                    rgb[2] *= 1.0f - saturation;
                    break;
                case 2:
                    rgb[0] *= 1.0f - saturation;
                    rgb[2] *= 1.0f - saturation * (1.0f - f);
                    break;
                case 3:
                    rgb[0] *= 1.0f - saturation;
                    rgb[1] *= 1.0f - saturation * f;
                    break;
                case 4:
                    rgb[0] *= 1.0f - saturation * (1.0f - f);
                    rgb[1] *= 1.0f - saturation;
                    break;
                case 5:
                    rgb[1] *= 1.0f - saturation;
                    rgb[2] *= 1.0f - saturation * f;
                    break;
            }
        }
        return rgb;
    }

    /**
     * Converts RGB components of a color to a set of HSV components.
     *
     * @param rgb  a float array with length of at least 3
     *             that contains RGB components of a color
     * @param hsv  a float array with length equal to
     *             the number of HSV components
     * @return a float array that contains HSV components
     */
    private static float[] RGBtoHSV(float[] rgb, float[] hsv) {
        if (hsv == null) {
            hsv = new float[3];
        }
        float max = ColorModelHSL.max(rgb[0], rgb[1], rgb[2]);
        float min = ColorModelHSL.min(rgb[0], rgb[1], rgb[2]);

        float saturation = max - min;
        if (saturation > 0.0f) {
            saturation /= max;
        }
        hsv[0] = ColorModelHSL.getHue(rgb[0], rgb[1], rgb[2], max, min);
        hsv[1] = saturation;
        hsv[2] = max;
        return hsv;
    }
}
final class ColorModelHSL extends ColorModel {

    ColorModelHSL() {
        super("hsl", "Hue", "Saturation", "Lightness", "Transparency"); // NON-NLS: components
    }

    @Override
    void setColor(int color, float[] space) {
        super.setColor(color, space);
        RGBtoHSL(space, space);
        space[3] = 1.0f - space[3];
    }

    @Override
    int getColor(float[] space) {
        space[3] = 1.0f - space[3];
        HSLtoRGB(space, space);
        return super.getColor(space);
    }

    @Override
    int getMaximum(int index) {
        return (index == 0) ? 360 : 100;
    }

    @Override
    float getDefault(int index) {
        return (index == 0) ? -1.0f : (index == 2) ? 0.5f : 1.0f;
    }

    /**
     * Converts HSL components of a color to a set of RGB components.
     *
     * @param hsl  a float array with length equal to
     *             the number of HSL components
     * @param rgb  a float array with length of at least 3
     *             that contains RGB components of a color
     * @return a float array that contains RGB components
     */
    private static float[] HSLtoRGB(float[] hsl, float[] rgb) {
        if (rgb == null) {
            rgb = new float[3];
        }
        float hue = hsl[0];
        float saturation = hsl[1];
        float lightness = hsl[2];

        if (saturation > 0.0f) {
            hue = (hue < 1.0f) ? hue * 6.0f : 0.0f;
            float q = lightness + saturation * ((lightness > 0.5f) ? 1.0f - lightness : lightness);
            float p = 2.0f * lightness - q;
            rgb[0]= normalize(q, p, (hue < 4.0f) ? (hue + 2.0f) : (hue - 4.0f));
            rgb[1]= normalize(q, p, hue);
            rgb[2]= normalize(q, p, (hue < 2.0f) ? (hue + 4.0f) : (hue - 2.0f));
        }
        else {
            rgb[0] = lightness;
            rgb[1] = lightness;
            rgb[2] = lightness;
        }
        return rgb;
    }

    /**
     * Converts RGB components of a color to a set of HSL components.
     *
     * @param rgb  a float array with length of at least 3
     *             that contains RGB components of a color
     * @param hsl  a float array with length equal to
     *             the number of HSL components
     * @return a float array that contains HSL components
     */
    private static float[] RGBtoHSL(float[] rgb, float[] hsl) {
        if (hsl == null) {
            hsl = new float[3];
        }
        float max = max(rgb[0], rgb[1], rgb[2]);
        float min = min(rgb[0], rgb[1], rgb[2]);

        float summa = max + min;
        float saturation = max - min;
        if (saturation > 0.0f) {
            saturation /= (summa > 1.0f)
                    ? 2.0f - summa
                    : summa;
        }
        hsl[0] = getHue(rgb[0], rgb[1], rgb[2], max, min);
        hsl[1] = saturation;
        hsl[2] = summa / 2.0f;
        return hsl;
    }

    /**
     * Returns the smaller of three color components.
     *
     * @param red    the red component of the color
     * @param green  the green component of the color
     * @param blue   the blue component of the color
     * @return the smaller of {@code red}, {@code green} and {@code blue}
     */
    static float min(float red, float green, float blue) {
        float min = (red < green) ? red : green;
        return (min < blue) ? min : blue;
    }

    /**
     * Returns the larger of three color components.
     *
     * @param red    the red component of the color
     * @param green  the green component of the color
     * @param blue   the blue component of the color
     * @return the larger of {@code red}, {@code green} and {@code blue}
     */
    static float max(float red, float green, float blue) {
        float max = (red > green) ? red : green;
        return (max > blue) ? max : blue;
    }

    /**
     * Calculates the hue component for HSL and HSV color spaces.
     *
     * @param red    the red component of the color
     * @param green  the green component of the color
     * @param blue   the blue component of the color
     * @param max    the larger of {@code red}, {@code green} and {@code blue}
     * @param min    the smaller of {@code red}, {@code green} and {@code blue}
     * @return the hue component
     */
    static float getHue(float red, float green, float blue, float max, float min) {
        float hue = max - min;
        if (hue > 0.0f) {
            if (max == red) {
                hue = (green - blue) / hue;
                if (hue < 0.0f) {
                    hue += 6.0f;
                }
            }
            else if (max == green) {
                hue = 2.0f + (blue - red) / hue;
            }
            else /*max == blue*/ {
                hue = 4.0f + (red - green) / hue;
            }
            hue /= 6.0f;
        }
        return hue;
    }

    private static float normalize(float q, float p, float color) {
        if (color < 1.0f) {
            return p + (q - p) * color;
        }
        if (color < 3.0f) {
            return q;
        }
        if (color < 4.0f) {
            return p + (q - p) * (4.0f - color);
        }
        return p;
    }
}
final class ColorModelCMYK extends ColorModel {

    ColorModelCMYK() {
        super("cmyk", "Cyan", "Magenta", "Yellow", "Black", "Alpha"); // NON-NLS: components
    }

    @Override
    void setColor(int color, float[] space) {
        super.setColor(color, space);
        space[4] = space[3];
        RGBtoCMYK(space, space);
    }

    @Override
    int getColor(float[] space) {
        CMYKtoRGB(space, space);
        space[3] = space[4];
        return super.getColor(space);
    }

    /**
     * Converts CMYK components of a color to a set of RGB components.
     *
     * @param cmyk  a float array with length equal to
     *              the number of CMYK components
     * @param rgb   a float array with length of at least 3
     *              that contains RGB components of a color
     * @return a float array that contains RGB components
     */
    private static float[] CMYKtoRGB(float[] cmyk, float[] rgb) {
        if (rgb == null) {
            rgb = new float[3];
        }
        rgb[0] = 1.0f + cmyk[0] * cmyk[3] - cmyk[3] - cmyk[0];
        rgb[1] = 1.0f + cmyk[1] * cmyk[3] - cmyk[3] - cmyk[1];
        rgb[2] = 1.0f + cmyk[2] * cmyk[3] - cmyk[3] - cmyk[2];
        return rgb;
    }

    /**
     * Converts RGB components of a color to a set of CMYK components.
     *
     * @param rgb   a float array with length of at least 3
     *              that contains RGB components of a color
     * @param cmyk  a float array with length equal to
     *              the number of CMYK components
     * @return a float array that contains CMYK components
     */
    private static float[] RGBtoCMYK(float[] rgb, float[] cmyk) {
        if (cmyk == null) {
            cmyk = new float[4];
        }
        float max = ColorModelHSL.max(rgb[0], rgb[1], rgb[2]);
        if (max > 0.0f) {
            cmyk[0] = 1.0f - rgb[0] / max;
            cmyk[1] = 1.0f - rgb[1] / max;
            cmyk[2] = 1.0f - rgb[2] / max;
        }
        else {
            cmyk[0] = 0.0f;
            cmyk[1] = 0.0f;
            cmyk[2] = 0.0f;
        }
        cmyk[3] = 1.0f - max;
        return cmyk;
    }
}
