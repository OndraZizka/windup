package org.jboss.windup.exec;

import java.nio.file.Path;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.Predicate;
import org.jboss.windup.config.DefaultEvaluationContext;
import org.jboss.windup.config.GraphRewrite;
import org.jboss.windup.config.PreRulesetEvaluation;
import org.jboss.windup.config.RuleLifecycleListener;
import org.jboss.windup.config.RuleSubset;
import org.jboss.windup.config.WindupRuleProvider;
import org.jboss.windup.config.loader.WindupRuleLoader;
import org.jboss.windup.config.metadata.WindupRuleMetadata;
import org.jboss.windup.engine.predicates.AndFilter;
import org.jboss.windup.engine.predicates.TagsRuleProviderFilter;
import org.jboss.windup.exec.configuration.WindupConfiguration;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.model.WindupConfigurationModel;
import org.jboss.windup.graph.model.resource.FileModel;
import org.jboss.windup.graph.service.FileService;
import org.jboss.windup.graph.service.WindupConfigurationService;
import org.jboss.windup.util.Checks;
import org.jboss.windup.util.ExecutionStatistics;
import org.jboss.windup.util.Logging;
import org.jboss.windup.util.exception.WindupException;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.config.RuleVisit;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.DefaultParameterValueStore;
import org.ocpsoft.rewrite.param.ParameterValueStore;
import org.ocpsoft.rewrite.util.Visitor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Ondrej Zizka, ozizka@redhat.com
 */
public class WindupProcessorImpl implements WindupProcessor
{
    private static Logger LOG = Logging.get(WindupProcessorImpl.class);

    @Inject
    private WindupRuleLoader windupConfigurationLoader;

    @Inject
    private Imported<RuleLifecycleListener> listeners;

    @Override
    public void execute()
    {
        execute(new WindupConfiguration());
    }

    @Override
    public void execute(WindupConfiguration windupConfiguration)
    {
        long startTime = System.currentTimeMillis();

        validateConfig(windupConfiguration);

        GraphContext graphContext = windupConfiguration.getGraphContext();

        graphContext.setOptions(windupConfiguration.getOptionMap());

        // Initialize the basic configuration data in the graph.
        WindupConfigurationModel configModel = WindupConfigurationService.getConfigurationModel(graphContext);
        configModel.setInputPath(getFileModel(graphContext, windupConfiguration.getInputPath()));
        configModel.setOutputPath(getFileModel(graphContext, windupConfiguration.getOutputDirectory()));
        configModel.setOfflineMode(windupConfiguration.isOffline());
        for (Path path : windupConfiguration.getAllUserRulesDirectories())
        {
            System.out.println("Using user rules dir: " + path);
            if (path == null)
            {
                throw new WindupException("Null path found (all paths are: "
                            + windupConfiguration.getAllUserRulesDirectories() + ")");
            }
            configModel.addUserRulesPath(getFileModel(graphContext, path));
        }

        for (Path path : windupConfiguration.getAllIgnoreDirectories())
        {
            configModel.addUserIgnorePath(getFileModel(graphContext, path));
        }

        final GraphRewrite event = new GraphRewrite(graphContext);


        // Merge the user-provided RuleProvider filter and others from the WindupConfiguration.

        Predicate<WindupRuleProvider> configuredFilter = windupConfiguration.getRuleProviderFilter();
        final TagsRuleProviderFilter tagsFilter = new TagsRuleProviderFilter(windupConfiguration.getOnlyExecuteRuleProvidersWithTags());
        Predicate<WindupRuleProvider> overallFilter = new AndFilter(configuredFilter, tagsFilter);



        WindupRuleMetadata ruleMetadata =
                windupConfigurationLoader.loadConfiguration(graphContext, overallFilter);
        event.getRewriteContext().put(WindupRuleMetadata.class, ruleMetadata);

        Configuration rules = ruleMetadata.getConfiguration();

        RuleSubset ruleSubset = RuleSubset.create(rules);
        if (windupConfiguration.getProgressMonitor() != null)
            ruleSubset.addLifecycleListener(new DefaultRuleLifecycleListener(windupConfiguration.getProgressMonitor(),
                        rules));

        for (RuleLifecycleListener listener : listeners)
        {
            ruleSubset.addLifecycleListener(listener);
        }

        new RuleVisit(ruleSubset).accept(new Visitor<Rule>()
        {
            @Override
            public void visit(Rule r)
            {
                if (r instanceof PreRulesetEvaluation)
                {
                    ((PreRulesetEvaluation) r).preRulesetEvaluation(event);
                }
            }
        });
        ruleSubset.perform(event, createEvaluationContext());

        long endTime = System.currentTimeMillis();
        long seconds = (endTime - startTime) / 1000L;
        LOG.info("Windup execution took " + seconds + " seconds to execute on input: "
                    + windupConfiguration.getInputPath() + "!");

        ExecutionStatistics.get().reset();
    }

    private FileModel getFileModel(GraphContext context, Path path)
    {
        return new FileService(context).createByFilePath(path.toString());
    }

    /**
     * Creates EvaluationContext which serves as an environment and input for RuleSubset executor.
     */
    private EvaluationContext createEvaluationContext()
    {
        final DefaultEvaluationContext evaluationContext = new DefaultEvaluationContext();
        final DefaultParameterValueStore values = new DefaultParameterValueStore();
        evaluationContext.put(ParameterValueStore.class, values);
        return evaluationContext;
    }

    private void validateConfig(WindupConfiguration windupConfiguration)
    {
        Assert.notNull(windupConfiguration,
                    "Windup configuration must not be null. (Call default execution if no configuration is required.)");

        GraphContext context = windupConfiguration.getGraphContext();
        Assert.notNull(context, "Windup GraphContext must not be null!");

        Path inputPath = windupConfiguration.getInputPath();
        Assert.notNull(inputPath, "Path to the application must not be null!");
        Checks.checkFileOrDirectoryToBeRead(inputPath.toFile(), "Application");

        Path outputDirectory = windupConfiguration.getOutputDirectory();
        Assert.notNull(outputDirectory, "Output directory must not be null!");
        Checks.checkDirectoryToBeFilled(outputDirectory.toFile(), "Output directory");
    }

}
