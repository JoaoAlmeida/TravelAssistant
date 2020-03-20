/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import framework.SpatialObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import util.file.BlockColumnFileImpl;
import util.file.ColumnFileException;

/**
 *
 * @author fellipe
 */
public class GenerateFileByte {
    private final String prefixFile;
    private final File file;
    public static final int BLOCKSPERFILE = 1000000000;    
    private static final String fileObjectName = "objects2";
    
    public GenerateFileByte(String prefix){
        prefixFile = prefix;        
        file = new File(prefixFile+ "/" + fileObjectName);
    }
    
    public static void main(String[] args) throws FileNotFoundException, ColumnFileException, IOException{
        //args[0] = archive Full, args[1]= way where the file will are created.
        GenerateFileByte gfb = new GenerateFileByte(args[0]);
        gfb.generateObjectFile();
    }

    public void generateObjectFile() throws FileNotFoundException, ColumnFileException, IOException {
        Scanner input = new Scanner(new File("/home/tomcat/files2/Full.txt"), "ISO-8859-1");
        BlockColumnFileImpl bcf = new BlockColumnFileImpl(this.file, SpatialObject.BLOCKSIZE, BLOCKSPERFILE);
        HashMap<Long, SpatialObject> objects;
        objects = new HashMap<Long, SpatialObject>();
        while (input.hasNext()) {
            String line = input.nextLine();
            if (!line.contains("# id ")) {
                String[] split = separateFullLine(line);
                SpatialObject ob;

                
                if (split[5].equals("-")) {
                  //  split[5] = "";
                }

                ob = new SpatialObject(Long.parseLong(split[0]), split[1], split[2],
                        Double.parseDouble(split[3]), Double.parseDouble(split[4]), "&lt;sem título&gt;", split[5], "categoria não informada", "");
                
                if(ob.getId() == Long.parseLong("9305")){
                    System.out.println("pause!");
                }
                //if (!split[7].equals("-")) {
                    ob.setName(split[7]);
                //}
                if (!split[6].equals("-")) {
                    ob.setSubCategory(split[6]);
                }
                objects.put(Long.parseLong(split[0]), ob);
                byte[] bytes = ob.toByteArray();
                bcf.insert((int) ob.getId(), bytes);
            }
        }
        System.out.println("carregou arquivos!");
        input.close();
        input = new Scanner(new File("/home/tomcat/files2/Text.txt"), "ISO-8859-1");


        while (input.hasNext()) {
            String line = input.nextLine();
            if (!line.contains("# id ")) {

                String[] aux = wikipedia.util.WikipediaFiles.separateWiki(line);

                if (aux != null) {
                    try {
                        long id = Long.parseLong(aux[0]);

                        SpatialObject ob = objects.get(id);
                        if (ob != null) {

                            String[] split = aux[1].split(" ", 30);
                            String text = "";
                            int i = 0;

                            for (String x : split) {
                                if (i < 30) {
                                    if (x.length() > 40) {
                                        text += "...";
                                    } else {
                                        text += x + " ";
                                        i++;
                                    }
                                }
                            }

                            if (text.contains("==")) {
                                text = text.substring(0, text.indexOf("=="));
                            }
                            ob.setText(text);
                            //if (ob.getText().equals("-")) {
                                ob.setText(ob.getText().replaceAll("#", ""));
                            //}
                            byte[] bytes = ob.toByteArray();
                            bcf.insert((int) ob.getId(), bytes);
                            
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println("linha: " + line);
                    }
                }
            }
        }
    
    
    }
    
    
    public static String[] separateFullLine(String line) {
        String[] split = new String[8];
        int x = 0, y = 0;
        y = line.indexOf(" ");
        split[0] = line.substring(x, y);
        x = y + 1;
        y = line.indexOf(" ", x);
        split[1] = line.substring(x, y);
        x = y + 1;
        y = line.indexOf(" ", x);
        split[2] = line.substring(x, y);
        x = y + 1;
        y = line.indexOf(" ", x);
        split[3] = line.substring(x, y);
        x = y + 1;
        y = line.indexOf(" ", x);
        split[4] = line.substring(x, y);
        x = y + 1;
        if (line.indexOf("(") != -1 && line.indexOf("(") - y < 2) {
            y = line.indexOf(")", x);
            split[5] = line.substring(x, y);
            split[5] = TextClean.clean(split[5]);
            x = y + 1;

            y = line.indexOf(")", x);
            split[6] = line.substring(x, y);
            split[6] = TextClean.clean(split[6]);
            x = y + 2;
        } else {
            y = line.indexOf(" ", x);
            split[5] = line.substring(x, y);
            x = y + 1;
            split[6] = "-";
        }

        split[7] = line.substring(x, line.length());
        return split;
    }
}
