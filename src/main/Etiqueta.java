/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;
import java.util.HashMap;

/**
 *
 * @author jorge
 */
public class Etiqueta extends Linea {
    private String etiqueta;
    private String direccion;
    private static HashMap<String, String> etiquetas = new HashMap<String,String>();

    
    public Etiqueta(String linea){
        super(linea);
        String[] partes = aux.split(" +");
        this.linea = partes[0] + getSpace(42 - partes[0].length())+comentario;
        this.etiqueta = aux.trim();
        this.direccion = Main.getAddress(); 
        this.etiquetas.put(etiqueta,String.format("%04X", Integer.parseInt(direccion,16)));
    }
    
    public static boolean contiene(String nombre){
        return etiquetas.containsKey(nombre);
    }
    
    public static String getEtiqueta(String nombre){
        return etiquetas.get(nombre);
    }
    
    @Override
    public String toPrintToFile(){
        //Falta ajustar espacio
        return direccion + getSpaceFor(direccion) + linea;
    }
    
    
    
    
}
