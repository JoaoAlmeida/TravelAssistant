
package wikipedia;



import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;
import wikipedia.util.Converter;

/**
 *
 * @author fellipe
 */
public class ContentExtractorTest {
    private ContentExtractor cc;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        cc = new ContentExtractor();

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of startDocument method, of class ConverteCoordenadas.
     */
    public void testStartDocument() throws Exception {

    }

    @Test
    public void testId(){
        HashMap<String, Boolean> ids = new HashMap<String, Boolean>();
        Scanner arquivo = null;

        ids.put("41825", false);
        ids.put("996178", false);
        ids.put("114483", false);
        ids.put("36795", false);
        ids.put("41830", false);
        ids.put("1319", false);
        ids.put("209618", false);
        ids.put("287781", false);
        ids.put("40032", false);
        ids.put("6063", false);

        try{
            arquivo = new Scanner( new File("/home/fellipe/NetBeansProjects/TravelAssistant/arquivosWikipédia/title.txt"));
            while(arquivo.hasNext()){
                String x = arquivo.next();
                if(ids.containsKey(x)){

                    ids.put(x, true);
                }
            }
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }finally{
            if(arquivo != null){
                arquivo.close();
            }
        }

        for(Boolean b : ids.values()){
            assertTrue(b.booleanValue());

        }
    }

   @Test
    public void testTextClean() throws SAXException{
        String text = "text1 {{any text}} text2";

        text = "text1 {{{any text blabla {{anytext}} text2";

        assertEquals("text1 text2", cc.textClean(text)[0]);

        text = "text1 {{any text}} text2 [[any text [[text3]]]] text4";
        assertEquals("text1 text2 text3 text4", cc.textClean(text)[0]);

        text = "text1 {{any text}} text2 [[text3 [[text4]] text5]] text6";
        assertEquals("text1 text2 text4 text6", cc.textClean(text)[0]);

        text = "text1 {{any text}} text2 [[text3 [[text4  [[text5]]]] text6]] text7";
        assertEquals("text1 text2 text5 text7", cc.textClean(text)[0]);

        text = "text1 {{any text}} text2 [[text3 [[text4 {{any text {{any text}} bla}} [[text5]]]] text6]] text7";
        assertEquals("text1 text2 text5 text7", cc.textClean(text)[0]);

        text = "text1 [[any text|text2]]";
        assertEquals("text1 text2", cc.textClean(text)[0]);

        text = "text1 [[any text|text|text2]]";
        assertEquals("text1 ", cc.textClean(text)[0]);

        text = "[[ja:間接民主制]]";
        assertEquals("|ja:間接民主制|", cc.textClean(text)[1]);

        text = "[[Ficheiro:Dona Ana beach, Lagos.jpg|right|thumb|200px|Praia Dona Ana em [[Lagos (Algarve)]].]]";
         assertEquals("", cc.textClean(text)[0]);
   }

    @Test
    public void testConvertCoordinates(){
        String[][] coordinates = new String[2][4];
        coordinates[0][0] = "20";
        coordinates[0][1] = "28";
        coordinates[0][2] = "57";
        coordinates[0][3] = "S";
        coordinates[1][0] = "39";
        coordinates[1][1] = "25";
        coordinates[1][2] = "50";
        coordinates[1][3] = "O";
        String[] test = Converter.convertCoordinatesStr(coordinates);
        assertEquals("-20.482499999999998", test[0]);
        assertEquals("-39.43055555555555", test[1]);
        coordinates[1][2] = "";
        coordinates[0][1] = "";
        assertNotNull(test = Converter.convertCoordinatesStr(coordinates));
        assertEquals("-39.416666666666664", test[1]);
        assertEquals("-20.015833333333333", test[0]);
        coordinates[0][0] = "|20";
        assertNull(Converter.convertCoordinates(coordinates));

        coordinates[0][0] = "20";
        coordinates[1][2] =  null;

        assertNotNull(Converter.convertCoordinates(coordinates));

        coordinates[0][3] = null;
        assertNull(Converter.convertCoordinates(coordinates));


    }

     /**
     * Test of geocoordenadas method, of class ConverteCoordenadas.
     */
    @Test
    public void testGeocoordenadas() throws Exception {
        String test1 = "{{geocoordenadas|22_56_57.52_S_43_09_W|}}";
        String[][] test2 = new String[2][4];

        test2 = cc.geocoordenadas(test1);
        assertEquals("22", test2[0][0]);
        assertEquals("56", test2[0][1]);
        assertEquals("57.52", test2[0][2]);
        assertEquals("S", test2[0][3]);
        assertEquals("W", test2[1][3]);

        test1 = "{{geocoordenadas|22_S_43_09_W_}}";
        test2 = cc.geocoordenadas(test1);
        assertEquals("22", test2[0][0]);
        assertEquals("W", test2[1][3]);
        assertEquals("09", test2[1][1]);





    }

    /**
     * Test of coord method, of class ConverteCoordenadas.
     */
    @Test
    public void testCoord() throws Exception {
        String test1 = new String("{{Coord|69|24|N|86|11|E}}");
        String[][] test2 = new String[2][4];

        test2 = cc.coord(test1);
        assertEquals("69", test2[0][0]);
        assertEquals("24", test2[0][1]);
        assertEquals("N", test2[0][3]);
        assertNull(test2[1][2]);
        assertEquals("E", test2[1][3]);
        test1 = "{{coor dms|display=inline,title|43|51|28.5"
                + "|N|18|25|43.5|E}}";
        test2 = cc.coord(test1);
        assertEquals("43", test2[0][0]);
        assertEquals("E", test2[1][3]);
        assertEquals("25", test2[1][1]);
        assertEquals("43.5", test2[1][2]);


        test1 = " |Coordenadas={{coord|22|57|8.7|S|43|12|42|W}} "
+ "|imagem_mapa_coordenada = Brasil";
        test2 = cc.coord(test1);
        assertNotNull(Converter.convertCoordinates(test2));

        test1 = "{{Coor dms|12.974172|||S|38.513314|||O|display=title|escala=1000}}";
        test2 = cc.coord(test1);
        assertNotNull(Converter.convertCoordinates(test2));
    }

    /**
     * Test of latP method, of class ConverteCoordenadas.
     */
   @Test
    public void testLatP() throws Exception {
        String test1 = "|latP        =N   | latG =40| latM =17    | latS =24 "
     + " |lonP        =W   | lonG =07| lonM =57    | lonS =57 ";

        String[][] test2;

        test2 = cc.latP(test1);

        assertNotNull(cc.latP(test1));
        assertEquals("40", test2[0][0]);
        assertEquals("17", test2[0][1]);
        assertEquals("N", test2[0][3]);
        assertEquals("W", test2[1][3]);
        assertEquals("57", test2[1][2]);

        test1 = " | lat_degrees             = 37 | lat_minutes             = 49|"
                + " lat_seconds             = 36| lat_direction           = N| "
                + "long_degrees            = 122| long_minutes            = 25| "
                + " long_direction          = W\n --";

        test2 = cc.latP(test1);

        assertNotNull(cc.latP(test1));

        assertEquals("37", test2[0][0]);
        assertEquals("49", test2[0][1]);
        assertEquals("N", test2[0][3]);
        assertEquals("122", test2[1][0]);
        assertEquals(null, test2[1][2]);
        assertEquals("W", test2[1][3]);

        test1 = "blabla [[Categoria:Alguma]]";
        System.out.println(cc.textClean(test1)[0]);

   }



   /**
     * Test of startElement method, of class ConverteCoordenadas.
     */

    public void testStartElement() throws Exception {

    }



    /**
     * Test of checkDirection method, of class ConverteCoordenadas.
     */

    public void testVerificaGrau() {

    }



    /**
     * Test of checkDistance method, of class ConverteCoordenadas.
     */



    public void testVerificaDistancia() {

    }

    /**
     * Test of checkNext method, of class ConverteCoordenadas.
     */

    public void testVerificaProximo() {
    }



    /**
     * Test of endDocument method, of class ConverteCoordenadas.
     */

    public void testEndDocument() throws Exception {

    }

    /**
     * Test of writeFile method, of class ConverteCoordenadas.
     */

    public void testEscreverArquivo() throws Exception {

    }

    /**
     * Test of main method, of class ConverteCoordenadas.
     */

    public void testMain() throws Exception {

    }

}