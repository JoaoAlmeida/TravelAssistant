/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fellipe
 */
public class MergeWikiOsmTest {
    
    private MergeWikiOsm wo;
    
    public MergeWikiOsmTest() throws UnsupportedEncodingException, FileNotFoundException {
         wo = new MergeWikiOsm();
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class MergeWikiOsm.
     */
    @Test
    public void testMain() {
        
    }

    /**
     * Test of loadObjects method, of class MergeWikiOsm.
     */
    @Test
    public void testLoadObjects() throws Exception {
        
    }

    /**
     * Test of separateFile method, of class MergeWikiOsm.
     */
    @Test
    public void testSeparateFullFile() {
        String line = "34574 -26.9113112 -48.6708521 Itajaí";
        String[] aux = wo.separateFullFile(line);
        assertEquals("34574", aux[0]);
        assertEquals("-26.9113112", aux[1]);
        assertEquals("-48.6708521", aux[2]);
        assertEquals("Itajaí", aux[3]);
        
        
    }

    
    /**
     * Test of addCategories method, of class MergeWikiOsm.
     */
    @Test
    public void testAddCategories() {
        
    }
}
