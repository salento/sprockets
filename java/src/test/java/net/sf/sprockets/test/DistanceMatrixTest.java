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

import net.sf.sprockets.google.DistanceMatrix;
import net.sf.sprockets.google.DistanceMatrix.Params;

import net.sf.sprockets.google.TravelDistance;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class DistanceMatrixTest {
//    lb1 += new Spot(1, "Albertina", "bei der Oper", 48.204699, 16.368182)
//    lb1 += new Spot(2, "Oper", "bei der Oper", 48.20274, 16.368843)
//    lb1 += new Spot(3, "Schwedenplatz", "Wien", 48.2116039, 16.37701)
//


    @Test
	public void test1to1() throws IOException {
        DistanceMatrix.Params params = new DistanceMatrix.Params();
        params.origins("48.204699,16.368182");
        params.destinations("48.20274,16.368843");
        params.mode("driving");
        DistanceMatrix.Response resp = DistanceMatrix.distances(params);
        System.out.println("General Status: " + resp.getStatus());
        Assert.assertEquals(DistanceMatrix.Response.Status.OK, resp.getStatus());

        List<TravelDistance> lst = resp.getResult();
        Assert.assertEquals(1, lst.size());
        for (TravelDistance dist : lst) {
            System.out.println(dist);
        }
	}

    @Test
    public void test1to2() throws IOException {
        DistanceMatrix.Params params = new DistanceMatrix.Params();
        params.origins("48.204699,16.368182");
        params.destinations("48.20274,16.368843", "48.2116039,16.37701");
        params.mode("driving");
        DistanceMatrix.Response resp = DistanceMatrix.distances(params);
        System.out.println("General Status: " + resp.getStatus());
        Assert.assertEquals(DistanceMatrix.Response.Status.OK, resp.getStatus());

        List<TravelDistance> lst = resp.getResult();
        Assert.assertEquals(2, lst.size());
        for (TravelDistance dist : lst) {
            System.out.println(dist);
        }
    }


    @Test
    public void test3matrix() throws IOException {
        DistanceMatrix.Params params = new DistanceMatrix.Params();
        params.origins("48.204699,16.368182", "48.20274,16.368843", "48.2116039,16.37701");
        params.destinations("48.204699,16.368182", "48.20274,16.368843", "48.2116039,16.37701");
        params.mode("driving");
        DistanceMatrix.Response resp = DistanceMatrix.distances(params);
        System.out.println("General Status: " + resp.getStatus());
        Assert.assertEquals(DistanceMatrix.Response.Status.OK, resp.getStatus());

        List<TravelDistance> lst = resp.getResult();
        Assert.assertEquals(9, lst.size());
        for (TravelDistance dist : lst) {
            System.out.println(dist);
        }
    }
    @Test
    public void testEmptyDestinations() throws IOException {
        DistanceMatrix.Params params = new DistanceMatrix.Params();
        params.origins("48.204699,16.368182");
        params.mode("driving");
        try {
            DistanceMatrix.Response resp = DistanceMatrix.distances(params);
            Assert.assertTrue(false);
        } catch (NullPointerException e) {
            Assert.assertEquals("destinations must be set", e.getMessage());
        }
    }

    @Test
    public void testGitHubSample() throws IOException {
        DistanceMatrix.Response resp = DistanceMatrix.distances(new Params().origin(48.2116039, 16.37701)
                .destinations("Staatsoper in Wien, Austria", "Rathaus in Wien, Austria").language("DE").mode("walking"));

        Assert.assertEquals(DistanceMatrix.Response.Status.OK, resp.getStatus());

        List<TravelDistance> lst = resp.getResult();
        Assert.assertEquals(2, lst.size());
        for (TravelDistance dist : lst) {
            System.out.println(dist);
        }

    }
}
