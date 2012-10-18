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
package org.jhove2.persist.berkeleydpl;

import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.app.Application;
import org.jhove2.module.Module;
import org.jhove2.module.display.Displayer;
import org.jhove2.persist.ApplicationModuleAccessor;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.model.Persistent;

/**
 * @author smorrissey
 *
 */
@Persistent
public class BerkeleyDbApplicationModuleAccessor 	
	extends BerkeleyDbBaseModuleAccessor
	implements ApplicationModuleAccessor {

	public BerkeleyDbApplicationModuleAccessor(){
		super();
	}
	/* (non-Javadoc)
	 * @see org.jhove2.persist.ApplicationModuleAccessor#getDisplayer(org.jhove2.core.app.Application)
	 */
	@Override
	public Displayer getDisplayer(Application module) throws JHOVE2Exception {
		Displayer displayer = null;
		if (module != null){
			try{
				if (module.getModuleId()== null){ // new module, never persisted
					this.persistModule(module);
				}
				Module rModule = 
					this.getBerkeleyDbPersistenceManager().getDisplayerByParentApp().get(module.getModuleId());
				if (rModule != null){
					displayer = (Displayer)rModule;
				}
			}
			catch(DatabaseException e){
				throw new JHOVE2Exception("Unable to retrieve displayer",e);
			}
		}
		return displayer;
	}

	/* (non-Javadoc)
	 * @see org.jhove2.persist.ApplicationModuleAccessor#setDisplayer(org.jhove2.core.app.Application, org.jhove2.module.display.Displayer)
	 */
	@Override
	public Displayer setDisplayer(Application module, Displayer displayer)
			throws JHOVE2Exception {
		if (module != null){			
			if (module.getModuleId()== null){ // new module, never persisted
				this.persistModule(module);
			}
			// Un-link old displayer
			Displayer oldDisplayer = this.getDisplayer(module);
			if (oldDisplayer != null){
				oldDisplayer.setParentAppId(null);
			}
			// link the new one
			if (displayer != null){
				displayer.setParentAppId(module.getModuleId());
			}			
		}
		return displayer;
	}


}
