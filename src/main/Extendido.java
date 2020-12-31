/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import herramientas.Serializador;
import java.util.HashMap;

/**
 * Clase cuyas instancias representan una línea que describe una instrucción
 * en modo direccionamiento extendido tanto en la primera como en la segunda 
 * pasada.
 * @author shuai
 */
public class Extendido extends Direccionamiento{
    private static final HashMap<String,String> tabla = Serializador.abrirHashMapString2("Extendidos.ser");
    private boolean especial = false;
    
    /**
     * Construye un objeto Extendido con la línea original a representar en la 
     * primera pasada, su mnemónico y su operando.
     * @param linea la línea original
     * @param mne el mnemónico
     * @param oper el operando
     */
    public Extendido(String linea, String mne, String oper){
        super(linea);
        this.mnemonico = mne;
        this.operando = oper;
        this.opcode = tabla.get(mnemonico.toLowerCase()).toUpperCase();
    }
    
    /**
     * Construye un objeto Extendido en un caso especial JSR o JMP donde el ope-
     * rando en una etiqueta en la primera pasada.
     * @param linea la línea original a representar
     * @param mne el mnemónico
     * @param etiqueta la etiqueta operando
     * @param etiq un booleano para indicar que es un caso especial
     */
    public Extendido(String linea, String mne, String etiqueta, boolean etiq){
        super(linea);
        this.mnemonico = mne;
        this.operando = etiqueta;
        this.opcode = tabla.get(mnemonico.toLowerCase()).toUpperCase();
        String format = (opcode.length()<3)? "%02X" : "%04X";
        this.opcode = String.format(format, Integer.parseInt(opcode,16));
        this.especial = true;
    }
    
    /**
     * Construye un objeto Extendido que representa un caso especial JMP o JSR 
     * en la segunda pasada.
     * @param linea la línea original
     * @param etiqueta la etiqueta operando
     */
    public Extendido(String linea, String etiqueta){
        super(linea);
        this.operando = etiqueta;
    }
    
    /**
     * Forma la cadena con la línea a imprimir en el archivo LST. En un caso es-
     * pecial JMP o JSR de la segunda pasada, sustituye la bandera JJJJ por el 
     * valor correcto de la etiqueta operando.
     * @return una cadena con la línea a imprimir en el LST
     */
    @Override
    public String toPrintToFile(){
        if(Main.segunda){
            if(!Etiqueta.contiene(operando))
                return Main.getLineNumber()+linea+"\n"+Main.getLineNumber()+getSpace(linea.indexOf(operando))+getError(3); //Etiqueta inexistente
            operando = Etiqueta.getEtiqueta(operando);
            opcode = linea.split(" ")[1];
            return Main.getLineNumber()+linea.substring(0, linea.indexOf("J"))+operando+linea.substring(linea.indexOf("J")+4);
        }
        String aImprimir = Main.getAddress();
        if(especial){
            aImprimir += " "+opcode+" "+"JJJJ";
            Main.updateAddress(3);
            return aImprimir + getSpaceFor(aImprimir) + linea;
        }
        aImprimir = Main.getAddress() + " " + opcode+ " " + String.format("%04X", Integer.parseInt(operando,16)); 
        updateAddress();
        return aImprimir + getSpaceFor(aImprimir)+linea;    
    }    
    
    /**
    * Para ser accedido desde otras partes del código.
    * Revisa si existe una mnemonico en la tabla de extendidos
    * @param mne el mnemónico a buscar en la tabla
    * @return true, si y sólo si, existe el mnemónico
    */
    public static boolean contieneMne(String mne){
        return tabla.containsKey(mne.toLowerCase());
    }
    
    /**
    * Para ser accedido desde otras partes del código.
    * Regresa el opcode de unaa instrucción en modo de direccionamiento extendido dado su mnemónico
    * @param mne el mnemónico cuyo opcode se quiere obtener
    * @return Una cadena con el opcode de la instrucción en hexadecimal a formato de dos o cuatro dígitos
    */
    public static String obtenOpcode(String mne){
        return tabla.get(mne.toLowerCase());        
    }
    
    /**
     * Actualiza la dirección actual del programa en Main
     * sujeto a la magnitud del opcode.
     */
    private void updateAddress(){
        if(opcode.length() == 2)
            Main.updateAddress(3);
        else if(opcode.length() == 4)
            Main.updateAddress(4);
    }
    
    
}
