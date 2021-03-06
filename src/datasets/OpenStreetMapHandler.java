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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import jdbm.util.Btree;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import util.FileUtils;
import util.Util;   
import util.Writer;
import util.file.BufferedListStorage;
import util.file.ColumnFileException;
import util.file.DoubleEntry;
import util.file.IntegerEntry;
import util.file.ListStorage;
import util.sse.SSEExeption;

/**
 *
 * @author joao
 */
public class OpenStreetMapHandler extends DefaultHandler {

    private static final long STATUS_TIME = 4 * 60 * 1000;
    static final String PARTITION_FILENAME = "partitions.txt";
    static final String NETWORK_FILENAME = "network.txt";
    static final String OBJECT_FILENAME = "object.txt";
    static final String WAY_FILENAME = "way.txt";
    static final String VERTICES_FILENAME = "vertices.txt";
    static final String POINTS = "points.txt";
    
    /**
     * Stores the keys that defines the spatio textual objects
     */
    private final HashSet<String> ignoreNodes;
    private final HashSet<String> ignoreTags;
    private HashMap<Long, String> nodesHash = new HashMap<Long, String>();
    private boolean isSpatioTextualObject;
    private final StringBuilder description;
    //Maps vertex to adjacent vertices
    private final BufferedListStorage<IntegerEntry> network;
    //vertices coordinates
    private final BufferedListStorage<DoubleEntry> vertices;
    //Converts big ids to small ids
    private final Btree<Long, Integer> vertexTinyId;
    private final String prefix;
    //Create ids for the objects
    private int objectId = 1;
    private SpatialObject spatialObject;
    private ArrayList<Long> way;
    private final PrintWriter objectOutput;
    private final File nodeInput;
    private final PrintWriter wayOutput;
    private final PrintWriter nodeOutput;
    private int newSmallId;
    //debug variables
    private final boolean debug;
    private long numObjects;
    private long numVertices;
    private long numAdjacencies;
    private long time;
    private boolean constructionPoints;
    
    public OpenStreetMapHandler(String prefix, int idsCacheSize, int networkCacheSize,
            int verticesCacheSize, HashSet<String> ignoreNodes, HashSet<String> ignoreTags,
            boolean debug, boolean constructionPoints) throws IOException, ColumnFileException, SSEExeption {

        this.constructionPoints = constructionPoints;
        
        this.ignoreNodes = ignoreNodes;
        this.ignoreTags = ignoreTags;        
        this.prefix = prefix;
        this.description = new StringBuilder();
        this.debug = debug;

        vertexTinyId = new Btree<Long, Integer>(null, null, prefix + "/vertexMapping",
                FileUtils.DISK_PAGE_SIZE, Long.SIZE / Byte.SIZE, idsCacheSize);
        vertexTinyId.open();

        newSmallId = vertexTinyId.size() + 1;

        vertices = new BufferedListStorage<DoubleEntry>(null, null, prefix + "/vertices",
                verticesCacheSize, DoubleEntry.SIZE, DoubleEntry.FACTORY);
        vertices.open();

        network = new BufferedListStorage<IntegerEntry>(null, null, prefix + "/network",
                networkCacheSize, IntegerEntry.SIZE, IntegerEntry.FACTORY);
        network.open();

        objectOutput = new PrintWriter(new BufferedWriter(new FileWriter(prefix + "/"
                + OBJECT_FILENAME, true)), true);
        nodeInput = new File(prefix + "/"+POINTS);
        wayOutput = new PrintWriter(new BufferedWriter(new FileWriter(prefix + "/"
                + WAY_FILENAME, true)), true);
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
                //description.append(id).append(" ").append(lat).append(" ").append(lon);
                isSpatioTextualObject = true;

            } else if ("tag".equals(eName(sName, qName)) && spatialObject != null) {
                if (isSpatioTextualObject) {
                    String key = attrs.getValue("k");
                    String value = attrs.getValue("v");

                    if (key == null || this.ignoreNodes.contains(key)) {
                        isSpatioTextualObject = false;
                        // description.delete(0, description.length());

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

                //  description.append(key).append(" ");

            } else if ("tag".equals(eName(sName, qName)) && way != null) {
                String key = attrs.getValue("k");
                String value = attrs.getValue("v");
                if (key == null || this.ignoreNodes.contains(key)) {
                  if(way==null){
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

                //ways are also used to represent building that are not path in the graph
            }
        }

    }

    private int getSmallId(long longId) throws IOException {
        Integer smallId = vertexTinyId.get(longId);
        if (smallId == null) {
            smallId = newSmallId++;
            vertexTinyId.put(longId, smallId);
        }
        return smallId;
    }

    @Override
    public void characters(char buf[], int offset, int len) throws SAXException {
        // characters are never used
    }

    @Override
    public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
        try {

            if ("node".equals(eName(sName, qName)) && spatialObject != null) {
                description.trimToSize();
                if(constructionPoints){
                    printNode(nodeOutput, spatialObject);
                }else{
                if (description.length() > 1) { //is a spatiotextual object

                  
                    
                    printSpatialObject(objectOutput, spatialObject);
                    objectOutput.print(' ');
                    objectOutput.print(description.toString().replace('\n', ' ').replace('\r', ' '));
                    objectOutput.println();
                    numObjects++;
                } else { //is a vertex
                    vertices.putList(getSmallId(spatialObject.getId()),
                            Arrays.asList(new DoubleEntry(spatialObject.getLatitude()),
                            new DoubleEntry(spatialObject.getLongitude())));

                    numVertices++;
                }
                description.delete(0, description.length());
                spatialObject = null;
                //    }else if("nd".equals(eName(sName, qName))){                
                //          numVertices++;                        
            }
            } else if (!constructionPoints && "way".equals(eName(sName, qName)) && way != null) {
                if (description.length() > 1) {
                    //Only ways with name
                    if (description.toString().contains("|name=")) {
                        Iterator<Long> it = way.iterator();
                        List<Point> points = new ArrayList<Point>();
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
                    //    printNode(wayOutput, description.toString());
                }
                
                description.delete(0, description.length());
                
                way = null;
            }
            //System.out.println("a");
            if (debug && (System.currentTimeMillis() - time) > STATUS_TIME) {
                //   System.out.println("b");
                time = System.currentTimeMillis();
                System.out.print(" [|P|=" + numObjects + ",|V|=" + numVertices + ",|A|=" + numAdjacencies + "]");
                //System.out.println(Util.time(System.currentTimeMillis()-start)+".");
            }
        } catch (Exception ex) {
            throw new SAXException(ex);
        }

    }

    private String eName(String sName, String qName) {
        return "".equals(sName) ? qName : sName;
    }

    /*private void createNetwork() throws ColumnFileException, IOException {
     if(debug)  System.out.println("\nStart building real adjacencies..."+ new Date());
     long watch = System.currentTimeMillis();

     File networkFile = new File(prefix+"/"+NETWORK_FILENAME);

     PrintWriter networkFileWriter= new PrintWriter(new BufferedWriter(new FileWriter(networkFile, true)), true);
     ListStorage<IntegerEntry> adjacencies = buildAdjacencies(networkFileWriter);
     networkFileWriter.close();

     //delete files that are not relevant anymore
     network.delete();
     vertices.delete();
     if(debug)  System.out.println("Adjacencies built in "+Util.time(System.currentTimeMillis()-watch));

     watch = System.currentTimeMillis();
     if(debug)  System.out.println("\nStart creating partitions..."+ new Date());
        
     PartitionsBuilder.createPartitions(prefix, PARTITION_FILENAME, adjacencies);

     adjacencies.close();
     adjacencies.delete();
     if(debug)  System.out.println("Partitions created in "+Util.time(System.currentTimeMillis()-watch));
     }
     */
    private ListStorage<IntegerEntry> buildAdjacencies(PrintWriter networkOutput) throws ColumnFileException, IOException {
        ListStorage<IntegerEntry> adjacencies = new ListStorage<IntegerEntry>(null,
                null, prefix + "/adjacencies", IntegerEntry.SIZE, IntegerEntry.FACTORY);
        adjacencies.open();

        ArrayList<Integer> path = new ArrayList<Integer>();
        int edgeId = 1;

        HashSet<Integer> pathsPrinted = new HashSet<Integer>();
        //The vertexId is the transformed vertex id to a small representation...

        Iterator<Integer> idsIterator = network.getIdsIterator();
        int vertexId;
        while (idsIterator.hasNext()) {
            vertexId = idsIterator.next();
            List<IntegerEntry> adjacency = network.getList(vertexId);

            if (adjacency != null && adjacency.size() > 2) { //it is a network vertex
                for (int i = 0; i < adjacency.size(); i++) {
                    Integer adjVertex = adjacency.get(i).getValue();
                    List<IntegerEntry> neighborAdjacents = network.getList(adjVertex);

                    if (neighborAdjacents != null) {
                        path.add(vertexId);
                        if (neighborAdjacents.size() == 2) {
                            expand(adjVertex, path);
                        } else {
                            path.add(adjVertex);
                        }

                        if (path.get(path.size() - 1) < path.get(0)) {
                            path = reverse(path);
                        }

                        if (!pathsPrinted.contains(path.toString().hashCode())) {
                            pathsPrinted.add(path.toString().hashCode());
                            networkOutput.print(edgeId++); //edgeId
                            networkOutput.print(' ');
                            printPath(networkOutput, path);

                            //update adjacencies
                            adjacencies.addEntry(path.get(0), new IntegerEntry(path.get(path.size() - 1)), true);
                            adjacencies.addEntry(path.get(path.size() - 1), new IntegerEntry(path.get(0)), true);
                        }

                        path.clear();
                    }
                }
                network.remove(vertexId); //To avoid to be visited abain
            }
        }
        return adjacencies;
    }

    /**
     * Returns false if the expansion causes a cycle.
     *
     * @param vertex
     * @param path
     * @return
     */
    private void expand(int vertex, ArrayList<Integer> path) throws ColumnFileException, IOException {
        HashSet<Integer> hash = new HashSet<Integer>(path);

        List<IntegerEntry> adjacents = network.getList(vertex);
        while (!hash.contains(vertex) && adjacents != null && adjacents.size() == 2) {
            path.add(vertex);
            hash.add(vertex);
            vertex = hash.contains(adjacents.get(0).getValue())
                    ? adjacents.get(1).getValue()
                    : adjacents.get(0).getValue();

            adjacents = network.getList(vertex);
        }
        path.add(vertex);
    }

    private void readPoints() throws FileNotFoundException {
        Scanner input = new Scanner(nodeInput, "ISO-8859-1");
        long time = System.currentTimeMillis();
        int count = 0;
        while (input.hasNext()) {
            String line = input.nextLine();
            String[] aux = line.split(" ");
            nodesHash.put(Long.parseLong(aux[0]), aux[1] + " " + aux[2]);
            if((System.currentTimeMillis()-time)>STATUS_TIME){
                        time = System.currentTimeMillis();
                        System.out.print(" ["+count+"]");                     
            }
        }
        input.close();
    }

    private void printPath(PrintWriter networkOutput, ArrayList<Integer> path) throws ColumnFileException, IOException {
        networkOutput.print(path.get(0)); //startVertexId
        networkOutput.print(' ');
        networkOutput.print(path.get(path.size() - 1)); //endVertexId

        List<DoubleEntry> coordinates;
        for (int vertex : path) {
            coordinates = vertices.getList(vertex);
            networkOutput.print(' ');
            networkOutput.print(coordinates.get(0).getValue());
            networkOutput.print(' ');
            networkOutput.print(coordinates.get(1).getValue());
        }
        networkOutput.println();
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
            wayOutput.close();
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(prefix + "/" + VERTICES_FILENAME, true)), true);
            Iterator<Entry<Long, Integer>> iterator = vertexTinyId.iterator();
            Entry<Long, Integer> entry;
            while (iterator.hasNext()) {
                entry = iterator.next();
                writer.println(entry.getKey() + " " + entry.getValue());
            }
            writer.close();

            vertexTinyId.delete();
            vertexTinyId.close();

            // createNetwork();
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

        //String[] spatioTextualKeys = new String[]{"leisure", "amenity", "shop",
        //    "craft", "emergency", "tourism",
        //    "historic", "sport"};

        String[] ignoreNodes = new String[]{"way", "highway", "multipolygon",
            "route", "barrier"};

        String[] ignoreTags = new String[]{"created_by", "type", "color", "ref", "network"};

//
//        OpenStreetMapHandler handler = new OpenStreetMapHandler(
//                "/home/joaopaulodias/OSMParsing", 10, 10, 10,
//                new HashSet(Arrays.asList(ignoreNodes)),
//                new HashSet(Arrays.asList(ignoreTags)), true, false);
//        SAXParserFactory factory = SAXParserFactory.newInstance();
//        SAXParser saxParser = factory.newSAXParser();

                OpenStreetMapHandler handler = new OpenStreetMapHandler(
                "OSMParsing", 1000000, 1000000, 1000000,
                new HashSet(Arrays.asList(ignoreNodes)),
                new HashSet(Arrays.asList(ignoreTags)), true, false);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        
        System.out.print("Starts parsing " + args[0] + " file..");
        long time = System.currentTimeMillis();


        InputStream input = null;
        if (args[0].endsWith(".osm")) {
            input = new BufferedInputStream(new FileInputStream(args[0]));
            saxParser.parse(input, handler);
        } else if (args[0].endsWith(".bz2")) {
            Process p = Runtime.getRuntime().exec("bzcat " + args[0]);
            input = new BufferedInputStream(p.getInputStream());
            saxParser.parse(input, handler);
        } else {
            throw new RuntimeException("File '" + args[0] + "' format not supported!");
        }
        input.close();

        System.out.println(", concluded in " + Util.time(System.currentTimeMillis() - time) + ".");

        System.out.println("Max memory: " + (Runtime.getRuntime().maxMemory() / 1024) / 1024 + "MB");
        System.out.println("Total memory used: " + (Runtime.getRuntime().totalMemory() / 1024) / 1024 + "MB");
    }
}

class CycleException extends Exception {

    public CycleException(String message) {
        super(message);
    }
}
