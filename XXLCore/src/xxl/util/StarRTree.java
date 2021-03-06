/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xxl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.StringTokenizer;
import xxl.util.FileUtils;
import xxl.util.statistics.DefaultStatisticCenter;
import xxl.util.statistics.StatisticCenter;
import xxl.core.collections.containers.Container;
import xxl.core.collections.containers.io.BlockFileContainer;
import xxl.core.collections.containers.io.BufferedContainer;
import xxl.core.collections.containers.io.ConverterContainer;
import xxl.core.cursors.Cursor;
import xxl.core.functions.Function;
import xxl.core.indexStructures.Descriptor;
import xxl.core.indexStructures.ORTree;
import xxl.core.indexStructures.ORTree.IndexEntry;
import xxl.core.indexStructures.RTree;
import xxl.core.io.LRUBuffer;
import xxl.core.io.converters.ConvertableConverter;
import xxl.core.io.converters.IntegerConverter;
import xxl.core.spatial.KPE;
import xxl.core.spatial.rectangles.DoublePointRectangle;
import xxl.core.spatial.rectangles.Rectangle;


/**
 *
 * @author joao
 */
public class StarRTree extends RTree{

    protected final StatisticCenter statisticCenter;
    protected BlockFileContainer fileContainer;
    private Container container;
    private final String outputPath;
    private final int minNodeCapacity;
    private final int maxNodeCapacity;
    private final int dimensions;
    private final String id;
    private boolean loadedFromFile;
    private final int bufferSize;
    private final int blockSize;

    public StarRTree(StatisticCenter statisticCenter, String id, String outputPath,
            int dimensions, int cacheSize, int blockSize, int minNodeCapacity, int maxNodeCapacity){
        this.outputPath = outputPath;
        this.id = id;
        this.minNodeCapacity = minNodeCapacity;
        this.maxNodeCapacity = maxNodeCapacity;
        this.dimensions = dimensions;
        this.bufferSize = cacheSize;
        this.blockSize = blockSize;
        this.statisticCenter = statisticCenter;
    }


    public void open() throws IOException, ClassNotFoundException {
       
        FileUtils.createDirectories(getOutputPath());

        File resourceFile = new File(getOutputPath()+".res");
        if(resourceFile.exists()){
            fileContainer = new BlockFileContainer(getOutputPath());
        }else{ //Create a new file
            fileContainer = new BlockFileContainer(getOutputPath(),blockSize);
        }
        CountContainer pageFaultsContainer = new CountContainer(
			createConverterContainer(dimensions),
                        statisticCenter.getCount(id+"pageFaults"));

        if(bufferSize>0){
            BufferedContainer bufferedContainer = new BufferedContainer(pageFaultsContainer,
                    new LRUBuffer(bufferSize), true);

            container = new CountContainer(bufferedContainer,
                    statisticCenter.getCount(id+"nodesAccessed"));
        }else{
            container = new CountContainer(pageFaultsContainer,
                    statisticCenter.getCount(id+"nodesAccessed"));
        }

        if(resourceFile.exists()){
            ObjectInputStream metadataFile = new ObjectInputStream(new FileInputStream(resourceFile));
            Object rootPageId = metadataFile.readObject(); //rootPageId
            Descriptor rootMBR =  (Descriptor) metadataFile.readObject();
            int height = metadataFile.readInt(); //height
            metadataFile.close();

            ORTree.IndexEntry root = (ORTree.IndexEntry) ((ORTree.IndexEntry)this.createIndexEntry(height)).initialize(rootMBR).initialize(rootPageId);

            loadedFromFile = true;
            initialize(root, rootMBR, GET_DESCRIPTOR, container, minNodeCapacity, maxNodeCapacity);
        }else{
            loadedFromFile = false;
            initialize(null, null, GET_DESCRIPTOR, container, minNodeCapacity, maxNodeCapacity);
        }
    }

    /**
     * Return the size in bytes of this R-TREE. Call the method save() before
     * calling this method to get an acurate value.
     * @return
     */
    public long getSizeInBytes(){
        return fileContainer.size()*fileContainer.blockSize();
    }

    /**
     * @return the minNodeCapacity
     */
    public int getMinNodeCapacity() {
        return minNodeCapacity;
    }

    /**
     * @return the maxNodeCapacity
     */
    public int getMaxNodeCapacity() {
        return maxNodeCapacity;
    }

    public boolean loadedFromFile(){
        return loadedFromFile;
    }

    public void flush() throws IOException{
        if(container!=null) container.flush();
        if(fileContainer!=null) fileContainer.flush();

        if(this.rootEntry()!=null){
            ObjectOutputStream metadata = new ObjectOutputStream(new FileOutputStream(this.getOutputPath() + ".res"));
            metadata.writeObject(this.rootEntry().id()); //rootPageId
            metadata.writeObject(this.rootDescriptor());//descriptor
            metadata.writeInt(this.height()); //height

            metadata.flush();
            metadata.close();
        }
    }

    public void close() throws IOException{
        this.flush();

        if(container!=null) container.close();
        container=null;

        if(fileContainer!=null) fileContainer.close();
        fileContainer=null;
    }

    /**
     * Function creating a descriptor for a given object.
     */
    private static Function GET_DESCRIPTOR = new Function () {
            public Object invoke (Object o) {
                    return ((KPE)o).getData();
            }
    };


    @Override
    public Rectangle rectangle (Object entry) {
        return (Rectangle) ((DoublePointRectangle)descriptor(entry)).clone();
    }

    protected ConverterContainer createConverterContainer(final int dimensions){
        return new ConverterContainer(
            fileContainer,
            this.nodeConverter(new ConvertableConverter(
                new Function () {
                    @Override
                    public Object invoke () {
                        return new KPE(new DoublePointRectangle(dimensions));
                    }
                }), this.indexEntryConverter(
                        new ConvertableConverter(
                            new Function () {
                                @Override
                                public Object invoke () {
                                    return new DoublePointRectangle(dimensions);
                            }
                            }
                        )
                   )
            )
        );
    }

    public void checkTree(IndexEntry n){
        //No op yet.
    }


    public String toString(IndexEntry n, int level){
        StringBuilder str = new StringBuilder();
        StringBuffer space = new StringBuffer();
        for(int i=n.level(); i<(this.height()-1);i++){
            space.append("     ");
        }
        str.append(space);
        str.append(toString((DoublePointRectangle)n.descriptor()));
        str.append("node_id=").append(n.id());
        str.append('\n');

        space.append("     ");
        if(n.level()==0){
            for(Iterator it = n.get().entries();it.hasNext();){
                str.append(space);
                KPE dataEntry = (KPE)it.next();
                str.append(dataEntry.getID());
                str.append(toString((DoublePointRectangle)dataEntry.getData()));
                str.append('\n');
            }
        }else{
            if((n.level()-1)>=level){
                for(Iterator it = n.get().entries(); it.hasNext();){
                    str.append(toString((IndexEntry)it.next(), level));
                }
            }
        }
        return str.toString();

    }

    public static String toString(Rectangle mbr){
        StringBuffer str = new StringBuffer();

        str.append('[');
        for(int i=0; i<2;i++){
            str.append('(');
            for(int d=0;d<mbr.dimensions();d++){
                str.append(String.format("%.2f", mbr.getCorner(i>0).getValue(d)));
                str.append(", ");
            }
            str.delete(str.length()-2, str.length());
            str.append(')');
        }
        if(mbr instanceof MaxDoubleRectangle){
            str.append(", scr=");
            str.append(((MaxDoubleRectangle)mbr).getScore());
        }
        if(mbr instanceof TextRectangle){
            str.append(", id=");
            str.append(((TextRectangle)mbr).getId());
        }
        if(mbr instanceof BooleanRectangle){
            str.append(", nn=");
            str.append(((BooleanRectangle)mbr).getBoolean());
        }
        str.append(']');
        return str.toString();
    }

    public String toString(int level){
        IndexEntry n = (IndexEntry)this.rootEntry();
        return toString(n, Math.min(level, this.height()-1));
    }

    @Override
    public String toString(){
        return toString(0);
    }

    public static double[] getValues(String line, int dims){
        StringTokenizer tokens = new StringTokenizer(line);

        double[] values = new double[dims];
        int d=0;
        String token=null;
        for(int i=0;d<dims && tokens.hasMoreTokens();i++){
            token = tokens.nextToken();
            values[d] =Double.parseDouble(token);
            d++;
        }

        return values;
    }


    /**
     * @return the outputPath
     */
    public String getOutputPath() {
        return outputPath;
    }


    public static void main(String[] args) throws Exception{
        java.util.Properties properties = new java.util.Properties();
        properties.load(new java.io.FileInputStream("spatial.properties"));

        DefaultStatisticCenter statisticCenter = new DefaultStatisticCenter();

        // internal variables
        // Leafnodes are 32+4 Bytes of size.
        // Indexnodes are 32+8 Bytes of size.
        // so take the maximum for the block size!
        //int blockSize = 4+2+(32+8)*maxcap;
        StarRTree rTree = new StarRTree(statisticCenter,"",
                properties.getProperty("experiment.dataSetFolder")+"/rtree",
                Integer.parseInt(properties.getProperty("rtree.dimensions")),
                Integer.parseInt(properties.getProperty("rtree.bufferSize")),
                Integer.parseInt(properties.getProperty("diskStorage.blockSize")),
                Integer.parseInt(properties.getProperty("rtree.minNodeEntries")),
                Integer.parseInt(properties.getProperty("rtree.maxNodeEntries")));

        rTree.open();

        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream("index.txt")));

        String line=input.readLine();
        double[] point;
        for(int i=1; line!=null; i++){
            if(!line.startsWith("#")){
                point = getValues(line, 2);
                DoublePointRectangle mbr = new DoublePointRectangle(point, point);
            
                rTree.insert(new KPE(mbr, i ,IntegerConverter.DEFAULT_INSTANCE));
            }
            
            line=input.readLine();
        }
        
        //Exemplo de busca
        
        double[] from = new double[]{0, 0};
        double[] to = new double[]{3, 3};
        System.out.println("Points between ("+from[0]+","+from[1]+") and ("+to[0]+","+to[1]+")...");
        Cursor cursor = rTree.query(new DoublePointRectangle(from, to));
        while(cursor.hasNext()){
            System.out.println(cursor.next());
        }
        
        System.out.println("\nTree...");
        System.out.println(rTree.toString());

        System.out.println("Statistics results...");
        System.out.println(statisticCenter.getStatus());
        input.close();
        rTree.close();
    }
}
