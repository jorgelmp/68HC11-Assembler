/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import herramientas.Serializador;
import java.util.HashMap;

/**
 *
 * @author jorge
 */
public class Relativo extends Direccionamiento{
    private static final HashMap<String,String> tabla = Serializador.abrirHashMapString2("Relativos.ser");
  
    public Relativo(String linea){
        super(linea);
    }
    
    @Override
    public String toPrintToFile(){
        return " ";
    }
    
    public static boolean contieneMne(String mne){
        return tabla.containsKey(mne.toLowerCase());
    }
    
    public static String obtenOpcode(String mne){
        return tabla.get(mne.toLowerCase());
        
    }
}
