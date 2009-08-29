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
	protected Format presumptiveFormat;

	/** Identification product. */
	protected Product identifierProduct;

	/**
	 * Source unit associated with the format.
	 */
	protected Source source;

	/**
	 * Instantiate a new <code>FormatIdentification</code>.
	 * 
	 * @param format
	 *            Presumptively identified format
	 * @param confidence
	 *            Identification confidence level
	 */
	public FormatIdentification(Format format, Confidence confidence) {
		this.presumptiveFormat = format;
		this.confidence = confidence;
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
		this.identifierProduct = process;
	}

	/**
	 * Get identification process.
	 * 
	 * @return Identification process
	 */
	@ReportableProperty(order = 1, value = "Identifier product.")
	public Product getIdentifierProduct() {
		return this.identifierProduct;
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
		return this.presumptiveFormat;
	}

	/**
	 * Get source associated with the format. Set only by aggregate
	 * identification.
	 * 
	 * @return Source associated with the format
	 */
	@ReportableProperty(order = 4, value = "Source associated with this presumptive identification.")
	public Source getSource() {
		return this.source;
	}

	
	/**
	 * Set source unit.
	 * 
	 * @param source
	 *            Source unit
	 */
	public void setSource(Source source) {
		this.source = source;
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
		if (identification==null){
			return 1;
		}
		if (this==identification){
			return 0;
		}
		int compareFormat = this.getPresumptiveFormat().getIdentifier().compareTo(
				identification.getPresumptiveFormat().getIdentifier());
		if (compareFormat != 0){
			return compareFormat;
		}
		if (this.getIdentifierProduct()==null){
			if (identification.getIdentifierProduct()!= null){
				return -1;
			}
		}
		else if (identification.getIdentifierProduct()==null){
			return 1;
		}
		else {
			int compareProduct = this.getIdentifierProduct().getJhove2Identifer().compareTo
						(identification.getIdentifierProduct().getJhove2Identifer());
			if (compareProduct != 0){
				return compareProduct;
			}
		}
		int order1 = this.confidence.getOrder();
		int order2 = identification.getConfidence().getOrder();
		if (order1 < order2) {
			return -1;
		} else if (order1 > order2) {
			return 1;
		}
		int compareSource = this.source.compareTo(identification.getSource());
		return compareSource;
	}
	
	
	@Override
	public boolean equals (Object obj){
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (! (obj instanceof FormatIdentification)){
			return false;
		}
		FormatIdentification fiObj = (FormatIdentification) obj;	
		boolean equals = (this.getPresumptiveFormat().getIdentifier().equals
				            (fiObj.getPresumptiveFormat().getIdentifier()));
		if (!equals){
			return equals;
		}		
		if (this.getIdentifierProduct()==null){
			if (fiObj.getIdentifierProduct() != null){
				return false;
			}
		}
		else if (fiObj.getIdentifierProduct()==null){
			return false;
		}
		else {
			equals = this.getIdentifierProduct().getJhove2Identifer().equals(fiObj.getIdentifierProduct()
					.getJhove2Identifer());
			if (!equals){
				return equals;
			}
		}			
		equals = this.getConfidence().getOrder()== fiObj.getConfidence().getOrder();
		if (!equals){
			return equals;
		}		
		equals = this.source.equals(fiObj.getSource());
		return equals;
	}
	

	public void setConfidence(Confidence confidence) {
		this.confidence = confidence;
	}

	public void setPresumptiveFormat(Format presumptiveFormat) {
		this.presumptiveFormat = presumptiveFormat;
	}

	public void setIdentifierProduct(Product identifierProduct) {
		this.identifierProduct = identifierProduct;
	}
}
