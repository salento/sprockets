/*
 * Copyright 2013 pushbit <pushbit@gmail.com>
 *
 * This file is part of Sprockets.
 *
 * Sprockets is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Sprockets is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Sprockets.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.sprockets.test;

import net.sf.sprockets.google.GeoCodedLocation;
import net.sf.sprockets.google.GeoCoding;
import net.sf.sprockets.google.GeoCoding.Response;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static net.sf.sprockets.google.GeoCoding.Response.Status.OK;
import static org.junit.Assert.*;


public class GeoCodingTest {

    private static final double ACCURACY_LOCATION = 0.000002;

    @Test
    public void testStephansdomVienna() throws IOException {

        Response resp = GeoCoding.geocoding(new GeoCoding.Params().address("Stephansdom, Wien, Österreich").language("DE"));
        assertEquals(OK, resp.getStatus());
        List<GeoCodedLocation> locations = resp.getResult();
        for (GeoCodedLocation location : locations) System.out.println(location);

        assertNotNull(locations);
        assertEquals(1, locations.size());

        GeoCodedLocation loc1 = locations.get(0);
        assertTrue(loc1.getFormattedAddress().contains("St. Stephan"));
        assertEquals(48.2084114, loc1.getLatitude(), ACCURACY_LOCATION);
        assertEquals(16.3734707, loc1.getLongitude(), ACCURACY_LOCATION);
    }

    @Test
    public void testReverseStephansdomVienna() throws IOException {

        Response resp = GeoCoding.geocoding(new GeoCoding.Params().latlng(48.2084114, 16.3734707).language("US"));
        assertEquals(OK, resp.getStatus());
        List<GeoCodedLocation> locations = resp.getResult();
        assertNotNull(locations);
        for (GeoCodedLocation loc : locations) System.out.println(loc);

        assertTrue(locations.size() > 1);

        GeoCodedLocation loc1 = locations.get(0);
        assertTrue(loc1.getFormattedAddress().contains("Stephansplatz"));
        assertTrue(loc1.getFormattedAddress().contains("Vienna"));
    }

    @Test
    public void testReverseGerman() throws IOException {

        Response resp = GeoCoding.geocoding(new GeoCoding.Params().latlng(48.2084114, 16.3734707).language("DE"));
        assertEquals(OK, resp.getStatus());
        List<GeoCodedLocation> locations = resp.getResult();
        assertNotNull(locations);
        for (GeoCodedLocation loc : locations) System.out.println(loc);

        assertTrue(locations.size() > 1);

        GeoCodedLocation loc1 = locations.get(0);
        assertTrue(loc1.getFormattedAddress().contains("Stephansplatz"));
        assertTrue(loc1.getFormattedAddress().contains("Wien, Österreich"));
    }
}
