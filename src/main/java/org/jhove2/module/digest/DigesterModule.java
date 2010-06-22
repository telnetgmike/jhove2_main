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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jhove2.core.Digest;
import org.jhove2.core.Invocation;
import org.jhove2.core.JHOVE2;
import org.jhove2.core.io.Input;
import org.jhove2.core.source.Source;
import org.jhove2.module.AbstractModule;

/**
 * JHOVE2 message digester module.
 * 
 * @author mstrong, slabrams
 */
public class DigesterModule
	extends AbstractModule 
	implements Digester
{
	/** Framework version identifier. */
	public static final String VERSION = "1.9.5";

	/** Framework release date. */
	public static final String RELEASE = "2010-02-16";

	/** Framework rights statement. */
	public static final String RIGHTS = "Copyright 2010 by The Regents of the University of California, "
		+ "Ithaka Harbors, Inc., and The Board of Trustees of the Leland "
		+ "Stanford Junior University. "
		+ "Available under the terms of the BSD license.";

	/** Algorithm-specific byte array digesters. */
	protected List<ArrayDigester> arrayDigesters;

	/** Algorithm-specific byte buffer digesters. */
	protected List<BufferDigester> bufferDigesters;

	/**
	 * Instantiate a new <code>DigesterModule</code>.
	 */
	public DigesterModule() {
		super(VERSION, RELEASE, RIGHTS, Scope.Specific);
	}

	/**
	 * Calculate message digests for the source unit.
	 * 
	 * @param jhove2
	 *            JHOVE2 framework
	 * @param source
	 *            Source unit
	 * @see org.jhove2.module.digest.Digester#digest(org.jhove2.core.JHOVE2,
	 *      org.jhove2.core.source.Source)
	 * @throws IOException
	 *             I/O exception calculating message digests
	 */
	@Override
	public void digest(JHOVE2 jhove2, Source source)
		throws IOException
	{
		Input input = null;
		try {
			Invocation config = jhove2.getInvocation();
			input = source.getInput(config.getBufferSize(), 
					                config.getBufferType());
			if (input != null) {
				long inputSize = input.getSize();
				long bufferSize = input.getMaxBufferSize();
				long ptr = 0L;
				while (inputSize - ptr > -1L) {
					input.setPosition(ptr);
					if (this.arrayDigesters != null
							&& this.arrayDigesters.size() > 0) {
						byte[] array = input.getByteArray();
						Iterator<ArrayDigester> iter = this.arrayDigesters
						.iterator();
						while (iter.hasNext()) {
							ArrayDigester digester = iter.next();
							digester.update(array);
						}
					}
					if (this.bufferDigesters != null
							&& this.bufferDigesters.size() > 0) {
						ByteBuffer buffer = input.getBuffer();
						Iterator<BufferDigester> iter = this.bufferDigesters
						.iterator();
						while (iter.hasNext()) {
							BufferDigester digester = iter.next();
							buffer.position(0);
							digester.update(buffer);
						}
					}
					ptr += bufferSize;
				}
			}
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}
	
	/**
	 * Get message digests.
	 * 
	 * @return Message digests
	 * @see org.jhove2.module.digest.Digester#getDigests()
	 */
	@Override
	public Set<Digest> getDigests() {
		Set<Digest> set = new TreeSet<Digest>();
		if (this.arrayDigesters != null && this.arrayDigesters.size() > 0) {
			Iterator<ArrayDigester> iter = this.arrayDigesters.iterator();
			while (iter.hasNext()) {
				ArrayDigester digester = iter.next();
				Digest digest = digester.getDigest();
				set.add(digest);
			}
		}
		if (this.bufferDigesters != null && this.bufferDigesters.size() > 0) {
			Iterator<BufferDigester> iter = this.bufferDigesters.iterator();
			while (iter.hasNext()) {
				BufferDigester digester = iter.next();
				Digest digest = digester.getDigest();
				set.add(digest);
			}
		}

		return set;
	}

	/**
	 * Set the algorithm-specific byte array digesters.
	 * 
	 * @param digesters
	 *            Algorithm-specific byte array digesters
	 */
	public void setArrayDigesters(List<ArrayDigester> digesters) {
		this.arrayDigesters = digesters;
	}

	/**
	 * Set the algorithm-specific byte buffer digesters.
	 * 
	 * @param digesters
	 *            Algorithm-specific byte buffer digesters
	 */
	public void setBufferDigesters(List<BufferDigester> digesters) {
		this.bufferDigesters = digesters;
	}
}
