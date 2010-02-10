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

package org.jhove2.module;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jhove2.core.I8R;

/** Abstract {@link org.jhove2.module.Command}.
 * 
 * @author slabrams
 */
public abstract class AbstractCommand
	extends AbstractModule
	implements Command
{
    /** Identifiers of modules associated with the command. */
    protected Set<String> identifiers;
    
	/** Modules associated with the command. */
	protected List<Module> modules;
	
	/** Instantiate a new <code>AbstractCommand</code>. 
	 * 	 * @param version
	 *            Module version identifier in three-part form: "M.N.P"
	 * @param release
	 *            Module release date in ISO 8601 format: "YYYY-MM-DD"
	 * @param rights
	 *            Module rights statement
	 * @param scope
	 *            Module scope: generic or specific (to a source unit)
	 */
	public AbstractCommand(String version, String release, String rights,
			               Scope scope) {
		super(version, release, rights, scope);
		
		this.identifiers = new HashSet<String>();
		this.modules     = new ArrayList<Module>();
	}

	/** Add module associated with the command.
	 * @param module Module associated with the command
	 * @see org.jhove2.module.Command#addModule(org.jhove2.module.Module)
	 */
	@Override
	public void addModule(Module module) {
	    String identifier = module.getReportableIdentifier().toString();
	    if (!this.identifiers.contains(identifier)) {
	        this.identifiers.add(identifier);
	        this.modules.add(module);
	    }
	}

	/** Get modules associated with the command.
	 * @return Modules associated with the command
	 * @see org.jhove2.module.Command#getModules()
	 */
	@Override
	public List<Module> getModules() {
		return this.modules;
	}
}
