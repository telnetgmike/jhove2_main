package org.jhove2.module.format.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.DefaultHandler;

public class XmlParserContentHandler extends DefaultHandler {

	private XmlModule xmlModule;
	private XMLReader xmlReader;
	
    private Locator locator;
    private Locator2 locator2;

	
	public XmlParserContentHandler(XMLReader xmlReader, XmlModule xmlModule) {
		this.xmlReader = xmlReader;
		this.xmlModule = xmlModule;
	}
	
	/**
     * Receive a Locator object for document events.
      * @param locator A locator for all SAX document events.
     */
	@Override
    public void setDocumentLocator(Locator locator) {
		this.locator = locator;
        if (locator instanceof Locator2) {
            this.locator2 = (Locator2) locator;
        }
     }
    
	 /**
     * Receive notification of the beginning of the document.
     */
	@Override
    public void startDocument() {
        if (locator2 != null) {
        	xmlModule.xmlDeclaration.encoding = locator2.getEncoding();
        	xmlModule.xmlDeclaration.version = locator2.getXMLVersion();
        }
    }

	 /**
     * Receive notification of the end of the document.
     */
	@Override
    public void endDocument() {
         try {
        	// http://www.saxproject.org/apidoc/org/xml/sax/package-summary.html
        	// May be examined only during a parse, after the startDocument() callback has been completed; read-only.
        	// The value is true if the document specified standalone="yes" in its XML declaration, 
        	// and otherwise is false. 
        	boolean isStandalone = xmlReader.getFeature("http://xml.org/sax/features/is-standalone");
			xmlModule.xmlDeclaration.standalone = (isStandalone) ? "yes" : "no" ;			 		
		} catch (SAXException e) {
			xmlModule.xmlDeclaration.standalone = null;
		}
    }
	
	
	 /**
     *  Fires at the beginning of every element in the XML document
     */
	@Override
   public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
    {
        // The first element we encounter is the root.
        // Save it.
        if (xmlModule.rootElementName == null) {
            if (qName.equals("")) {
            	xmlModule.rootElementName = localName;
            } else {
            	xmlModule.rootElementName = qName;            	
            }
        }
    }   
	
	@Override
	public void processingInstruction (String target, String data) throws SAXException {
		List<ProcessingInstruction> piList = this.xmlModule.processingInstructions;
		if (piList == null)
			this.xmlModule.processingInstructions = new ArrayList<ProcessingInstruction>();
			piList = this.xmlModule.processingInstructions;
		ProcessingInstruction pi = new ProcessingInstruction();
		pi.target = target;
		pi.data = data;
		piList.add(pi);
	}
	
}
