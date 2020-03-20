/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package doutorado.util;

import java.util.ArrayList;

/**
 *
 * @author JoãoPaulo
 */

public class Categoria {

    String nome;
    ArrayList<String> objetos;

    public Categoria() {
        objetos = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ArrayList<String> getObjetos() {
        return objetos;
    }
    
    public String getFirstObject(){
        return objetos.get(0);
    }
    
    public String getObjeto(int index){
        return objetos.get(index);
    }
    
    public void addObjeto(String objeto){
        objetos.add(objeto);        
    }
    
    public void addObjeto(String objeto, int index){
        objetos.add(index, objeto);
    }

    public void setObjetos(ArrayList<String> objetos) {
        this.objetos = objetos;
    }
    
     @Override
        public boolean equals(Object obj) {
                if(obj == null)
                        return false;
                if(!(obj instanceof Categoria))
                        return false;
               
                return ((Categoria)obj).getNome().equals(this.nome);
        }

        @Override
        public int hashCode() {
            int hash = 1;
            return hash * 31 + nome.hashCode();
        }

        @Override
        public String toString() {
                return this.nome;
        }
}
