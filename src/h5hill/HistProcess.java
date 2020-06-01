package h5hill;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileSystemView;

public class HistProcess extends JPanel {

	private static final long serialVersionUID = 1L;
	private int size = 256;
	private int colors = 3;

    private static BufferedImage img;
    
    public final int r = 0;
    public final int green = 1;
    public final int b = 2;

    private int[][] bin;
    private volatile boolean loaded = false;
    private int maxY;

    /**
     * Image Build path
     */
    
    
    public HistProcess() {
    	bin = new int[colors][];
        for (int i = 0; i < colors; i++) {
        	bin[i] = new int[size];
        }

        loaded = false;
    }
    
    /**
     * Main method to create Histogram.
     * @param args  BufferedImage 
     */
    public static void main(BufferedImage args) {
    	new HistProcess();
    	new HistProcess(img);
    }

    /**
     * Method to load an image and create histogram arrays.
     * @param path  file name.
     * @throws IOException
     */
    public void load(String path) throws IOException {
        BufferedImage bi = ImageIO.read(new File(path));

        for (int i = 0; i < colors; i++) {
           for (int j = 0; j < size; j++) {
        	   bin[i][j] = 0;
           }
        }

        for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
                Color c = new Color(bi.getRGB(x, y));
                bin[r][c.getRed()]++;
                bin[green][c.getGreen()]++;
                bin[b][c.getBlue()]++;
            }
        }
        
        maxY = 0;
        for (int i = 0; i < colors; i++) {
            for (int j = 0; j < size; j++) {
                if (maxY < bin[i][j]) {
                    maxY = bin[i][j];
                }
            }
        }

        loaded = true;
    }

    /**
     * Method to draw histogram from pixel values using graphics.
     */
    @Override
    public void paint(Graphics g) {
        if (loaded) {        	
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(Color.white);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setStroke(new BasicStroke(2));
            int xInterval = (int) ((double)getWidth() / ((double)size + 1));
            g2.setColor(Color.black);

            for (int i = 0; i < colors; i++) {
                if (i == r) {
                    g2.setColor(Color.red);
                } else if (i == green) {
                    g2.setColor(Color.GREEN);
                } else if (i == b) {
                    g2.setColor(Color.blue);
                }

                for (int j = 0; j < size - 1 ; j++) {
                    int value = (int) (((double)bin[i][j] / (double)maxY) * getHeight());
                    int value2 = (int) (((double)bin[i][j+1] / (double)maxY) * getHeight());
                    g2.drawLine(j * xInterval, getHeight() - value, (j+1)*xInterval, getHeight() - value2);
                }
                
            }
        } else {
            super.paint(g);
        }
    }

    /**
     * New constructor for the window of the histogram
     * @param img  BufferedImage to read in.
     */
    public HistProcess(BufferedImage img) {
        JFrame frame = new JFrame("||| Histogram |||");
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());
        HistProcess his = new HistProcess();
      	
        try {
    		final JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView());
    		fc.setCurrentDirectory(new File("images/"));
    		
    		int returnVal = fc.showOpenDialog(getParent());
        	if (returnVal == JFileChooser.APPROVE_OPTION) {
        		File f = fc.getSelectedFile();
        		String name = f.getName();
        		frame.setTitle(name);
        		his.load("images/" + name);
        	}
        	
        } catch (IOException e) {
            e.printStackTrace();
        }
        

        frame.add(his,BorderLayout.CENTER);
        frame.setVisible(true);
    }

}



