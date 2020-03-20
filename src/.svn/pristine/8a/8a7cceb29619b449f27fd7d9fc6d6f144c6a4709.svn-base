/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import org.xml.sax.SAXException;
import wikipedia.util.WikipediaFiles;
import util.Writer;
import wikipedia.util.Converter;

/**
 *
 * @author fellipe
 */
public class SearchObject {

    private final File textWiki;
    private final File titleWikiFile;
    private final File osm;
    private final File merge;
    private HashMap<String, String> objectWiki = new HashMap<>();
    private HashSet<String> objectOSM = new HashSet<>();
    private HashMap<String, String> titlesWiki = new HashMap<>();
    public SearchObject() {
        File dir = new File("merge_wiki");
        dir.mkdir();
        textWiki = new File("arquivosWikipédia/text.txt");
        titleWikiFile = new File("arquivosWikipédia/title.txt");
        merge = new File(dir,"merge.txt");        
        osm = new File("Brasil/object.txt");
    }

    public static void main(String[] args) throws FileNotFoundException, SAXException, Exception {
        SearchObject so = new SearchObject();
        so.loadHashOsm();
        so.loadObjectWiki();
        so.mergeObject();
    }

    
    public void mergeObject() throws SAXException, Exception{
        Set<String> keysWiki = objectWiki.keySet();
        int i =0;
        for(String coordWiki: keysWiki){
            System.out.println(++i);
            double coordWikiDouble[] = Converter.coordParseDouble(coordWiki);
            for (Iterator<String> it = objectOSM.iterator(); it.hasNext();) {
                String coordOsm = it.next();
                double coordOsmDouble[] = Converter.coordParseDouble(coordOsm);
                if(Anotacao.calcDistance(coordWikiDouble[0], coordWikiDouble[1], 
                        coordOsmDouble[0], coordOsmDouble[1]) < 30){
                    Writer.writeFile(merge, objectWiki.get(coordWiki), coordOsm);
                }
            }
        }
    }
    
    public void loadHashOsm() throws FileNotFoundException {
        Scanner input = new Scanner(osm, "ISO-8859-1");

        while (input.hasNext()) {
            String aux = separateOsm(input.nextLine());
            if (aux != null) {
                objectOSM.add(aux);
            }
        }
        input.close();
    }
    
    public void loadTitlesWiki() throws FileNotFoundException{
        
        Scanner input = new Scanner(titleWikiFile, "ISO-8859-1");

        while (input.hasNext()) {
            String[] aux = WikipediaFiles.separateWiki(input.nextLine());
            if (aux != null) {
                titlesWiki.put(aux[0], aux[1]);
            }
        }
        input.close();
    }

    public void loadObjectWiki() throws FileNotFoundException{
        Scanner input = new Scanner(textWiki, "ISO-8859-1");

        while (input.hasNext()) {
            String[] aux = separateCoordWiki(input.nextLine());            
                objectWiki.put(aux[1], aux[0]);
            
        }
        input.close();
    }
    
    public String separateOsm(String line) {
        if (!line.contains("|name") && !line.contains("|operator")) {
            int x = 0, y = 0;
            x = line.indexOf(" ") + 1;
            x = line.indexOf(" ", x) + 1;
            y = line.indexOf(" ", x);
            y = line.indexOf(" ", y + 1);

            return line.substring(x, y);
        }

        return null;
    }

    /**
     * Return the id and coordinate of page.
     *
     * @param line
     * @return
     */
    public String[] separateCoordWiki(String line) {
        int x = 0, y = 0;
        String[] aux = {"", ""};
        x = line.indexOf(" ");
        aux[0] = line.substring(0, x);
        y = line.indexOf(" ", x + 1) + 1;
        y = line.indexOf(" ", y);
        aux[1] = line.substring(x + 1, y);

        return aux;
    }
}
