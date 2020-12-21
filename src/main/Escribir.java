/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author shens
 */
public class Escribir {
    public void escribir(String nombreDeArchivo, String linea){
    
        FileWriter escribir;
        try {
            escribir = new FileWriter(nombreDeArchivo,true);
            BufferedWriter bufferW= new BufferedWriter(escribir);
            bufferW.write(linea);
            bufferW.newLine();
            bufferW.flush();
            escribir.close();
        } catch (IOException ex) {
            Logger.getLogger(Escribir.class.getName()).log(Level.SEVERE, null, ex);
        }
        
           
        
        
    }
}
