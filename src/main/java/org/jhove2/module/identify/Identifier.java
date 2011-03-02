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

package org.jhove2.module.identify;

import java.io.IOException;
import java.util.Set;

import org.jhove2.annotation.ReportableProperty;
import org.jhove2.core.JHOVE2;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.format.FormatIdentification;
import org.jhove2.core.io.Input;
import org.jhove2.core.source.Source;
import org.jhove2.module.Module;

/**
 * Interface for JHOVE2 identifier modules.
 * 
 * @author mstrong, slabrams, smmorrissey
 */
public interface Identifier
	extends Module
{
	/**
	 * Presumptively identify the format of a source unit.
	 * 
	 * @param jhove2
	 *            JHOVE2 framework
	 * @param source
	 *            Source unit
	 * @param input
	 *            Source input
	 * @return Set of presumptive format identifications
	 * @throws IOException
	 *             I/O exception encountered identifying the source unit
	 * @throws JHOVE2Exception
	 */
	public Set<FormatIdentification> identify(JHOVE2 jhove2, Source source, Input input)
		throws IOException, JHOVE2Exception;

	/**
	 * Get file source identifier module.
	 * @return File source identifier module
	 * @throws JHOVE2Exception
	 */
	public SourceIdentifier getSourceIdentifier() throws JHOVE2Exception;
	/**
	 * Set file source identifier module
	 * @param sourceIdentifier
	 * @return SourceIdentifier attached to Identifer
	 * @throws JHOVE2Exception
	 */
	public SourceIdentifier setSourceIdentifier(SourceIdentifier fileSourceIdentifier) throws JHOVE2Exception;
	
	/**
	 * Get pre-identified Sources flag
	 * @return boolean pre-identified Sources flag
	 */
	 @ReportableProperty(order = 2, value = "Skip pre-identified Sources flag")
	public boolean isShouldSkipIdentifyIfPreIdentified();
	/**
	 * Set pre-identified Sources flag
	 * @param boolean pre-identified Sources flag
	 */
	public void setShouldSkipIdentifyIfPreIdentified(boolean shouldSkipIdentifyIfPreIdentified);

}
