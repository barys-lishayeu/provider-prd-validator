package com.tsm.prd;

import com.tsm.prd.objects.Airports;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AirportsTest {
    @Test
    public void testIsSameAirportGroupOrSameAirport() {
        final String departureOne = "north west";
        final String departureTwo = "lpl";
        assertTrue(Airports.isSameAirportGroupOrSameAirport(departureOne, departureTwo));
    }
}
