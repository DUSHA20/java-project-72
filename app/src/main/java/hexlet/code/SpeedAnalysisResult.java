package hexlet.code;

public class SpeedAnalysisResult {
    private boolean isOptimal;
    private String message;

    public SpeedAnalysisResult(boolean isOptimal, String message) {
        this.isOptimal = isOptimal;
        this.message = message;
    }

    public boolean isOptimal() {
        return isOptimal;
    }

    public String getMessage() {
        return message;
    }
}
