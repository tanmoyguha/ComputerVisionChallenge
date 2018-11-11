package VisionEnhancement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gaurav on 4/18/2018.
 */
public class Processor {

    public static void main(String[] args) {

        String testData = "Images\\dataTest\\test";
        String trainData = "Images\\dataTest\\train";

        File[] testD = new File(testData).listFiles();
        File[] trainM = new File(trainData).listFiles();

        BufferedWriter writer = null;

        try {
            File file = new File("Output.text");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            writer = new BufferedWriter(new FileWriter(file));

            writer.write("image_name,category");
            writer.newLine();

            for (int i = 0; i < testD.length; i++) {

                BufferedImage img1 = ImageIO.read(testD[i]);
                Map<String, Double> cSMap = new HashMap<String, Double>();
                Map<String, Double> cMPMap = new HashMap<String, Double>();
                Map<String, Double> cMCPMap = new HashMap<String, Double>();

                for (int j = 0; j < trainM.length; j++) {
                    File[] trainC = trainM[j].listFiles();
                    String categoryName = trainM[j].getName();
                    double maxValue = 0.0;
                    double maxPercentageCount = 0.0;
                    double totalSimilarityValue = 0.0;

                    for (int k = 0; k < trainC.length; k++) {
                        File trainI = trainC[k];
                        BufferedImage trainB = ImageIO.read(trainI);

                        double d = getSimilarityValue(img1, trainB);

                        totalSimilarityValue += d;
                        maxPercentageCount = (d == maxValue) ? (maxPercentageCount + 1) : 1;
                        maxValue = d > maxValue ? d : maxValue;
                    }

                    totalSimilarityValue = totalSimilarityValue / trainC.length;
                    maxPercentageCount = maxPercentageCount / trainC.length;

                    cSMap.put(categoryName, totalSimilarityValue);
                    cMPMap.put(categoryName, maxValue);
                    cMCPMap.put(categoryName, maxPercentageCount);
                }

                Comparator<Object> comparator = new Comparator<Object>() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        Map.Entry<String, Double> e1 = (Map.Entry<String, Double>) o1;
                        Map.Entry<String, Double> e2 = (Map.Entry<String, Double>) o2;

                        return (e2.getValue() - e1.getValue()) == 0.0 ? 0 : ((e2.getValue() - e1.getValue()) > 0.0 ? 1 : -1);
                    }
                };

                /*Map.Entry<String, Double>[] cSArray = new Map.Entry[cSMap.size()];
                Map.Entry<String, Double>[] cMPArray = new Map.Entry[cMPMap.size()];
                Map.Entry<String, Double>[] cMCPArray = new Map.Entry[cMCPMap.size()];

                Arrays.sort(cSMap.entrySet().toArray(cSArray), comparator);
                Arrays.sort(cSMap.entrySet().toArray(cMPArray), comparator);
                Arrays.sort(cSMap.entrySet().toArray(cMCPArray), comparator);*/

                Map<String, Double> valueMap = new HashMap<>();
                for (String s : cMCPMap.keySet()) {
                    double d = cSMap.get(s) * 0.25 + cMPMap.get(s) * 0.5 + cMCPMap.get(s) * 0.25;
                    valueMap.put(s, d);
                }

                Map.Entry<String, Double>[] valueArray = new Map.Entry[valueMap.size()];
                Arrays.sort(valueMap.entrySet().toArray(valueArray), comparator);

                writer.write(testD[i].getName() + "," + valueArray[0].getKey());
                writer.newLine();

                if (i % 1000 == 0) {
                    writer.flush();
                }
            }

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static double getSimilarityValue(BufferedImage img, BufferedImage trImg) {
        double d = 0.0;

        BufferedImage img1 = null;
        BufferedImage img2 = null;

        if (img.getHeight() == trImg.getHeight() && img.getWidth() == trImg.getWidth()) {
            img1 = img;
            img2 = trImg;
        } else {
//            double d1 = (img.getHeight() - trImg.getHeight()) / img.getHeight();
//            double d2 = (img.getWidth() - trImg.getWidth()) / img.getWidth();
            int minHeight = img.getHeight() < trImg.getHeight() ? img.getHeight() : trImg.getHeight();
            int minWidth = img.getWidth() < trImg.getWidth() ? img.getWidth() : trImg.getWidth();

            img1 = getCompressedImage(img, minHeight, minWidth);
            img2 = getCompressedImage(trImg, minHeight, minWidth);
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

    public static BufferedImage getCompressedImage(BufferedImage img, int height, int width) {
        return CompressImage.compress(img, height, width);
    }
}
