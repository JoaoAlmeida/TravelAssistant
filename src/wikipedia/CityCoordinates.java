
package wikipedia;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;
import util.ReadPage;
import util.Writer;

/**
 * This class generates a file with cities and their spatial coordinates.
 * 
 * @author <Fellipe>
 */
public class CityCoordinates {

    private ArrayList<String> cities;
    private ReadPage rp;
    private File citiesFile;
    
    public CityCoordinates() {
        cities = new ArrayList<>();
        rp = new ReadPage();
        citiesFile = new File("cities_coordinates.txt");
    }

    public static void main(String[] args) throws FileNotFoundException, Exception {
        CityCoordinates cc = new CityCoordinates();
        cc.fillsList(new File(args[0]));        
        cc.run(args[1], args[2]);
    }

    public void run(String begin, String end){
       
        int b = Integer.parseInt(begin);
        int e = Integer.parseInt(end);
        int i = b;
        
        while(i>=b && i<=e && i<=cities.size()-1){
            String x = cities.get(i);
            String[] aux = separate(x);
            try {
                double[] coord = searchCoord(getXml(aux[0], aux[1]));
                Writer.writeFile(citiesFile, aux[0] + "|", coord[0] + " " + coord[1]);
            } catch (Exception ex) {
                System.out.println(aux[0] + " " + aux[1]);
            }
            i++;
        }
        
    }
    
    
    public void fillsList(File file) throws FileNotFoundException {
        Scanner input = new Scanner(file, "UTF-8");

        while (input.hasNextLine()) {
            String aux = input.nextLine();
            aux = aux.replaceAll("\\(", "");
            aux = aux.replaceAll("\\)", "");
            aux = aux.substring(4);    
            cities.add(aux);
        }
        input.close();
    }

    public ArrayList<String> getList() {
        return cities;
    }

    public String[] separate(String x) {
        String[] aux = new String[2];
        aux[0] = x.substring(0, x.lastIndexOf(" "));
        aux[1] = x.substring(x.lastIndexOf(" ") + 1);
        
        return aux;
    }

    public String getXml(String city, String state) throws Exception {
        city = URLEncoder.encode(city, "UTF-8");
        state = URLEncoder.encode(state, "UTF-8");
        return rp.getContent("http://maps.googleapis.com/maps/api/geocode/xml?address="+city+","+ state +"&sensor=false");
    }

     public double[] searchCoord(String xml) {
         String lat = xml.substring(xml.indexOf("<lat>")+5, xml.indexOf("<", xml.indexOf("<lat>") + 3));
         double aux[] = {0,0};
         aux[0] = Double.parseDouble(lat);
         String lgt = xml.substring(xml.indexOf("<lng>") + 5, xml.indexOf("<", xml.indexOf("<lng>")+3));
         aux[1] = Double.parseDouble(lgt);
         
         return aux;                  
     }
}
