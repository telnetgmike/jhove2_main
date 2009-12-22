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

package org.jhove2.module.display;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jhove2.core.I8R;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.info.ReportableInfo;
import org.jhove2.core.info.ReportablePropertyInfo;
import org.jhove2.core.info.ReportableSourceInfo;
import org.jhove2.core.reportable.ReportableFactory;
import org.jhove2.core.reportable.Reportable;
import org.jhove2.module.AbstractModule;

/**
 * JHOVE2 displayer utility.
 * 
 * @author mstrong, slabrams, smorrissey
 */
public abstract class AbstractDisplayer extends AbstractModule implements
Displayer {

	/** Feature display visibilities. */
	public enum DisplayVisibility {
		Always, IfFalse, IfNegative, IfNonNegative, IfNonPositive, IfNonZero, IfPositive, IfTrue, IfZero, Never
	}
	
	/**
	 * Show identifiers flag: if true, show identifiers in JSON and Text display
	 * mode.
	 */
	protected boolean showIdentifiers;

	/** Output file pathname, if System.out is not being used. */
	protected String filePathname;

	/** Displayer visbility flags configured by user to indicate whether
	 *  or not a feature should be displayed.
	 */
	private static ConcurrentMap<String, DisplayVisibility> visibilities;

	/** Indentation flag.  If true, displayed output is indented to indicate
	 * subsidiarity relationships.
	 */
	protected boolean shouldIndent;

	/**
	 * Instantiate a new <code>AbstractDisplayer</code>.
	 * 
	 * @param version
	 *            AbstractDisplayer version identifier
	 * @param date
	 *            AbstractDisplayer build date
	 * @param rights
	 *            AbstractDisplayer rights statement
	 */
	public AbstractDisplayer(String version, String date, String rights) {
		super(version, date, rights);
		this.setShowIdentifiers(DEFAULT_SHOW_IDENTIFIERS);
	}


	@Override
	public void display(Reportable reportable)
		throws FileNotFoundException, JHOVE2Exception
	{
		this.display(reportable, this.getFilePathname());
	}

	@Override
	public void display(Reportable reportable, String filePathname)
		throws FileNotFoundException, JHOVE2Exception
	{
		PrintStream out = System.out;
		if (filePathname != null) {
			this.filePathname = filePathname;
			out = new PrintStream(filePathname);
		}	
		this.display(reportable, out);
	}

	@Override
	public void display(Reportable reportable, PrintStream out)
		throws JHOVE2Exception
	{
		this.getTimerInfo().setStartTime();
		this.startDisplay(out, 0);
		this.display(out, reportable, 0, 0);
		this.endDisplay(out, 0);
		this.getTimerInfo().setEndTime();
	}

	/**
	 * Display a {@link org.jhove2.core.reportable.Reportable}.
	 * 
	 * @param out
	 *            Print stream
	 * @param reportable
	 *            Reportable
	 * @param level
	 *            Nesting level
	 * @param order
	 *            Ordinal position of this reportable with respect to its
	 *            enclosing reportable or collection
	 */
	protected void display(PrintStream out, Reportable reportable, int level,
			               int order)
		throws JHOVE2Exception
	{
		this.display(out, reportable, level, order, true);
	}
	
	/**
	 * Display a {@link org.jhove2.core.reportable.Reportable}.
	 * 
	 * @param out
	 *            Print stream
	 * @param reportable
	 *            Reportable
	 * @param level
	 *            Nesting level
	 * @param order
	 *            Ordinal position of this reportable with respect to its
	 *            enclosing reportable or collection
	 * @param shouldNestReportable
	 * 			boolean indicating new level of reportable hierarchy
	 */
	protected void display(PrintStream out, Reportable reportable, int level,
			               int order, boolean shouldNestReportable)
		throws JHOVE2Exception
	{
		ReportableInfo reportableInfo = new ReportableInfo(reportable);
		String name = reportableInfo.getName();
		I8R identifier = reportableInfo.getIdentifier();
		if (shouldNestReportable){
			this.startReportable(out, level, name, identifier, order);
		}
		int or = 0;
		List<ReportableSourceInfo> list = reportableInfo.getProperties();
		for (ReportableSourceInfo source : list) {
			Set<ReportablePropertyInfo> props = source.getProperties();
			for (ReportablePropertyInfo prop : props) {
				I8R id = prop.getIdentifier();
				DisplayVisibility visbility = 
					getVisibilities().get(id.getValue());
				if (visbility != null && visbility == DisplayVisibility.Never) {
					continue;
				}
				Method method = prop.getMethod();
				String propertyName = method.getName();
				if (propertyName.indexOf("get") == 0) {
					propertyName = propertyName.substring(3);
				}
				try {
					Object value = method.invoke(reportable);
					if (value != null) {
						if (visbility != null) {
							if (value instanceof Boolean) {
								boolean b = ((Boolean) value).booleanValue();
								if ((b && visbility == DisplayVisibility.IfFalse)
										|| (!b && visbility == DisplayVisibility.IfTrue)) {
									continue;
								}
							} else if (value instanceof Number) {
								double d = ((Number) value).doubleValue();
								if ((d == 0.0 && visbility == DisplayVisibility.IfNonZero)
										|| (d != 0.0 && visbility == DisplayVisibility.IfZero)
										|| (d < 0.0 && visbility == DisplayVisibility.IfNonNegative)
										|| (d > 0.0 && visbility == DisplayVisibility.IfNonPositive)
										|| (d <= 0.0 && visbility == DisplayVisibility.IfPositive)
										|| (d >= 0.0 && visbility == DisplayVisibility.IfNegative)) {
									continue;
								}
							}
						}
						display(out, level, propertyName, id, value, or++, prop.getUnitOfMeasure());
					}
				} catch (IllegalArgumentException e) {
					throw new JHOVE2Exception(
							"AbstractDisplayer.display(): IllegalArgumentException", e);
				} catch (IllegalAccessException e) {
					throw new JHOVE2Exception(
							"AbstractDisplayer.display(): IllegalAccessException", e);
				} catch (InvocationTargetException e) {
					throw new JHOVE2Exception(
							"AbstractDisplayer.display(): InvocationTargetException", e);
				}
			}
		}
		if (shouldNestReportable){
			this.endReportable(out, level, name, identifier);
		}
	}

	/**
	 * Display a {@link org.jhove2.annotation.ReportableProperty}.
	 * 
	 * @param out
	 *            Print stream
	 * @param level
	 *            Nesting level
	 * @param name
	 *            Property name
	 * @param identifier
	 *            Property identifier
	 * @param value
	 *            Property value
	 * @param order
	 *            Ordinal position of this property with respect to its
	 *            enclosing reportable or collection
	 */
	protected void display(PrintStream out, int level, String name,
			               I8R identifier, Object value, int order,
			               String unitOfMeasure)
		throws JHOVE2Exception
	{
		if (value instanceof List<?>) {
			List<?> valueList = (List<?>) value;
			int size = valueList.size();
			if (size > 0) {
				this.startCollection(out, level + 1, name,
						identifier, size, order);
				String singularName = I8R.singularName(name);
				I8R id = I8R.singularIdentifier(identifier);
				int i = 0;
				for (Object prop : valueList) {
					this.display(out, level + 1, singularName, id, prop, i++, unitOfMeasure);
				}
				this.endCollection(out, level + 1, name, identifier,
						size);
			}
		} else if (value instanceof Set<?>) {
			Set<?> set = (Set<?>) value;
			int size = set.size();
			if (size > 0) {
				this.startCollection(out, level + 1, name,
						identifier, size, order);
				String singularName = I8R.singularName(name);
				I8R id = I8R.singularIdentifier(identifier);
				int i = 0;
				for (Object prop : set) {
					display(out, level + 1, singularName, id, prop, i++, unitOfMeasure);
				}
				this.endCollection(out, level + 1, name, identifier,
						size);
			}
		} else if (value instanceof Reportable) {
			this.startReportable(out, level + 1, name, identifier,
					order, ((Reportable)value).getReportableIdentifier());
			display(out, (Reportable) value, level + 2, 0, false);
			this.endReportable(out, level + 1, name, identifier);
		} else {
			if (value instanceof Date) {
				value = ISO8601.format(value);
			}
			this.displayProperty(out, level + 1, name, identifier,
					value, order, unitOfMeasure);
		}
	}

	/**
	 * Get indentation appropriate for a nesting level.
	 * 
	 * @param level
	 *            Nesting level
	 * @param shouldIndent
	 *            Indentation flag; if true displayed output is indented to
	 *            indicate subsidiarity relationships
	 * @return Indentation string
	 */
	public static String getIndent(int level, boolean shouldIndent) {
		StringBuffer indent = new StringBuffer("");
		if (shouldIndent){
			for (int i = 0; i < level; i++) {
				indent.append(" ");
			}
		}
		return indent.toString();
	}

	/**
	 * Utility method to get user-specified restrictions on display visibility of Reportable properties
	 * @return TreeMap mapping from Reportable property I8R to a DisplayVisibility
	 * @throws JHOVE2Exception
	 */
	public static ConcurrentMap<String, DisplayVisibility> getVisibilities()
		throws JHOVE2Exception
	{
		if (visibilities == null){
			ConcurrentHashMap<String, DisplayVisibility> viz = new ConcurrentHashMap<String, DisplayVisibility>();
			Properties props = ReportableFactory.getProperties("DisplayVisibility");
			if (props != null) {
				Set<String> keys = props.stringPropertyNames();
				for (String key : keys) {
					DisplayVisibility value = DisplayVisibility.valueOf(props
							.getProperty(key));
					if (value != null) {
						viz.put(key, value);
					}
				}
			}
			visibilities = viz;
		}
		return visibilities;
	}

	/**
	 * Get show identifiers flag.
	 * 
	 * @return Show identifiers flag; if true, show identifiers in JSON and Text
	 *         display mode
	 * @see org.jhove2.module.display.Displayer#getShowIdentifiers()
	 */
	@Override
	public boolean getShowIdentifiers() {
		return this.showIdentifiers;
	}

	/**
	 * Set show identifiers flag.
	 * 
	 * @param flag
	 *            If true, show identifiers in JSON and Text display mode
	 * @see org.jhove2.module.display.Displayer#setShowIdentifiers(boolean)
	 */
	@Override
	public void setShowIdentifiers(boolean flag) {
		this.showIdentifiers = flag;
	}
	
	/** Get output file pathname.
	 * @return Output file pathname
	 */
	@Override
	public String getFilePathname() {
		return this.filePathname;
	}

	/** Set output file pathname
	 * @param filePathname Output file pathname
	 */
	@Override
	public void setFilePathname(String filePathname) {
		this.filePathname = filePathname;
	}
	
	@Override
	public boolean getShouldIndent() {
		return this.shouldIndent;
	}
	
	@Override
	public void setShouldIndent(boolean shouldIndent){
		this.shouldIndent = shouldIndent;
	}
}


