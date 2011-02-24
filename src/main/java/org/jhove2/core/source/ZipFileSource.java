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

package org.jhove2.core.source;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.zip.ZipEntry;

import org.jhove2.annotation.ReportableProperty;
import org.jhove2.core.Digest;
import org.jhove2.core.JHOVE2;
import org.jhove2.core.io.Input;
import org.jhove2.core.io.InputFactory;
import org.jhove2.module.digest.AbstractArrayDigester;
import org.jhove2.module.digest.CRC32Digester;

import com.sleepycat.persist.model.Persistent;

/**
 *  Zip file source unit.
 * 
 * @author mstrong, slabrams
 */
@Persistent
public class ZipFileSource
    extends AbstractSource
    implements MeasurableSource, NamedSource
{
	/** CRC message digest value recorded in the ZipEntry. */
	protected long crc;
	
	/** Zip file CRC-32 message digest. */
	protected Digest crc32;

	/** Zip file comment. */
	protected String comment;
	
    /** Ending offset, in bytes, relative to the parent source.  If there is
     * no parent, the ending offset is the size.
     */
    protected long endingOffset;

	/** Zip file last modified date. */
	protected Date lastModified;

	/** Zip file path. */
	protected String path;

	/** Zip file size, in bytes. */
	protected long size;

    /** Starting offset, in bytes, relative to the parent source.  If there is
     * no parent, the starting offset is 0.
     */
    protected long startingOffset;

	protected ZipFileSource(){
		super();
	}
	/**
	 * Instantiate a new <code>ZipFileSource</code>.
	 * @param jhove2 JHOVE2 framework object 
     * @param entry
     *            Zip file entry
	 * @param stream
	 *            Input stream for the Zip file entry
	 * @param name
	 *            File name
	 * @throws IOException
	 */
	protected ZipFileSource(JHOVE2 jhove2, ZipEntry entry, InputStream stream, String name)
		throws IOException
	{
		super(jhove2, stream, name);
		this.path = entry.getName();
		this.size = entry.getSize();
		this.lastModified = new Date(entry.getTime());
		this.crc = entry.getCrc();
		this.crc32 = new Digest(AbstractArrayDigester.toHexString(this.crc), CRC32Digester.ALGORITHM);
		this.comment = entry.getComment();
		this.startingOffset = 0L;
		this.endingOffset   = this.size;
        if (this.size > 0L) {
            this.endingOffset--;
        }
		
		/* This is a temporary fix.  We need to keep the temporary backing
		 * files for Zip components in case we need to later get an
		 * {@link java.io.InputStream} on the component
		 * (Source.getInputStream()) to pass to a third-party package that
		 * doesn't support {@link org.jhove2.core.io.Input}s.
		 * 
		 * Note that the temporary files will accumulate in the temporary
		 * directory after termination.
		 * 
		 * TODO: Find a better mechanism for dealing with this problem
		 * in the recursive processing model.
		 */
		this.deleteTempFileOnClose = false;
	}

	/**
	 * Get Zip file comment.
	 * 
	 * @return Zip file comment
	 */
	@ReportableProperty(order = 5, value = "Zip file comment.")
	public String getComment() {
		return this.comment;
	}
	   
    /**
     * CRC value recorded in ZipEntry
     * @return CRC value
     */
    public long getCRC() {
        return crc;
    }
    
	/**
	 * Get Zip file CRC-32 message digest.
	 * 
	 * @return Zip file CRC-32 message digest
	 */
	@ReportableProperty(order = 4, value = "Zip file CRC-32 message digest.")
	public Digest getCRC32MessageDigest() {
		return this.crc32;
	}

    /** Get ending offset of the source unit, in bytes, relative to the
     * parent source.  If there is no parent, the ending offset is the
     * size.
     * @return Starting offset of the source unit
     */
    @Override
    public long getEndingOffset() {
        return this.endingOffset;
    }

    /**
     * Create and get {@link org.jhove2.core.io.Input} for the source unit. Concrete
     * classes extending this abstract class must provide an implementation of
     * this method if they are are based on parsable input. Classes without
     * parsable input (e.g. {@link org.jhove2.core.source.ClumpSource} or
     * {@link org.jhove2.core.source.DirectorySource} can let this inherited
     * method return null.
     * 
     * TODO: This override is only necessary to set the Input delete-on-close
     * flag to the the Source flag.  Once we have a better way to maintain the
     * temporary files for Zip components, this method can be removed.
     * 
     * @param jhove2 JHOVE2 framework object
     * @param order
     *            Byte order
     * @return null
     * @throws FileNotFoundException
     *             File not found
     * @throws IOException
     *             I/O exception getting input
     */
    @Override
    public Input getInput(JHOVE2 jhove2, ByteOrder order)
        throws FileNotFoundException, IOException
    {
        Input input = InputFactory.getInput(jhove2, this.file, this.isTemp, order);
        /* Set the Input delete-on-close flag to the Source flag. */
        input.setDeleteTempOnClose(this.deleteTempFileOnClose);
        return input;
    }
    
	/**
	 * Get Zip file last modified date.
	 * 
	 * @return Zip file last modified date
	 */
	@ReportableProperty(order = 3, value = "Zip file last modified date.")
	public Date getLastModified() {
		return this.lastModified;
	}

	/**
	 * Get Zip file path.
	 * 
	 * @return Zip file path
	 */
	@ReportableProperty(order = 1, value = "Zip file path.")
	public String getPath() {
		return this.path;
	}

	/**
	 * Get Zip file size, in bytes.
	 * 
	 * @return Zip file size, in bytes
	 */
	@ReportableProperty(order = 2, value = "Zip file size, in bytes.")
	public long getSize() {
		return this.size;
	}

    /**
     * Get Zip file name.
     * 
     * @return Zip file name
     * @see org.jhove2.core.source.NamedSource#getSourceName()
     */
    @Override
    public String getSourceName() {
        return this.path;
    }

    /** Get starting offset of the source unit, in bytes, relative to the
     * parent source.  If there is no parent, the starting offset is 0.
     * @return Starting offset of the source unit
     */
    @Override
    public long getStartingOffset() {
        return this.startingOffset;
    }
 
	/** Equality comparison.
	 * @oparam obj The object being compared
	 * @return True if the object is equal
	 */
	@Override
	public boolean equals(Object obj){
		boolean equals = false;
		if (obj==null){
			return false;
		}
		if (this == obj){
			return true;
		}
		equals = obj instanceof ZipFileSource;
		if (!equals){
			return false;
		}
		ZipFileSource zObj = (ZipFileSource)obj;
		if (this.getPath()==null){
			if (zObj.getPath() != null){
				return false;
			}
		}
		else if (zObj.getPath()==null){
			return false;
		}
		equals = this.getPath().equalsIgnoreCase(zObj.getPath());
		if (!equals){
			return false;
		}
		if (this.getLastModified()==null){
			if (zObj.getLastModified()!= null){
				return false;
			}
		}
		else if (zObj.getLastModified()==null){
			return false;
		}
		equals = this.getLastModified().equals(zObj.getLastModified());
		if (!equals){
			return false;
		}
		if (this.getComment()==null){
			if (zObj.getComment()!= null){
				return false;
			}
		}
		else if (zObj.getComment()==null){
			return false;
		}
		equals = this.getComment().equalsIgnoreCase(zObj.getComment());
		if (!equals){
			return false;
		}
		equals = this.getCRC()==zObj.getCRC();
		if (!equals){
			return false;
		}
		equals = this.getSize()==zObj.getSize();
		if (!equals){
			return false;
		}
		return super.equals(obj);
	}
	
	/** Generate a unique hash code for the Zip File source.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + (int) (crc ^ (crc >>> 32));
        result = prime * result + ((crc32 == null) ? 0 : crc32.hashCode());
        result = prime * result
                + ((lastModified == null) ? 0 : lastModified.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + (int) (size ^ (size >>> 32));
        return result;
    }
    
    /** Comparison.
	 * @param source Source unit to be compared
	 * @return Return -1, 0, 1 if the compared source unit collates less than,
	 *         equal to, or greater than the underlying source unit
	 */
	@Override
	public int compareTo(Source source){
		int comp = 0;
		if (source==null){
			return 1;
		}
		if (this==source){
			return 0;
		}
		if (!(source instanceof ZipFileSource)){
			int compareSource = this.getReportableIdentifier().
				compareTo(source.getReportableIdentifier());
			return compareSource;
		}
		ZipFileSource zObj = (ZipFileSource)source;
		if (this.getPath()==null){
			if (zObj.getPath()!= null){
				return -1;
			}
		}
		else if (zObj.getPath()==null){
			return 1;
		}
		else {
			comp = this.getPath().compareToIgnoreCase(zObj.getPath());
			if (comp < 0){
				return -1;
			}
			else if (comp > 0){
				return 1;
			}
		}
		if (this.getLastModified()==null){
			if (zObj.getLastModified()!= null){
				return -1;
			}
		}
		else if (zObj.getLastModified()==null){
			return 1;
		}
		else {
			comp = this.getLastModified().compareTo(zObj.getLastModified());
			if (comp < 0){
				return -1;
			}
			else if (comp > 0){
				return 1;
			}
		}
		if (this.getComment()==null){
			if (zObj.getComment()!= null){
				return -1;
			}
		}
		else if (zObj.getComment()==null){
			return 1;
		}
		else {
			comp = this.getComment().compareToIgnoreCase(zObj.getComment());
			if (comp < 0){
				return -1;
			}
			else if (comp > 0){
				return 1;
			}
		}
		if (this.getCRC()< zObj.getCRC()){
			return -1;
		}
		else if (this.getCRC()>zObj.getCRC()){
			return 1;
		}
		if (this.getSize()< zObj.getSize()){
			return -1;
		}
		else if (this.getSize()> zObj.getSize()){
			return 1;
		}
		return super.compareTo(source);
	}
}
