/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author fellipe
 */
public class LeitorWikiXML {

    public static String findText(String xml) throws Exception {
        try{
        int x = 0;
        int y;
        x = xml.indexOf("<extract xml");
        y = xml.indexOf(">", x) + 1;
        x = xml.indexOf("</extract>", y);

        String text = EncoderUtil.convertUTF8toISO(xml.substring(y, x));
        text = text.replaceAll("&lt", "<");
        text = text.replaceAll(";", "");
        text = text.replaceAll("&gt", ">");
        return text;
        }catch(Exception ex){
            throw new Exception(ex);
        }
    }

    public static String findTitle(String xml) {
        
        int x = xml.indexOf("title=");
        int y = xml.indexOf("\"", x) + 1;
        x = xml.indexOf("\"", y);

        return EncoderUtil.convertUTF8toISO(xml.substring(y, x));

    }
    
    public static String findImg(String xml){
        try{
        int x = xml.indexOf("thumbnail");
        int y = xml.indexOf("\"", x) + 1;
        x = xml.indexOf("\"", y);
        
        return xml.substring(y, x);

        }catch(Exception ex){
            return "";
        }
    }

    
}
