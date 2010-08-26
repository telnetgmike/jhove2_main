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

import org.jhove2.core.reportable.AbstractReportable;

/**
 * Abstract JHOVE2 algorithm-specific message digester that operates on a byte
 * array.
 * 
 * @author mstrong, slabrams
 */
public abstract class AbstractArrayDigester extends AbstractReportable implements ArrayDigester {
	/** Message digest algorithm. */
	protected String algorithm;
	
	public AbstractArrayDigester(){
		super();
	}

	/**
	 * Instantiate a new <code>AbstractArrayDigester</code>.
	 * 
	 * @param algorithm
	 *            Message digest algorithm
	 */
	public AbstractArrayDigester(String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * Format a message digest value as a hexadecimal string.
	 * 
	 * @param digest
	 *            Message digest value
	 * @return Message digest value as a hexadecimal string
	 */
	public static synchronized String toHexString(long digest) {
		StringBuffer hex = new StringBuffer();
		String h = Long.toHexString(digest);
		int len = h.length();
		for (int i = len; i < 8; i++) {
			hex.append("0");
		}
		hex.append(h);

		return hex.toString();
	}
}
