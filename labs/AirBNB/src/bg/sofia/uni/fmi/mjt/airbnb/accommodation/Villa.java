package bg.sofia.uni.fmi.mjt.airbnb.accommodation;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.location.Location;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Villa implements Bookable{
    static private int numForId = 0;

    public String id;
    private final Location location;
    private final double pricePerNight;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private boolean isBooked;

    public Villa(Location location, double pricePerNight) {
        this.location = location;
        this.pricePerNight = pricePerNight;
        isBooked = false;
        id = "VIL-" + numForId++;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Location getLocation() {
        return location;
    }
    @Override
    public boolean isBooked() {
        return isBooked;
    }

    @Override
    public boolean book(LocalDateTime checkIn, LocalDateTime checkOut) {
        if(isBooked) {
            return false;
        }

        if(checkIn == null || checkOut == null) {
            return false;
        }

        if(checkIn.isBefore(LocalDateTime.now())) {
            return false;
        }

        if(!checkIn.isBefore(checkOut)) {
            return false;
        }

        this.checkIn = checkIn;
        this.checkOut = checkOut;
        isBooked = true;

        return true;
    }

    @Override
    public double getTotalPriceOfStay() {
        if(!isBooked) {
            return 0;
        }

        return ChronoUnit.DAYS.between(checkIn, checkOut) * pricePerNight;
    }

    @Override
    public double getPricePerNight() {
        return pricePerNight;
    }
}
