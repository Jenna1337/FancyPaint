package designer.components;

import javax.swing.AbstractButton;
import javax.swing.Action;

public class ButtonFactory
{
	public static <T extends AbstractButton> T createComponent(Class<T> clazz, String label, Action a) throws InstantiationException, IllegalAccessException
	{
		T comp = clazz.newInstance();
		comp.setAction(a);
		comp.setText(label);
		comp.setToolTipText(label);
		return comp;
	}
}
