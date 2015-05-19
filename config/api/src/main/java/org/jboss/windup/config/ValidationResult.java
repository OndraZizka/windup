package org.jboss.windup.config;

/**
 * Indicates the result of a validation operation.
 *
 * @author <a href="mailto:jesse.sightler@gmail.com">Jesse Sightler</a>
 *
 */
public class ValidationResult
{
    /**
     * Indicates that the validation was successful (with no attached message).
     */
    public static final ValidationResult SUCCESS = new ValidationResult(Level.SUCCESS, null);

    public enum Level { ERROR, WARNING, SUCCESS };

    private Level level;
    private String message;

    /**
     * Indicates the success of failure of a validation, as well as a short informative message for the user.
     */
    public ValidationResult(Level level, String message)
    {
        this.level = level;
        this.message = message;
    }

    public boolean isSucess()
    {
        return Level.ERROR.equals(level);
    }

    public Level getLevel()
    {
        return level;
    }

    public String getMessage()
    {
        return message;
    }
}
