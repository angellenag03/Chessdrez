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
    private static final float LOW_VOLUME = -45.0f; // Volumen base más bajo
    private static final float MAIN_VOLUME = -8.0f;  // Volumen normal
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
            
            // Cargar y convertir tema de jaque
            InputStream checkSrc = getClass().getResourceAsStream("/music/" + checkFile);
            AudioInputStream originalCheckStream = AudioSystem.getAudioInputStream(
                new BufferedInputStream(checkSrc));
            AudioFormat checkFormat = getSupportedFormat(originalCheckStream.getFormat());
            AudioInputStream checkStream = AudioSystem.getAudioInputStream(checkFormat, originalCheckStream);
            
            // Abrir ambos clips
            mainTheme.open(mainStream);
            checkTheme.open(checkStream);
            
            // Configurar controles de volumen
            mainVolumeControl = (FloatControl) mainTheme.getControl(FloatControl.Type.MASTER_GAIN);
            checkVolumeControl = (FloatControl) checkTheme.getControl(FloatControl.Type.MASTER_GAIN);
            
            // Establecer volúmenes iniciales
            setVolume(mainVolumeControl, MAIN_VOLUME);
            setVolume(checkVolumeControl, LOW_VOLUME);
            
            // Iniciar ambos temas simultáneamente
            mainTheme.loop(Clip.LOOP_CONTINUOUSLY);
            checkTheme.loop(Clip.LOOP_CONTINUOUSLY);
            
            // Cerrar streams
            mainStream.close();
            checkStream.close();
            originalMainStream.close();
            originalCheckStream.close();
            
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
        // Ya no necesitamos iniciar el tema aquí porque ya está sonando
        fadeInCheckTheme();
    }
    
    private void stopCheckTheme() {
        fadeOutCheckTheme();
    }
    
    private void fadeInCheckTheme() {
        new Thread(() -> {
            float step = (MAIN_VOLUME - LOW_VOLUME) / FADE_STEPS;
            for (int i = 1; i <= FADE_STEPS; i++) {
                float volume = LOW_VOLUME + (step * i);
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
            float step = (MAIN_VOLUME - LOW_VOLUME) / FADE_STEPS;
            for (int i = FADE_STEPS; i >= 0; i--) {
                float volume = LOW_VOLUME + (step * i);
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
                // Si falla, intentar una ruta relativa sin la barra inicial
                audioSrc = getClass().getResourceAsStream("sounds/" + soundFile);
            }
            
            if (audioSrc == null) {
                // Intentar una tercera alternativa usando ClassLoader
                audioSrc = getClass().getClassLoader().getResourceAsStream("sounds/" + soundFile);
            }
            
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
            case "hover": return "hover.wav";  // Añadir directamente hover.wav
            case "click": return "click.wav";  // Añadir directamente click.wav
            default: return null;
        }
    }
}