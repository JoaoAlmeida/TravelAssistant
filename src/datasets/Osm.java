/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datasets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;
import org.xml.sax.SAXException;
import util.Writer;

/**
 *
 * @author fellipe
 */
public class Osm {

    private File osmFile;
    private File addrFile;

    public Osm() {
        osmFile = new File("OSMParsing/object.txt");
        addrFile = new File("San Francisco.txt");
    }

    public static void main(String[] args) throws SAXException, FileNotFoundException, IOException {
        Osm osm = new Osm();
        osm.readFile();
    }

    public static String searchId(String line) {
        int x = 0;
        int y = 0;
        x = line.indexOf(" ") + 1;
        y = line.indexOf(" ", x);
        return line.substring(x, y);
    }

    public static String[] searchCoord(String line) {
        int x = 0, y = 0;
        x = line.indexOf(" ") + 1;
        x = line.indexOf(" ", x) + 1;
        y = line.indexOf(" ", x);
        y = line.indexOf(" ", y + 1);
        
        return line.substring(x, y).split(" ");
    }

    public static String searchCategory(String line) {
        String[] cat = {"", ""};
        if (line.contains("tourism=")) {
            cat[0] = "(tourism)";
            cat[1] = "(" + Category.findCategory(line, "tourism") + ")";
        } else if (line.contains("natural")) {
            cat[0] = "(natural)";
            cat[1] = "(" + Category.findCategory(line, "natural") + ")";
        } else if (line.contains("shop")) {
            cat[0] = "(shop)";
            cat[1] = "(" + Category.findCategory(line, "shop") + ")";
        } else if (line.contains("amenity")) {
            cat[0] = "(amenity)";
            cat[1] = "(" + Category.findAmenity(line) + ")";
        }

        if (cat[1] != null && !cat[1].equals("") && !cat[0].equals("")) {
            return cat[0] + " " + cat[1];
        } else {
            return "-";
        }
    }

    public static String searchName(String line) {
        if (line.indexOf("|name") != -1) {
            int x = line.indexOf("|name");
            int y = line.indexOf("=", x) + 1;
            if (line.indexOf("|", y) != -1) {
                return line.substring(y, line.indexOf("|", y));

            } else {
                return line.substring(y, line.length());
            }
        }
        return null;
    }

    public String searchBairro(String line) {
        if (line.toLowerCase().indexOf("|ipp:bairro") != -1) {
            int x = line.toLowerCase().indexOf("|ipp:bairro");
            x = line.indexOf("=", x) + 1;
            if (line.indexOf("|", x) != -1) {
                return line.substring(x, line.indexOf("|", x));

            } else {
                return line.substring(x, line.length());
            }
        }
        return null;
    }

    public String searchStreet(String line) {
        String street = null;
        if (line.toLowerCase().indexOf("|ipp:endereco") != -1) {
            int x = line.toLowerCase().indexOf("|ipp:endereco");
            x = line.indexOf("=", x) + 1;
            if (line.indexOf("|", x) != -1) {
                street = line.substring(x, line.indexOf("|", x));

            } else {
                street = line.substring(x, line.length());
            }
        } else if (line.toLowerCase().indexOf("|addr:street") != -1) {
            int x = line.toLowerCase().indexOf("|addr:street");
            x = line.indexOf("=", x) + 1;
            if (line.indexOf("|", x) != -1) {
                street = line.substring(x, line.indexOf("|", x));
            } else {
                street = line.substring(x, line.length());
            }

            if (street != null && line.toLowerCase().indexOf("|addr:housenumber") != -1) {
                x = line.toLowerCase().indexOf("|addr:housenumber");
                x = line.indexOf("=", x) + 1;
                if (line.indexOf("|", x) != -1) {
                    street += ", " + line.substring(x, line.indexOf("|", x));
                } else {
                    street += ", " + line.substring(x, line.length());
                }
            }
        }

        return street;
    }

    public void readFile() throws SAXException, FileNotFoundException, IOException {      
        Scanner input = new Scanner(new FileInputStream(osmFile), "ISO-8859-1");
        
//        while (input.hasNext()) {
//            String line = input.nextLine();
//            String adress = findAdress(line);
//            if (!adress.equals("")) {
//                Writer.writeFile(addrFile, searchId(line), adress);
//            }
//        }
        
//        input.close();
        
        int id = 1;
        
        while (input.hasNext()) {
            String line = input.nextLine();
            String name = searchName(line);
            String category = searchCategory(line);
            String[] coords = searchCoord(line);

            if (name != null) {
                Writer.writeFile(addrFile, id + "\t" + coords[0] + "\t" + coords[1] + "\t" + category + "\t" + name);
                id++;
            }
            //Acrescentado por mim para acrescentar ao arquivo final objetos que n�o possuem o campo "name"
            else if (!category.equals("-")){
                System.out.println(category);
                Writer.writeFile(addrFile, id + "\t" + coords[0] + "\t" + coords[1] + "\t" + category);
                id++;
            }
        }
        
        input.close();
        
    }

    public void findAmenity() throws FileNotFoundException, SAXException, IOException {
        Scanner input = new Scanner(new File("Brasil/object.txt"), "UTF-8");
        File amenity = new File("merge_osm/amenity.txt");
        while (input.hasNext()) {
            String[] aux = separateOsmAmenity(input.nextLine());
            if (aux != null) {
                Writer.writeFile(amenity, aux[0], aux[1]);
            }
        }

    }

    public String[] separateOsmAmenity(String line) {
        String aux[] = {"", ""};
        int x = 0, y = 0;
        x = line.indexOf(" ") + 1;
        y = line.indexOf(" ", x);
        aux[0] = line.substring(x, y);
        if (line.indexOf("|amenity") != -1) {
            x = line.indexOf("|amenity");
            y = line.indexOf("=", x) + 1;
            if (line.indexOf("|", y) != -1) {
                aux[1] = line.substring(y, line.indexOf("|", y));
                return aux;
            } else {
                aux[1] = line.substring(y, line.length());
                return aux;
            }
        }
        return null;

    }

    public String findAdress(String line) {
        String adress = "";
        String street = null;
        String bairro = null;
        street = searchStreet(line);
        if (street != null) {
            adress += street;
        }
        bairro = searchBairro(line);
        if (bairro != null) {
            adress += ", " + bairro;
        }

        return adress;
    }
}
