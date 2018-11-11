package VisionEnhancement;

/**
 * Created by gaurav on 4/19/2018.
 */
public class SimilarityMetric {
    volatile double maxValue;
    volatile double maxPercentageCount;
    volatile double totalSimilarityValue;

    final String categoryName;
    final int count;

    public SimilarityMetric(String categoryName, int count) {
        this.categoryName = categoryName;
        this.count = count;

        maxValue = 0.0;
        maxPercentageCount = 0.0;
        totalSimilarityValue = 0.0;
    }

    public synchronized void calculate(double value) {
        totalSimilarityValue += value;
        maxPercentageCount = (value == maxValue) ? (maxPercentageCount + 1) : ((value < maxValue) ? maxPercentageCount : 1);
        maxValue = value > maxValue ? value : maxValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getMaxPercentageCount() {
        return (maxPercentageCount / count);
    }

    public double getTotalSimilarityValue() {
        return (totalSimilarityValue / count);
    }
}
