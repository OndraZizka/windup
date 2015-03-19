package org.jboss.windup.test.exec;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.windup.config.AbstractRuleLifecycleListener;
import org.jboss.windup.config.AbstractRuleProvider;
import org.jboss.windup.config.GraphRewrite;
import org.jboss.windup.config.RuleProvider;
import org.jboss.windup.config.metadata.RuleMetadata;
import org.jboss.windup.config.metadata.RuleMetadataType;
import org.jboss.windup.config.metadata.RuleProviderRegistryCache;
import org.jboss.windup.config.operation.Log;
import org.jboss.windup.exec.WindupProcessor;
import org.jboss.windup.exec.configuration.WindupConfiguration;
import org.jboss.windup.exec.configuration.options.ExcludeTagsOption;
import org.jboss.windup.exec.configuration.options.IncludeTagsOption;
import org.jboss.windup.exec.rulefilters.EnumerationOfRulesFilter;
import org.jboss.windup.exec.rulefilters.RuleFilter;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.GraphContextFactory;
import org.jboss.windup.util.exception.WindupException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.Context;
import org.ocpsoft.rewrite.context.EvaluationContext;

/**
 * Test for the tags include/exclude - RuleProvider execution filtering based on tags.
 *
 * How this tests works:
 *
 * The 3 RuleProviders have different tags.
 * There are 4 executions, each time with different include/exclude tags.
 * Through the RuleExecutionListener, execution of rules is observed,
 * and the same listener, at the end of execution, checks whether the right set of rules was executed.
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
@RunWith(Arquillian.class)
public class TagsIncludeExcludeTest
{
    //private static final Logger log = Logging.get(TagsIncludeExcludeTest.class);

    @Deployment
    @Dependencies({
        @AddonDependency(name = "org.jboss.forge.furnace.container:cdi"),
        @AddonDependency(name = "org.jboss.windup.utils:windup-utils"),
        @AddonDependency(name = "org.jboss.windup.graph:windup-graph"),
        @AddonDependency(name = "org.jboss.windup.config:windup-config"),
        @AddonDependency(name = "org.jboss.windup.exec:windup-exec"),
    })
    public static ForgeArchive getDeployment()
    {
        //AddonDependencyEntry[] entries = classToAddonDepEntries(TagsIncludeExcludeTest.class);
        final ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
            .addBeansXML()
            .addPackages(true, RuleFilter.class.getPackage())
            .addAsAddonDependencies(
                AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                AddonDependencyEntry.create("org.jboss.windup.utils:windup-utils"),
                AddonDependencyEntry.create("org.jboss.windup.graph:windup-graph"),
                AddonDependencyEntry.create("org.jboss.windup.config:windup-config"),
                AddonDependencyEntry.create("org.jboss.windup.exec:windup-exec")
            );
        return archive;
    }


    @Inject
    private WindupProcessor processor;

    @Inject
    private GraphContextFactory contextFactory;

    @Inject
    private RuleProviderRegistryCache cache;



    private final static EnumerationOfRulesFilter testRuleProviders =
            new EnumerationOfRulesFilter(TestTagsA1B1Rules.class, TestTagsARules.class, TestTagsBRules.class);


    public static class RuleExecutionListener extends AbstractRuleLifecycleListener
    {
        Map<Class<? extends RuleProvider>, Boolean> executedRules = new HashMap();

        @Override
        public void beforeExecution(GraphRewrite event)
        {
            event.getRewriteContext().put("testData", new HashMap());
        }


        @Override
        public void beforeRuleEvaluation(GraphRewrite event, Rule rule, EvaluationContext context)
        {
            RuleProvider provider = (RuleProvider) ((Context)rule).get(RuleMetadataType.RULE_PROVIDER);
            executedRules.put(provider.getClass(), Boolean.FALSE);
        }


        @Override
        public void afterRuleConditionEvaluation(GraphRewrite event, EvaluationContext context, Rule rule, boolean result)
        {
            RuleProvider provider = (RuleProvider) ((Context)rule).get(RuleMetadataType.RULE_PROVIDER);
            executedRules.put(provider.getClass(), Boolean.TRUE);
        }


        @Override
        public void afterExecution(GraphRewrite event)
        {
            //Map<RuleProvider, Boolean> executedRules = (Map<RuleProvider, Boolean>) event.getRewriteContext().get("testData");
            Set<Class<? extends RuleProvider>> shouldHaveRun =
                    (Set<Class<? extends RuleProvider>>) event.getGraphContext().getOptionMap().get("rulesThatShouldRun");
            Assert.assertEquals(shouldHaveRun.contains(TestTagsA1B1Rules.class), executedRules.get(TestTagsA1B1Rules.class));
            Assert.assertEquals(shouldHaveRun.contains(TestTagsARules.class), executedRules.get(TestTagsARules.class));
            Assert.assertEquals(shouldHaveRun.contains(TestTagsBRules.class), executedRules.get(TestTagsBRules.class));
        }
    }


    @Test
    public void testIncludeTags()
    {
        goTest(Arrays.asList("tagA1"), null, new HashSet(Arrays.asList(TestTagsARules.class, TestTagsA1B1Rules.class)));
    }

    @Test
    public void testExcludeTags()
    {
        goTest(null, Arrays.asList("tagA1"), new HashSet(Arrays.asList(TestTagsBRules.class)));
    }

    @Test
    public void testCombinedTags()
    {
        goTest(Arrays.asList("tagA1"), Arrays.asList("tagB1"), new HashSet(Arrays.asList(TestTagsARules.class)));
    }

    @Test
    public void testNoTags()
    {
        // All rules should be executed (tags should create no limitation).
        goTest(null, null, new HashSet(Arrays.asList(TestTagsA1B1Rules.class, TestTagsARules.class, TestTagsBRules.class)));
    }

    private void goTest(List<String> includeTagsToSet, List<String> excludeTagsToSet, Set<Class<? extends RuleProvider>> rulesThatShouldRun)
    {
        try (GraphContext grCtx = contextFactory.create())
        {
            runRules(testRuleProviders, grCtx, includeTagsToSet, excludeTagsToSet, rulesThatShouldRun);
        }
        catch (Exception ex)
        {
            throw new WindupException(ex.getMessage(), ex);
        }
    }




    /**
     * Configure the WindupConfiguration according to the params and run the RuleProviders.
     */
    private void runRules(EnumerationOfRulesFilter filter, GraphContext grCtx, List<String> includeTagsToSet, List<String> excludeTagsToSet, Set<Class<? extends RuleProvider>> rulesThatShouldRun)
    {
        try
        {
            // Windup config.
            WindupConfiguration wc = new WindupConfiguration();
            wc.setGraphContext(grCtx);
            wc.setInputPath(Paths.get("."));
            wc.setRuleProviderFilter(filter);
            wc.setOutputDirectory(Paths.get("target/WindupReport"));

            wc.setOptionValue(IncludeTagsOption.NAME, includeTagsToSet);
            wc.setOptionValue(ExcludeTagsOption.NAME, excludeTagsToSet);
            wc.setOptionValue("rulesThatShouldRun", rulesThatShouldRun);

            // Run.
            processor.execute(wc);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @RuleMetadata(tags = { "tagA1", "tagA2", "tagA3" })
    public static class TestTagsARules extends NoopRuleProvider
    {
    }

    @RuleMetadata(tags = { "tagB1", "tagB2", "tagB3" })
    public static class TestTagsBRules extends NoopRuleProvider
    {
    }

    @RuleMetadata(tags = { "tagA1", "tagB1" })
    public static class TestTagsA1B1Rules extends NoopRuleProvider
    {
    }

    // Formatter:off
    public abstract static class NoopRuleProvider extends AbstractRuleProvider
    {
        @Override
        public Configuration getConfiguration(GraphContext context)
        {
            return ConfigurationBuilder.begin().addRule()
            .perform(
                Log.message(org.ocpsoft.logging.Logger.Level.TRACE, "Performing Rule: " + this.getClass().getSimpleName())
            );
        }
    }
    // Formatter:off

}
