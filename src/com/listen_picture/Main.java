package com.listen_picture;

import org.apache.commons.cli.*;

import javax.imageio.ImageIO;
import javax.sound.midi.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class Main {

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

        String filePath = args[args.length - 1];

        switch (mode) {
            case "play":
                play();
                break;
            case "encode":
//                encode(filePath);
                break;
            case "decode":
//                try {
//                    decode(filePath);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                break;
            case "vk-test": {
                vkTest();
            }
        }
    }


    static void vkTest() {
        try {
            Gui.main(new String[]{});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // проигрывание картинки, т.е. генерация музыки по картинке
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


        MidiChannel[] mChannels = midiSynthesizer.getChannels();
        midiSynthesizer.loadInstrument(instruments[0]);//load an instrument
        MidiChannel channel = mChannels[0];

        javax.swing.JLabel label = new javax.swing.JLabel(new ImageIcon(img));

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(label);
        f.pack();
        f.setLocation(200, 200);
        f.setVisible(true);
        Graphics graphics = label.getGraphics();

        for (int y = 0; y < img.getHeight(); ++y) {
            for (int x = 0; x < img.getWidth(); ++x) {
                graphics.drawLine(x,y, x, y);
                f.pack();

                Color color = new Color(img.getRGB(x, y));
                int note = (color.getBlue() + color.getGreen() + color.getRed()) / 6;

                channel.noteOn(note, 100);
                System.out.println("x: " + x + " y: " + y + " Note: " + note);

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
                channel.noteOff(note);
            }
        }
    }




    static class QueueItem<T> {
        public int order;
        public T items;
        public QueueItem(int o, T i){
            this.order = o;
            this.items = i;
        }
    }
    public static class MyThread extends Thread {
        public int length;
        public BufferedImage image;
        public Thread playingThread;

        public MyThread(Runnable target){
            super(target);
        }
    }


    public static void decode(BufferedImage image, int length) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes1 = baos.toByteArray();
        System.out.println(bytes1.length);

        int readLength = 0;
        try {
            for (int x = 0; x < image.getWidth(); ++x) {
                for (int y = 0; y < image.getHeight(); ++y) {
                    Color color = new Color(image.getRGB(x, y));
                    readLength += 3;
                    if (readLength <= length) {
                        baos.write(new byte[]{
                                (byte) (color.getRed() - Converter.byteShift),
                                (byte) (color.getGreen() - Converter.byteShift),
                                (byte) (color.getBlue() - Converter.byteShift)
                        });
                    } else if (readLength - 1 == length) {
                        baos.write(new byte[]{
                                (byte) (color.getRed() - Converter.byteShift),
                                (byte) (color.getGreen() - Converter.byteShift),
                        });
                        break;
                    } else if (readLength - 2 == length) {
                        baos.write(new byte[]{
                                (byte) (color.getRed() - Converter.byteShift),
                        });
                        break;
                    } else {
                        break;
                    }
                }
                if (readLength > length) break;
            }

        } catch (IOException e) {
        }


        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream decodedInputStream = new ByteArrayInputStream(bytes);

        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = getAudioInputStream(decodedInputStream);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }

        AudioFilePlayer player = new AudioFilePlayer();
        try {
            System.out.println("play start");
            player.playAudioInputStream(audioInputStream);
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }
}
