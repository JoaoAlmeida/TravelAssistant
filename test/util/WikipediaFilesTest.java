/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import wikipedia.util.WikipediaFiles;
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
public class WikipediaFilesTest {

    public WikipediaFilesTest() {
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
     * Test of findCoordinates method, of class WikipediaFiles.
     */
    @Test
    public void testFindCoordinates() throws Exception {
        String line = "253 33.93333333333333 66.18333333333334  O afeganistão";
        assertEquals(Double.parseDouble("33.93333333333333"), WikipediaFiles.findCoordinates(line)[0], 1);
        assertEquals(Double.parseDouble("66.18333333333334"), WikipediaFiles.findCoordinates(line)[1], 1);
    }

    @Test
    public void testSeparateWiki() {
        String text = "220 Astronomia";
        assertEquals("220", WikipediaFiles.separateWiki(text)[0]);
        assertEquals("Astronomia", WikipediaFiles.separateWiki(text)[1]);
    }
    @Test
    public void testSeparateTextWiki(){
        String text = "253 33.93333333333333 66.18333333333334  O '''Afeganistão'";
        String[] aux;
        aux = WikipediaFiles.separateTextFile(text);
        assertEquals(" O '''Afeganistão'",aux[3]);
    }
}
