/**
 * JHOVE2 - Next-generation architecture for format-aware characterization
 *
 * Copyright (c) 2009 by The Regents of the University of California,
 * Ithaka Harbors, Inc., and The Board of Trustees of the Leland Stanford
 * Junior University.
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

package org.jhove2.module.format.zip;

import org.jhove2.annotation.ReportableProperty;
import org.jhove2.core.Digest;
import org.jhove2.core.reportable.AbstractReportable;

/** Zip data descriptor.
 * 
 * @author slabrams
 */
public class ZipDataDescriptor
        extends AbstractReportable
{
    /** Compressed size of the file, in bytes. */
    protected long compressed;
    
    /** CRC-32 digest of the file. */
    protected Digest crc32;
    
    /** Optional descriptor signature, in hexadecimal. */
    protected String signature;
    
    /** Uncompressed size of the file, in bytes. */
    protected long uncompressed;
    
    /** Instantiate a new <code>ZipDataDescriptor</code>.
     * @param signature    Descriptor signature, in hexadecimal
     * @param crc32        CRC-32 digest of the file
     * @param compressed   Compressed size of the file
     * @param uncompressed Uncompressed size of the file
     */
    public ZipDataDescriptor(String signature, Digest crc32, long compressed,
                             long uncompressed) {
        this.signature    = signature;
        this.crc32        = crc32;
        this.compressed   = compressed;
        this.uncompressed = uncompressed;
    }
    
    /** Get compressed file size, in bytes.
     * @return Compressed file size
     */
    @ReportableProperty(order=3, value="Compressed file size, in bytes")
    public long getCompressedSize() {
        return this.compressed;
    }
    
    /** Get CRC-32 digest of the file.
     * @return CRC-32 digest
     */
    @ReportableProperty(order=2, value="CRC-32 digest of the file.")
    public Digest getCRC32() {
        return this.crc32;
    }
    
    /** Get data descriptor signature, in hexadecimal.
     * @return Data descriptor signature
     */
    @ReportableProperty(order=1, value="Data descriptor signature, in hexadecimal.")
    public String getSignature() {
        return this.signature;
    }
    
    /** Get uncompressed file size, in bytes.
     * @return Uncompressed file size
     */
    @ReportableProperty(order=4, value="Uncompressed file size, in bytes.")
    public long getUncompressedSize() {
        return this.uncompressed;
    }
}
