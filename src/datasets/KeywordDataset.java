/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package datasets;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import util.Util;
/**
 * This class receives a tweets file and a object.txt (e.g., australia.txt)
 * and combines them to create keyword dataset. There are two parameters the
 * start line at the tweetfile(gap) and the number of aggregated tweets (2, 3, ..).
 * @author joao
 */
public class KeywordDataset{
    private final String objectFile;
    private final String tweetFile;
    private final String outputFile;
    private final int gap;
    private final int numTweets;

    public KeywordDataset(String objectFile, String tweetFile, int gap,
            int numTweets, String outputFile){
       this.gap = gap;
       this.numTweets = numTweets;
       this.objectFile = objectFile;
       this.tweetFile = tweetFile;
       this.outputFile = outputFile;
    }
    
    public void run() throws FileNotFoundException, IOException{
        LineNumberReader tweets = new LineNumberReader(new FileReader(tweetFile));
        LineNumberReader realObjects = new LineNumberReader(new FileReader(objectFile));
        PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true)), true);

        System.out.println("Creating dataset '"+outputFile+"' from datasets '"+
                objectFile+"' and '"+tweetFile+"', gap="+gap+"...");
        long time = System.currentTimeMillis();
        for(int i=0;i<gap;i++){
            tweets.readLine();
        }
        int objectId, pos;
        double lat, lgt;
        StringBuilder tweet;
        String line;
        while((line=realObjects.readLine()) != null){
             //parseLine
            try{
                objectId = Integer.parseInt(line.substring(0, (pos = line.indexOf(' '))));
                pos = skip(line, pos, 1); //skip the original object id.
                lat = Double.parseDouble(line.substring(pos+1,(pos = line.indexOf(' ',pos+1))));
                lgt = Double.parseDouble(line.substring(pos+1,(pos = line.indexOf(' ',pos+1))));
            } catch (NumberFormatException e){
                System.out.println("Invalid line='"+line+"'!!!");
                continue; //read the next line
            }
            tweet = new StringBuilder();
            for(int i=0;i<numTweets;i++){
                pos=0;
                line = tweets.readLine();
                pos = skip(line, pos, 4); //skip  id, lat, lgt, and tweet id (user)
                tweet.append(line.substring(pos+1)); //tweet message
                tweet.append(' ');
            }
            printSpatialObject(output, objectId, lat, lgt, tweet.toString());
        }

        output.close();
        tweets.close();
        realObjects.close();
        System.out.println("Dataset created in "+ Util.time(System.currentTimeMillis()-time));

    }

    private int skip(String line, int pos, int size){
        for(int i=0;i<size;i++){
            pos = line.indexOf(' ',pos+1);
        }
        return pos;
    }

    private void printSpatialObject(PrintWriter output, int id, double lat,
            double lgt, String msg){
        output.print(id);
        output.print(" - "); //No, original id
        output.print(lat);
        output.print(' ');
        output.print(lgt);
        output.print(' ');
        output.print(msg.replace('\n', ' ').replace('\r', ' '));
        output.println();
    }

    public static void main(String[] args) throws Exception{
        String sourceDataset = "./australia/object.txt";
        String tweetDataset = "tweets";
        int numDataSets = 5;
        int souceDatasetLines = 70064;
        if(args.length==4){
            sourceDataset=args[0];
            souceDatasetLines = Integer.parseInt(args[1]);
            tweetDataset = args[2];
            numDataSets = Integer.parseInt(args[3]);
        }else{
            System.out.println("Usage: KeywordDataset [sourceDataset] [sourceDatasetLines] [tweetDataset] [numDatasets]");
            System.out.println("\nRunning with default parameters sourceDataset="+
                    sourceDataset+", sourceDatasetLines="+souceDatasetLines+
                    ", tweetDataset="+tweetDataset+", numDatasets="+numDataSets);
        }
                        
        
        int skip = 0;
        for(int i=1;i<=numDataSets;i++){
            new KeywordDataset(sourceDataset, tweetDataset, skip, i, "keywordDataset_"+i+".txt").run();
            skip+=i*souceDatasetLines;
        }
    }
}
