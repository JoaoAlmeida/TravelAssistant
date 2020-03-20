
package util;


public class TextClean {


public static final String REGEX = "[\\s,:/.;!\"'#$%&?\\+@\\*{} \\| \\/ \\- = \\[ \\] \\( \\) \\d]+";

    public static void main(String[] args){
        String line = "eu estou , testando";
        String text = clean(line);
        
            System.out.print(text + " ");
        
    }
    
    public static String clean(String text){
        if(text == null){
            return null;
        }
        String[] aux = text.split(REGEX);        
        StringBuilder result = new StringBuilder();
        int i = 1;
        for(String x : aux){
            if(!"".equals(x)){                
                result.append(x);
                if(i!=aux.length){
                result.append(" ");
                }
                
            }
            i++;
        }
        return result.toString();
    }
    
}
