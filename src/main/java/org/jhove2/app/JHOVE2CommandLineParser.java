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

package org.jhove2.app;

import java.util.ArrayList;
import java.util.List;

import org.jhove2.core.JHOVE2;

/**
 * @author slabrams
 *
 */
public class JHOVE2CommandLineParser {
	/** Buffer size. */
	protected int bufferSize;
	
	/** Fail fast limit. */
	protected int failFastLimit;
	
	/** File system path names. */
	protected List<String> names;
	
	/** Instantiate a new <code>JHOVE2CommandLineParser</code>.
	 */
	
	/** Instantiate a new <code>JHOVE2CommandLineParser</code>.
	 */
	public JHOVE2CommandLineParser() {
		this(JHOVE2.DEFAULT_BUFFER_SIZE, JHOVE2.DEFAULT_FAIL_FAST_LIMIT);
	}
	
	/** Instantiate a new <code>JHOVE2CommandLineParser</code> with specific
	 * default values.
	 * @param bufferSize    Default buffer size
	 * @param failFastLimit Default fail fast limit
	 */
	public JHOVE2CommandLineParser(int bufferSize, int failFastLimit) {
		this.bufferSize    = bufferSize;
		this.failFastLimit = failFastLimit;
		this.names         = new ArrayList<String>();
	}
	
	/** Parse the JHOVE2 application command line.
	 * @param args Command line arguments
	 * @return File system path names
	 */
	public List<String> parse(String [] args) {
		/* TODO: add more robust error handling. */
		for (int i=0; i<args.length; i++) {
			if (args[i].charAt(0) == '-') {
				if (args[i].length() > 1) {
					char opt = Character.toLowerCase(args[i].charAt(1));
					if      (opt == 'b') {
						if (i+1 < args.length) {
							this.bufferSize = Integer.valueOf(args[++i]);
						}
					}
					else if (opt == 'f') {
						if (i+1 < args.length) {
							this.failFastLimit = Integer.valueOf(args[++i]);
						}
					}
				}
			}
			else {
				this.names.add(args[i]);
			}
		}
		
		return this.names;
	}
	
	/** Get buffer size.
	 * @return Buffer size
	 */
	public int getBufferSize() {
		return this.bufferSize;
	}
	
	/** Get fail fast limit.
	 * @return Fail fast limit
	 */
	public int getFailFastLimit() {
		return this.failFastLimit;
	}
	
	/** Get file system path names.
	 * @return File system path names
	 */
	public List<String> getPathNames() {
		return this.names;
	}
}
