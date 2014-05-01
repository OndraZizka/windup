package org.jboss.windup.engine.visitor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jboss.windup.graph.model.meta.JarManifest;
import org.jboss.windup.graph.model.meta.PropertiesMeta;
import org.jboss.windup.graph.model.meta.javaclass.EjbEntityFacet;
import org.jboss.windup.graph.model.meta.javaclass.EjbSessionBeanFacet;
import org.jboss.windup.graph.model.meta.javaclass.MessageDrivenBeanFacet;
import org.jboss.windup.graph.model.meta.javaclass.SpringBeanFacet;
import org.jboss.windup.graph.model.meta.xml.DoctypeMeta;
import org.jboss.windup.graph.model.meta.xml.EjbConfigurationFacet;
import org.jboss.windup.graph.model.meta.xml.NamespaceMeta;
import org.jboss.windup.graph.model.meta.xml.SpringConfigurationFacet;
import org.jboss.windup.graph.model.resource.ArchiveEntryResource;
import org.jboss.windup.graph.model.resource.ArchiveResource;
import org.jboss.windup.graph.model.resource.EarArchive;
import org.jboss.windup.graph.model.resource.FileResource;
import org.jboss.windup.graph.model.resource.JarArchive;
import org.jboss.windup.graph.model.resource.JavaClass;
import org.jboss.windup.graph.model.resource.Resource;
import org.jboss.windup.graph.model.resource.WarArchive;
import org.jboss.windup.graph.model.resource.XmlResource;

public abstract class AbstractGraphVisitor implements GraphVisitor
{

    @Override
    public abstract void run();

    @Override
    public List<Class<? extends GraphVisitor>> getDependencies()
    {
        return Collections.emptyList();
    }

    @Override public void visitResource(Resource entry) { } 
    @Override public void visitFile(FileResource entry) { }
    @Override public void visitArchive(ArchiveResource entry) {  }
    @Override public void visitArchiveEntry(ArchiveEntryResource entry) {  }
    @Override public void visitEarArchive(EarArchive entry) {  }
    @Override public void visitJarArchive(JarArchive entry) {  }
    @Override public void visitWarArchive(WarArchive entry) {  }

    @Override public void visitJavaClass(JavaClass entry) {  }
    
    @Override public void visitDoctype(DoctypeMeta entry) {  }
    
    /*
    @Override public void visitEjbEntity(EjbEntityFacet entry) {  }

    @Override public void visitEjbService(EjbSessionBeanFacet entry) {  }

    @Override public void visitMessageDrivenBean(MessageDrivenBeanFacet entry) {  }

    @Override public void visitEjbEntity(SpringBeanFacet entry) {  }


    @Override public void visitEjbConfiguration(EjbConfigurationFacet entry) {  }

    @Override public void visitSpringConfiguration(SpringConfigurationFacet entry) {  }
    */
    @Override public void visitXmlResource(XmlResource entry) {  }
    @Override public void visitManifest(JarManifest entry) {  }

    /*
    @Override public void visitNamespace(NamespaceMeta entry) {  }
    */

    @Override public void visitProperties(PropertiesMeta entry) {  }

    @SafeVarargs
    protected final List<Class<? extends GraphVisitor>> generateDependencies(Class<? extends GraphVisitor>... deps)
    {
        return Arrays.asList(deps);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[" + getClass().getSimpleName() + ": [Phase: " + getPhase() + ", Dependencies: " + getDependencies()
                    + "]");
        return sb.toString();
    }
}
