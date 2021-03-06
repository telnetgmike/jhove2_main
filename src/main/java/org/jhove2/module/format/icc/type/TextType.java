/**
 * JHOVE2 - Next-generation architecture for format-aware characterization
 * <p>
 * Copyright (c) 2010 by The Regents of the University of California. All rights reserved.
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

package org.jhove2.module.format.icc.type;

import java.io.EOFException;
import java.io.IOException;

import org.jhove2.annotation.ReportableProperty;
import org.jhove2.core.JHOVE2;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.Message;
import org.jhove2.core.Message.Context;
import org.jhove2.core.Message.Severity;
import org.jhove2.core.io.Input;
import org.jhove2.core.reportable.AbstractReportable;
import org.jhove2.core.source.MeasurableSource;
import org.jhove2.core.source.Source;
import org.jhove2.module.format.Validator.Validity;

import com.sleepycat.persist.model.Persistent;

/** ICC ASCII text type, as defined in ICC.1:2004-10, \u00a7 10.20.
 * This class also supports the older "desc" description type.
 * 
 * @author slabrams
 */
@Persistent
public class TextType
    extends AbstractReportable
{
    /** Text type signature. */
    public static final String SIGNATURE = "text";
    
    /** Validation status. */
    protected Validity isValid;
 
    /** Text size in bytes. */
    protected long size;
    
    /** Text. */
    protected StringBuffer text;

    /** Signature. */
    protected StringBuffer signature = new StringBuffer(4);   
    
    /** Invalid tag type message. */
    protected Message invalidTagTypeMessage;
    
    /** Missing final NUL (0x00) byte. */
    protected Message missingFinalNULByteMessage;
    
    /** Non-zero data in reserved field message. */
    protected Message nonZeroDataInReservedFieldMessage;
    
    /** Instantiate a new <code>TextType</code>. */
    public TextType() {
        super();
        
        this.isValid = Validity.Undetermined;
    }
    
    /** Parse an ICC text tag type.
     * @param jhove2 JHOVE2 framework
     * @param source ICC source unit
     * @param input  ICC source input
     * @param elementSize Element size
     * @return Number of bytes consumed
     * @throws EOFException
     *             If End-of-File is reached reading the source unit
     * @throws IOException
     *             If an I/O exception is raised reading the source unit
     * @throws JHOVE2Exception
     */
    public long parse(JHOVE2 jhove2, Source source, Input input, long elementSize)
        throws EOFException, IOException, JHOVE2Exception
    {
        long consumed  = 0L;
        int  numErrors = 0;
        this.isValid   = Validity.True;
        long start     = ((MeasurableSource) source).getStartingOffset();
  
        /* Tag signature. */
        for (int i=0; i<4; i++) {
            short b = input.readUnsignedByte();
            this.signature.append((char) b);
        }
        if (!this.signature.toString().equals(SIGNATURE)) {
            numErrors++;
            this.isValid = Validity.False;
            Object [] args =
                new Object [] {input.getPosition()-4L-start, SIGNATURE,
                               signature.toString()};
            this.invalidTagTypeMessage = new Message(Severity.ERROR,
                Context.OBJECT,
                "org.jhove2.module.format.icc.ICCTag.InvalidTagType",
                args, jhove2.getConfigInfo());
        }
        consumed += 4;
        
        /* Reserved. */
        int reserved = input.readSignedInt();
        if (reserved != 0) {
            numErrors++;
            this.isValid = Validity.False;
            Object [] args = new Object [] {input.getPosition()-4L-start};
            this.nonZeroDataInReservedFieldMessage = new Message(Severity.ERROR,
                    Context.OBJECT,
                    "org.jhove2.module.format.icc.ICCTag.NonZeroDataInReservedField",
                    args, jhove2.getConfigInfo());
        }
        consumed += 4;
        
        this.size = elementSize - 8;
        this.text = new StringBuffer((int) this.size);
        for (int i=1; i<this.size; i++) {
            short b = input.readUnsignedByte();
            text.append((char) b);
        }
        consumed += this.size - 1;
        short b = input.readUnsignedByte();
        if (b != 0) {
            numErrors++;
            this.isValid = Validity.False;
            Object [] args = new Object [] {input.getPosition()-1L-start, b};
            this.missingFinalNULByteMessage = new Message(Severity.ERROR,
                    Context.OBJECT,
                    "org.jhove2.module.format.icc.ICCTag.MissingFinalNULByte",
                    args, jhove2.getConfigInfo());
        }
        consumed++;
          
        return consumed;
    }
    
    /** Get invalid tag type message.
     * @return Invalid tag type message
     */
    @ReportableProperty(order=11, value="Invalid tag type.")
    public Message getInvalidTagTypeMessage() {
        return this.invalidTagTypeMessage;
    }

    /** Get text size.
     * @return Text size
     */
    @ReportableProperty(order=1, value="Text size.",
            ref="ICC.1:2004-10, \ua077 10.20")
    public long getSize() {
        return this.size;
    }
    
    /** Get non-zero data in reserved field message.
     * @return Non-zero data in reserved field message
     */
    @ReportableProperty(order=12, value="Non-zero data in reserved field.",
            ref="ICC.1:2004-10, \ua077 10.20")
    public Message getNonZeroDataInReservedFieldMessage() {
        return this.nonZeroDataInReservedFieldMessage;
    }
    
    /** Get missing final NUL (0x00) byte message.
     * @return Missing final NUL message
     */
    @ReportableProperty(order=13, value="Missing final NUL (0x00) byte.",
            ref="ICC.1:2004-10, \ua077 10.20")
    public Message getMissingFinalNULByteMessage() {
        return this.missingFinalNULByteMessage;
    }
    
    /** Get text.
     * @return Tex.
     */
    @ReportableProperty(order=2, value="Tex.",
            ref="ICC.1:2004-10, \u00a7 10.20")
    public String getText() {
        if (this.text != null) {
            return this.text.toString();
        }
        return null;
    }
    
    /** Get validation status.
     * @return Validation status
     */
    @ReportableProperty(order=3, value="Validation status.",
            ref="ICC.1:2004-10, \u00a7 10.20")
    public Validity isValid() {
        return this.isValid;
    }
}
