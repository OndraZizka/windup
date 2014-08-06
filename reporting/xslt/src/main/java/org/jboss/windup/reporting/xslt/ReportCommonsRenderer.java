package org.jboss.windup.reporting.xslt;


import java.io.File;
import java.nio.file.Path;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.model.WindupVertexFrame;
import org.jboss.windup.reporting.model.ReportCommonsModelModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
class ReportCommonsRenderer {
    private static final Logger log = LoggerFactory.getLogger( ReportCommonsRenderer.class );

    private final GraphContext graphContext;
    private Path outputDir;


    ReportCommonsRenderer( GraphContext graphContext ) {
        this.graphContext = graphContext;
    }


    void renderFrame( WindupVertexFrame frame, ReportCommonsModelModel rcmm ) {
        
    }

    
    public Path getOutputDir() {
        return outputDir;
    }

    public void setOutputDir( Path outputDir ) {
        this.outputDir = outputDir;
    }
    
}// class
