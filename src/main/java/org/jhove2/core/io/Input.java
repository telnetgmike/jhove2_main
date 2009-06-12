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

package org.jhove2.core.io;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Interface for JHOVE2 inputters.
 * 
 * @author mstrong, slabrams
 */
public interface Input {
	/** AbstractInput buffer type. */
	public enum Type {
		Direct,
		NonDirect,
		MemoryMapped
	};
		
	/** Marker indicating end-of-file. */
	public final static int EOF = -1;
	
	/** Marker indicating an uninitialized value. */
	public final static int UNINITIALIZED = -1;
	
	/** Close the inputable.
	 */
	public void close()
		throws IOException;
	
	/** Get the {@link java.nio.ByteBuffer} underlying the inputable.
	 * @return Buffer underlying the inputable
	 */
	public ByteBuffer getBuffer();
	
	/** Get current buffer size, in bytes.
	 * @return Current buffer size, in bytes
	 */
	public int getBufferSize();

	/** Get the current buffer as a <code>byte[]</code> array.
	 * @return Byte array
	 */
	public byte [] getByteArray();
	
	/** Get current buffer offset from the beginning of the inputable, in
	 * bytes.
	 * @return Current buffer offset, in bytes
	 */
	public long getBufferOffset();
	
	/** Get {@link java.io.File} underlying the inputable.
	 * @return File underlying the inputable
	 */
	public File getFile();
	
	/** Get maximum buffer size, in bytes.
	 * @return Maximum buffer size, in bytes
	 */
	public int getMaxBufferSize();
	
	/** Get current position, as a byte offet.
	 * @return Current position, as a byte offset
	 */
	public long getPosition();
	
	/** Get signed byte at the current position.  This implicitly advances
	 * the current position by one byte.
	 * @return Signed byte at the current position, or -1 if EOF
	 */
	public byte getSignedByte()
		throws IOException;
	
	/** Get signed (four byte) integer at the the current position.  This
	 * implicitly advances the current position by four bytes.
	 * @return Unsigned integer at the current position, or -1 if EOF
	 */
	int getSignedInt() 
		throws IOException;
	
	/** Get size, in bytes.
	 * @return Size
	 */
	public long getSize();
	
	/** Get unsigned byte at the current position.  This implicitly advances
	 * the current position by one byte.
	 * @return Unsigned byte at the current position, or -1 if EOF
	 */
	public short getUnsignedByte()
		throws IOException;
	
	/** Get unsigned short (two byte) integer at the current position.  This
	 * implicitly advances the current position by two bytes.
	 * @return Unsigned short integer at the current position, or -1 if EOF
	 */
	public int getUnsignedShort()
		throws IOException;
	
	/** Get unsigned (four byte) integer at the the current position.  This
	 * implicitly advances the current position by four bytes.
	 * @return Unsigned integer at the current position, or -1 if EOF
	 */
	public long getUnsignedInt()
		throws IOException;
	
	/** Set byte order: big-endian or little-endian.
	 * @param order Byte order
	 */
	public void setByteOrder(ByteOrder order);
	
	/** Set current position, as a byte offset.
	 * @param position Position, as a byte offset
	 */
	public void setPosition(long position)
		throws IOException;


}