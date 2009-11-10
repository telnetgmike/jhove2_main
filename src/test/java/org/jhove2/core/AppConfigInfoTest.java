/**
 * 
 */
package org.jhove2.core;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author smorrissey
 *
 */
public class AppConfigInfoTest {

	private AppConfigInfo config;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		config = new AppConfigInfo();
	}

	/**
	 * Test method for {@link org.jhove2.core.AppConfigInfo#getJhove2Home()}.
	 */
	@Test
	public void testGetJhove2Home() {
		// test assumes we have  set JHOVE2 HOME env variable
		String sep = System.getProperty("file.separator");
		assertEquals(config.getWorkingDirectory().concat(sep).
				concat("target").concat(sep).concat("classes"),
				config.getJhove2Home());
	}

}
