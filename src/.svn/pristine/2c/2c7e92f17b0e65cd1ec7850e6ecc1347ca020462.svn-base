
package wikipedia.util;


public class TextClean {

    public static String cleanTags(String text){
        int x = 0, y = 0;
        String aux = "";
        
        
        while(text.indexOf("<", x) != -1 && text.indexOf(">", x) > text.indexOf("<", x) 
                && text.indexOf(">", text.indexOf("<", x)) - text.indexOf("<", x) < 30 
                && text.indexOf("</", x) != -1){
            
            y = text.indexOf("<", x);
            aux += text.substring(x, y);
            y = text.indexOf("</", y);
            x = text.indexOf(">", y) + 1;
            
        }
    
    aux += text.substring(x, text.length());
    aux = aux.replaceAll("  ", " ");
    return aux;
    }
    
    
    
    
}
