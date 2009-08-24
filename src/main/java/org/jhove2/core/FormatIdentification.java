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

import java.util.ArrayList;
import java.util.List;

import org.jhove2.annotation.ReportableProperty;
import org.jhove2.core.source.Source;

/**
 * JHOVE2 presumptive format identification.
 * 
 * @author mstrong, slabrams
 */
public class FormatIdentification implements Reportable,
		Comparable<FormatIdentification> {
	/** Identification confidence levels. */
	public enum Confidence {
		Negative(6), Tentative(5), Heuristic(4), PositiveGeneric(3), PositiveSpecific(
				2), Validated(1);

		/** Priority order. */
		private int order;

		/**
		 * Instantiate a new <code>Confidence</code> enum.
		 * 
		 * @param order
		 *            Priority order
		 */
		private Confidence(int order) {
			this.order = order;
		}

		/**
		 * Get priority order.
		 * 
		 * @return Priority order
		 */
		public int getOrder() {
			return this.order;
		}
	}

	/** Identification confidence level. */
	protected Confidence confidence;

	/** Presumptive format. */
	protected Format format;

	/** Identification process. */
	protected Product process;

	/**
	 * Source units associated with the format. Defined only by aggregate
	 * identification.
	 */
	protected List<Source> sources;

	/**
	 * Instantiate a new <code>FormatIdentification</code>.
	 * 
	 * @param format
	 *            Presumptively identified format
	 * @param confidence
	 *            Identification confidence level
	 */
	public FormatIdentification(Format format, Confidence confidence) {
		this.format = format;
		this.confidence = confidence;
		this.sources = new ArrayList<Source>();
	}

	/**
	 * Instantiate a new <code>FormatIdentification</code>.
	 * 
	 * @param format
	 *            Presumptively identified format
	 * @param confidence
	 *            Identification confidence level
	 * @param process
	 *            Identification process
	 */
	public FormatIdentification(Format format, Confidence confidence,
			Product process) {
		this(format, confidence);
		this.process = process;
	}

	/**
	 * Get identification process.
	 * 
	 * @return Identification process
	 */
	@ReportableProperty(order = 1, value = "Identification process.")
	public Product getIdentificationProcess() {
		return this.process;
	}

	/**
	 * Get identification confidence level.
	 * 
	 * @return Identification confidence level
	 */
	@ReportableProperty(order = 2, value = "Identification confidence level.")
	public Confidence getConfidence() {
		return this.confidence;
	}

	/**
	 * Get presumptively identified format.
	 * 
	 * @return Presumptively identified format
	 */
	@ReportableProperty(order = 3, value = "Presumptively identified format.")
	public Format getPresumptiveFormat() {
		return this.format;
	}

	/**
	 * Get source units associated with the format. Defined only by aggregate
	 * identification.
	 * 
	 * @return Source units associated with the format
	 */
	public List<Source> getSources() {
		return this.sources;
	}

	/**
	 * Add a source unit for an aggregate format.
	 * 
	 * @param source
	 *            Source unit
	 */
	public void addSource(Source source) {
		this.sources.add(source);
	}

	/**
	 * Lexically compare format identifications.
	 * 
	 * @param identification
	 *            IdentifierModule to be compared
	 * @return -1, 0, or 1 if this identifier value is less than, equal to, or
	 *         greater than the second
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(FormatIdentification identification) {
		int order1 = this.confidence.getOrder();
		int order2 = identification.getConfidence().getOrder();
		if (order1 < order2) {
			return -1;
		} else if (order1 > order2) {
			return 1;
		}
		return this.format.getName().compareToIgnoreCase(
				identification.getPresumptiveFormat().getName());
	}
}
