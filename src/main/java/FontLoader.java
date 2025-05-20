
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Ricardo
 */
public class FontLoader {
    public static Font loadFont(String path, float size) throws IOException {
        try {
            // Cargar la fuente como un recurso
            InputStream is = FontLoader.class.getResourceAsStream(path);
            if (is == null) {
                System.err.println("No se pudo cargar la fuente: " + path);
                return new Font("Arial", Font.PLAIN, (int)size);
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(size);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, (int)size);
        }
    }
}
