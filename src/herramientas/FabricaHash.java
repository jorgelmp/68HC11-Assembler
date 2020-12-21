/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package herramientas;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author jorge
 */
public class FabricaHash implements ActionListener{

    private enum Options{
        MNEMONICO,
        INMEDIATO,
        DIRECTO,
        INDX,
        INDY,
        EXTENDIDO,
        INHERENTE,
        RELATIVO,
        SER,
        TER
    }
    
    private final int MNEMONICO = 0;
    private final int INMEDIATO = 1;
    private final int DIRECTO = 2;
    private final int INDX = 3;
    private final int INDY = 4;
    private final int EXTENDIDO = 5;
    private final int INHERENTE = 6;
    private final int RELATIVO  = 7;
    
    private String[][] instructionSet;
    private HashSet<String> setMnem;
    private HashMap<String,String> mapInm;
    private HashMap<String,String> mapDir;
    private HashMap<String,String> mapIndx;
    private HashMap<String,String> mapIndy;
    private HashMap<String,String> mapExt;
    private HashMap<String,String> mapInh;
    private HashMap<String,String> mapRel;
    private JFrame frame;
    
    public FabricaHash(String[][] instructionSet){
        
        this.instructionSet = instructionSet;
        this.frame = new JFrame();
        
        JButton mne = new JButton("Mnemonicos");
        mne.setActionCommand(Options.MNEMONICO.name());
        mne.addActionListener(this);
        
        JButton inm = new JButton("Inmediato");
        inm.setActionCommand(Options.INMEDIATO.name());
        inm.addActionListener(this);
        
        JButton dir = new JButton("Directo");
        dir.setActionCommand(Options.DIRECTO.name());
        dir.addActionListener(this);
        
        JButton indx = new JButton("Indexado X");
        indx.setActionCommand(Options.INDX.name());
        indx.addActionListener(this);
        
        JButton indy = new JButton("Indexado Y");
        indy.setActionCommand(Options.INDY.name());
        indy.addActionListener(this);
        
        JButton ext = new JButton("Extendido");
        ext.setActionCommand(Options.EXTENDIDO.name());
        ext.addActionListener(this);
        
        JButton inh = new JButton("Inherente");
        inh.setActionCommand(Options.INHERENTE.name());
        inh.addActionListener(this);
        
        JButton rel = new JButton("Relativo");
        rel.setActionCommand(Options.RELATIVO.name());
        rel.addActionListener(this);
        
        JButton ser = new JButton("Guardar (Serializar)");
        ser.setActionCommand(Options.SER.name());
        ser.addActionListener(this);
       
        JButton terminar = new JButton("Terminar");
        terminar.setActionCommand(Options.TER.name());
        terminar.addActionListener(this);
        
        
        GridBagLayout grid = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        frame.setLayout(grid);
        
        frame.setTitle("Fabrica de HashMaps");
       
        GridBagLayout layout = new GridBagLayout();
        
        frame.setLayout(layout);
        
        
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.gridx =0;
        gbc.gridy = 0;
        frame.add(mne);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        frame.add(inm, gbc);
        
        gbc.gridx = 2;
        gbc.gridy = 0;
        frame.add(dir, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 3;
        gbc.gridy = 0;
        frame.add(ext,gbc);
        
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(indx,gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(indy,gbc);
        
        gbc.gridx = 2;
        gbc.gridy = 2;
        frame.add(inh,gbc);
        
        gbc.gridx = 3;
        gbc.gridy = 2;
        frame.add(rel,gbc);
        
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        gbc.ipady = 20;
        gbc.gridx = 1;
        gbc.gridy = 4;
        frame.add(ser,gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        frame.add(terminar,gbc);
        
        
        frame.setSize(600,300);
        frame.setPreferredSize(frame.getSize());
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
    
    @Override
    public void actionPerformed(ActionEvent evt){
        if(evt.getActionCommand() == Options.MNEMONICO.name())
        {
            if(setMnem == null)
                setMnem = crearMnemonicoHashSet();
            JOptionPane.showMessageDialog(frame, "¡HashSet de nombres de mnemonicos creado!");
        }
        else if (evt.getActionCommand() == Options.INMEDIATO.name()) 
        {
            if(mapInm == null)
                mapInm = crearHashMap(INMEDIATO);
            JOptionPane.showMessageDialog(frame,"¡HashMap de mnemonicos inmediatos creado!");
        } 
        else if (evt.getActionCommand() == Options.DIRECTO.name()) 
        {
            if(mapDir==null)
                mapDir = crearHashMap(DIRECTO);
            JOptionPane.showMessageDialog(frame,"¡HashMap de mnemonicos directos creado!");
        }
        else if (evt.getActionCommand() == Options.INDX.name())
        {
            if(mapIndx == null)
                mapIndx = crearHashMap(INDX);
            JOptionPane.showMessageDialog(frame,"¡HashMap de mnemonicos indexados de x creado!");
        }
        else if (evt.getActionCommand() == Options.INDY.name())
        {
            if(mapIndy == null)
                mapIndy = crearHashMap(INDY);
            JOptionPane.showMessageDialog(frame,"¡HashMap de mnemonicos indexados de y creado!");
        }
        else if (evt.getActionCommand() == Options.EXTENDIDO.name())
        {
            if(mapExt == null)
                mapExt = crearHashMap(EXTENDIDO);
            JOptionPane.showMessageDialog(frame,"¡HashMap de mnemonicos extendidos creado!");
        }
        else if(evt.getActionCommand() == Options.INHERENTE.name())
        {
            if(mapInh == null)
                mapInh = crearHashMap(INHERENTE);
            JOptionPane.showMessageDialog(frame,"¡HashMap de mnemonicos inherentes creado!");
        }
        else if(evt.getActionCommand() == Options.RELATIVO.name())
        {
            if(mapRel == null)
                mapRel = crearHashMap(RELATIVO);
            JOptionPane.showMessageDialog(frame,"¡HashMap de mnemonicos relativos creado!");                        
        }
        else if(evt.getActionCommand() == Options.SER.name())
        {
            String message = "Se guardaron: \n";
            if(!(setMnem == null)){
                Serializador.guardarObjeto(setMnem, "Mnemonicos.ser");
                message += "\tMnemonicos.ser\n";
            }
            if(!(mapInm == null)){
                Serializador.guardarObjeto(mapInm, "Inmediatos.ser");
                message += "\tInmediatos.ser\n";
            }
            if(!(mapDir==null)){
                Serializador.guardarObjeto(mapDir, "Directos.ser");
                message += "\tDirectos.ser\n";
            }
            if(!(mapExt ==null)){
                Serializador.guardarObjeto(mapExt, "Extendidos.ser");
                message += "\tExtendidos.ser\n";
            }
            if(!(mapIndx == null)){
                Serializador.guardarObjeto(mapIndx, "IndexadosX.ser");
                message += "\tIndexadosX.ser\n";
            }
            if(!(mapIndy == null)){
                Serializador.guardarObjeto(mapIndy, "IndexadosY.ser");
                message += "\tIndexadosY.ser\n";
            }
            if(!(mapInh ==null)){
                Serializador.guardarObjeto(mapInh, "Inherentes.ser");
                message += "\tInherentes.ser\n";
            }
            if(!(mapRel == null)){
                Serializador.guardarObjeto(mapRel,"Relativos.ser");
                message += "\tRelativos.ser\n";
            }
            if(message.equals("Se guardaron\n"))
                message = "No se guardó ningún HashMap porque no se ha creado ningun HashMap";
            
            JOptionPane.showMessageDialog(frame, message);
        }
        else if(evt.getActionCommand() == Options.TER.name())
            frame.dispose();;
    }
        
    private HashMap<String,String> crearHashMap(int tipo){
        HashMap<String,String> map = new HashMap<String,String>();
        for(int i=0; i<instructionSet.length;i++){
            if(!(instructionSet[i][tipo].contains("--")))
                map.put(instructionSet[i][MNEMONICO], instructionSet[i][tipo]);
        }
        return map;
    }
    
    private HashSet<String> crearMnemonicoHashSet(){
        HashSet<String> set = new HashSet<String>();
        for(int i=0; i<instructionSet.length;i++)
            set.add(instructionSet[i][MNEMONICO]);
        return set;
    }
}
