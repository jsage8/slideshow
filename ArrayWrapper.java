import java.awt.image.BufferedImage;

// @author Jonathan Sage
// @phone 206-498-5442
// @email jsage8@gmail.com

public class ArrayWrapper {
    private int i;
    private BufferedImage image;

    public ArrayWrapper(BufferedImage image, int i) {
        this.i = i;
        this.image = image;
    }

    public int getIndex() {
        return i;
    }

    public BufferedImage getImage() {
        return image;
    }
}