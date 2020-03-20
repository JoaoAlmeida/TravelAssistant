/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * 
 * @author fellipe
 */
public class PointTest {

    public PointTest() {
    }

    /**
     * Test of compareLatitude method, of class Point.
     */
    @Test
    public void testCompareLatitude() {
        Point a = new Point(25.887, 28.00092);
        Point b = new Point(26.887, 27.00092);
        int x = a.compareLatitude(b);
        assertEquals(x, -1);
        x = b.compareLatitude(a);
        assertEquals(x, 1);
        b.setLat(25.887);
        x = b.compareLatitude(a);
        assertEquals(x, 0);
        b.setLat(25.887345);
        x = b.compareLatitude(a);
        assertEquals(x, 1);
    }
    
    @Test
    public void testCompareLatitudeOfPoints() {
        Point a = new Point(-12.9729615,-38.5136526);
        Point b = new Point(-12.9726582,-38.5139659);
        Point c = new Point(-12.9726342,-38.5140243);
        Point d = new Point(-12.9726194,-38.5140846);
        List<Point> points = new ArrayList<Point>(); 
        points.add(a);
        points.add(d);
        points.add(c);
        points.add(b);
        double largeLat = Point.largeLatitude(points);
        assertEquals(largeLat,-12.9726194, 0.00000000001);                
        double smallerLat = Point.smallerLatitude(points);
        assertEquals(smallerLat,-12.9729615, 0.00000000001);                
    }
    
    
}
