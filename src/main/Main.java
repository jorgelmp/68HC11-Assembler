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
    
    private static int numeroLinea = 0;
    private static String lineaActual = numeroLinea + " A";
    private static int direccionActual = 0;
    private static String direccionHex = "0";
    
    public static String nombreArchivoR = "";
    public static String nombreArchivoE = "";
    public static int totalLinea = 0;
    public static boolean fin = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        

        //String[][] instruction = Serializador.abrirArreglo2("68HC11.ser");
        //FabricaHash fab = new FabricaHash(instruction);
        //String[][] mc68hc11 = datasheet.getSubExcel(145, 8, 8, 1);
        //printStringArray(mc68hc11,145,8);
        /*String var0 = "SHUAI EQU $0002";
        Linea vart0 = Linea.getLineType(var0);
        System.out.println(vart0.toPrintToFile());
        String var = "JORGE EQU $200A*hello";
        Linea vart = Linea.getLineType(var);
        System.out.println(vart.toPrintToFile());
        String org = "    ORG    $8000";
        Linea lin0 = Linea.getLineType(org);
        System.out.println(lin0.toPrintToFile());
        String etiqueta = "MAIN   *main";
        Linea lin = Linea.getLineType(etiqueta);
        System.out.println(lin.toPrintToFile());
        String ins = "     ldaa    JORGE    *hellooo";
        Linea inst = Linea.getLineType(ins);
        System.out.println(inst.toPrintToFile());
        String i2 = "     ldaa    #JORGE    *helloo ";
        Linea i2s = Linea.getLineType(i2);
        System.out.println(i2s.toPrintToFile());
        String i3 = "     ldaa    $B2   *hrl ,x";
        Linea i3s = Linea.getLineType(i3);
        System.out.println(i3s.toPrintToFile());
        String i4 = "     ldaa    SHUAI,x      *joo #wqk";
        Linea i4s = Linea.getLineType(i4);
        System.out.println(i4s.toPrintToFile());
        String i5 = "     ldaa    $20,Y   ";
        Linea i5s = Linea.getLineType(i5);
        System.out.println(i5s.toPrintToFile());
        String i6 = "     asr     JORGE   ";
        Linea i6s = Linea.getLineType(i6);
        System.out.println(i6s.toPrintToFile());
        String if6 = "     FCB $80,$00 *hola como estas   ";
        Linea if6s = Linea.getLineType(if6);
        System.out.println(if6s.toPrintToFile());
        String ir6 = "RESET FCB $80,$A0   ";
        Linea ir6s = Linea.getLineType(ir6);
        System.out.println(ir6s.toPrintToFile());
        String end = "    END    $8000";
        Linea endo = Linea.getLineType(end);
        System.out.println(endo.toPrintToFile());
        
        */
                
        
        
        nombreArchivoR = JOptionPane.showInputDialog("Ingresar la ruta del archivo(*.ASC):");
        File archivoR = new File(nombreArchivoR);//tiene que estar en la carpeta de ficheros
        
        //cambia la terminacion del archivo de salida
        int longitud=nombreArchivoR.length();
        nombreArchivoE = nombreArchivoR.substring(0, longitud-4)+".LST";
        File archivoE = new File(nombreArchivoE);
        
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
            //podemos verificar aqui si la ultima linea es END

            
            
        }else{
            JOptionPane.showMessageDialog(null, "No se encontro el archivo");
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
    
    public String getLineNumber(){
        String actual = lineaActual;
        numeroLinea += 1;
        return actual;
    }
    
    public String aImprmir(String codigo){
        return getLineNumber() + " " + codigo;
    }
        
    

}

