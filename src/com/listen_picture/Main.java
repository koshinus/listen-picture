package com.listen_picture;

import org.apache.commons.cli.*;

import javax.imageio.ImageIO;
import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;


public class Main {

    public static void drawPoint(Graphics graphics, int x, int y) {
        graphics.drawLine(x, y, x, y);
    }

    public static void main(String[] args) throws MidiUnavailableException {
        Options options = new Options();
        options.addOption("m", "mode", true, "pick mode");

        CommandLineParser parser = new DefaultParser();
        String mode = "";
        try {
            CommandLine cmd = parser.parse(options, args);
            mode = cmd.getOptionValue("m");
        } catch (ParseException e) {
        }

        String filePath = args[args.length - 1] ;

        switch (mode) {
            case "play":
                play();
                break;
            case "encode":
                encode(filePath);
                break;
        }

    }

    public static void play() throws MidiUnavailableException {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("samples/sample.jpg"));
        } catch (IOException e) {
        }

        Synthesizer midiSynthesizer = MidiSystem.getSynthesizer();
        midiSynthesizer.open();

        //get and load default instrument and channel lists
        Instrument[] instruments = midiSynthesizer.getDefaultSoundbank().getInstruments();

        System.out.print(instruments);

        MidiChannel[] mChannels = midiSynthesizer.getChannels();
        midiSynthesizer.loadInstrument(instruments[0]);//load an instrument
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

                Color color = new Color(img.getRGB(x, y));
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
    }

    private static void encode(String audioPath) {

        AudioFilePlayer.main(new String[]{audioPath});

//        InputStream in = new ByteArrayInputStream(new byte[]{});
//        try {
//            in = new FileInputStream(audioPath);
//        } catch (FileNotFoundException e) {
//            System.out.println("not found");
//        }
//
//        System.out.println("asd");
//
//        try {
//            AudioStream as = new AudioStream(in);
//            AudioPlayer.player.start(as);
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//            }
//            AudioPlayer.player.stop(as);
//        } catch (IOException e) {
//            System.out.println("io");
//        }

    }
}
