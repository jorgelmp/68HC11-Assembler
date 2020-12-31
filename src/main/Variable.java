/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.HashMap;

/**
 * Clase cuyas instancias representa una línea con una declaración de variable
 * @author fercho
 */
public class Variable extends Linea{
    
    private static HashMap<String, String> variables = new HashMap<String,String>(); //Tabla Hash con la variables declaradas y sus valores 
    private String nombre;
    private String valor;
    
    /**
     * Construye un objeto de Variable con la línea dada
     * Se asume que la línea ha sido analizada previamente
     * @param linea línea que representa una declaración de variable
     */
    public Variable(String linea){
        super(linea);
        String[] partes = aux.trim().split(" +"); //Divde la línea en palabras
        // [NOMBRE] EQU [VALOR]
        this.nombre = partes[0]; //La primera palabra el nombre de la variable
        this.valor = partes[2].substring(1).toUpperCase(); //La tercera palabra es el valor de la variable
        this.variables.put(nombre, String.format("%04X", Integer.parseInt(valor,16))); //Se guarda la variable en la Tabla Hash
    }
   
    /**
     * Para construir la línea que se imprimirá en el archivo LST
     * @return una cadena con la línea a imprimir en el LST
     */
   public String toPrintToFile(){
       return getSpace(5) + valor + getSpace(7) + linea;
   }
   
   /**
    * Para ser accedido desde otras partes del código.
    * Revisa si existe una variable en la tabla
    * @param nombre el nombre de la variable a buscar en la tabla
    * @return true, si y sólo si, existe la variable
    */
   public static boolean contiene(String nombre){
       return variables.containsKey(nombre);
   }
   /**
    * Para ser accedido desde otras partes del código.
    * Regresa el valor de una variable dado su nombre
    * @param nombre el nombre de la variable cuyo valor se quiere obtener
    * @return el valor de la variable en hexadecimal a formato de dos o cuatro dígitos
    */
   public static String getVariable(String nombre){
       int valor = Integer.parseInt(variables.get(nombre),16);
       if(valor<256)
           return String.format("%02X", valor);
       return String.format("%04X", valor);
   }
   
   /**
    * Revisa si hay alguna variable registrada de en la tabla
    * @return true, si y sólo si, la tabla no está vacía
    */
   public static boolean isEmpty(){
       return variables.isEmpty();
   }
        
    
}
