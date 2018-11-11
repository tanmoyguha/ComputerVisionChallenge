package VisionEnhancement;

import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;

/**
 * Created by gaurav on 4/20/2018.
 */
public class CustomRunnableTemp implements Runnable {
    SimilarityMetric metric;
    final BufferedImage test;
    final BufferedImage train;
    //    final File trainImage;
    final CountDownLatch down;

    CustomRunnableTemp(SimilarityMetric metric, BufferedImage test, BufferedImage train/*File trainImage*/, CountDownLatch down) {
        this.metric = metric;
        this.test = test;
        this.train = train;
//        this.trainImage = trainImage;
        this.down = down;
    }

    @Override
    public void run() {
        try {
//            BufferedImage train = ImageIO.read(trainImage);
            double d = getSimilarityValue(test, train);
            metric.calculate(d);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            down.countDown();
        }
    }

    public double getSimilarityValue(BufferedImage img, BufferedImage trImg) {
        double d = 0.0;

        BufferedImage img1 = null;
        BufferedImage img2 = null;

        if (img.getHeight() == trImg.getHeight() && img.getWidth() == trImg.getWidth()) {
            img1 = img;
            img2 = trImg;
        } else {
//            double d1 = (img.getHeight() - trImg.getHeight()) / img.getHeight();
//            double d2 = (img.getWidth() - trImg.getWidth()) / img.getWidth();
            /*int minHeight = img.getHeight() < trImg.getHeight() ? img.getHeight() : trImg.getHeight();
            int minWidth = img.getWidth() < trImg.getWidth() ? img.getWidth() : trImg.getWidth();

            img1 = getCompressedImage(img, minHeight, minWidth);
            img2 = getCompressedImage(trImg, minHeight, minWidth);*/

            return d;
        }

        for (int i = 0; i < img1.getWidth(); i++) {
            for (int j = 0; j < img1.getHeight(); j++) {
//                int similarity = 0;

                int img1RGB = img1.getRGB(i, j);
                int img2RGB = img2.getRGB(i, j);

                d += (img1RGB == img2RGB ? 1 : 0);
            }
        }

        d = (d * 100) / (img1.getHeight() * img1.getWidth());

        return d;
    }

    public BufferedImage getCompressedImage(BufferedImage img, int height, int width) {
        return CompressImage.compress(img, height, width);
    }
}
