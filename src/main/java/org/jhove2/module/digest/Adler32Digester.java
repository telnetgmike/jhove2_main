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

package org.jhove2.module.digest;

import java.util.zip.Adler32;

import org.jhove2.core.Digest;

import com.sleepycat.persist.model.NotPersistent;
import com.sleepycat.persist.model.Persistent;

/**
 * JHOVE2 Adler-32 message digester.
 * 
 * @author mstrong, slabrams
 */
@Persistent
public class Adler32Digester extends AbstractArrayDigester {
	/** Algorothm name. */
	public static final String ALGORITHM = "Adler-32";

	/** Alder-32 digester. */
	@NotPersistent
	protected Adler32 digester;

	/**
	 * Instantiate a new <code>Adler32</code> digester.
	 */
	public Adler32Digester() {
		super(ALGORITHM);

		this.digester = new Adler32();
	}

	/**
	 * Update a message digest.
	 * 
	 * @param array
	 *            Byte array
	 * @see org.jhove2.module.digest.ArrayDigester#update(byte[])
	 */
	@Override
	public void update(byte[] array) {
		this.digester.update(array);
	}

	/**
	 * Get message digest value, as a hexadecimal string.
	 * 
	 * @return Message digest value, as a hexadecimal string
	 * @see org.jhove2.module.digest.DigesterAlgorithm#getDigest()
	 */
	@Override
	public Digest getDigest() {
		long value = this.digester.getValue();

		return new Digest(toHexString(value), this.algorithm);
	}
}
