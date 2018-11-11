package VisionEnhancement;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by gaurav on 4/19/2018.
 */
public class CompressImage {

    public static BufferedImage compress(BufferedImage image, int height, int width) {
        BufferedImage cImage = null;

        if (image.getHeight() == height && image.getWidth() == width) {
            cImage = image;
        } else {
            cImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            final Graphics2D graphics2D = cImage.createGraphics();
            graphics2D.setComposite(AlphaComposite.Src);
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.drawImage(image, 0, 0, width, height, null);
            graphics2D.dispose();
        }

        return cImage;
    }

    public static void main(String[] args) {

        try {
            BufferedImage bufferedImage = ImageIO.read(new File("Images\\dataTest\\n02667093_1172_0.jpg"));
            BufferedImage compressedImage = compress(bufferedImage, 180, 200);
            ImageIO.write(compressedImage, "jpg", new File("Images\\dataTest\\n02667093_11 _2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
