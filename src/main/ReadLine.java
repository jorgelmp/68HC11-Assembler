/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author jorge
 */
public class ReadLine extends Linea{
    public ReadLine(String linea){
        super(linea);
    }
    public int checkLineType(){
        if(esVacia())
            return Linea.VACIA;
        if(esComentario())
            return Linea.COMENTARIO;
        
        
        
    }
    
    private boolean esVacia(){
        return linea.isEmpty();
    }
    
    private boolean esComentario(){
        if(retrieveComment().isEmpty())
            return false;
        
        if(linea.isEmpty())
            return true;    
        
        return false;
    }
    
    
    private boolean esDireccionamiento(){
        String aux = 
        
    }
    /**
     * Busca un comentario en la línea
     * Si lo encuentra, lo quita de la línea original, lo guarda en el atributo 'comentario'y lo regresa
     * Si no, deja la línea intacta y regresa la cadena vacía.
     * @return el  
     */
    public String retrieveComment(){
        for(int i=0;  i<linea.length(); i++){
            if(linea.charAt(i) == '*'){
                comentario = linea.substring(i);
                linea = linea.substring(0, i);
                return comentario;
            }
        }
        return comentario;
    }
}
