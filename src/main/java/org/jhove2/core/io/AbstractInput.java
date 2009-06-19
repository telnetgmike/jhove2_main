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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/** Abstract JHOVE2 inputter.
 * 
 * @author mstrong, slabrams
 */
public abstract class AbstractInput
	implements Input
{
	/** Buffer to hold data from channel. */
	protected ByteBuffer buffer;

	/** Starting offset of the current buffer. */
	protected long bufferOffset;

	/** Current buffer size. */
	protected int bufferSize;

	/** Current byte order of buffer.*/ 
	protected ByteOrder byteOrder;

	/** AbstractInput channel. */
	protected FileChannel channel;
	
	/** File underlying the inputable. */
	protected File file;

	/** InputStream underlying the inputable. */
	protected InputStream stream;

	/** Current position relative to the beginning of the inputable, as a byte offset. 
	 * equal to buffer offset + buffer position */
	protected long inputablePosition;
	
	/** Size, in bytes. */
	protected long fileSize;

	/** Instantiate a new <code>AbstractInput</code>.
	 * @param file Java {@link java.io.File} underlying the inputable
	 */
	public AbstractInput(File file)
		throws FileNotFoundException, IOException
	{
		this.file              = file;
		this.stream            = (InputStream) new FileInputStream(file); 
		this.fileSize          = file.length();
		this.inputablePosition = 0L;

		RandomAccessFile raf = new RandomAccessFile(file, "r");
		this.channel = raf.getChannel();
	}

	/** Close the inputable.
	 * @see org.jhove2.core.io.Input#close()
	 */
	@Override
	public void close()
		throws IOException
	{
		this.channel.close();
	}
	
	/** Get the {@link java.nio.ByteBuffer} underlying the inputable.
	 * @return Buffer underlying the inputable
	 * @see org.jhove2.core.io.Input#getBuffer()
	 */
	@Override
	public ByteBuffer getBuffer() {
		return this.buffer;
	}
	
	/** Get {@link java.io.File} backing the input.
	 * @return File backing the input
	 * @see org.jhove2.core.io.Input#getFile()
	 */
	@Override
	public File getFile() {
		return this.file;
	}
	
	/** Get {@link java.io.InputStream} backing the input.
	 * @return Input stream backing the input
	 * @see org.jhove2.core.io.Input#getInputStream()
	 */	
	@Override
	public InputStream getInputStream() {
		return this.stream;
	}

	/** Get current buffer offset from the beginning of the inputable, in
	 * bytes.  This offset is the beginning of the buffer.
	 * 
	 * Buffer Offset = channel position - buffer size
	 * 
	 * Note: the channel position is the location in the inputable
	 * where data will next be read from or written to.
	 * @return Current buffer offset, in bytes
	 * @see org.jhove2.core.io.Input#getBufferOffset()
	 */
	@Override
	public long getBufferOffset() {
		return this.bufferOffset;
	}
	
	/** Get current buffer size, in bytes.
	 * @return Current buffer size, in bytes
	 * @see org.jhove2.core.io.Input#getBufferSize()
	 */
	@Override
	public int getBufferSize() {
		return this.bufferSize;
	}

	/** Get the current buffer as a <code>byte[]</code> array.
	 * @return Byte array
	 * @see org.jhove2.core.io.Input#getByteArray()
	 */
	@Override
	public byte[] getByteArray() {
		byte [] buffer;
		if (this.buffer.hasArray()) 
			buffer = this.buffer.array();
		else {
			buffer = new byte[this.buffer.limit()]; // capacity()];
			this.buffer.position(0);
			this.buffer.get(buffer);
		}
		
		return buffer;
	}
	
	/** Get maximum buffer size, in bytes.
	 * @return Maximum buffer size, in bytes
	 * @see org.jhove2.core.io.Input#getMaxBufferSize()
	 */
	@Override
	public abstract int getMaxBufferSize();

	/** Get the next buffer's worth of data from the channel.
	 * @return Number of bytes actually read, possibly 0 or -1 if EOF 
	 * @throws IOException 
	 */
	protected long getNextBuffer()
		throws IOException
	{
		this.buffer.clear();
		int n = this.channel.read(this.buffer);
		this.buffer.flip();
		this.bufferOffset      = this.channel.position() - n;
		this.bufferSize        = n;
		this.inputablePosition = this.bufferOffset + this.buffer.position();
		
		return this.bufferSize;
	}
	
	/** Get the current position in the inputable, as a byte offset.
	 * @return Current position, as a byte offset
	 * @see org.jhove2.core.io.Input#getPosition()
	 */
	@Override
	public long getPosition() {
		return this.inputablePosition;
	}

	/** Get size, in bytes.
	 * @return Size
	 */
	@Override
	public long getSize() {
		return this.fileSize;
	}

	/** Get signed byte at the current position.  This implicitly advances
	 * the current position by one byte.
	 * @return Signed byte at the current position, or -1 if EOF
	 * @see org.jhove2.core.io.Input#getSignedByte()
	 */
	@Override
	public byte readSignedByte()
		throws IOException
	{
		if (this.buffer.position() >= this.buffer.limit()) {
			if (getNextBuffer() == EOF) {
				return EOF;
			}
		}
		byte b = this.buffer.get();
		this.inputablePosition += 1;
		
		return b;
	}
	
	/** Get unsigned (four byte) integer at the current position.  This
	 * implicitly advances the current position by four bytes.
	 * @return Unsigned short integer at the current position, or -1 if EOF
	 * @see org.jhove2.core.io.Input#getUnsignedInt()
	 */
	@Override
	public int readSignedInt()
		throws IOException
	{
		int in = 0;
		int byteValue = 0;
		int remaining = this.buffer.limit() - this.buffer.position();
		if (remaining < 4) {
			for (int i=0; i<remaining; i++) {
				/* LITTLE_ENDIAN - shift byte value then add to accumlative value */
				if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
					byteValue = (((int) this.buffer.get()& 0xff));
					byteValue <<=(8*i);
					in += byteValue;
				}
				else {
					/* BIG_ENDIAN - shift accumulative value then add byte value */
				  in <<= 8;
				  in += (((int) this.buffer.get()& 0xff));
				}
			}
			if (getNextBuffer() == EOF) {
				return EOF;
			}
			for (int i=remaining; i<4; i++) {
				if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
					byteValue = (((int) this.buffer.get()& 0xff));
					byteValue <<= (8*i);
					in += byteValue;
				}
				else {
				  in <<=8;
				  in += (((int) this.buffer.get()& 0xff));
				}
			}
		}
		else {
			in = this.buffer.getInt();
		}
		this.inputablePosition += 4L;
		
		return in;
	}
	
	@Override
	public long readSignedLong() throws IOException {
		long in = 0;
		int byteValue = 0;
		int remaining = this.buffer.limit() - this.buffer.position();
		if (remaining < 8) {
			for (int i=0; i<remaining; i++) {
				/* LITTLE_ENDIAN - shift byte value then add to accumlative value */
				if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
					byteValue = (((int) this.buffer.get()& 0xff));
					byteValue <<=(8*i);
					in += byteValue;
				}
				else {
					/* BIG_ENDIAN - shift accumulative value then add byte value */
				  in <<= 8;
				  in += (((int) this.buffer.get()& 0xff));
				}
			}
			if (getNextBuffer() == EOF) {
				return EOF;
			}
			for (int i=remaining; i<8; i++) {
				if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
					byteValue = (((int) this.buffer.get()& 0xff));
					byteValue <<= (8*i);
					in += byteValue;
				}
				else {
				  in <<=8;
				  in += (((int) this.buffer.get()& 0xff));
				}
			}
		}
		else {
			in = this.buffer.getShort();
		}
		this.inputablePosition += 8L;
		
		return in;
	}

	

	@Override
	public short readSignedShort() throws IOException {
		{
			short in = 0;
			int byteValue = 0;
			int remaining = this.buffer.limit() - this.buffer.position();
			if (remaining < 2) {
				for (int i=0; i<remaining; i++) {
					/* LITTLE_ENDIAN - shift byte value then add to accumlative value */
					if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
						byteValue = (((int) this.buffer.get()& 0xff));
						byteValue <<=(8*i);
						in += byteValue;
					}
					else {
						/* BIG_ENDIAN - shift accumulative value then add byte value */
					  in <<= 8;
					  in += (((int) this.buffer.get()& 0xff));
					}
				}
				if (getNextBuffer() == EOF) {
					return EOF;
				}
				for (int i=remaining; i<2; i++) {
					if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
						byteValue = (((int) this.buffer.get()& 0xff));
						byteValue <<= (8*i);
						in += byteValue;
					}
					else {
					  in <<=8;
					  in += (((int) this.buffer.get()& 0xff));
					}
				}
			}
			else {
				in = this.buffer.getShort();
			}
			this.inputablePosition += 2L;
			
			return in;
		}
	}

	/** Get unsigned byte at the current position.  This implicitly advances
	 * the current position by one byte.
	 * @return Unsigned byte at the current position, or -1 if EOF
	 * @see org.jhove2.core.io.Input#getUnsignedByte()
	 */
	@Override
	public short readUnsignedByte()
		throws IOException
	{
		if (this.buffer.position() >= this.buffer.limit()) {
			if (getNextBuffer() == EOF) {
				return EOF;
			}
		}
		short b = this.buffer.get();
		b &= 0xffL;
		this.inputablePosition += 1L;
		
		return b;
	}

	/** Get unsigned (four byte) integer at the current position.  This
	 * implicitly advances the current position by four bytes.
	 * @return Unsigned short integer at the current position, or -1 if EOF
	 * @see org.jhove2.core.io.Input#getUnsignedInt()
	 */
	@Override
	public long readUnsignedInt()
		throws IOException
	{
		long in = 0L;
		long byteValue = 0L;
		int remaining = this.buffer.limit() - this.buffer.position();
		if (remaining < 4) {
			for (int i=0; i<remaining; i++) {
				/* LITTLE_ENDIAN - shift byte value then add to accumlative value */
				if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
					byteValue = (((long) this.buffer.get()& 0xffL));
					byteValue <<=(8*i);
					in += byteValue;
				}
				else {
					/* BIG_ENDIAN - shift accumulative value then add byte value */
				  in <<= 8;
				  in += (((long) this.buffer.get()& 0xffL));
				}
			}
			if (getNextBuffer() == EOF) {
				return EOF;
			}
			for (int i=remaining; i<4; i++) {
				if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
					byteValue = (((long) this.buffer.get()& 0xffL));
					byteValue <<= (8*i);
					in += byteValue;
				}
				else {
				  in <<=8;
				  in += (((long) this.buffer.get()& 0xffL));
				}
			}
		}
		else {
			in = this.buffer.getInt();
			in &= 0xffffffffL;
		}
		this.inputablePosition += 4L;
		
		return in;
	}


	/** Get unsigned short (two byte) integer at the the current position.  This
	 * implicitly advances the current position by two bytes.
	 * @return Unsigned integer at the current position, or -1 if EOF
	 * @see org.jhove2.core.io.Input#getUnsignedShort()
	 */
	@Override
	public int readUnsignedShort()
		throws IOException
	{
		int sh = 0;
		int byteValue = 0;
		int remaining = this.buffer.limit() - this.buffer.position();
		if (remaining < 2) {
			for (int i=0; i<remaining; i++) {
				/* LITTLE_ENDIAN - shift byte value then add to accumlative value */
				if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
					byteValue = (((int) this.buffer.get() & 0xff));
					byteValue <<=(8*i);
					sh += byteValue;
				}
				else {
					/* BIG_ENDIAN - shift accumulative value then add byte value */
					sh <<= 8;
				  sh += (((int) this.buffer.get()& 0xffL));
				}
			}
			if (getNextBuffer() == EOF) {
				return EOF;
			}
			for (int i=remaining; i<4; i++) {
				if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
					byteValue = (((int) this.buffer.get() & 0xff));
					byteValue <<= (8*i);
					sh += byteValue;
				}
				else {
					sh <<= 8;
				  sh += (((int) this.buffer.get()& 0xffL));
				}
			}
		}
		else {
			sh = this.buffer.getShort();
			sh &= 0xffffL;
		}
		this.inputablePosition += 2L;
		
		return sh;
	}
	
	/** Set byte order: big-endian or little-endian.
	 * @param order Byte order
	 * @see org.jhove2.core.io.Input#setByteOrder(ByteOrder)
	 */
	@Override
	public void setByteOrder(ByteOrder order) {
		this.buffer.order(order);
		this.byteOrder = this.buffer.order();
	}

	/** Set the current position, as a byte offset
	 * @param newInputablePosition Current position, as a byte offset
	 * @throws IOException 
	 * @see org.jhove2.core.io.Input#setPosition(long)
	 */
	@Override
	public void setPosition(long newInputablePosition)
		throws IOException
	{
		/* Determine if the new position is within the current buffer. */
		int  pos = this.buffer.position();
		int  lim = this.buffer.limit();
		long del = newInputablePosition - this.inputablePosition;
		if (-del > pos || del > lim - pos) {
			this.channel.position(newInputablePosition);
			getNextBuffer();
		}
		else {
			this.buffer.position(pos + (int) del);
			this.inputablePosition = newInputablePosition;
		}
		
	}
	
}
