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
public class Variable extends Linea{
    
    private static HashMap<String, String> variables = new HashMap<String,String>();
    private String nombre;
    private String valor;

    public Variable(String linea){
        super(linea);
        String[] partes = aux.trim().split(" +");
        this.nombre = partes[0];
        this.valor = partes[2].substring(1).toUpperCase();
        this.variables.put(nombre, String.format("%04X", Integer.parseInt(valor,16)));
    }
   
   public String toPrintToFile(){
       return getSpace(5) + valor + getSpace(7) + linea;
   }
   
   public static boolean contiene(String nombre){
       return variables.containsKey(nombre);
   }
   
   public static String getVariable(String nombre){
       int valor = Integer.parseInt(variables.get(nombre),16);
       if(valor<256)
           return String.format("%02X", valor);
       return String.format("%04X", valor);
   }
   
   public static boolean isEmpty(){
       return variables.isEmpty();
   }
        
    
}
