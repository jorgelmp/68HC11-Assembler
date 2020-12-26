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
    
    private static final int DEFAULT  = 16;
    private static String espacio = new String(new char[DEFAULT]).replace('\0', ' ');
    

    public Linea(String linea){
        this.linea = linea;
        if(!linea.contains("^")){
            this.comentario = getComment(linea);
            this.aux = comentario.isEmpty()? linea.trim() : retrieveComment(linea).trim();
        }
    }

    public static Linea getLineType(String linea){
        if(Main.segunda){
            Matcher relativo = Pattern.compile("\\p{XDigit}{4} +\\p{XDigit}{2} +GG +\\p{Alpha}{3,4} +[^ *]+ *(\\*+.*)?$").matcher(linea);
            Matcher especial = Pattern.compile("\\p{XDigit}{4} +\\p{XDigit}{2,4} +\\p{XDigit}{4,6}GG +(BRCLR|BRSET) +\\$\\p{XDigit}{2}\\,((X|Y|x|y)\\,)?#(\\$\\p{XDigit}{2,4}|\\p{Digit}+?|'\\p{ASCII}) +[^ *]+ *(\\*+.*)?$").matcher(linea);
            Matcher extespecial = Pattern.compile("\\p{XDigit}{4} +\\p{XDigit}{2} +JJJJ +(JMP|JSR) +[^ *]+ *(\\*+.*$)?").matcher(linea);
            if(relativo.matches()){ // 8000 3242321           Mnemonico etiqueta * comentario 
                String [] partes = linea.split(" +");
                String direccion = partes[0];
                String etiqueta = partes[4];
                return new Relativo(linea, direccion,etiqueta, partes[1].length()/2);
                }
            else if(especial.matches()){ //8000 3242321           Mnemonico ope,x,y etiqueta * comentario
                String [] partes = linea.split(" +");
                String direccion = partes[0];
                String etiqueta = partes[5];
                return new Relativo(linea, direccion, etiqueta, (partes[1].length()+partes[2].length())/2);
            }
            else if(extespecial.matches()){
                String[] partes = linea.split(" +");
                String etiqueta = partes[4];
                return new Extendido(linea,etiqueta);
            }
            return new Linea(Main.getLineNumber()+linea);
        }
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
            
            String comentario = getComment(linea);
            String[] partes = retrieveComment(linea).trim().split(" +");
            
            switch(partes.length){
            case 1:
                linea = partes[0]+getSpace(32-partes[0].length())+comentario;
                return new Linea(generarError(linea,9,0));
            case 2:
                linea = partes[0]+getSpace(10-partes[0].length())+partes[1]+getSpace(22-partes[1].length())+comentario;
                return new Linea(generarError(linea,9,0));
            case 3:
                linea = partes[0]+getSpace(10-partes[0].length())+partes[1]+" "+partes[2]+getSpace(22-partes[1].length()-1-partes[2].length())+comentario;
                return new Linea(generarError(linea,9,0));
            default:
                return null; //Identificador inesperado
            }
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
    
    static String getError(int error){
         switch(error){
            case 1:
                return "^001 CONSTANTE INEXISTENTE";
            case 2:
                return "^002 VARIABLE INEXISTENTE";
            case 3:
                return "^003 ETIQUETA INEXISTENTE";
            case 4:
                return "^004 MNEMONICO INEXISTENTE";
            case 5:
                return "^005 INSTRUCCIÓN CARECE DE OPERANDOS";
            case 6:
                return "^006 INSTRUCCIÓN NO LLEVA OPERANDOS";
            case 7:
                return "^007 MAGNITUD DE OPERANDO ERRÓNEA";
            case 8:
                return "^008 SALTO RELATIVO MUY LEJANO ";
            case 9:
                return "^009 INSTRUCCIÓN CARECE DE AL MENOS UN ESPACIO RELATIVO AL MARGEN";
            case 10:
                return "^010 NO SE ENCUENTRA END";
            default:
                return "";
        }
    }
    static String generarError(String linea, int cual, int donde){
        return getSpace()+linea+"\n"+getSpace()+getSpace(donde)+ getError(cual);
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
 
