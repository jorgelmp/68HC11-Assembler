/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 * Clase cuyas instancias representan uno de los cuatro casos de directivas aceptadas
 * @author fercho
 */
public class Directiva extends Linea{
    String directiva;
    String operando;
    String direccion;
    String reset;
    
    /**
     * Construye un objeto Directiva de acuerdo a una línea
     * @param linea la línea con la directiva
     */
    public Directiva(String linea){
        super(linea);
        if(!Main.segunda){
            String[] partes = aux.split(" +");//Se divide en palabras
            
            //Se da formato de espacios de acuerdo al número de palabras en la primera pasada
            switch(partes.length){
                case 1:
                    this.linea = getSpace(10)+partes[0]+getSpace(32-partes[0].length())+comentario;
                    break;
                case 2:
                    this.linea = getSpace(10)+partes[0]+getSpace(10-partes[0].length())+partes[1]+getSpace(22-partes[1].length())+comentario;
                    break;
                case 3:
                    this.linea = partes[0]+getSpace(10-partes[0].length())+partes[1]+getSpace(10-partes[1].length())+partes[2]+getSpace(22-partes[2].length())+comentario;
                    break;
                default:
                    break;
            }
            this.direccion = Main.getAddress(); //La dirección de la línea en el código objeto
        }
        else{
            operando = linea.trim().split(" +")[4].substring(1); //Si es la segunda pasada sólo se busca reconocer el operando, el cual está en la quinta palabra
        }
    }
    
    /**
     * Para obtener la línea que se imprimirá en el archivo LST
     * @return una cadena con la línea a imprimir
     */
    @Override
    public String toPrintToFile(){
        if(Main.segunda)
            return linea;
        
        String[] partes; //Se divide en palabras
        
        if(linea.charAt(0) == ' '){ // Si hay espacios es uno de tres casos
            aux = linea.trim();
            partes = aux.split(" +");
            
            directiva = partes[0]; //La directiva siempre está en la primera palabra
            operando = partes[1]; //El operando siempre está en la segunda palabra
            
            switch(directiva){
                case "ORG": 
                    return casoORG(); 
                case "FCB": 
                    return casoFCB();
                case "END":
                    return casoEND();    
            }
        }
        
        //Si no hay espacios se asume que es RESET
        
        partes = aux.split(" +");
        reset = partes[0]; //RESET está en primera palabra
        directiva = partes[1]; //Directiva (siempre FCB) en segunda
        operando = partes[2]; //Operando en tercera
        return casoFCB();     
    }
    
    /**
     * Se trata con el caso ORG, dando formato, generando código objeto y actulizando la dirección actual en Main
     * @return la cadena a imprimir en el LST
     */
    private String casoORG(){
        this.direccion = operando.substring(1); 
        Main.setAddress(direccion);
        return getSpace(5) + direccion + getSpace(7) + linea;
    }
    
    /**
     * Se trata con el caso END, dando formato, generando código objeto y actualizanco la bandera fin en Main.
     * @return la cadena a imprimir en el LST
     */
    private String casoEND(){
        //Falta ajustar espacios
        Main.fin = true;
        return getSpace(16) + linea;
    }
    
    /**
     * Se trata con el caso FCB, dando formato y generando el código objeto
     * @return la cadena a imprimir en el LST
     */
    private String casoFCB(){
        String[] partesOp = operando.split(",");
        String operando2 = partesOp[0].substring(1) + partesOp[1].substring(1);
        Main.updateAddress(2);
        return direccion + " "  + operando2 + getSpace(7) + linea;
    }
    
    /**
     * Regresa el operando de la directiva
     * @return 
     */
    public String getOperando(){
        return operando;
    }
}