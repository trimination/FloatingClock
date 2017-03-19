package clock;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JColorChooser;
import javax.swing.JSlider;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.Color;
import javax.swing.BorderFactory;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.plaf.metal.*;
import javax.swing.UIManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileOutputStream;
import java.io.OutputStream;

class FloatingClockSettings 
{
	FloatingClock clock;

	final String configFileName = "FloatingClock.config";

	final int MAX_OPACITY=100, MIN_OPACITY=0, MAX_SIZE=40, MIN_SIZE=12;

	int opacityValue=70, sizeValue=15, distanceValue=0;

	JFrame frame;
	JDialog fgDialog, bgDialog;
	GridLayout layout;
	JPanel pane;
	JTextField distanceFromTop;
	JButton saveButton, exitButton;
	JSlider opacitySlider, sizeSlider;
	Color backgroundColor, foregroundColor;
	JLabel  titleLabel, backgroundColorLabel, foregroundColorLabel,
		backgroundColorExample, foregroundColorExample, 
		distanceFromTopLabel, opacityLabel, sizeLabel;

	JColorChooser bgChooser, fgChooser;

	public FloatingClockSettings()
	{
		loadSettings();		
		foregroundColor	= Color.BLACK;
		backgroundColor	= Color.BLACK;

		try 
		{
			// Set cross-platform Java L&F (also called "Metal")
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			UIManager.put("Label.font", UIManager.getFont("Label.font").deriveFont(20.0f));
			UIManager.put("Label.foreground", Color.gray);
		} 
		catch (Exception e) 
		{
			// handle exception
			e.printStackTrace();
		}

		ActionListener fgOkActionListener = new ActionListener() 
		{
			public void actionPerformed(ActionEvent actionEvent) 
			{
				foregroundColor = fgChooser.getColor();
				foregroundColorExample.setBackground(foregroundColor);
			}
		};
	
		ActionListener bgOkActionListener = new ActionListener() 
		{
			public void actionPerformed(ActionEvent actionEvent) 
			{
				backgroundColor = bgChooser.getColor();
				backgroundColorExample.setBackground(backgroundColor);
			}
		};
	
		
		
		ActionListener bgCancelActionListener = new ActionListener() 
		{
			public void actionPerformed(ActionEvent actionEvent) 
			{
				
			}
		};

		ActionListener fgCancelActionListener = new ActionListener() 
		{
			public void actionPerformed(ActionEvent actionEvent) 
			{
				
			}
		};


		Border border 	= new LineBorder(Color.darkGray, 10);

		layout			= new GridLayout(0,2);
		frame 			= new JFrame("FloatingClock Settings");

		frame.setUndecorated(true);
		//frame.setSize(new Dimension(200,400));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				new FloatingClock();
				
			}
		});

		pane 			= new JPanel();
		pane.setBorder(border);
		pane.setBackground(Color.darkGray);
		pane.setLayout(layout);

		distanceFromTop		= new JTextField(Integer.toString(distanceValue));

		bgChooser 		= new JColorChooser();
		bgDialog = JColorChooser.createDialog(null, "Change Background", true, bgChooser, bgOkActionListener, bgCancelActionListener);


		fgChooser 		= new JColorChooser();
		fgDialog = JColorChooser.createDialog(null, "Change Foreground", true, fgChooser, fgOkActionListener, fgCancelActionListener);

			/* fgChooser.getSelectionModel().addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e)
				{
					// update the backgroundColorExample color
					foregroundColorExample.setBackground(fgChooser.getColor());
				}
			});
			*/

		opacitySlider	= new JSlider(JSlider.HORIZONTAL, MIN_OPACITY, MAX_OPACITY, opacityValue);
		opacitySlider.setBackground(Color.darkGray);
		opacitySlider.setForeground(Color.gray);

		sizeSlider	= new JSlider(JSlider.HORIZONTAL, MIN_SIZE, MAX_SIZE, sizeValue);
		sizeSlider.setBackground(Color.darkGray);
		sizeSlider.setForeground(Color.gray);
		
		//opacitySlider.setMajorTickSpacing(10);
		//opacitySlider.setPaintTicks(true); //display tick lines
		//opacitySlider.setPaintLabels(true); // displays tick labels

		backgroundColorLabel	= new JLabel("Background Color: ");
		foregroundColorLabel	= new JLabel("Foreground Color: ");

		backgroundColorExample	= new JLabel();
		backgroundColorExample.setOpaque(true);
		backgroundColorExample.setBorder(border);
		backgroundColorExample.setBackground(backgroundColor);

		backgroundColorExample.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent e)
			{
				bgDialog.setVisible(true);
			} // end mouseClicked
		}); // end addMouseListener

		foregroundColorExample	= new JLabel();
		foregroundColorExample.setOpaque(true);
		foregroundColorExample.setBorder(border);
		foregroundColorExample.setBackground(foregroundColor);
		
		foregroundColorExample.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent e)
			{
				fgDialog.setVisible(true);
			} // end mouseClicked
		});

		titleLabel		= new JLabel("FloatingClock Settings");
		distanceFromTopLabel	= new JLabel("Distance from top (px): ");
		opacityLabel		= new JLabel("Opacity: ");
		sizeLabel		= new JLabel("Font Size: ");

		saveButton 		= new JButton("Save");
		saveButton.setBackground(Color.gray);
		saveButton.setForeground(Color.WHITE);
		
		saveButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				saveSettings();
				new FloatingClock();
				frame.dispose();
			}
		});




		exitButton		= new JButton("Close");
		exitButton.setBackground(Color.gray);
		exitButton.setForeground(Color.WHITE);

		exitButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				new FloatingClock();
				frame.dispose();
			}
		});

		frame.add(titleLabel);
		//pane.add(titleLabel); 
		layout.setHgap(20);
		pane.add(foregroundColorLabel);
		pane.add(foregroundColorExample);
		pane.add(backgroundColorLabel);
		pane.add(backgroundColorExample);
		pane.add(distanceFromTopLabel);
		pane.add(distanceFromTop);
		pane.add(opacityLabel);
		pane.add(opacitySlider);
		pane.add(sizeLabel);
		pane.add(sizeSlider);
                layout.setVgap(40);
		pane.add(saveButton);
		pane.add(exitButton);

		frame.add(pane);
		frame.pack();

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void saveSettings()
	{
		Properties prop = new Properties();
		OutputStream output = null;

		try 
		{
			output = new FileOutputStream(configFileName);

			String distanceVal = "0";

			try
			{
				int temp = Integer.parseInt(distanceFromTop.getText());
				distanceVal = distanceFromTop.getText();
			}
			catch(NumberFormatException e)
			{
				distanceVal = "0";
			}
			// set the properties value

			prop.setProperty("Foreground", Integer.toString( foregroundColor.getRGB() ) );
			prop.setProperty("Background", Integer.toString( backgroundColor.getRGB() ) );
			prop.setProperty("Distance",  distanceVal );
			prop.setProperty("Opacity", Integer.toString( opacitySlider.getValue() ) );
			prop.setProperty("FontSize", Integer.toString( sizeSlider.getValue() ) );

			// save properties to project root folder
			prop.store(output, null);

		} 
		catch (IOException io) 
		{
			io.printStackTrace();
		} 
		finally 
		{
			if (output != null) 
			{
				try 
				{
					output.close();
				} 
				catch (IOException io) 
				{
					io.printStackTrace();
				}
			}
		}
	}

	private void loadSettings()
	{
		Properties prop = new Properties();
		InputStream input = null;

		try 
		{
			input = new FileInputStream(configFileName);

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			backgroundColor = new Color( Integer.parseInt( prop.getProperty("Background") ), true);
			foregroundColor = new Color( Integer.parseInt( prop.getProperty("Foreground") ), true);
			distanceValue	= Integer.parseInt( prop.getProperty("Distance") );
			opacityValue	= Integer.parseInt( prop.getProperty("Opacity") );
			sizeValue	= Integer.parseInt( prop.getProperty("FontSize") );

			//System.out.println("S: "+sizeValue); System.out.println("O: " + opacityValue); System.out.println("D: "+distanceValue);

		} 
		catch (IOException io) 
		{
			io.printStackTrace();
		} 
		finally 	
		{
			if (input != null) 
			{
				try 
				{
					input.close();
				} 
				catch (IOException io) 
				{
					io.printStackTrace();
				}
			}
		}
	}
}