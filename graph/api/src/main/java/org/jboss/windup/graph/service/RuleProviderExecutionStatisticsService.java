package org.jboss.windup.graph.service;

import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.model.performance.RuleProviderExecutionStatisticsModel;

//import com.tinkerpop.gremlin.java.GremlinPipeline;
//import com.tinkerpop.pipes.PipeFunction;
//import com.tinkerpop.pipes.util.structures.Pair;

/**
 * This service provides useful methods for dealing with {@link RuleProviderExecutionStatisticsModel} Vertices within
 * the graph
 *
 * @author <a href="mailto:jesse.sightler@gmail.com">Jesse Sightler</a>
 *
 */
public class RuleProviderExecutionStatisticsService extends GraphService<RuleProviderExecutionStatisticsModel>
{
    public RuleProviderExecutionStatisticsService(GraphContext context)
    {
        super(context, RuleProviderExecutionStatisticsModel.class);
    }

    /**
     * Return an {@link Iterable} of all RuleProviderExecutionStatisticsModel ordered by Index (ascending)
     */
    public Iterable<RuleProviderExecutionStatisticsModel> findAllOrderedByIndex()
    {
        Traversal t = this.getGraphContext().getGraph().getBaseGraph().traversal()
        GremlinPipeline<RuleProviderExecutionStatisticsModel, RuleProviderExecutionStatisticsModel> pipeline = new GremlinPipeline<>(
                    findAll());
        pipeline.order(new PipeFunction<Pair<RuleProviderExecutionStatisticsModel, RuleProviderExecutionStatisticsModel>, Integer>()
        {
            @Override
            public Integer compute(
                        Pair<RuleProviderExecutionStatisticsModel, RuleProviderExecutionStatisticsModel> argument)
            {
                return argument.getA().getRuleIndex() - argument.getB().getRuleIndex();
            }
        });
        return pipeline;
    }
}
