/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datasets;

import framework.Point;
import framework.SpatialObject;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import util.Util;
import util.file.ColumnFileException;
import util.sse.SSEExeption;

/**
 *
 * @author joao
 */
public class OpenStreetMapObjectHandler extends DefaultHandler {

    private static final long STATUS_TIME = 4 * 60 * 1000;
    static final String OBJECT_FILENAME = "object.txt";
    static final String POINTS = "points.txt";
    /**
     * Stores the keys that defines the spatio textual objects
     */
    private final HashSet<String> ignoreNodes;
    private final HashSet<String> ignoreTags;
    private HashMap<Long, String> nodesHash = new HashMap<Long, String>();
    private boolean isSpatioTextualObject;
    private final StringBuilder description;
    private final String prefix;
    //Create ids for the objects
    private int objectId = 1;
    private SpatialObject spatialObject;
    private ArrayList<Long> way;
    private final PrintWriter objectOutput;
    private final File nodeInput;
    private final PrintWriter nodeOutput;
    private int newSmallId;
    //debug variables
    private final boolean debug;
    private long numObjects;
    private long numVertices;
    private long numAdjacencies;
    private long time;
    private boolean constructionPoints;

    public OpenStreetMapObjectHandler(String prefix, int idsCacheSize, int networkCacheSize,
            int verticesCacheSize, HashSet<String> ignoreNodes, HashSet<String> ignoreTags,
            boolean debug, boolean constructionPoints) throws IOException, ColumnFileException, SSEExeption {

        this.constructionPoints = constructionPoints;

        this.ignoreNodes = ignoreNodes;
        this.ignoreTags = ignoreTags;
        this.prefix = prefix;
        this.description = new StringBuilder();
        this.debug = debug;

        objectOutput = new PrintWriter(new BufferedWriter(new FileWriter(prefix + "/"
                + OBJECT_FILENAME, true)), true);
        nodeInput = new File(prefix + "/" + POINTS);
        nodeOutput = new PrintWriter(new BufferedWriter(new FileWriter(prefix + "/"
                + POINTS, true)), true);
    }

    @Override
    public void startDocument() throws SAXException {
        //No op.
        if (debug) {
            System.out.print("\nStarts parsing file..." + new Date());
            time = System.currentTimeMillis();
            try {
                this.readPoints();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(OpenStreetMapHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("End read file");
    }

    @Override
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {

        if (attrs != null) {
            if ("node".equals(eName(sName, qName))) {
                long id = Long.parseLong(attrs.getValue("id"));
                double lat = Double.parseDouble(attrs.getValue("lat"));
                double lon = Double.parseDouble(attrs.getValue("lon"));

                spatialObject = new SpatialObject(id, "", "", lat, lon, "", "", "", "");
//                description.append(id).append(" ").append(lat).append(" ").append(lon);
                isSpatioTextualObject = true;

            } else if ("tag".equals(eName(sName, qName)) && spatialObject != null) {
                if (isSpatioTextualObject) {
                    String key = attrs.getValue("k");
                    String value = attrs.getValue("v");

                    if (key == null || this.ignoreNodes.contains(key)) {
                        isSpatioTextualObject = false;
//                        description.delete(0, description.length());

                        /*}else if(value!=null && !ignoreTags.contains(key) &&
                         !value.equals("yes") &&
                         !value.equals("no") && value.trim().length()>1){
                         description.append(value);
                         description.append(' ');
                         }*/
                    } else if (value != null && !ignoreTags.contains(key)
                            && !value.equals("yes")
                            && !value.equals("no") && value.trim().length() > 1) {
                        description.append("|").append(key).append("=").append(value);
                    }
                }

            } else if ("way".equals(eName(sName, qName))) {
                way = new ArrayList();
            } else if ("nd".equals(eName(sName, qName)) && way != null) {

                way.add(Long.parseLong(attrs.getValue("ref")));
                String key = attrs.getValue("ref");

//                description.append(key).append(" ");

            } else if ("tag".equals(eName(sName, qName)) && way != null) {
                String key = attrs.getValue("k");
                String value = attrs.getValue("v");
                if (key == null || this.ignoreNodes.contains(key)) {
                    if (way == null) {
                        isSpatioTextualObject = false;
                        description.delete(0, description.length());
                    }
                    /*}else if(value!=null && !ignoreTags.contains(key) &&
                     !value.equals("yes") &&
                     !value.equals("no") && value.trim().length()>1){
                     description.append(value);
                     description.append(' ');
                     }*/
                } else if (value != null && !ignoreTags.contains(key)
                        && !value.equals("yes")
                        && !value.equals("no") && value.trim().length() > 1) {
                    description.append("|").append(key).append("=").append(value);
                }
            }
        }
    }

    @Override
    public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
        try {

            if ("node".equals(eName(sName, qName)) && spatialObject != null) {
                description.trimToSize();
                if (constructionPoints) {
                    printNode(nodeOutput, spatialObject);
                } else {
                    if (description.length() > 1) { //is a spatiotextual object

                        printSpatialObject(objectOutput, spatialObject);
                        objectOutput.print(' ');
                        objectOutput.print(description.toString().replace('\n', ' ').replace('\r', ' '));
                        objectOutput.println();
                        numObjects++;
                    }

                    description.delete(0, description.length());
                    spatialObject = null;
                }
            } else if (!constructionPoints && "way".equals(eName(sName, qName)) && way != null) {
                if (description.length() > 1) {
                    //Only ways with name
                    if (description.toString().contains("|name=")) {
                        Iterator<Long> it = way.iterator();
                        List<Point> points = new ArrayList<>();
                        int count = 0;
                        long wayId = 0;
                        while (it.hasNext()) {
                            long id = it.next();
                            if (count == 0) {
                                wayId = id;
                            }
                            count++;
                            String get = nodesHash.get(id);
                            String[] split = get.split(" ");
                            points.add(new Point(Double.parseDouble(split[0]), Double.parseDouble(split[1])));
                        }
                        double largeLatitude = Point.largeLatitude(points);
                        double smallerLatitude = Point.smallerLatitude(points);
                        double largeLongitude = Point.largeLongitude(points);
                        double smallerLongitude = Point.smallerLongitude(points);
                        double lat = (largeLatitude + smallerLatitude) / 2;
                        double lgt = (largeLongitude + smallerLongitude) / 2;

                        spatialObject = new SpatialObject(wayId, "", "", lat, lgt, "", "", "", "");
                        printSpatialObject(objectOutput, spatialObject);
                        objectOutput.print(' ');
                        objectOutput.print(description.toString().replace('\n', ' ').replace('\r', ' '));
                        objectOutput.println();
                        spatialObject = null;
                        numObjects++;
                    }
                }

                description.delete(0, description.length());

                way = null;
            }
            if (debug && (System.currentTimeMillis() - time) > STATUS_TIME) {
                time = System.currentTimeMillis();
                System.out.print(" [|P|=" + numObjects + ",|V|=" + numVertices + ",|A|=" + numAdjacencies + "]");
            }
        } catch (Exception ex) {
            throw new SAXException(ex);
        }
    }

    private String eName(String sName, String qName) {
        return "".equals(sName) ? qName : sName;
    }

    private void readPoints() throws FileNotFoundException {
        Scanner input = new Scanner(nodeInput, "UTF-8");
        long time = System.currentTimeMillis();
        int count = 0;
        while (input.hasNext()) {
            String line = input.nextLine();
            String[] aux = line.split(" ");
            nodesHash.put(Long.parseLong(aux[0]), aux[1] + " " + aux[2]);
            if ((System.currentTimeMillis() - time) > STATUS_TIME) {
                time = System.currentTimeMillis();
                System.out.print(" [" + count + "]");
            }
        }
        input.close();
    }

    private void printSpatialObject(PrintWriter output, SpatialObject object) {
        output.print(objectId++); //small ID
        output.print(' ');
        output.print(object.getId()); //original id
        output.print(' ');
        output.print(object.getLatitude());
        output.print(' ');
        output.print(object.getLongitude());
    }

    private void printNode(PrintWriter output, SpatialObject object) {
        output.print(object.getId()); //original id
        output.print(' ');
        output.print(object.getLatitude());
        output.print(' ');
        output.print(object.getLongitude());
        output.println();
    }

    private static ArrayList<Integer> reverse(ArrayList<Integer> list) {
        ArrayList<Integer> other = new ArrayList<Integer>(list.size());
        for (Integer item : list) {
            other.add(0, item);
        }
        return other;
    }

    @Override
    public void endDocument() throws SAXException {

        try {
            objectOutput.close();

        } catch (Exception e) {
            throw new SAXException(e);
        }

        if (debug) {
            System.out.print("\nParsing finished [|P|=" + numObjects
                    + ",|V|=" + numVertices + "]..." + new Date());
        }
    }

    /**
     * Receives an osm (Open Street Map) file and outputs three files: - network
     * file: <vertexId> <adjVertexId> <<lat> <lgt>> <<lat> <lgt>> <<lat>
     * <lgt>>... - spatio textual objects file: <objectId> <latitude>
     * <longitude> <Description>
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        args = new String[]{"NewYork.osm"};

        String[] ignoreNodes = new String[]{"way", "highway", "multipolygon",
            "route", "barrier"};

        String[] ignoreTags = new String[]{"created_by", "type", "color", "ref", "network"};

        for (int a = 0; a < 2; a++) {

            boolean constructionPoints = true;

            if (a == 1) {
                constructionPoints = false;
            }

            OpenStreetMapObjectHandler handler = new OpenStreetMapObjectHandler(
                    "OSMParsing", 150000, 150000, 150000,
                    new HashSet(Arrays.asList(ignoreNodes)),
                    new HashSet(Arrays.asList(ignoreTags)), true, constructionPoints);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            System.out.print("Starts parsing " + args[0] + " file..");
            long time = System.currentTimeMillis();


            InputStream input = null;
            if (args[0].endsWith(".osm")) {
                input = new BufferedInputStream(new FileInputStream(args[0]));
                Reader reader = new InputStreamReader(input, "UTF-8");

                InputSource is = new InputSource(reader);
                is.setEncoding("UTF-8");

                saxParser.parse(is, handler);
            } else if (args[0].endsWith(".bz2")) {
                Process p = Runtime.getRuntime().exec("bzcat " + args[0]);
                input = new BufferedInputStream(p.getInputStream());
                Reader reader = new InputStreamReader(input, "UTF-8");

                InputSource is = new InputSource(reader);
                is.setEncoding("UTF-8");

                saxParser.parse(is, handler);
            } else {
                throw new RuntimeException("File '" + args[0] + "' format not supported!");
            }
            input.close();

            System.out.println(", concluded in " + Util.time(System.currentTimeMillis() - time) + ".");

            System.out.println("Max memory: " + (Runtime.getRuntime().maxMemory() / 1024) / 1024 + "MB");
            System.out.println("Total memory used: " + (Runtime.getRuntime().totalMemory() / 1024) / 1024 + "MB");
        }
//        java.io.Writer file = new OutputStreamWriter(new FileOutputStream(new File("/home/joaopaulo/Done" + ".txt"), true));
//
//        Calendar cal = Calendar.getInstance();
//        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL,
//                DateFormat.MEDIUM);
//
//        file.append(args[0] + " Done! " + cal.getTime());
//        file.close();
    }
}
