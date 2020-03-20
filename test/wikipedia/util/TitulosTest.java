/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;
import stemmer.PTStemmerException;

/**
 *
 * @author fellipe
 */
public class TitulosTest {
    private NamesUtils t;

    public TitulosTest() throws PTStemmerException, FileNotFoundException, IOException, SAXException {
        t = new NamesUtils();
    }
    
    @BeforeClass
    public static void setUpClass() {
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    //@Test
    public void findCity() throws IOException, SAXException{
        String text = "O '''Jacarezinho''' � um bairro da cidade do Rio de Janeiro, no Brasil. Tamb�m � uma das maiores [[favelas]] da cidade. Localiza-se na [[Zona Norte (Rio de Janeiro)|Zona Norte]] da cidade, junto � via f�rrea. Era um bairro com altos �ndices de viol�ncia, principalmente relacionados ao consumo e ao tr�fico de drogas&lt;ref&gt;http://g1.globo.com/jornal-hoje/noticia/2011/03/operacao-do-bope-na-favela-do-jacarezinho-rj-deixa-cinco-mortos.html&lt;/ref&gt;.Seu IDH, no ano 2000, era de 0,731, o 121� colocado entre 126 regi�es analisadas na cidade do Rio de Janeiro.&lt;ref&gt;[http://www.armazemdedados.rio.rj.gov.br/arquivos/1172_%C3%ADndice%20de%20desenvolvimento%20humano%20municipal%20(idh).xls Tabela 1172 - �ndice de Desenvolvimento == Caracter�sticas ==[[Image:Entrada do Jacarezinho.JPG|thumb|Um dos acessos � Favela do Jacarezinho]][[File:Jacarezinho.JPG|thumb|Vista do bairro]]Tem popula��o estimada em mais de 36 000 habitantes, segundo dados de 2004 da prefeitura.� servida por uma [[Esta��o Jacarezinho|esta��o ferrovi�ria]] da [[Supervia]] e, nas suas imedia��es, passam importantes eixos vi�rios da cidade, especialmente a [[Avenida Dom H�lder C�mara]].Nasceu na comunidade, em janeiro de 1966, o ex-jogador de futebol e atual deputado federal [[Rom�rio]] e o funkeiro [[MC Serginho]].� de car�ter plano e integra ruas, avenidas e uma esta��o ferrovi�ria de igual nome. Comunica-se com outra favela pr�xima, a de [[Manguinhos (bairro do Rio de Janeiro)|Manguinhos]].";
        t.loadCitiesCoord();
        String city = t.findCity(text);
        assertEquals("Rio de Janeiro|", city);
        
    }
    
    //@Test
    public void testMergeRedicts() throws SAXException, FileNotFoundException, IOException{
        t.loadPagesSD();
        t.loadRedirectPages();
        t.loadDesambiguatesPages();
        t.mergeRedirects();
    }

    /**
     * Test of separateRedirectLine method, of class Titulos.
     */
    //@Test
    public void testSeparateRedirectLine() {
        String text = "Bobs| Bob?s";
        assertEquals("bobs",NamesUtils.separateRedirectLine(text)[0]);
        assertEquals("bob?s",NamesUtils.separateRedirectLine(text)[1]);
    }


    @Test
    public void desambiguationTest() throws IOException, SAXException, FileNotFoundException, PTStemmerException{
        t.loadCitiesCoord();
        t.generateObjectCitiesText();
    }  
}
