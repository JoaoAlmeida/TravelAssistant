# TravelAssistant - A Parser for OpenStreetMap OSM files
 
 ## How to use it
 
 ### Parsing
First, edit the file name in **args** attribute in the class **datasets.OpenStreetMapObjectHandler**. It parses the OSM file and generates the object and points files into the OSMParsing folder. Then, edit the file name in **addrFile** attribute and execute the class **Osm.java**. This step generates the output file, **New York.txt** is an example of an output file (root folder). If you need to modify how information is presented in the output file, you may edit the method **readFile()** in **Osm.java** class.

### OsmFilesUtil.java
This class contains some utilities to manipulate and organize the POIs in the output file:

- cleanFile() removes POIs without textual description
- divideSubCategoriasPerta(int quantidadeObjetos) creates files containing POIs of the same sub-category (information in the second parentheses). This method creates files only for subcategories that contain at least *quantidadeObjetos* number of POIs
- removeCategoriadoTotal(String objetoInteresse) removes all POIs of the specified *objetoInteresse* category

## Authors
This code was inittialy created inside the PerTA (Personal Travel Assistant) project: http://sites.ecomp.uefs.br/perta/. These authors contributed with the code:

- Thiago Sampaio Lima: http://lattes.cnpq.br/6103257120721546
- Fellipe de Lima Fonseca: http://lattes.cnpq.br/6180854801583203
- João Paulo Dias de Almeida: http://lattes.cnpq.br/3029122677763319 
- João B. Rocha-Junior: https://sites.google.com/a/ecomp.uefs.br/joao/home

