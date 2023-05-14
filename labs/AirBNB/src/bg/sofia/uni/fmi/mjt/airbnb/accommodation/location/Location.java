package bg.sofia.uni.fmi.mjt.airbnb.accommodation.location;

public class Location {
    private double x;
    private double y;

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getDistanceTo(Location secondPoint) {
        double dx = this.x - secondPoint.x;
        double dy = this.y - secondPoint.y;

        return Math.sqrt(dx * dx + dy * dy);
    }
}
