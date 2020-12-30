/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import herramientas.Serializador;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jorge
 */
public class Indexado extends Direccionamiento{
    private String operando1;
    private String operando2;
    private boolean x = false;
    private boolean especial = false;
    private static final HashMap<String,String> tablaX = Serializador.abrirHashMapString2("IndexadosX.ser");
    private static final HashMap<String,String> tablaY = Serializador.abrirHashMapString2("IndexadosY.ser");
    
    public Indexado(String linea, String mnemonico, String operando, boolean isX){
        super(linea);
        this.mnemonico = mnemonico;
        this.operando = operando;
        this.x = isX;
        this.opcode = x?  tablaX.get(mnemonico.toLowerCase()).toUpperCase() : tablaY.get(mnemonico.toLowerCase()).toUpperCase();
        String format = (opcode.length()<3)? "%02X" : "%04X";
        this.opcode = String.format(format, Integer.parseInt(opcode,16));
        
    }
    
    public Indexado(String linea, String mnemonico, String operando1, String operando2, boolean isX, boolean isSpecial){
        super(linea);
        this.mnemonico = mnemonico;
        this.operando1 = operando1;
        this.operando2 = operando2;                
        this.x = isX;
        especial = isSpecial;
        this.opcode = x?  tablaX.get(mnemonico.toLowerCase()).toUpperCase() : tablaY.get(mnemonico.toLowerCase()).toUpperCase();
        String format = (opcode.length()<3)? "%02X" : "%04X";
        this.opcode = String.format(format, Integer.parseInt(opcode,16));
        
    }
    
    @Override
    public String toPrintToFile(){
        String aImprimir="";
        if(especial){
            int error1, error2;
            error1 = linea.indexOf(operando1);
            error2 = linea.indexOf(operando2);
            operando1 = operandoToHex(operando1);
            operando2 = operandoToHex(operando2.substring(1));
            if(operando1.length()>2)
                return generarError(linea,7,error1);
            if(operando2.length()>4)
                return generarError(linea,7,error2);
            operando = operando1+operando2;
            aImprimir = Main.getAddress() + " "+opcode+" "+operando;
            updateAddress();
            return aImprimir + getSpaceFor(aImprimir) + linea;
        }
        operando = operando.split(",")[0];
        int error = linea.indexOf(operando);
        operandoToHex();
        if(operando.length()>2)
            return generarError(linea,7,error); //Magnitud de operando erronea
        aImprimir += Main.getAddress() + " " + opcode+" "+ operando;
        updateAddress();
            
        return aImprimir + getSpaceFor(aImprimir) + linea;
        
    }
    
    private String operandoToHex(){
        Matcher dec = Pattern.compile("[0-9]+?").matcher(operando);
        
        if(operando.charAt(0) =='$')
            return operando = operando.substring(1);
       
        else if(dec.matches()){
            return operando = Integer.toHexString(Integer.parseInt(operando)).toUpperCase();
        }
        else if(Variable.contiene(operando)){
            return operando = Variable.getVariable(operando);
        }
        else
            return "Error variable inexistente";
        
    }
    
    private void updateAddress(){
        if(especial){
            int add = (operando2.length()  < 3)? 1:2;
            if(x)
                Main.updateAddress(2 + add );
            else
                Main.updateAddress(3 + add);
            return;
        }
        if(opcode.length() == 2)
            Main.updateAddress(2);
        else if(opcode.length() == 4)
            Main.updateAddress(3);
    }
    
    public static boolean contieneMneX(String mne){
        return tablaX.containsKey(mne.toLowerCase());
    }
    
    public static String obtenOpcodeX(String mne){
        return tablaX.get(mne.toLowerCase());
    }
    
    public static boolean contieneMneY(String mne){
        return tablaY.containsKey(mne.toLowerCase());
    }
    
    public static String obtenOpcodeY(String mne){
        return tablaY.get(mne.toLowerCase());
        
    }
    
}
