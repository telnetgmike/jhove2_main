/**
 * JHOVE2 - Next-generation architecture for format-aware characterization
 * 
 * Copyright (c) 2009 by The Regents of the University of California, Ithaka
 * Harbors, Inc., and The Board of Trustees of the Leland Stanford Junior
 * University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * o Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * o Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * o Neither the name of the University of California/California Digital
 * Library, Ithaka Harbors/Portico, or Stanford University, nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
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
package org.jhove2.config.spring;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jhove2.core.I8R;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.format.Format;
import org.jhove2.module.Module;
import org.jhove2.module.format.BaseFormatModule;
import org.jhove2.module.format.FormatModuleFactory;
import org.jhove2.module.format.FormatProfile;

/**
 * Spring-based implementation of Factory class for {@link org.jhove2.module.format.FormatModule} 
 * objects
 * 
 * @author smorrissey, rnanders
 *
 */
public class SpringFormatModuleFactory implements FormatModuleFactory {

	/**
	 * Dispatch map. Maps from unique identifiers to Spring bean names for the
	 * modules associated with the formats.
	 */
	static ConcurrentMap<String, String> dispatchMap;

	/* (non-Javadoc)
	 * @see org.jhove2.module.format.FormatModuleFactory#makeFormatModule(java.lang.String)
	 */
	@Override
	public Module makeFormatModule(I8R identifier) throws JHOVE2Exception {
		return getModuleFromIdentifier(identifier);
	}

	/**
	 * Gets the mapping from format to format module. Initializes the static map
	 * on first invocation.
	 * 
	 * @return map from JHOVE2 format identifier to module bean name
	 * 
	 * @throws JHOVE2Exception
	 */
	public static ConcurrentMap<String, String> getDispatchMap()
	        throws JHOVE2Exception {
	    if (dispatchMap == null) {
	        dispatchMap = new ConcurrentHashMap<String, String>();
	        /*
	         * Use Spring to get instances of all objects inheriting from
	         * BaseFormatModule
	         */
	        Map<String, Object> map = SpringConfigInfo
	                .getObjectsForType(BaseFormatModule.class);
	        /* For each of the format modules */
	        for (Entry<String, Object> entry : map.entrySet()) {
	            /* Get the Spring bean name for the format module */
	            String moduleBeanName = entry.getKey();
	            /* Get the JHOVE format identifier that the module references */
	            BaseFormatModule module = (BaseFormatModule) entry.getValue();
	            Format format = module.getFormat();
	            I8R formatID = format.getIdentifier();
	            /* Add an entry into the format identifier to module map */
	            dispatchMap.put(formatID.getValue(), moduleBeanName);
	            /*
	             * Now get the format profiles that the module references and
	             * add them to the map
	             */
	            for (FormatProfile profile : module.getProfiles()) {
	                I8R profileID = profile.getFormat().getIdentifier();
	                dispatchMap.put(profileID.getValue(), moduleBeanName);
	                // System.out.println(profileID.getValue() + " = " + moduleBeanName);
	            }
	            // System.out.println(formatID.getValue() + " = " + moduleBeanName);
	        }
	    }
	    return dispatchMap;
	}

	@Override
    public Module getModuleFromIdentifier(I8R identifier)
            throws JHOVE2Exception {
        Module module = null;
        String name = SpringFormatModuleFactory.getDispatchMap().get(identifier.getValue());
        if (name != null) {
            module = SpringConfigInfo.getReportable(Module.class, name);
        }
        return module;
    }

}
