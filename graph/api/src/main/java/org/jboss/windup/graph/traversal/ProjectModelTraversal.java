package org.jboss.windup.graph.traversal;

import com.tinkerpop.blueprints.Vertex;
import org.apache.commons.lang3.StringUtils;
import org.jboss.windup.graph.model.DuplicateProjectModel;
import org.jboss.windup.graph.model.ProjectModel;
import org.jboss.windup.graph.model.resource.FileModel;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This allows a {@link ProjectModel} to be traversed in a way that is aware of {@link DuplicateProjectModel}s.
 *
 * This should make it easier to calculate the actual path within the application without regard to where the original
 * project was actually stored.
 *
 * Cases where this is used include:
 * <ul>
 *   <li>Listing all of the files in the application with accurate paths, regardless of how they are stored in the graph</li>
 *   <li>The application details report, which traverses the applications in a hierarchical manner.</li>
 * </ul>
 *
 * @author <a href="mailto:jesse.sightler@gmail.com">Jesse Sightler</a>
 */
public class ProjectModelTraversal
{
    private final ProjectModelTraversal previous;
    private final ProjectModel current;
    private final TraversalStrategy traversalStrategy;

    /**
     * Builds an instance with a reference to the previous {@link ProjectModelTraversal}, and the given parameters.
     *
     * NOTE: This would generally only be used by {@link TraversalStrategy} implementations. {@see DefaultTraversalStrategy}
     */
    public ProjectModelTraversal(ProjectModelTraversal previous, ProjectModel current, TraversalStrategy traversalStrategy)
    {
        this.previous = previous;
        this.current = current;
        this.traversalStrategy = traversalStrategy == null ? new AllTraversalStrategy() : traversalStrategy;
    }

    /**
     * Creates a new {@link ProjectModelTraversal} based upon the provided {@link ProjectModel}. The {@link ProjectModel}
     * should be a "root" model (an application) rather than a subpart of an application.
     *
     * {@link TraversalStrategy} will default to {@link AllTraversalStrategy}.
     */
    public ProjectModelTraversal(ProjectModel current)
    {
        this(null, current, null);
    }

    /**
     * Creates a new {@link ProjectModelTraversal} based upon the provided {@link ProjectModel}. The {@link ProjectModel}
     * should be a "root" model (an application) rather than a subpart of an application.
     *
     * The provided {@link TraversalStrategy} will determine how the ProjectModel's subprojects tree will be traversed.
     */
    public ProjectModelTraversal(ProjectModel current, TraversalStrategy traversalStrategy)
    {
        this(null, current, traversalStrategy);
    }

    /**
     * Recursively crawls this {@link ProjectModelTraversal} and adds all of the {@link ProjectModel}s to a
     * {@link Set}. The exact projects returns will be affected by the {@link TraversalStrategy} in use by the
     * current traversal.
     */
    public Set<ProjectModel> getAllProjects(boolean recursive)
    {
        if (!recursive)
            return Collections.singleton(getCanonicalProject());
        else
            return addProjects(new HashSet<ProjectModel>(), this);
    }

    /**
     * Recursively crawls this {@link ProjectModelTraversal} and adds all of the {@link ProjectModel}s to a
     * {@link Set}. The exact projects returns will be affected by the {@link TraversalStrategy} in use by the
     * current traversal.
     *
     * This is the same as {@link ProjectModelTraversal#getAllProjects(boolean)} except that it returns a Set of vertices
     * instead of frames.
     */
    public Set<Vertex> getAllProjectsAsVertices(boolean recursive)
    {
        Set<Vertex> vertices = new LinkedHashSet<>();
        for (ProjectModel projectModel : getAllProjects(recursive))
            vertices.add(projectModel.asVertex());
        return vertices;
    }

    private Set<ProjectModel> addProjects(Set<ProjectModel> existingVertices, ProjectModelTraversal traversal)
    {
        existingVertices.add(traversal.getCanonicalProject());

        for (ProjectModelTraversal child : traversal.getChildren())
            addProjects(existingVertices, child);

        return existingVertices;
    }

    /**
     * Implements the Visitor pattern with a callback that receives each {@link ProjectModelTraversal}
     * instance. This can be useful for implementing algorithms that need to operate over all of the data within
     * this traversal.
     *
     * Note that the children visited will be affected by the current {@link TraversalStrategy} selected for this traversal.
     */
    public void accept(ProjectTraversalVisitor visitor)
    {
        visitor.visit(this);

        for (ProjectModelTraversal child : getChildren())
            child.accept(visitor);
    }

    /**
     * Gets all child projects of the current project.
     */
    public Iterable<ProjectModelTraversal> getChildren()
    {
        return traversalStrategy.getChildren(this);
    }

    /**
     * Gets the path of the specified {@link FileModel} within this traversal. The file must be within the
     * current {@link ProjectModel} for this method to return an accurate path.
     */
    public String getFilePath(FileModel fileModel)
    {
        FileModel rootFileModel = getCurrent().getRootFileModel();
        FileModel canonicalRootFileModel = getCanonicalProject().getRootFileModel();

        String base = "";

        // get the path from the chain up until this project
        if (previous != null)
            base = combinePaths(base, previous.getFilePath(canonicalRootFileModel));

        // get the path of the root file within its project
        if (current.getRootFileModel().getParentFile() != null)
            base = combinePaths(base, current.getRootFileModel().getParentFile().getPrettyPathWithinProject());

        String rootFilename = rootFileModel.getFileName();
        base = combinePaths(base, rootFilename);

        // if this is the root file, then just return the base
        if (getCurrent().getRootFileModel().getFilePath().equals(fileModel.getFilePath()))
            return base;

        String relativePath = fileModel.getPrettyPathWithinProject();
        return combinePaths(base, relativePath);
    }

    private String combinePaths(String path1, String path2)
    {
        if (StringUtils.isNotBlank(path1) && StringUtils.isNotBlank(path2))
            return path1 + "/" + path2;
        else
            return path1 + path2;
    }

    /**
     * Gets the canonical Project by unwrapping any {@link DuplicateProjectModel}s wrapping it.
     */
    public ProjectModel getCanonicalProject()
    {
        return getCanonicalProject(current);
    }

    /**
     * Gets the current {@link ProjectModel} without unwrapping.
     */
    public ProjectModel getCurrent()
    {
        return this.current;
    }

    private ProjectModel getCanonicalProject(ProjectModel projectModel)
    {
        if (projectModel instanceof DuplicateProjectModel)
        {
            DuplicateProjectModel duplicate = (DuplicateProjectModel) projectModel;
            return getCanonicalProject(duplicate.getCanonicalProject());
        }
        else
        {
            return projectModel;
        }
    }


    @Override
    public String toString()
    {
        String projectInfo = current == null ? null : current.getName() + " (" + current.getProjectType() + ')';
        String strategyInfo = traversalStrategy == null ? null : traversalStrategy.getClass().getSimpleName();
        return "Trav{cur: " + projectInfo + ", strategy: " + strategyInfo + ", prev: " + previous + '}';
    }

}
