package bg.sofia.uni.fmi.mjt.airbnb;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.Bookable;
import bg.sofia.uni.fmi.mjt.airbnb.filter.Criterion;

import java.util.Arrays;

public class Airbnb implements AirbnbAPI {
    private Bookable[] accommodations;

    public Airbnb(Bookable[] accommodations) {
        if(accommodations == null) {
            this.accommodations = null;

            return;
        }

        this.accommodations = new Bookable[accommodations.length];
        System.arraycopy(accommodations, 0, this.accommodations, 0, accommodations.length);
    }

    @Override
    public Bookable findAccommodationById(String id) {
        if(id == null || id.isBlank()) {
            return null;
        }

        for (Bookable accommodation : accommodations) {
            if (accommodation.getId().equalsIgnoreCase(id)) {
                return accommodation;
            }
        }

        return null;
    }

    @Override
    public double estimateTotalRevenue() {
        if(accommodations == null) {
            return 0;
        }

        double result = 0;
        for (Bookable accommodation : accommodations) {
            if (accommodation.isBooked()) {
                result += accommodation.getTotalPriceOfStay();
            }
        }

        return result;
    }


    @Override
    public long countBookings() {
        long result = 0;
        for (Bookable accommodation : accommodations) {
            if (accommodation.isBooked()) {
                result++;
            }
        }

        return result;
    }

    @Override
    public Bookable[] filterAccommodations(Criterion... criteria) {
        boolean hasPassed;
        int count = 0;

        if(criteria == null) {
            return accommodations;
        }

        if(criteria.length == 0) {
            return accommodations;
        }

        if(accommodations == null) {
            return null;
        }

        boolean[] indexes = new boolean[accommodations.length];
        Arrays.fill(indexes, false);

        for(int i = 0; i < accommodations.length; i++) {
            if(accommodations[i] == null || accommodations[i].isBooked()) {
                continue;
            }

            hasPassed = true;
            for(Criterion criterion : criteria) {
                if(!criterion.check(accommodations[i])) {
                    hasPassed = false;
                }
            }

            if(hasPassed) {
                indexes[i] = true;
                count++;
            }
        }

        if(count == 0) {
            return null;
        }

        Bookable[] result = new Bookable[count];
        for(int i = 0, j = 0; i < accommodations.length; i++) {
            if(indexes[i]) {
                result[j] = accommodations[i];
                j++;
            }
        }

        return result;
    }
}
