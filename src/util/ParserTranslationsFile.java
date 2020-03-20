/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;

/**
 *
 * @author fellipe
 */
public class ParserTranslationsFile {
    
    public static void main(String[] args){
        ParserTranslationsFile p = new ParserTranslationsFile();
        try {        
            p.run("categorias.txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ParserTranslationsFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ParserTranslationsFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ParserTranslationsFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    public void run(String file) throws FileNotFoundException, SAXException, IOException{
        Scanner input = new Scanner(new File(file));
        
        while (input.hasNextLine()){
            File newFile = new File("categories.txt");
            String line = input.nextLine();
            int x = line.indexOf("\"") + 1;
            int y = line.indexOf("\"", x);
            String cat = line.substring(x, y);
            x = line.indexOf("\"", y+1) + 1;
            y = line.indexOf("\"", x);
            String translation = line.substring(x,y);
            Writer.writeFile(newFile, cat + "|" + translation);
        }
    }
}
