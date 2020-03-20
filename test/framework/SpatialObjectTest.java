/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;
import stemmer.PTStemmerException;
import util.file.BlockColumnFileImpl;
import util.file.ColumnFileException;
import util.file.DataNotFoundException;

/**
 *
 * @author fellipe
 */
public class SpatialObjectTest {
    
    public SpatialObjectTest() {
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

  
    @Test
    public void testEquals() throws PTStemmerException, FileNotFoundException, IOException, SAXException {
        long id = 1;
        SpatialObject o1 = new SpatialObject(id, "", "", -13.0093206, -38.5320549, "Bar da Brahma","" ,"", "");
        id = 2;
        SpatialObject o2 = new SpatialObject(id, "", "", -13.0093279, -38.5319024, "Bar da Brahma", "","", "");
        assertTrue(o1.equals(o2));
        /*o2 = new SpatialObject(id, "", "", -13.0093279, -38.5319024, "bar brahma", "","", "");
        assertTrue(o1.equals(o2));
        o2 = new SpatialObject(id, "", "", -13.0093279, -38.5319024, "bares da brahma", "","", "");
        assertTrue(o1.equals(o2));
        o2 = new SpatialObject(id, "", "", -13.0093279, -38.5319024, "bares brahma", "", "","");
        assertTrue(o1.equals(o2));
         */
    }
    
    @Test
    public void testCreateObject() throws IOException, ColumnFileException, DataNotFoundException{
        BlockColumnFileImpl bcf = new BlockColumnFileImpl("/home/fellipe/objetos", 600, 1000000000);
        
        //Criando objeto 2
        SpatialObject ob = new SpatialObject(Long.parseLong("1"), "12", "2", 10.0, 10.1, "Pelourinho", "amenity", "museum", "um texto");
        byte[] bytes = ob.toByteArray();        
        bcf.insert((int) Long.parseLong("1"), bytes);
        
        //Criando objeto 2
        SpatialObject ob2 = new SpatialObject(Long.parseLong("2"), "20", "22", 20.0, 20.1, "Largo do Pelourinho", "natural", "beach", "texto dois нннн");
        bytes = ob2.toByteArray();
        bcf.insert((int) Long.parseLong("2"), bytes);               
        
        //Select
        bytes = new byte[600];
        bcf.select(2, bytes);
        SpatialObject retorno = SpatialObject.createObject(bytes);        
                
        assertEquals(20.0, retorno.getLatitude(), 0.0001);
        assertEquals(20.1, retorno.getLongitude(), 0.0001);
        assertEquals("Largo do Pelourinho", retorno.getName());        
        assertEquals("natural", retorno.getCategory());
        assertEquals("beach", retorno.getSubCategory());
        assertEquals(Long.parseLong("2"), retorno.getId());
        assertEquals("20", retorno.getIdWiki());
        assertEquals("22", retorno.getIdOsm());
        assertEquals("texto dois нннн", retorno.getText());
        
        bcf.select(1, bytes);
        retorno = SpatialObject.createObject(bytes);        
        
        assertEquals(retorno.getLatitude(), 10.0, 0.0001);
        assertEquals(retorno.getLongitude(), 10.1, 0.0001);
        assertEquals(retorno.getName(), "Pelourinho");
        assertEquals(retorno.getCategory(), "amenity");
        assertEquals(retorno.getSubCategory(), "museum");
        assertEquals(retorno.getId(), Long.parseLong("1"));
        assertEquals(retorno.getIdWiki(), "12");
        assertEquals(retorno.getIdOsm(), "2");
        assertEquals(retorno.getText(), "um texto");        
    }    
}
