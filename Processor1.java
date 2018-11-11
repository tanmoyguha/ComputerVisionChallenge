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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by gaurav on 4/18/2018.
 */
public class Processor1 {

    public static void main(String[] args) {
        long l1 = System.currentTimeMillis();
        System.out.println(l1);

        String testData = "Images\\dataset\\test";
        String trainData = "Images\\dataset\\train";

        File[] testD = new File(testData).listFiles();
        File[] trainM = new File(trainData).listFiles();

        BufferedWriter writer = null;
        ExecutorService execute = null;

        try {
            File file = new File("Output.csv");
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

            execute = Executors.newFixedThreadPool(16);
            Map<String, BufferedImage> bufferedImageMap = new HashMap<>();

            for (int i = 0; i < testD.length; i++) {

                final BufferedImage img1 = ImageIO.read(testD[i]);
//                Map<String, Double> cSMap = new HashMap<String, Double>();
//                Map<String, Double> cMPMap = new HashMap<String, Double>();
//                Map<String, Double> cMCPMap = new HashMap<String, Double>();

                Map<String, SimilarityMetric> similarityMap = new HashMap<>();

                /*for (int j = 0; j < trainM.length; j++) {
                    String name = trainM[j].getName();
                    SimilarityMetric metric = new SimilarityMetric(name, trainM[j].listFiles().length);
                    similarityMap.put(name, metric);
                }*/

                for (int j = 0; j < trainM.length; j++) {
                    File[] trainC = trainM[j].listFiles();
                    String categoryName = trainM[j].getName();

                    SimilarityMetric metric = new SimilarityMetric(categoryName, trainC.length);
                    similarityMap.put(categoryName, metric);

                    CountDownLatch latch = new CountDownLatch(trainC.length);

//                    double maxValue = 0.0;
//                    double maxPercentageCount = 0.0;
//                    double totalSimilarityValue = 0.0;

                    for (int k = 0; k < trainC.length; k++) {
                        File trainI = trainC[k];
                        BufferedImage trainB = bufferedImageMap.get(categoryName + trainI.getName());

                        if (trainB == null) {
                            trainB = ImageIO.read(trainI);
                            bufferedImageMap.put(categoryName, trainB);
                        }

                        /*double d = getSimilarityValue(img1, trainB);

                        totalSimilarityValue += d;
                        maxPercentageCount = (d == maxValue) ? (maxPercentageCount + 1) : 1;
                        maxValue = d > maxValue ? d : maxValue;*/

                        execute.submit(new CustomRunnable(metric, img1, trainB, latch));
//                        execute.submit(new CustomRunnable(metric, img1, trainI, latch));
                    }

                    while (latch.getCount() > 1) {
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    /*totalSimilarityValue = totalSimilarityValue / trainC.length;
                    maxPercentageCount = maxPercentageCount / trainC.length;

                    cSMap.put(categoryName, totalSimilarityValue);
                    cMPMap.put(categoryName, maxValue);
                    cMCPMap.put(categoryName, maxPercentageCount);*/
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
                for (String s : similarityMap.keySet()) {
                    SimilarityMetric metric = similarityMap.get(s);
                    double d = metric.getTotalSimilarityValue() * 2 + metric.getMaxValue() * 4 + metric.getMaxPercentageCount() * 2;
//                    double d = metric.getMaxValue() * 0.5 + metric.getMaxPercentageCount() * 0.25;
                    valueMap.put(s, d);
                }

                Map.Entry<String, Double>[] valueArray = new Map.Entry[valueMap.size()];
                Arrays.sort(valueMap.entrySet().toArray(valueArray), comparator);

                writer.write(testD[i].getName() + "," + valueArray[0].getKey());
                System.out.print(testD[i].getName() + "," + valueArray[0].getKey());
                writer.newLine();
                System.out.println();

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

                if (execute != null) {
                    execute.shutdown();
                }
            }


            long l2 = System.currentTimeMillis();
            System.out.println(l2);

            long l = (l2 - l1) / 1000;
            System.out.println(l);
        }
    }

    /*public static double getSimilarityValue(BufferedImage img, BufferedImage trImg) {
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
    }*/

    public static BufferedImage getCompressedImage(BufferedImage img, int height, int width) {
        return CompressImage.compress(img, height, width);
    }
}
