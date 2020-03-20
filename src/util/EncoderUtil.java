/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import org.xml.sax.SAXException;

/**
 *
 * @author fellipe
 */
public class EncoderUtil {
    
    public static void main(String[] args) throws FileNotFoundException, SAXException, IOException{
        
        String nameDir = args[0].substring(0, args[0].indexOf("."));
        EncoderUtil e = new EncoderUtil();
        e.convertFileUTF8toISO(args[0], nameDir);
    }
    
    public void convertFileUTF8toISO(String nameFile, String nameDir) throws FileNotFoundException, SAXException, IOException{
        Scanner input = new Scanner(new File(nameFile));
        File dir = new File(nameDir);
        dir.mkdirs();
        String nameFileISO = nameDir+"/fileISO.txt";
        File fileISO = new File(nameFileISO);
        while (input.hasNext()) {
            String line = input.nextLine();
       
            String lineISO = convertUTF8toISO(line);            
            Writer.writeFile(fileISO, lineISO);            
       
        }
    }
    
    public static String convertUTF8toISO(String str) {
        String ret = null;
        try {
            ret = new String(str.getBytes("ISO-8859-1"), "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return ret;
    }
    
    public static String convertISOtoUTF8(String str) {
        String ret = null;
        try {
            ret = new String(str.getBytes("UTF-8"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return ret;
    }
}
