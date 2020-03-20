/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia;

/**
 *
 * @author fellipe
 */

import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

public class WikipediaParser extends DefaultHandler{
  private StringBuffer textBuffer = null;
  private int contapagina = 0;
  private int contacoord = 0;
  private String salvapagina = null; 
  private File arquivo = new File("/home/fellipe/test.xml");        
  private WikipediaParser parser;
 
  
  public void startDocument() throws SAXException{
   
    System.out.println("startDocument");
    
    try{
       arquivo.createNewFile();
    }catch(IOException e){
       e.printStackTrace();
    }  
    parser = new WikipediaParser();
    parser.escreverArquivo("<?xml version=\"1.0\"?>\n<wiki>\n", false);
  }
  
  
  public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException{
   
    if(salvapagina == null){
       salvapagina = "<" + eName(sName, qName);
    }else{   
       salvapagina += "<" + eName(sName, qName);
    } 
    if(attrs != null){
      for(int i=0; i< attrs.getLength(); i++){
        salvapagina += " "+attrs.getLocalName(i)+
                         "=\""+attrs.getValue(i)+"\"";
      }
    }
    salvapagina += ">";
   
    if(eName(sName,qName).equals("page")){
    contapagina +=1;
    }
  }

  public void characters(char buf[], int offset, int len) throws SAXException{
    if(textBuffer==null){
      textBuffer = new StringBuffer();
    }
    textBuffer.append(buf, offset, len);
    
  }

  public void endElement(String namespaceURI, String sName, String qName) throws SAXException{

    salvapagina += textBuffer.toString().trim()+"</"+eName(sName, qName)+">\n";
    textBuffer = null;
    
    if(salvapagina.contains("</page>")){
    salvapagina = salvapagina.replaceAll("\n", " ");    
        System.out.println("*****" + contapagina + "a pagina*****");   
        if(salvapagina.contains("{{geocoordenadas") || salvapagina.contains("{{geocoordenadas")
           || salvapagina.contains("{{coord") || salvapagina.contains("{{Coord")
           || salvapagina.contains("|latitude") || salvapagina.contains("| latitude")
           || salvapagina.contains("latP") || salvapagina.contains("lat_")){
          
           contacoord += 1;
           System.out.println( + contacoord + "a pagina com coordenada");
        //   parser.escreverArquivo(salvapagina, true);
            
        }
    System.out.println(salvapagina);
    salvapagina = null;    
    }
    
  }
  private String eName(String sName, String qName){
    return "".equals(sName)?qName:sName;
  }

    @Override
  public void endDocument() throws SAXException{
    System.out.println("endDocument");
    System.out.println(contapagina + " paginas");
    System.out.println(contacoord + " coordenadas");
    
    parser.escreverArquivo("</wiki>", true);
  }

  public void escreverArquivo(String escreve, boolean x){
     
    try{
        FileWriter fileWriter = new FileWriter(arquivo, x);    
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println(escreve);
        printWriter.close();
    }catch(IOException e){
       System.out.println(e);
    }       

  }  
  public static void main(String[] args) throws Exception{
    WikipediaParser handler = new WikipediaParser();
    SAXParserFactory factory = SAXParserFactory.newInstance();
    
    SAXParser saxParser = factory.newSAXParser();
    saxParser.parse(args[0], handler);
    
 
  }
}
