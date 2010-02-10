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

package org.jhove2.module.format.directory;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;

import org.jhove2.core.Format;
import org.jhove2.core.JHOVE2;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.source.DirectorySource;
import org.jhove2.core.source.Source;
import org.jhove2.module.format.BaseFormatModule;

/**
 * JHOVE2 file system directory module.
 * 
 * @author mstrong, slabrams
 */
public class DirectoryModule
	extends BaseFormatModule

{
	/** Directory module version identifier. */
	public static final String VERSION = "0.1.2";

	/** Directory module release date. */
	public static final String RELEASE = "2010-01-26";

	/** Directory module rights statement. */
	public static final String RIGHTS = "Copyright 2010 by The Regents of the University of California, "
			+ "Ithaka Harbors, Inc., and The Board of Trustees of the Leland "
			+ "Stanford Junior University. "
			+ "Available under the terms of the BSD license.";

	/**
	 * Instantiate a new <code>DirectoryModule</code>.
	 * 
	 * @param format
	 *            Directory format
	 */
	public DirectoryModule(Format format) {
		super(VERSION, RELEASE, RIGHTS, Scope.Generic, format);
	}

	/**
	 * Parse a directory source unit.
	 * 
	 * @param jhove2
	 *            JHOVE2 framework
	 * @param source
	 *            Directory source unit
	 * @return 0
	 * @throws EOFException
	 *             If End-of-File is reached reading the source unit
	 * @throws IOException
	 *             If an I/O exception is raised reading the source unit
	 * @throws JHOVE2Exception
	 * @see org.jhove2.module.format.FormatModule#parse(org.jhove2.core.JHOVE2,
	 *      org.jhove2.core.source.Source)
	 */
	@Override
	public long parse(JHOVE2 jhove2, Source source)
		throws EOFException, IOException, JHOVE2Exception
	{
		if (source instanceof DirectorySource) {
			List<Source> children = ((DirectorySource) source)
					.getChildSources();
			for (Source src : children) {
				jhove2.characterize(src);
			}
		}
		return 0;
	}
}
