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

package org.jhove2.module.format;

import java.util.List;

import org.jhove2.annotation.ReportableProperty;
import org.jhove2.core.JHOVE2;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.format.Format;
import org.jhove2.core.io.Input;
import org.jhove2.core.source.Source;
import org.jhove2.module.Module;
import org.jhove2.module.format.FormatProfile;


/**
 * Interface for format {@link Module} that models a
 * format or format family.
 * 
 * @author mstrong, slabrams, smorrissey
 */
public interface FormatModule
	extends Module, Parser
{
    /**
     * Invoke the parsing of the {@link org.jhove2.core.source.Source} unit's
     * {@link org.jhove2.core.io.Input} and validate all registered profiles.
     * @param jhove2 JHOVE2 framework
     * @param source Source to be parsed
     * @param input  Source input
     * @throws JHOVE2Exception
     * @see org.jhove2.module.Command#execute(org.jhove2.core.JHOVE2, org.jhove2.core.source.Source, org.jhove2.core.io.Input)
     */
    public void invoke(JHOVE2 jhove2, Source source, Input input)
       throws JHOVE2Exception;
	
	/**
	 * Get format module format.
	 * 
	 * @return Format module format
	 */
	@ReportableProperty(order = 1, value = "Format module format.")
	public Format getFormat();

	/**
	 * Get format module format profiles.
	 * 
	 * @return Format module format profiles.
	 */
	@ReportableProperty(order = 2, value = "Format module format profiles.")
	public List<FormatProfile> getProfiles()
		throws JHOVE2Exception;

	/** Set module format.
	 * @param format Module format
	 */
	public void setFormat(Format format);
	
	/**
	 * Add a format module format profile.
	 * 
	 * @param profiles
	 *            Format module format profile
	 * @return FormatProfiles added to Module
	 */
	public List<FormatProfile> setProfiles(List<FormatProfile> profiles)
		throws JHOVE2Exception;

}
