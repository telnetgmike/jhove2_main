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
package org.jhove2.persist.berkeleydpl.proxies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;

/**
 * Proxy for java.util.LinkedHashSet
 * This proxy will be needed because the Spring IOC container creates a LinkedHashSet for any <Set> constructor
 * or property args
 * @author smorrissey
 *
 */
@SuppressWarnings("unchecked")
@Persistent(proxyFor=LinkedHashSet.class)
public class LinkedHashSetPersistentProxy implements PersistentProxy<LinkedHashSet> {
	
	protected ArrayList setList = null;
	
	/**
	 * 
	 */
	public LinkedHashSetPersistentProxy() {}

	@Override
	public LinkedHashSet convertProxy() {
		LinkedHashSet linkedHashSet = null;
		if (setList != null){
			linkedHashSet = new LinkedHashSet(setList.size());
			for (int i=0; i<setList.size(); i++){
				linkedHashSet.add(setList.get(i));
			}
		}
		return linkedHashSet;
	}

	@Override
	public void initializeProxy(LinkedHashSet linkedHashSet) {
		if (linkedHashSet != null){
			setList = new ArrayList();
			Iterator iterator = linkedHashSet.iterator();
			while (iterator.hasNext()){
				setList.add(iterator.next());
			}
		}
	}

}
