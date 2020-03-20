
package wiktionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import org.xml.sax.SAXException;
import util.Writer;

/**
 *
 * @author fellipe
 */
public class FindNouns {

    private FileReader nouns;
    private FileReader nounsFinal;
    private File nounsFinalFile;
    
    private HashMap<String, String> nounsHash;
    private HashMap<String, String> nounsFinalHash;

    public FindNouns(FileReader diretorio) {
        nounsHash = new HashMap<>();
        nounsFinalHash = new HashMap<>();
    }

    public FindNouns() throws FileNotFoundException {
        nouns = new FileReader("sinonimos.txt");
        nounsFinal = new FileReader("sinonimosfinal.txt");
        nounsFinalFile = new File("sinonimos_Final.txt");
        nounsHash = new HashMap<>();
        nounsFinalHash = new HashMap<>();
    }

    public void readFiles() throws IOException {
        BufferedReader input = new BufferedReader(nouns);

        while (input.ready()) {
            String aux = input.readLine();
            fillHash(nounsHash, aux);
        }

        input = new BufferedReader(nounsFinal);

        while (input.ready()) {
            String aux = input.readLine();
            fillHash(nounsFinalHash, aux);
        }
    }

    public HashMap<String, String> fillHash(HashMap<String, String> nounsHash, String str) {
        String[] aux = separateString(str);
        if (aux != null && !aux[1].equals("|")) {
            nounsHash.put(aux[0], aux[1]);
        }
        return nounsHash;        
    }

    public String[] separateString(String str) {
        String[] aux = new String[2];
        int x = 0;
        int y = 0;
        if (str.contains("|")) {
            y = str.indexOf("|");
            aux[0] = str.substring(x, y);
            if(aux[0].contains(" ")){
                aux[0] = aux[0].substring(0, aux[0].lastIndexOf(" "));
            }
            aux[1] = str.substring(y, str.length());
        } else {
            return null;
        }

        return aux;
    }
    /*Procura se uma palavra está entre os termos da outra, e faz o processo inverso
     * para conriderá-las como sinônimos;
     */
    private void find() {
        Set<String> set = nounsHash.keySet();
        //Percorre todas as chaves.
        for(String key: set){
            
            String value = nounsHash.get(key);
            String[] values = value.split("\\|");
             /*Percorre os termos da chave, e procura entre esses termos, palavras
              * que cont�m a chave entre seus termos.
              */
            for(String x : values){
                if(!x.equals("") && !x.equals(key)){
                    if(nounsHash.containsKey(x)){
                        findSynonymous(nounsHash, x, key);
                    }
                }
            }
        }
    }
    
    private void findSynonymous(HashMap<String, String> nounsHash, String key, String value) {
        String[] values = nounsHash.get(key).split("\\|");
        for(String x : values){
            if (!x.equals("") && x.equals(value)) {
                
                if (nounsFinalHash.containsKey(key)) {
                    String tmp = nounsFinalHash.get(key);
                    if (!tmp.contains("|" + value + "|")) {
                        String aux = tmp + value + "|";
                        nounsFinalHash.put(key, aux);
                    }
                } else {
                    nounsFinalHash.put(key, "|" + value + "|");
                }
                if (nounsFinalHash.containsKey(value)) {
                    String tmp = nounsFinalHash.get(value);
                    if(!tmp.contains("|" + key + "|")){
                        String aux = tmp + key + "|";
                        nounsFinalHash.put(value, aux);
                    }
                } else {
                    nounsFinalHash.put(value, "|" + key + "|");
                }
                return;
            }
        }
    }

    
    public static void main(String[] args) throws FileNotFoundException, IOException, SAXException {
        //File diretorio = null;
        FindNouns fn;
        //try {
        // diretorio = new File(args[1]);
        //fn = new FindNouns(diretorio);
        //} catch (ArrayIndexOutOfBoundsException e) {
        fn = new FindNouns();
        fn.readFiles();
        fn.find();
        fn.write();
        //}

    }

    
    private void write() throws SAXException, FileNotFoundException, IOException {
        Set<String> set = nounsFinalHash.keySet();
        for(String key: set){
            String value = nounsFinalHash.get(key);
            ContentExtractor ce = new ContentExtractor();
            Writer.writeFile(nounsFinalFile, key, value);
        
        }
    }
    
}
