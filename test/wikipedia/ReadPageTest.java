/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia;

import util.ReadPage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author fellipe
 */
public class ReadPageTest {
    private ReadPage ReadPage;
    
    public ReadPageTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getContent method, of class readPag.
     */
    @Test
    public void testGetContent() throws Exception {
        ReadPage = new ReadPage();
        System.out.println(ReadPage.getContent("http://maps.googleapis.com/maps/api/geocode/xml?address=Anápolis,GO&sensor=false"));
        System.out.println(ReadPage.getContent("http://maps.googleapis.com/maps/api/geocode/xml?address=Feira_de_Santana,BA&sensor=false"));
    }
}
