/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fellipe
 */
public class SearchObjectTest {
    private SearchObject  so = new SearchObject();
    
    public SearchObjectTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of main method, of class searchObject.
     */
    @Test
    public void testMain() {
    }

    /**
     * Test of separateOsm method, of class searchObject.
     */
    @Test
    public void testSeparateOsm() {
        String text = "2 597284 -23.5552 -46.6236497 |public_transport=stop_position";
        assertEquals("-23.5552 -46.6236497", so.separateOsm(text));
    }

    @Test
    public void testSeparateCoordWiki(){
        String text = "253 33.93333333333333 66.18333333333334  O '''Afeganistão'";
        assertEquals("253", so.separateCoordWiki(text)[0]);
        assertEquals("33.93333333333333 66.18333333333334", so.separateCoordWiki(text)[1]);
    }
}

