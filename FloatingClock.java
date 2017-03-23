/*
	Author: Tim Talbot.
	Date  : 20 March 2017
	
	Feel free to use, modify or otherwise make use of this code in any way you wish,
	I just ask that you keep this notice here and pull-request any cool changes back
	to me. Thanks!

*/

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
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
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
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;



public class FloatingClock
{

	boolean debug = true;

	int mouseX, mouseY;

	String screenName="";

	// context menu components
	JPopupMenu menu;
	JMenuItem settingsMenuItem, exitMenuItem;
	
	boolean isDraggable = false;
	
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

		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run() 
			{
				init();
				new ClockTick().run();
			}
		});

	}

	// initializes swing components and positions frame on users screen
	private void init()
	{
		frame 		= new JFrame("Floating Clock");
		pane		= new MainContainer();
		clockText 	= new JLabel("");

		// implement drag functionality
		frame.addMouseListener(new MouseListener() {


			public void mousePressed(MouseEvent e) 
			{
				mouseX = e.getX();
				mouseY = e.getY();
			}
			public void mouseReleased(MouseEvent e) { }
			public void mouseEntered(MouseEvent e) { }
			public void mouseExited(MouseEvent e) { }
			public void mouseClicked(MouseEvent e) { }
		});

		frame.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e)
			{
				if(isDraggable && SwingUtilities.isLeftMouseButton(e))
				{
					frame.setLocation(e.getXOnScreen()-mouseX,e.getYOnScreen()-mouseY);
				}
			}
			public void mouseMoved(MouseEvent e) { }
		});

		frame.setType(javax.swing.JFrame.Type.UTILITY);
		frame.setUndecorated(true);
		frame.setAlwaysOnTop(true);
		frame.setBackground(new Color(0, 0, 0, 0));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 	
		// set clockText details based on settings or defaults
		clockText.setForeground(foregroundColor);
		clockText.setFont(clockText.getFont().deriveFont(sizeValue));

		// prepare context menu
		menu 			= new JPopupMenu();
		settingsMenuItem 	= new JMenuItem("Settings");
		exitMenuItem		= new JMenuItem("Exit");
		
		menu.add(settingsMenuItem);
		menu.add(exitMenuItem);

		settingsMenuItem.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				frame.dispose();
				new FloatingClockSettings();
			}


		});

		exitMenuItem.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}


		});
		
		// add components to container and add container to frame
		pane.add(clockText);
		frame.add(pane);
		frame.pack();

		// position window to top right of screen
		boolean foundScreen = false;

	        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] screenDevices = ge.getScreenDevices();
		GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();

		// more than one screen?
		if(screenDevices.length > 1)
		{
			// which screen should i draw to ...

			// if no screen name is specified in the settings, draw to default screen
			if(screenName.equals(""))
			{
				screenName = defaultScreen.getIDstring();
			}
			else
			{
				// find settings-defined screen and set as default
				for (int j = 0; j < screenDevices.length; j++) 
				{ 
					GraphicsDevice gd = screenDevices[j];
				
					if(gd.getIDstring().equals(screenName))
					{
						defaultScreen = gd;
						foundScreen = true;
						break;
					}
				}
      
			}
		}

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
					menu.show(e.getComponent(), e.getX(), e.getY());

				}
				
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
				if(debug) System.out.println(e.toString());
				backgroundColor = Color.BLACK;	
				System.out.println("Possible Config file corruption. Delete FloatingClock.config to resolve");
			}

			try
			{
				foregroundColor = new Color( Integer.parseInt( prop.getProperty("Foreground") ), true);
			}
			catch(NumberFormatException e)
			{
				if(debug) System.out.println(e.toString());
				foregroundColor = Color.WHITE;
				System.out.println("Possible Config file corruption. Delete FloatingClock.config to resolve");
			}

			try
			{
				distanceValue	= Integer.parseInt( prop.getProperty("Distance") );
			}
			catch(NumberFormatException e)
			{
				if(debug) System.out.println(e.toString());
				distanceValue = 0;
				System.out.println("Possible Config file corruption. Delete FloatingClock.config to resolve");
			}

			try
			{
				opacityValue	= Float.parseFloat( prop.getProperty("Opacity") ) / 100;
			}
			catch(NumberFormatException e)
			{
				if(debug) System.out.println(e.toString());
				opacityValue = 0.7f;
				System.out.println("Possible Config file corruption. Delete FloatingClock.config to resolve");
			}

			try
			{
				sizeValue	= Float.parseFloat( prop.getProperty("FontSize") );
			}
			catch(NumberFormatException e)
			{
				if(debug) System.out.println(e.toString());
				sizeValue = 15.0f;
				System.out.println("Possible Config file corruption. Delete FloatingClock.config to resolve");
			}

			try
			{
				isDraggable = Boolean.parseBoolean( prop.getProperty("isDraggable") );
			}
			catch(Exception e)
			{
				if(debug) System.out.println(e.toString());
				isDraggable = false;
				System.out.println("Possible Config file corruption. Delete FloatingClock.config to resolve");
			}

			
			screenName = prop.getProperty("ScreenName");

			//System.out.println("S: "+sizeValue); System.out.println("O: " + opacityValue); System.out.println("D: "+distanceValue);
		} 
		catch (IOException io) 
		{
			if(debug) io.printStackTrace();
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
					if(debug) io.printStackTrace();
				}
			}
		}
	} // end loadSettings

	// save default settings on first run in case stuff doesn't exist
	private void saveSettings()
	{
		if(debug) System.out.println("saving settings..");
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
			prop.setProperty("isDraggable", Boolean.toString (false) );
			prop.setProperty("ScreenName", screenName);
			// save properties to project root folder
			prop.store(output, null);

			if(debug) System.out.println("Settings saved");
		} 
		catch (IOException io) 
		{
			if(debug) io.printStackTrace();
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
					if(debug) io.printStackTrace();
				}
			}
		}
	} // end saveSettings()

	class MenuListener extends MouseAdapter 
	{
		public void MousePressed(MouseEvent e)
		{
			System.out.println("mouse pressed");
		}

		public void MouseReleased(MouseEvent e)
		{
			System.out.println("mouse released");
			showMenu(e);
		}

		private void showMenu(MouseEvent e)
		{
			if(e.isPopupTrigger()) 
			{
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

} // end FloatingClock class