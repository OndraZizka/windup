package org.jboss.windup.log;

import java.io.InputStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.jboss.windup.log.jul.config.Logging;
import org.jboss.windup.log.jul.format.SimplestFormatter;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public class LoggerTest {
    
    @Before public void before(){
        LogManager.getLogManager().reset();
        System.out.println("====== Next test =======================================================");
    }

    @Test
    public void testLogging() {
        Logger log = Logger.getLogger( LoggerTest.class.getName() );
        log.setUseParentHandlers(false);

        Handler h;
        //h = new java.util.logging.ConsoleHandler();
        h = new java.util.logging.StreamHandler( System.out, new SimplestFormatter() );
        h.setLevel(Level.ALL);
        //h.setFormatter( new SimpleFormatter() );
        log.addHandler( h );/**/

        try{
            h = new java.util.logging.FileHandler("target/LogTest.log", 50000, 1);
            h.setLevel(Level.ALL);
            h.setFormatter( new SimpleFormatter() );
            log.addHandler( h );
        }
        catch(Exception ex){ ex.printStackTrace(); }

        log.setLevel(Level.INFO);
        log.entering("Entering", "method");
        log.severe("Test SEVERE");
        log.info("Test INFO");
        log.log(Level.FINE, "Test FINE");
    }
    
    
    
    @Test
    public void testLoggingProperties() throws Exception {

        System.setProperty("java.util.logging.config.file", "logging.properties");
        try {
            final InputStream is = this.getClass().getResourceAsStream("/logging.properties");
            if( null == is)
                throw new Exception("logging.properties resource not found!");
            LogManager.getLogManager().readConfiguration(is);
        }
        catch( Exception ex ){
            //ex.printStackTrace();
            throw ex;
        }

        Logger.getLogger("").severe("Root SEVERE");
        Logger.getLogger("").warning("Root WARNING");
        Logger.getLogger("").fine("Root FINE");
        Logger.getLogger("Foo").warning("Foo WARNING");
        Logger.getLogger("Foo").info("Foo INFO");
        Logger.getLogger("Foo").fine("Foo FINE");
        Logger.getLogger("Foo.Aj").warning("Foo.Aj WARNING");
        Logger.getLogger("Foo.Aj").info("Foo.Aj INFO");
        Logger.getLogger("Foo.Aj").fine("Foo.Aj FINE");
    }

    
    @Test
    public void testLogging_init() throws Exception {

        Logging.init();

        Logger.getLogger("").severe("Root SEVERE");
        Logger.getLogger("").warning("Root WARNING");
        Logger.getLogger("").fine("Root FINE");
        Logger.getLogger("Foo").warning("Foo WARNING");
        Logger.getLogger("Foo").info("Foo INFO");
        Logger.getLogger("Foo").fine("Foo FINE");
        Logger.getLogger("Foo.Aj").warning("Foo.Aj WARNING");
        Logger.getLogger("Foo.Aj").info("Foo.Aj INFO");
        Logger.getLogger("Foo.Aj").fine("Foo.Aj FINE");
    }

}
