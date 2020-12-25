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
        String partes[] = aux.split(" +");
        switch(partes.length){
            case 1:
                this.linea = getSpace(10)+partes[0]+getSpace(32-partes[0].length())+comentario;
                break;
            case 2:
                this.linea = getSpace(10)+partes[0]+getSpace(10-partes[0].length())+partes[1]+getSpace(22-partes[1].length())+comentario;
                break;
            case 3:
                this.linea = getSpace(10)+partes[0]+getSpace(10-partes[0].length())+partes[1]+" "+partes[2]+getSpace(22-partes[1].length()-1-partes[2].length())+comentario;
                break;
            default:
                break;
        }
        
    }
    
    public static Linea getLineType(String linea){
        String aux = linea.contains("*")? retrieveComment(linea).trim() : linea.trim();
        String[] partes = aux.split(" +");
        String mnemonico = partes[0];
        
        if(!mneExists(mnemonico)){
            int error = linea.indexOf(mnemonico);
            return new Linea(generarError(linea,4,error)); //Error Mnemonico inexistente
        }
        
        
        if(Inherente.contieneMne(mnemonico)){
            if(partes.length > 1){
                int error = linea.indexOf(partes[1]);
                return new Linea(generarError(linea,6,error)); //Error No lleva operando
            }
            return new Inherente(linea,mnemonico);
        }
        
        if(partes.length == 1){ 
            int error = linea.indexOf(mnemonico)+mnemonico.length()+1;
            return new Linea(generarError(linea,5,error)); //Error carece de operando
        }
        
        String operando = partes[1];
        Matcher indxM = Pattern.compile(",x|,X").matcher(operando);
        Matcher indyM = Pattern.compile(",y|,Y").matcher(operando);
        Matcher dirM = Pattern.compile(",#,").matcher(operando);
        
        //
        //partes[0] -> MNEMONICO 
        //partes[1] -> OPE,X,#NOD
        //partes[2] -> ETIQUETA
        
        // mne ,x,#das
        if(mnemonico.toUpperCase().equals("JMP")||mnemonico.toUpperCase().equals("JSR")){
            return new Extendido(linea,mnemonico,operando,true);
        }
        if(mnemonico.toUpperCase().equals("BRCLR") || mnemonico.toUpperCase().equals("BRSET")){
            if(partes.length < 3)
                return new Linea(generarError(linea,5,linea.indexOf(operando+operando.length()+1))); //Error carece de operando
            
            String etiqueta = partes[2];
            //Si hay más de tres partes separadas por comas, lanzar "Identificador o argumento inesperado"
            if(operando.contains(",X")||operando.contains(",x")){
                if(operando.split(",").length<3)
                    return new Linea(generarError(linea,5,linea.indexOf(operando))); //Error carece de operando
                return new Relativo(linea,mnemonico,operando,etiqueta,1);
            }
            if(operando.contains(",y")||operando.contains(",Y")){
                 if(operando.split(",").length<3)
                    return new Linea(generarError(linea,5,linea.indexOf(operando))); //Error carece de operando
                return new Relativo(linea,mnemonico, operando,etiqueta,2);
            }
            if(operando.contains(",#")){
                if(operando.split(",").length < 2)
                    return new Linea(generarError(linea,5,linea.indexOf(operando))); //Error carece de operando
                return new Relativo(linea,mnemonico, operando,etiqueta, 3);
            }
            return null;
        }
        
        if(mnemonico.toUpperCase().equals("BCLR") || mnemonico.toUpperCase().equals("BSET")){
            if(partes.length < 2)
                return null; //Error carece de operando;
            if(operando.contains(",X")||operando.contains(",x")){
                if(operando.split(",").length<3)
                    return new Linea(generarError(linea,5,linea.indexOf(operando))); //Error carece de operando
                return new Indexado(linea,mnemonico,operando.split(",")[0],operando.split(",")[2],true,true);
            }
            if(operando.contains(",y")||operando.contains(",Y")){
                if(operando.split(",").length<3)
                    return new Linea(generarError(linea,5,linea.indexOf(operando))); //Error carece de operando
                return new Indexado(linea,mnemonico,operando.split(",")[0],operando.split(",")[2],false,true);
            }
            if(operando.contains(",#"))
                return new Directo(linea, mnemonico, operando.split(",")[0], operando.split(",")[1]);
        }
        
        //Extendidos con etiquetas como operandos
        //   -JMP
        //   - JSR
        //   -
        
        
        if(Relativo.contieneMne(mnemonico)){
            return new Relativo(linea,mnemonico,operando);
        }
       
        if(Indexado.contieneMneX(mnemonico) && indxM.find()){
            return new Indexado(linea, mnemonico, operando, true);
        }
        
        if(Indexado.contieneMneY(mnemonico)&& indyM.find()){
            return new Indexado(linea,mnemonico, operando, false);
        }
        
        if(Inmediato.contieneMne(mnemonico) && operando.charAt(0)=='#'){
            /*if(operando.contains(","))
                operando+= " "+partes[2];*/
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
                    return new Linea(generarError(linea,7,error));
                return new Directo(linea,mnemonico,operando);
            }
            else if(ext && !dir){
                if(operando.length()>4)
                    return new Linea(generarError(linea,7,error));
                return new Extendido(linea,mnemonico,operando);
            }
            else if(dir && ext)
            { 
                if(operando.length() < 3)
                    return new Directo(linea,mnemonico, operando);
                else if(operando.length()<5)
                    return new Extendido(linea,mnemonico, operando);     
                return new Linea(generarError(linea,7,error));
            }
        }
        //dasdsada
        return new Linea(generarError(linea,2,error)); //Variable inexistente (Etiqueta?)
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
