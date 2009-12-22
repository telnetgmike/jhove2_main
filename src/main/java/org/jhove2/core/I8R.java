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

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.jhove2.core.reportable.Reportable;

/**
 * A JHOVE2 identifier. Note that this class is named "I8R", not
 * "IdentifierModule" to avoid confusion between "identifier" as a label and
 * "identifier" as a process that determines a format.
 * 
 * @author mstrong, slabrams, smorrissey
 */
public class I8R implements Comparable<I8R> {
	/** JHOVE2 namespace identifier prefix. */
	public static final String JHOVE2_PREFIX = "http://jhove2.org/terms";

	/** JHOVE2 namespace identifier format infix. */
	public static final String JHOVE2_FORMAT_INFIX = "format";

	/**
	 * JHOVE2 namespace identifier {@link org.jhove2.core.Message} infix.
	 */
	public static final String JHOVE2_MESSAGE_INFIX = "message";

	/**
	 * JHOVE2 reportable property identifier infix
	 * {@link org.jhove2.annotation.ReportableProperty} infix.
	 */
	public static final String JHOVE2_PROPERTY_INFIX = "property";

	/**
	 * JHOVE2 reportable identifier infix {@link org.jhove2.core.reportable.Reportable}
	 * infix.
	 */
	public static final String JHOVE2_REPORTABLE_INFIX = "reportable";


	protected static String messageClassName = null;

	/** IdentifierModule types, or namespaces. */
	public enum Namespace {
		AFNOR, /* AFNOR standard */
		AIIM, /* AIIM standard */
		ANSI, /* ANSI standard */
		ARK, /* ARK identifier */
		BCP, /* IETF Best Community Practice */
		BSI, /* BSI standard */
		CallNumber, /* Call number */
		CCITT, /* CCITT standard */
		Charset, /* IANA charset */
		DDC, /* Dewey Decimal Classification */
		DOI, /* Digital Object IdentifierModule */
		ECMA, /* ECMA standard */
		FDD, /* Library of Congress FDD identifier */
		FIPS, /* FIPS standard */
		FourCC, /* 4CC Standard */
		GUID, /* Globally Unique IdentifierModule */
		Handle, /* Handle */
		I3A, /* I3A standard */
		IEC, /* IEC standard */
		ISBN, /* International Standard Book Number */
		ISO, /* ISO standard */
		ISSN, /* International Standard Serial Nummber */
		ITU, /* ITU standard */
		JEITA, /* JEITA standard */
		JHOVE2, /* JHOVE2 identifier */
		LCC, /* Library of Congress Classification */
		LCCN, /* Library of Congress Control Number */
		MIME, /* MIME media type */
		NISO, /* NISO standard */
		OCLC, /* OCLC number */
		PII, /* Publisher Item IdentifierModule */
		PUID, /* PRONOM Unique IdentifierModule */
		PURL, /* Persistent URL */
		RFC, /* IETF Request for Comments */
		Shelfmark, /* Shelfmark */
		SICI, /* Serial Item and Contribution IdentifierModule */
		SMPTE, /* SMPTE standard */
		SN, /* Serial number */
		STD, /* IETF standard */
		TOM, /* TOM identifier */
		UUID, /* Universally Unique IdentifierModule */
		URI, /* W3C Uniform Resource IdentifierModule */
		URL, /* W3C Uniform Resource Locator */
		URN, /* W3C Uniform Resource Name */
		UTI, /* Apple Uniform Type IdentifierModule */
		Other
	}
	/** Class object for a JHOVE2 Message */
	protected static Class<?> messageClass = null;
	
	/** IdentifierModule namespace. */
	protected Namespace namespace;

	/** IdentifierModule value. */
	protected String value;

	/**
	 * Instantiate a <code>I8R</code> identifier in the JHOVE2 namespace.
	 * 
	 * @param value
	 *            IdentifierModule value
	 */
	public I8R(String value) {
		this(value, Namespace.JHOVE2);
	}

	/**
	 * Instantiate a new <code>I8R</code>.
	 * 
	 * @param value
	 *            IdentifierModule value
	 * @param namespace
	 *            IdentifierModule namespace
	 */
	public I8R(String value, Namespace namespace) {
		this.value = value;
		this.namespace = namespace;
	}

	/**
	 * Get the identifier namespace.
	 * 
	 * @return IdentifierModule namespace
	 */
	public Namespace getNamespace() {
		return this.namespace;
	}

	/**
	 * Get the identifier value.
	 * 
	 * @return IdentifierModule value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Get a String representation of the identifier, in the form
	 * "namespace:identifier".
	 * 
	 * @return String representation of the identifier
	 */
	@Override
	public String toString() {
		return "[" + this.namespace.toString() + "] " + value;
	}

	/**
	 * Lexically compare identifier.
	 * 
	 * @param identifier
	 *            IdentifierModule to be compared
	 * @return -1, 0, or 1 if this identifier value is less than, equal to, or
	 *         greater than the second
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(I8R identifier) {
		if (identifier==null){
			return 1;
		}
		if (this==identifier){
			return 0;
		}
		int ret = this.namespace.toString().compareToIgnoreCase(
				identifier.getNamespace().toString());
		if (ret > 0){
			return 1;
		}
		else if (ret < 0){
			return -1;
		}
		ret = this.value.compareToIgnoreCase(identifier.getValue());
		if (ret > 0){
			return 1;
		}
		else if (ret < 0){
			return -1;
		}
		else return ret;
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj == null){
			return false;
		}
		if (this == obj){
			return true;
		}
		if (!(obj instanceof I8R)){
			return false;
		}
		I8R id = (I8R)obj;
		int ret = this.namespace.toString().compareToIgnoreCase(
				id.getNamespace().toString());
		if (ret != 0){
			return false;
		}
		ret = this.value.compareToIgnoreCase(id.getValue());
		boolean equals = (ret==0);
		return equals;
	}
	
	/**
	 * Get the singular form of a plural property name.
	 * 
	 * @param name
	 *            Property name
	 * @return Singular form of a property name
	 */
	public static String singularName(String name) {
		String singular = null;
		int len = name.length();
		if (name.substring(len - 3).equals("ies")) {
			singular = name.substring(0, len - 3) + "y";
		} else {
			singular = name.substring(0, len - 1);
		}
	
		return singular;
	}

	/**
	 * Get the singular form of a plural property identifier.
	 * 
	 * @param identifier
	 *            Property identifier
	 * @return Singular form of a property identifier
	 */
	public static I8R singularIdentifier(I8R identifier) {
		I8R singular = null;
		String value = identifier.getValue();
		int in = value.lastIndexOf('/') + 1;
		int len = value.length();
		if (value.substring(len - 3).equals("ies")) {
			singular = new I8R(value + "/" + value.substring(in, len - 3) + "y");
		} else {
			singular = new I8R(value + "/" + value.substring(in, len - 1));
		}
	
		return singular;
	}

	/**
	 * Creates a JHOVE2 namespace identifier for a Reportable object
	 * @param r Reportable object for which we want JHOVE2 namespace Identifier
	 * @return I8R object containing JHOVE2 namespace identifier for a Reportable object
	 */
	public static I8R makeReportableTypeI8R (Reportable r){
		Class<? extends Reportable> c1 = r.getClass();
		String qName = c1.getName();
		I8R identifier = new I8R(I8R.JHOVE2_PREFIX + "/"
				+ I8R.JHOVE2_REPORTABLE_INFIX + "/"
				+ qName.replace('.', '/'));
		return identifier;
	}
	
	/**
	 *  A reportable instance has an I8R field, but that I8R
	 *	 is for the instance as a whole
	 *	 for each (reportable) field in the instance,
	 *	 we have to construct an I8R
	 * @param method accessor method for the reportable field
	 * @param reportableClass Class instance for the reportable
	 * @return I8R for the reportable field in a reportable object
	 */
	public static I8R makeFeatureI8RFromMethod(Method method, 
			Class <? extends Reportable> reportableClass){
		
		I8R featureI8R = null;			
		String name = method.getName();
		int i = name.indexOf("get");
		if (i == 0) {
			name = name.substring(3);
		}		
		String featureId = I8R.JHOVE2_PREFIX + "/";
		Type type = method.getGenericReturnType();
		boolean isMessage = false;
		try {
			isMessage = isMessage(type);
		}
		catch(ClassNotFoundException e){}
		if (isMessage) {
			featureId += I8R.JHOVE2_MESSAGE_INFIX;
		} else {
			featureId += I8R.JHOVE2_PROPERTY_INFIX;
		}
		featureId += "/" + reportableClass.getName().
			replace('.', '/') + "/" + name;		
		featureI8R = new I8R(featureId);		
		return featureI8R;		
	}
	
	/**
	 * Determine if Type is a JHOVE2 Message
	 * 
	 * @param type
	 *            Property type
	 * @return True if the Type is a message; otherwise, false
	 * @throws ClassNotFoundException 
	 */
	public static synchronized boolean isMessage(Type type) 
	throws ClassNotFoundException {
		boolean isMessage = false;
		String tname = type.toString();
		if (tname != null){
			int i = tname.lastIndexOf("<");
			if (i>0){
				tname = tname.substring(i+1);
				int j = tname.indexOf(">");
				tname = tname.substring(0, j);
			}
			String messageCn = getMessageClassName();
			isMessage = tname.endsWith(messageCn);
		}
		return isMessage;
	}


	protected static String getMessageClassName() throws ClassNotFoundException{
		if (messageClassName==null){
			messageClassName = Message.class.getName();
		}
		return messageClassName;
	}
}
