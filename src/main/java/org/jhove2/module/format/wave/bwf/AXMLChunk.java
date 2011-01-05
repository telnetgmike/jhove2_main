/**
 * JHOVE2 - Next-generation architecture for format-aware characterization
 *
 * Copyright (c) 2009 by The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * o Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * o Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * o Neither the name of the University of California/California Digital
 *   Library, Ithaka Harbors/Portico, or Stanford University, nor the names of
 *   its contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
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
 */

package org.jhove2.module.format.wave.bwf;

import java.io.EOFException;
import java.io.IOException;
import org.jhove2.core.I8R;
import org.jhove2.core.JHOVE2;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.format.Format;
import org.jhove2.core.format.FormatIdentification;
import org.jhove2.core.format.FormatIdentification.Confidence;
import org.jhove2.core.io.Input;
import org.jhove2.core.source.ByteStreamSource;
import org.jhove2.core.source.Source;
import org.jhove2.module.format.riff.GenericChunk;

/** Broadcast Wave Format (BWF) AXML chunk.
 * 
 * @author slabrams
 */
public class AXMLChunk
    extends GenericChunk
{
    /** XML format. */
    protected Format xmlFormat;
    
    /** Instantiate a new <code>AXMLChunk</code>.
     * @param xml XML format
     */
    public AXMLChunk(Format xml)
    {
        super();
        
        this.xmlFormat = xml;
    }
    
    /** Parse an AXML chunk.
     * @param jhove2 JHOVE2 framework
     * @param source WAVE source unit
     * @param input  WAVE source input
     * @return Number of bytes consumed
     * @throws JHOVE2Exception 
     * @throws IOException 
     * @throws EOFException 
     */
    @Override
    public long parse(JHOVE2 jhove2, Source source, Input input)
        throws EOFException, IOException, JHOVE2Exception
    {
        long consumed = super.parse(jhove2, source, input);
        
        /* The chunk contents are in XML; invoke the XML module. */
        ByteStreamSource child =
            new ByteStreamSource(jhove2, source, input.getPosition(), this.size);
        I8R xml = xmlFormat.getIdentifier();
        FormatIdentification id = new FormatIdentification(xml, Confidence.PositiveGeneric);
        child.addPresumptiveFormat(id);
        jhove2.characterize(child, input);      
        consumed += this.size;
        
        return consumed;
    }
    
    /** Get XML format.
     * @return XML format
     */
    public Format getXMLFormat() {
        return this.xmlFormat;
    }
}
