package org.jboss.windup.rules.apps.mavenize;

import java.util.logging.Logger;
import org.jboss.windup.graph.model.ProjectModel;
import org.jboss.windup.util.Logging;

/**
 * Adds the appropriate API dependencies to Maven POMs based on what's found in the project.
 *
 * This is in a prototyping stage. Far away from final implementation.
 *
 * TODO:
 * This could be rule-based in the future, so the users could write their own mapping.
 * Or, the best would be, download all the API JARs, index the classes,
 * and map them to the G:A:V.
 * This index could be prepared in a similar way as the Maven Central index addon.
 * Or maybe - the Maven Central index contains the class data as well?
 *
 * However, we also want to map some older APIs - migration vs. (forced) upgrade.
 *
 * @author Ondrej Zizka, zizka at seznam.cz
 */
class ApiDependenciesDeducer
{
    private static final Logger LOG = Logging.get(ApiDependenciesDeducer.class);

    private MavenizationService.MavenizationContext mavCtx;

    ApiDependenciesDeducer(MavenizationService.MavenizationContext mavCtx)
    {
        this.mavCtx = mavCtx;
    }


    void addAppropriateDependencies(ProjectModel projectModel, Pom modulePom)
    {
        addDeploymentTypeBasedDependencies(projectModel, modulePom);
        addHardcodedRecognitionDependencies(projectModel, modulePom);
        addIndexBasedDependencies(projectModel, modulePom);
    }


    private void addIndexBasedDependencies(ProjectModel projectModel, Pom modulePom)
    {
        PackagesToContainingMavenArtifactsIndex packageIndex = new PackagesToContainingMavenArtifactsIndex(mavCtx.getGraphContext());
        for (MavenCoords apiCoords : ApiDependenciesData.API_ARTIFACTS)
        {
            if (packageIndex.moduleContainsPackagesFromAPI(projectModel, apiCoords))
                modulePom.getDependencies().add(apiCoords);
        }
    }


    /**
     * Adds the dependencies typical for particular deployment types.
     * This is not accurate and doesn't cover the real needs of the project.
     * Basically it's just to have "something" for the initial implementation.
     */
    private boolean addDeploymentTypeBasedDependencies(ProjectModel projectModel, Pom modulePom)
    {
        if (projectModel.getProjectType() == null)
            return true;
        switch (projectModel.getProjectType()){
            case "ear":
                break;
            case "war":
                modulePom.getDependencies().add(ApiDependenciesData.DEP_API_SERVLET_31);
                break;
            case "ejb":
                modulePom.getDependencies().add(ApiDependenciesData.DEP_API_EJB_32);
                modulePom.getDependencies().add(ApiDependenciesData.DEP_API_CDI);
                modulePom.getDependencies().add(ApiDependenciesData.DEP_API_JAVAX_ANN);
                break;
            case "ejb-client":
                modulePom.getDependencies().add(ApiDependenciesData.DEP_API_EJB_CLIENT);
                break;
        }
        return false;
    }


    /**
     * This is, theoretically, slightly better than addDeploymentTypeBasedDependencies(),
     * but we don't have any of the has*() methods implemented yet, so it does nothing.
     */
    private void addHardcodedRecognitionDependencies(ProjectModel projectModel, Pom modulePom)
    {
        // TODO: Create a mapping from API occurence in the module into use of
        if (hasJpaEntities(projectModel))
            modulePom.getDependencies().add(ApiDependenciesData.DEP_API_JPA_21);

        if (hasJsf(projectModel))
            modulePom.getDependencies().add(ApiDependenciesData.DEP_API_JSF);

        if (hasJaxrs(projectModel))
            modulePom.getDependencies().add(ApiDependenciesData.DEP_API_JAXRS_20);
    }


     private boolean hasJpaEntities(ProjectModel projectModel)
    {
        return false;
    }

    private boolean hasJsf(ProjectModel projectModel)
    {
        return false;
    }

    private boolean hasJaxrs(ProjectModel projectModel)
    {
        return false;
    }


    private boolean moduleContainsClassesFromAPI(ProjectModel projectModel, MavenCoords apiCoords)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
