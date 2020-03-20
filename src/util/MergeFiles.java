
package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.xml.sax.SAXException;
import wiktionary.ContentExtractor;

/**
 * This class makes a merge between two files.
 * 
 * Example of use:
 * 
 * MergeFiles.merge("file1.txt", "file2.txt", "mergefile.txt");
 * 
 * @author fellipe
 */
public class MergeFiles {

    MergeFiles() {
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, SAXException {
        

        MergeFiles.merge(new FileReader(args[0]),
                new FileReader(args[1]), new File(args[2]));

    }

    /**
     * The method makes marge between two files creating a third file with that
     * merge.
     * 
     * @param file1 file to be done  merge.
     * @param file2 file to be done  merge.
     * @param merge file generated.
     * 
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SAXException
     */
    public static void merge(FileReader file1, FileReader file2, File merge) throws FileNotFoundException, IOException, SAXException {
        HashSet<String> set1 = new HashSet<>();

        ready(file1, set1);
        ready(file2, set1);
        writeFile(merge, set1);

    }

    private static void writeFile(File file, Set<String> set) throws SAXException, IOException {
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String x = it.next();
            if (x.length() > 1) {
                Writer.writeFile(file, x);
            }
        }
    }

    private static void ready(FileReader file, Set<String> s) throws IOException {
        BufferedReader input = new BufferedReader(file);

        while (input.ready()) {
            s.add(input.readLine());
        }
    }
}
