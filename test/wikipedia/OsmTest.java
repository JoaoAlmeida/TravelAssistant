/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia;

import datasets.Osm;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fellipe
 */
public class OsmTest {
    private Osm osm = new Osm();
    
    public OsmTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
       
    @Test
    public void testSearchId(){
        String line = "613660 880725427 -22.9611876 -43.1782322 |addr:city=Rio de Janeiro|addr:country=BR|addr:housenumber=371|addr:street=Ladeira Coelho Cintra|amenity=school|IPP:BAIRRO=Copacabana|IPP:CHAVE=10814|IPP:CODBAIRRO=024|IPP:COD_INEP=33065012|IPP:COD_SMA=010814|IPP:CRE=02|IPP:DESIGNACAO=0205005|IPP:ENDNOVO=http://webapp.sme.rio.rj.gov.br/jcartela/publico/pesquisa.do?idSetor=10814&cmd=load|name=Escola Municipal Porto Rico|phone=+55-21-25411247|source=IPP";
        assertEquals("880725427", osm.searchId(line));
        
    }   
    
    @Test
    public void testSearchName(){
        String line = "613660 880725427 -22.9611876 -43.1782322 |addr:city=Rio de Janeiro|addr:country=BR|addr:housenumber=371|addr:street=Ladeira Coelho Cintra|amenity=school|IPP:BAIRRO=Copacabana|IPP:CHAVE=10814|IPP:CODBAIRRO=024|IPP:COD_INEP=33065012|IPP:COD_SMA=010814|IPP:CRE=02|IPP:DESIGNACAO=0205005|IPP:ENDNOVO=http://webapp.sme.rio.rj.gov.br/jcartela/publico/pesquisa.do?idSetor=10814&cmd=load|name=Escola Municipal Porto Rico|phone=+55-21-25411247|source=IPP";
        assertEquals("Escola Municipal Porto Rico", osm.searchName(line));
    }
    
    @Test
    public void testSearchBairro(){
        String line = "613348 875956058 -22.9211938 -43.2335523 |addr:city=Rio de Janeiro|addr:country=BR|addr:housenumber=317|addr:street=Rua Major ?vila|amenity=school|IPP:BAIRRO=Tijuca|IPP:CHAVE=10860|IPP:CODBAIRRO=033|IPP:COD_INEP=33068321|IPP:COD_SMA=010860|IPP:CRE=02|IPP:DESIGNACAO=0208016|IPP:ENDNOVO=http://webapp.sme.rio.rj.gov.br/jcartela/publico/pesquisa.do?idSetor=10860&cmd=load|name=Escola Municipal Leit?o da Cunha|phone=+55-21-25693092|source=IPP";
        assertEquals("Tijuca", osm.searchBairro(line));
    }
    
    @Test
    public void testSearchAdress(){
        String line = "633816 1078079936 -29.775118 -51.1156402 |addr:city=S?o Leopoldo|addr:country=BR|addr:housenumber=488|addr:street=Rua Leopoldo Freitas";
        System.out.println(osm.searchStreet(line));
        line = "610821 823958977 -22.9242402 -43.1872402 |amenity=police|IPP:Codigo=5.000000000|IPP:Endereco=Rua Francisco de Castro, 5|IPP:Telefone=3399-5070|name=7? DP - Santa Tereza|source=IPP";
        System.out.println(osm.searchStreet(line));
    }   

    @Test
    public void testFindAdress(){
        String line = "610740 823952603 -22.9318942 -43.6725361 |amenity=school|IPP:BAIRRO=Santa Cruz|IPP:CHAVE=11184|IPP:CODBAIRRO=149|IPP:COD_INEP=33085013|IPP:COD_SMA=011184|IPP:CRE=10|IPP:DESIGNACAO=1019020|IPP:ENDERECO=Rua Felipe Cardoso, 500|IPP:ENDNOVO=http://webapp.sme.rio.rj.gov.br/jcartela/publico/pesquisa.do?idSetor=11184&cmd=load|IPP:TELEFONES=3395-5743|name=Escola Municipal Professora Maria Santiago|source=IPP";
        System.out.println(osm.findAdress(line));
        line = "613347 875955957 -22.91624 -43.2365731 |addr:city=Rio de Janeiro|addr:country=BR|addr:street=Rua Maxwell|amenity=school|IPP:BAIRRO=Vila Isabel|IPP:CHAVE=10875|IPP:CODBAIRRO=036|IPP:COD_INEP=33068330|IPP:COD_SMA=010875|IPP:CRE=02|IPP:DESIGNACAO=0209008|IPP:ENDNOVO=http://webapp.sme.rio.rj.gov.br/jcartela/publico/pesquisa.do?idSetor=10875&cmd=load|name=Escola Municipal Madrid|phone=+55-21-22087594|source=IPP";
        System.out.println(osm.findAdress(line));
    }
    
    @Test
    public void testFindAmenity(){
        String text = "3 1277707292 -30.0376205 -51.2292695 |amenity=bench";
        assertEquals("1277707292" , osm.separateOsmAmenity(text)[0]);
        assertEquals("bench" , osm.separateOsmAmenity(text)[1]);
    }
    
    @Test
    public void testFindCoord(){
        String text = "3 1277707292 -30.0376205 -51.2292695 |amenity=bench";
        assertEquals("-30.0376205" , osm.searchCoord(text)[0]);
        assertEquals("-51.2292695" , osm.searchCoord(text)[1]);
    }
}
