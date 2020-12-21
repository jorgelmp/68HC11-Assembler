/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author shens
 */
public class Leer {
    String linea="";
    
    
    public void leer(String nombreDeArchivoR, int contadorLinea){
        try {
            int contador=0;
            FileReader read;
            read = new FileReader(nombreDeArchivoR);
            BufferedReader bufferR = new BufferedReader(read);
            Escribir esc = new Escribir();
            while(linea!=null){
                linea=bufferR.readLine();
                if(linea!=null){
                    contador+=1;
                }
                if(linea==null){
                    break;
                }
                if(contador==contadorLinea){
                    break;
                }
            }
            read.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Leer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Leer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int totalLineas(String nombreDeArchivoR){
        int contador=0;
        try {
            FileReader read;
            read = new FileReader(nombreDeArchivoR);
            BufferedReader bufferR = new BufferedReader(read);
            Escribir esc = new Escribir();
             while(linea!=null){
                linea=bufferR.readLine();
                if(linea!=null){
                    contador+=1;
                }
            }
            read.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Leer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Leer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return contador;
    }
 
}
