package wikipedia;

import java.io.*;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import util.EncoderUtil;
import util.LeitorWikiXML;
import util.ReadPage;
import wikipedia.util.Converter;
import util.Writer;

/**
 * This class makes parser in the pages of Wikipedia, in order to find pages
 * with geographic coordinates. This class also converts these coordinates in
 * decimal format and saves in a file the pages that contains the coordinates.
 *
 * Example of use:
 *
 * ~$ java ConverteCoordenadas args1 args2 args3 args4 args5
 *
 * args1: file in the xml format directly from wikipedia. arqs2: path to the
 * creation of the file containing the coordinates and text. arqs3: path to the
 * creation of the file containing the languages. args4: path to the creation of
 * the file containing the categories of pages. args5: path to the creation of
 * the file containing the title of pages. args6: path to the creation of the
 * file containing the hyperlinks.
 *
 * @author <Fellipe>
 */
public class ContentExtractor extends DefaultHandler {

    private StringBuffer textBuffer = null;
    private int coordinatesCount = 0;
    private String page = null;
    private final File fileTextCoordinates;
    private final File fileLanguages;
    private final File fileCategories;
    private final File fileTitle;
    private final File fileEntities;
    private final File fileFullLanguages;
    private final File fileFullTitle;
    private final File fileFullText;
    private final File fileRedirects;
    private final File fileFullEntities;
    private final File fileImg;
    private final File descartedFileText;
    private final File descartedFileLanguages;
    private final File descartedFileTitle;
    private final File descartedFileEntities;
    private final File desambiguationFile;
    private boolean segueTexto;
    private String title;
    private String id;
    private long beginning;
    private int counter;
    private String redir = new String();
    private int contPage = 0;
    private int contDesambiguation1 = 0;
    private int contDesambiguation2 = 0;
    private long time = System.currentTimeMillis();
    private static final long STATUS_TIME = 60 * 1000;

    public ContentExtractor() {

        File dir = new File("/home/thiagolima/arquivosWikipédia");
        dir.mkdirs();
        this.fileRedirects = new File(dir, "redirect.txt");
        this.fileTextCoordinates = new File(dir, "text.txt");
        this.fileLanguages = new File(dir, "languages.txt");
        this.fileCategories = new File(dir, "categories.txt");
        this.fileTitle = new File(dir, "title.txt");
        this.fileEntities = new File(dir, "entities.txt");
        this.fileFullLanguages = new File(dir, "full_languages.txt");
        this.fileFullTitle = new File(dir, "full_title.txt");
        this.fileFullText = new File(dir, "full_text.txt");
        this.fileFullEntities = new File(dir, "full_entities.txt");
        this.descartedFileText = new File(dir, "descarted_text.txt");
        this.descartedFileLanguages = new File(dir, "descarted_languages.txt");
        this.descartedFileTitle = new File(dir, "descarted_title.txt");
        this.descartedFileEntities = new File(dir, "descarted_entities.txt");
        this.desambiguationFile = new File(dir, "disambiguations.txt");
        this.fileImg = new File(dir, "imagens.txt");

        id = new String();
        title = new String();
    }

    @Override
    public void startDocument() throws SAXException {
        beginning = System.currentTimeMillis();

        System.out.println("startDocument");

        try {
            fileTextCoordinates.createNewFile();
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {

        //page = "";
        if (eName(sName, qName).equals("title")) {
            segueTexto = true;
            counter = 1;

        } else if (eName(sName, qName).equals("id") && id.equals("")) {
            segueTexto = true;
            counter = 2;
        } else {
            counter = 3;
            segueTexto = false;
        }

        if (eName(sName, qName).equals("text")) {
            page = "";
            segueTexto = true;
        } else if (eName(sName, qName).equals("redirect")) {
            if (attrs != null) {

                redir = attrs.getValue(0);

                if (redir.toString().contains("(")) {
                    if (redir.indexOf("(") > 2) {
                        redir = redir.substring(0, redir.indexOf("(") - 1);
                    }
                }


            }
        }

    }

    @Override
    public void characters(char buf[], int offset, int len) throws SAXException {
        if (textBuffer == null) {
            textBuffer = new StringBuffer();
        }
        if (segueTexto) {
            textBuffer.append(buf, offset, len);

            if (counter == 1) {
                title = textBuffer.toString();
                counter = 3;
            } else if (counter == 2) {
                id = textBuffer.toString();
                counter = 3;

            }
        }
        //Adicionar {{CoorHeader.
    }

    /**
     * Search the coordinates of the type "{{geoocoordenadas|".
     *
     * @param page page that the search will be performed.
     * @return coordinates.
     * @throws SAXException
     */
    public String[][] geocoordenadas(String page) throws SAXException {
        int cont1 = 0;
        int cont = 0, cont2 = 0;
        String aux;
        int x;
        int y;
        boolean proceed = true;
        String[][] coordinates = new String[2][4];

        x = page.toLowerCase().indexOf("geocoordenadas", 0);
        x = page.indexOf("|", x) + 1;

        if (checkDistance(page, "_", x, 10)) {
            y = page.indexOf("_", x);
        } else {
            return null;
        }

        while (proceed) {
            aux = page.substring(x, y);
            if (checkDirection(aux)) {

                if (cont1 == 1) {
                    coordinates[1][3] = aux;
                    return coordinates;
                }
                coordinates[0][3] = aux;
                cont = 0;
                cont1++;
            } else if (cont == 0) {

                if (cont2 == 0) {
                    coordinates[0][0] = aux;
                    cont2 = 1;
                } else {
                    coordinates[1][0] = aux;
                    cont2 = 2;
                }
                cont++;
            } else {

                if (cont == 1) {
                    if (cont2 == 1) {
                        coordinates[0][1] = aux;
                    } else {
                        coordinates[1][1] = aux;
                    }

                } else {
                    if (cont2 == 1) {
                        coordinates[0][2] = aux;
                    } else {
                        coordinates[1][2] = aux;
                    }
                }
                cont++;
            }
            x = y + 1;
            if (checkDistance(page, "_", x, 10) && (page.indexOf("_", x) < page.indexOf("}", x))) {
                y = page.indexOf("_", x);
            } else {
                if (checkDistance(page, "|", x, 10) && (page.indexOf("|", x) < page.indexOf("}", x))) {
                    y = page.indexOf("|", x);
                    aux = page.substring(x, y);
                    proceed = false;
                    coordinates[1][3] = aux;
                } else {
                    return null;
                }
            }
        }
        return coordinates;
    }

    /**
     * Search the coordinates of the type "{{coord|" and "{{coor dms|".
     *
     * @param page page that the search will be performed.
     * @return coordinates.
     * @throws SAXException
     */
    public String[][] coord(String page) throws SAXException {
        int cont1 = 0, cont2 = 0;
        int cont = 0;
        String aux;
        int x;
        int y;
        boolean proceed = true;
        String[][] coordinates = new String[2][4];

        x = page.toLowerCase().indexOf("{{coor", 0);
        x = page.indexOf("|", x) + 1;

        if (checkDistance(page, "|", x, 10)) {
            y = page.indexOf("|", x);
        } else {
            if (page.indexOf("|", x) != -1) {
                x = page.indexOf("|", x) + 1;
                if (checkDistance(page, "|", x, 10)) {
                    y = page.indexOf("|", x);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        while (proceed) {

            aux = page.substring(x, y);
            if (checkDirection(aux)) {
                if (cont1 == 1) {
                    coordinates[1][3] = aux;
                    return coordinates;
                }
                coordinates[0][3] = aux;
                cont = 0;
                cont1++;
            } else if (cont == 0) {
                if (cont2 == 0) {
                    coordinates[0][0] = aux;
                    cont2 = 1;
                } else {
                    coordinates[1][0] = aux;
                    cont2 = 2;
                }
                cont++;
            } else {
                if (cont == 1) {
                    if (cont2 == 1) {
                        coordinates[0][1] = aux;
                    } else {
                        coordinates[1][1] = aux;
                    }

                } else {
                    if (cont2 == 1) {
                        coordinates[0][2] = aux;
                    } else {
                        coordinates[1][2] = aux;
                    }
                }
                cont++;
            }
            x = y + 1;
            if (checkDistance(page, "|", x, 10) && (page.indexOf("|", x) < page.indexOf("}", x))) {
                y = page.indexOf("|", x);
            } else {
                if (checkDistance(page, "}", x, 10)) {
                    y = page.indexOf("}", x);
                    aux = page.substring(x, y);
                    proceed = false;
                    coordinates[1][3] = aux;
                } else {
                    proceed = false;
                }
            }
        }
        return coordinates;
    }

    /**
     * Search the coordinates of the type "{{coord|" and "{{coor dms|".
     *
     * @param page page that the search will be performed.
     * @return coordinates.
     * @throws SAXException
     */
    public String[][] latP(String page) throws SAXException {
        int cont1 = 0;
        int cont = 0;
        int cont2 = 0, cont3 = 0;
        String aux;
        int x;
        int y;
        boolean proceed = true;
        String coordinates[][] = new String[2][4];

        x = page.indexOf("lat", 0);
        x = page.indexOf("=", x) + 1;
        if (checkDistance(page, "|", x, 6)) {
            y = page.indexOf("|", x);

        } else {
            return null;
        }

        while (proceed) {
            page = page.replaceAll("\n", " ");
            aux = page.substring(x, y);
            aux = aux.replaceAll(" ", "");

            if (checkDirection(aux)) {
                if (cont1 == 1) {
                    coordinates[1][3] = aux;
                } else {
                    coordinates[0][3] = aux;
                }
                cont = 0;
                cont1++;
            } else if (cont == 0) {
                if (cont2 == 0) {
                    coordinates[0][0] = aux;
                    cont2 = 1;
                } else {
                    coordinates[1][0] = aux;
                    cont2 = 2;
                }
                cont++;
            } else {
                if (cont == 1) {
                    if (cont2 == 1) {
                        coordinates[0][1] = aux;
                    } else {
                        coordinates[1][1] = aux;
                    }
                } else {
                    if (cont2 == 1) {
                        coordinates[0][2] = aux;
                    } else {
                        coordinates[1][2] = aux;

                    }
                }
                cont++;
            }
            x = y + 1;
            if (cont3 == 1) {
                return coordinates;
            }

            if (page.indexOf("=", x) != -1) {
                x = page.indexOf("=", x) + 1;
            }
            if (page.indexOf("|", x) != -1 && coordinates[1][2] == null) {
                y = page.indexOf("|", x);

            } else {
                if (page.indexOf(" ", x + 1) != -1 && coordinates[1][2] == null) {
                    y = page.indexOf(" ", x + 1);
                } else {
                    return coordinates;
                }
            }
        }
        return coordinates;
    }

    /**
     * Checks if the String contains the direction.
     *
     * @param str String to be checked.
     * @return True, if it contains direction. And false if have not.
     */
    public boolean checkDirection(String str) {
        if (str.equalsIgnoreCase("S") || str.equalsIgnoreCase("W")
                || str.equalsIgnoreCase("N") || str.equalsIgnoreCase("E")
                || str.equalsIgnoreCase("O") || str.equalsIgnoreCase("L")) {

            return true;
        }
        return false;
    }

    /**
     * Checking if there is the searched string, and if the distance is
     * acceptable. This method is used for searching strings that separate the
     * coordinates. Example: |20|57|W| When this distance is too large, it means
     * that the coordinate is complete.
     *
     * @param page page that contains the search string.
     * @param search expected String.
     * @param x position where the search should start.
     * @param max maximum acceptable distance.
     * @return True, if it finds String searched, and false, if not find.
     *
     */
    public boolean checkDistance(String page, String search, int x, int max) {
        return (page.indexOf(search, x) != -1 && !((page.indexOf(search, x) - x) > max));
    }

    @Override
    public void endElement(String namespaceURI, String sName, String qName) throws SAXException {

        page += textBuffer.toString().trim() + " ";
        textBuffer = null;

        if (eName(sName, qName).equals("page")) {

            try {
                //Tira a parte entre parênteses.
                if (title.toString().contains("(")) {
                    if (title.indexOf("(") > 2) {
                        title = title.substring(0, title.indexOf("(") - 1);
                    }
                }

                page = page.replaceAll("\n", " ");

                boolean isDesambiguation = false;
                String[] coordinates = new String[2];
                String[] temp = textClean(page);
                String categories = temp[2];
                title = title.replaceAll("\n", "").replaceAll("  ", "");

                //Localizando desambiguação.
                if (page.replaceAll(" ", "").toLowerCase().contains("categoria:desambiguaç")) {
                    isDesambiguation = true;

                } else if (page.replaceAll(" ", "").toLowerCase().contains("{{desambig")) {
                    isDesambiguation = true;
                }
                contPage++;
                if ((System.currentTimeMillis() - time) > STATUS_TIME) {
                    time = System.currentTimeMillis();
                    System.out.println("paginas: " + contPage);
                }


                if (isDesambiguation && !title.contains(":")) {
                    Writer.writeFile(desambiguationFile, id, title);
                }


                if (redir.length() > 0 && !title.contains(":") && !redir.contains(":")) {
                    Writer.writeFile(fileRedirects, title + "|", redir.toString());
                }

                if (!isDesambiguation && !isRedirect() && !title.contains(":")) {
                    Writer.writeFile(fileFullEntities, id, temp[3]);
                    Writer.writeFile(fileFullLanguages, id, temp[1]);
                    Writer.writeFile(fileFullText, id, temp[0]);
                    /*if (!temp[4].equals("1.0")) {
                     Writer.writeFile(fileImg, id, temp[4]);
                     }*/
                    Writer.writeFile(fileFullTitle, id, title);
                }


                if (categories.length() > 2) {
                    util.Writer.writeFile(fileCategories, id, categories);
                }
                if (((page.toLowerCase().contains("{{coord|"))
                        || (page.toLowerCase().contains("{{geocoordenadas"))
                        || (page.toLowerCase().contains("{{coor "))
                        || (page.contains("latP")) || (page.contains("lat_"))) && !title.contains(":")) {

                    String[][] aux = new String[2][4];

                    if (page.contains("{{geocoordenadas|") || page.contains("{{Geocoordenadas|")) {
                        aux = geocoordenadas(page);

                    } else if (page.toLowerCase().contains("{{coord|") || page.toLowerCase().contains("{{coor ")) {
                        aux = coord(page);
                    } else if (page.contains("latP") || page.contains("lat_")) {
                        aux = latP(page);
                    }
                    if (aux != null) {
                        coordinates = Converter.convertCoordinatesStr(aux);
                        title = title.replaceAll("\n", "").replaceAll("  ", "");


                        id = id.replaceAll("\n", "");
                        if (coordinates != null) {
                            coordinatesCount += 1;

                            page = temp[0];
                            String languages = temp[1];

                            String entities = temp[3];
                            //System.out.println("paginas com coordenadas: " + coordinatesCount);
                            Writer.writeFile(fileTextCoordinates, id, coordinates, page);
                            Writer.writeFile(fileTitle, id, title);

                            if (entities.length() > 2) {
                                Writer.writeFile(fileEntities, id, entities);
                            }
                            if (languages.length() > 2) {
                                Writer.writeFile(fileLanguages, id, languages);
                            }
                            /*System.out.println(id + "   "
                             + "   " + coordinates[0]
                             + " " + coordinates[1] + "   "
                             + title + "   " + page); */
                        }
                    }
                } else {

                    if (!isDesambiguation && !isRedirect() && !title.contains(":")) {
                        page = temp[0];
                        Writer.writeFile(descartedFileText, id, page);
                        Writer.writeFile(descartedFileTitle, id, title);
                        if (temp[3].length() > 2) {
                            util.Writer.writeFile(descartedFileEntities, id, temp[3]);
                        }
                        if (temp[1].length() > 2) {
                            Writer.writeFile(descartedFileLanguages, id, temp[1]);
                        }
                    }
                }

                redir = "";
                page = "";
                title = "";
                id = "";
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Remove the texts between "{{".
     *
     * @param text text to be cleaned.
     * @return text cleaned.
     */
    public String textClean1(String text, String a, String b) {
        String auxText = "";
        int x = 0;
        int y = 0;
        Stack stack = new Stack();

        while (true) {

            if (((text.indexOf(a, x) != -1) && text.indexOf(b, x) != -1
                    && text.indexOf(a, x) < text.indexOf(b, x))
                    || (text.indexOf(a, x) != -1) && text.indexOf(b, x) == -1) {

                y = text.indexOf(a, x);

                if (stack.empty()) {
                    auxText += text.substring(x, y);
                    stack.push(10);
                } else {
                    stack.push(10);
                }
                x = y + 2;

            }
            if ((text.indexOf(b, x) != -1 && (text.indexOf(b, x) < text.indexOf(a, x)
                    && text.indexOf(a, x) != -1)) || (!stack.empty() && text.indexOf(b, x)
                    != -1 && text.indexOf(a, x) == -1)) {
                y = text.indexOf(b, x);
                x = y + 2;

                try {
                    stack.pop();
                } catch (java.util.EmptyStackException ex) {
                    break;
                }
            }
            if (text.indexOf(b, x) == -1) {
                break;
            }
            if (stack.empty()) {
                break;
            }
        }
        auxText += text.substring(x, text.length());
        if (auxText.contains(b) && !auxText.contains(a)) {
            auxText = auxText.substring(auxText.indexOf(b) + 2, auxText.length());
        }


        auxText = auxText.replaceAll("  ", " ");
        return auxText;

    }

    /**
     * Makes a cleaning in the text. Also search for entities, categories and
     * languagens in the text.
     *
     * @param text to be cleaned.
     * @return vector of String. Since the position [0] = text cleaned, [1] =
     * languages [2] = categories, [3] entities.
     * @throws SAXException
     */
    public String[] textClean(String text) throws SAXException {
        String auxText = "";
        String[] conteudo = {"", "|", "|", "|", ""};
        String lixo = "";
        boolean isTrush = false;
        int x = 0;
        int y = 0;
        Stack<Integer> pilha = new Stack<Integer>();
        boolean segue = false;

        while (text.contains("{{") && text.contains("}}")) {
            text = textClean1(text, "{{", "}}");
        }

        while (text.contains("{|") && text.contains("|}")) {
            text = textClean1(text, "{|", "|}");
        }

        while (true) {

            if (text.indexOf("[[", x) != -1 && text.indexOf("[[", x) < text.indexOf("]]", x)) {
                segue = true;
                y = text.indexOf("[[", x);
                if (pilha.empty()) {
                    auxText += text.substring(x, y);
                    pilha.push(y + 2);
                    isTrush = false;
                    lixo = "";
                } else {
                    pilha.push(y + 2);
                    lixo += text.substring(x, y);
                    //Quando tem um colchetes dentro de outro. ignorar tudo quando o primeiro colchetes tiver ":"
                    if (lixo.contains(":")) {
                        isTrush = true;
                    }
                }
                x = y + 2;
            } else if (text.indexOf("]]", x) != -1) {
                int a = 0;
                try {
                    a = pilha.pop();
                } catch (java.util.EmptyStackException ex) {
                    break;
                }
                if (segue && !isTrush) {

                    y = text.indexOf("]]", a);
                    String aux = text.substring(a, y);
                    a = 0;
                    if (aux.contains("|") && !aux.contains(":")) {
                        a = aux.indexOf("|", a) + 1;
                        if (!aux.substring(a, aux.length()).contains("|")) {
                            aux = aux.substring(a, aux.length());
                        } else {
                            aux = "";
                        }

                    }
                    if (aux.contains(":")) {
                        if (checkDistance(aux, ":", 0, 3)) {
                            conteudo[1] += aux + "|";
                        } else if (aux.contains("Categoria:")) {
                            conteudo[2] += aux.substring(aux.indexOf(":") + 1, aux.length()) + "|";
                        }
                    } else {
                        auxText += aux;
                        conteudo[3] += aux + "|";
                    }
                    x = y + 2;
                    segue = false;
                } else {
                    y = text.indexOf("]]", x);
                    x = y + 2;
                }
            } else {
                break;
            }
        }
        if (auxText.equals("")) {
            conteudo[0] = text;
        } else {
            conteudo[0] = auxText;
        }

        /*
         try {
         String xml = ReadPage.getContent("http://pt.wikipedia.org/w/api.php?action=query&prop=pageimages|extracts|langlinks&format=xml&pithumbsize=100&pageids=" + id);
         conteudo[0] = LeitorWikiXML.findText(xml).replaceAll("\n", " ");
         try {
         conteudo[4] = LeitorWikiXML.findImg(xml);
         } catch (Exception ex) {
         conteudo[4] = null;
         }
         } catch (Exception ex) {
         Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
         System.out.println("id: " + id);

         }*/
        return conteudo;
    }

    private String eName(String sName, String qName) {
        return "".equals(sName) ? qName : sName;
    }

    @Override
    public void endDocument() throws SAXException {
        System.out.println("endDocument");
        System.out.println(coordinatesCount + " coordinates");
        long end = System.currentTimeMillis();
        long diff = end - beginning;
        System.out.println("Demorou " + (diff / 1000) + " segundos");
        System.out.println("2: " + contDesambiguation2);
        System.out.println("1: " + contDesambiguation1);

    }

    private boolean isRedirect() {
        return redir.length() > 0;
    }

    /**
     * Receives file xml and initiates parser.
     *
     * @param args the file to be parsed.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //args[0] is the xml from wikipedia.
        ContentExtractor handler = new ContentExtractor();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse("/home/thiagolima/ptwiki-latest-pages-articles.xml", handler);
    }
}