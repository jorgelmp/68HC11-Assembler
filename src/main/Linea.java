package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jorge
 */

public class Linea{
    String linea;
    String comentario;
    String aux;
    int error = 0;
    
    private static final int DEFAULT  = 16;
    private static String espacio = new String(new char[DEFAULT]).replace('\0', ' ');
    
/*
    public static final int VACIA = 1;
    public static final int COMENTARIO = 2;
    public static final int DIRECTIVA = 3;
    public static final int VARIABLE = 4;
    public static final int ETIQUETA = 5;
    public static final int DIRECCIONAMIENTO = 6;
*/
    public Linea(String linea){
        this.linea = linea;
        if(!linea.contains("^")){
            this.comentario = getComment(linea);
            this.aux = comentario.isEmpty()? linea : retrieveComment(linea);
        }
    }
   
    public static Linea getLineType(String linea){    
        if(esVacia(linea)){
            return new Linea(linea);
        }
        
        if(esComentario(linea)){
            return new Linea(getSpace(DEFAULT)+linea);
        }
        
        if(!(linea.charAt(0) == ' ')){
            if(esEtiqueta(linea))
                return new Etiqueta(linea);           
            if(esVariable(linea))
                return new Variable(linea);
            if(esDirectiva(linea))
                return new Directiva(linea);
            return new Linea(generarError(linea,9,0)); //Error carece de espacio relativo al margen
        }
        
        if(esDirectiva(linea))
            return new Directiva(linea);
        
        return Direccionamiento.getLineType(linea);        
    }
    
    private static boolean esVacia(String linea){
        Pattern vaciaP = Pattern.compile("^ *$");
        Matcher vaciaM = vaciaP.matcher(linea);
        return linea.isEmpty()||vaciaM.matches();
    }
    
    private static boolean esComentario(String linea){
        Pattern comentarioP = Pattern.compile("^ *(\\*+.*$)?$");
        Matcher comentarioM = comentarioP.matcher(linea);
        return comentarioM.matches();
    }
    
    private static boolean esEtiqueta(String linea){
        Pattern etiquetaP = Pattern.compile("^[^ *]+ *(\\*+.*$)?");
        Matcher etiquetaM = etiquetaP.matcher(linea);
        return etiquetaM.matches();
    }
    
    private static boolean esVariable(String linea){
        Pattern variableP = Pattern.compile("^[^ *]+ +EQU +\\$[0-9A-F]{1,4} *(\\*+.*$)?$");
        Matcher variableM = variableP.matcher(linea);
        return variableM.matches();
    }
    
    private static boolean esDirectiva(String linea){
        Pattern directivaP = Pattern.compile("^ +(ORG|END) +\\$[0-9A-F]{1,4} *(\\*+.*$)?$");
        Matcher directivaM = directivaP.matcher(linea);
        
        Pattern directivaFP = Pattern.compile("^ +FCB +\\$[0-9A-F]{2},\\$[0-9A-F]{2} *(\\*+.*$)?$");
        Matcher directivaFM = directivaFP.matcher(linea);
        
        Pattern directivaRP = Pattern.compile("^RESET +FCB +\\$[0-9A-F]{2},\\$[0-9A-F]{2} *(\\*+.*$)?$");
        Matcher directivaRM = directivaRP.matcher(linea);
        
        return directivaM.matches()||directivaRM.matches()||directivaFM.matches();        
    }

    /**
     * Busca un comentario en la línea
     * Si lo encuentra, lo regresa
     * Si no, regresa la cadena vacía.
     * @return La subcadena de la línea que corresponde al comentario
     */
   
    
    private static String emptyLine(){
        return "0000"; //Varios casos
    }       
    
    
    static String getComment(String aux){
        String comentario =  "";
        for(int i=0;  i<aux.length(); i++){
            if(aux.charAt(i) == '*'){
                comentario = aux.substring(i);
                return comentario;
            }
        }
        return comentario;
    }
    
    static String retrieveComment(String  aux){
        for(int i=0;  i<aux.length(); i++){
            if(aux.charAt(i) == '*'){
                aux = aux.substring(0,i);
                return aux;
            }
        }
        return aux;
    
    }
    
    public String toPrintToFile(){
        return linea;
    }
    
    static String generarError(String linea, int cual, int donde){
        String error;
        switch(cual){
            case 1:
                error= "^001 CONSTANTE INEXISTENTE";
                break;
            case 2:
                error= "^002 VARIABLE INEXISTENTE";
                break;
            case 3:
                error = "^003 ETIQUETA INEXISTENTE";
                break;
            case 4:
                error = "^004 MNEMONICO INEXISTENTE";
                break;
            case 5:
                error =  "^005 INSTRUCCIÓN CARECE DE OPERANDOS";
                break;
            case 6:
                error = "^006 INSTRUCCIÓN NO LLEVA OPERANDOS";
                break;
            case 7:
                error = "^007 MAGNITUD DE OPERANDO ERRÓNEA";
                break;
            case 8:
                error = "^008 SALTO RELATIVO MUY LEJANO ";
                break;
            case 9:
                error =  "^009 INSTRUCCIÓN CARECE DE AL MENOS UN ESPACIO RELATIVO AL MARGEN";
                break;
            case 10:
                error = "^010 NO SE ENCUENTRA END";
                break;
            default:
                return "";
        }
        return getSpace()+linea+"\n"+getSpace()+getSpace(donde)+ error;
        
    }
    public static String getSpace(){
        return espacio;
    }
    public static String getSpace(int howMuch){
        return new String(new char[howMuch]).replace('\0', ' ');
    }
    
    public static String getSpaceFor(String takesSpace){
        return espacio.substring(takesSpace.length());
    }
            

    

   
        
}
 
