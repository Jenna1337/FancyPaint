package designer.components;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ImageIcon;

public class ButtonFactory
{
	public static <T extends AbstractButton> T createComponent(Class<T> clazz,
			Action a, String label, String toolTip, ImageIcon defaultIcon,
			ImageIcon disabledIcon, ImageIcon pressedIcon,
			ImageIcon rolloverIcon, ImageIcon selectedIcon,
			ImageIcon disabledSelectedIcon, ImageIcon rolloverSelectedIcon)
			throws InstantiationException, IllegalAccessException
	{
		T comp = clazz.newInstance();
		comp.setAction(a);
		if(label != null)
			comp.setText(label);
		if(toolTip != null)
			comp.setToolTipText(toolTip);
		if(defaultIcon != null)
			comp.setIcon(defaultIcon);
		if(disabledIcon != null)
			comp.setDisabledIcon(disabledIcon);
		if(pressedIcon != null)
			comp.setPressedIcon(pressedIcon);
		if(rolloverIcon != null)
			comp.setRolloverIcon(rolloverIcon);
		if(selectedIcon != null)
			comp.setSelectedIcon(selectedIcon);
		if(disabledSelectedIcon != null)
			comp.setDisabledSelectedIcon(disabledSelectedIcon);
		if(rolloverSelectedIcon != null)
			comp.setRolloverSelectedIcon(rolloverSelectedIcon);
		return comp;
	}
	public static <T extends AbstractButton> T createComponent(Class<T> clazz,
			Action a, String label, String toolTip, ImageIcon icon)
			throws InstantiationException, IllegalAccessException
	{
		return createComponent(clazz, a, label, toolTip, icon, null, null, null,
				null, null, null);
	}
	public static <T extends AbstractButton> T createComponent(Class<T> clazz,
			Action a, String label, ImageIcon icon)
			throws InstantiationException, IllegalAccessException
	{
		return createComponent(clazz, a, label, label, icon);
	}
	public static <T extends AbstractButton> T createComponent(Class<T> clazz,
			Action a, ImageIcon icon, String toolTip)
			throws InstantiationException, IllegalAccessException
	{
		return createComponent(clazz, a, null, toolTip, icon);
	}
	public static <T extends AbstractButton> T createComponent(Class<T> clazz,
			Action a, String label, String toolTip)
			throws InstantiationException, IllegalAccessException
	{
		return createComponent(clazz, a, label, toolTip, null);
	}
	public static <T extends AbstractButton> T createComponent(Class<T> clazz,
			Action a, ImageIcon icon)
			throws InstantiationException, IllegalAccessException
	{
		return createComponent(clazz, a, null, null, icon);
	}
	public static <T extends AbstractButton> T createComponent(Class<T> clazz,
			Action a, ImageIcon defaultIcon, ImageIcon disabledIcon,
			ImageIcon pressedIcon, ImageIcon rolloverIcon,
			ImageIcon selectedIcon, ImageIcon disabledSelectedIcon,
			ImageIcon rolloverSelectedIcon)
			throws InstantiationException, IllegalAccessException
	{
		return createComponent(clazz, a, null, null, defaultIcon, disabledIcon,
				pressedIcon, rolloverIcon, selectedIcon, disabledSelectedIcon,
				rolloverSelectedIcon);
	}
}
