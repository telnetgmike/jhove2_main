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

import java.util.Set;

import org.jhove2.core.JHOVE2;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.TimerInfo;
import org.jhove2.core.format.FormatIdentification;
import org.jhove2.core.source.Source;
import org.jhove2.module.AbstractCommand;

/**
 * {@link org.jhove2.module.Command} to perform identifier on
 * {@link org.jhove2.core.source.Source}s.
 * Note that this module does not detect
 * {@link org.jhove2.core.source.ClumpSource} format instances. 
 * 
 * @author smorrissey
 */
public class IdentifierCommand
	extends AbstractCommand
{
	/** IdentifierCommand module version identifier. */
	public static final String VERSION = "1.9.5";

	/** IdentifierCommand module release date. */
	public static final String RELEASE = "2010-02-16";

	/** IdentifierCommand module rights statement. */
	public static final String RIGHTS = "Copyright 2010 by The Regents of the University of California, "
		+ "Ithaka Harbors, Inc., and The Board of Trustees of the Leland "
		+ "Stanford Junior University. "
		+ "Available under the terms of the BSD license.";
	
	/** Instantiate a new <code>IdentifierCommand</code>.
	 */
	public IdentifierCommand(){
		super(VERSION, RELEASE, RIGHTS, Scope.Generic);
	}

	protected IdentifierFactory identifierFactory;
	/**
	 * Identify the presumptive formats of the source unit.
	 * @param jhove2 JHOVE2 framework object
	 * @param source Source to be identified
	 * @throws JHOVE2Exception
	 * @see org.jhove2.module.Command#execute(org.jhove2.core.JHOVE2, org.jhove2.core.source.Source)
	 * @see org.jhove2.module.identify.Identifier#identify(org.jhove2.core.JHOVE2, org.jhove2.core.source.Source)
	 */
	@Override
	public void execute(JHOVE2 jhove2, Source source)
		throws JHOVE2Exception
	{
		try {		
			Identifier identifier =
				this.getIdentifierFactory().getIdentifier();
			TimerInfo timer = identifier.getTimerInfo();
			timer.setStartTime();
			try {
	            /* Register all identifying modules. */
				source.addModule(identifier);
				source.addModule(identifier.getFileSourceIdentifier());
	            
	            /* Identify the format. */
				Set<FormatIdentification> formats = identifier.identify(jhove2,
					                                                    source);
				source.addPresumptiveFormats(formats);
			}
			finally {
				timer.setEndTime();
			}
		}
		catch (Exception e){
			throw new JHOVE2Exception("failed to execute identifier", e);
		}	
        return;
	}
	/**
	 * @return the identifierFactory
	 */
	public IdentifierFactory getIdentifierFactory() {
		return identifierFactory;
	}
	/**
	 * @param identifierFactory the identifierFactory to set
	 */
	public void setIdentifierFactory(IdentifierFactory identifierFactory) {
		this.identifierFactory = identifierFactory;
	}
}
