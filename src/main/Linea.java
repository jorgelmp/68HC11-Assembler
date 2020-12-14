package main;

/**
 *
 * @author jorge
 */

public class Linea {
    protected String linea;
    protected String instruccion;
    protected String comentario;
    
    public static final int VACIA = 0;
    public static final int ETIQUETA = 1;
    public static final int DIRECTIVA = 2;
    public static final int VARIABLE = 3;
    public static final int COMENTARIO = 4;
    public static final int DIRECCIONAMIENTO = 5;
    

    public Linea(String linea){
        this.linea = linea;
    }
   
        
}
 
