/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fellipe
 */
public class TextCleanTest {
    
    
    @Test
    public void testCleanTags(){
        String text = "<tag>any text</tag>";
        text = text.replaceAll("<[/]*?.+?[/]*  ?>","");
        System.out.println(text);
             
        /*assertEquals("text1 text2", TextClean.cleanTags(text));
        
        
        text = "text1 <tag>any text</tag> text2<tag>trash</tag>";
        assertEquals("text1 text2", TextClean.cleanTags(text));
        
        text = "text1 <tag>any text</tag> text2<tag>trash</tag> text3";
        assertEquals("text1 text2 text3", TextClean.cleanTags(text));
        
        text = "text1 <tag>any text<tag></tag></tag> text2<tag>trash</tag><tag></tag> text3";
        assertEquals("text1 text2 text3", TextClean.cleanTags(text));*/
    }
    

}
