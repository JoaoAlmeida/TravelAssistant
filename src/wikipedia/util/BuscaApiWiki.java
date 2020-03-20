/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.LeitorWikiXML;
import util.ReadPage;
import util.Writer;

/**
 * Gera arquivo com texto da api do wikipédia a partir do id. args[1] - arquivo
 * com ids. args[2] - diretório para criação de arquivo (com id e texto e id e
 * imagem).
 *
 * @author fellipe
 */
public class BuscaApiWiki {

    public static void main(String[] args) {
        BuscaApiWiki.run(new File(args[0]), new File(args[1]));
    }

    public static void run(File fileId, File dir) {
        int i = 1;
        //dir.mkdirs();        
        File textWiki = new File(dir, "TextWiki.txt");
        File imgWiki = new File(dir, "Imgs.txt");
        Scanner input;
        try {
            System.out.println(fileId.toPath());
            input = new Scanner(fileId, "ISO-8859-1");

            while (input.hasNextLine()) {
                System.out.println(i++);
            
                    String line = input.nextLine();
                    String idWiki = line.split(" ")[1];
                    String id = line.split(" ")[0];
                    if (!line.contains("#") && !idWiki.contains("-")) {
                        String xml;
                        try {
                            xml = ReadPage.getContent("http://pt.wikipedia.org/w/api.php?action=query&prop=pageimages|extracts|langlinks&format=xml&pithumbsize=100&pageids=" + idWiki);
                            String text;
                            try {
                                text = LeitorWikiXML.findText(xml).replaceAll("\n", " ");
                                System.out.println(idWiki + " " + text);
                                Writer.writeFile(textWiki, id, text);

                            } catch (Exception ex) {

                                Logger.getLogger(BuscaApiWiki.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            try {
                                String img = LeitorWikiXML.findImg(xml);
                                if (!img.equals("1.0")) {
                                    Writer.writeFile(imgWiki, id, img);
                                    System.out.println(idWiki + " " + img);
                                }
                            } catch (Exception ex) {

                                Logger.getLogger(BuscaApiWiki.class.getName()).log(Level.SEVERE, null, ex);
                                System.out.println("id: " + idWiki);
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(BuscaApiWiki.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("id: " + idWiki);
                        }

                    }
                
            }
            input.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BuscaApiWiki.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
