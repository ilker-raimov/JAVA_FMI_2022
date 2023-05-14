package bg.sofia.uni.fmi.mjt.airbnb.accommodation;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.location.Location;

import java.time.LocalDateTime;

public interface Bookable {
    String getId();

    Location getLocation();

    boolean isBooked();

    boolean book(LocalDateTime checkIn, LocalDateTime checkOut);

    double getTotalPriceOfStay();

    double getPricePerNight();
}
