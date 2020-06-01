package h5hill;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileSystemView;

import h5hill.HistProcess;


/**
 * ImageProcessing Class creates a menu
 * The user can enhance the image using the "histogram equalization" option on the menu 
 * The user has an option to create a histogram from the image data
 * @author JacobHill
 *
 */
public class ProcessingMain extends Component implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private BufferedImage img;
	private BufferedImage buff, imgeq;
	private int[] hist;
	private int width, height;
	private String filePath;
	
	public ProcessingMain(){
		}
	
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	/**
	 * paint(Graphics g) method
	 */
	public void paint(Graphics g) {
		g.drawImage(imgeq, 0, 0, null);
	}
/////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu, submenu;
        JMenuItem menuItem;

        menuBar = new JMenuBar();

        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_T);
        menuBar.add(menu);

        menuItem = new JMenuItem("Open", KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        menu.add(menuItem);
         
        menuItem = new JMenuItem("Save", KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Exit");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
       //////////////////////////
        //Build Second menu
       //////////////////////////
        
        
        menu = new JMenu("Image");
        menu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(menu);
        
        menuItem = new JMenuItem("Original");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Histogram");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.ALT_MASK));
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Histogram Equalize");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
       
      
                
  
        
     
        

        return menuBar;
    }
    /////////////////////////////////
    /////////////////////////////////
    
    /**
	 * Schedule the event-dispatching thread to
	 * create and show the GUI.
	 * @param args  String arguments.
	 */
    
    public static void main(String args[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
    
    
    /**
     * Creates JFrame and GUI 
     * menuBar is visible
     * image is displayed. Invoked from thread
     */
	
    public static void createAndShowGUI() {
		JFrame f = new JFrame("||||||| Jacob Hill Menu ||||||");
		f.setSize(500, 500);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ProcessingMain si = new ProcessingMain();
		f.add("Center", si);
		
		f.setJMenuBar(si.createMenuBar());
		f.pack();
		f.setVisible(true);
	}
	
	
	/**
	 * Method handles actions in JFrame when menu items
	 * are selected by calling their private methods.
	 */
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		JMenuItem menu = null;
		
		if (o instanceof JMenuItem) {
			menu = (JMenuItem) (o);
			switch (menu.getActionCommand()) {
			case "Open":
				open();
				break;
			case "Save":
				save();
				break;
			case "Exit":
				exit();
				break;
			case "Original":
				imgeq = buff;
				break;
			case "Histogram":
				imgeq = histogram(buff);
				break;
			case "Histogram Equalize":
				imgeq = histogramEqualize();
				break;
			}
		}

		repaint();
		
	}
	
	/**
	 * Flips image horizontally.
	 * @param image  BufferedImage.
	 * @return     horizontal image.
	 */
	private BufferedImage flipHorizontal(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage himg = new BufferedImage(w, h, image.getType());
		Graphics2D g = himg.createGraphics();
		g.drawImage(image, 0, 0, w, h, w, 0, 0, h, null);
		return himg;
	}
    
	/**
	 * Flips image vertically by using source and destination coordinates to flip/draw image.
	 * @param image  BufferedImage.
	 * @return     vertical image.
	 */
	private BufferedImage flipVertical(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage vimg = new BufferedImage(w, h, image.getColorModel().getTransparency());
		Graphics2D g = vimg.createGraphics();
		g.drawImage(image, 0, 0, w, h, 0, h, w, 0, null);
		g.dispose();
		return vimg;

	}

	/**
	 * Method for the histogram equalization on the gray scaled BufferedImage.
	 * @return  equalized BufferedImage.
	 */
	private BufferedImage histogramEqualize() {
		BufferedImage bi2 = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		Graphics big = bi2.getGraphics();
		big.drawImage(buff, 0, 0, null);
		hist = new int[256];
		
		for (int x = 0; x < width; x++) 
			for (int y = 0; y < height; y++)
				hist[bi2.getRGB(x, y) & 0xFF]++;
		int[] lut = new int[256];
		float sf = (float) 255 / (width * height);
		int sumH = 0;
		int sk = 0;
		for (int i = 0; i < hist.length; i++) { 
			sumH += hist[i];
			sk = (int) (sf * sumH);
			lut[i] = sk;
		}
		
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				int grey = lut[bi2.getRGB(x, y) & 0xFF];
				bi2.setRGB(x, y, grey << 16 | grey << 8 | grey);
			}
		return bi2;
	}
	
	/**
	 * Method to call Histogram Class when "Histogram" menu item chosen and return image
	 * of histogram. 
	 * 
	 * ** Currently displays histogram in new JFrame. **
	 * @param img  BufferedImage.
	 * @return     filtered BufferedImage. * Should return histogram as image * 
	 * @throws IOException 
	 */
	private BufferedImage histogram(BufferedImage img) {
		HistProcess h = new HistProcess(buff);
		return imgeq;
	}

	/**
	 * Sets selected image in window and gets dimensions of image. Prints name of 
	 * selected image and size to control panel for user.
	 * @param fileName  name of the selected image
	 */
	public void setImage(String fileName) {
		try {
			buff = ImageIO.read(new File("images/"+fileName));
			if (width == 0) {
				System.out.print(fileName + " (" + width + ", " + height+ ")");
				width = buff.getWidth() * 3 / 2;
				height = buff.getHeight() * 3 / 2;
				System.out.println(" -> (" + width + ", " + height +")");
			} else {
				int cw = buff.getWidth();
				double sf = 1. * width / cw;
				height = (int) (sf * buff.getHeight());
				System.out.println(fileName + " (" + width + ", " + height + ")");
			}

			if (buff.getType() != BufferedImage.TYPE_INT_RGB) {
				BufferedImage bi2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics big = bi2.getGraphics();
				big.drawImage(buff, 0, 0, width, height, null);
				buff = bi2;
				bi2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				big = bi2.getGraphics();
				big.drawImage(buff, 0, 0, width, height, null);
				imgeq = bi2;
			}
		} catch (IOException e) {
			System.out.println("Image could not be read");
			System.exit(1);
		}
		
		setPreferredSize(getPreferredSize());
	}

	/**
	 * Opens image when "Open" menu item selected.
	 */
	private void open() {
		
		final JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView());
		fc.setCurrentDirectory(new File("images/"));
		
		int returnVal = fc.showOpenDialog(getParent());
    	if (returnVal == JFileChooser.APPROVE_OPTION) {
    		File f = fc.getSelectedFile();
    		setImage(f.getName());
    	}

	}
	
	/**
	 * Saves image as csv file. Saves differently for histogram and histogram equalized image.
	 */
	private void save() {
		try (PrintWriter writer = new PrintWriter(new File("HistInfo.csv"))) {

		      StringBuilder sb = new StringBuilder();
		      sb.append("Jacob,");
		      sb.append(',');
		      sb.append("Hill");
		      sb.append('\n');

		      sb.append(filePath);
		      sb.append('\n');
		      
		      for (int i = 0; i < 256; i++) {
		    	  sb.append(i);
		    	  sb.append(",");
		      }

		      writer.write(sb.toString());

		      System.out.println("saved!");

		    } catch (FileNotFoundException e) {
		      System.out.println(e.getMessage());
		    }
	}
	
	/**
	 * Exits the program when "Exit" menu item selected.
	 */
	private void exit() {
		try (PrintWriter writer = new PrintWriter(new File("HistInfo.csv"))) {

		      StringBuilder sb = new StringBuilder();
		      sb.append("Jacob,");
		      sb.append(',');
		      sb.append("Hill");
		      sb.append('\n');

		      sb.append(filePath);
		      sb.append('\n');
		      
		      for (int i = 0; i < 256; i++) {
		    	  sb.append(i);
		    	  sb.append(",");
		      }

		      writer.write(sb.toString());

		      System.out.println("done!");

		    } catch (FileNotFoundException e) {
		      System.out.println(e.getMessage());
		    }
		System.exit(0);
	}
	
	
	///////////////////////////////////////////////////////////////////


    

}
