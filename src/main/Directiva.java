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
public class Directiva extends Linea{
    String directiva;
    String operando;
    String direccion;
    String reset;
    
    public Directiva(String linea){
        super(linea);
        String[] partes = aux.split(" +");
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
        this.direccion = Main.getAddress();
    }
    
    @Override
    public String toPrintToFile(){
        
        String[] partes;
        
        if(linea.charAt(0) == ' '){
            aux = linea.trim();
            partes = aux.split(" +");
            
            directiva = partes[0];
            operando = partes[1];
            
            switch(directiva){
                case "ORG":
                    return casoORG();
                case "FCB":
                    return casoFCB();
                case "END":
                    return casoEND();    
            }
        }
        partes = aux.split(" +");
        reset = partes[0];
        directiva = partes[1];
        operando = partes[2];
        return casoFCB();     
    }
    
    
    private String casoORG(){
        this.direccion = operando.substring(1); 
        Main.setAddress(direccion);
        return getSpace(5) + direccion + getSpace(7) + linea;
    }
    
    private String casoEND(){
        //Falta ajustar espacios
        Main.fin = true;
        return getSpace(16) + linea;
    }
    
    private String casoFCB(){
        String[] partesOp = operando.split(",");
        String operando2 = partesOp[0].substring(1) + partesOp[1].substring(1);
        //Falta ajustar espacios
        return direccion + " "  + operando2 + getSpace(7) + linea;
    }
    
}

//  direccion operando linea