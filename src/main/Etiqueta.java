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
        this.etiqueta = aux.trim();
        this.direccion = Main.getAddress(); 
        this.etiquetas.put(etiqueta,direccion);
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
