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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.jhove2.core.JHOVE2;
import org.jhove2.core.io.Input.Type;

/**
 * JHOVE2 non direct inputable.
 * 
 * @author mstrong, slabrams
 */
public class NonDirectInput
    extends AbstractInput
{
	/**
	 * Instantiate a new, big-endian <code>NonDirectInput</code> object.
	 * @param jhove2 JHOVE2 framework object
	 * @param file
	 *            Java {@link java.io.File} underlying the inputable
     * @param isTemp
     *            Temporary file  status: true if temporary
	 * @throws FileNotFoundException
	 *             File not found
	 * @throws IOException
	 *             I/O exception instantiating input
	 */
	public NonDirectInput(JHOVE2 jhove2, File file, boolean isTemp)
			throws FileNotFoundException, IOException {
		this(jhove2, file, isTemp, ByteOrder.BIG_ENDIAN);
	}

	/**
	 * Instantiate a new <code>NonDirectInput</code> object.
	 * @param jhove2 JHOVE2 framework object
	 * @param file
	 *            Java {@link java.io.File} underlying the inputable
     * @param isTemp
     *            Temporary file status: true if temporary
	 * @param order
	 *            Byte order of the underlying buffer
	 * @throws FileNotFoundException
	 *             File not found
	 * @throws IOException
	 *             I/O exception instantiating input
	 */
	public NonDirectInput(JHOVE2 jhove2, File file, boolean isTemp, ByteOrder order)
		throws FileNotFoundException, IOException
	{
		super(jhove2, file, isTemp, order);
        this.bufferType = Type.NonDirect;

		/* Allocate direct buffer and initialize it. */
		this.buffer = ByteBuffer.allocate(this.maxBufferSize).order(order);
		getNextBuffer();
	}
}
