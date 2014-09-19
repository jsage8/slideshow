import java.awt.image.BufferedImage;
/**
 *
 * @author Jonathan Sage
 */
public class TrickSettings {
    private String trick;
    private BufferedImage[] imageArray;
    private int size;
    private String pace;
    private int[] subgoals;
    private int slidePause;
    private int duration;
    private String file;
    private Boolean isDone;

    //Default constructor is not used
    public TrickSettings() {
        this.trick = null;
        this.size = 0;
        this.pace = null;
        this.duration = 750;
        this.file = null;
        this.isDone = false;
    }
    
    //For use with transitional images begin, interim, and transition
    public TrickSettings(String trick, int size) {
        this.trick = trick;
        this.imageArray = new BufferedImage[size];
        this.size = size;
        this.pace = null;
        this.subgoals = null;
        this.duration = 750;
        this.file = null;
        this.isDone = false;
    }
    
    //For use with all other tricks
    public TrickSettings(String trick, int size, String pace, int[] subgoals) {
        this.trick = trick;
        this.imageArray = new BufferedImage[size];
        this.size = size;
        this.pace = pace;
        this.subgoals = subgoals;
        this.duration = 750;
        this.file = null;
        this.isDone = false;
    }

    public String getTrick() {
        return trick;
    }
    
    public BufferedImage[] getImages() {
        return imageArray;
    }
    
    public BufferedImage getImage(int index) {
        return imageArray[index];
    }
    
    public int getSize() {
        return size;
    }

    public String getPace() {
        return pace;
    }

    public int getDuration() {
        return duration;
    }
    
    public int[] getSubgoals() {
        return subgoals;
    }
    
    public int getSlidePause() {
        return slidePause;
    }
    
    public String getFile() {
        return file;
    }
    
    public Boolean getIsDone() {
        return isDone;
    }
    
    public void setTrick(String trick) {
        this.trick = trick;
    }
    
    public void setImages(BufferedImage[] imageArray) {
        this.imageArray = imageArray;
    }
    
    public void setImage(int index, BufferedImage image) {
        this.imageArray[index] = image;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public void setPace(String pace) {
        this.pace = pace;
    }
    
    public void setDuration(String duration) {
        this.duration = Integer.parseInt(duration.trim());
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public void setSubgoals(int[] subgoals) {
        this.subgoals = subgoals;
    }
    
    public void setSlidePause(String slidePause) {
        this.slidePause = Integer.parseInt(slidePause.trim());
    }
    
    public void setFile(String file) {
        this.file = file;
        this.duration = 0;
    }
    
    public void setIsDone(Boolean isDone) {
        this.isDone = isDone;
    }
}