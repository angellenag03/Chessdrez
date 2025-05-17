package sfx;

import javax.sound.sampled.*;
import java.io.*;
import java.util.Random;

public class SFXManager {
    private static SFXManager instance;
    private Clip mainTheme;
    private Clip checkTheme;
    private FloatControl mainVolumeControl;
    private FloatControl checkVolumeControl;
    private boolean isInCheck = false;
    private Random random = new Random();
    
    // Configuración de fade
    private static final float MAIN_VOLUME = -30.0f; // Volumen base más bajo
    private static final float CHECK_VOLUME = -8.0f;  // Volumen de jaque
    private static final int FADE_DURATION_MS = 1800;
    private static final int FADE_STEPS = 30;
    
    private SFXManager() {
        try {
            mainTheme = AudioSystem.getClip();
            checkTheme = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    
    public static SFXManager getInstance() {
        if (instance == null) {
            instance = new SFXManager();
        }
        return instance;
    }
    
    public void playSong(String mainFile, String checkFile) {
        stopAllMusic();
        
        try {
            // Cargar y convertir tema principal
            InputStream mainSrc = getClass().getResourceAsStream("/music/" + mainFile);
            AudioInputStream originalMainStream = AudioSystem.getAudioInputStream(
                new BufferedInputStream(mainSrc));
            AudioFormat mainFormat = getSupportedFormat(originalMainStream.getFormat());
            AudioInputStream mainStream = AudioSystem.getAudioInputStream(mainFormat, originalMainStream);
            
            mainTheme.open(mainStream);
            mainVolumeControl = (FloatControl) mainTheme.getControl(FloatControl.Type.MASTER_GAIN);
            setVolume(mainVolumeControl, CHECK_VOLUME);
            mainTheme.loop(Clip.LOOP_CONTINUOUSLY);
            
            // Cargar y convertir tema de jaque
            InputStream checkSrc = getClass().getResourceAsStream("/music/" + checkFile);
            AudioInputStream originalCheckStream = AudioSystem.getAudioInputStream(
                new BufferedInputStream(checkSrc));
            AudioFormat checkFormat = getSupportedFormat(originalCheckStream.getFormat());
            AudioInputStream checkStream = AudioSystem.getAudioInputStream(checkFormat, originalCheckStream);
            
            checkTheme.open(checkStream);
            checkVolumeControl = (FloatControl) checkTheme.getControl(FloatControl.Type.MASTER_GAIN);
            setVolume(checkVolumeControl, MAIN_VOLUME*0.9f); // Inicia con volumen bajo
        } catch (Exception e) {
            System.err.println("Error al cargar música: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private AudioFormat getSupportedFormat(AudioFormat originalFormat) {
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
    
    public void updateCheckState(boolean inCheck) {
        if (this.isInCheck != inCheck) {
            this.isInCheck = inCheck;
            if (inCheck) {
                startCheckTheme();
            } else {
                stopCheckTheme();
            }
        }
    }
    
    private void startCheckTheme() {
        if (!checkTheme.isRunning()) {
            checkTheme.loop(Clip.LOOP_CONTINUOUSLY);
        }
        fadeInCheckTheme();
    }
    
    private void stopCheckTheme() {
        fadeOutCheckTheme();
    }
    
    private void fadeInCheckTheme() {
        new Thread(() -> {
            float step = (CHECK_VOLUME - MAIN_VOLUME) / FADE_STEPS;
            for (int i = 1; i <= FADE_STEPS; i++) {
                float volume = MAIN_VOLUME + (step * i);
                setVolume(checkVolumeControl, volume);
                try {
                    Thread.sleep(FADE_DURATION_MS / FADE_STEPS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
    
    private void fadeOutCheckTheme() {
        new Thread(() -> {
            float step = (CHECK_VOLUME - MAIN_VOLUME) / FADE_STEPS;
            for (int i = FADE_STEPS; i >= 0; i--) {
                float volume = MAIN_VOLUME + (step * i);
                setVolume(checkVolumeControl, volume);
                try {
                    Thread.sleep(FADE_DURATION_MS / FADE_STEPS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
    
    private void setVolume(FloatControl control, float volume) {
        if (volume > control.getMaximum()) volume = control.getMaximum();
        if (volume < control.getMinimum()) volume = control.getMinimum();
        control.setValue(volume);
    }
    
    private void stopAllMusic() {
        if (mainTheme != null) mainTheme.stop();
        if (checkTheme != null) checkTheme.stop();
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
    
}