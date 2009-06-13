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

package org.jhove2.core.spring;

import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.Reportable;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/** Spring configuration utility.
 * 
 * @author mstrong, slabrams
 */
public class Configure {
	/** Spring configuration classpath. */
	public static final String CLASSPATH = "classpath*:**/*-config.xml";
	
	/** Spring application context. */
	protected static ApplicationContext context;
	
	/** Get reportable by bean name.
	 * @param reportable Reportable class
	 * @param name       Reportable bean name
	 * @throws JHOVE2Exception
	 */
	public static synchronized Reportable getReportable(String name)
		throws JHOVE2Exception
	{
		Reportable reportable = null;
		try {
			if (context == null) {
				context = new ClassPathXmlApplicationContext(CLASSPATH);
			}
		
			reportable = (Reportable) context.getBean(name);
		} catch (BeansException e) {
			throw new JHOVE2Exception("Can't instantiate reportable: " +
					                         name, e);
		}
		
		return reportable;
	}
	
	/** Get reportable names by type.
	 * @param type Reportable type
	 * @throws JHOVE2Exception 
	 */
	public static synchronized String [] getReportableNames(Class<?> reportable)
		throws JHOVE2Exception
	{
		String [] names = null;
		try {
			if (context == null) {
				context = new ClassPathXmlApplicationContext(CLASSPATH);
			}
		
			names = context.getBeanNamesForType(reportable);
		} catch (BeansException e) {
			throw new JHOVE2Exception("Can't retrieve instantiation names for reportable: " +
					                         reportable.getName(), e);
		}
		
		return names;
	}
}
