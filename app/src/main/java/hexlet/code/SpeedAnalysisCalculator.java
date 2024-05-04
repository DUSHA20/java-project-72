package hexlet.code;

import java.util.ArrayList;
import java.util.List;

public class SpeedAnalysisCalculator {
    private List<Long> loadTimes;
    private List<Integer> contentLengths;

    public SpeedAnalysisCalculator(List<Long> loadTimes, List<Integer> contentLengths) {
        this.loadTimes = loadTimes;
        this.contentLengths = contentLengths;
    }

    public List<Double[]> calculateApproximations() {
        int numIntervals = 3; // Всегда используем 3 интервала для аппроксимации
        List<Integer> intervalEnds = divideIntoIntervals(numIntervals);
        List<Double[]> approximations = new ArrayList<>();

        for (int i = 0; i < numIntervals; i++) {
            double[] coefficients = calculateLinearApproximation(intervalEnds.get(i));
            approximations.add(new Double[]{coefficients[0], coefficients[1]});
        }

        return approximations;
    }

    private List<Integer> divideIntoIntervals(int numIntervals) {
        List<Integer> intervalEnds = new ArrayList<>();
        int totalSites = contentLengths.size();
        int sitesPerInterval = totalSites / numIntervals;

        for (int i = 0; i < numIntervals - 1; i++) {
            intervalEnds.add(contentLengths.get((i + 1) * sitesPerInterval));
        }
        intervalEnds.add(contentLengths.get(totalSites - 1));

        return intervalEnds;
    }

    private double[] calculateLinearApproximation(int intervalEnd) {
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumX2 = 0;
        int count = 0;

        for (int i = 0; i < contentLengths.size(); i++) {
            if (contentLengths.get(i) <= intervalEnd) {
                double x = contentLengths.get(i);
                double y = loadTimes.get(i);

                sumX += x;
                sumY += y;
                sumXY += x * y;
                sumX2 += x * x;
                count++;
            }
        }

        if (count == 0) return new double[]{0, 0};

        // Вычисление коэффициентов линейной аппроксимации (y = ax + b)
        double a = (count * sumXY - sumX * sumY) / (count * sumX2 - sumX * sumX);
        double b = (sumY - a * sumX) / count;

        return new double[]{a, b};
    }
}
