package herramientas;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;  
import org.apache.poi.ss.usermodel.*;  
import org.apache.poi.ss.usermodel.Sheet;  
import org.apache.poi.ss.usermodel.Workbook;  
import org.apache.poi.xssf.usermodel.XSSFWorkbook; 

/**
 *
 * @author jorge
 */

public class ExcelReader {
    private String excelname;
    private XSSFWorkbook wb;
    private FileInputStream f;
    
    
    /**
     * Regresa el contenido (una cadena) de una celda del archivo .xlsx
     * @param vRow el índice de la fila de la celda que se leer
     * @param vColumn el índice de la columna de la celda que se leerá
     * @return  La cadena (String) contenida en la celda [vRow,vColumn]
     */
    private String celdaExcel(int vRow, int vColumn) {
        String value = null;            
        Sheet sheet = wb.getSheetAt(0);   //se coloca el objeto XSSFSheet en un índice inicial  
        Row row = sheet.getRow(vRow); //se obtiene la fila vRow
        Cell cell = row.getCell(vColumn); //se obtiene la celda ubicada en la columna vColumn y de la fila vRow
        try {
            value = cell.getStringCellValue();    //se obtiene el valor de la celda
        } catch (IllegalStateException e) {
            value = String.valueOf((int) cell.getNumericCellValue());
        }
        return value;               //se regresa el valor de la celda  
    }
    
    /**
     * Construye un ExcelReader con el nombre del archivo .xlsx que se quiere leer
     * @param excelname nombre del archivo con extensión .xlsx incluida
     */
    public ExcelReader(String excelname){
        this.excelname = excelname;
    }
    
    /**
     * Inicializa las interfaces para leer del .xlsx
     */
    private void openExcel()throws IOException,FileNotFoundException{
            f = new FileInputStream(excelname);
            wb = new XSSFWorkbook(f);
    }
    private void closeExcel() throws IOException{
        wb.close();
        f.close();
    }
    
    /**
     * Regresa un arreglo bidimensional de cadenas (String) análogo al contenido de una subtabla del archivo .xlsx
     * @param m número de filas del arreglo
     * @param n número de columnas del arreglo
     * @param rbegin fila de .xlsx a partir del cual comienza la subtabla
     * @param cbegin columna del .xlsx a partir de la cual comienza la subtabla
     * @return un arreglo de cadenas que representa una subtabla en Excel
     */
    public String[][] getSubExcel(int m, int n, int rbegin, int cbegin){
        String[][] subExcel = new String[m][n];
        try{
            openExcel();
            for(int i=0; i<m; i++){
                for(int j=0; j<n; j++){
                    subExcel[i][j] = celdaExcel(i+rbegin,j+cbegin); 
                }
            }
            closeExcel();
        }
            catch(FileNotFoundException e){
            System.out.println("Something went wrong");
            e.printStackTrace();
        }
            catch(IOException e1){
            System.out.println("Something went wrong");
            e1.printStackTrace();
        }
        return subExcel;
    }
    
}

/*Copien este código en main para serializar (guardar como archivo) el arreglo bidimensional de cadenas
        ExcelReader datasheet = new ExcelReader("68HC11.xlsx");    
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
*/

/* Copien este código para deserializar el arreglo
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

/* Método para imprimir el contenido del arreglo bidimensional
        public static void printStringArray(String[][] s, int m, int n){
        for(int i=0; i<m; i++){
            for(int j=0; j<n;j++){
                System.out.print(s[i][j]+"    ");
            }
            System.out.println();
        }
    }
*/
