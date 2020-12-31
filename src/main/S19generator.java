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
 * @author jorge
 */
public class S19generator {
    private int dirActual = 0;
    private int extra = 0;
    private int lengActual= 0;
    private String leng = "";
    private String checkSum = "";
    private String filename="";
    private String codigo = "";
    private String aImprimir;
    private String aHtml = "";
    private final String defaultField = "S1";
    private final String primera = "S00B00002020202020202020F4";
    private final String ultima = "S90380007C";
    private final int max = 16;
    private boolean generar;
    private boolean org = false;
    private FileWriter fescritor;
    private BufferedWriter bescritor;
    private FileWriter fhescritor;
    private BufferedWriter bhescritor;
    private File file;
    private File html;
    private String codigoHtml="";
    private String iniciHtml = "";
    private String finalHtml= "";
    
    //class Html
    
    public S19generator(String filename, boolean continuar) throws IOException{
        this.generar = continuar;
        if(generar){
            this.filename = filename;
            file = new File(filename+".S19");
            html = new File(filename+".html");
            fescritor = new FileWriter(file,true);
            bescritor = new BufferedWriter(fescritor);
            fhescritor = new FileWriter(html,true);
            bhescritor = new BufferedWriter(fhescritor);
            initHtml();
        }
        aImprimir = primera;
    }
    
    public void buildLine(String dirOrg, boolean continuar) throws IOException{
        this.generar = continuar;
        if(!generar)
            return;
        escribe();
        dirActual = Integer.parseInt(dirOrg,16);
        lengActual = 0;
        org = true;
        //sumaActual += toInt(dirActual);
    }
    
    public void buildLine(String opcode, String operando, boolean continuar) throws IOException{
        this.generar = continuar;
        if(!generar)
            return;
        
        if(!org){
            escribe();
            org = true;
        }
        
        if(addC(opcode,true))
        {
            escribe();
            String toAdd = (extra == 0)? "" : opcode.substring(opcode.length()-extra*2);
            codigo = toAdd;
            codigoHtml = (toAdd.isEmpty())? "" : "<span style = \"color:zblue\">"+toAdd+"</span>"; 
            dirActual += max;
            lengActual += codigo.length()/2;
            
        }
        if(addC(operando,false))
        {
            escribe();
            String toAdd = (extra == 0)? "" : operando.substring(operando.length()-extra*2);
            codigo = toAdd;
            codigoHtml = (toAdd.isEmpty())? "" : "<span style = \"color:red\">"+toAdd+"</span>"; 
            dirActual += max;
            lengActual += codigo.length()/2;
        }
    }
    
    public void end() throws IOException{
        if(!generar){
            //file.deleteOnExit();
            return;
        }
        if(!codigo.isEmpty()){
            construyeLinea();
            construyeHtml();
            escribe();
        }
        aImprimir = ultima;
        aHtml = "<p  style = \"font-family:Courier New,Monaco,monospace;font-size:16px;\">"+ultima+"</p></body></html>";
        escribe();
        try{
            bescritor.close();
            fescritor.close();
            bhescritor.close();
            fhescritor.close();
            
        }catch(IOException e){
            Logger.getLogger(S19generator.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    private boolean addC(String toAdd, boolean opcode){
        int extra = sePasaPor(toAdd);
        String color = (opcode)? "blue" : "red";
        if(extra < 0){
            lengActual += toAdd.length()/2;
            //sumaActual += toInt();
            codigo += toAdd;
            codigoHtml += "<span style = \"color:"+color+"\">"+toAdd+"</span>"; 
            return false;
        }
        else if(extra > 0)
        {
            this.extra = extra;
            toAdd = toAdd.substring(0, toAdd.length()-extra*2);
            lengActual += toAdd.length()/2;
            //sumaActual += toInt(toAdd);
            codigo += toAdd;
            codigoHtml += "<span style = \"color:" + color + "\">" + toAdd + "</span>";
            return true;
        }
        else
        {
            this.extra = 0;
            lengActual += toAdd.length()/2;     
            //sumaActual += toInt(toAdd);
            codigo += toAdd;
            codigoHtml += "<span style = \"color:" + color + "\">" + toAdd + "</span>";
            return true;
        }
    }
    
    private  void construyeLinea(){
        aImprimir = defaultField + toHex(lengActual + 3,2)+ toHex(dirActual,4) + codigo;
        aImprimir += checkSum();
    }
    
    private void construyeHtml(){
        aHtml = "<p style = \"font-family:Courier New,Monaco,monospace;font-size:16px;\">"+defaultField + toHex(lengActual+3,2)+toHex(dirActual,4) + codigoHtml + checkSum + "</p>";
    }
    
    private void initHtml() throws IOException{
        String inicial = 
                "<!DOCTYPE html>"
                + "<html lang=\"es\">"
                + "<head>"
                +       "<meta charset=\"UTF-8\"> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                +       "<title>"+filename+"</title>"
                + "</head>"
                + "<body>"
                + "<h2> Codigo Objeto en Formato Motorola</h2>"
                + "<p> Los opcodes estan en color <span style = \"color:blue\"> azul</span> y los operandos estan en color <span style = \"color:red\">rojo</span>.</p>"
                + "<p style = \"font-family:Courier New,Monaco,monospace;font-size:16px;\">"+primera+"</p>";
        bhescritor.write(inicial);
        bhescritor.newLine();
        bhescritor.flush();
    }
    
    private int sePasaPor(String aAnadir){
        int nuevo = aAnadir.length()/2;
        return lengActual+nuevo-max;        
    }
    
    private int toInt(String cadenaHex){

        return Integer.parseInt(cadenaHex, 16);
    }
    
    private String toHex(int a, int len){
        return  String.format("%0"+len+"X", a);
    }
    
    private String checkSum(){
        String[] pairs = aImprimir.substring(2).split("(?<=\\G.{2})");
        String sum = sumAt8B(pairs);
        return checkSum = String.format("%02X", 255 - Integer.parseInt(sum, 16));
    }
    
    private String sumAt8B(String[] pairs){
        int sum = 0;
        for(String hex: pairs){
            sum += toInt(hex);
        }
        String hex = Integer.toHexString(sum);
        return hex.substring(hex.length()-2);
    }
    private void escribe() throws IOException{
        if(!generar)
            return;
        if(aImprimir.isEmpty() && aHtml.isEmpty()){
            construyeLinea();
            construyeHtml();
        }
        bescritor.write(aImprimir);
        bescritor.newLine();
        bescritor.flush();
        bhescritor.write(aHtml);
        bhescritor.newLine();
        bhescritor.flush();
        
        lengActual = 0;
        codigo = "";
        codigoHtml = "";
        aImprimir = "";
        aHtml = "";
    }
    
    
}
