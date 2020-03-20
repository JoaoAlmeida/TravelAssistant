/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datasets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import org.xml.sax.SAXException;
import util.TextClean;
import util.Writer;

/**
 *
 * @author fellipe
 */
public class Category {

    public static void main(String[] args) throws FileNotFoundException, SAXException, IOException {
        Category c = new Category();
        c.run();

    }

    public void run() throws FileNotFoundException, SAXException, IOException {
        Scanner input = new Scanner(new File("object.txt"), "ISO-8859-1");;
        File amenityFile = new File("amenity.txt");
        File shopFile = new File("shop.txt");
        File naturalFile = new File("natural.txt");
        File tourismFile = new File("tourism.txt");
        HashSet<String> amenity = new HashSet<String>();
        HashSet<String> tourism = new HashSet<String>();
        HashSet<String> shop = new HashSet<String>();
        HashSet<String> natural = new HashSet<String>();
        int i = 0;
        while (input.hasNext()) {
            System.out.println(++i);
            String line = input.nextLine();
            String cat = null;
            if (line.contains("tourism=")) {
                cat = findCategory(line, "tourism");
                if (cat != null) {                
                    cat = cat.replaceAll("_", " ");
                    if (cat.contains("/")) {
                        cat = cat.substring(0, cat.indexOf("/"));
                    }
                     tourism.add(TextClean.clean(cat));
                }
            } else if (line.contains("natural")) {
                cat = findCategory(line, "natural");
                if (cat != null) {
                    cat = cat.replaceAll("_", " ");
                    if (cat.contains("/")) {
                        cat = cat.substring(0, cat.indexOf("/"));
                    }
                    natural.add(TextClean.clean(cat));
                }
            } else if (line.contains("shop")) {
                cat = findCategory(line, "shop");
                if (cat != null) {
                    cat = cat.replaceAll("_", " ");
                    if (cat.contains("/")) {
                        cat = cat.substring(0, cat.indexOf("/"));
                    }
                    shop.add(TextClean.clean(cat));
                }
            } else if (line.contains("amenity")) {
                cat = findAmenity(line);
                if (cat != null) {                    
                    cat = cat.replaceAll("_", " ");
                    if (cat.contains("/")) {
                        cat = cat.substring(0, cat.indexOf("/"));
                    }
                    amenity.add(TextClean.clean(cat));
                }
            }

        }

        Iterator<String> it = amenity.iterator();
        while (it.hasNext()) {
            String x = it.next();
            if (x.length() > 1) {
                Writer.writeFile(amenityFile, x);
            }
        }
        it = shop.iterator();
        while (it.hasNext()) {
            String x = it.next();
            if (x.length() > 1) {
                Writer.writeFile(shopFile, x);
            }
        }
        it = natural.iterator();
        while (it.hasNext()) {
            String x = it.next();
            if (x.length() > 1) {
                Writer.writeFile(naturalFile, x);
            }
        }
        it = tourism.iterator();
        while (it.hasNext()) {
            String x = it.next();
            if (x.length() > 1) {
                Writer.writeFile(tourismFile, x);
            }
        }
    }

    public static String findCategory(String line, String cat) {
        int x, y;
        if (line.indexOf("|" + cat) != -1) {
            x = line.indexOf("|" + cat);
            y = line.indexOf("=", x) + 1;
            if (line.indexOf("|", y) != -1) {
                return line.substring(y, line.indexOf("|", y));
            } else {
                return line.substring(y, line.length());
            }
        }
        return null;
    }

    public static String findAmenity(String line) {
        int x, y;
        if (line.indexOf("|amenity") != -1) {
            x = line.indexOf("|amenity");
            y = line.indexOf("=", x) + 1;
            if (line.indexOf("|", y) != -1) {
                return line.substring(y, line.indexOf("|", y));
            } else {
                return line.substring(y, line.length());
            }
        }
        return null;
    }
}