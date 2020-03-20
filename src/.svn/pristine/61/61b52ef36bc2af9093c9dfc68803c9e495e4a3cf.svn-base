/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia.util;

import wikipedia.util.Converter;

/**
 *
 * @author fellipe
 */
public class WikipediaFiles {
    
    public WikipediaFiles(){    
    }
    
    /**
     * Separate the line of Wikipédia file, returning the id and the data of file.
     * 
     * @param line line relative to an article of the Wikipédia 
     * @return id and dates of line.
     */
    public static String[] separateWiki(String line) {
        String aux[] = {"", ""};
        if (line.contains(" ")) {
            aux[0] = line.substring(0, line.indexOf(" "));
            aux[1] = line.substring(line.indexOf(" ") + 1, line.length());
            return aux;
        }
        return null;
    }
    
    /**
     * Separa linha do arquivo com objetos anotados.
     *
     * @return
     */
    public static String[] separateTextFile(String line) {
        String[] aux = {"", "", "", "", ""};
        int x, y;
        x = line.indexOf(" ");
        aux[0] = line.substring(0, x);
        x++;
        y = line.indexOf(" ", x);
        aux[1] = line.substring(x, y);
        x = y + 1;
        y = line.indexOf(" ", x);
        aux[2] = line.substring(x, y);
        y++;
        aux[3] = line.substring(y, line.length());
        return aux;
    }
    
    /**
     * Find coordinates for each line of the a file.
     * @param line
     * @return
     */
    public static double[] findCoordinates(String line) throws Exception{
        int x, y;
        double[] coord = null;
        x = line.indexOf(" ") + 1;
        y = line.indexOf(" ", x);        
        y = y + 1;
        y = line.indexOf(" ", y);
        if(y>=x){
        coord = Converter.coordParseDouble(line.substring(x, y));
        
        return coord;
        }else{
            return null;
        }
    }
}
