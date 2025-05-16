package sfx;

import javax.sound.sampled.*;
import java.io.*;
import java.util.Random;

public class SFXManager {
    private static SFXManager instance;
    private Clip currentSong;
    private Random random = new Random();
    
    private SFXManager() {}
    
    public static SFXManager getInstance() {
        if (instance == null) {
            instance = new SFXManager();
        }
        return instance;
    }
    
    public void playSound(String soundType) {
        String soundFile = getRandomSoundFile(soundType);
        if (soundFile == null) {
            System.err.println("Tipo de sonido no válido: " + soundType);
            return;
        }
        
        try {
            InputStream audioSrc = getClass().getResourceAsStream("/sounds/" + soundFile);
            if (audioSrc == null) {
                System.err.println("No se pudo encontrar el archivo: " + soundFile);
                return;
            }
            
            // Convertir el audio a un formato compatible
            AudioInputStream originalStream = AudioSystem.getAudioInputStream(
                new BufferedInputStream(audioSrc));
            AudioFormat targetFormat = getCompatibleFormat(originalStream.getFormat());
            AudioInputStream convertedStream = AudioSystem.getAudioInputStream(targetFormat, originalStream);
            
            Clip clip = AudioSystem.getClip();
            clip.open(convertedStream);
            clip.start();
            
            // Cerrar streams
            originalStream.close();
            convertedStream.close();
        } catch (Exception e) {
            System.err.println("Error al reproducir el sonido " + soundFile + ": " + e.getMessage());
        }
    }
    
    private AudioFormat getCompatibleFormat(AudioFormat originalFormat) {
        // Convertir a PCM_SIGNED 16-bit que es ampliamente compatible
        return new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            originalFormat.getSampleRate(),
            16, // bits por muestra
            originalFormat.getChannels(),
            originalFormat.getChannels() * 2, // bytes por frame (16 bits = 2 bytes)
            originalFormat.getSampleRate(),
            false // little endian
        );
    }
    
    private String getRandomSoundFile(String soundType) {
        int variant = random.nextInt(4);
        
        switch(soundType.toLowerCase()) {
            case "delete": return "delete" + variant + ".wav";
            case "grab": return "grab" + variant + ".wav";
            case "put": return "put" + variant + ".wav";
            default: return null;
        }
    }
    
    public void playSong(String file) {
        stopSong();
        
        try {
            InputStream musicSrc = getClass().getResourceAsStream("/music/" + file);
            if (musicSrc == null) {
                System.err.println("No se pudo encontrar el archivo de música: " + file);
                return;
            }
            
            AudioInputStream originalStream = AudioSystem.getAudioInputStream(
                new BufferedInputStream(musicSrc));
            AudioFormat targetFormat = getCompatibleFormat(originalStream.getFormat());
            AudioInputStream convertedStream = AudioSystem.getAudioInputStream(targetFormat, originalStream);
            
            currentSong = AudioSystem.getClip();
            currentSong.open(convertedStream);
            currentSong.loop(Clip.LOOP_CONTINUOUSLY);
            currentSong.start();
            
            originalStream.close();
            convertedStream.close();
        } catch (Exception e) {
            System.err.println("Error al reproducir la canción: " + e.getMessage());
        }
    }
    
    public void stopSong() {
        if (currentSong != null) {
            if (currentSong.isRunning()) {
                currentSong.stop();
            }
            currentSong.close();
            currentSong = null;
        }
    }
}