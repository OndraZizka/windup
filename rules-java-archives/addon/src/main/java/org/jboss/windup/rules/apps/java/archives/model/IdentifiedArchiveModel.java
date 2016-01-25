package org.jboss.windup.rules.apps.java.archives.model;

import org.jboss.windup.graph.model.ArchiveModel;

import org.apache.tinkerpop.gremlin.structure.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

/**
 * An {@link IdentifiedArchiveModel} has a {@link ArchiveCoordinateModel}.
 *
 * @author <a href="mailto:ozizka@redhat.com">Ondrej Zizka</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@TypeValue(IdentifiedArchiveModel.TYPE)
public interface IdentifiedArchiveModel extends ArchiveModel
{
    public static final String TYPE = "identifiedArchive:";
    public static final String COORDINATE = TYPE + "coordinate";

    @Adjacency(label = COORDINATE, direction = Direction.OUT)
    public ArchiveCoordinateModel getCoordinate();

    @Adjacency(label = COORDINATE, direction = Direction.OUT)
    public void setCoordinate(ArchiveCoordinateModel vul);
}
