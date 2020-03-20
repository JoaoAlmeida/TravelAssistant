/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import wikipedia.util.Converter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fellipe
 */
public class ConverterTest {
    
    public ConverterTest() {
    }
    
    @Test
    public void testConvertCoordinates(){
        String[][] coordinates = new String[2][4];
        coordinates[0][0] = "20";
        coordinates[0][1] = "28";
        coordinates[0][2] = "57";
        coordinates[0][3] = "S";
        coordinates[1][0] = "39";
        coordinates[1][1] = "25";
        coordinates[1][2] = "50";
        coordinates[1][3] = "O";
        double[] test = Converter.convertCoordinates(coordinates);
        assertEquals(-20.4825, test[0], 0.0001);
        assertEquals(-39.43056, test[1], 0.0001);
        coordinates[1][2] = "";
        coordinates[0][1] = "";
        assertNotNull(test = Converter.convertCoordinates(coordinates));
        assertEquals(-39.41667, test[1], 0.0001);
        assertEquals(-20.01583, test[0], 0.0001);
        coordinates[0][0] = "|20";
        assertNull(Converter.convertCoordinates(coordinates));
        
        coordinates[0][0] = "20";
        coordinates[1][2] =  null;

        assertNotNull(Converter.convertCoordinates(coordinates));
        
        coordinates[0][3] = null;
        assertNull(Converter.convertCoordinates(coordinates));
 
        
    }
}
