package sg.edu.singaporetech.sit.theclueless;

/**
 * This is a XYValue class for plotting the scatter plot.
 * @author The Clueless
 */
public class XYValue {

    private double x;
    private double y;

    /**
     * This is default constructor.
     */
    public XYValue() {
    }

    /**
     * A constructor to creates a new item.
     * @param x X is x-axis.
     * @param y Y is y-axis.
     */
    public XYValue(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get x-axis.
     * @return the x-axis.
     */
    public double getX() {
        return x;
    }

    /**
     * Set x-axis
     * @param x set the x-axis.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Get y-axis
     * @return the y-axis.
     */
    public double getY() {
        return y;
    }

    /**
     * Set y-axis
     * @param y set the y-axis
     */
    public void setY(double y) {
        this.y = y;
    }
}
