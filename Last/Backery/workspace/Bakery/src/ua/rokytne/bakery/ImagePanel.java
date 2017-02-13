package ua.rokytne.bakery;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage image;

    public ImagePanel() {
       
    }
    
    public void setPhoto(String path){
    	try {           
            image = ImageIO.read(new File(path));
            
            int w = image.getWidth();
            int h = image.getHeight();
            BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            AffineTransform at = new AffineTransform();
            at.scale(this.getWidth()/(float)w, this.getHeight()/(float)h);
            AffineTransformOp scaleOp = 
               new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            after = scaleOp.filter(image, after);
            image = after;
            if(this.getGraphics()!=null)
            	this.paint(this.getGraphics());
         } catch (IOException ex) {
        	 try {
				image = ImageIO.read(new File("photos/-9.jpg"));int w = image.getWidth();
	             int h = image.getHeight();
	             BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	             AffineTransform at = new AffineTransform();
	             at.scale(this.getWidth()/(float)w, this.getHeight()/(float)h);
	             AffineTransformOp scaleOp = 
	                new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
	             after = scaleOp.filter(image, after);
	             image = after;
	             this.paint(this.getGraphics());
			} catch (IOException e) {
			}
         }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);      
    }

}