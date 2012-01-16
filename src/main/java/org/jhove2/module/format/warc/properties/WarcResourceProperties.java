package org.jhove2.module.format.warc.properties;

import java.util.List;

import org.jhove2.annotation.ReportableProperty;
import org.jhove2.core.reportable.AbstractReportable;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class WarcResourceProperties extends AbstractReportable {

	protected WarcRecordData record;

	public WarcResourceProperties() {
	}

	public WarcResourceProperties(WarcRecordData record) {
		this.record = record;
	}

    @ReportableProperty(order = 1, value = "Warc-Target-URI header value.")
    public String getWarcTargetUri() {
        return record.warcTargetUri;
    }

    @ReportableProperty(order = 2, value = "Warc-Concurrent-To header value.")
    public List<String> getWarcConcurrentTo() {
        return record.warcConcurrentToList;
    }

    @ReportableProperty(order = 3, value = "Warc-IP-Address header value.")
    public String getWarcIpAddress() {
        return record.warcIpAddress;
    }

    @ReportableProperty(order = 4, value = "Ip-Address version.")
    public String getIpAddressVersion() {
    	return record.ipVersion;
    }

    @ReportableProperty(order = 5, value = "Warc-Warcinfo-ID header value.")
    public String getWarcWarcinfoId() {
        return record.warcWarcinfoId;
    }

}
