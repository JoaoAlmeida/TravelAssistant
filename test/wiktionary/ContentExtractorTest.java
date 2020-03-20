/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiktionary;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.Attributes;

/**
 *
 * @author fellipe
 */
public class ContentExtractorTest {
    ContentExtractor cc;
    
    @Before
    public void setUp() {
        cc = new ContentExtractor();
    }
    @Test
    public void testConjugacoes(){
        
    }
    
    
    
    @Test
    public void testFindConjugation(){
        String text = "{{{1|}}}faz | {{{1|}}}fazem | {{{1|}}}fizera}}";
        String x = cc.findConjugation(text);
        assertEquals("faz|fazem|fizera", x);
        
        text = "{{link opcional|{{{1}}}a}} | {{link opcional|{{{1}}}am}}";
        x = cc.findConjugation(text);
        assertEquals("a|am", x);
        
        text = "[[haver]] |{{link opcional|havendo}} |{{link opcional|havido}}";
        x = cc.findConjugation(text);
        assertEquals("havendo|havido", x);
    }
    
    @Test
    public void testConjugation(){
        String text = "===Conjugação=== {{conj/pt|pe|gar}}";
        String[] x = cc.conjugation(text);
        assertEquals("pe", x[0]);
        assertEquals("gar", x[1]);
        
        text = "===Conjugação=== {{conj.pt.fazer}}";
        x = cc.conjugation(text);
        assertEquals("fazer", x[0]);
        assertEquals("", x[1]);
        
        text = "===Conjugação==={{conj.pt.ar|sach}}";
        x = cc.conjugation(text);
        assertEquals("ar", x[0]);
        assertEquals("sach", x[1]);
    }
    
    @Test
    public void testVerTambem(){
       
        String text = "==Ver também== "
                + "===No Wikcionário==={{verTambém.Ini}}* "
                + "[[armamento]]{{verTambém.NovaColuna}}* [[beleza]]"
                + "{{verTambém.Fim}}===Na Wikipédia===";
        text = cc.topicText(text.toLowerCase(), "==No Wikcionário==".toLowerCase());        
        assertEquals("|armamento|beleza|", cc.findSynonymous2(text, "[[", "]]", true));
      
        text = " ==Ver também== ===No Wikcionário=== {{verTambém.Ini}}"
               + "* {{verLigações|barulho}} " + "{{verTambém.NovaColuna}} "
               + "* [[estrépito]] {{verTambém.NovaColuna}} "
               + "* {{verLigações|ruído/pt}} {{verTambém.Fim}} "
               + "[[Categoria:Substantivo (Português)]]";
        text = cc.topicText(text.toLowerCase(), "==No Wikcionário==".toLowerCase());
        assertEquals("|estrépito|", cc.findSynonymous2(text, "[[", "]]", true));
        assertEquals("|barulho|ruído|", cc.findSynonymous2(text, "{{", "}}", false));
             System.out.println(text);           
        text = "==={{-varort-}}=== * {{escopo|Anterior ao AO 1990}} [[moluscóide]]"
                + "[[Categoria:Substantivo (Português)]]";
        text = cc.topicText(text.toLowerCase(), "=={{-varort-}}==".toLowerCase());
        System.out.println(text);

    }
}
