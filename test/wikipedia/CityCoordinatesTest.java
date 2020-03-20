/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia;

import java.io.File;
import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fellipe
 */
public class CityCoordinatesTest {
    private CityCoordinates cc;
    
    public CityCoordinatesTest() {
        cc = new CityCoordinates();
    }
    
    @BeforeClass
    public static void setUpClass() {        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of main method, of class CityCoordinates.
     */
        

    
    //@Test
    public void testFillsList() throws Exception {
        cc.fillsList(new File("Municipios.txt"));
        ArrayList<String> cities = cc.getList();
        
        for(String x : cities){
            System.out.println(x);
        }                
    }
    
    @Test
    public void testSeparate() throws Exception {
        String city = "Abadia dos Dourados MG";
        assertEquals("Abadia dos Dourados", cc.separate(city)[0]);
        assertEquals("MG", cc.separate(city)[1]);
        
        city = "Vilhena RO";
        assertEquals("Vilhena", cc.separate(city)[0]);
        assertEquals("RO", cc.separate(city)[1]);    
    }
    
    @Test
    public void testGetXml() throws Exception {
        String[] obj = {"Abadiânia", "GO"};
        String xml = cc.getXml(obj[0], obj[1]);
        System.out.println(xml);
    }
    
    
    
    @Test
    public void testSearchCoord() throws Exception {
        String xml = "<location>\n<lat>-19.1574953</lat>\n<lng>-45.4452747</lng>\n </location>";
        double[] coord = cc.searchCoord(xml);
        assertEquals(-19.1574953, coord[0], 0.001);
        assertEquals(-45.4452747, coord[1], 0.001);
    }
}
