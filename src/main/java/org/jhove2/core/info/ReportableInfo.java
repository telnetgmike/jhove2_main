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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jhove2.annotation.ReportableProperty;
import org.jhove2.core.I8R;
import org.jhove2.core.Message;
import org.jhove2.core.Reportable;
import org.jhove2.core.info.ReportableSourceInfo.Source;

/** JHOVE2 introspection utility for retrieving the properties of
 * {@link org.jhove2.core.Reportable}s.
 * 
 * @author mstrong, slabrams
 */
public class ReportableInfo {
	/** Reportable identifier. */
	protected I8R identifier;
	
	/** Reportable simple name. */
	protected String name;
	
	/** Reportable properties defined for the reportable. */
	protected List<ReportableSourceInfo> properties;
	
	/** Reportable package. */
	protected String packageName;
	
	/** Reportable qualified name. */
	protected String qName;
	
	/** Instantiate a new <code>ReportableInfo</code> utility.
	 * @param reportable Reportable
	 */
	public ReportableInfo(Reportable reportable)
	{
		this(reportable.getClass());
	}

	/** Instantiate a new <code>ReportableInfo</code> utility.
	 * @param cl   Reportable class
	 */
	public ReportableInfo(Class<? extends Reportable> cl) {
		this.name  = cl.getSimpleName();
		this.qName = cl.getName();
		int in = this.qName.lastIndexOf('.');
		if (in > -1) {
			this.packageName = this.qName.substring(0, in);
		}
		else {
			this.packageName = this.qName;
		}
		this.identifier = new I8R(I8R.JHOVE2_PREFIX + "/" +
				                  I8R.JHOVE2_REPORTABLE_INFIX + "/" +
				                  this.qName.replace('.', '/'));
		this.properties = new ArrayList<ReportableSourceInfo>();
		Map<String,String> idMap = new HashMap<String,String>();
		ReportablePropertyComparator comparator =
			new ReportablePropertyComparator();
		do {
			/* Introspect on the class's methods (and all of its superclass
			 * methods) to retrieve reportable properties.
		 	 */
			Set<ReportablePropertyInfo> set = new TreeSet<ReportablePropertyInfo>(comparator);
			Method [] methods = cl.getDeclaredMethods();
			for (int j=0; j<methods.length; j++) {
				ReportableProperty annot = methods[j].getAnnotation(ReportableProperty.class);
				if (annot != null) {
					String name = methods[j].getName();
					in = name.indexOf("get");
					if (in == 0) {
						name = name.substring(3);
					}
					String id = I8R.JHOVE2_PREFIX + "/";
					Type type = methods[j].getGenericReturnType();
					if (isMessage(type)) {
						id += I8R.JHOVE2_MESSAGE_INFIX;
					}
					else {
						id += I8R.JHOVE2_PROPERTY_INFIX;
					}
					id += "/" + cl.getName().replace('.', '/') + "/" + name;
					if (idMap.get(id) == null) {
						idMap.put(id, id);
						ReportablePropertyInfo prop =
							new ReportablePropertyInfo(new I8R(id), methods[j],
									                   annot.value(),
									                   annot.ref());
						set.add(prop);
					}
				}
			}
			if (set.size() > 0) {
				ReportableSourceInfo source =
					new ReportableSourceInfo(cl.getSimpleName(), Source.Class,
							                 set);
				this.properties.add(source);
			}
		
			/* Introspect on the class's interface's methods (and all its
			 * superinterfaces) to retrieve reportable properties.
			 */
			checkInterfaces(cl.getInterfaces(), idMap, comparator);
		} while ((cl = (Class<? extends Reportable>) cl.getSuperclass()) != null);
	}
	
	/** Introspect on interfaces (and superinterfaces) to retrieve reportable
	 * properties.
	 * @param ifs        Interfaces to examine
	 * @param idMap      Map of properties identifiers already retrieved
	 * @param comparator Reportable property comparator
	 */
	protected void checkInterfaces(Class<?> [] ifs, Map<String,String> idMap,
				                   ReportablePropertyComparator comparator) {
		for (int i=0; i<ifs.length; i++) {
			Set<ReportablePropertyInfo> set = new TreeSet<ReportablePropertyInfo>(comparator);
			Method [] methods = ifs[i].getDeclaredMethods();
			for (int j=0; j<methods.length; j++) {
				ReportableProperty annot = methods[j].getAnnotation(ReportableProperty.class);
				if (annot != null) {
					String name = methods[j].getName();
					int in = name.indexOf("get");
					if (in == 0) {
						name = name.substring(3);
					}
					String id = I8R.JHOVE2_PREFIX + "/";
					Type type = methods[j].getGenericReturnType();
					if (isMessage(type)) {
						id += I8R.JHOVE2_MESSAGE_INFIX;
					}
					else {
						id += I8R.JHOVE2_PROPERTY_INFIX;
					}
					id += "/" + ifs[i].getName().replace('.', '/') + "/" + name;
					if (idMap.get(id)== null) {
						idMap.put(id, id);
						ReportablePropertyInfo prop =
							new ReportablePropertyInfo(new I8R(id), methods[j],
									                   annot.value(),
									                   annot.ref());
						set.add(prop);
					}
				}
			}
			if (set.size() > 0) {
				ReportableSourceInfo source =
					new ReportableSourceInfo(ifs[i].getSimpleName(),
							                 Source.Interface, set);
				this.properties.add(source);
			}
			checkInterfaces(ifs[i].getInterfaces(), idMap, comparator);
		}
	}
	
	/** Get {@link org.jhove2.core.Reportable} formal identifier in
	 * the JHOVE2 namespace.  This identifier is guaranteed to be unique.
	 * @return Reportable identifier in the JHOVE2 namespace
	 */
	public I8R getIdentifier() {
		return this.identifier;
	}
	
	/** Get {@link org.jhove2.core.Reportable} simple name.
	 * @return Simple name of the reportable
	 */
	public String getName() {
		return this.name;
	}
	
	/** Get {@link org.jhove2.core.Reportable} package name.
	 * @return Package name of the reportable
	 */
	public String getPackage() {
		return this.packageName;
	}
	
	/** Get reportable properties of the {@link org.jhove2.core.Reportable}.
	 * @return Reportable properties of the reportable
	 */
	public List<ReportableSourceInfo> getProperties() {
		return this.properties;
	}
	
	/** Get {@link org.jhove2.core.Reportable} qualified name.
	 * @return Qualified name of the reporter
	 */
	public String getQName() {
		return this.qName;
	}
	
	/** Get property message status.
	 * @param type Property type
	 * @return True if the property is a message; otherwise, false
	 */
	protected static synchronized boolean isMessage(Type type) {
		String name = type.toString();
		int in = name.lastIndexOf('<');
		if (in > 0) {
			name = name.substring(in+1, name.indexOf('>'));
		}
		in = name.lastIndexOf('.');
		if (in > 0) {
			name = name.substring(in+1);
		}
		
		return name.equals(Message.class.getSimpleName()) ? true : false;
	}
}
