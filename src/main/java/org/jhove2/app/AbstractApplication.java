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

import java.util.Date;

import org.jhove2.core.AppConfigInfo;
import org.jhove2.core.JHOVE2;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.config.Configure;
import org.jhove2.module.AbstractModule;
import org.jhove2.module.display.Displayer;

/**
 * Abstract JHOVE2 application .
 * 
 * @author mstrong, slabrams, smorrissey
 */
public abstract class AbstractApplication extends AbstractModule implements
		Application {

	/** Application command line. */
	protected String commandLine;

	/** Application invocation date/timestamp. */
	protected Date dateTime;

	/** Application configuration settings  */
	protected AppConfigInfo appConfigInfo;
	
	/** Application framework. */
	protected JHOVE2 framework;
	
	/** Application displayer */
	protected Displayer displayer;

	/**
	 * Instantiate a new <code>AbstractApplication</code>.
	 * 
	 * @param version
	 *            Application version identifier in three-part form: "M.N.P"
	 * @param release
	 *            Application release date in ISO 8601 format: "YYYY-MM-DD"
	 * @param rights
	 *            Application rights statement
	 * @throws JHOVE2Exception 
	 */
	public AbstractApplication(String version, String release, String rights) throws JHOVE2Exception {
		super(version, release, rights);
		// uses application defaults
		this.setAppConfigInfo(new AppConfigInfo()); 
		try {
			this.displayer = Configure.getReportable(Displayer.class,
					Displayer.DEFAULT_DISPLAYER_TYPE);
		}
		catch (JHOVE2Exception e){
			try {
				this.displayer = (Displayer)(Class.forName(Displayer.DEFAULT_DISPLAYER_CLASS).newInstance());
			}
			catch (Exception ex){
				throw new JHOVE2Exception("Fail to instantiate default displayer", ex);
			}
		}
	}

	/**
	 * Get application command line.
	 * 
	 * @return Application command line
	 * @see org.jhove2.app.Application#getCommandLine()
	 */
	@Override
	public String getCommandLine() {
		return this.commandLine;
	}

	/**
	 * Get application invocation date/timestamp.
	 * 
	 * @return Application invocation date/timestamp
	 * @see org.jhove2.app.Application#getDateTime()
	 */
	@Override
	public Date getDateTime() {
		return this.dateTime;
	}
	
	/**
	 * Get application framework.
	 * 
	 * @return Application framework
	 * @see org.jhove2.app.Application#getFramework()
	 */
	@Override
	public JHOVE2 getFramework() {
		return this.framework;
	}

	/**
	 * Set application framework.
	 * 
	 * @param framework
	 *            Application framework
	 * @see org.jhove2.app.Application#setFramework(org.jhove2.core.JHOVE2)
	 */
	@Override
	public void setFramework(JHOVE2 framework) {
		this.framework = framework;
	}

	public AppConfigInfo getAppConfigInfo() {
		return appConfigInfo;
	}

	public void setAppConfigInfo(AppConfigInfo appConfigInfo) {
		this.appConfigInfo = appConfigInfo;
	}

	public void setCommandLine(String commandLine) {
		this.commandLine = commandLine;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	@Override
	public Displayer getDisplayer() {
		return displayer;
	}

	@Override
	public void setDisplayer(Displayer displayer) {
		this.displayer = displayer;
	}
}
