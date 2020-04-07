/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package doutorado.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;

/**
 *
 * @author JoãoPaulo
 */
public class OSMFilesUtil {

    BufferedReader reader;

    public OSMFilesUtil(String sourceFile) throws UnsupportedEncodingException, FileNotFoundException {
        reader = new BufferedReader((new InputStreamReader(new FileInputStream(new File(sourceFile)), "ISO-8859-1")));
    }

    //Remove uma categoria do arquivo contendo os POIs
    public void removeCategoriadoTotal(String objetoInteresse) throws IOException {

        //Novo arquivo que será criado sem o objeto de interesse
        Writer file = new OutputStreamWriter(new FileOutputStream("./DatasetsOutput"
                + "/New York-" + objetoInteresse + ".txt"), "ISO-8859-1");

        //Leitura do arquivo fonte contendo todos os objetos
        String line = reader.readLine();

        String subCategoria;

        int fim = line.indexOf(')');
        int inicioSub = 0, fimSub = 0;

        if (fim != -1) {
            inicioSub = line.indexOf('(', fim + 1);
            fimSub = line.indexOf(')', inicioSub + 1);
        }
        while (fim == -1) {

            file.write(line + "\n");
            file.flush();

            line = reader.readLine();
            fim = line.indexOf(')');
            inicioSub = line.indexOf('(', fim + 1);
            fimSub = line.indexOf(')', inicioSub + 1);
        }

        subCategoria = line.substring(inicioSub + 1, fimSub);

        //Armazena o nome da nova categoria a ser separada        
        while (subCategoria.equals(objetoInteresse)) {

            line = reader.readLine();

            fim = line.indexOf(')');
            inicioSub = line.indexOf('(', fim + 1);
            fimSub = line.indexOf(')', inicioSub + 1);

            subCategoria = line.substring(inicioSub + 1, fimSub);
        }

        //Adiciona o objeto a esta categoria
        file.write(line + "\n");
        file.flush();

        line = reader.readLine();

        while (line != null) {

            fim = line.indexOf(')');

            inicioSub = line.indexOf('(', fim + 1);
            fimSub = line.indexOf(')', inicioSub + 1);

            if (inicioSub < fimSub) {

                subCategoria = line.substring(inicioSub + 1, fimSub);

                while (subCategoria.equals(objetoInteresse)) {

                    line = reader.readLine();

                    int inicio = line.indexOf('(');

                    if (inicio < 0) {
                        break;
                    }

                    fim = line.indexOf(')');

                    if (fim < 0) {
                        break;
                    }
                    inicioSub = line.indexOf('(', fim + 1);
                    fimSub = line.indexOf(')', inicioSub + 1);

                    subCategoria = line.substring(inicioSub + 1, fimSub);
                }

                file.write(line + "\n");
                file.flush();

                line = reader.readLine();
            } else {

                file.write(line + "\n");
                file.flush();
                line = reader.readLine();

            }
        }
        file.close();
        reader.close();
    }

    //Cria arquivos contendo apenas POIs que possuem a quantidade de categorias definida no parametro
    public void divideSubCategoriasPerta(int quantidadeObjetos) throws IOException {

        String line = reader.readLine();
        ArrayList<Categoria> ConjuntoCategorias = new ArrayList();
        Categoria categoriaAux = new Categoria();

        int inicio = line.indexOf('(');

        //ignora as linhas que não possuem categoria
        while (inicio < 0) {
            line = reader.readLine();
            inicio = line.indexOf('(');
        }

        String subCategoria;

        int fim = line.indexOf(')');
        int inicioSub = line.indexOf('(', fim + 1);
        int fimSub = line.indexOf(')', inicioSub + 1);

        subCategoria = line.substring(inicioSub + 1, fimSub);
        
        //Armazena o nome da nova categoria a ser separada
        categoriaAux.setNome(subCategoria);

        //Adiciona o objeto a esta categoria
        categoriaAux.addObjeto(line);
        ConjuntoCategorias.add(categoriaAux);

        line = reader.readLine();

        boolean categoriaExists = false;

        while (line != null) {

            String[] teste = line.split("\t");

            //apenas as linhas que possuem categoria
            if (!teste[3].equals("-")) {

                fim = line.indexOf(')');

                inicioSub = line.indexOf('(', fim + 1);
                fimSub = line.indexOf(')', inicioSub + 1);

                subCategoria = line.substring(inicioSub + 1, fimSub);

                //Compara a categoria da linha lida com a ultima categoria armazenada na ArrayList
                for (int a = 0; a < ConjuntoCategorias.size(); a++) {
                    if (subCategoria.equals(ConjuntoCategorias.get(a).getNome())) {

                        ConjuntoCategorias.get(a).addObjeto(line);

                        categoriaExists = true;

                        line = reader.readLine();

                        break;
                    }
                }
                if (!categoriaExists) {

                    categoriaAux = new Categoria();

                    categoriaAux.setNome(subCategoria);

                    //Adiciona um objeto a este conjunto de categoria
                    categoriaAux.addObjeto(line);

                    ConjuntoCategorias.add(categoriaAux);

                    line = reader.readLine();
                }
                categoriaExists = false;
            } else {
                line = reader.readLine();
            }
        }
        escreveArquivo(ConjuntoCategorias, quantidadeObjetos);
    }

    public void escreveArquivo(ArrayList<Categoria> category, int quantidadeLimite) throws UnsupportedEncodingException, FileNotFoundException, IOException {

        for (int a = 0; a < category.size(); a++) {

            ArrayList<String> objetos = new ArrayList<String>();
            objetos = category.get(a).getObjetos();

            //só irá criar arquivos para categorias que possuam mais de 5 objetos
            if (objetos.size() > quantidadeLimite) {

                String nomeCategoria = category.get(a).getNome().trim();
                nomeCategoria = nomeCategoria.length() > 23 ? category.get(a).getNome().substring(0, 22) : nomeCategoria;
                Writer file;
                int inc = 0;
                try {
                    file = new OutputStreamWriter(new FileOutputStream("./DatasetsOutput/object of interest/" + nomeCategoria + ".txt"), "ISO-8859-1");
                } catch (java.io.FileNotFoundException e) {
                    System.out.println("Entrou no erro!");
                    file = new OutputStreamWriter(new FileOutputStream("./DatasetsOutput/object of interest/null" + inc + ".txt"), "ISO-8859-1");
                    inc++;
                };
                for (int b = 0; b < objetos.size(); b++) {
                    System.out.println(objetos.get(b).toString());

                    file.write(objetos.get(b) + "\n");

                    file.flush();


                }
                file.close();
                reader.close();
            }
        }
    }

    //limpa os arquivos de objetos sem descrição alguma
    static public void cleanFile(String filePath) throws FileNotFoundException, UnsupportedEncodingException, IOException {

        BufferedReader readerAux = new BufferedReader((new InputStreamReader(new FileInputStream(new File(filePath)), "ISO-8859-1")));

        Writer fileWrt = new OutputStreamWriter(new FileOutputStream("DatasetsOutput/"
                + "[cleaned] " + filePath), "ISO-8859-1");

        String line = readerAux.readLine();

        while (line != null) {
            System.out.println(line);

            if (line.indexOf('?') == -1) {
                fileWrt.write(line + "\n");
            }
            line = readerAux.readLine();
        }
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {

        //File which categories will be extracted
        OSMFilesUtil category = new OSMFilesUtil("San Francisco.txt");
        OSMFilesUtil.cleanFile("hotel.txt");
//        category.divideSubCategoriasPerta(5);

//        category.removeCategoriadoTotal("hotel");       
    }
}
