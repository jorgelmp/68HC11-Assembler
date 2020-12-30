package main;
import herramientas.*;
import java.io.File;
import java.io.IOException;

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
    public static boolean error = false;

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
           if(!fin)
               write.escribir(archivoE.getAbsolutePath(), Linea.getSpace()+Linea.getError(10));
           
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
        try{
            S19generator sninet = new S19generator(nombreArchivoR.substring(0, longitud-4)+".S19",!error);
        String linea;
        for(int i=1;i<=totalLinea;i++){
            Direccionamiento dir;
            Directiva org;
            read.leer(archivoE.getAbsolutePath(),i);
            linea = read.linea;
            actual = Linea.getLineType(linea);
            String lineaactual = actual.toPrintToFile();
            System.out.println(lineaactual);
            if(actual instanceof Direccionamiento){
                dir = (Direccionamiento) actual;
                sninet.buildLine(dir.getOpcode(), dir.getOperando(), !error);
            }
            else if(actual instanceof Directiva){
                org = (Directiva) actual;
                sninet.buildLine(org.getOperando(), !error);
            }
            write.escribir(archivoF.getAbsolutePath(), lineaactual);
        }
        sninet.end();
        }catch(IOException e){
            System.out.println("Error de IO");
        }
    }
    
    
    public static String getAddress(){
        return String.format("%04X",direccionActual);
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
        String actual = " "+Linea.getSpace(String.valueOf(totalLinea*2).length()-String.valueOf(numeroLinea).length())+numeroLinea + " A ";
        numeroLinea += 1;
        return actual;
    }
    
    public static String aImprmir(String codigo){
        return getLineNumber() + " " + codigo;
    }
        
    

}

