package ch.epfl.xblast.client;


import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;
 
public class AfficheurDimage extends JPanel {
     
    private static final long   serialVersionUID    = 1L;
     
    protected Image buffer;    
     
    public AfficheurDimage(Image buffer){
        this.buffer = buffer;
    }  
         
    public void paintComponent(Graphics g) {
       g.drawImage(buffer,0,0,null);
     }
}