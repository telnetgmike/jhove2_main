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

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;

import org.jhove2.core.Digest;

/** JHOVE2 MD2 message digester.  The MD2 algorithm is defined by RFC 1319.
 * 
 * @author mstrong, slabrams
 */
public class MD2Digester
	extends AbstractBufferDigester
{
	/** Algorithm name. */
	public static final String ALGORITHM = "MD2";
	
	/** Instantiate a new <code>MD2Digester</code>.
	 * @throws NoSuchAlgorithmException 
	 */
	public MD2Digester()
		throws NoSuchAlgorithmException
	{
		super(ALGORITHM);
	}

	/* (non-Javadoc)
	 * @see org.jhove2.module.digest.BufferDigester#update(java.nio.ByteBuffer)
	 */
	@Override
	public void update(ByteBuffer buffer) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.jhove2.module.digest.Digester#getDigest()
	 */
	@Override
	public Digest getDigest() {
		// TODO Auto-generated method stub
		return null;
	}

}