/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import herramientas.Serializador;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jorge
 */
public abstract class Direccionamiento extends Linea{
    String mnemonico;
    String opcode;
    String operando;
/*
    public static final int INMEDIATO = 1;
    public static final int DIRECTO = 2;
    public static final int INDX = 3;
    public static final int INDY = 4;
    public static final int EXTENDIDO = 5;
    public static final int INHERENTE = 6;
    public static final int RELATIVO  = 7;
*/
    private static final HashSet<String> mnemonicos = Serializador.abrirHashSetString("Mnemonicos.ser");

    public Direccionamiento(String linea){
        super(linea);
    }
    
    public static Linea getLineType(String linea){
        String aux = linea.contains("*")? retrieveComment(linea).trim() : linea.trim();
        String[] partes = aux.split(" +");
        String mnemonico = partes[0];
        
        if(!mneExists(mnemonico)){
            int error = linea.indexOf(mnemonico);
            return new Linea(getSpace(DEFAULT)+linea+"\n"+getSpace(DEFAULT)+getSpace(error)+lanzarError(4)); //Error Mnemonico inexistente
        }
        
        
        if(Inherente.contieneMne(mnemonico)){
            if(partes.length > 1){
                int error = linea.indexOf(partes[1]);
                return new Linea(getSpace(DEFAULT)+linea+"\n"+getSpace(DEFAULT)+getSpace(error)+lanzarError(6)); //Error No lleva operando
            }
            return new Inherente(linea,mnemonico);
        }
        
        if(partes.length == 1){
            int error = linea.indexOf(mnemonico)+mnemonico.length()+1;
            return new Linea(getSpace(DEFAULT)+linea+"\n"+getSpace(DEFAULT)+getSpace(error)+lanzarError(5)); //Error carece de operando
        }
        
        String operando = partes[1];
        Matcher indxM = Pattern.compile(",x|,X").matcher(operando);
        Matcher indyM = Pattern.compile(",y|,Y").matcher(operando);
        Matcher dirM = Pattern.compile(",#").matcher(operando);
        
        
        if(mnemonico.toUpperCase() == "BRCLR" || mnemonico.toUpperCase() == "BRSET"){
            if(partes.length < 3)
                return null; //Error carece de operando
            if(indxM.find())
                return new Indexado(linea,mnemonico,operando+" "+partes[2],true, "BR");
            if(indyM.find())
                return new Indexado(linea,mnemonico, operando+" "+partes[2],false,"BR");
            if(dirM.find())
                return new Directo(linea,mnemonico, operando+" "+partes[2], "BR");
        }
        
        if(mnemonico.toUpperCase() == "BCLR" || mnemonico.toUpperCase() == "BSET"){
            if(partes.length < 3)
                return null; //Error carece de operando;
            if(indxM.find())
                return new Indexado(linea,mnemonico,operando,true, "B");
            if(indyM.find())
                return new Indexado(linea,mnemonico, operando,false,"B");
            if(dirM.find())
                return new Directo(linea,mnemonico, operando, "B");
        }
        
        if(Relativo.contieneMne(mnemonico)){
            return new Relativo(linea);
        }
       
        if(Indexado.contieneMneX(mnemonico) && indxM.find()){
            return new Indexado(linea, mnemonico, operando, true);
        }
        
        if(Indexado.contieneMneY(mnemonico)&& indyM.find()){
            return new Indexado(linea,mnemonico, operando, false);
        }
        
        if(Inmediato.contieneMne(mnemonico) && operando.charAt(0)=='#'){
            if(operando.contains(","))
                operando+= " "+partes[2];
            return new Inmediato(linea,mnemonico,operando);
        }
        
        boolean dir = Directo.contieneMne(mnemonico);
        boolean ext = Extendido.contieneMne(mnemonico);
        
        Matcher dec = Pattern.compile("[0-9]+?").matcher(operando);
        int error = linea.indexOf(operando);
        if(dec.matches())
        {
            operando = "$"+Integer.toHexString(Integer.parseInt(operando)).toUpperCase();
        }
        else if(!Variable.isEmpty())
            if(Variable.contiene(operando))
                operando = "$"+Variable.getVariable(operando);
        
        if(operando.charAt(0)=='$')
        {
            operando = operando.substring(1);
            if(dir && !ext){
                if(operando.length()>2)
                    return new Linea(getSpace(DEFAULT)+linea+"\n"+getSpace(DEFAULT)+getSpace(error)+lanzarError(7));
                return new Directo(linea,mnemonico,operando);
            }
            else if(ext && !dir){
                if(operando.length()>4)
                    return new Linea(getSpace(DEFAULT)+linea+"\n"+getSpace(DEFAULT)+getSpace(error)+lanzarError(7));
                return new Extendido(linea,mnemonico,operando);
            }
            else if(dir && ext)
            { 
                if(operando.length() < 3)
                    return new Directo(linea,mnemonico, operando);
                else if(operando.length()<5)
                    return new Extendido(linea,mnemonico, operando);     
                return new Linea(getSpace(DEFAULT)+linea+"\n"+getSpace(DEFAULT)+getSpace(error)+lanzarError(7));
            }
        }
        return new Linea(getSpace(DEFAULT)+linea+"\n"+getSpace(DEFAULT)+getSpace(error)+lanzarError(2)); //Variable inexistente (Etiqueta?)
    }
    
    static String operandoToHex(String operando){
        Matcher dec = Pattern.compile("[0-9]+?").matcher(operando);
        
        if(operando.charAt(0) =='$')
            return operando.substring(1);
        
        else if(operando.charAt(0) == (char)39){
            int caracter = operando.charAt(1);
            return Integer.toHexString(caracter).toUpperCase();
        }
        else if(dec.matches()){
            return Integer.toHexString(Integer.parseInt(operando)).toUpperCase();
        }
        else if(Variable.contiene(operando)){
            return Variable.getVariable(operando);
        }
        else
            return ""; //Error  constante o variable inexistente;
    }
    
    public static boolean mneExists(String mne){
        return mnemonicos.contains(mne.toLowerCase());
    }
    
   
    
}
