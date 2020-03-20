/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package datasets;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.StringTokenizer;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import util.Util;
import util.experiment.Experiment;
import util.experiment.ExperimentException;
import util.experiment.ExperimentResult;
import util.experiment.StringExperimentResult;

/**
 *
 * @author joao
 */
public class OpenStreetMapParser implements Experiment{
    private final String osmFile;
    private final String datasetDirectory;
    private final String ignoreNodes;
    private final String ignoreTags;
    private final int idsCacheSize;
    private final int networkCacheSize;
    private final int verticesCacheSize;

    public OpenStreetMapParser(String osmFile, String datasetDirectory, int idsCacheSize,
            int networkCacheSize, int verticesCacheSize, String ignoreNodes, String ignoreTags){
        this.osmFile = osmFile;
        this.datasetDirectory = datasetDirectory;
        this.ignoreNodes = ignoreNodes;
        this.ignoreTags = ignoreTags;
        this.idsCacheSize = idsCacheSize;
        this.networkCacheSize = networkCacheSize;
        this.verticesCacheSize = verticesCacheSize;
    }

    public void open() throws ExperimentException {
        //no op.
    }

    public void run() throws ExperimentException {
        try{

            HashSet<String> ignoreNodesSet = new HashSet<String>();
            StringTokenizer tokens = new StringTokenizer(ignoreNodes," ,;");
            while(tokens.hasMoreTokens()){
                ignoreNodesSet.add(tokens.nextToken());
            }
            
            HashSet<String> ignoreTagSet = new HashSet<String>();
            tokens = new StringTokenizer(ignoreTags," ,;");
            while(tokens.hasMoreTokens()){
                ignoreTagSet.add(tokens.nextToken());
            }

            OpenStreetMapHandler handler = new OpenStreetMapHandler(datasetDirectory, 
                    idsCacheSize, networkCacheSize, verticesCacheSize,
                    ignoreNodesSet, ignoreTagSet, true, false);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            System.out.print("Starts parsing " + osmFile + " file..");

            
            long time = System.currentTimeMillis();

            InputStream input = null;
            if(osmFile.endsWith(".osm")){
                input = new BufferedInputStream(new FileInputStream(osmFile));
                saxParser.parse(input, handler);
            }else if(osmFile.endsWith(".bz2")){
                Process p = Runtime.getRuntime().exec("bzcat "+osmFile);
                input = new BufferedInputStream(p.getInputStream());
                saxParser.parse(input, handler);
            }else{
                throw new RuntimeException("File '"+osmFile+"' format not supported!");
            }
            input.close();

            System.out.println(", concluded in " + Util.time(System.currentTimeMillis() - time) + ".");
        }catch(Exception e){
            throw new ExperimentException(e);
        }
    }

    public void close() throws ExperimentException {
        //no op.
    }

    public ExperimentResult[] getResult() {
    	return new ExperimentResult[] {new StringExperimentResult(1,
    			"OpenStreetMapParser executed on file : "+ osmFile)};
    }

}
