/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;


/**
 *
 * @author jorge
 */
public class Serializador {
    public static void guardarObjeto(Object o,String filename){
        try{
            FileOutputStream fo = new FileOutputStream(filename);
            ObjectOutputStream oo = new ObjectOutputStream(fo);
  
            oo.writeObject(o);
            
            oo.close();
            fo.close();
            
            //System.out.println("Objeto serializado");
            
        }catch(IOException e){
            System.out.println("IOException caught");        
        }
    }
    
    public static HashMap<String,String> abrirHashMapString2(String filename){
        HashMap<String,String> map = null;
        try
        {
            FileInputStream i = new FileInputStream(filename);
            //InputStream i = Serializador.class.getResourceAsStream(File.separator+"herramientas"+File.separator+filename);
            ObjectInputStream oi = new ObjectInputStream(i);
  
            map = (HashMap<String,String>) oi.readObject();
            
            oi.close();
            i.close();
            
            //System.out.println("HashMap de cadenas deserializado");
        }
        catch(IOException e)
        {
            
            System.out.println("No se pudo deserializar el HashMap");
            System.out.println("IOException caught");        
        }
        catch(ClassNotFoundException c){            
            System.out.println("No se pudo deserializar el HashMap");
            System.out.println("ClassNotFoundException caught");
        }
        catch(NullPointerException n){
            System.out.println("fuck you");
        }
        
        return map;
        
    }
    
    public static HashSet<String> abrirHashSetString(String filename){
        HashSet<String> set = new HashSet<String>();
        try
        {
            FileInputStream i = new FileInputStream(filename);
            //InputStream i = Serializador.class.getResourceAsStream(File.separator+"herramientas"+File.separator+filename);
            ObjectInputStream oi = new ObjectInputStream(i);
  
            set = (HashSet<String>) oi.readObject();
            
            oi.close();
            i.close();
            
            //System.out.println("HashSet de cadenas deserializado");
        }
        catch(IOException e)
        {
            
            System.out.println("No se pudo deserializar el HashSet");
            System.out.println("IOException caught");        
        }
        catch(ClassNotFoundException c){            
            System.out.println("No se pudo deserializar el HashSet");
            System.out.println("ClassNotFoundException caught");
        }
        catch(NullPointerException n){
            System.out.println("you");
        }
        
        return set;
    }
    
    public static String[][] abrirArreglo2(String filename){
        String[][] arr = null;
        try
        {
            FileInputStream i = new FileInputStream(filename);
            //InputStream i = Serializador.class.getResourceAsStream(filename);
            ObjectInputStream o1 = new ObjectInputStream(i);
  
            arr = (String[][]) o1.readObject();
            
            o1.close();
            i.close();
            
            //System.out.println("Arreglo bidimensional deserializado");
        }
        catch(IOException e)
        {
            System.out.println("No se pudo deserializar el arreglo");
            System.out.println("IOException caught");        
        }
        catch(ClassNotFoundException c)
        {
            System.out.println("No se pudo deserializar el arreglo");
            System.out.println("ClassNotFoundException caught");
        }
        
        return arr;
    }
    
    
}

