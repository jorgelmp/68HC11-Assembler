/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import herramientas.Serializador;
import java.util.HashMap;

/**
 *Clase que representa una línea del modo de direccionamiento inherente
 * @author jesus
 */
public class Inherente extends Direccionamiento{
    private static final HashMap<String,String> tabla = Serializador.abrirHashMapString2("Inherentes.ser");
    
    /**
     *Construye un objeto de Inherente con la línea y el mnemónico dados
     * @param linea línea con la instrucción de direccionamiento inherente
     * @param mnemonico mnemónico correspondiente a la instrucción ingresada
     */
    public Inherente(String linea, String mnemonico){
        super(linea);
        this.mnemonico = mnemonico;
        this.opcode = tabla.get(mnemonico.toLowerCase());
        String format = (opcode.length()<3)? "%02X" : "%04X";
        this.opcode = String.format(format, Integer.parseInt(opcode,16));
        
    }
    
    @Override
    /**
     * Modifica la línea original y genera la línea del archivo de salida
     * @return la línea modificada que se debe escribir en el archivo de salida
     */
    public String toPrintToFile(){
        String aImprimir = Main.getAddress() + " "+opcode;
        updateAddress();
        return aImprimir + Linea.getSpaceFor(aImprimir)+ linea;
    }
    
    /**
     *Actualiza la dirección de memoria sobre la cual se está trabajando
     */    
    private void updateAddress(){
        if(opcode.length() == 2)
            Main.updateAddress(1);
        else if(opcode.length() == 4)
            Main.updateAddress(2);
    }
    
    /**
     *Verifica si el mnemónico existe
     * @param mne mnemónico que se buscará en la tabla
     * @return booleano que señala si se encontró el mnemónico o no
     */
    public static boolean contieneMne(String mne){
        return tabla.containsKey(mne.toLowerCase());
    }
    
    /**
     *Obtiene el opcode del mnemónico ingresado
     * @param mne mnemónico cuyo opcode se requiere
     * @return cadena que contiene el opcode del mnemónico
     */
    public static String obtenOpcode(String mne){
        return tabla.get(mne.toLowerCase());
        
    }
}
