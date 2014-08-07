package org.jboss.windup.log.jul.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.jboss.windup.log.jul.format.SingleLineFormatter;
import org.jboss.windup.log.jul.format.SystemOutHandler;

/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
public final class Logging {
    static{
        System.setProperty("java.util.logging.SimpleFormatter.format", 
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s %6$s%n");
        System.out.println( "java.util.logging.SimpleFormatter.format was set." );
    }
    
    private static boolean initDone = false;

    /** init() helper. */
    public static void init(){
        init(Logging.class);
    }

    /** init() helper. */
    public static void init( final Class cls )
    {
        if( initDone )
            return;
        initDone = true;
        
        // Set SingleLineFormatter for all root logger's handlers.
        for( Handler handler : Logger.getLogger("").getHandlers() ) {
            //handler.setFormatter( new SingleLineFormatter() );
        }

        // Read logging.properties from somewhere and feed it to JUL.
        String logConfigFile = System.getProperty("java.util.logging.config.file", "logging.properties");
        try
        {
            InputStream is = null;
            do {
                if( logConfigFile != null ) {
                    System.out.println("Reading logging config from: " + logConfigFile);
                    System.out.println("(Set in sys prop java.util.logging.config.file)");
                    if( ! new File(logConfigFile).exists() ){
                        System.err.println("File "+logConfigFile+" not found!");
                    }
                    else {
                        is = new FileInputStream(logConfigFile);
                    }
                }
                
                if( is == null ) {
                    System.out.println("Reading logging config from resource /logging.properties");
                    is = cls.getResourceAsStream("/logging.properties");
                    if( null == is ){
                        System.err.println("Resource /logging.properties not found!");
                    }
                }
                if( null != is ){
                    init( is );
                }
                    
            } while( false );
        }
        catch(Exception ex)
        {
            System.err.println("Can't read logging config from ["+logConfigFile+"] (will use default):\n    "
                    + ex.getMessage());
            final SystemOutHandler soutHandler = new SystemOutHandler();
            soutHandler.setFormatter( new SingleLineFormatter() );
            //Logger.getLogger("").addHandler( soutHandler );
        }
        Logger.getLogger(Logging.class.getName()).info("Logging configured.");
    }

    
    public static void init( InputStream is ) {
        LogManager.getLogManager().reset();
        try {
            LogManager.getLogManager().readConfiguration(is);
        } catch( IOException | SecurityException ex ) {
            throw new RuntimeException("Can't init JUL: " + ex.getMessage(), ex);
        }
    }

    

    public static Logger of( Class cls ) {
        return Logger.getLogger(cls.getName());
    }

}// class
