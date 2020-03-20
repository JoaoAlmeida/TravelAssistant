package wikipedia.util;

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
import util.Writer;
import wikipedia.Anotacao;

/**
 *
 * @author fellipe
 */
public class NamesUtils {

    private File objectCitiesFile;
    private  File wikiTitlesFile;
    private HashSet<String> stopWords = new HashSet<>();
    private  File StopWords;
    private  File Stemming;
    private  File StemmingStop;
    private  File redirectsFile;
    private Stemmer st;
    private  File pagesWDFile;
    private  File desambiguationFile;
    private  File paginasDesambiguadasFile;
    private  File citiesCoordFile;
    private HashMap<String, String> citiesCoord = new HashMap<>();
    private HashMap<String, String> desambiguations = new HashMap<>();
    private HashMap<String, String> desambiguates = new HashMap<>();
    private HashMap<String, String> pagesSD = new HashMap<>();
    private HashMap<String, String> redirectPages = new HashMap<>();

    public NamesUtils() throws PTStemmerException, FileNotFoundException, IOException, SAXException {
        paginasDesambiguadasFile = new File("arquivosWikipédia/disambiguates.txt");
        redirectsFile = new File("arquivosWikipédia/redirect.txt");
        desambiguationFile = new File("arquivosWikipédia/disambiguations.txt");
        pagesWDFile = new File("arquivosWikipédia/titles_without_disambiguations.txt");
        wikiTitlesFile = new File("arquivosWikipédia/full_title.txt");
        StopWords = new File("arquivosWikipédia/title_stopWords.txt");
        Stemming = new File("arquivosWikipédia/title_Stemming.txt");
        StemmingStop = new File("arquivosWikipédia/title_stopWordsStemming.txt");
        citiesCoordFile = new File("arquivosWikipédia/cities_coordinates.txt");
        objectCitiesFile = new File("arquivosWikipédia/object_cities.txt");
        st = new OrengoStemmer();
        st.enableCaching(1000);
        loadStopWords();
    }

    /**
     * Usado para iniciar e fazer apenas stopword e stemming.
     * @param enableCaching
     * @throws PTStemmerException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SAXException
     */
    public NamesUtils(int enableCaching) throws PTStemmerException, FileNotFoundException, IOException, SAXException{
        st = new OrengoStemmer();
        st.enableCaching(enableCaching);
        loadStopWords();
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException, PTStemmerException, SAXException {
        NamesUtils t = new NamesUtils();
        t.loadDesambiguationPages();
        t.generateDesambiguatesPages();
        t.loadCitiesCoord();
        t.loadDesambiguatesPages();
        t.loadPagesSD();
        t.loadRedirectPages();
        t.generateObjectCitiesText();
        t.mergeRedirects();
        t.generateStopWordAndStemmingFiles();
    }

    /**
     * Load the hash map with the cities and its coordinates.
     *
     * @throws FileNotFoundException
     */
    public void loadCitiesCoord() throws FileNotFoundException {
        Scanner input = new Scanner(citiesCoordFile);

        while (input.hasNext()) {
            String[] aux = Anotacao.separateCities(input.nextLine());                
            citiesCoord.put(aux[1], aux[0]);
        }
        input.close();
    }

    /**
     * Load the StopList to HashMap.
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SAXException
     */
    public void loadStopWords() throws FileNotFoundException, IOException, SAXException {
        try (Scanner input = new Scanner(new File("stoplistPt.txt"))) {
            while (input.hasNextLine()) {
                String aux = input.nextLine();
                if (!"".equals(aux) && !aux.contains("*")) {
                    stopWords.add(aux);
                }
            }
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * Load pages without desabiguations.
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SAXException
     */
    public void loadPagesSD() throws FileNotFoundException, IOException, SAXException {
        try{Scanner input = new Scanner(pagesWDFile);
        while (input.hasNextLine()) {
            String[] aux = WikipediaFiles.separateWiki(input.nextLine());
            if (aux != null) {                
                pagesSD.put(aux[1], aux[0]);
            }
        }
        input.close();
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
        
        
    }

    /**
     * Load redirect pages.
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SAXException
     */
    public void loadRedirectPages() throws FileNotFoundException, IOException, SAXException {
        Scanner input = new Scanner(redirectsFile);
        while (input.hasNextLine()) {
            String[] aux = separateRedirectLine(input.nextLine());
            if (aux != null) {
                redirectPages.put(aux[0], aux[1]);
            }
        }
        input.close();

    }

    /**
     * Load desambiguation pages. Load for HashMap.
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SAXException
     */
    public void loadDesambiguationPages() throws FileNotFoundException, IOException, SAXException {
        Scanner input = new Scanner(desambiguationFile);;

        while (input.hasNext()) {
            String[] aux = WikipediaFiles.separateWiki(input.nextLine());
            desambiguations.put(aux[0], aux[1]);
        }
        input.close();
    }

    /**
     * Load file with articles which are desambiguations.
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SAXException
     */
    public void loadDesambiguatesPages() throws FileNotFoundException, IOException, SAXException {
        Scanner input = new Scanner(paginasDesambiguadasFile);;

        while (input.hasNext()) {

            String[] aux = WikipediaFiles.separateWiki(input.nextLine());
            desambiguates.put(aux[0], aux[1]);
        }
        input.close();
    }

    /**
     * Removes the StopWords the phrase.
     *
     * @param phrase to be removed the StopWords
     * @return phrase without StopWords
     */
    public String removeStopWords(String phrase) {
        String[] spt1 = phrase.split(" ");
        StringBuilder aux1 = new StringBuilder();
        for (String aux : spt1) {
            if (!aux.equals("") && !stopWords.contains(aux.toLowerCase())) {
                aux1.append(aux).append(" ");
            }
        }
        if (aux1.length() < 2) {
            return null;
        }
        aux1.deleteCharAt(aux1.length() - 1);
        return aux1.toString();
    }

    /**
     * Separate the line of redirect file.
     *
     * @param line line with title and redirect title.
     * @return title and redirect title
     */
    public static String[] separateRedirectLine(String line) {
        String aux[] = {"", ""};
        if (line.contains("|")) {
            aux[0] = line.substring(0, line.indexOf("|")).toLowerCase();
            aux[1] = line.substring(line.indexOf("|") + 2, line.length()).toLowerCase();
            return aux;
        }
        return null;
    }

    /**
     * Applies the Stemming technique in the phrase.
     *
     * @param text phrase
     * @return phrase after Stemming process
     * @throws PTStemmerException
     */
    public String stemming(String text) throws PTStemmerException {

        StringBuilder t1 = new StringBuilder();
        String aux1[] = st.getPhraseStems(text);

        for (String x : aux1) {
            t1.append(x).append(" ");
        }
        if (t1.length() < 2) {
            return null;
        }
        t1.deleteCharAt(t1.length() - 1);
        return t1.toString();
    }

    /**
     * Adds titles of the redirect on the titles file. Maintains the id's of
     * pages origins (title of article of the Wikipédia).
     *
     * @throws SAXException
     */
    public void mergeRedirects() throws SAXException, FileNotFoundException, IOException {
        Set<String> s = redirectPages.keySet();
        System.out.println("Adicionando paginas de redirecionamento...");
        int cont =0;
        for (String x : s) {
            String value = redirectPages.get(x);
            System.out.println(++cont);
            if (pagesSD.containsKey(value) && !desambiguates.containsValue(x)) {
                Writer.writeFile(pagesWDFile, pagesSD.get(value), x);
            }
        }
    }

    /**
     * Search by cities in the text.
     *
     * @param text page of the Wikipédia
     * @return Cities found, separetes for "|"
     */
    public String findCity(String text) {
        String cities = "";
        if (text.length() > 500) {
            text = text.substring(0, 499);
        } else {
            text = text.substring(0, text.length());
        }

        Set<String> keys = citiesCoord.keySet();
        for (String value : keys) {
            String city = citiesCoord.get(value);


            if ((text.contains(" " + city + " ") || text.contains("(" + city + ")")
                    || text.contains(" " + city + ",") || text.contains(" " + city + ".")) && !text.contains("'''" + city)
                    && !text.contains(city + "'''")) {

                cities += city + "|";
            }
        }
        return cities;
    }

    /**
     * Generate the file with the cities found in the 500 first caracters of
     * pages.
     *
     * @throws FileNotFoundException
     * @throws SAXException
     */
    public void generateObjectCitiesText() throws FileNotFoundException, SAXException, IOException {
        Scanner input = new Scanner(new File("arquivosWikipédia/full_text.txt"));
        System.out.println("Gerando arquivo com as cidades dos objetos...");
        int cont = 0;
        String[] aux;
        String cities;
        while (input.hasNext()) {                        
            System.out.println(cont++);
            String line = input.nextLine();
            aux = WikipediaFiles.separateWiki(line);                        
            if (aux != null) {                            
                cities = findCity(aux[1]);
                if (!cities.equals("")) {
                    Writer.writeFile(objectCitiesFile, aux[0], cities);
                }
            }
        }
    }

    /**
     * Search for cities in the links of the pages.
     *
     * @throws IOException
     * @throws SAXException
     */
    public void generateObjectCitiesEntities() throws IOException, SAXException {
        Scanner input = new Scanner(new File("arquivosWikipédia/full_entities.txt"));
        String[] aux;
        int cont = 0;
        String cities = "";
        while (input.hasNext()) {            
            
            aux = WikipediaFiles.separateWiki(input.nextLine());
            String[] links = aux[1].split("\\|", 10);
            for (String link : links) {
                if (isCity(link)) {
                    cities += link + "|";
                }
            }
            if (!"".equals(cities)) {
                Writer.writeFile(objectCitiesFile, aux[0], cities);
                cities = "";
            }
        }

        input.close();
    }

    /**
     * Cheks if the word is a Brasil city.
     *
     * @param word word to be cheked
     * @return true, if the word is a Brasil city. False, if not.
     */
    public boolean isCity(String word) {
        return citiesCoord.containsValue(word);
    }

    /**
     * Generate the file with the articles desambiguates.
     *
     * @throws SAXException
     * @throws FileNotFoundException
     */
    public void generateDesambiguatesPages() throws SAXException, FileNotFoundException, IOException {
        Scanner input = new Scanner(wikiTitlesFile);
        System.out.println("Gerando paginas desambiguadas e sem desambiguações...");
        int cont =0;
        while (input.hasNextLine()) {            
            //title and id
            String[] x = WikipediaFiles.separateWiki(input.nextLine());
            if (x != null) {
                
                //System.out.println(cont++);
                if (desambiguations.containsValue(x[1])) {
                    Writer.writeFile(paginasDesambiguadasFile, x[0], x[1].toLowerCase());
                } else {
                    Writer.writeFile(pagesWDFile, x[0], x[1].toLowerCase());
                }
            }
        }

    }

    /**
     * Generate a file with titles after application of the Stemming and
     * StopWords.
     *
     * @throws FileNotFoundException
     * @throws SAXException
     * @throws PTStemmerException
     */
    public void generateStopWordAndStemmingFiles() throws FileNotFoundException, SAXException, PTStemmerException, IOException {
        Scanner input = new Scanner(wikiTitlesFile);
        System.out.println("Aplicando StopWord e Stemming nos títulos...");
        int cont =0;
        while (input.hasNextLine()) {
            System.out.println(++cont);
            //title and id
            String[] x = WikipediaFiles.separateWiki(input.nextLine());
            if (x != null) {
                Writer.writeFile(pagesWDFile, x[0], x[1].toLowerCase());
                if (desambiguations.containsValue(x[1])) {
                } else {

                    Writer.writeFile(StopWords, x[0], removeStopWords(x[1].toLowerCase()));
                    String stm = stemming(x[1]);
                    if (stm != null) {
                        Writer.writeFile(Stemming, x[0], stm.toLowerCase());
                    }
                    String stopStemming = StemmingStopWords(x[1]);
                    if (stopStemming != null) {
                        Writer.writeFile(StemmingStop, x[0], stopStemming.toLowerCase());
                    }
                }
            }
        }
        input.close();
    }

    /**
     * Removes StopWords and applies the Stemming process.
     *
     * @param text1 phrase
     * @return phrase after the process
     * @throws PTStemmerException
     */
    public String StemmingStopWords(String text1) throws PTStemmerException {
        String[] spt1 = text1.toLowerCase().split(" ");
        StringBuilder aux1 = new StringBuilder();

        for (String aux : spt1) {
            if (!aux.equals("") && !stopWords.contains(aux)) {
                aux1.append(aux).append(" ");
            }
        }
        if (aux1.length() < 2) {
            return null;
        }
        aux1.deleteCharAt(aux1.length() - 1);
        spt1 = st.getPhraseStems(aux1.toString());
        aux1.delete(0, aux1.length());
        for (String aux : spt1) {
            aux1.append(aux).append(" ");
        }
        if (aux1.length() < 2) {
            return null;
        }
        aux1.deleteCharAt(aux1.length() - 1);

        return aux1.toString();
    }
}
