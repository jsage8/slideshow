import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

// @author Jonathan Sage
// @phone 206-498-5442
// @email jsage8@gmail.com

/******************************************
 * The following SwingWorker class Pre-loads all necessary images.
 ******************************************/

public class ImageWorker extends SwingWorker<BufferedImage[], ArrayWrapper> {
    private TrickSettings trickSettings;
    private int position;
    private int arraySize;
    private String trickName;
    private boolean isInitial;

    public ImageWorker(TrickSettings trickSettings, int position, boolean isInitial) {
        super();
        this.trickSettings = trickSettings;
        this.position = position;
        this.arraySize = trickSettings.getSize();
        this.trickName = trickSettings.getTrick();
        this.isInitial = isInitial;
    }

    @Override
    public BufferedImage[] doInBackground() {
        BufferedImage[] imageArray = new BufferedImage[arraySize];
        // Initially load 20 images
        if(isInitial) {
            for(int i = position; i < position + 20 && i < arraySize; i++) {
                try {
                    imageArray[i] = ImageIO.read(new File("images/" + trickName + (i+1) + ".jpg"));
                    ArrayWrapper wrapArray = new ArrayWrapper(imageArray[i], i);
                    publish(wrapArray);
                } 
                catch (IOException e) {
                    System.out.println(trickName);
                    System.out.println(e.getMessage());
                    System.exit(0);
                }
            }
        }
        // Load the next 10 images
        else {
            for(int i = position; i < position + 10 && i < arraySize; i++) {
                try {
                    imageArray[i] = ImageIO.read(new File("images/" + trickName + (i+1) + ".jpg"));
                    ArrayWrapper wrapArray = new ArrayWrapper(imageArray[i], i);
                    publish(wrapArray);
                } 
                catch (IOException e) {
                    System.out.println(trickName);
                    System.out.println(e.getMessage());
                    System.exit(0);
                }
            }
        }
        return imageArray;
    }

    @Override
    public void process(java.util.List<ArrayWrapper> chunks) {
        for(ArrayWrapper element: chunks) {
            trickSettings.setImage(element.getIndex(), element.getImage());
        }
    }
}