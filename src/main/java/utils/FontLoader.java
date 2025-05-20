package utils;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Ricardo & angelsn
 */
public class FontLoader {
    
    public static Font loadFont(float size) throws IOException {
        try {
            // Cargar la fuente como un recurso
            InputStream is = FontLoader.class.getClassLoader().getResourceAsStream("alagard.ttf");
            if (is == null) {
                System.err.println("No se pudo cargar la fuente: " + "alagard.ttf");
                return new Font("Century Gothic", Font.PLAIN, (int)size);
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(size);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("Century Gothic", Font.PLAIN, (int)size);
        }
    }
}
