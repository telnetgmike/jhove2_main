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

package org.jhove2.core;

import java.io.IOException;
import java.util.List;

import org.jhove2.annotation.ReportableProperty;
import org.jhove2.core.source.Source;
import org.jhove2.module.AbstractModule;

/**
 * The JHOVE2 core processing framework.
 * 
 * @author mstrong, slabrams, smorrissey
 */
public class JHOVE2  
extends AbstractModule
{
	/** Framework version identifier. */
	public static final String VERSION = "0.5.4";

	/** Framework release date. */
	public static final String RELEASE = "2009-09-09";

	/** Framework rights statement. */
	public static final String RIGHTS = "Copyright 2009 by The Regents of the University of California, "
		+ "Ithaka Harbors, Inc., and The Board of Trustees of the Leland "
		+ "Stanford Junior University. "
		+ "Available under the terms of the BSD license.";

	/** Framework installation properties. */
	protected Installation installation;

	/** Counter to track number and type of sources processed by framework */
	protected SourceCounter sourceCounter;

	/** List of commands to be executed in sequence in order to characterize Source */
	protected List<JHOVE2Command> commands;

	/** Configuration settings for framework.  If not configured, default values will be used */
	protected AppConfigInfo appConfigInfo;
	
	/**
	 * Instantiate a new <code>JHOVE2</code> core framework with a default
	 * configuration.
	 * 
	 * @throws JHOVE2Exception
	 */
	public JHOVE2() throws JHOVE2Exception {
		this(new AppConfigInfo());
	}
	
	/**
	 * Instantiate a new <code>JHOVE2</code> core framework with a specific
	 * configuration.
	 * @param appConfigInfo Configuration settings for this instance of the
	 *                      JHOVE2 framework
	 */
	public JHOVE2(AppConfigInfo appConfigInfo) {
		super(VERSION, RELEASE, RIGHTS);
		
		this.appConfigInfo = appConfigInfo;
		this.installation  = Installation.getInstance();
		this.sourceCounter = new SourceCounter();
	}

	/**
	 * Characterize a source unit.
	 * This method will be used as a call-back by any format module that must
	 * recursively characterize components of a format instance.
	 * 
	 * @param source
	 *            Source unit
	 * @throws JHOVE2Exception
	 * @throws IOException
	 */
	public void characterize(Source source)
		throws IOException, JHOVE2Exception
	{
		TimerInfo timer = source.getTimerInfo();
		timer.setStartTime();
		
		/* Update summary counts of source units, by type. */
		this.sourceCounter.incrementSourceCounter(source);				
		
		/* Characterize the source unit. */
		source.setDeleteTempFiles(this.getAppConfigInfo().getDeleteTempFiles());
		try {
			for (JHOVE2Command command : this.commands){
				command.execute(this, source);
			}
		} finally {
			source.close();
			timer.setEndTime();
		}
	}

	/**
	 * Determine if the fail fast limit has been exceeded.
	 * 
	 * @param numErrors
	 *            Number of errors
	 * @return True if the fail fast limit has been exceeded
	 */
	public boolean failFast(int numErrors) {
		if (this.appConfigInfo.getFailFastLimit() > 0 && 
				numErrors > this.appConfigInfo.getFailFastLimit()) {
			return true;
		}
		return false;
	}

	/**
	 * Get framework installation properties.
	 * 
	 * @return Framework installation properties.
	 */
	@ReportableProperty(order = 21, value = "Framework installation properties.")
	public Installation getInstallation() {
		return this.installation;
	}

	/**
	 * Get framework memory usage. This is calculated naively as the Java
	 * {@link java.lang.Runtime}'s total memory minus free memory at the time of
	 * method abstractApplication.
	 * 
	 * @return Memory usage, in bytes
	 */
	@ReportableProperty(order = 51, value = "Framework memory usage, in bytes.")
	public long getMemoryUsage() {
		Runtime rt = Runtime.getRuntime();
		long use = rt.totalMemory() - rt.freeMemory();

		return use;
	}

	/**
	 * Accessor for counter to track number and type of source units processed
	 * by the JHOVE2 framework.
	 * @return Source unit counter
	 */
	@ReportableProperty(order = 52, value = "Counters of source units processed, " +
		"by type.")
	public SourceCounter getSourceCounter() {
		return sourceCounter;
	}

	/**
	 * Mutator for counter to track number and type of sources processed by
	 * the JHOVE2 framework.
	 * @param sourceCounter Source unit counter
	 */
	public void setSourceCounter(SourceCounter sourceCounter) {
		this.sourceCounter = sourceCounter;
	}

	/**
	 * Get object which maintains configuration information for the running of
	 * this module.
	 * @return AppConfigInfo with configuration information for this module
	 */
	@ReportableProperty(order = 1, value = "Configuration info for this module.")
	public AppConfigInfo getAppConfigInfo() {
		return appConfigInfo;
	}

	/**
	 * Mutator for object which maintains configuration information for the running of this module
	 * @param appConfigInfo
	 */
	public void setAppConfigInfo(AppConfigInfo appConfigInfo) {
		this.appConfigInfo = appConfigInfo;
	}

	/**
	 * Mutator for object which records environment in which engine is run.
	 * @param installation
	 */
	public void setInstallation(Installation installation) {
		this.installation = installation;
	}
	
	/**
	 * Accessor for list of commands to be executed in sequence to characterize
	 * a source unit.
	 * @return List of command to be executed
	 */
	@ReportableProperty
	public List<JHOVE2Command> getCommands() {
		return commands;
	}
	
	/**
	 * Mutator for list of commands to be executed in sequence to characterize
	 * a source unit.
	 * @param commands
	 */
	public void setCommands(List<JHOVE2Command> commands) {
		this.commands = commands;
	}
}
