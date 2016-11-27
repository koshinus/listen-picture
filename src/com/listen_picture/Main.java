package com.listen_picture;

import org.apache.commons.cli.*;

import javax.imageio.ImageIO;
import javax.sound.midi.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;


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

        String filePath = args[args.length - 1];

        switch (mode) {
            case "play":
                play();
                break;
            case "encode":
                encode(filePath);
                break;
            case "decode":
                try {
                    decode(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
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

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
                channel.noteOff(note);
            }
        }
    }

    static final int byteShift = 128;

    // преобразование музыки в картинку, сохраняет в файл
    private static void encode(String audioPath) {
        final File file = new File(audioPath);

        BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();

        try (final AudioInputStream in = getAudioInputStream(file)) {
            final byte[] buffer = new byte[3];
            int x = 0;
            int y = 0;

            for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
                Color color = new Color(buffer[0] + byteShift, buffer[1] + byteShift, buffer[2] + byteShift);
                graphics.setColor(color);
                drawPoint(graphics, x, y);

                x += 1;
                if (x > 1000) {
                    x = 0;
                    y += 1;
                }
            }

            ImageIO.write(bufferedImage, "png", new File("saved.png"));

        } catch (UnsupportedAudioFileException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    // проигрывание картинки, сгенерированной с помощью функции encode
    //   еще не работает
    public static void decode(String path) throws IOException {
        final File file = new File(path);

        BufferedImage in = null;
        try {
            in = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedImage image = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);

        PipedInputStream pipedIn = new PipedInputStream();
        final PipedOutputStream out = new PipedOutputStream(pipedIn);

        new Thread(() -> {
            try {
                for (int y = 0; y < image.getHeight(); ++y) {
                    for (int x = 0; x < image.getWidth(); ++x) {
                        Color color = new Color(image.getRGB(x, y));
                        out.write(new byte[]{
                                (byte) (color.getRed() - byteShift),
                                (byte) (color.getGreen() - byteShift),
                                (byte) (color.getBlue() - byteShift)
                        });
                    }
                }

            } catch (IOException e) {
            }
        }).start();


        AudioInputStream aaa = null;
        try {
            aaa = getAudioInputStream(pipedIn);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }

        AudioFilePlayer player = new AudioFilePlayer();
        try {
            System.out.println("play start");
            player.playAudioInputStream(aaa);
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }
}
