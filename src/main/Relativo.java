/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import herramientas.Serializador;
import java.util.HashMap;

/**
 * Clase cuyas instancias representan una instrucción en modo de direccionamien-
 * to relativo.
 * @author shuai
 */
public class Relativo extends Direccionamiento{
    private static final HashMap<String,String> tabla = Serializador.abrirHashMapString2("Relativos.ser");
    private String direccion;
    private String etiqueta;
    private int tipo;
    private int siguiente;
    private boolean especial=false;
    
    /**
     * Construye un objeto Relativo que representa una instrucción relativa en 
     * la primera pasada, obtiene el opcode y le da formato.
     * @param linea la línea original
     * @param mnemonico el mnemónico
     * @param operando  el operando
     */
    public Relativo(String linea, String mnemonico, String operando){
        super(linea);
        this.mnemonico = mnemonico;
        this.operando =operando;
        this.opcode = tabla.get(mnemonico.toLowerCase()).toUpperCase();
        this.opcode = String.format("%02X", Integer.parseInt(opcode, 16));
    }
    
    /**
     * Construye un objeto Relativo que representa una instrucción relativa en la
     * segunda pasada.
     * @param linea la línea original
     * @param direccion la dirección de la línea original
     * @param etiqueta la etiqueta operando cuyo valor hay que conocer
     * @param siguiente la diferencia entre la dirección de la instrucción si-
     * guiente y esta instrucción
     */
    public Relativo(String linea, String direccion, String etiqueta, int siguiente){
        super(linea);
        this.direccion = direccion;
        this.etiqueta = etiqueta;        
        this.siguiente = siguiente;
    }
    
    /**
     * Construye un objeto Relativo que representa una caso especial (BRCLR o BR
     * SET) de una instrucción relativa en la primera pasada.
     * @param linea la línea original
     * @param mnemonico el mnemónico
     * @param operandos los operandos directo e inmediato
     * @param etiqueta el operando etiqueta
     * @param tipo el tipo de caso especial (1 - indexadoX, 2 - indexado Y, 3 - Directo)
     */
    public Relativo(String linea, String mnemonico, String operandos,String etiqueta, int tipo){
        super(linea);
        this.mnemonico = mnemonico;
        this.etiqueta = etiqueta;
        this.tipo = tipo;
        this.operando = operandos;
        this.especial = true;
        
    }
    
    
    /**
     * Forma la cadena con la línea a imprimir en el archivo LST. En la segunda 
     * pasada, sustituye la bandera GG por el valor correcto del operando.
     * @return una cadena con la línea a imprimir en el LST 
     */
    @Override
    public String toPrintToFile(){
        if(Main.segunda){
            if(!Etiqueta.contiene(etiqueta)){
                return Main.getLineNumber() + linea + "\n"+Main.getLineNumber()+getSpace(linea.indexOf(etiqueta))+getError(3); //etiqueta inexistente
            }
            int salto = Integer.valueOf(Etiqueta.getEtiqueta(etiqueta),16) - Integer.valueOf(direccion,16) - siguiente;
            if(salto > 128 || salto <-127){
                return Main.getLineNumber()+linea + "\n"+Main.getLineNumber()+getSpace(linea.indexOf(etiqueta))+getError(8); //salto relativo muy grande
            }
            String saltoHex = (salto>0)? String.format("%02X", salto) : String.format("%02X", 256+salto);
            
            String re = linea.substring(0,linea.indexOf("G"))+ saltoHex.toUpperCase() + linea.substring(linea.indexOf("G")+2);
            opcode = re.split(" ")[1];
            operando = re.split(" ")[2];
            return Main.getLineNumber() + re;
        }
            
        String aImprimir = Main.getAddress()+" ";
        
        if(especial){
            String operandos = operando;
            String operando1 = " ";
            String operando2 = " ";
            String opcode = " ";
            switch (tipo) {
                case 1:
                    opcode = Indexado.obtenOpcodeX(mnemonico.toLowerCase()).toUpperCase();
                    operando1 = operandos.split(",")[0];
                    operando2 = operandos.split(",")[2];
                    break;
                case 2:
                    opcode = Indexado.obtenOpcodeY(mnemonico.toLowerCase()).toUpperCase();
                    operando1 = operandos.split(",")[0];
                    operando2 = operandos.split(",")[2];
                    break;
                case 3:
                    opcode = Directo.obtenOpcode(mnemonico.toLowerCase()).toUpperCase();
                    operando1 = operandos.split(",")[0];
                    operando2 = operandos.split(",")[1];
                    break;
                default:
                    break;
            }
            
            int error = linea.indexOf(operando1);
            operando1 = operando1.substring(1);
            
            if(operando1.length()>2){
                return generarError(linea,7,error);
            }
            error = linea.indexOf(operando2);
            operando2 = operando2.substring(1);
            operando2 = operandoToHex(operando2);
            
            if(operando2.length()>4)
                return generarError(linea,7,error);
            
            operando1 = String.format("%02X", Integer.parseInt(operando1,16));
            String format = (operando2.length()<3)? "%02X" : "%04X";
            operando2 = String.format(format, Integer.parseInt(operando2,16));
            int add = (tipo == 2)? 4 + updateAddress(operando2)  :  3 + updateAddress(operando2);
            Main.updateAddress(add);
            operando = operando1+operando2;
            aImprimir+= opcode+ " "+ operando + "GG";
            return aImprimir + getSpaceFor(aImprimir) + linea;
        }
        
        Main.updateAddress(2);
        aImprimir+= opcode+" "+"GG";
        return aImprimir + getSpaceFor(aImprimir)+linea;
    }
    
    /**
     * Actualiza la dirección actual del programa en Main
     * sujeto a la magnitud del operando.
     */
    private int updateAddress(String operando){
        int add = operando.length();
        
        if(add == 2)
            return 1;
        else if(add == 4)
            return 2;
        
        return 0;
    }
    
    /**
    * Para ser accedido desde otras partes del código.
    * Revisa si existe una mnemonico en la tabla de relativos
    * @param mne el mnemónico a buscar en la tabla
    * @return true, si y sólo si, existe el mnemónico
    */
    public static boolean contieneMne(String mne){
        return tabla.containsKey(mne.toLowerCase());
    }
    
    /**
    * Para ser accedido desde otras partes del código.
    * Regresa el opcode de unaa instrucción en modo de direccionamiento relativo dado su mnemónico
    * @param mne el mnemónico cuyo opcode se quiere obtener
    * @return Una cadena con el opcode de la instrucción en hexadecimal a formato de dos dígitos
    */
    public static String obtenOpcode(String mne){
        return tabla.get(mne.toLowerCase());
        
    }
}
