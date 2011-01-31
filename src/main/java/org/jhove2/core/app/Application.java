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

package org.jhove2.core.app;

import java.util.Date;

import org.jhove2.annotation.ReportableProperty;
import org.jhove2.core.Installation;
import org.jhove2.core.Invocation;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.module.Module;
import org.jhove2.module.display.Displayer;

/**
 * Interface for command line applications based on the JHOVE2 framework.
 * 
 * @author mstrong, slabrams
 */
public interface Application
	extends Module
{
	/**
	 * Get application command line.
	 * 
	 * @return Application command line
	 */
	@ReportableProperty(order = 2, value = "Application command line.")
	public String getCommandLine();

	/**
	 * Get application invocation date/timestamp.
	 * 
	 * @return Application invocation date/timestamp
	 */
	@ReportableProperty(order = 1, value = "Application invocation date/timestamp.")
	public Date getDateTime();

	
	/** Get application {@link org.jhove2.module.display.Displayer} properties.
	 * @return Application displayer
	 * @throws JHOVE2Exception 
	 */
	public Displayer getDisplayer() throws JHOVE2Exception;
	

	/** Get application {@link org.jhove2.core.Invocation} properties.
	 * @return Application invocation properties
	 */
	public Invocation getInvocation();

	/** Get application {@link org.jhove2.core.Installation} properties.
	 * @return Application installation properties
	 */
	public Installation getInstallation();

	
	/**
	 * Set application displayer
	 * @param displayer Application displayer
	 * @return Displayer assigned to Application
	 * @throws JHOVE2Exception 
	 */
	public Displayer setDisplayer(Displayer displayer) throws JHOVE2Exception;
	
	

}
