/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.odftoolkit.simple;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.simple.utils.ResourceUtilities;

/**
 * Test class for template aspects of calc documents.
 * @author <a href="mailto:fhopf@odftoolkit.org">Florian Hopf</a>
 */
public class SpreadsheetTemplateTest {

	private static final String TEST_SPREADSHEET_TEMPLATE = "/spreadsheetTestTemplate.ots";

	@Test
	public void testLoadingASpreadsheetTemplate() throws Exception {
		Document document = Document.loadDocument(this.getClass().getResourceAsStream(TEST_SPREADSHEET_TEMPLATE));
		Assert.assertEquals(Document.OdfMediaType.SPREADSHEET_TEMPLATE.getMediaTypeString(), document.getMediaTypeString());
	}

	@Test
	public void testSavingASpreadsheetTemplate() throws Exception {
		Document document = Document.loadDocument(this.getClass().getResourceAsStream(TEST_SPREADSHEET_TEMPLATE));
		File destination = File.createTempFile("simple-test", ".ots", ResourceUtilities.getTempTestDirectory());
		document.save(destination);

		// load again
		Document loadedDocument = Document.loadDocument(destination);
		Assert.assertEquals(Document.OdfMediaType.SPREADSHEET_TEMPLATE.getMediaTypeString(), loadedDocument.getMediaTypeString());
	}

	@Test
	public void testNewSpreadsheetTemplate() throws Exception {
		Document document = SpreadsheetDocument.newSpreadsheetTemplateDocument();
		Assert.assertEquals(Document.OdfMediaType.SPREADSHEET_TEMPLATE.getMediaTypeString(), document.getMediaTypeString());
		Assert.assertEquals(Document.OdfMediaType.SPREADSHEET_TEMPLATE.getMediaTypeString(), document.getPackage().getMediaTypeString());
		File destination = File.createTempFile("simple-test", ".ots", ResourceUtilities.getTempTestDirectory());
		document.save(destination);

		// load again
		Document loadedDocument = Document.loadDocument(destination);
		Assert.assertEquals(Document.OdfMediaType.SPREADSHEET_TEMPLATE.getMediaTypeString(),
				loadedDocument.getMediaTypeString());
		Assert.assertTrue(document instanceof SpreadsheetDocument);
	}

	@Test
	public void testSwitchingOdfSpreadsheetDocument() throws Exception {
		SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
		document.changeMode(SpreadsheetDocument.OdfMediaType.SPREADSHEET_TEMPLATE);
		Assert.assertEquals(Document.OdfMediaType.SPREADSHEET_TEMPLATE.getMediaTypeString(), document.getPackage().getMediaTypeString());

		document = SpreadsheetDocument.newSpreadsheetTemplateDocument();
		document.changeMode(SpreadsheetDocument.OdfMediaType.SPREADSHEET);
		Assert.assertEquals(Document.OdfMediaType.SPREADSHEET.getMediaTypeString(),
				document.getPackage().getMediaTypeString());
	}
}
