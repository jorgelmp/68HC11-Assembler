package main;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.lang.ClassNotFoundException;
import java.util.Arrays;
/**
 *
 * @author jorge
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String a = "0012";
        System.out.println(String.valueOf(Integer.valueOf(a)));
        /*ExcelReader datasheet = new ExcelReader("68HC11.xlsx");    
        String[][] mc68hc11 = datasheet.getSubExcel(145, 8,8,1);
       
        try{
            FileOutputStream f = new FileOutputStream("MC68HC11.ser");
            ObjectOutputStream o = new ObjectOutputStream(f);
  
            o.writeObject(mc68hc11);
            
            o.close();
            f.close();
            
            System.out.println("Objeto serializado");
            
        }catch(IOException e){
            System.out.println("IOException caught");        
        }
        
        String[][] ser68hc11 = null;
        try
        {
            FileInputStream f1 = new FileInputStream("MC68HC11.ser");
            ObjectInputStream o1 = new ObjectInputStream(f1);
  
            ser68hc11 = (String[][]) o1.readObject();
            
            o1.close();
            f1.close();
            
            System.out.println("Objeto deserializado");
        }
        catch(IOException e)
        {
            System.out.println("IOException caught");        
        }
        catch(ClassNotFoundException c){
            System.out.println("ClassNotFoundException caught");
        }
        */
    }
    
    public static void printStringArray(String[][] s, int m, int n){
        for(int i=0; i<m; i++){
            for(int j=0; j<n;j++){
                System.out.print(s[i][j]+"    ");
            }
            System.out.println();
        }
    }
}

