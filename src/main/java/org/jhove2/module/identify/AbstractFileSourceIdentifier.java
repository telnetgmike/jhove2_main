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

import static com.sleepycat.persist.model.DeleteAction.NULLIFY;
import static com.sleepycat.persist.model.Relationship.ONE_TO_ONE;


import org.jhove2.core.JHOVE2Exception;
import org.jhove2.module.AbstractModule;
import org.jhove2.persist.ModuleAccessor;
import org.jhove2.persist.SourceIdentifierAccessor;

import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.SecondaryKey;

/**
 * @author smorrissey
 *
 */
@Persistent
public abstract class AbstractFileSourceIdentifier extends AbstractModule
		implements SourceIdentifier {

	/** foreign key linking FileSourceIdentifer to parent Identifer module */
	@SecondaryKey(relate=ONE_TO_ONE, relatedEntity=AbstractModule.class,
			onRelatedEntityDelete=NULLIFY)
	protected Long parentIdentifierId;
	
	/**
     * Instantiate a new <code>AbstractFileSourceIdentifier</code>.
	 * @param version
	 *            Application version identifier in three-part form: "M.N.P"
	 * @param release
	 *            Application release date in ISO 8601 format: "YYYY-MM-DD"
	 * @param rights
	 *            Application rights statement
	 * @param scope
	 *            Application scope: generic or specific
	 * @param moduleAccessor persistence access manager
	 */
	public AbstractFileSourceIdentifier(String version, String release,
			String rights, Scope scope, ModuleAccessor moduleAccessor) {
		super(version, release, rights, scope, moduleAccessor);
	}

	/**
	 * 
	 * @param moduleAccessor
	 */
	public AbstractFileSourceIdentifier(ModuleAccessor moduleAccessor){
		this(null, null, null, Scope.Generic, moduleAccessor);
	}
	/**
	 * 
	 */
	public AbstractFileSourceIdentifier() {
		this(null);
	}

	/* (non-Javadoc)
	 * @see org.jhove2.module.identify.SourceIdentifier#getParentIdentifierId()
	 */
	@Override
	public Long getParentIdentifierId() {
		return this.parentIdentifierId;
	}
	/* (non-Javadoc)
	 * @see org.jhove2.module.identify.SourceIdentifier#setParentIdentifierId(java.lang.Long)
	 */
	@Override
	public void setParentIdentifierId(Long id) throws JHOVE2Exception {
		Long oldId = this.parentIdentifierId;
		this.parentIdentifierId = id;
		this.getModuleAccessor().verifyNewParentModuleId(this, oldId, id);
	}
	
	@Override
	public Identifier getParentIdentifier() throws JHOVE2Exception {
		SourceIdentifierAccessor sa = null;
		try {
			ModuleAccessor ma = this.getModuleAccessor();
			sa = (SourceIdentifierAccessor)ma;
		}
		catch (Exception e){
			throw new JHOVE2Exception ("Failed to cast ModuleAccessor to SourceIdentifierAccessor", e);
		}
		return sa.getParentIdentifier(this);
	}

}
