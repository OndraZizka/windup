package org.jboss.windup.config.builder;

import javax.enterprise.inject.Vetoed;

import org.jboss.windup.config.AbstractRuleProvider;
import org.jboss.windup.config.loader.RuleLoaderContext;
import org.jboss.windup.config.metadata.MetadataBuilder;
import org.jboss.windup.config.phase.RulePhase;
import org.jboss.windup.graph.GraphContext;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.ConfigurationRuleBuilderCustom;
import org.ocpsoft.rewrite.config.Rule;

/**
 * Used to construct new dynamic {@link AbstractRuleProvider} instances.
 * 
 * @author <a href="mailto:jesse.sightler@gmail.com">Jess Sightler</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Vetoed
public final class RuleProviderBuilder extends AbstractRuleProvider implements
            RuleProviderBuilderSetPhase,
            RuleProviderBuilderMetadataSetPhase,
            RuleProviderBuilderAddDependencies
{
    private final ConfigurationBuilder configurationBuilder;
    private MetadataBuilder metadata;
   
    /**
     * Begin creating a new dynamic {@link AbstractRuleProvider}.
     */
    public static RuleProviderBuilder begin(String id)
    {
        MetadataBuilder builder = MetadataBuilder.forProvider(RuleProviderBuilder.class, id);
        return new RuleProviderBuilder(builder).setMetadataBuilder(builder);
    }

    private RuleProviderBuilder(MetadataBuilder builder)
    {
        super(builder);
        configurationBuilder = ConfigurationBuilder.begin();
    }

    private RuleProviderBuilder setMetadataBuilder(MetadataBuilder builder)
    {
        this.metadata = builder;
        return this;
    }

    public void setOrigin(String origin)
    {
        metadata.setOrigin(origin);
    }

    @Override
    public RuleProviderBuilderAddDependencies setPhase(Class<? extends RulePhase> phase)
    {
        metadata.setPhase(phase);
        return this;
    }

    @Override
    public RuleProviderBuilderAddDependencies addExecuteAfter(String id)
    {
        metadata.addExecuteAfterId(id);
        return this;
    }

    @Override
    public RuleProviderBuilderAddDependencies addExecuteAfter(Class<? extends AbstractRuleProvider> type)
    {
        metadata.addExecuteAfter(type);
        return this;
    }

    @Override
    public RuleProviderBuilderAddDependencies addExecuteBefore(String id)
    {
        metadata.addExecuteBeforeId(id);
        return this;
    }

    @Override
    public RuleProviderBuilderAddDependencies addExecuteBefore(Class<? extends AbstractRuleProvider> type)
    {
        metadata.addExecuteBefore(type);
        return this;
    }

    @Override
    public ConfigurationRuleBuilderCustom addRule()
    {
        ConfigurationRuleBuilderCustom rule = configurationBuilder.addRule();
        return rule;
    }

    @Override
    public ConfigurationRuleBuilderCustom addRule(Rule rule)
    {
        ConfigurationRuleBuilderCustom wrapped = configurationBuilder.addRule(rule);
        return wrapped;
    }

    @Override
    public Configuration getConfiguration(RuleLoaderContext ruleLoaderContext)
    {
        return configurationBuilder;
    }
    
    public MetadataBuilder getMetadataBuilder() {
        return metadata;
    }

    @Override
    public String toString()
    {
        return super.toString();
    }
}
