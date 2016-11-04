package promakh.listen_picture;

import javax.imageio.ImageIO;
import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Console;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void drawPoint(Graphics graphics, int x, int y) {
        graphics.drawLine(x, y, x, y);
    }

    public static void main(String[] args) throws MidiUnavailableException {

        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("samples/fractal.png"));
        } catch (IOException e) {
        }

        Synthesizer midiSynthesizer = MidiSystem.getSynthesizer();
        midiSynthesizer.open();

        //get and load default instrument and channel lists
        Instrument[] instr = midiSynthesizer.getDefaultSoundbank().getInstruments();
        MidiChannel[] mChannels = midiSynthesizer.getChannels();
        midiSynthesizer.loadInstrument(instr[0]);//load an instrument
        MidiChannel channel = mChannels[0];

        JLabel label = new JLabel(new ImageIcon(img));

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(label);
        f.pack();
        f.setLocation(200, 200);
        f.setVisible(true);
        Graphics graphics = label.getGraphics();

        for (int y = 0; y < img.getHeight(); ++y) {
            for (int x = 0; x < img.getWidth(); ++x) {
                drawPoint(graphics, x, y);
                f.pack();

                Color color = new Color(img.getRGB(x,y));
                int note = (color.getBlue() + color.getGreen() + color.getRed()) / 6;

                channel.noteOn(note, 100);
                System.out.println("x: " + x + " y: " + y + " Note: " + note);
//                try { Thread.sleep(200); } catch( InterruptedException e ) { }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
                channel.noteOff(note);
            }
        }

//        mChannels[0].noteOn(60, 100);//On channel 0, play note number 60 with velocity 100
//        try { Thread.sleep(1000); } catch( InterruptedException e ) { }
//        mChannels[0].noteOff(60);//turn of the note


//        for (int i = 0; i < 127; i++)
//        {
//            channel.noteOn(i, 100);
//            System.out.println("Note: " + i);
//            try { Thread.sleep(200); } catch( InterruptedException e ) { }
//            channel.noteOff(i);
//        }
    }
}
