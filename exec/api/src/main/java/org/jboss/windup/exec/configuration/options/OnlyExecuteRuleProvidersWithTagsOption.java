package org.jboss.windup.exec.configuration.options;

import org.jboss.windup.config.AbstractPathConfigurationOption;
import org.jboss.windup.config.InputType;
import org.jboss.windup.config.ValidationResult;

/**
 * Only RuleProviders with the tags contained in this option will be executed;
 * Ignored if empty.
 *
 * @author Ondrej Zizka, ozizka@redhat.com
 */
public class OnlyExecuteRuleProvidersWithTagsOption extends AbstractPathConfigurationOption
{
    public static final String NAME = "tags";


    public OnlyExecuteRuleProvidersWithTagsOption()
    {
        super(false);
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public String getLabel()
    {
        return "Only execute RuleProviders with given tags.";
    }

    @Override
    public String getDescription()
    {
        return "Only RuleProviders with the tags contained in this option will be executed. Ignored if empty.";
    }

    @Override
    public Class<?> getType()
    {
        return String.class;
    }

    @Override
    public InputType getUIType()
    {
        return InputType.MANY;
    }

    @Override
    public boolean isRequired()
    {
        return false;
    }

    public ValidationResult validate(Object valueObj)
    {
        return ValidationResult.SUCCESS;
    }
}
