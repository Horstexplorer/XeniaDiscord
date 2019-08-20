package de.netbeacon.xeniadiscord.util.gui;

import de.netbeacon.xeniadiscord.util.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Terminal extends JFrame implements Runnable{

    public Terminal(){
        try{
            JFrame frame = new JFrame();

            //Output
            JTextArea outtextArea = new JTextArea();
            outtextArea.setFont(new Font("monospaced", Font.ITALIC, 12));
            outtextArea.setEnabled(false);
            outtextArea.setBackground(Color.BLACK);
            outtextArea.setForeground(Color.WHITE);
            outtextArea.setBorder(BorderFactory.createEmptyBorder());
            PrintStream outprintStream = new PrintStream(new CustomOutputStream(outtextArea)); //out
            System.setOut(outprintStream);
            System.setErr(outprintStream);
            JScrollPane jsp = new JScrollPane(outtextArea);
            frame.add(jsp);

            //Input
            JTextField input = new JTextField();
            input.setBackground(Color.BLACK);
            input.setForeground(Color.WHITE);
            input.setBorder(BorderFactory.createEmptyBorder());
            frame.add(input, BorderLayout.SOUTH);
            //InStream
            PipedInputStream pis = new PipedInputStream();
            PipedOutputStream pos = new PipedOutputStream(pis);
            System.setIn(pis);
            input.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        pos.write(input.getText().getBytes());
                        pos.write(10);
                        pos.flush();
                    } catch (IOException ex) {
                        //ex.printStackTrace();
                    }
                    System.out.println("LOCAL>"+input.getText());
                    input.setText("");
                }
            });

            //Frame
            frame.pack();
            frame.setSize(1900,1050);
            frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            frame.getContentPane().setBackground(Color.BLACK);
            if(Boolean.parseBoolean(new Config().load("bot_gui_exitonclose"))){
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
            frame.setTitle("XeniaDiscord");
            frame.setVisible( true );


        }catch (Exception e){
            System.err.println("Error "+e);
        }
    }

    @Override
    public void run() {
        //future use
    }
}

