/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;
import stemmer.OrengoStemmer;
import stemmer.PTStemmerException;
import stemmer.Stemmer;
import util.SpatialUtils;
import wikipedia.util.Converter;

/**
 *
 * @author fellipe
 */
public class AnotacaoTest {

    private Anotacao mo;
    
    
    public AnotacaoTest() throws FileNotFoundException, IOException, PTStemmerException, SAXException {
        mo = new Anotacao("Brasil");
        
        //                                                                                                                                                                                                                          mo.fillsHash();
      //  mo.arquivoDesambiguation();
    }

    
    
   

    @AfterClass
    public static void tearDownClass() {
    }

    
    

     @Test
    public void testSeparateOsm() throws FileNotFoundException, IOException {
        String text = "1 2084441556 -27.8659588 -55.1332782 |name=Hotel Aca|tourism=hotel";
        
        assertEquals("-27.8659588 -55.1332782", mo.separateOsm(text)[0]);
        assertEquals("Hotel Aca", mo.separateOsm(text)[1]);
        
        text = "2 2084450558 -33.4283698 -70.6187734 |amenity=pub|name=Bar Central";   
        assertEquals("-33.4283698 -70.6187734", mo.separateOsm(text)[0]);
        assertEquals("Bar Central", mo.separateOsm(text)[1]);
        
        text = "2 2084450558 -33.4283698 -70.6187734 |amenity=pub";   
        assertNull(Anotacao.separateOsm(text));
    }

  @Test
    public void separateCities(){
        String text = "Abadia dos Dourados| -18.4866769 -47.4029106";
        String[] aux = mo.separateCities(text);
        assertEquals("Abadia dos Dourados", aux[0]);
        assertEquals("-18.4866769 -47.4029106", aux[1]);
    }
    
           
    @Test
    public void testCalcDistance(){
        System.out.println(Anotacao.calcDistance(-27.8659588, -55.1332782, -33.4283698, -70.6187734)); 
        System.out.println(Anotacao.calcDistance(-12.9703817, -38.512382, -12.2554649, -38.9542874)); 
    }
    
    @Test
    public void testCoordParseDouble() throws Exception{
        double[] d = Converter.coordParseDouble("-27.8659588 -55.1332782"); 
        System.out.println(d[0] +" " + d[1]);
        
    } 
}
