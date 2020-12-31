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
 * en modo direccionamiento directo tanto en la primera como en la segunda 
 * pasada
 * @author shuai
 */
public class Directo extends Direccionamiento{
    private static final HashMap<String,String> tabla = Serializador.abrirHashMapString2("Directos.ser"); //Tabla Hash con los mnemonicos y los opcodes de directo
    private boolean especial = false;
    private String operando1;
    private String operando2;
    
    /**
     * Construye un Directo con la línea original, el mnemonico y el operando.
     * Obtiene el opcode de mnemonico al momento de crear la instancia y le da
     * formato.
     * @param linea la línea original
     * @param mne el mnemónico 
     * @param ope el operando
     */
    public Directo(String linea, String mne, String ope){
        super(linea);
        this.mnemonico = mne;
        this.operando = ope;
        this.opcode = tabla.get(mnemonico.toLowerCase()).toUpperCase();
        String format = (opcode.length()<3)? "%02X" : "%04X";
        this.opcode = String.format(format, Integer.parseInt(opcode,16));    
    }
    
    /**
     * Construye un Directo que representa uno de los casos especiales BSET
     * y BCLR con la línea original, el mnemónico y los dos operandos.
     * @param linea la línea original
     * @param mne el mnemónico 
     * @param operando1 el primer operando (en directo)
     * @param operando2 el segundo operando (en inmediato)
     */
    public Directo(String linea, String mne, String operando1, String operando2){
        super(linea);
        this.mnemonico = mne;
        this.operando1 = operando1;
        this.operando2 = operando2;
        this.especial = true;
        this.opcode = tabla.get(mnemonico.toLowerCase()).toUpperCase();
        String format = (opcode.length()<3)? "%02X" : "%04X";
        this.opcode = String.format(format, Integer.parseInt(opcode,16));
    }
    
    /**
     * Construye la línea que se imprimirá en el archivo LST con el formato de 
     * espacios, la dirección actual del código objeto, el opcode y el operando
     * en hexadecimal en un caso normal. En un caso especial (BCLR o BSET) ajusta
     * ambos operandos y busca errores de magnitud de operando errónea.
     * @return una cadena para imprimirse en el archivo LST
     */
    @Override
    public String toPrintToFile(){
        String aImprimir = "";
        if(especial){
            int error1, error2;
            error1 = linea.indexOf(operando1);
            error2 = linea.indexOf(operando2);
            operando1 = operandoToHex(operando1);
            if(operando1.length()>2)
                return generarError(linea,7,error1);
            operando2 = operandoToHex(operando2.substring(1));
            if(operando2.length()>4)
                return generarError(linea,7,error2);
            operando1 = String.format("%02X", Integer.parseInt(operando1,16));
            String format = (operando2.length()<3)? "%02X" : "%04X";
            operando2 = String.format(format, Integer.parseInt(operando2,16));
            operando = operando1+operando2;
            aImprimir += Main.getAddress()+" "+opcode+ " "+operando;
            updateAddress();
            return aImprimir+getSpaceFor(aImprimir)+linea;
            
        }
   
        aImprimir = Main.getAddress() + " " + opcode +" "+ operando; 
        updateAddress();
        return aImprimir + getSpaceFor(aImprimir)+linea;
    }
    
    /**
    * Para ser accedido desde otras partes del código.
    * Revisa si existe una mnemonico en la tabla de directos
    * @param mne el mnemónico a buscar en la tabla
    * @return true, si y sólo si, existe el mnemónico
    */
    public static boolean contieneMne(String mne){
        return tabla.containsKey(mne.toLowerCase());
    }
    
    /**
    * Para ser accedido desde otras partes del código.
    * Regresa el opcode de unaa instrucción en modo de direccionamiento directo dado su mnemónico
    * @param mne el mnemónico cuyo opcode se quiere obtener
    * @return Una cadena con el opcode de la etiqueta en hexadecimal a formato de dos o cuatro dígitos
    */
    public static String obtenOpcode(String mne){
        return tabla.get(mne.toLowerCase()).toUpperCase();
        
    }
    
    /**
     * Actualiza la dirección actual del programa en Main
     * sujeto a la magnitud del opcode en un caso normal.
     * En un caso especial (BCLR y BSET) analiza también
     * la magnitud del segundo operando (el inmediato).
     */
    private void updateAddress(){
        if(especial){
            Main.updateAddress(2+operando2.length()/2);
            return;
        }
        int add = opcode.length() + operando.length();
        if(add == 4)
            Main.updateAddress(2);
        else if(add == 6)
            Main.updateAddress(3);
    }
        
    
}
