package FloatingClock;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.*;
import javax.swing.UIManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FloatingClock
{

	FloatingClock clock;
	FloatingClockSettings settings;

	final String configFileName = "FloatingClock.config";

	// declare components--
	JFrame frame;
	JPanel pane;
	JLabel clockText;

	Color backgroundColor, foregroundColor;
	int distanceValue;
	float opacityValue, sizeValue;

	public static void main(String... args)
	{
		try 
		{
			// Set cross-platform Java L&F (also called "Metal")
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} 
		catch (Exception e) 
		{
			// handle exception
			e.printStackTrace();
		}

		new FloatingClock();
		
	}

	// get the system time and apply to the clockText label
	public void setTime()
	{
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		Date dateobj = new Date();
		clockText.setText(df.format(dateobj));
	}

	public FloatingClock()
	{
		clock = this;
		loadSettings();
		init();
		new ClockTick().run();
	}

	// initializes swing components and positions frame on users screen
	private void init()
	{
		frame 		= new JFrame("Floating Clock");
		pane		= new MainContainer();
		clockText 	= new JLabel("");

		frame.setType(javax.swing.JFrame.Type.UTILITY);

		frame.setUndecorated(true);
		frame.setAlwaysOnTop(true);
		frame.setBackground(new Color(0, 0, 0, 0));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 	
		// set clockText details based on settings or defaults
		clockText.setForeground(foregroundColor);
		clockText.setFont(clockText.getFont().deriveFont(sizeValue));
		pane.add(clockText);
		frame.add(pane);
		frame.pack();

		// position window to top right of screen
	        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        	GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
	        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        	int x = (int) rect.getMaxX() - frame.getWidth();
	        int y = distanceValue;
       		frame.setLocation(x, y);
		frame.setVisible(true);
	
		frame.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent e)
			{
				// if right-click, show the settings frame
				if(SwingUtilities.isRightMouseButton(e))
				{
					frame.dispose();
					new FloatingClockSettings();
				}
				/* RESIZE CODE
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
				Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
				int x = (int) rect.getMaxX() - frame.getWidth();
				int y = distanceValue;
				frame.setLocation(x, y);
				*/
			} // end mouseClicked
		}); // end addMouseListener

	} // end initComponents() method


	public class MainContainer extends JPanel
	{
		int x = 200;
		int y = 50;

		public MainContainer()
		{
			setOpaque(false);
		} //end MainContainer

		@Override
		public Dimension getPreferredSize()
		{
			return new Dimension(x, y);
		} //end getPreferredSize override

		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			Graphics2D g2d = (Graphics2D) g.create();

			g2d.setComposite(AlphaComposite.SrcOver.derive(opacityValue));

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			           RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(backgroundColor);
			g2d.fillRoundRect(0, 0, x, y, 0, 0);

			g2d.dispose();
		} //end paintComponent() override
	}

	public class ClockTick extends Thread 
	{
		public void run()
		{
			Timer timer = new Timer();
			timer.schedule(new TimerTask() 
			{

				@Override
				public void run() 
				{
					clock.setTime();
				}
			}, 0, 1000);

		} // end thread/run
	} //end ClockTick


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

			try
			{
				backgroundColor = new Color( Integer.parseInt( prop.getProperty("Background") ), true);
			}
			catch(NumberFormatException e)
			{
				System.out.println(e.toString());
				backgroundColor = Color.BLACK;	
			}

			try
			{
				foregroundColor = new Color( Integer.parseInt( prop.getProperty("Foreground") ), true);
			}
			catch(NumberFormatException e)
			{
				System.out.println(e.toString());
				foregroundColor = Color.WHITE;
			}

			try
			{
				distanceValue	= Integer.parseInt( prop.getProperty("Distance") );
			}
			catch(NumberFormatException e)
			{
				System.out.println(e.toString());
				distanceValue = 0;
			}

			try
			{
				opacityValue	= Float.parseFloat( prop.getProperty("Opacity") ) / 100;
			}
			catch(NumberFormatException e)
			{
				System.out.println(e.toString());
				opacityValue = 0.7f;
			}

			try
			{
				sizeValue	= Float.parseFloat( prop.getProperty("FontSize") );
			}
			catch(NumberFormatException e)
			{
				System.out.println(e.toString());
				sizeValue = 15.0f;
			}


			//System.out.println("S: "+sizeValue); System.out.println("O: " + opacityValue); System.out.println("D: "+distanceValue);
		} 
		catch (IOException io) 
		{
			// file doesn't exist, save then reload
			saveSettings();
			loadSettings();
			return;
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
	} // end loadSettings

	// save default settings on first run in case stuff doesn't exist
	private void saveSettings()
	{
		Properties prop = new Properties();
		OutputStream output = null;

		try 
		{
			output = new FileOutputStream(configFileName);

			// set the properties value
			Color b = Color.BLACK;
			Color w = Color.WHITE;
			prop.setProperty("Foreground", Integer.toString( w.getRGB() ) );
			prop.setProperty("Background", Integer.toString(b.getRGB() ) );
			prop.setProperty("Distance", Integer.toString( 0 ) );
			prop.setProperty("Opacity", Integer.toString( 70 ) );
			prop.setProperty("FontSize", Integer.toString( 15 ) );

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
	} // end saveSettings()

} // end FloatingClock class