package framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import util.EncoderUtil;
import util.SpatialUtils;



/**
 *
 * @author joao
 */
public class SpatialObject implements Comparable<Object>{
    public static final int BLOCKSIZE = 600;
    private Long id;
    private String name;    
    private String idWiki;
    private String idOsm;
    private Double latitude;
    private Double longitude;
    private String text;
    private String category;        
    private String subCategory;
    private String img;
    private double distance = 0;
    
    public SpatialObject(Long id, String idW, String idO, double lat, double lgt, String name, String cat, String subCategory, String text){
        this.id = id;
        this.latitude = lat;
        this.longitude = lgt;       
        this.name = name;        
        this.category = cat;
        this.idWiki = idW;
        this.idOsm = idO;
        this.text = text;         
        this.subCategory = subCategory;
        this.img = "-";
    }

    private SpatialObject() {
        
    }
    
    public double getDistance(){
        return distance;
    }
    
    public void setId(long id) {
        this.id = id;
    }
        
    public void setDistance(double d){
        distance = d;
    }
    
    public String getText() {
        return text;
    }

    public void setSubCategory(String subC){
        subCategory = subC;
    }
    
    public String getSubCategory(){
        return subCategory;
    }
    public void setText(String text) {
        this.text = text;
    }
    
    public void setImg(String img){
        this.img = img;
    }

    public String getImg(){
        return img;
    }
    
    public String getIdWiki() {
        return idWiki;
    }

    public String getIdOsm() {
        return idOsm;
    }

    public void setIdOsm(String idO) {
        idOsm = idO;
    }

    public void setIdWiki(String idW) {
        idWiki = idW;
    }

    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String cat) {
        category = cat;
    }

    public void setLatitude(Double lat) {
        this.latitude = lat;
    }

    public void setLongitude(Double lgt) {
        this.longitude = lgt;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void show() {
        System.out.println(id + " " + idWiki + " " + idOsm + " " + latitude
                + " " + longitude + " " + name + " " + category + " " + text);
    }
        
    public byte[] toByteArray() throws IOException {        
        ByteArrayOutputStream byteArrayOutput = new MyByteArrayOutputStream(BLOCKSIZE);
        DataOutputStream dataOutput = new DataOutputStream(byteArrayOutput);

        dataOutput.writeLong(id);
        dataOutput.writeInt(name.length()); 
        dataOutput.writeUTF(name);
        dataOutput.writeInt(idWiki.length()); 
        dataOutput.writeBytes(idWiki);
        dataOutput.writeInt(idOsm.length()); 
        dataOutput.writeBytes(idOsm);
        dataOutput.writeDouble(latitude);        
        dataOutput.writeDouble(longitude);        
        dataOutput.writeInt(text.length()); 
        dataOutput.writeUTF(text);
        dataOutput.writeInt(category.length()); 
        dataOutput.writeUTF(category);
        dataOutput.writeInt(subCategory.length()); 
        dataOutput.writeUTF(subCategory);
        dataOutput.writeInt(img.length());
        dataOutput.writeBytes(img);
        
        dataOutput.close();

        return byteArrayOutput.toByteArray();
    }
        
    public static SpatialObject createObject(byte[] bytes) throws IOException {
        SpatialObject obj = new SpatialObject();        
        ByteArrayInputStream byteArrayInput = new ByteArrayInputStream(bytes);
        DataInputStream dataInput = new DataInputStream(byteArrayInput);
        //Id
        long id = dataInput.readLong();
        obj.setId(id);
        //Title
        int length = dataInput.readInt();
        byte[] b = new byte[length];
        
        //dataInput.readFully(b);
        //String titulo = new String(b);
        obj.setName(dataInput.readUTF());
        //Id wikipédia
        length = dataInput.readInt();
        b = new byte[length];
        dataInput.readFully(b);
        obj.setIdWiki(new String(b));
        //Id osm
        length = dataInput.readInt();
        b = new byte[length];
        dataInput.readFully(b);
        obj.setIdOsm(new String(b));
        //Latitude
        obj.setLatitude(dataInput.readDouble());                
        //Longitude
        obj.setLongitude(dataInput.readDouble());        
        //Text
        length = dataInput.readInt();
        b = new byte[length];
        
        obj.setText(dataInput.readUTF());
        
        //Category
        length = dataInput.readInt();
        b = new byte[length];
        //dataInput.readFully(b);
        obj.setCategory(dataInput.readUTF());
        //Subcategory
        length = dataInput.readInt();
        b = new byte[length];
        //dataInput.readFully(b);
        obj.setSubCategory(dataInput.readUTF());
        //image
        length = dataInput.readInt();
        b = new byte[length];
        dataInput.readFully(b);
        obj.setImg(new String(b));
                
        dataInput.close();
        return obj;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof SpatialObject){
            
                SpatialObject aux = (SpatialObject) o;
                
                double distance = SpatialUtils.haversineDistance(getLatitude(), getLongitude(), 
                        aux.getLatitude(), aux.getLongitude());
                                
                if(distance < 100 && (getName().toLowerCase().equals(aux.getName().toLowerCase()))){
                    return true;
                }                       
        }
        return false;
    }

    @Override
    public int hashCode() {
        
        int hash = 7;
        if(!this.idWiki.equals("-")){
            hash = 43 * hash + Objects.hashCode(this.idWiki);
        }else{
            hash = 43 * hash + Objects.hashCode(this.id);
        }
        return hash;
    }

    @Override
    public int compareTo(Object o) {
        //Verifica se o objeto é um lance.
        if (o instanceof SpatialObject) {
            SpatialObject aux = (SpatialObject) o;
            //Compara os valores dos lances.
            if (this.getDistance() < aux.getDistance()) {
                return -1;
            } else if (this.getDistance() > aux.getDistance()) {
                return 1;
            } else {
                return 0;
            }
        } else {
            throw new RuntimeException("Objeto incompatível"); 
        }
    }    
    
    private class MyByteArrayOutputStream extends ByteArrayOutputStream{
        public MyByteArrayOutputStream(int size) {
            super(size);
        }
        @Override
        public synchronized byte[] toByteArray() {
            return super.buf;
        }        
    }
}
