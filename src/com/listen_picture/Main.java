package com.listen_picture;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import org.apache.commons.cli.*;

import javax.imageio.ImageIO;
import javax.sound.midi.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class Main {
    static final java.lang.Integer APP_ID = 5770405;
    public static String vkCode;

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
            case "vk-test": {
                vkTest();
            }
        }

    }


    static void vkTest() {


        Gui.main(new String[]{});

        System.out.println(Main.vkCode);

        TransportClient transportClient = HttpTransportClient.getInstance();
        VkApiClient vk = new VkApiClient(transportClient);

//        HttpGet get = new HttpGet("http://vk.com/login.php?email=%s&pass=%s");

        UserAuthResponse authResponse = null;
        try {
            authResponse = vk.oauth()
                    .userAuthorizationCodeFlow(APP_ID, Config.CLIENT_SECRET, "https://oauth.vk.com/blank.html", vkCode)
                    .execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }

        UserActor actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());


        try {
            List audios = vk.audio().get(actor).count(10).execute().getItems();
            System.out.println();

//            java.util.List users = vk.users().get(actor)
//                    .fields(UserField.VERIFIED, UserField.SEX, UserField.SCREEN_NAME)
//                    .lang(Lang.RU)
//                    .execute();

        } catch (ApiException | ClientException e) {
            e.printStackTrace();
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

//    private static void encode2(String audioPath) {
//        final File file = new File(audioPath);
//
//        try {
//            final byte[] buffer = new byte[5000];
//            FileInputStream fis = new FileInputStream(file);
//            FileChannel fc = fis.getChannel();
//
//            boolean continueRead = true;
//            int length = 4;
//            while (continueRead) {
//                fis.read(buffer, 0, length);
//                ByteBuffer bb2 = ByteBuffer.allocate(30);
//                fc.read(bb2, 0);
//                bb2.flip() ;
//                ByteBuffer bb = ByteBuffer.wrap(buffer);
//                if (MPEGFrameHeader.isMPEGFrame(bb2)) {
//                    System.out.println("YES");
//                    MPEGFrameHeader header = null;
//                    header = MPEGFrameHeader.parseMPEGHeader(bb);
//                    System.out.println(header.getFrameLength());
//                } else {
//                    System.out.println("No");
//                    System.out.println(bb);
//                    continueRead = false;
//                }
//            }
//
////            final MP3File mp3File = new MP3File(file);
////            mp3File.getMP3AudioHeader()
////            long framesNumber = mp3File.getMP3AudioHeader().getNumberOfFrames();
////            System.out.println(file.length());
////            System.out.println(framesNumber);
////            System.out.println(file.length() / framesNumber);
//        } catch (IOException | InvalidAudioFrameException e) {
//            e.printStackTrace();
//        }
//    }

    static int FRAME_LENGTH = 1500;

    static class QueueItem<T> {
        public int order;
        public T items;
        public QueueItem(int o, T i){
            this.order = o;
            this.items = i;
        }
    }

    static ConcurrentLinkedQueue<QueueItem<byte[]>> bytesQueue = new ConcurrentLinkedQueue<>();
    static ConcurrentLinkedQueue<QueueItem<Color[]>> colorsQueue = new ConcurrentLinkedQueue<>();

    static Random rand = new Random();

    static public class MyTask implements Runnable {
        QueueItem<byte[]> item;
        public MyTask(QueueItem<byte[]> i) {
            this.item = i;
        }

        public void run() {
//            QueueItem<byte[]> item = bytesQueue.poll();
            byte[] bytes = item.items;
            Color[] result = new Color[FRAME_LENGTH / 3];
            for (int i = 0; i < FRAME_LENGTH; i += 3) {
                result[i / 3] = new Color(bytes[i] + byteShift, bytes[i + 1] + byteShift, bytes[i + 2] + byteShift);
            }
            System.out.println("Done " + item.order);
//            try {
//                Thread.sleep(rand.nextInt(50) + 1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            colorsQueue.add(new QueueItem<>(item.order, result));
        }
    }

    static public class Worker extends Thread {
        ExecutorService executor;

        public Worker(){
            this.executor = Executors.newSingleThreadExecutor();
        }

        public void run() {
            while( true ) {
                if (bytesQueue.isEmpty()){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                } else {
                    executor.execute(new MyTask(bytesQueue.poll()));
                }
            }
        }
    }

    // преобразование музыки в картинку, сохраняет в файл
    private static void encode(String audioPath) {
        final File file = new File(audioPath);

        BufferedImage bufferedImage = new BufferedImage(2000, 500, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();

        try (final AudioInputStream in = getAudioInputStream(file)) {
            int columns = 0;
            int nSum = 0;

            Worker w = new Worker();
            w.start();

            int n = 0;
            while(n != -1){
                final byte[] buffer = new byte[FRAME_LENGTH];
                n = in.read(buffer, 0, buffer.length);
                nSum += n;

                System.out.println("Pulled " + columns);

                bytesQueue.add(new QueueItem<>(columns, buffer));
                columns += 1;
            }

            int columnsDrawed = 0;
            while( columnsDrawed != columns ) {
                if (colorsQueue.isEmpty()){
                    try {
                        Thread.sleep(500);
                        System.out.println("wait2 " + columnsDrawed + "/" + columns);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    QueueItem<Color[]> item = colorsQueue.poll();
                    int x = item.order;
                    for (int y = 0; y < 500; y+=1){
                        graphics.setColor(item.items[y]);
                        drawPoint(graphics, x, y);
                    }
                    System.out.println("Drawed " + x);
                    columnsDrawed += 1;
                }
            }

            w.interrupt();

            ImageIO.write(bufferedImage, "png", new File("saved." + nSum + ".png"));

        } catch (UnsupportedAudioFileException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    // проигрывание картинки, сгенерированной с помощью функции encode
    public static void decode(String path) throws IOException {
        final File file = new File(path);
        String[] split = path.split("\\.", -1);
        int length = Integer.parseInt(split[split.length - 2]);

        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                                (byte) (color.getRed() - byteShift),
                                (byte) (color.getGreen() - byteShift),
                                (byte) (color.getBlue() - byteShift)
                        });
                    } else if (readLength - 1 == length) {
                        baos.write(new byte[]{
                                (byte) (color.getRed() - byteShift),
                                (byte) (color.getGreen() - byteShift),
                        });
                        break;
                    } else if (readLength - 2 == length) {
                        baos.write(new byte[]{
                                (byte) (color.getRed() - byteShift),
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
