
package util;

import java.text.DecimalFormat;

/**
 *
 * @author fellipe
 */
public class Converter {
    /**
     * Converts the coordinates in the format DMS (Degrees/Minutes/Second) to
     * the format DD (Decimal degrees).
     *
     * @param   str  latitude and longitude contening degrees, minutes, seconds and
     * direction.
     * @return  latitude and longitude in decimal format.
     */
    public static double[] convertCoordinates(String[][] str) {        
        double[] decimal = {0, 0}; //decimal[0] is latitude and decimal[1] is longitude;

        if (str[0][3] == null || str[1][3] == null) {
            return null;
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                if (str[i][j] == null || str[i][j].equals("")) {
                    str[i][j] = "0";
                }
            }
        }
        for (int i = 0; i < 2; i++) {
            try {
                
                decimal[i] = Double.parseDouble(str[i][0].replaceAll(",", "."));
            } catch (NumberFormatException ex) {
                return null;
            }
            try {
                decimal[i] += Double.parseDouble(str[i][1].replaceAll(",", ".")) / 60;
            } catch (NumberFormatException ex) {
                return null;
            }
            try {                
                decimal[i] += Double.parseDouble(str[i][2].replaceAll(",", ".")) / 3600;
            } catch (NumberFormatException ex) {
                return null;
            }
        }       
        
        if(str[0][3].equals("S") || str[0][3].contains("W") || str[1][3].contains("O")){
            decimal[0] = decimal[0]*-1;
        }
        if(str[1][3].equals("S") || str[1][3].contains("W") || str[1][3].contains("O")){
            decimal[1] = decimal[1]*-1;
        }
        
        //aux[0] = df.format(decimal[0]);    
        //aux[1] = df.format(decimal[1]);
        // return aux;
        return decimal;
    } 
    
    public static String[] convertCoordinatesStr(String[][] str) {        
        double[] decimal = convertCoordinates(str);
        String[] aux = new String[2];        
        aux[0] = "" + decimal[0];    
        aux[1] = "" + decimal[1];
        return aux;
    }
}
