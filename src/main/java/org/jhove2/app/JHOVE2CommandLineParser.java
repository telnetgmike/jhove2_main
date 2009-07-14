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
import org.jhove2.core.io.Input.Type;

/** JHOVE2 command line application command line parser.
 * 
 * @author mstrong, slabrams
 */
public class JHOVE2CommandLineParser {
	/** {@link org.jhove2.core.io.Input} buffer size. */
	protected int bufferSize;
	
	/** Message digests flag; ff true, calculate message digests. */
	protected boolean calcDigests;
	
	/** {@link org.jhove2.core.io.Input} buffer type. */
	protected Type bufferType;
	
	/** Delete temporary files flag. */
	protected boolean deleteTempFiles;
	
	/** {@link org.jhove2.module.display.Displayer}. */
	protected String displayer;
	
	/** Fail fast limit. */
	protected int failFastLimit;
	
	/** File system path names. */
	protected List<String> names;
	
	/** Show identifiers flag; tf true, show identifiers in non-XML display
	 * modes.
	 */
	protected boolean showIdentifiers;
	
	/** Temporary directory. */
	protected String tempDirectory;
	
	/** Instantiate a new <code>JHOVE2CommandLineParser</code>.
	 */
	public JHOVE2CommandLineParser() {
		this(JHOVE2.DEFAULT_BUFFER_SIZE, JHOVE2.DEFAULT_BUFFER_TYPE,
			 JHOVE2.DEFAULT_CALC_DIGESTS, JHOVE2.DEFAULT_DELETE_TEMP_FILES,
			 JHOVE2.DEFAULT_DISPLAYER, JHOVE2.DEFAULT_FAIL_FAST_LIMIT,
			 JHOVE2.DEFAULT_SHOW_IDENTIFIERS);
	}
	
	/** Instantiate a new <code>JHOVE2CommandLineParser</code> with specific
	 * default values.
	 * @param bufferSize      Default {@link org.jhove2.core.io.Input} buffer
	 *                        size
	 * @param bufferType      Default {@link org.jhove2.core.io.Input} buffer
	 *                        type
	 * @param calcDigests     Default message digests flag; if true, calculate
	 *                        message digests
	 * @param deleteTemp      Default delete temporary files flag; if true,
	 *                        delete files 
	 * @param displayer       Default {@link org.jhove2.module.display.Displayer}
	 * @param failFastLimit   Default fail fast limit
	 * @param showIdentifiers Show identifiers flag; if true, show identifiers
	 *                        in JSON and Text display mode
	 */
	public JHOVE2CommandLineParser(int bufferSize, Type bufferType,
			                       boolean calcDigests, boolean deleteTempFiles,
			                       String displayer, int failFastLimit,
			                       boolean showIdentifiers) {
		this.bufferSize      = bufferSize;
		this.bufferType      = bufferType;
		this.calcDigests     = calcDigests;
		this.deleteTempFiles = deleteTempFiles;
		this.displayer       = displayer;
		this.failFastLimit   = failFastLimit;
		this.showIdentifiers = showIdentifiers;
		this.names           = new ArrayList<String>();
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
					else if (opt == 'B') {
						if (i+1 < args.length) {
							this.bufferType = Type.valueOf(args[++i]);
						}
					}
					else if (opt == 'd') {
						if (i+1 < args.length) {
							this.displayer = args[++i] + "Displayer";
						}
					}
					else if (opt == 'f') {
						if (i+1 < args.length) {
							this.failFastLimit = Integer.valueOf(args[++i]);
						}
					}
					else if (opt == 't') {
						if (i+1 < args.length) {
							this.tempDirectory = args[++i];
						}
					}
					else {
						if (args[i].indexOf('i') > -1) {
							this.showIdentifiers = true;
						}
						if (args[i].indexOf('k') > -1) {
							this.calcDigests = true;
						}
						if (args[i].indexOf('T') > -1) {
							this.deleteTempFiles = false;
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
	
	/** Get {@link org.jhove2.core.io.Input} buffer size.
	 * @return Buffer size
	 */
	public int getBufferSize() {
		return this.bufferSize;
	}
	
	/** Get {@link org.jhove2.core.io.Input} buffer type.
	 * @return Input buffer type
	 */
	public Type getBufferType() {
		return this.bufferType;
	}
	
	/** Get message digests flag.
	 * @return Message digests flag; if true, calculate message digests
	 */
	public boolean getCalcDigests() {
		return this.calcDigests;
	}
	
	/** Get delete temporary files flag.
	 * @param Delete temporary files flag
	 */
	public boolean getDeleteTempFiles() {
		return this.deleteTempFiles;
	}
	
	/** Get {@link org.jhove2.core.Displayeble}.
	 * @return AbstractDisplayer
	 */
	public String getDisplayer() {
		return this.displayer;
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
	
	/** Get show identifiers flag.
	 * @param Show identifiers flag
	 */
	public boolean getShowIdentifiers() {
		return this.showIdentifiers;
	}
	
	/** Get temporary directory.
	 * @return Temporary directory
	 */
	public String getTempDirectory() {
		return this.tempDirectory;
	}
}
