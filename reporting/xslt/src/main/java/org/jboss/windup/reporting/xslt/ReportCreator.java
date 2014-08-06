package org.jboss.windup.reporting.xslt;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.io.FileUtils;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.model.ApplicationModel;
import org.jboss.windup.graph.service.GraphService;
import org.jboss.windup.reporting.xslt.model.MigrationReportJaxbBean;
import org.jboss.windup.reporting.xslt.util.XmlUtils;
import org.jboss.windup.util.exception.WindupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 *  Creates the overall XML and HTML report.
 * 
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
public class ReportCreator {
    public static final Logger log = LoggerFactory.getLogger( ReportCreator.class );
    
    private static final String RESOURCES_PATH = "/org/jboss/windup/reporting/xslt/res/";
    private static final String XSLT_FILE = "MigrationReportJaxbBean.xsl";
    private static final String CSS_FILE = "MigrationReport.css";
    private static final String JQUERY_FILE = "jquery-1.10.1.min.js";

    
    public static void createReport( GraphContext ctx, File reportDir ) throws WindupException {
        try {
            // Create the reporting content.
            MigrationReportJaxbBean reportJaxb = new MigrationReportJaxbBean();
            reportJaxb.config = GraphService.getConfigurationModel(ctx);
            //report.finalException = ctx.getFinalException();
            
            // Copy deployments reports to the $reportDir.
            for( ApplicationModel depl : reportJaxb.deployments ) {
                // TODO, or remove - should be generic.
            }
            
            Document doc = createXmlReport(reportJaxb);
            
            File reportFile = saveXmlReport(reportDir, doc);
            
            createHtmlReport(reportFile, doc);
            
            copyResources( reportDir );
        }
        /*catch( TransformerException ex ){
            for( Throwable throwable : ex.getSuppressed() ) {
                log.error( "ex.getSuppressed():", new Exception( throwable ) );
            }
            throw new WindupException("Failed creating migration report:\n    " + ex.getMessageAndLocation(), ex);
        }*/
        catch( Exception ex ) {
            final String msg = "Failed creating migration report:\n    " + ex.getMessage();
            log.error(msg, ex);
            throw new WindupException(msg, ex);
        }
    }


    private static void copyResources( File reportDir ) throws IOException {
        // Copy CSS and jQuery.
        InputStream is;
        is = ReportCreator.class.getResourceAsStream(RESOURCES_PATH + CSS_FILE);
        FileUtils.copyInputStreamToFile( is, new File(reportDir, CSS_FILE) );
        is = ReportCreator.class.getResourceAsStream(RESOURCES_PATH + JQUERY_FILE);
        FileUtils.copyInputStreamToFile( is, new File(reportDir, "jQuery.js") );
        is = ReportCreator.class.getResourceAsStream(RESOURCES_PATH + "iconsBig.png");
        FileUtils.copyInputStreamToFile( is, new File(reportDir, "iconsBig.png") );
        is = ReportCreator.class.getResourceAsStream(RESOURCES_PATH + "iconsMed.png");
        FileUtils.copyInputStreamToFile( is, new File(reportDir, "iconsMed.png") );
    }


    private static File createHtmlReport( File reportFile, Document doc ) throws TransformerException {
        // Use XSLT to produce HTML report.
        File htmlFile = new File( reportFile.getPath() + ".html");
        log.debug("Storing the HTML report to " + htmlFile.getPath());
        InputStream is = ReportCreator.class.getResourceAsStream(RESOURCES_PATH + XSLT_FILE);
        XmlUtils.transformDocToFile( doc, htmlFile, is );
        return htmlFile;
    }


    private static File saveXmlReport( File reportDir, Document doc ) throws IOException, WindupException {
        //saveXmlReport( doc );
        // File name
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss").format(new Date());
        File reportFile = new File(reportDir, "MigrationReport-"+timestamp+".xml");
        FileUtils.forceMkdir( reportDir );
        // Write node to a file.
        log.debug("Storing the XML report to " + reportFile.getPath());
        XmlUtils.saveXmlToFile( doc, reportFile );
        return reportFile;
    }


    private static Document createXmlReport( MigrationReportJaxbBean reportJaxb ) throws JAXBException, ParserConfigurationException
    {
        Marshaller mar = XmlUtils.createMarshaller( MigrationReportJaxbBean.class );

        // Write to a Node.
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        mar.marshal( reportJaxb, doc );
        return doc;
    }
    
    
}// class
