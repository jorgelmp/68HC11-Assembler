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
    private String direccion;
    private String etiqueta;
    private int tipo;
    private int siguiente;
    private boolean especial=false;
            
    public Relativo(String linea, String mnemonico, String operando){
        super(linea);
        this.mnemonico = mnemonico;
        this.operando =operando;
        this.opcode = tabla.get(mnemonico.toLowerCase()).toUpperCase();
        this.opcode = String.format("%02X", Integer.parseInt(opcode, 16));
        
    }
    
    public Relativo(String linea, String direccion, String etiqueta, int siguiente){
        super(linea);
        this.direccion = direccion;
        this.etiqueta = etiqueta;        
        this.siguiente = siguiente;
    }
    
    public Relativo(String linea, String mnemonico, String operandos,String etiqueta, int tipo){
        super(linea);
        this.mnemonico = mnemonico;
        this.etiqueta = etiqueta;
        this.tipo = tipo;
        this.operando = operandos;
        this.especial = true;
        
    }
    
    @Override
    public String toPrintToFile(){
        if(Main.segunda){
            if(!Etiqueta.contiene(etiqueta)){
                return Main.getLineNumber() + linea + "\n"+Main.getLineNumber()+getSpace(linea.indexOf(etiqueta))+getError(3); //etiqueta inexistente
            }
            int salto = Integer.valueOf(Etiqueta.getEtiqueta(etiqueta),16) - Integer.valueOf(direccion,16) - siguiente;
            if(Math.abs(salto)>255){
                return Main.getLineNumber()+linea + "\n"+Main.getLineNumber()+getSpace(linea.indexOf(etiqueta))+getError(8); //salto relativo muy grande
            }
            String saltoHex = (salto>0)? String.format("%02X", salto) : String.format("%02X", 256+salto);
            return Main.getLineNumber()+linea.substring(0,linea.indexOf("G"))+ saltoHex.toUpperCase() +linea.substring(linea.indexOf("G")+2);
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
            aImprimir+= opcode+ " "+ operando1 + operando2 + "GG";
            return aImprimir + getSpaceFor(aImprimir) + linea;
        }
        
        Main.updateAddress(2);
        aImprimir+= opcode+" "+"GG";
        return aImprimir + getSpaceFor(aImprimir)+linea;
    }
    
    private int updateAddress(String operando){
        
        int add = operando.length();
        
        if(add == 2)
            return 1;
        else if(add == 4)
            return 2;
        
        return 0;
    }
    public static boolean contieneMne(String mne){
        return tabla.containsKey(mne.toLowerCase());
    }
    
    public static String obtenOpcode(String mne){
        return tabla.get(mne.toLowerCase());
        
    }
}
