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

    public String getApproximatedLoadTimesAsString() {
        StringBuilder result = new StringBuilder();

        List<Integer> intervalEnds = divideIntoIntervals(3); // Получаем конечные значения интервалов
        List<Double[]> approximations = calculateApproximations(); // Получаем коэффициенты аппроксимации

        // Проходим по каждому интервалу и рассчитываем приблизительное время загрузки
        for (int i = 0; i < intervalEnds.size(); i++) {
            int intervalEnd = intervalEnds.get(i);
            Double[] coefficients = approximations.get(i);

            // Получаем коэффициенты линейной аппроксимации для текущего интервала
            double a = coefficients[0];
            double b = coefficients[1];

            // Рассчитываем время загрузки для конечного значения размера контента в текущем интервале
            double contentLength = intervalEnd;
            double approximatedLoadTime = a * contentLength + b;

            // Добавляем результат в строку
            result.append("Interval ").append(i + 1).append(" (Content Length up to ").append(intervalEnd).append("): ").append(approximatedLoadTime).append(" ms<br>");
        }

        return result.toString();
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
