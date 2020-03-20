
package wikipedia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import org.xml.sax.SAXException;
import stemmer.OrengoStemmer;
import stemmer.PTStemmerException;
import stemmer.Stemmer;
import util.SpatialUtils;
import util.Util;
import wikipedia.util.WikipediaFiles;
import util.Writer;
import wikipedia.util.Converter;
import wikipedia.util.NamesUtils;

/**
 * This class is used to make the annotation textual in objects spatial.
 * 
 * @author fellipe
 */
public class Anotacao {

    private final double MAXDISTANCE = 50000;
    private HashSet<String> stopWords = new HashSet<>();
    private final File desambiguadasFile;
    private final File osmFile;
    private final File desambiguationFile;
    private File titleSDFile;
    private File citiesFile;
    private File mergeFile;
    private File titleStop;
    private File titleStem;
    private File titleStemStop;
    private HashMap<String, String> desambiguadas = new HashMap<>();
    private HashMap<String, String> wikiTitles = new HashMap<>();
    private HashMap<String, String> wikiTitlesStop = new HashMap<>();
    private HashMap<String, String> wikiTitlesStem = new HashMap<>();
    private HashMap<String, String> wikiTitlesStopStem = new HashMap<>();    
    private HashMap<String, String> citiesCoord = new HashMap<>();
    private int contEquals = 0, contStopWords = 0, contStemmingStopWords = 0, contStemming = 0;
    private HashMap<String, String> desambiguations = new HashMap<>();
    private HashMap<String, String> objectCities = new HashMap<>();
    private File desambiguationCitiesFile;
    private Stemmer st;
    private NamesUtils t;
    private int contOsmTitle = 0;
    private int contDisambiguations = 0;
    private int contMunicipio =0;    

    /**
     *  
     * @param dir directory where are the objects of OSM.
     * @throws FileNotFoundException
     * @throws PTStemmerException
     * @throws IOException
     * @throws IOException
     * @throws SAXException
     */
    public Anotacao(String dir) throws FileNotFoundException, PTStemmerException, IOException, IOException, SAXException {
        desambiguadasFile = new File("arquivosWikipédia/disambiguates.txt");
        titleStop = new File("arquivosWikipédia/title_stopWords.txt");
        titleStem = new File("arquivosWikipédia/title_Stemming.txt");
        titleStemStop = new File("arquivosWikipédia/title_stopWordsStemming.txt");
        titleSDFile = new File("arquivosWikipédia/titles_without_disambiguations.txt");
        File dir2 = new File(dir);        
        osmFile = new File(dir2, "object.txt");
        desambiguationFile = new File("arquivosWikipédia/disambiguations.txt");
        st = new OrengoStemmer();
        st.enableCaching(1000);
        citiesFile = new File("arquivosWikipédia/cities_coordinates.txt");
        File dir3 = new File("merge_osm");
        dir3.mkdir();
        mergeFile = new File(dir3, "wiki-osm.txt");
        desambiguationCitiesFile = new File("arquivosWikipédia/object_cities.txt");
        t = new NamesUtils();
    }

    
    /**
     *
     * @param args directory where are the objects of OSM.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SAXException
     * @throws PTStemmerException
     * @throws Exception
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, SAXException, PTStemmerException, Exception {
        long time = System.currentTimeMillis();
        Anotacao anotacao = new Anotacao(args[0]);
        System.out.println("Carregando arquivos...");
        anotacao.fillsHash();
        System.out.println("Comparando títulos...");
        anotacao.compareTitle();
        System.out.println(", concluded in " + Util.time(System.currentTimeMillis() - time) + ".");

    }

    /**
     * Makes a comparison between titles of wikipédia and OSM. Uses techniques 
     * with StopWords and Stemming and solves cases of disambiguation.
     * 
     * @throws SAXException
     * @throws PTStemmerException
     * @throws FileNotFoundException
     */
    public void compareTitle() throws SAXException, PTStemmerException, FileNotFoundException, Exception {
        //Set<String> setWiki = FullTitles.keySet();
        Scanner input = new Scanner(osmFile, "ISO-8859-1");
        File unannotated = new File("merge_osm/não_anotados.txt");
        while (input.hasNext()) {
            String line = input.nextLine();
            try{        
            String[] aux = separateOsm(line);

            if (aux != null) {
                                
                contOsmTitle++;
                String titleStop1 = t.removeStopWords(aux[1]);
                String titleStem1 = t.stemming(aux[1]);
                String titleStopSt = t.StemmingStopWords(aux[1]);
                //Se estiver no arquivo de desambiguação
                if (desambiguations.containsValue(aux[1].toLowerCase())) {
                    resolveDesambiguacao(aux[1], aux[0]);

                } else {
                    //Se não... testa removendo as stopwords.
                    String aux1 = t.removeStopWords(aux[1]);

                    if (aux1 != null && desambiguations.containsValue(aux1.toLowerCase())) {
                        resolveDesambiguacao(aux1, aux[0]);
                    } else {

                        if (wikiTitles.containsKey(aux[1].toLowerCase())
                                && isObject(wikiTitles.get(aux[1].toLowerCase()))
                                && distance(aux[0], wikiTitles.get(aux[1].toLowerCase())) < MAXDISTANCE
                                && distance(aux[0], wikiTitles.get(aux[1].toLowerCase())) != 0) 
                        {

                       //     System.out.println("Equals: " + aux[1]);
                            Writer.writeFile(mergeFile, wikiTitles.get(aux[1].toLowerCase()), aux[0] + " " + aux[1]);
                            contEquals++;

                        } else if (titleStop1 != null && wikiTitlesStop.containsKey(titleStop1.toLowerCase())
                                && isObject(wikiTitlesStop.get(titleStop1.toLowerCase()))
                                && distance(aux[0], wikiTitlesStop.get(titleStop1.toLowerCase())) < MAXDISTANCE
                                && distance(aux[0], wikiTitlesStop.get(titleStop1.toLowerCase())) != 0) 
                                {

                     //       System.out.println("StopWords: " + aux[1]);
                            Writer.writeFile(mergeFile, wikiTitlesStop.get(titleStop1.toLowerCase()), aux[0] + " " + aux[1]);
                            contStopWords++;

                        } else if (titleStem1 != null && wikiTitlesStem.containsKey(titleStem1.toLowerCase())
                                && isObject(wikiTitlesStem.get(titleStem1.toLowerCase()))
                                && distance(aux[0], wikiTitlesStem.get(titleStem1.toLowerCase())) < MAXDISTANCE
                                && distance(aux[0], wikiTitlesStem.get(titleStem1.toLowerCase())) != 0) 
                        {

                   //         System.out.println("Stemming: " + aux[1]);
                            Writer.writeFile(mergeFile, wikiTitlesStem.get(titleStem1.toLowerCase()), aux[0] + " " + aux[1]);
                            contStemming++;

                        } else if (titleStopSt != null && wikiTitlesStopStem.containsKey(titleStopSt.toLowerCase())
                                && isObject(wikiTitlesStopStem.get(titleStopSt.toLowerCase()))
                                && distance(aux[0], wikiTitlesStopStem.get(titleStopSt.toLowerCase())) < MAXDISTANCE
                                && distance(aux[0], wikiTitlesStopStem.get(titleStopSt.toLowerCase())) != 0) 
                                {

                 //           System.out.println("StopWords + Stemming: " + aux[1]);
                            Writer.writeFile(mergeFile, wikiTitlesStopStem.get(titleStopSt.toLowerCase()), aux[0] + " " + aux[1]);
                            contStemmingStopWords++;

                        }else if(citiesCoord.containsValue(aux[1]) && wikiTitles.containsKey(aux[1].toLowerCase())){
               //             System.out.println("Município: " + aux[1]);
                            Writer.writeFile(mergeFile, wikiTitles.get(aux[1].toLowerCase()), aux[0] + " " + aux[1]);
                            contMunicipio++;        
                        }else{
                            Writer.writeFile(unannotated, line);
                        }

                    }
                }
            }else{
                Writer.writeFile(unannotated, line);
            }
        }catch(Exception ex){
                System.out.println(line);
        }
        }
        input.close();


        //Para cada linha do OSM, passa por todas do wikipédia.
        System.out.println("Total com título: " + contOsmTitle);
        System.out.println("Equals: " + contEquals);
        System.out.println("Desambiguações: " + contDisambiguations);
        System.out.println("Municipios: " + contMunicipio);
        System.out.println("StopWods: " + contStopWords);
        System.out.println("Stemming: " + contStemming);
        System.out.println("StopWord + Stemming: " + contStemmingStopWords);

    }

    
    public boolean isObject(String key) {
        return objectCities.containsKey(key);
    }

    /**
     * Calculates the distance between the coordinates of the object OSM and 
     * the city mentioned on page.
     * 
     * @param coordinates coordinates of the object OMS.
     * @param id id of the Wikipédia article.
     * @return
     */
    public double distance(String coordinates, String id) throws Exception {
        double coord[] = Converter.coordParseDouble(coordinates);
        double currentDistance = 0;
        double distance = 0;
        Set<String> citiesKey = citiesCoord.keySet();
        String[] aux = objectCities.get(id).split("\\|");

        //Percorre as cidades, levando em conta que pode ter cidades com mesmo nome.
        for (String key : citiesKey) {
            String city = citiesCoord.get(key);
            for (String temp : aux) {
                if (temp.equalsIgnoreCase(city)) {
                    double coord2[] = Converter.coordParseDouble(key);
                    currentDistance = calcDistance(coord[0], coord[1], coord2[0], coord2[1]);
                    if (distance == 0 || currentDistance < distance) {
                        distance = currentDistance;
                    }
                }
            }
        }
        return distance;
    }

    
    /**
     * Fills the hash maps, with data of the files.
     * 
     * @throws IOException
     */
    public void fillsHash() throws IOException {
        Scanner input = new Scanner(desambiguadasFile, "ISO-8859-1");

        while (input.hasNext()) {
            String[] aux = WikipediaFiles.separateWiki(input.nextLine());
        
            desambiguadas.put(aux[0], aux[1]);
        }
        input.close();
        input = new Scanner(titleStop, "ISO-8859-1");

        while (input.hasNext()) {
            String[] aux = WikipediaFiles.separateWiki(input.nextLine());
            
            if(aux!=null){                            
        
                wikiTitlesStop.put(aux[1], aux[0]);
            }
        }        
        
        input.close();

        input = new Scanner(titleSDFile, "ISO-8859-1");

        while (input.hasNext()) {
            String[] aux = WikipediaFiles.separateWiki(input.nextLine());
            
            if (aux != null) {                
        
                wikiTitles.put(aux[1], aux[0]);
            }
        }
        input.close();

        input = new Scanner(titleStem, "ISO-8859-1");
        
        while (input.hasNext()) {
            
            String[] aux = WikipediaFiles.separateWiki(input.nextLine());
            if(aux!=null){                        
                wikiTitlesStem.put(aux[1], aux[0]);
            }
        }
        input.close();

        input = new Scanner(titleStemStop, "ISO-8859-1");

        while (input.hasNext()) {
            String[] aux = WikipediaFiles.separateWiki(input.nextLine());
            if(aux!=null){        
                wikiTitlesStopStem.put(aux[1], aux[0]);
            }
        }
        input.close();


        input = new Scanner(desambiguationFile, "ISO-8859-1");

        while (input.hasNext()) {            
            String[] aux = WikipediaFiles.separateWiki(input.nextLine());        
            desambiguations.put(aux[0], aux[1].toLowerCase());
        }

        input.close();
        String[] aux;
  
        input = new Scanner(citiesFile, "ISO-8859-1");

        while (input.hasNext()) {
            aux = separateCities(input.nextLine());        
            citiesCoord.put(aux[1], aux[0]);
        }
        input.close();        
        input = new Scanner(desambiguationCitiesFile, "ISO-8859-1");

        while (input.hasNext()) {
            aux = WikipediaFiles.separateWiki(input.nextLine());            
            objectCities.put(aux[0], aux[1]);
        }
        input.close();
        
    }

    /**
     * Calculates the distance between two object.
     * 
     * @param lat1 latitude of the first object
     * @param lon1 longitude of the first object
     * @param lat2 latitude of the second object
     * @param lon2 longitude of the second object
     * @return distance in meters
     */
    public static double calcDistance(double lat1, double lon1, double lat2, double lon2) {
        return SpatialUtils.haversineDistance(lat1, lon1, lat2, lon2);
    }

    

    /**
     * Separate line containing the id of the page and the cities cited on page.
     * @param line line of file.
     * @return id and cities cited on page.
     */
    public static String[] separateCities(String line) {
        String[] aux = {"", ""};
        int x = line.indexOf("|");
        aux[0] = line.substring(0, x);
        x = x + 2;
        aux[1] = line.substring(x, line.length());
        return aux;
    }
            
    
    /**
     * Separate the line of the OSM file in coordinates and name.
     * 
     * @param line line of OSM file
     * @return coordinates and object name
     */
    public static String[] separateOsm(String line) {
        int x = 0, y = 0;
        String aux[] = {"", ""};
        x = line.indexOf(" ") + 1;
        x = line.indexOf(" ", x) + 1;
        y = line.indexOf(" ", x);
        y = line.indexOf(" ", y + 1);
        aux[0] = line.substring(x, y);
        if (line.indexOf("|name") != -1) {
            x = line.indexOf("|name");
            y = line.indexOf("=", x) + 1;
            if (line.indexOf("|", y) != -1) {
                aux[1] = line.substring(y, line.indexOf("|", y));
                return aux;
            } else {
                aux[1] = line.substring(y, line.length());
                return aux;
            }
        } else if (line.indexOf("|operator") != -1) {
            x = line.indexOf("|operator");
            y = line.indexOf("=", x) + 1;
            if (line.indexOf("|", y) != -1) {
                aux[1] = line.substring(y, line.indexOf("|", y));
                return aux;
            } else {
                aux[1] = line.substring(y, line.length());
                return aux;
            }
        }
        return null;
    }

    

    private void resolveDesambiguacao(String titulo, String coordinates) throws SAXException, Exception {
        double coord[] = Converter.coordParseDouble(coordinates);
        Set<String> wikiKeys = desambiguadas.keySet();
        double currentDistance = 0;

        Set<String> citiesKey = citiesCoord.keySet();
        double menorDistancia = 0;
        String idMenorDistancia = "";

        //percorre os titulos do wikipedia.
        for (String x : wikiKeys) {

            String value = desambiguadas.get(x);
            //Quando achar, procura no arquivo com as cidades.
            if (value.equalsIgnoreCase(titulo)) {
                if (objectCities.containsKey(x)) {
                    //aux[0] = id. aux[1] = cidade
                    String[] aux = objectCities.get(x).split("\\|");
                    //Percorre as cidades, levando em conta que pode ter cidades com mesmo nome.
                    for (String key : citiesKey) {
                        String city = citiesCoord.get(key);
                        for (String tmp : aux) {
                            if (tmp.equalsIgnoreCase(city)) {
                                double coord2[] = Converter.coordParseDouble(key);
                                //Calcula distância.
                                currentDistance = calcDistance(coord[0], coord[1], coord2[0], coord2[1]);
                                if (currentDistance < menorDistancia || menorDistancia == 0) {
                                    menorDistancia = currentDistance;
                                    idMenorDistancia = x;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (menorDistancia > 0 && menorDistancia < MAXDISTANCE) {
            contDisambiguations++;
            //System.out.println("Desambiguação: " + titulo);
            Writer.writeFile(mergeFile, idMenorDistancia, coordinates + " " + titulo);
        }
    }

    
}