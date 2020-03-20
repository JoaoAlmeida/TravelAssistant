/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;
import java.util.List;
import java.util.Iterator;
/**
 *
 * @author fellipe
 */
public class Point{
    double lat;
    double lgt;

    public Point(double lat, double lgt) {
        this.lat = lat;
        this.lgt = lgt;
    }
    
    public Point(){
    
    }
    
    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLgt(double lgt) {
        this.lgt = lgt;
    }
    
    public double getLat() {
        return lat;
    }

    public double getLgt() {
        return lgt;
    }

  
    public int compareLatitude(Object o) {
        if(o instanceof Point){
            Point x = (Point)o;
            if(this.lat > x.lat){
                return 1;
            }else if(this.lat < x.lat){
                return -1;
            }else{
                return 0;
            }
        }else{
            throw new RuntimeException("bla");
        }        
    }
    
    public int compareLongitude(Object o) {
        if(o instanceof Point){
            Point x = (Point)o;
            if(this.lgt > x.lgt){
                return 1;
            }else if(this.lgt < x.lgt){
                return -1;
            }else{
                return 0;
            }
        }else{
            throw new RuntimeException("bla");
        }        
    }
    
    
    public static double largeLatitude(List<Point> points){
        double large = 0;
        Iterator<Point> it = points.iterator();
        Point x = it.next();
        large = x.lat;
        while(it.hasNext()){
            x = it.next();
            if(x.getLat() > large){
                large = x.getLat();
            }
        }
        return large;
    }
    
    public static double smallerLatitude(List<Point> points){
        double smaller = 0;
        Iterator<Point> it = points.iterator();
        Point x = it.next();
        smaller = x.lat;
        while(it.hasNext()){
            x = it.next();
            if(x.getLat() < smaller){
                smaller = x.getLat();
            }
        }
        return smaller;
    }
    
    public static double largeLongitude(List<Point> points){
        double large = 0;
        Iterator<Point> it = points.iterator();
        Point x = it.next();
        large = x.lgt;
        while(it.hasNext()){
            x = it.next();
            if(x.getLgt() > large){
                large = x.getLgt();
            }
        }
        return large;
    }
    
    public static double smallerLongitude(List<Point> points){
        double smaller = 0;
        Iterator<Point> it = points.iterator();
        Point x = it.next();
        smaller = x.lgt;
        while(it.hasNext()){
            x = it.next();
            if(x.getLgt() < smaller){
                smaller = x.getLgt();
            }
        }
        return smaller;
    }
}
