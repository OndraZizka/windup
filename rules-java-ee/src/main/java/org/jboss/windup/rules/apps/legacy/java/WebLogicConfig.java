package org.jboss.windup.rules.apps.legacy.java;

import org.jboss.windup.config.RulePhase;
import org.jboss.windup.config.WindupRuleProvider;
import org.jboss.windup.config.metadata.RuleMetadata;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.reporting.config.Classification;
import org.jboss.windup.reporting.config.Hint;
import org.jboss.windup.reporting.config.Link;
import org.jboss.windup.rules.apps.java.config.JavaClass;
import org.jboss.windup.rules.apps.java.scan.ast.TypeReferenceLocation;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.context.Context;

public class WebLogicConfig extends WindupRuleProvider
{

    @Override
    public RulePhase getPhase()
    {
        return RulePhase.MIGRATION_RULES;
    }

    @Override
    public void enhanceMetadata(Context context)
    {
        context.put(RuleMetadata.CATEGORY, "Java");
    }

    // @formatter:off
    @Override
    public Configuration getConfiguration(GraphContext context)
    {
        Configuration configuration = ConfigurationBuilder
                    .begin()
                    .addRule()
                    .when(JavaClass.references("javax.xml.rpc.handler.GenericHandler").at(
                                            TypeReferenceLocation.INHERITANCE))
                    .perform(Classification.as("JAX-RPC Generic Handler").withEffort(0))
                    
                    .addRule()
                    .when(JavaClass.references("weblogic.security.Security$").at(TypeReferenceLocation.TYPE))
                    .perform(Hint.withText("Migrate to JBoss EAP 5: org.jboss.security.SecurityAssociation")
                                 .with(Link.to("Security Context - JBoss 6",
                                               "https://access.redhat.com/knowledge/docs/en-US/JBoss_Enterprise_Application_Platform/6/html/Javadocs/files/javadoc/org/jboss/security/SecurityContextAssociation.html")).withEffort(0))
                                
                    .addRule()
                    .when(JavaClass.references("weblogic.security.Security$").at(TypeReferenceLocation.TYPE))
                    .perform(Hint.withText("Migrate to JBoss EAP 6: org.jboss.security.SecurityContextAssociation")
                                 .with(Link.to("Security Context - JBoss 6",
                                               "https://access.redhat.com/knowledge/docs/en-US/JBoss_Enterprise_Application_Platform/6/html/Javadocs/files/javadoc/org/jboss/security/SecurityContextAssociation.html"))
                                 .withEffort(0))
                                
                    .addRule()
                    .when(JavaClass.references("weblogic.security.Security.getCurrentSubject").at(
                                            TypeReferenceLocation.METHOD))
                    .perform(Hint.withText("Migrate to JBoss EAP 6: org.jboss.security.SecurityContextAssociation.getCurrentContext().getCurrentPrincipal()").with(Link.to(
                                                        "Security Context - JBoss 6",
                                                        "https://access.redhat.com/knowledge/docs/en-US/JBoss_Enterprise_Application_Platform/6/html/Javadocs/files/javadoc/org/jboss/security/SecurityContextAssociation.html")).withEffort(3))
                                

                    .addRule()
                    .when(JavaClass.references("weblogic.security.Security.getCurrentSubject").at(
                                            TypeReferenceLocation.METHOD))
                    .perform(Hint.withText("Migrate to JBoss EAP 5: org.jboss.security.SecurityAssociation.getPrincipal().getName()").with(Link.to(
                                                        "Security Context - JBoss 6",
                                                        "https://access.redhat.com/knowledge/docs/en-US/JBoss_Enterprise_Application_Platform/6/html/Javadocs/files/javadoc/org/jboss/security/SecurityContextAssociation.html")).withEffort(3))
                                

                    // java-gateLink.to( "Security Context - JBoss 6"
                    // ,"https://access.redhat.com/knowledge/docs/en-US/JBoss_Enterprise_Application_Platform/6/html/Javadocs/files/javadoc/org/jboss/security/SecurityContextAssociation.html")
                    .addRule()
                    .when(JavaClass.references("weblogic.application.ApplicationLifecycleListener$").at(
                                            TypeReferenceLocation.INHERITANCE))
                    .perform(Classification.as("Weblogic ApplicationLifecycleListener, proprietary class, must be migrated.")
                                           .with(Link.to("Master the JBoss Tutorial: EJB 3.1 Tutorial",
                                                         "http://www.mastertheboss.com/ejb-3/ejb-31-tutorial"))
                                                     .with(Link.to("Caucho.com Tutorial: ServletContextListener, @WebListener tutorial",
                                                         "http://blog.caucho.com/2009/10/06/servlet-30-tutorial-weblistener-webservlet-webfilter-and-webinitparam/"))
                                                     .with(Link.to("Rose India Tutorial: ServletContextListener, @WebListener tutorial",
                                                          "http://www.roseindia.net/servlets/servlet3/WebListener_annotation.shtml"))
                                                     .withEffort(3))
                                            
                    .addRule()
                    .when(JavaClass.references("weblogic.application.ApplicationLifecycleListener$").at(
                                            TypeReferenceLocation.IMPORT))
                    .perform(Hint.withText("This class is proprietary to Weblogic, remove.").withEffort(2))
                                

                    .addRule()
                    .when(JavaClass.references("weblogic.application.ApplicationLifecycleListener$").at(
                                            TypeReferenceLocation.INHERITANCE))
                    .perform(Hint.withText("Use a javax.servlet.ServletContextListener with @javax.annotation.servlet.WebListener, or EJB 3.1 @javax.ejb.Startup @javax.ejb.Singleton service bean.").withEffort(2))
                                

                    .addRule()
                    .when(JavaClass.references("weblogic.application.ApplicationLifecycleEvent$").at(
                                            TypeReferenceLocation.IMPORT))
                    .perform(Hint.withText("This class is proprietary to Weblogic, remove.").withEffort(2))
                                

                    .addRule()
                    .when(JavaClass.references("weblogic.application.ApplicationLifecycleEvent$").at(
                                            TypeReferenceLocation.TYPE))
                    .perform(Hint.withText("Requires migration effort").withEffort(0))
                                

                    .addRule()
                    .when(JavaClass.references("weblogic.i18n.logging.NonCatalogLogger\\(.+\\)").at(
                                            TypeReferenceLocation.CONSTRUCTOR_CALL))
                    .perform(Hint.withText("Requires migration effort").withEffort(1))
                                

                    .addRule()
                    .when(JavaClass.references("oracle.jms.AQjmsConnectionFactory"))
                    .perform(Hint.withText("Migrate to: javax.jms.ConnectionFactory").withEffort(1))
                                

                    .addRule()
                    .when(JavaClass.references("oracle.jms.AQjmsQueueConnectionFactory"))
                    .perform(Hint.withText("Migrate to: javax.jms.QueueConnectionFactory").withEffort(1))
                                

                    .addRule()
                    .when(JavaClass.references("oracle.jms.AQjmsTopicConnectionFactory"))
                    .perform(Hint.withText("Migrate to: javax.jms.TopicConnectionFactory").withEffort(1))
                                

                    .addRule()
                    .when(JavaClass.references("oracle.jms.AQjmsDestination"))
                    .perform(Hint.withText("Migrate to: javax.jms.Destination").withEffort(1))
                                

                    .addRule()
                    .when(JavaClass.references("oracle.jms.AQjmsMessage"))
                    .perform(Hint.withText("Migrate to: javax.jms.Message").withEffort(1))
                                

                    .addRule()
                    .when(JavaClass.references("oracle.jms.AQjmsBytesMessage"))
                    .perform(Hint.withText("Migrate to: javax.jms.BytesMessage").withEffort(1))
                                

                    .addRule()
                    .when(JavaClass.references("oracle.jms.AQjmsMapMessage"))
                    .perform(Hint.withText("Migrate to: javax.jms.MapMessage").withEffort(1))
                                

                    .addRule()
                    .when(JavaClass.references("oracle.jms.AQjmsObjectMessage"))
                    .perform(Hint.withText("Migrate to: javax.jms.ObjectMessage").withEffort(1))
                                

                    .addRule()
                    .when(JavaClass.references("oracle.jms.AQjmsStreamMessage"))
                    .perform(Hint.withText("Migrate to: javax.jms.StreamMessage").withEffort(1))
                                

                    .addRule()
                    .when(JavaClass.references("oracle.jms.AQjmsTextMessage"))
                    .perform(Hint.withText("Migrate to: javax.jms.TextMessage").withEffort(1))
                                

                    .addRule()
                    .when(JavaClass.references("oracle.jms.AQjmsConnection"))
                    .perform(Hint.withText("Migrate to: javax.jms.Connection").withEffort(1))
                                

                    .addRule()
                    .when(JavaClass.references("oracle.jms.AQjmsConsumer"))
                    .perform(Hint.withText("Migrate to: javax.jms.MessageConsumer").withEffort(1))
                                

                    .addRule()
                    .when(JavaClass.references("oracle.jms.AQjmsProducer"))
                    .perform(Hint.withText("Migrate to: javax.jms.MessageProducer").withEffort(1))
                                

                    .addRule()
                    .when(JavaClass.references("oracle.jms.AQjmsQueueBrowser"))
                    .perform(Hint.withText("Migrate to: javax.jms.QueueBrowser").withEffort(1))
                                

                    .addRule()
                    .when(JavaClass.references("oracle.jms.AQjmsSession"))
                    .perform(Hint.withText("Migrate to: javax.jms.Session").withEffort(1)) ;

        return configuration;
    }
    // @formatter:on
}