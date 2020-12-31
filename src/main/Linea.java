package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Una representación de cualquier línea en un archivo de texto que especifique
 * un programa escrito en ensamblador para el MC68HC11 de Motorola.
 * @author jorge
 */

public class Linea{
    String linea; //La línea original
    String comentario; //El comentario en la línea (si existe)
    String aux; //Una copia de la línea original sin el comentraio
    
    private static final int DEFAULT  = 16; //El número de espacios por defecto entre la última columna del código objeto y la primera del código fuente.
    private static String espacio = new String(new char[DEFAULT]).replace('\0', ' '); //La cadena con el número de espacios por defecto
    
    /**
     * Construye un objeto Linea con una cadena que representa una línea en el código fuente
     * y extráe su comentario si existe y crea la línea auxiliar
     * @param linea la cadena con la línea leída del código fuente
     */
    public Linea(String linea){
        this.linea = linea;
        if(!linea.contains("^")){
            this.comentario = getComment(linea);
            this.aux = comentario.isEmpty()? linea.trim() : retrieveComment(linea).trim();
        }
    }
    
    /**
     * Método que regresa una instancia de Linea o alguna de sus subclases (tipos)
     * de acuerdo a la línea que se le proporcione y a la pasada actual del programa.
     * Detecta líneas vacías, comentarios,  etiquetas, variables y directivas, si no
     * encuentra ninguna de ellas asume que es un direccionamiento y relega la tarea 
     * al método del mismo nombre de la clase Direccionamiento
     * En la segunda pasada regresa una Linea a ignorar, una instancia de algún caso 
     * relativo para terminar de calcular su operando, un Direccionamiento o un caso
     * ORG de directiva.
     * @param linea la línea que se quiere clasificar para obtener una objeto que la represente adecuadamente
     * @return una instancia de Linea o de una clase que hereda de Linea (Etiqueta, Directiva, Variable o Direccionamiento)
     */
    public static Linea getLineType(String linea){
        if(Main.segunda){ //Lo que se debe reconocer en la segunda pasada
            Matcher relativo = Pattern.compile("\\p{XDigit}{4} +\\p{XDigit}{2} +GG +\\p{Alpha}{3,4} +[^ *]+ *(\\*+.*)?$").matcher(linea); //Expresión regular para detectar direccionamientos relativos en la segunda pasada
            Matcher especial = Pattern.compile("\\p{XDigit}{4} +\\p{XDigit}{2,4} +\\p{XDigit}{4,6}GG +(BRCLR|BRSET) +\\$\\p{XDigit}{2}\\,"
                    + "((X|Y|x|y)\\,)?#(\\$\\p{XDigit}{2,4}|\\p{Digit}+?|'\\p{ASCII}) +[^ *]+ *(\\*+.*)?$").matcher(linea);// Expresión regular para detercar  BRCLR y BRSET relativos en la segunda pasada
            Matcher extespecial = Pattern.compile("\\p{XDigit}{4} +\\p{XDigit}{2} +JJJJ +(JMP|JSR) +[^ *]+ *(\\*+.*$)?").matcher(linea); //Expresión regular para detectar JSR y JMP en la segunda pasada
            Matcher direccionReg = Pattern.compile("\\p{XDigit}{4} \\p{XDigit}{2,4} \\p{XDigit}* *.*$").matcher(linea); //Expresión regular para detectar cualquier direccionamiento terminado en la segunda pasada
            Matcher org = Pattern.compile("     \\p{XDigit}{4} +ORG +\\$\\p{XDigit}{4} *(\\*+.*$)?$").matcher(linea); //Expresión regular para detectar cualquier ORG en la segunda pasada
            
            if(relativo.matches()){
                String [] partes = linea.split(" +"); //Se divide la línea por espacios (en palabras)
                String direccion = partes[0]; //La direccion de la instrucción s encuentra en la primera palabra
                String etiqueta = partes[4]; //La etiqueta de la instrucción se encuentra en la quinta palabra
                return new Relativo(linea, direccion,etiqueta, partes[1].length()/2); //Se construye un Relativo con la linea, la direccion, la etiqueta y la longitud de operando en bytes (para poder calcular el salto relativo)
                
                }
            else if(especial.matches()){
                String [] partes = linea.split(" +");
                String direccion = partes[0]; //La direccion está en la primera palabra
                String etiqueta = partes[5]; //En BRCLR y BRSET la etiqueta está en la sexta palabra
                Relativo re =  new Relativo(linea, direccion, etiqueta, (partes[1].length()+partes[2].length())/2);
                return re;
            }
            else if(extespecial.matches()){
                String[] partes = linea.split(" +");
                String etiqueta = partes[4]; //En JSR y JMP la etiqueta está en la quinta palabra
                return new Extendido(linea,etiqueta);
            }
            else if(direccionReg.matches()){
                return new Direccionamiento(Main.getLineNumber()+linea, linea.split(" ")[1], linea.split(" ")[2]); //Si es direccionamiento se agrega el número de línea y se especifica el opcode (segunda palabra) y el operando(primera palabra)
            }
            else if(org.matches()){
                return new Directiva(Main.getLineNumber()+linea); //Si es ORG se agrega el número de linea
            }
            return new Linea(Main.getLineNumber()+linea); // Si es cualquier otra línea se agrega el número de línea
        }
        
        //Se ejecuta sólo en la primera pasada
        
        if(esVacia(linea)){
            return new Linea(linea);
        }
        
        if(esComentario(linea)){
            return new Linea(getSpace(DEFAULT)+linea);
        }
        
        if(!(linea.charAt(0) == ' ')){ //Si no tiene espacio al inicio puede ser tres cosas
            if(esEtiqueta(linea)) //1. Una etiqueta
                return new Etiqueta(linea);           
            if(esVariable(linea)) //2. Una variable
                return new Variable(linea);
            if(esDirectiva(linea))//3. Un caso RESET FCB de la directiva
                return new Directiva(linea);
            
            
            //Si no es ninguna de las tres anteriores se asume que se trata de un error de falta de espacios relativos al margen
            String comentario = getComment(linea);
            String[] partes = retrieveComment(linea).trim().split(" +"); 
            
            //Se separa la línea en palabras para que el espacio entre las palabras en la línea errónea cumpla con el formato.
            switch(partes.length){ //El espacio total entre el inicio del código fuente y el comentario es de 42 espacios, por lo cual los espacios entre palabras cambian de acuerdo al número de palabras
            case 1:
                linea = partes[0]+getSpace(32-partes[0].length())+comentario; //Se da formato a la línea
                return new Linea(generarError(linea,9,0)); //Se crea el error
            case 2:
                linea = partes[0]+getSpace(10-partes[0].length())+partes[1]+getSpace(22-partes[1].length())+comentario; //Se da formato a la línea
                return new Linea(generarError(linea,9,0)); //Se crea el error
            case 3:
                linea = partes[0]+getSpace(10-partes[0].length())+partes[1]+" "+partes[2]+getSpace(22-partes[1].length()-1-partes[2].length())+comentario; //Se da formato a la línea
                return new Linea(generarError(linea,9,0)); //Se crea el error
            default:
                return null; //Podría implementarse un nuevo error: Identificador insperado
            }
        }
        
        if(esDirectiva(linea))
            return new Directiva(linea);
        
        return Direccionamiento.getLineType(linea); //Si no cumple con ninguno de los casos anteriores se verá si cumple con alguno de los modos de direccionamiento       
    }
    /**
     * Detecta líneas vacías
     * @param linea la línea a analizar
     * @return true, si y sólo si, la línea es vacía
     */
    private static boolean esVacia(String linea){
        Pattern vaciaP = Pattern.compile("^ *$"); //Expresión regular para detectar líneas vacías o compuestas sólo de espacios
        Matcher vaciaM = vaciaP.matcher(linea);
        return linea.isEmpty()||vaciaM.matches();
    }
    
    /**
     * Detecta líneas de sólo comentarios
     * @param linea la línea a analizar
     * @return true, si y sólo si, la línea es sólo un comentario
     */
    private static boolean esComentario(String linea){
        Pattern comentarioP = Pattern.compile("^ *(\\*+.*$)?$"); //Expresión regular para detectar líneas compuestas sólo de espacios y comentarios
        Matcher comentarioM = comentarioP.matcher(linea);
        return comentarioM.matches();
    }
    
    /**
     * Detecta líneas de sólo etiquetas
     * @param linea la línea a analizar
     * @return true, si y sólo si, la línea es sólo una etiqueta (con un comentario opcionalmente)
     */
    private static boolean esEtiqueta(String linea){
        Pattern etiquetaP = Pattern.compile("^[^ *]+ *(\\*+.*$)?"); //Expresión regular para detectar líneas compuestas de una etiqueta un o ningún comentario
        Matcher etiquetaM = etiquetaP.matcher(linea);
        return etiquetaM.matches();
    }
    
    /**
     * Detecta líneas donde se declaran variables
     * @param linea la línea a analizar
     * @return true, si y sólo si, la línea es sólo una declaración de variable (con un comentario opcionalmente).
     */
    private static boolean esVariable(String linea){
        Pattern variableP = Pattern.compile("^[^ *]+ +EQU +\\$[0-9A-F]{1,4} *(\\*+.*$)?$");
        Matcher variableM = variableP.matcher(linea);
        return variableM.matches();
    }
    
    /**
     * Detecta cuatro casos de directivas: ORG, FCB, RESET y END
     * @param linea la línea a analizar
     * @return true, si y sólo si, la línea es alguno de los cuatro casos de directivas.
     */
    private static boolean esDirectiva(String linea){
        Pattern directivaP = Pattern.compile("^ +(ORG|END) +\\$[0-9A-F]{1,4} *(\\*+.*$)?$"); //Expresión regular para detectar ORG o END (opcionalmente un comentario)
        Matcher directivaM = directivaP.matcher(linea);
        
        Pattern directivaFP = Pattern.compile("^ +FCB +\\$[0-9A-F]{2},\\$[0-9A-F]{2} *(\\*+.*$)?$"); //Expresión regular para detectar FCB (opcionalmente un comentario)
        Matcher directivaFM = directivaFP.matcher(linea);
        
        Pattern directivaRP = Pattern.compile("^RESET +FCB +\\$[0-9A-F]{2},\\$[0-9A-F]{2} *(\\*+.*$)?$"); //Expresión regular para detectar RESET(opcionalmente un comentario)
        Matcher directivaRM = directivaRP.matcher(linea);
        
        return directivaM.matches()||directivaRM.matches()||directivaFM.matches();        
    }

    
    /**
     * Identifica y regresa el comentario de la línea parámetro
     * @param aux la línea de la cual se quiere obtener el comentario
     * @return una cadena con el comentario si existe o una línea vacía si no/
     */
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
    /**
     * Identifica y quita el comentario de la línea parámetro
     * @param aux la línea a la que se quiere quitar el comentario
     * @return una cadena con la línea original sin el comentario, si existía, si no sólo la línea originial
     */
    static String retrieveComment(String  aux){
        for(int i=0;  i<aux.length(); i++){
            if(aux.charAt(i) == '*'){
                aux = aux.substring(0,i);
                return aux;
            }
        }
        return aux;
    
    }
    /**
     * Para formar una cadena con lo que se debe imprimir en el archivo LST de salida en este caso (Linea);
     * @return una cadena con la línea a imprimir en el archivo LST
     */
    public String toPrintToFile(){
        return linea;
    }
    
    /**
     * Para obtener uno de los 10 errores detectados por el compilador
     * e indicarle al método principal que se encontró un error
     * @param error un entero con el número de error pedido
     * @return una cadena con el error correspondiente al entero parámetro o la cadena vacía si no corresponde a ninguno
     */
    static String getError(int error){
        if(!Main.error)
            Main.error = true;
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
    /**
     * Oara generar una error en una línea de la primera pasada
     * @param linea la línea donde se generará el error
     * @param cual el número de error que se generará 
     * @param donde el índice de la línea orignial donde se encuentra el error
     * @return una cadena con la línea original y una nueva línea con error en el lugar adecuado
     */
    static String generarError(String linea, int cual, int donde){
        return getSpace()+linea+"\n"+getSpace()+getSpace(donde)+ getError(cual);
    }
    
    /**
     * Para obtener el espacio a imprimir entre el código objeto y el código fuente
     * @return la cadena con el número por defecto de espacios
     */
    public static String getSpace(){
        return espacio;
    }
    
    /**
     * Para obtener un número determinado de espacios
     * @param howMuch el número de espacios que se quiere
     * @return una cadena con howMuch espacios
     */
    public static String getSpace(int howMuch){
        return new String(new char[howMuch]).replace('\0', ' ');
    }
     /**
      * Para ajustar el espacio entre objeto y fuente de acuerdo a lo que vaya a poner en objeto
      * @param takesSpace la cadena se imprimirá en la columa del código objeto
      * @return el espacio que habrá que poner en la entre objeto y fuente luego de poner el código objeto del renglón
      */
    public static String getSpaceFor(String takesSpace){
        return espacio.substring(takesSpace.length());
    }
            

    

   
        
}
 
