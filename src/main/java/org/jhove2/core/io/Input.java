/* JHOVE2 - Next-generation architecture for format-aware characterization
 *
 * Copyright (c) 2008 by The Regents of the University of California.
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
 *   Library nor the names of its contributors may be used to endorse or
 *   promote products derived from this software without specific prior
 *   written permission.
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

package org.jhove2.core.io;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * JHOVE2 input, an I/O abstraction used for parsing the contents of source
 * units.
 * 
 * @author MStrong
 */
public interface Input  {
	/** AbstractInput buffer type. */
	public enum Type {
		Direct, NonDirect, Mapped
	};

	/** Marker indicating end-of-file. */
	public final static int EOF = -1;

	/** Marker indicating an uninitialized value. */
	public final static int UNINITIALIZED = -1;

	/** Maximum file size region that can mapped ~1.6 GB. */
	public final static int MAX_MAPPED_FILE = 1717986918;

	/**
	 * Close the input and release all underlying system I/O resources.
	 * 
	 * @throws IOException
	 *             I/O exception closing input
	 */
	public void close() throws IOException;

	/**
	 * Get the {@link java.nio.ByteBuffer} underlying the Input.  If the
	 * buffer position is locally manipulated, the resetBuffer() method must
	 * be invoked prior to reading the Input.
	 * @see org.jhove2.core.io.Input#resetBuffer()
	 * 
	 * @return Buffer underlying the Input
	 */
	public ByteBuffer getBuffer();

	/**
	 * Get current buffer size, in bytes.
	 * 
	 * @return Current buffer size, in bytes
	 */
	public int getBufferSize();

	/**
	 * Get the current buffer as a <code>byte[]</code> array.
	 * 
	 * @return Byte array
	 */
	public byte[] getByteArray();

	/**
	 * Get byte order.
	 * 
	 * @return Byte order
	 */
	public ByteOrder getByteOrder();

	/**
	 * Get current buffer offset from the beginning of the inputable, in bytes.
	 * 
	 * @return Current buffer offset, in bytes
	 */
	public long getBufferOffset();

	/** Get buffer type.
	 * @return Buffer type
	 */
	public Type getBufferType();

	/**
	 * Get maximum buffer size, in bytes.
	 * 
	 * @return Maximum buffer size, in bytes
	 */
	public int getMaxBufferSize();

	/**
	 * Get current position, as a byte offet.
	 * 
	 * @return Current position, as a byte offset
	 */
	public long getPosition();
	
	/** Get UTF-16BE Unicode character at the current position.  This
	 * implicitly advances the current position by two bytes.
	 * @return Characer at the current position
	 */
	public char readChar() throws EOFException, IOException;

    /**
     * Get signed double float point at the current position. This implicitly advances
     * the current position by eight bytes.
     * 
     * @return signed double floating point at the current position, or -1 if EOF
     */
    double readDouble() throws IOException;

    /**
     * Get signed 32 bit floating point float at the current position. This implicitly advances
     * the current position by four bytes.
     * 
     * @return signed 32 bit floating point float at the current position, or -1 if EOF
     */
    float readFloat() throws IOException;
    
	/**
	 * Get signed byte at the current position. This implicitly advances the
	 * current position by one byte.
	 * 
	 * @return Signed byte at the current position, or -1 if EOF
	 * @throws IOException
	 *             I/O exception reading byte
	 */
	public byte readSignedByte() throws IOException;

	/**
	 * read signed (four byte) integer at the current position. This
	 * implicitly advances the current position by four bytes.
	 * 
	 * @return Unsigned integer at the current position, or -1 if EOF
	 * @throws IOException
	 *             I/O exception reading int
	 */
	int readSignedInt() throws IOException;

	/**
	 * Get signed short at the current position. This implicitly advances the
	 * current position by two bytes.
	 * 
	 * @return Signed short at the current position, or -1 if EOF
	 * @throws IOException
	 *             I/O exception reading short
	 */
	public short readSignedShort() throws IOException;

	/**
	 * Get signed long at the current position. This implicitly advances the
	 * current position by eight bytes.
	 * 
	 * @return Signed long at the current position, or -1 if EOF
	 * @throws IOException
	 *             I/O exception reading long
	 */
	public long readSignedLong() throws IOException;

	/**
	 * read unsigned byte at the current position. This implicitly advances the
	 * current position by one byte.
	 * 
	 * @return Unsigned byte at the current position, or -1 if EOF
	 * @throws IOException
	 *             I/O exception reading byte
	 */
	public short readUnsignedByte() throws IOException;

	/**
	 * read unsigned short (two byte) integer at the current position. This
	 * implicitly advances the current position by two bytes.
	 * 
	 * @return Unsigned short integer at the current position, or -1 if EOF
	 * @throws IOException
	 *             I/O exception reading short
	 */
	public int readUnsignedShort() throws IOException;

	/**
	 * read unsigned (four byte) integer at the the current position. This
	 * implicitly advances the current position by four bytes.
	 * 
	 * @return Unsigned integer at the current position, or -1 if EOF
	 * @throws IOException
	 *             I/O exception reading int
	 */
	public long readUnsignedInt() throws IOException;
    
    /** Reset the buffer position.  This method is only necessary after
     * a buffer has been retrieved via getBuffer() and the position of 
     * that local copy has been manipulated.
     * @see org.jhove2.core.io.Input#getBuffer()
     */
    public void resetBuffer();

	/**
	 * Set byte order: big-endian or little-endian.
	 * 
	 * @param order
	 *            Byte order
	 */
	public void setByteOrder(ByteOrder order);

    /**
     * Set temporary file deletion flag; if true, delete on close.
     * 
     * @param flag
     *            Delete temporary files flag
     */
    public void setDeleteTempFileOnClose(boolean flag);

	/**
	 * Set current position, as a byte offset.
	 * 
	 * @param position
	 *            Position, as a byte offset
	 * @throws IOException
	 *             I/O exception setting position
	 */
	public void setPosition(long position) throws IOException;

	/** Set buffer type.
	 * @param type Buffer type
	 */
	public void setBufferType(Type type);
}
