/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiktionary;

import java.io.FileNotFoundException;
import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fellipe
 */
public class FindNounsTest {
    /**
     *
     */
    public FindNouns fn; 
    
    public FindNounsTest() throws FileNotFoundException {
        fn = new FindNouns();
    }

    @Test
    public void testReadFiles() throws Exception {
        
    }
    
    @Test
    public void testFillHash() {
        String text = "excerto |extrato|texto|fragmento||perícope||";
        String[] separate = fn.separateString(text);
        assertEquals("excerto", separate[0]);
        assertEquals("|extrato|texto|fragmento||perícope||", separate[1]);
        
        text = "excerto";
        assertNull(fn.separateString(text));
        
        text = "excerto |";
        separate = fn.separateString(text);
        assertEquals("excerto", separate[0]);
        assertEquals("|", separate[1]);
        
        text = "excerto |extrato|texto|fragmento||perícope||";
        HashMap<String,String> hm = new HashMap<>();
        hm =  fn.fillHash(hm, text);
        assertEquals("|extrato|texto|fragmento||perícope||", hm.get("excerto"));
        text = "extrato|texto|fragmento||perícope|";
       
        String[] aux = text.split("\\|");
        System.out.println(aux[0]);
        for(String x : aux){
            System.out.println(x);
        }
    }
}
