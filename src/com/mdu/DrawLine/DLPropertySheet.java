package com.mdu.DrawLine;

import static com.mdu.DrawLine.Const.ENUM;
import static com.mdu.DrawLine.Const.GET;
import static com.mdu.DrawLine.Const.RANGE;
import static com.mdu.DrawLine.Const.SET;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class Accessor implements Comparable<Accessor> {
	Object component;
	Object range;
	String shortName;
	Class<?> type;
	Object value;
	Object[] enu;
	Editor editor;

	Accessor(Object comp, String shortName, Class<?> type) {
		this.component = comp;
		this.shortName = shortName;
		this.type = type;
	}

	Object getRange() {
		return range;
	}

	void setRange(Object o) {
		range = o;
	}

	void setEnum(Object[] o) {
		enu = o;
	}

	Object[] getEnum() {
		return enu;
	}

	public int compareTo(Accessor o) {
		return shortName.compareTo(o.shortName);
	}
}

@SuppressWarnings("serial")
class CompositeEditor extends Editor {
	ArrayList<Editor> editorList = new ArrayList<Editor>();
	JTabbedPane tabbedPane;

	public void update(Object o) {
		for (Editor e : editorList)
			e.update(o);
	}

	CompositeEditor(String name, ArrayList<Accessor> accessor) {
		super(null);
		tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		for (Editor e : editorList)
			addTab(e);
	}

	void addTab(Editor e) {
		tabbedPane.add(e);
	}
}

@SuppressWarnings("serial")
class BoolEditor extends Editor {
	final JCheckBox button;

	public void update(Object o) {
		button.setSelected((Boolean) o);
	}

	BoolEditor(final Accessor accessor) {
		super(accessor);
		button = new JCheckBox();
		final String name = "get" + accessor.shortName;
		try {
			final Object comp = accessor.component;
			final Method m = comp.getClass().getMethod(name);
			final boolean v = (Boolean) m.invoke(comp);
			button.setSelected(v);
		} catch (final Exception e1) {
			e1.printStackTrace();
		}
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final boolean i = button.isSelected();
				final Object comp = accessor.component;
				final String name = SET + accessor.shortName;
				try {
					final Method m = comp.getClass().getMethod(name, accessor.type);
					m.invoke(comp, i);
					Object parent = getParent(comp);
					((Component) parent).repaint();
				} catch (final Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		add(button);
	}
}

@SuppressWarnings("serial")
class ColorEditor extends Editor {
	final JButton button;

	public void update(Object o) {
		button.setBackground((Color) o);
	}

	ColorEditor(final Accessor accessor) {
		super(accessor);
		button = new JButton() {
			public void paintComponent(Graphics g) {
				// super.paint(g);
				Color b = button.getBackground();
				b = new Color((b.getRGB() & 0x00ffffff) | 0xbb000000, true);
				g.setColor(b);

				int m = 3;
				int x = m;
				int y = m;
				int w = getWidth() - 2 * m;
				int h = getHeight() - 2 * m;
				int r = h / 4;
				g.fillRoundRect(x, y, w, h, r, r);
			}
		};

		final Color v = getColor();
		button.setBackground(v);
		final JCheckBox delete = new JCheckBox();

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final Object comp = accessor.component;
				Color c = getColor();
				Object parent = getParent(comp);
				c = JColorChooser.showDialog((Component) parent, "Color", c);
				button.setBackground(c);
				try {
					invokeSetter(c);
					delete.setSelected(c != null);
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		final JPanel p = new JPanel(new BorderLayout());
		p.add(button, BorderLayout.CENTER);

		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				invokeSetter(delete.isSelected() ? button.getBackground() : null);
			}
		});
		delete.setSelected(v != null);

		p.add(delete, BorderLayout.LINE_END);
		add(p);
	}

	Color getColor() {
		final String name = "get" + accessor.shortName;
		final Object comp = accessor.component;
		try {
			final Method m = comp.getClass().getMethod(name);
			final Color v = (Color) m.invoke(comp);
			return v;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

@SuppressWarnings("serial")
class StringEditor extends Editor {
	final JTextField text;

	public void update(Object o) {
		text.setText((String) o);
	}

	StringEditor(final Accessor accessor) {
		super(accessor);
		text = new JTextField();
		final String s = getString();
		text.setText(s);
		final JCheckBox delete = new JCheckBox();

		text.getDocument().addDocumentListener(new DocumentListener() {

			public void removeUpdate(DocumentEvent e) {
				String s = text.getText();
				invokeSetter(s);
				delete.setSelected(s == null || s == "");
			}

			public void insertUpdate(DocumentEvent e) {
				String s = text.getText();
				invokeSetter(s);
				delete.setSelected(s == null || s == "");
			}

			public void changedUpdate(DocumentEvent e) {
				String s = text.getText();
				invokeSetter(s);
				delete.setSelected(s == null || s == "");
			}
		});

		final JPanel p = new JPanel(new BorderLayout());
		p.add(text, BorderLayout.CENTER);

		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				invokeSetter(delete.isSelected() ? text.getText() : "");
			}
		});
		delete.setSelected(s != null);

		p.add(delete, BorderLayout.LINE_END);
		add(p);
	}

	String getString() {
		return (String) invokeGetter();
	}

}

@SuppressWarnings("serial")
class PushEditor extends Editor {
	final JButton but;

	PushEditor(final Accessor accessor) {
		super(accessor);
		Push ps = (Push) invokeGetter();
		String s = ps != null ? ps.getString() : "Push";
		but = new JButton(s);

		final JPanel p = new JPanel(new BorderLayout());
		p.add(but, BorderLayout.CENTER);

		but.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				invokeSetter(null);
			}
		});
		add(but);
	}

}

@SuppressWarnings("serial")
class FontEditor extends Editor {
	final JButton button;

	public void update(Object o) {
		button.setFont((Font) o);
	}

	FontEditor(final Accessor accessor) {
		super(accessor);
		button = new JButton() {
			public void paint(Graphics g) {
				super.paint(g);
				Font f = button.getFont();
				g.setFont(f);
				g.drawString("coucou", 0, 0);
			}
		};

		final Font f = _getFont();
		button.setFont(f);
		final JCheckBox delete = new JCheckBox();

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final Object comp = accessor.component;
				Font f = _getFont();
				FontChooser fontChooser = new FontChooser();
				Object parent = getParent(comp);
				int result = fontChooser.showDialog((Component) parent);
				if (result == FontChooser.OK_OPTION) {
					Font font = fontChooser.getSelectedFont();
					button.setFont(font);
				}
				try {
					invokeSetter(f);
					delete.setSelected(f != null);
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		final JPanel p = new JPanel(new BorderLayout());
		p.add(button, BorderLayout.CENTER);

		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				invokeSetter(delete.isSelected() ? button.getBackground() : null);
			}
		});
		delete.setSelected(f != null);
		p.add(delete, BorderLayout.LINE_END);
		add(p);
	}

	Font _getFont() {
		Font f = (Font) invokeGetter();
		return f;
	}

}

@SuppressWarnings("serial")
public class DLPropertySheet extends JFrame {
	DLComponent comp;
	ArrayList<Editor> editors = new ArrayList<Editor>();
	HashMap<String, Accessor> accessors;

	static String ShortClassName(Object c) {
		String cls = c.getClass().getName();
		int i = cls.lastIndexOf('.');
		if (i <= 0)
			return cls;
		return cls.substring(i + 1, cls.length());
	}

	public DLPropertySheet(DLComponent c) {
		this("Properties for " + ShortClassName(c), c);
	}

	public DLPropertySheet(String title, DLComponent c) {
		super(title);
		comp = c;
		c.sheet = this;
		getContentPane().setLayout(new BorderLayout());

		JComponent p = page();
		// setSize(300, 200);
		HashMap<String, Method> raw = listProperties();
		HashMap<String, Accessor> acc = cleanProperties(raw);
		accessors = acc;
		Collection<Accessor> values = acc.values();
		ArrayList<Accessor> list = new ArrayList<Accessor>(values);
		Collections.sort(list);
		for (Accessor a : list) {
			final Editor e = Editor.getEditor(a);
			if (e != null) {
				p.add(e);
				editors.add(e);
			}
		}

		pack();

		final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		// Determine the new location of the window
		final int w = getSize().width;
		final int h = getSize().height;
		final int x = (dim.width - w) / 2;
		final int y = (dim.height - h) / 2;

		setLocation(x, y);
		setVisible(true);
		dump();
	}

	void close() {
		setVisible(false);
		if (comp instanceof DLComponent)
			((DLComponent) this.comp).sheet = null;
		this.comp = null;
		dispose();
	}

	void update(String property, Object value) {
		for (Editor e : editors) {
			if (property.equals(e.accessor.shortName)) {
				e.updating = true;
				e.update(value);
				e.updating = false;
			}
		}
	}

	HashMap<String, Accessor> cleanProperties(HashMap<String, Method> methods) {
		final HashMap<String, Accessor> accessors = new HashMap<String, Accessor>();

		final String[] keys = methods.keySet().toArray(new String[] {});
		for (final String s : keys) {
			Accessor a = null;
			String shortName = null;
			Method m = null;
			if (s.startsWith(SET)) {
				final String sn = s.substring(SET.length());
				shortName = sn;
				m = methods.get("get" + sn);
				if (m == null) {
					methods.remove(s);
					continue;
				}
				if (accessors.get(sn) == null) {
					a = new Accessor(comp, sn, m.getReturnType());
					accessors.put(sn, a);
				}
			}
			if (s.startsWith("get")) {
				final String sn = s.substring("get".length());
				shortName = sn;
				m = methods.get(SET + sn);
				if (m == null) {
					methods.remove(s);
					continue;
				}
				m = methods.get(s);
				if (accessors.get(sn) == null) {
					a = new Accessor(comp, sn, m.getReturnType());
					accessors.put(sn, a);
				}
			}
			if (a != null && shortName != null) {
				final String name = "range" + shortName;
				m = methods.get(name);
				if (m != null) {
					Object o;
					try {
						o = m.invoke(comp);
						a.setRange(o);
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (a != null && shortName != null) {
				final String name = "enum" + shortName;
				m = methods.get(name);
				if (m != null) {
					try {
						Object[] o = (Object[]) m.invoke(comp);
						a.setEnum(o);
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		return accessors;
	}

	HashMap<String, Method> listProperties() {
		Class<?> cls = comp.getClass();
		final HashMap<String, Method> accessors = new HashMap<String, Method>();

		while (cls != null) {
			final Method[] ms = cls.getDeclaredMethods();
			for (final Method m : ms) {
				final String name = m.getName();
				if (name.startsWith(SET) || name.startsWith(GET) || name.startsWith(RANGE) || name.startsWith(ENUM))
					accessors.put(name, m);
			}
			cls = cls.getSuperclass();
		}
		return accessors;
	}

	void dump() {
		Class<?> cls = comp.getClass();
		HashMap<String, Method> setters = new HashMap<String, Method>();
		HashMap<String, Method> getters = new HashMap<String, Method>();

		while (cls != null) {
			final Method[] ms = cls.getDeclaredMethods();
			for (final Method m : ms) {
				final String name = m.getName();
				if (name.startsWith(SET))
					setters.put(name, m);
				if (name.startsWith(GET))
					getters.put(name, m);
			}
			cls = cls.getSuperclass();
		}

		final String[] skeys = setters.keySet().toArray(new String[] {});

		for (String k : skeys) {
			final String sn = k.substring(SET.length());
			Method m = getters.get(GET + sn);
			if (m != null) {
				try {
					Object res = m.invoke(comp);
					System.err.println(sn + " " + (res != null ? res.toString() : null));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					System.err.println(m.getName() + " " + e.getMessage());
				}
			}
		}

	}

	private JComponent page() {
		final JPanel sheet = new JPanel();
		BoxLayout bl = new BoxLayout(sheet, BoxLayout.Y_AXIS);
		sheet.setLayout(bl);
		sheet.setSize(300, 200);
		getContentPane().add(sheet, BorderLayout.CENTER);
		LineBorder lb = new LineBorder(sheet.getBackground(), 10);
		sheet.setBorder(lb);
		return sheet;
	}

}

@SuppressWarnings("serial")
abstract class Editor extends JPanel {
	Accessor accessor;
	boolean updating = false;

	Editor(Accessor accessor) {
		this.accessor = accessor;
		setLayout(new GridLayout(1, 0));
		final JLabel label = new JLabel(accessor.shortName);
		add(label, BorderLayout.LINE_START);
	}

	static JDialog dialog = null;

	static void _ShowHideWait(boolean show, Component parent) {
		if (dialog != null) {
			dialog.setVisible(false);
			dialog = null;
		}
		if (show) {
			dialog = new JDialog();
			dialog.setUndecorated(true);
			JPanel panel = new JPanel();
			panel.add(new JLabel("Please wait"));
			dialog.add(panel);
			dialog.pack();
			if (parent != null)
				dialog.setLocation(parent.getWidth() / 2 - dialog.getWidth() / 2,
						parent.getHeight() / 2 - dialog.getHeight() / 2);
			dialog.setVisible(true);
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					dialog.toFront();
					dialog.repaint();
				}
			});
			// dialog.repaint();
		}
	}

	Object getParent(Object comp) {
		Object parent = null;
		Class<?> klass = comp.getClass();
		while (klass != null) {
			try {
				Field field = klass.getDeclaredField("parent");
				field.setAccessible(true);
				parent = field.get(comp);
				break;
			} catch (Exception e1) {
			}
			klass = klass.getSuperclass();
		}
		return parent;
	}

	static Editor getEditor(Accessor accessor) {
		final Class<?> cls = accessor.type;

		if (accessor.getEnum() != null)
			return new EnumEditor(accessor);
		if (cls == int.class)
			return new IntEditor(accessor);
		if (cls == long.class)
			return new LongEditor(accessor);
		if (cls == float.class)
			return new FloatEditor(accessor);
		if (cls == double.class)
			return new DoubleEditor(accessor);
		if (cls == boolean.class)
			return new BoolEditor(accessor);
		if (cls == Color.class)
			return new ColorEditor(accessor);
		if (cls == Paint.class)
			return new ColorEditor(accessor);
		if (cls == Point2D.Float.class)
			return new PointEditor(accessor);
		if (cls == Font.class)
			return new FontEditor(accessor);
		if (cls == String.class)
			return new StringEditor(accessor);
		if (cls == Push.class)
			return new PushEditor(accessor);
		return null;
	}

	void invokeSetter(Object o) {
		try {
			final Object comp = accessor.component;
			final String name = SET + accessor.shortName;
			final Method m = comp.getClass().getMethod(name, accessor.type);
			m.invoke(comp, o);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	Object invokeGetter() {
		try {
			final Object comp = accessor.component;
			final String name = "get" + accessor.shortName;
			final Method m = comp.getClass().getMethod(name);
			Object o = m.invoke(comp);
			return o;
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void update(Object o) {
	}

}

@SuppressWarnings("serial")
class FloatEditor extends Editor {
	JSlider slider;
	JTextField sliderLabel;
	final float mult = 10000;
	DecimalFormat dc = new DecimalFormat("###0.000");

	public void update(Object o) {
		float v = (Float) o;
		slider.setValue((int) (mult * v));
		sliderLabel.setText(dc.format(v));
	}

	FloatEditor(final Accessor accessor) {
		super(accessor);
		slider = new JSlider();
		final float[] a = (float[]) accessor.getRange();
		if (a != null) {
			slider.setMinimum((int) (mult * a[0]));
			slider.setMaximum((int) (mult * a[1]));
		}

		float v = (Float) invokeGetter();
		slider.setValue((int) (mult * v));
		sliderLabel = new JTextField();
		sliderLabel.setColumns(5);
		sliderLabel.setText(dc.format(v));
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (updating)
					return;
				final float i = slider.getValue();
				final float v = i / mult;
				slider.setToolTipText(dc.format(v));
				invokeSetter(v);
				sliderLabel.setText(dc.format(v));
			}
		});
		JPanel sliderPanel = new JPanel(new BorderLayout());
		sliderPanel.add(slider, BorderLayout.CENTER);
		sliderPanel.add(sliderLabel, BorderLayout.LINE_END);
		add(sliderPanel);
	}
}

@SuppressWarnings("serial")
class DoubleEditor extends Editor {
	JSlider slider;
	float mult = 1000;

	public void update(Object o) {
		double v = (Double) o;
		slider.setValue((int) (mult * v));
	}

	DoubleEditor(final Accessor accessor) {
		super(accessor);
		slider = new JSlider();
		final double[] a = (double[]) accessor.getRange();
		if (a != null) {
			slider.setMinimum((int) (mult * a[0]));
			slider.setMaximum((int) (mult * a[1]));
		}

		final double v = (Double) invokeGetter();
		slider.setValue((int) (mult * v));

		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (updating)
					return;
				final int i = slider.getValue();
				final double v = i / mult;
				slider.setToolTipText("" + v);
				invokeSetter(v);
			}
		});
		add(slider);
	}
}

@SuppressWarnings("serial")
class IntEditor extends Editor {
	JSlider slider;
	JTextField sliderLabel;

	public void update(Object o) {
		Integer i = (Integer) o;
		slider.setValue(i);
		sliderLabel.setText(i.toString());
	}

	IntEditor(final Accessor accessor) {
		super(accessor);

		slider = new JSlider();
		final int[] a = (int[]) accessor.getRange();
		if (a != null) {
			slider.setMinimum(a[0]);
			slider.setMaximum(a[1]);
		}
		final int v = (Integer) invokeGetter();
		slider.setValue(v);

		JPanel sliderPanel = new JPanel(new BorderLayout());
		sliderLabel = new JTextField();

		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				final int i = slider.getValue();
				invokeSetter(i);
				sliderLabel.setText("" + i);
			}
		});
		sliderPanel.add(slider, BorderLayout.CENTER);
		sliderLabel.setText("" + v);
		sliderLabel.setColumns(5);
		sliderPanel.add(sliderLabel, BorderLayout.LINE_END);

		add(sliderPanel);
	}
}

@SuppressWarnings("serial")
class LongEditor extends Editor {
	JSlider slider;
	JTextField sliderLabel;

	public void update(Object o) {
		Long i = (Long) o;
		slider.setValue(i.intValue());
		sliderLabel.setText(i.toString());
	}

	LongEditor(final Accessor accessor) {
		super(accessor);

		slider = new JSlider();
		final long[] a = (long[]) accessor.getRange();
		if (a != null) {
			slider.setMinimum((int) a[0]);
			slider.setMaximum((int) a[1]);
		}
		final Long v = (Long) invokeGetter();
		slider.setValue(v.intValue());

		JPanel sliderPanel = new JPanel(new BorderLayout());
		sliderLabel = new JTextField();

		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				final int i = slider.getValue();
				invokeSetter(i);
				sliderLabel.setText("" + i);
			}
		});
		sliderPanel.add(slider, BorderLayout.CENTER);
		sliderLabel.setText("" + v);
		sliderLabel.setColumns(5);
		sliderPanel.add(sliderLabel, BorderLayout.LINE_END);

		add(sliderPanel);
	}
}

@SuppressWarnings("serial")
class EnumEditor extends Editor {
	JComboBox<Object> combo;

	public void update(Object o) {
		combo.setSelectedItem(o);
	}

	EnumEditor(Accessor accessor) {
		super(accessor);
		final Object[] a = (Object[]) accessor.getEnum();

		combo = new JComboBox<Object>(a);
		final ListCellRenderer<? super Object> renderer = combo.getRenderer();

		combo.setRenderer(new ListCellRenderer<Object>() {

			public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {

				int size = 100;

				if (value == null) {
					JPanel panel = new JPanel();
					Dimension d = new Dimension(size, size);
					panel.setSize(d);
					panel.setMinimumSize(d);
					panel.setMaximumSize(d);
					panel.setPreferredSize(d);
					return panel;
				}

				BufferedImage image = DLUtil.getImage(value, size);

				if (image == null) {
//					if (value instanceof String) {
//						String t = (String) value;
//						if (t.lastIndexOf('/') > 0)
//							value = t.substring(t.lastIndexOf('/'), t.length());
//					}
					return renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				} else {
					JPanel panel = new JPanel();
					panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
					final BufferedImage fimage = image;
					final String str = value.toString();
					JPanel imagePanel = new JPanel() {
						public void paint(Graphics g) {
							Graphics2D gr = fimage.createGraphics();
							Font f = new Font(Font.MONOSPACED, Font.PLAIN, 10);
							g.setFont(f);
							g.setColor(Color.red);
							gr.drawString(str, 0, fimage.getHeight() / 2);
							g.drawImage(fimage, 0, 0, null);
						}
					};
					Dimension d = new Dimension(size, size);
					panel.setSize(d);
					panel.setMinimumSize(d);
					panel.setMaximumSize(d);
					panel.setPreferredSize(d);
					panel.add(imagePanel, BorderLayout.CENTER);
					return panel;
				}
			}
		});

		Object o = invokeGetter();
		combo.setSelectedItem(o);
		combo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object o = combo.getSelectedItem();
				invokeSetter(o);
			}
		});
		add(combo);
	}
}

/*
 * @SuppressWarnings("serial") class EnumEditor2 extends Editor {
 * 
 * static HashMap<Object, BufferedImage> imageMap = new HashMap<>();
 * 
 * JComboBox<Object> combo;
 * 
 * public void update(Object o) { combo.setSelectedItem(o); }
 * 
 * public Component renderImage(JList<? extends Object> list, Object value, int
 * index, boolean isSelected, boolean cellHasFocus) {
 * 
 * int size = 100;
 * 
 * if (value == null) { JPanel panel = new JPanel(); Dimension d = new
 * Dimension(size, size); panel.setSize(d); panel.setMinimumSize(d);
 * panel.setMaximumSize(d); panel.setPreferredSize(d); return panel; }
 * 
 * BufferedImage image = DLUtil.getImage(value, size);
 * 
 * if (image == null) { ListCellRenderer<? super Object> renderer =
 * combo.getRenderer(); return renderer.getListCellRendererComponent(list,
 * value, index, isSelected, cellHasFocus); } else { final JPanel panel = new
 * JPanel(); panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS)); final
 * BufferedImage fimage = image; final String str = value.toString(); final
 * JPanel imagePanel = new JPanel() { public void paint(Graphics g) { Graphics2D
 * gr = fimage.createGraphics(); Font f = new Font(Font.MONOSPACED, Font.PLAIN,
 * 10); g.setFont(f); g.setColor(Color.red); gr.drawString(str, 0,
 * fimage.getHeight() / 2); g.drawImage(fimage, 0, 0, null); } }; Dimension d =
 * new Dimension(size, size); panel.setSize(d); panel.setMinimumSize(d);
 * panel.setMaximumSize(d); panel.setPreferredSize(d); panel.add(imagePanel,
 * BorderLayout.CENTER); return panel; } }
 * 
 * static HashMap<Object, Component> renderedCell = new HashMap<Object,
 * Component>();
 * 
 * EnumEditor2(Accessor accessor) { super(accessor); final Object[] a =
 * (Object[]) accessor.getEnum();
 * 
 * combo = new JComboBox<Object>(a);
 * 
 * combo.setRenderer(new ListCellRenderer<Object>() { public Component
 * getListCellRendererComponent(final JList<? extends Object> list, final Object
 * value, final int index, final boolean isSelected, final boolean cellHasFocus)
 * {
 * 
 * Component c = renderedCell.get(value); if (c != null) return c; new
 * SwingWorker<String, Component>() { public String doInBackground() { Component
 * c = renderImage(list, value, index, isSelected, cellHasFocus);
 * renderedCell.put(value, c); return ""; } }.execute(); JButton b = new
 * JButton(); return b; } });
 * 
 * Object o = invokeGetter(); combo.setSelectedItem(o);
 * combo.addActionListener(new ActionListener() { public void
 * actionPerformed(ActionEvent e) { Object o = combo.getSelectedItem();
 * invokeSetter(o); } }); add(combo); } }
 */
@SuppressWarnings("serial")
class PointEditor extends Editor {
	Point last = null;
	Shape shape = null;
	int pwidth = 50;
	int pheight = 50;
	float sz = 2;
	DecimalFormat df = new DecimalFormat("#.0");

	public void update(Object o) {

	}

	Point2D.Float fromEditor(Point2D.Float p) {
		final Point2D.Float[] limits = (Point2D.Float[]) accessor.getRange();

		float xMin, yMin, xMax, yMax;
		if (limits != null) {
			float l1, l2;
			l1 = limits[0].x;
			l2 = limits[1].x;
			xMin = l1; // (float) Math.min(l1, l2);
			xMax = l2; // (float) Math.max(l1, l2);
			l1 = limits[0].y;
			l2 = limits[1].y;
			yMin = l1; // (float) Math.min(l1, l2);
			yMax = l2; // (float) Math.max(l1, l2);
		} else {
			xMin = 0;
			xMax = 100;
			yMin = 0;
			yMax = 100;
		}
		Point2D.Float ret = new Point2D.Float();
		ret.x = DLUtil.Normalize(xMin, xMax, 0, pwidth, p.x);
		ret.y = DLUtil.Normalize(yMin, yMax, 0, pheight, p.y);

		return ret;
	}

	Point2D.Float toEditor(Point2D.Float p) {

		final Point2D.Float[] limits = (Point2D.Float[]) accessor.getRange();

		float xMin, yMin, xMax, yMax;
		if (limits != null) {
			float l1, l2;
			l1 = limits[0].x;
			l2 = limits[1].x;
			xMin = l1;
			xMax = l2;
			l1 = limits[0].y;
			l2 = limits[1].y;
			yMin = l1;
			yMax = l2;
		} else {
			xMin = 0;
			xMax = 100;
			yMin = 0;
			yMax = 100;
		}
		Point2D.Float ret = new Point2D.Float();
		ret.x = DLUtil.Normalize(0, pwidth, xMin, xMax, p.x);
		ret.y = DLUtil.Normalize(0, pheight, yMin, yMax, p.y);
		return ret;
	}

	PointEditor(final Accessor accessor) {
		super(accessor);

		Point2D.Float p = (Point2D.Float) invokeGetter();
		Point2D.Float pe = toEditor(p);

		shape = new Ellipse2D.Float(pe.x - sz / 2, pe.y - sz / 2, sz, sz);

		JPanel parent = new JPanel();
		parent.setLayout(new BoxLayout(parent, BoxLayout.X_AXIS));

		final JPanel square = new JPanel() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g.setColor(Color.black);
				g2.draw(shape);
			}
		};
		square.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				last = null;
			}

			public void mousePressed(MouseEvent e) {
				last = e.getPoint();
			}

			public void mouseClicked(MouseEvent e) {
				last = e.getPoint();
			}
		});

		final JLabel xylabel = new JLabel();
		String s = "x " + df.format(p.x) + " y " + df.format(p.y);
		xylabel.setText(s);
		square.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				Point p = e.getPoint();
				Point pp = new Point(p);
				if (last != null) {
					Rectangle2D b = shape.getBounds2D();
					double cx = b.getX() + b.getWidth() / 2;
					double cy = b.getY() + b.getHeight() / 2;
					double dx = p.x - last.x;
					double dy = p.y - last.y;
					if ((cx + dx) < 0)
						p.x = (int) (last.x - cx);
					if ((cy + dy) < 0)
						p.y = (int) (last.y - cy);
					if (cx + dx > pwidth)
						p.x = (int) (pwidth + last.x - cx);
					if (cy + dy > pheight)
						p.y = (int) (pheight + last.y - cy);
					dx = p.x - last.x;
					dy = p.y - last.y;
					AffineTransform tr = AffineTransform.getTranslateInstance(dx, dy);
					shape = tr.createTransformedShape(shape);
					square.repaint();
					b = shape.getBounds2D();
					float px = (float) (b.getX() + b.getWidth() / 2);
					float py = (float) (b.getY() + b.getHeight() / 2);
					Point2D.Float ep = fromEditor(new Point2D.Float(px, py));
					String s = "x " + df.format(ep.x) + " y " + df.format(ep.y);
					xylabel.setText(s);
					invokeSetter(ep);
				}
				last = pp;
			}
		});
		Dimension d = new Dimension(pwidth, pheight);
		square.setSize(d);
		square.setPreferredSize(d);
		square.setMaximumSize(d);
		square.setMinimumSize(d);
		square.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		parent.add(square);
		parent.add(xylabel);
		add(parent);
	}
}

class Push {
	String str;

	Push(String s) {
		str = s;
	}

	String getString() {
		return str;
	}
}