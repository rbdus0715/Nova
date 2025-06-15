package daw.main.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import daw.main.LayeredPaneTrackMain;
import daw.utils.Utils;

public class EditArea extends JPanel {
	private int HEIGHT;
	private LayeredPaneTrackMain layeredPane;
	
	public EditArea(LayeredPaneTrackMain layeredPane) {
		this.layeredPane = layeredPane;
		int HEIGHT = layeredPane.getHeight();

		
		setBackground(Utils.ColorMap.COLOR_CONTROLBAR);
		setBounds(0, layeredPane.getHeight() - HEIGHT, layeredPane.getWidth(), HEIGHT);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.WHITE));
		
		initHeader();
	}
	
	public void close() {
		layeredPane.remove(this);
	}
	
	public void initHeader() {
		JPanel header = new JPanel();
		header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
		header.setBackground(Utils.ColorMap.COLOR_BACKGROUND);
		JButton closeButton = new JButton();
		closeButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				layeredPane.closeEditor(EditArea.this);
				System.out.println("EditArea : editArea is closed");
		    }
		});
		JLabel closeLabel = new JLabel("X");
		closeButton.add(closeLabel);
		header.add(closeButton);
		add(header, BorderLayout.NORTH);
	}
}
