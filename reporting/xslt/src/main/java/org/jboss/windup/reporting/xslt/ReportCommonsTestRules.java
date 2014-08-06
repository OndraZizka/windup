package org.jboss.windup.reporting.xslt;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.jboss.forge.furnace.Furnace;
import org.jboss.windup.config.GraphRewrite;
import org.jboss.windup.config.RulePhase;
import org.jboss.windup.config.WindupRuleProvider;
import org.jboss.windup.config.operation.GraphOperation;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.model.WindupVertexFrame;
import org.jboss.windup.graph.service.GraphService;
import org.jboss.windup.graph.service.Service;
import org.jboss.windup.log.jul.config.Logging;
import org.jboss.windup.reporting.meta.ann.ReportElement;
import org.jboss.windup.reporting.model.ReportCommonsModelModel;
import static org.jboss.windup.reporting.model.ReportCommonsModelModel.ELEM_TYPE;
import org.jboss.windup.util.exception.WindupException;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;

public class ReportCommonsTestRules extends WindupRuleProvider
{
    private static final Logger log = Logging.of( ReportCommonsRenderer.class );
    

    @Inject
    private Furnace furnace;
    
    @Override
    public RulePhase getPhase()
    {
        return RulePhase.REPORT_RENDERING;
    }

    public Configuration getConfiguration(GraphContext context)
    {
        return ConfigurationBuilder.begin()
        .addRule()
        .perform(
            new RenderReportCommonsFrameOperation()
        );
    }


    
    /**
     * 1) Loads all ReportCommonsModelModel's which are @ReportElement(type=BOX)
     * 2) For each, finds all frames of that type
     * 3) For each frame, renders it to a XML snippet, saves to a temp file, stores the path to the vertex
     * 4) Joins these snippets into several XML documents, future sections of a report.
     */
    private static class RenderReportCommonsFrameOperation extends GraphOperation {
        public void perform( GraphRewrite event, EvaluationContext context ) {
            ReportCommonsRenderer rend = new ReportCommonsRenderer( event.getGraphContext() );
            rend.setOutputDir( event.getWindupTemporaryFolder() );
            
            //event.getGraphContext().getFramed().query().
            Service<ReportCommonsModelModel> serv = event.getGraphContext().getService(ReportCommonsModelModel.class);
            Iterable<ReportCommonsModelModel> rcmms = serv.findAllByProperty(ELEM_TYPE, ReportElement.Type.BOX.toString());
            for( ReportCommonsModelModel rcmm : rcmms ) {
                log.fine("Rendering frames of Model: " + rcmm.getClassName());
                Class<WindupVertexFrame> cls = getModelClass( rcmm.getClassName() );
                Service<WindupVertexFrame> modelServ = event.getGraphContext().getService(cls);
                Iterable<WindupVertexFrame> frames = modelServ.findAll();
                for( WindupVertexFrame frame : frames ) {
                    rend.renderFrame( frame, rcmm );
                }
            }
        }


        /**
         * TODO: Put this to GraphUtils.
         */
        private Class<WindupVertexFrame> getModelClass( String name ) {
            Class<?> cls = null;
            try {
                cls = Class.forName( name );
            } catch( ClassNotFoundException ex ) {
                throw new WindupException("Couldn't load class: " + name + "\n    " + ex.getMessage(), ex);
            }
            if( ! WindupVertexFrame.class.isAssignableFrom( cls ) )
                throw new WindupException("Class " + name + "is not a " + WindupVertexFrame.class.getName());
            
            return (Class<WindupVertexFrame>) cls;
        }
    }
}
