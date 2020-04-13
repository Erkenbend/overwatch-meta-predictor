package predictor.exception;

import java.util.List;

public class InvalidTeamException extends Exception {
    public InvalidTeamException(List<String> violations, Throwable cause) {
        super(getMessageFromViolations(violations), cause);
    }

    public InvalidTeamException(List<String> violations) {
        super(getMessageFromViolations(violations));
    }

    private static String getMessageFromViolations(List<String> violations) {
        return String.format("Invalid Team! Violations: [%s]", String.join(", ", violations));
    }
}
