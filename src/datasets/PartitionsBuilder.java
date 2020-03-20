/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package datasets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import util.Util;
import util.file.ColumnFileException;
import util.file.IntegerEntry;
import util.file.ListStorage;

/**
 *
 * @author joao
 */
public class PartitionsBuilder {
     private static final long STATUS_TIME= 4*60*1000;

     public static void createPartitions(String prefix, String partitionFileName,
             ListStorage<IntegerEntry> adjacencies) throws ColumnFileException,
             FileNotFoundException, IOException{

        long statusWatch = System.currentTimeMillis();
        long time = statusWatch;
        System.out.println("Creating partitions... "+new Date());

        PrintWriter output= new PrintWriter(new BufferedWriter(new FileWriter(prefix+"/"+partitionFileName, true)), true);
        //vertexId -> partitionID
        int partionId=1;
        HashMap<Integer,Integer> partitions = new HashMap<Integer, Integer>();
        Integer partion;
        Iterator<Integer> idsIterator = adjacencies.getIdsIterator();
        int vertexId;
        while(idsIterator.hasNext()){
            vertexId = idsIterator.next();
            partion = partitions.get(vertexId);
            if(partion == null){
                int groupCount=0;
                LinkedList<Integer> group = new LinkedList<Integer>();
                group.add(vertexId);
                int groupVertex;
                while(!group.isEmpty()){
                    groupVertex = group.removeFirst();

                    if(!partitions.containsKey(groupVertex)){
                        partitions.put(groupVertex, partionId);
                        output.print("P"+partionId+" "+groupVertex);
                        output.println();
                        groupCount++;

                        for(IntegerEntry vertex:adjacencies.getList(groupVertex)){
                            if(!partitions.containsKey(vertex.getValue())){
                                group.add(vertex.getValue());
                            }
                        }
                    }else{
                        if(partitions.get(groupVertex)!=partionId){
                            throw new RuntimeException("The vertex="+groupVertex+
                            " is already assigned to partion="+partitions.get(groupVertex)+"!!!");
                        }
                    }

                    if((System.currentTimeMillis()-statusWatch)>STATUS_TIME){
                        statusWatch = System.currentTimeMillis();
                        System.out.print("[partitionId="+partionId+",size="+groupCount+"] ");
                    }
                }
                //System.out.println("Partition P"+partionId+" has "+groupCount+" entries.");
                partionId++;
            }
        }
        output.close();
        System.out.println("\nPartitions created in "+Util.time(System.currentTimeMillis()-time));
    }

    private static void buildAdjacencies(ListStorage<IntegerEntry> adjacencies, String networkFile) throws FileNotFoundException, IOException, ColumnFileException{
        LineNumberReader input = new LineNumberReader(new BufferedReader(new FileReader(networkFile)));
        long statusWatch = System.currentTimeMillis();
        long time = statusWatch;
        System.out.println("Building adjacencies... "+new Date());

        int startVertexId;
        int endVertexId;
        String line;
        int pos;        
        for(int id=1; (line=input.readLine())!=null ;id++){
            pos = line.indexOf(' ');
            startVertexId = Integer.parseInt(line.substring(pos+1,(pos = line.indexOf(' ',pos+1))));
            endVertexId = Integer.parseInt(line.substring(pos+1,(pos = line.indexOf(' ',pos+1))));

            adjacencies.addEntry(startVertexId, new IntegerEntry(endVertexId), false);
            adjacencies.addEntry(endVertexId, new IntegerEntry(startVertexId), true);

            if((System.currentTimeMillis()-statusWatch)>STATUS_TIME){
                statusWatch = System.currentTimeMillis();
                System.out.print("["+id+"] ");                
            }
        }
        input.close();
        System.out.println("\nAdjacencies built in "+Util.time(System.currentTimeMillis()-time));
    }

    public static void main(String[] args) throws Exception{
        String prefix = args[0]; //ex: trondheim

        ListStorage<IntegerEntry> adjacencies = new ListStorage<IntegerEntry>(
                null, null, prefix+"/adjacencies", IntegerEntry.SIZE, IntegerEntry.FACTORY);
        adjacencies.open();

        buildAdjacencies(adjacencies, prefix +"/"+ OpenStreetMapHandler.NETWORK_FILENAME);

        createPartitions(prefix, OpenStreetMapHandler.PARTITION_FILENAME, adjacencies);

        System.out.print("Deleting adjacencies file..."+new Date());
        adjacencies.close();
        adjacencies.delete();
        System.out.println(", finished!");
    }
}
