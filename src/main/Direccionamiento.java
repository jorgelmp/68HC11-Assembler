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
 *Clase cuyas instancias representan una modo de direccionamiento no identificado. Se usa principalmente para
 * determinar el modo de direccionamiento concreto y regresar una instancia de él con el método getLineType
 * @author jorge
 */
public class Direccionamiento extends Linea{
    String mnemonico;
    String opcode;
    String operando;
    
    private static final HashSet<String> mnemonicos = Serializador.abrirHashSetString("Mnemonicos.ser"); //HashSet con los nombres de los mnemonicos que existen para el 68HC11
    
    /**
     * Construye un direccionamiento de acuerdo a una línea dada
     * @param linea la línea que representará con un objeto de Direccionamiento
     */
    public Direccionamiento(String linea){
        super(linea);
    }
    
    /**
     * Construye un direccionamieto con una línea dada, el opcode y el operando de la instrucción
     * @param linea la línea a representar con un objeto de Direccionamiento
     * @param opcode el opcode de la instrucción
     * @param operando el operando de la instrucción
     */
    public Direccionamiento(String linea, String opcode, String operando){
        super(linea);
        this.opcode = opcode;
        this.operando = operando;
    }
    
    /**
     * Regresa una instancia de una clase que herada de Direccionamiento (un modo de direccionamiento) de acuerdo a la línea parámetro
     * @param linea la línea de la cual se quiere hacer una objeto de Direccionamiento
     * @return una instancia del modo de direccionamiento adecuado a la línea
     */
    public static Linea getLineType(String linea){
        String aux = linea.contains("*")? retrieveComment(linea).trim() : linea.trim(); //Se crea una línea auxiliar sin el comentario ni los espacios iniciales y finales
        String[] partes = aux.split(" +"); //Se divide la línea auxiliar en palabras
        String mnemonico = partes[0]; //El mnemonico siempre es la primera palabra
        String comentario = getComment(linea); //Se guarda el comentario de la línea original si existe
        
        //Se da formato a los espacios entre las palabras de acuerdo al número de palabras
        switch(partes.length){
            case 1:
                linea = getSpace(10)+partes[0]+getSpace(32-partes[0].length())+" "+comentario;
                break;
            case 2:
                linea = getSpace(10)+partes[0]+getSpace(10-partes[0].length())+partes[1]+getSpace(22-partes[1].length())+" "+comentario;
                break;
            case 3:
                linea = getSpace(10)+partes[0]+getSpace(10-partes[0].length())+partes[1]+" "+partes[2]+getSpace(22-partes[1].length()-1-partes[2].length())+" "+comentario;
            default:
                break;
        }

        //Se revisa que el mnemonico exista
        if(!mneExists(mnemonico)){
            return new Linea(generarError(linea,4,linea.indexOf(mnemonico)));
        }
        
        //Primero se revisa si se trata de un direccionamiento inherente
        if(Inherente.contieneMne(mnemonico)){
            if(partes.length > 1){ //Si sí es inherente y tiene algún operando se lanza un error
                int error = linea.indexOf(partes[1]); //Se obtiene el índice del error en la línea
                return new Linea(generarError(linea,6,error)); //Error No lleva operando
            }
            return new Inherente(linea,mnemonico);
        }
        
        //Si no es inherente, debe tener por lo menos una palabra más además del mnemonico
        if(partes.length == 1){ //Si no tiene otra palabra se lanza un error
            int error = linea.indexOf(mnemonico)+mnemonico.length()+1; //Indice del error
            return new Linea(generarError(linea,5,error)); //Error carece de operando
        }
        
        String operando = partes[1]; //La segunda palabra siempre es un operando
        Matcher indxM = Pattern.compile(",x|,X").matcher(operando); //Expresión regular para detectar indexados de x
        Matcher indyM = Pattern.compile(",y|,Y").matcher(operando); //Expresión regular para detectar indexados de y
        
        //Se revisa si se tratá de un caso JMP o JSR de extendido, pues son diferentes ya que su operando es una etiqueta
        if(mnemonico.toUpperCase().equals("JMP")||mnemonico.toUpperCase().equals("JSR")){
            return new Extendido(linea,mnemonico,operando,true);
        }
        
        //Se revisa si es un caso especial de BRCLR o BRSET de relativo
        if(mnemonico.toUpperCase().equals("BRCLR") || mnemonico.toUpperCase().equals("BRSET")){
            if(partes.length < 3) //Si sí es, debe haber por lo menos tres palabras, de otra manera se lanza
                return new Linea(generarError(linea,5,linea.indexOf(operando+operando.length()+1))); //Error carece de operando
            
            String etiqueta = partes[2]; //La tercera palabra siempre es el segundo operanod (etiqueta) en este caso especial
            
            //Si hay más de tres partes separadas por comas, se podría implementar un error "Identificador o argumento inesperado"
            
            if(operando.contains(",X")||operando.contains(",x")){ //Se revisa si es relativo con indexado x
                if(operando.split(",").length<3) //Se divide el primer operando por comas y debe haber tres partes, de otra manera se lanza
                    return new Linea(generarError(linea,5,linea.indexOf(operando))); //Error carece de operando
                return new Relativo(linea,mnemonico,operando,etiqueta,1);
            }
            
            if(operando.contains(",y")||operando.contains(",Y")){//Se revisa si es relativo con indexado y
                 if(operando.split(",").length<3)  //Se divide el primer operando por comas y debe haber tres partes, de otra manera se lanza
                    return new Linea(generarError(linea,5,linea.indexOf(operando))); //Error carece de operando
                return new Relativo(linea,mnemonico, operando,etiqueta,2);
            }
            
            if(operando.contains(",#")){ //Se revisa si es relativo con directo
                if(operando.split(",").length < 2) //Se divide el primer operando por comas y debe haber dos partes, de otra manera se lanza
                    return new Linea(generarError(linea,5,linea.indexOf(operando))); //Error carece de operando
                return new Relativo(linea,mnemonico, operando,etiqueta, 3);
            }
            
            
            return null; //Se podría implementar un nuevo error de formato en el operando
        }
        
        //Se revisan los casos no relativos especiales BCLR y BSET
        if(mnemonico.toUpperCase().equals("BCLR") || mnemonico.toUpperCase().equals("BSET")){
            if(partes.length < 2) //Debe haber por lo menos dos palabras
                return null; //Error carece de operando;
            
            if(operando.contains(",X")||operando.contains(",x")){ //Se revisa si es indexado x
                if(operando.split(",").length<3) //Se divide el primer operando por comas y debe haber tres partes, de otra manera se lanza
                    return new Linea(generarError(linea,5,linea.indexOf(operando))); //Error carece de operando
                return new Indexado(linea,mnemonico,operando.split(",")[0],operando.split(",")[2],true,true);
            }
            if(operando.contains(",y")||operando.contains(",Y")){ //Se revisa si es indexado y
                if(operando.split(",").length<3) //Se divide el primer operando por comas y debe haber tres partes, de otra manera se lanza
                    return new Linea(generarError(linea,5,linea.indexOf(operando))); //Error carece de operando
                return new Indexado(linea,mnemonico,operando.split(",")[0],operando.split(",")[2],false,true);
            }
            
            if(operando.contains(",#")) //Se revisa si es directo 
                return new Directo(linea, mnemonico, operando.split(",")[0], operando.split(",")[1]);
        }
                
        //Se revisa si es relativo normal
        if(Relativo.contieneMne(mnemonico)){
            return new Relativo(linea,mnemonico,operando);
        }
       
        //Se revisa si es indexado de x normal
        if(Indexado.contieneMneX(mnemonico) && indxM.find()){
            return new Indexado(linea, mnemonico, operando, true);
        }
        
        //Se revisa si es indexado de y normal
        if(Indexado.contieneMneY(mnemonico)&& indyM.find()){
            return new Indexado(linea,mnemonico, operando, false);
        }
        
        //Se revisa si es inmediato normal
        if(Inmediato.contieneMne(mnemonico) && operando.charAt(0)=='#'){
            return new Inmediato(linea,mnemonico,operando);
        }
        
        //No se puede determinar si es directo o extendido sólo con el mnemónico o sólo con el operando, tienen que coordinarse ambas condiciones
        
        boolean dir = Directo.contieneMne(mnemonico); //Se comprueba si es un mnemónico posible de directo
        boolean ext = Extendido.contieneMne(mnemonico); //Se comprueba si es un mnemónico posible de extendido
        
        Matcher dec = Pattern.compile("[0-9]+?").matcher(operando); //Expresión regular para comprobar si el operando está en decimal
        int error = linea.indexOf(operando); //Como se modificará el operando, se guarda el índice de éste en la línea por si hay un error de operando
        
        if(dec.matches())
        {
            operando = "$"+Integer.toHexString(Integer.parseInt(operando)).toUpperCase(); //Si el operando es decimal se pasa a hexadecimal
        }
        else if(!Variable.isEmpty())
            if(Variable.contiene(operando)) //Si el operando es una variable
                operando = "$"+Variable.getVariable(operando); //se obtiene el valor de la variable
        
        //Se asume que el operando no puede ser ASCII en directo ni extendido
        
        if(operando.charAt(0)=='$')  //Cuando el operando ya está en hexadecimal
        {
            operando = operando.substring(1);  //Se remueve el $ de hexadecimal
            if(dir && !ext){ //Si el mnemónico sólo puede ser directo
                if(operando.length()>2) //Se comprueba que el operando tenga la magnitud correcta
                    return new Linea(generarError(linea,7,error)); //Si no, se lanza error Magnitud de operando errónea
                return new Directo(linea,mnemonico,operando);
            }
            else if(ext && !dir){ //Si el mnemónico sólo puede ser extendido
                if(operando.length()>4) //Se comprueba que el operando tenga la magnitud correcta
                    return new Linea(generarError(linea,7,error)); //Si no, se lanza error Magnitud de operando errónea
                return new Extendido(linea,mnemonico,operando);
            }
            else if(dir && ext) //Si el mnémonico es compartido por ambos todo depende del operando
            { 
                if(operando.length() < 3) //Si el operando es de 8 bits
                    return new Directo(linea,mnemonico, operando); //Se considera directo
                else if(operando.length()<5) //Si el operando es de 16 bits
                    return new Extendido(linea,mnemonico, operando); //Se considrea extendido 
                return new Linea(generarError(linea,7,error)); //Si ninguno, se considera un error Magnitud de operando errónea
            }
        }
        //dasdsada
        return new Linea(generarError(linea,2,error)); //Si el operando nunca se puso en hexadecimal se asume que hubo un error de Variable inexistente
    }
    
    /**
     * Para pasar un operando de decimal, ASCII o variable a hexadecimal
     * @param operando el operando a analizar y transformar
     * @return el operando en hexadecimal sin el signo de $
     */
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
            return ""; //Se regresa una cadena vacía para indicar el error Constante o variable inexistente;
    }
    
    /**
     * Para buscar un mnemónico en el set de mnemónicos 
     * @param mne el mneónico a buscar
     * @return true, si y sólo si, el mnemónico existe
     */
    public static boolean mneExists(String mne){
        return mnemonicos.contains(mne.toLowerCase());
    }
    
    /**
     * Para obtener el opcode de está instancia de Direccionamiento 
     * @return el opcode
     */
    public String getOpcode(){
        return opcode;
    }
    
    /**
     * Para obtener el operando de esta instancia de Direccionamiento
     * @return el operando
     */
    public String getOperando(){
        return operando;
    }
    
   
    
}
