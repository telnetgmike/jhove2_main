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

package org.jhove2.core.info;

import java.util.Set;

/** Information about the source of JHOVE2 reportable properties, which is
 * either an interface or superclass in which the properties are defined.
 * 
 * @author mstrong, slabrams
 */
public class ReportableSourceInfo {
	/** Source types for reportable properties. */
	public enum Source {
		Class,
		Interface
	}
	
	/** Source name. */
	protected String name;
	
	/** Reportable properties. */
	protected Set<ReportablePropertyInfo> props;
	
	/** Source type of the reportable properties. */
	protected Source source;
	
	/** Instantiate a new <code>ReportableSourceInfo</code>.
	 * @param name   Source name
	 * @param source Source type
	 * @param props  Reportable properties defined by source
	 */
	public ReportableSourceInfo(String name, Source source,
			                    Set<ReportablePropertyInfo> props) {
		this.name   = name;
		this.source = source;
		this.props  = props;
	}
	
	/** Get source name.
	 * @return Source name
	 */
	public String getName() {
		return this.name;
	}
	
	/** Get source reportable properties.
	 * @return Source reportable properties
	 */
	public Set<ReportablePropertyInfo> getProperties() {
		return this.props;
	}
	
	/** Get source type.
	 * @return Source type
	 */
	public Source getSource() {
		return this.source;
	}
}
