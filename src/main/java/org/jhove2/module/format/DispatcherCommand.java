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

package org.jhove2.module.format;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jhove2.core.AbstractCommand;
import org.jhove2.core.Format;
import org.jhove2.core.FormatIdentification;
import org.jhove2.core.I8R;
import org.jhove2.core.JHOVE2;
import org.jhove2.core.JHOVE2Exception;
import org.jhove2.core.Message;
import org.jhove2.core.Message.Context;
import org.jhove2.core.Message.Severity;
import org.jhove2.core.reportable.ReportableFactory;
import org.jhove2.core.source.Source;
import org.jhove2.module.Module;
import org.jhove2.module.identify.DROIDIdentifier;

/**
 * Module that inspects the presumptive format identifications attached to a
 * Source object, and dispatches the Source to the appropriate format module(s)
 * for feature extraction and, where appropriate, format and profile validation
 * 
 * @author smorrissey, rnanders
 */
public class DispatcherCommand extends AbstractCommand {
    /** Module version identifier. */
    public static final String VERSION = "0.5.5";

    /** Module release date. */
    public static final String RELEASE = "2010-01-21";

    /** Module rights statement. */
    public static final String RIGHTS = "Copyright 2009 by The Regents of the University of California, "
            + "Ithaka Harbors, Inc., and The Board of Trustees of the Leland "
            + "Stanford Junior University. "
            + "Available under the terms of the BSD license.";

    /**
     * Dispatch map. Maps from unique identifiers to Spring bean names for the
     * modules associated with the formats.
     */
    private static ConcurrentMap<String, String> dispatchMap;

    /** Map from JHOVE2 format identifiers to bean name for format */
    private static ConcurrentMap<String, String> jhoveIdToBeanName;

    /**
     * Instantiate a new <code>DispatcherCommand</code>.
     */
    public DispatcherCommand() {
        super(VERSION, RELEASE, RIGHTS);
    }

    /**
     * Maps from Source FormatIdentifications to the appropriate JHOVE2 format
     * modules (if one exists), and invokes the modules to extract format
     * features of the format instance
     * 
     * @param source
     *            Source with FormatIdentifications
     * @param jhove2
     *            JHOVE2 framework object
     * @throws JHOVE2Exception
     * @see org.jhove2.core.Command#execute(org.jhove2.core.JHOVE2,
     *      org.jhove2.core.source.Source)
     */
    @Override
    public void execute(JHOVE2 jhove2, Source source) throws JHOVE2Exception {
        /*
         * Sometimes more than one format identification will match to the same
         * JHOVE2 format; eliminate duplicates from list of JHOVE2 format
         * modules to be run, then dispatch to each format module.
         */
        HashMap<I8R, Format> jhoveFormats = new HashMap<I8R, Format>();
        for (FormatIdentification fid : source.getPresumptiveFormats()) {
            /*
             * Make sure identification found a match for format in the JHOVE2
             * namespace.
             */
            if (fid.getJhove2Identification() != null) {
                /*
                 * Use the JHOVE2 format id to get bean name for format in
                 * Spring configuration file.
                 */
                String beanName = getJhoveIdToBeanName().get(
                        fid.getJhove2Identification().getValue());
                if (beanName != null) {
                    Format format = ReportableFactory.getReportable(
                            Format.class, beanName);
                    jhoveFormats.put(fid.getJhove2Identification(), format);
                }
            }
        }
        /*
         * More than one JHOVE2 format might map to the same format module, so
         * we will keep track of the modules we run so as not to run them more
         * than once per Source.
         */
        TreeSet<I8R> visitedModules = new TreeSet<I8R>();
        /* now invoke the format module. */
        for (I8R id : jhoveFormats.keySet()) {
            Format format = jhoveFormats.get(id);
            Module module = this.getModuleFromIdentifier(id);
            if (module == null) {
                BaseFormatModule bFormatModule = new BaseFormatModule();
                String[] parms = new String[] { id.getValue() };
                bFormatModule
                        .setModuleNotFoundMessage(new Message(
                                Severity.ERROR,
                                Context.PROCESS,
                                "org.jhove2.module.format.DispatcherCommand.moduleNotFoundMessage",
                                (Object[]) parms));
                bFormatModule.setFormat(format);
                source.addModule(bFormatModule);
            }
            else if (!(module instanceof FormatModule)) {
                BaseFormatModule bFormatModule = new BaseFormatModule();
                String[] parms = new String[] { id.getValue() };
                bFormatModule
                        .setModuleNotFormatModuleMessage(new Message(
                                Severity.ERROR,
                                Context.PROCESS,
                                "org.jhove2.module.format.DispatcherCommand.moduleNotFormatModuleMessage",
                                (Object[]) parms));
                bFormatModule.setFormat(format);
                source.addModule(bFormatModule);
            }
            else {
                FormatModule formatModule = (FormatModule) module;
                if (formatModule.getFormat() == null) {
                    formatModule.setFormat(format);
                }
                if (!visitedModules.contains(formatModule
                        .getReportableIdentifier())) {
                    visitedModules.add(formatModule.getReportableIdentifier());
                    formatModule.execute(jhove2, source);
                }
            }
        }
    }

    /**
     * Loads static map from JHOVE2 identifier to module from properties file if
     * necessary, then returns map
     * 
     * @return map from JHOVE2 identifier to module from properties if necessary
     * @throws JHOVE2Exception
     */
    public static ConcurrentMap<String, String> getDispatchMapOld()
            throws JHOVE2Exception {
        if (dispatchMap == null) {
            dispatchMap = new ConcurrentHashMap<String, String>();
            Properties props = ReportableFactory.getProperties("DispatchMap");
            Set<String> keys = props.stringPropertyNames();
            for (String key : keys) {
                String value = props.getProperty(key);
                dispatchMap.put(key, value);
                // System.out.println(key + " = " + value);
            }
        }
        return dispatchMap;
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
            Map<String, Object> map = ReportableFactory
                    .getObjectsForType(BaseFormatModule.class);
            /* For each of the format modules */
            for (Entry<String, Object> entry : map.entrySet()) {
                /* Get the Spring bean name for the format module */
                String moduleBeanName = entry.getKey();
                /* Get the JHOVE format identifier that the module references */
                BaseFormatModule module = (BaseFormatModule) entry.getValue();
                Format format = module.format;
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

    /**
     * Determine module to be invoked from format identifier
     * 
     * @param identifier
     *            JHOVE2 identifier for format
     * @return Module mapped to the JHOVE2 format identifier, or null if no
     *         module has been mapped to the JHOVE2 identifier
     * @throws JHOVE2Exception
     */
    public Module getModuleFromIdentifier(I8R identifier)
            throws JHOVE2Exception {
        Module module = null;
        String name = getDispatchMap().get(identifier.getValue());
        if (name != null) {
            module = ReportableFactory.getReportable(Module.class, name);
        }
        return module;
    }

    /**
     * Loads map from JHOVE2 format ID to bean name for that format from
     * properties file if map is not already populated and returns map;
     * otherwise just returns map
     * 
     * @return map from JHOVE2 format ID to bean name for that format
     * @throws JHOVE2Exception
     */
    public static ConcurrentMap<String, String> getJhoveIdToBeanNameOld()
            throws JHOVE2Exception {
        if (DispatcherCommand.jhoveIdToBeanName == null) {
            ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
            Properties props = null;
            props = ReportableFactory
                    .getProperties(DROIDIdentifier.JHOVE2BEANMAP_BEANNAME);
            Set<String> keys = props.stringPropertyNames();
            for (String key : keys) {
                String value = props.getProperty(key);
                map.put(key, value);
            }
            DispatcherCommand.jhoveIdToBeanName = map;
        }
        return DispatcherCommand.jhoveIdToBeanName;
    }

    /**
     * Gets the mapping from format identifier to format object. Initializes the
     * static map on first invocation.
     * 
     * @return map from JHOVE2 format identifier to format object bean name
     * @throws JHOVE2Exception
     */
    public static ConcurrentMap<String, String> getJhoveIdToBeanName()
            throws JHOVE2Exception {
        if (jhoveIdToBeanName == null) {
            jhoveIdToBeanName = new ConcurrentHashMap<String, String>();
            /*
             * Use Spring to get instances of all objects inheriting from
             * BaseFormatModule
             */
            Map<String, Object> formatMap = ReportableFactory
                    .getObjectsForType(Format.class);
            /* For each of the formats */
            for (Entry<String, Object> entry : formatMap.entrySet()) {
                /* Get the Spring bean name for the format object */
                String formatBeanName = entry.getKey();
                /* Get the JHOVE format identifier for the format */
                Format format = (Format) entry.getValue();
                I8R formatID = format.getIdentifier();
                /* Add an entry into the format identifier to module map */
                jhoveIdToBeanName.put(formatID.getValue(), formatBeanName);
                // System.out.println(formatID.getValue() + " = " + formatBeanName);
            }
        }
        return jhoveIdToBeanName;
    }

}
