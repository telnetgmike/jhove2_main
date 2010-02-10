/**
 * JHOVE2 - Next-generation architecture for format-aware characterization
 * <p>
 * Copyright (c) 2009 by The Regents of the University of California, Ithaka
 * Harbors, Inc., and The Board of Trustees of the Leland Stanford Junior
 * University. All rights reserved.
 * </p>
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * </p>
 * <ul>
 * <li>Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.</li>
 * <li>Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.</li>
 * <li>Neither the name of the University of California/California Digital
 * Library, Ithaka Harbors/Portico, or Stanford University, nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.</li>
 * </ul>
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </p>
 */

package org.jhove2.module.format.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jhove2.annotation.ReportableProperty;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.reportable.AbstractReportable;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This class provides an wrapper for methods used to create and initialize the
 * SAX2 parser used to characterize an XML instance.
 * 
 * @author rnanders
 * 
 * @see <a href="http://www.saxproject.org/">official website for SAX</a> <br />
 * @see <a href="https://jaxp.dev.java.net/">Sun's JAXP website</a> <br />
 * @see <a href="http://xerces.apache.org/xerces2-j/">Xerces2 Java Parser</a>
 */
public class SaxParser extends AbstractReportable {

    /** The XmlModule object that is invoking the parser. */
    protected XmlModule xmlModule;

    /**
     * The XMLReader object (the actual parser) created and initialized in this
     * class.
     */
    protected XMLReader xmlReader;

    /** The explicit class name of the SAX driver being used. */
    protected String parser;

    /**
     * The set of SAX features (toggles) that have been set to fine tune the
     * behavior of the parser.
     */
    protected Map<String, String> features;

    /**
     * The set of SAX properties (such as event handlers) that have been
     * registered with the parser.
     */
    protected Map<String, Object> properties = new HashMap<String, Object>();

    /**
     * The name of the SAX driver class to be used for parsing can optionally be
     * set in the Spring config file that instantiates this class.
     * 
     * @param parser
     *            class name of the specific SAX driver being used
     */
    public void setParser(String parser) {
        this.parser = parser;
    }

    /**
     * Gets the parser's class name.
     * 
     * @return the parser's class name
     */
    @ReportableProperty(order = 1, value = "Java class used to parse the XML")
    public String getParser() {
        return parser;
    }

    /**
     * The set of SAX features that specify the behavior of the parser can
     * optionally be set in the Spring config file that instantiates this class.
     * 
     * @param features
     *            the set of (feture name, boolean value) pairs that will
     *            fine-tune parser behavior
     */
    public void setFeatures(Map<String, String> features) {
        this.features = features;
    }

    /**
     * Gets the list of SAX features that are currently in effect
     * 
     * @return a list of strings that specify the (feature name, boolean value)
     *         settings
     */
    @ReportableProperty(order = 2, value = "SAX Parser Feature Settings")
    public List<String> getSaxFeatures() {
        ArrayList<String> list = new ArrayList<String>();
        for (Entry<String, String> entry : features.entrySet()) {
            list.add(entry.getKey() + " = " + entry.getValue());
        }
        return list;
    }

    /**
     * Gets the list of SAX properties that are currently in effect
     * 
     * @return a list of strings that specify the (feature name, class name)
     *         settings
     */
    @ReportableProperty(order = 3, value = "SAX Parser Property Settings")
    public List<String> getSaxProperties() {
        ArrayList<String> list = new ArrayList<String>();
        for (Entry<String, Object> entry : properties.entrySet()) {
            if (entry.getValue() instanceof String) {
                list.add(entry.getKey() + " = " + entry.getValue().toString());
            }
            else {
                list.add(entry.getKey() + " = "
                        + entry.getValue().getClass().getName());
            }
        }
        return list;
    }

    /**
     * Sets a pointer to the XmlModule that has invoked this class.
     * 
     * @param xmlModule
     *            he XmlModule that has invoked this class
     */
    protected void setXmlModule(XmlModule xmlModule) {
        this.xmlModule = xmlModule;
    }

    /**
     * Creates and initializes the SAX2 XMLReader object (the actual parser).
     * 
     * @return the the SAX2 XMLReader object
     * 
     * @throws JHOVE2Exception
     */
    protected XMLReader getXmlReader() throws JHOVE2Exception {
        if (xmlReader == null) {
            createXmlReader();
            specifyXmlReaderFeatures();
            specifyXmlReaderHandlers();
            specifyXmlReaderHandlers2();
        }
        return xmlReader;
    }

    /**
     * Creates the SAX2 XMLReader object.
     * 
     * @throws JHOVE2Exception
     */
    private void createXmlReader() throws JHOVE2Exception {
        try {
            if (parser != null) {
                /*
                 * SAX Parser class name has been specified in the Spring config
                 * file
                 */
                xmlReader = XMLReaderFactory.createXMLReader(parser);
            }
            else {
                /*
                 * SAX Parser class name will be determined from value of
                 * org.xml.sax.driver set as an environmental variable or a
                 * META-INF value from a jar file in the classpath
                 */
                xmlReader = XMLReaderFactory.createXMLReader();
                parser = xmlReader.getClass().getName();
            }
        }
        catch (SAXException e) {
            throw new JHOVE2Exception("Could not create a SAX parser", e);
        }
    }

    /**
     * Initialize SAX2 XMLReader features.
     */
    private void specifyXmlReaderFeatures() {
        if (features != null) {
            for (Entry<String, String> entry : features.entrySet()) {
                try {
                    xmlReader.setFeature(entry.getKey(), Boolean
                            .parseBoolean(entry.getValue()));
                }
                catch (SAXNotRecognizedException e) {
                    entry.setValue("Feature not recognized by parser");
                }
                catch (SAXNotSupportedException e) {
                    entry.setValue("Feature not supported by parser");
                }
            }
        }
    }

    /**
     * Initialize core event handlers.
     */
    private void specifyXmlReaderHandlers() {
        xmlReader.setContentHandler(new SaxParserContentHandler(xmlReader,
                xmlModule));
        xmlReader.setDTDHandler(new SaxParserDtdHandler(xmlModule));
        xmlReader.setErrorHandler(new SaxParserErrorHandler(xmlModule));
    }

    /**
     * Initialize event handlers unique to SAX2.
     */
    private void specifyXmlReaderHandlers2() {
        properties.put("http://xml.org/sax/properties/declaration-handler",
                new SaxParserDeclHandler(xmlModule));
        properties.put("http://xml.org/sax/properties/lexical-handler",
                new SaxParserLexicalHandler(xmlModule));
        for (Entry<String, Object> entry : properties.entrySet()) {
            try {
                xmlReader.setProperty(entry.getKey(), entry.getValue());
            }
            catch (SAXNotRecognizedException e) {
                entry.setValue("Property not recognized by parser");
            }
            catch (SAXNotSupportedException e) {
                entry.setValue("Property not supported by parser");
            }
        }
    }

}
