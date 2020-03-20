/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import wikipedia.util.WikipediaFiles;
import datasets.Category;
import datasets.Osm;
import framework.SpatialObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import org.xml.sax.SAXException;
import stemmer.PTStemmerException;

/**
 *
 * @author fellipe
 */
public class MergeWikiOsm {

    private long id = 1;
    private HashMap<String, SpatialObject> objects;
    private HashMap<String, SpatialObject> objectsUnannotateds;
    private File pertaFileText;
    private File pertaFullFile;
    private File pertaFile;
    public MergeWikiOsm() throws UnsupportedEncodingException, FileNotFoundException {
        objects = new HashMap<String, SpatialObject>();
        objectsUnannotateds = new HashMap<String, SpatialObject>();
        File dir = new File("/home/fellipe/NetBeansProjects/TravelAssistant/files");
        dir.mkdirs();
        pertaFile = new File("/home/fellipe/NetBeansProjects/TravelAssistant/files/perta.txt");
        pertaFileText = new File("/home/fellipe/NetBeansProjects/TravelAssistant/files/Text.txt");
        pertaFullFile = new File("/home/fellipe/NetBeansProjects/TravelAssistant/files/Full.txt");
    }

    public static void main(String[] args) throws FileNotFoundException, SAXException, PTStemmerException, IOException {
        MergeWikiOsm wo = new MergeWikiOsm();
        wo.loadObjects();
        wo.addCategories();
        wo.addText();
        wo.mergeObjectsWikiOsm();
    }

    public void loadObjects() throws FileNotFoundException, PTStemmerException, IOException, SAXException {
        Scanner input = new Scanner(new File("merge_osm/wiki-osm.txt"));
        System.out.println("Carregando objetos OSM...");

        while (input.hasNext()) {
            String line = input.nextLine();
            String aux[] = separateFullFile(line);
            double lat = Double.parseDouble(aux[1]);
            double lgt = Double.parseDouble(aux[2]);
            objects.put(aux[1] + " " + aux[2], new SpatialObject(id, aux[0], "-", lat, lgt, aux[3], "-", "", "-"));
            id++;
        }
        input = new Scanner(new File("merge_osm/n�o_anotados.txt"));
        while (input.hasNext()) {
            String line = input.nextLine();
            
            String idOsm = Osm.searchId(line);
            String[] coord = Osm.searchCoord(line);
            double lat = Double.parseDouble(coord[0]);
            double lgt = Double.parseDouble(coord[1]);
//            String[] aux = Osm.searchCategory(line); //remover antes de usar e resolver o erro
            String category = null;
//            if(aux != null){
//                category = "(" + aux[0] + ")" + " (" + aux[1] + ")";
//                category = category.replaceAll("_", " ");
//            }
            String name = Osm.searchName(line);
            if (category == null) {
                category = "-";
            }
            if (name == null) {
                name = "-";
            }
            objectsUnannotateds.put(coord[0] + " " + coord[1], new SpatialObject(id, "-", idOsm, lat, lgt, name, category, "", "-"));
            id++;
        }
        input.close();

    }

    /**
     * Separa linha do arquivo com objetos anotados.
     *
     * @return
     */
    public String[] separateFullFile(String line) {
        String[] aux = {"", "", "", "", ""};
        int x, y;
        x = line.indexOf(" ");
        aux[0] = line.substring(0, x);
        x++;
        y = line.indexOf(" ", x);
        aux[1] = line.substring(x, y);
        x = y + 1;
        y = line.indexOf(" ", x);
        aux[2] = line.substring(x, y);
        y++;
        aux[3] = line.substring(y, line.length());
        return aux;
    }

    /**
     * Adiciona categorias ? objetos do OSM
     */
    public void addCategories() throws FileNotFoundException {
        System.out.println("Adicionando categorias...");
        Scanner input = new Scanner(new File("xmls/fsa/object.txt"));

        String[] coord = null;
        String idOsm = null;
        while (input.hasNext()) {
            
            String line = input.nextLine();
            String cat = null;
            try{
            coord = Osm.searchCoord(line);
            idOsm = Osm.searchId(line);
            if (line.contains("tourism=")) {
                cat = "(tourism) " + "(" + TextClean.clean(Category.findCategory(line, "tourism")) + ")";
            } else if (line.contains("natural")) {
                cat = "(natural) " + "(" + TextClean.clean(Category.findCategory(line, "natural")) + ")";
            } else if (line.contains("shop")) {
                cat = "(shop) " + "(" + TextClean.clean(Category.findCategory(line, "shop")) + ")";
            } else if (line.contains("amenity")) {
                cat = "(amenity) " + "(" + TextClean.clean(Category.findAmenity(line)) + ")";
            }

            SpatialObject aux = objects.get(coord[0] + " " + coord[1]);
            if (aux != null) {
                aux.setIdOsm(idOsm);
            }


            if (aux!= null && cat != null && !cat.contains("(null)")) {
                cat = cat.replaceAll("_", " ");
                if (cat.contains("/")) {
                    cat = cat.substring(0, cat.indexOf("/"));
                }
                   aux.setCategory(cat);
            }
            }catch(Exception ex){
                System.out.println(line);
            }
        }
        input.close();
    }

    public void mergeObjectsWikiOsm() throws FileNotFoundException, SAXException, UnsupportedEncodingException, IOException {
        Scanner input = new Scanner(new File("arquivosWikip�dia/text.txt"));
        System.out.println("Criando objetos do Wikip�dia...");
        HashMap<String, SpatialObject> objectsWiki = new HashMap<>();

        while (input.hasNext()) {
            String line = input.nextLine();
            
            try {
                double[] coord = WikipediaFiles.findCoordinates(line);
                if (coord != null) {
                    String[] wiki = WikipediaFiles.separateTextFile(line);
                    SpatialObject obj = new SpatialObject(id, wiki[0], "-", coord[0], coord[1], "-", "-", "", wiki[3]);
                    objectsWiki.put(wiki[0], obj);
                    id++;
                }
            } catch (Exception e) {
            }
        }
        input.close();
        System.out.println("Adicionando nomes aos objetos...");
        input = new Scanner(new File("arquivosWikip�dia/title.txt"), "ISO-8859-1");
        while (input.hasNext()) {
            String line = input.nextLine();            
            String aux[] = WikipediaFiles.separateWiki(line);
            SpatialObject obj = objectsWiki.get(aux[0]);
            if (obj != null) {
                obj.setName(aux[1]);
            }
        }
        int cont = 1;
        input.close();
        System.out.println("Juntando objetos...");
        HashSet<SpatialObject> setObject = new HashSet<>();
        Set<String> set = objects.keySet();
        for (String key : set) {
            SpatialObject obj = objects.get(key);
            setObject.add(obj);
        }

        set = objectsWiki.keySet();
        for (String key : set) {
            SpatialObject obj = objectsWiki.get(key);
            setObject.add(obj);
        }

        set = objectsUnannotateds.keySet();
        for (String key : set) {
            SpatialObject obj = objectsUnannotateds.get(key);
            setObject.add(obj);

        }

        System.out.println("Escrevendo nos arquivos...");
        Writer.writeFile(pertaFullFile, "# id    id_wikip�dia  id_osm  lat       lgt     (category) (subcategory) title");
        Writer.writeFile(pertaFileText, "# id         text");
        Iterator it = setObject.iterator();
        while (it.hasNext()) {
            SpatialObject ob = (SpatialObject) it.next();
            
            if (!ob.getName().equals("-") || !ob.getCategory().equals("-")) {
                if(ob.getId() == 928347){
                    System.out.println("pause!");
                }
                Writer.writeFile(pertaFullFile, ob.getId() + " " + ob.getIdWiki() + " "
                        + ob.getIdOsm() + " " + ob.getLatitude() + " "
                        + ob.getLongitude() + " " + ob.getCategory() + " " +  ob.getName());
                Writer.writeFile(pertaFile, ob.getId() + " " + ob.getLatitude() + " "
                        + ob.getLongitude() + " " + ob.getCategory() + " "
                        +  ob.getName() + " "  + ob.getText());
            }

            if (!ob.getText().equals("-")) {
                Writer.writeFile(pertaFileText, ob.getId() + " " + ob.getText());
                 
            }
        }

    }

    public void loadWikiObjects() {
    }

    public void addText() throws FileNotFoundException {
        HashMap<String, SpatialObject> hashId = new HashMap<>();
        System.out.println("Adicionanto texto...");
        Set<String> set = objects.keySet();
        for (String key : set) {
            SpatialObject aux = objects.get(key);
            hashId.put(aux.getIdWiki(), aux);
        }
        Scanner input = new Scanner(new File("arquivosWikip�dia/full_text.txt"));
        while (input.hasNext()) {
            String line = input.nextLine();
            
            String[] wiki = WikipediaFiles.separateWiki(line);
            if (wiki != null) {
                SpatialObject aux = hashId.get(wiki[0]);
                if (aux != null) {
                    aux.setText(wiki[1]);
                }
            }
        }
        input.close();
    }
}