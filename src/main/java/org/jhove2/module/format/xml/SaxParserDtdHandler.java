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

import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * An instance of this class is registered with the SAX parser to handle events
 * related to the XML document's DTD declarations. In this application, the
 * information about notations and unparsed external entity declarations is
 * captured
 * 
 * @author rnanders
 * @see <a
 *      href="http://www.saxproject.org/apidoc/org/xml/sax/DTDHandler.html">DTDHandler javadoc</a>
 */
public class SaxParserDtdHandler extends DefaultHandler implements DTDHandler {

    /** The XmlModule object that is invoking the parser. */
    private XmlModule xmlModule;

    /**
     * Instantiates a new SaxParserDeclHandler object.
     * 
     * @param xmlModule
     *            the XmlModule object that is invoking the parser
     */
    public SaxParserDtdHandler(XmlModule xmlModule) {
        this.xmlModule = xmlModule;
    }

    /** Receive notification of a notation declaration event. */
    @Override
    public void notationDecl(String name, String publicId, String systemId)
            throws SAXException {
        Notation notation = new Notation();
        notation.name = name;
        notation.publicId = publicId;
        notation.systemId = systemId;
        xmlModule.notations.add(notation);
    }

    /** Receive notification of a unparsed entity declaration event. */
    @Override
    public void unparsedEntityDecl(String name, String publicId,
            String systemId, String notationName) throws SAXException {
        Entity entity = new Entity();
        entity.type = Entity.EntityType.ExternalUnparsed;
        entity.name = name;
        entity.publicId = publicId;
        entity.systemId = systemId;
        entity.notationName = notationName;
        xmlModule.entities.add(entity);

    }

}
