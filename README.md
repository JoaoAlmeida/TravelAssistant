# TravelAssistant - A Parser for OpenStreetMap OSM files
 
 ## How to use it
 
 ### Parsing
First, edit the file name in **args** attribute in the class **datasets.OpenStreetMapObjectHandler**. It parses the OSM file and generates the object and points files in the OSMParsing folder. Then, edit the file name in **addrFile** attribute and execute the class **Osm.java**. This step generates the output file, **New York.txt** is an example of an output file (root folder). If you need to modify how information is presented in the output file, you may edit the method **readFile()** in **Osm.java** class.

### OsmFilesUtil.java
This class contains some utilities to manipulate and organize the POIs in the output file:

- cleanFile() removes POIs without textual description
- divideSubCategoriasPerta(int quantidadeObjetos) creates files containing POIs of the same sub-category (information in the second parentheses). This method creates files only for subcategories that contain at least *quantidadeObjetos* number of POIs
- removeCategoriadoTotal(String objetoInteresse) removes all POIs of the specified *objetoInteresse* category
