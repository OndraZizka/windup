package org.jboss.windup.engine;

import java.nio.file.Path;
import org.jboss.forge.furnace.util.Predicate;
import org.jboss.windup.config.WindupRuleProvider;


/**
 * Configuration of WindupProcessor.
 * 
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public class WindupProcessorConfig {

    private Predicate<WindupRuleProvider> ruleProviderFilter;
    
    private WindupProgressMonitor progressMonitor = new NullWindupProgressMonitor();
    
    private Path outputDirectory;


    
    //<editor-fold defaultstate="collapsed" desc="get/set">

    public Path getOutputDirectory()
    {
        return outputDirectory;
    }

    /**
     * Sets the directory to put the output to (migration report, temporary files, exported graph data...).
     */
    public WindupProcessorConfig setOutputDirectory(Path outputDirectory)
    {
        this.outputDirectory = outputDirectory;
        return this;
    }


    public Predicate<WindupRuleProvider> getRuleProviderFilter()
    {
        return ruleProviderFilter;
    }
    
    /**
     * A filter to limit which rule providers' rules will be executed.
     */
    public WindupProcessorConfig setRuleProviderFilter(Predicate<WindupRuleProvider> ruleProviderFilter)
    {
        this.ruleProviderFilter = ruleProviderFilter;
        return this;
    }
    
    
    public WindupProgressMonitor getProgressMonitor()
    {
        return progressMonitor;
    }
    
    /**
     * A progress monitor which will get notification of the rule execution progress.
     */
    public WindupProcessorConfig setProgressMonitor(WindupProgressMonitor progressMonitor)
    {
        this.progressMonitor = progressMonitor;
        return this;
    }
    
    //</editor-fold>
    

}// class
