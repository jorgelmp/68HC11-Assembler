/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;
import java.util.HashMap;

/**
 * Clase cuyas instancias representan etiquetas en el código fuente.
 * @author jorge
 */
public class Etiqueta extends Linea {
    private String etiqueta;
    private String direccion;
    private static HashMap<String, String> etiquetas = new HashMap<String,String>(); //Tabla Hash donde se guardan las etiquetas y sus direcciones

/**
 * Construye un objeto Etiqueta de acuerdo a una línea
 * @param linea la línea de la etiqueta
 */
    public Etiqueta(String linea){
        super(linea);
        String[] partes = aux.split(" +"); //Divide la línea en palabras
        this.linea = partes[0] + getSpace(42 - partes[0].length())+comentario; //Da formato de espacios al la línea que se imprimirá en el LST
        this.etiqueta = aux.trim(); //La etiqueta es igual al auxiliar sin los espacios
        this.direccion = Main.getAddress();  //La dirección de la etiqueta depende de la dirección actual del código objeto
        this.etiquetas.put(etiqueta,String.format("%04X", Integer.parseInt(direccion,16))); //Se guarda la etiqueta y su dirección en la talba
    }
    
    /**
    * Para ser accedido desde otras partes del código.
    * Revisa si existe una etiqueta en la tabla
    * @param nombre el nombre de la etiqueta a buscar en la tabla
    * @return true, si y sólo si, existe la etiqueta
    */
    public static boolean contiene(String nombre){
        return etiquetas.containsKey(nombre);
    }
    
    /**
    * Para ser accedido desde otras partes del código.
    * Regresa el dirección de una etiqueta dado su nombre
    * @param nombre el nombre de la etiqueta cuya dirección se quiere obtener
    * @return la dirección de la etiqueta en hexadecimal a formato de cuatro dígitos
    */
    public static String getEtiqueta(String nombre){
        return etiquetas.get(nombre);
    }
    /**
     * Para construir la línea que se imprimirá en el archivo LST
     * @return una cadena con la línea a imprimir en el LST 
     */
    @Override
    public String toPrintToFile(){
        //Falta ajustar espacio
        return direccion + getSpaceFor(direccion) + linea;
    }
    
}
