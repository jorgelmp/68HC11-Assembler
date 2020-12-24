package main;
import herramientas.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.lang.ClassNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
/**
 *
 * @author jorge
 */
public class Main {
    
    private static int numeroLinea = 1;
    private static int direccionActual = 0;
    private static String direccionHex = "0";
    
    public static String nombreArchivoR = "";
    public static String nombreArchivoE = "";
    public static String nombreArchivoF = "";
    public static int totalLinea = 0;
    public static boolean segunda = false;
    public static boolean fin = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        

        //String[][] instruction = Serializador.abrirArreglo2("68HC11.ser");
        //FabricaHash fab = new FabricaHash(instruction);
        //String[][] mc68hc11 = datasheet.getSubExcel(145, 8, 8, 1);
        //printStringArray(mc68hc11,145,8);         
        
        
        nombreArchivoR = JOptionPane.showInputDialog("Ingresar la ruta del archivo(*.ASC):");
        File archivoR = new File(nombreArchivoR);//tiene que estar en la carpeta de ficheros
        
        //cambia la terminacion del archivo de salida
        int longitud=nombreArchivoR.length();
        nombreArchivoE = nombreArchivoR.substring(0, longitud-4)+".txt";
        nombreArchivoF = nombreArchivoR.substring(0, longitud-4)+".LST";

        File archivoE = new File(nombreArchivoE);
        archivoE.deleteOnExit();
        File archivoF = new File(nombreArchivoF);

        
        if(archivoR.exists()==true){//verifica que existe el archivo
            try {
                archivoE.createNewFile();// crea el archivo de salida
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "No se pudo crear el archivo");
            }
            
            // Aqui empieza todo el proceso
            
            Leer read = new Leer();// para leer el archivo linea por linea
            Leer read1 = new Leer();//para saber cuantos lineas hay en el archivo
            Escribir write = new Escribir();
            Linea actual;
            totalLinea = read1.totalLineas(archivoR.getAbsolutePath());
            String linea;
            for(int i=1;i<=totalLinea;i++){
                read.leer(archivoR.getAbsolutePath(),i);
                linea = read.linea;
                actual = Linea.getLineType(linea);
                String lineaactual = actual.toPrintToFile();
                System.out.println(lineaactual);
                write.escribir(archivoE.getAbsolutePath(), lineaactual);
                if(fin)
                    break;   
           }
           
            
           segunda = !segunda;
            //podemos verificar aqui si la ultima linea es END
            
        }else{
            JOptionPane.showMessageDialog(null, "No se encontro el archivo");
        }
        System.out.println("\n\n\n");
        try {
                archivoF.createNewFile();// crea el archivo de salida
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "No se pudo crear el archivo");
            }
            
            // Aqui empieza todo el proceso
            
        Leer read = new Leer();// para leer el archivo linea por linea
        Leer read1 = new Leer();//para saber cuantos lineas hay en el archivo
        Escribir write = new Escribir();
        Linea actual;
        totalLinea = read1.totalLineas(archivoE.getAbsolutePath());
        String linea;
        for(int i=1;i<=totalLinea;i++){
            read.leer(archivoE.getAbsolutePath(),i);
            linea = read.linea;
            actual = Linea.getLineType(linea);
            String lineaactual = actual.toPrintToFile();
            System.out.println(lineaactual);
            write.escribir(archivoF.getAbsolutePath(), lineaactual);
        }
    }
    
    
    public static String getAddress(){
        return Integer.toHexString(direccionActual).toUpperCase();
    } 
    
    public static void updateAddress(int i){
       direccionActual+=i;
    }
    
    public static void setAddress(int address){
        direccionActual = address;
        direccionHex = Integer.toHexString(address).toUpperCase();
    }
    
    public static void setAddress(String hex){
        direccionActual = Integer.parseInt(hex, 16);
        direccionHex = hex.toUpperCase();
    }
    
    public static String getLineNumber(){
        String actual = numeroLinea + " A ";
        numeroLinea += 1;
        return actual;
    }
    
    public static String aImprmir(String codigo){
        return getLineNumber() + " " + codigo;
    }
        
    

}

