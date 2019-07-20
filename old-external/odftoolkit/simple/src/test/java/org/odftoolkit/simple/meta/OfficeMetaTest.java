/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package org.odftoolkit.simple.meta;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.attribute.meta.MetaValueTypeAttribute.Value;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.type.Duration;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class OfficeMetaTest {

	private String filename = "metaTest.odt";
	private TextDocument doc;
	private OdfFileDom metadom;
	private Meta fMetadata;
	private String generator = "SIMPLE/" + System.getProperty("simple.version");
	private String dctitle = "dctitle";
	private String dcdescription = "dcdescription";
	private String subject = "dcsubject";
	private List<String> keywords = new ArrayList<String>();
	private String initialCreator = "creator";
	private String dccreator = System.getProperty("user.name");
	private String printedBy = "persia p";
	private String language = System.getProperty("user.language");
	private Integer editingCycles = new Integer(4);
	private Duration editingDuration = Duration.valueOf("P49DT11H8M9S");
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	@Before
	public void setUp() throws Exception {
		doc = (TextDocument) TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(filename));
		metadom = doc.getMetaDom();
		fMetadata = new Meta(metadom);
	}

	@Test
	public void testGetGenerator() throws Exception {
		fMetadata.setGenerator(generator);
                writeAndReload();
		Assert.assertEquals(generator, fMetadata.getGenerator());
	}

	@Test
	public void testGetDcTitle() throws Exception {
		fMetadata.setTitle(dctitle);
                writeAndReload();
		Assert.assertEquals(dctitle, fMetadata.getTitle());
	}

	@Test
	public void testGetDcDescription() throws Exception {
		fMetadata.setDescription(dcdescription);
                writeAndReload();
		Assert.assertEquals(dcdescription, fMetadata.getDescription());
	}

	@Test
	public void testGetSubject() throws Exception {
		fMetadata.setSubject(subject);
                writeAndReload();
		Assert.assertEquals(subject, fMetadata.getSubject());
	}

	@Test
	public void testSetAndGetKeywords() throws Exception {
		keywords.add("lenovo2");
		keywords.add("computer3");
		keywords.add("think center");
		fMetadata.setKeywords(keywords);
		writeAndReload();
		Assert.assertEquals(keywords, fMetadata.getKeywords());
	}

	@Test
	public void testGetInitialCreator() throws Exception {
		fMetadata.setInitialCreator(initialCreator);
                writeAndReload();
		Assert.assertEquals(initialCreator, fMetadata.getInitialCreator());
	}

	@Test
	public void testGetDcCreator() throws Exception {
		fMetadata.setCreator(dccreator);
                writeAndReload();
		Assert.assertEquals(dccreator, fMetadata.getCreator());
	}

	@Test
	public void testGetPrintedBy() throws Exception {
		fMetadata.setPrintedBy(printedBy);
                writeAndReload();
		Assert.assertEquals(printedBy, fMetadata.getPrintedBy());
	}

	@Test
	public void testSetAndGetCreationDate() throws Exception {
		Calendar creationDate = Calendar.getInstance();
		fMetadata.setCreationDate(creationDate);
		writeAndReload();
		// //the millisecond lost while changing calendar to string
		// creationDate.clear(Calendar.MILLISECOND);
		// fMetadata.getCreationDate().clear(Calendar.MILLISECOND);
		//Assert.assertEquals(0,creationDate.compareTo(fMetadata.getCreationDate
		// ()));
		String expected = sdf.format(creationDate.getTime());
		String actual = sdf.format(fMetadata.getCreationDate().getTime());
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testSetDcDate() throws Exception {
		Calendar dcDate = Calendar.getInstance();
		fMetadata.setDcdate(dcDate);
		writeAndReload();
		// dcDate.clear(Calendar.MILLISECOND);
		// fMetadata.getDcdate().clear(Calendar.MILLISECOND);
		// Assert.assertEquals(0,dcDate.compareTo(fMetadata.getDcdate()));
		String expected = sdf.format(dcDate.getTime());
		String actual = sdf.format(fMetadata.getDcdate().getTime());
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testSetPrintDate() throws Exception {
		Calendar printDate = Calendar.getInstance();
		fMetadata.setPrintDate(printDate);
		writeAndReload();
		// printDate.clear(Calendar.MILLISECOND);
		// fMetadata.getPrintDate().clear(Calendar.MILLISECOND);
		// Assert.assertEquals(0,printDate.compareTo(fMetadata.getPrintDate()));
		String expected = sdf.format(printDate.getTime());
		String actual = sdf.format(fMetadata.getPrintDate().getTime());
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testGetLanguage() throws Exception {
		fMetadata.setLanguage(language);
                writeAndReload();
		Assert.assertEquals(language, fMetadata.getLanguage());
	}

	@Test
	public void testGetEditingCycles() throws Exception {
		fMetadata.setEditingCycles(editingCycles);
                writeAndReload();
		Assert.assertNotNull(fMetadata.getEditingCycles());
	}

	@Test
	public void testGetEditingDuration() throws Exception {
		fMetadata.setEditingDuration(editingDuration);
                writeAndReload();
		Assert.assertNotNull(fMetadata.getEditingDuration());
	}

	@Test
	public void testEmptyKeyword() throws Exception {
		List<String> emptyKeyword = new ArrayList<String>();
		fMetadata.setKeywords(emptyKeyword);
		writeAndReload();
		Assert.assertNull(fMetadata.getKeywords());
	}

	@Test
	public void testAddKeyword() throws Exception {
		String newKeyword = "hello";
		fMetadata.addKeyword(newKeyword);
		writeAndReload();
		Assert.assertEquals(true, fMetadata.getKeywords().contains(newKeyword));
	}

	@Test
	public void testSetAndGetUserdefinedData() throws Exception {
		// remove if there is userdefined data
		List<String> names;
		names = fMetadata.getUserDefinedDataNames();
		if (names == null) {
			names = new ArrayList<String>();
		} else {
			for (String name : names) {
				fMetadata.removeUserDefinedDataByName(name);
			}
			names.clear();
		}
		names.add("weather");
		names.add("mood");
		names.add("late");
		// test set
		fMetadata.setUserDefinedData(names.get(0), Value.STRING.toString(),
				"windy");
		fMetadata.setUserDefinedData(names.get(1), Value.STRING.toString(),
				"happy");
		fMetadata.setUserDefinedData(names.get(2), Value.BOOLEAN.toString(),
				"false");
		writeAndReload();

		// test get
		Assert.assertEquals(names, fMetadata.getUserDefinedDataNames());
		Assert.assertEquals(Value.STRING.toString(), fMetadata.getUserDefinedDataType(names.get(0)));
		Assert.assertEquals("windy", fMetadata.getUserDefinedDataValue(names.get(0)));

		fMetadata.setUserDefinedDataValue(names.get(1), "false");
		fMetadata.setUserDefinedDataType(names.get(1), Value.BOOLEAN.toString());
		fMetadata.setUserDefinedData(names.get(2), Value.STRING.toString(),
				"no");
		writeAndReload();
		// update
		Assert.assertEquals("false", fMetadata.getUserDefinedDataValue(names.get(1)));
		Assert.assertEquals(Value.BOOLEAN.toString(), fMetadata.getUserDefinedDataType(names.get(1)));
		Assert.assertEquals("no", fMetadata.getUserDefinedDataValue(names.get(2)));
		Assert.assertEquals(Value.STRING.toString(), fMetadata.getUserDefinedDataType(names.get(2)));
		writeAndReload();
		// remove
		fMetadata.removeUserDefinedDataByName(names.get(0));
		writeAndReload();
		Assert.assertEquals(2, fMetadata.getUserDefinedDataNames().size());
	}

	@Test
	public void testReadEmptyDocumentMeta() throws Exception {

		// create a new empty document
		doc = TextDocument.newTextDocument();
		doc.save(ResourceUtilities.newTestOutputFile("EmptyDocForMetaTest.odt"));

		// read empty document meta
		doc = (TextDocument) TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("EmptyDocForMetaTest.odt"));
		metadom = doc.getMetaDom();
		fMetadata = new Meta(metadom);
		//Assert.assertTrue(fMetadata.getGenerator().startsWith(generator));
		//ToDO: http://odftoolkit.org/bugzilla/show_bug.cgi?id=171
		// Assert.assertEquals(fMetadata.getGenerator(), generator);
		Assert.assertNull(fMetadata.getTitle());
		Assert.assertNull(fMetadata.getDescription());
		Assert.assertNull(fMetadata.getSubject());
		Assert.assertNull(fMetadata.getKeywords());
		Assert.assertNull(fMetadata.getPrintedBy());
		Assert.assertNull(fMetadata.getPrintDate());
		Assert.assertNotNull(fMetadata.getUserDefinedDataNames());
	}

	@Test
	public void testReadDocumentMeta() throws Exception {
		// create a new empty document
		TextDocument textDoc = TextDocument.newTextDocument();
		textDoc.save(ResourceUtilities.newTestOutputFile("DocForMetaTest.odt"));
		textDoc.close();
		// read empty document meta
		textDoc = (TextDocument) TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("DocForMetaTest.odt"));
		Meta meta = textDoc.getOfficeMetadata();
		Assert.assertNotNull(meta.getGenerator());
		Assert.assertNotNull(meta.getCreationDate());
		Assert.assertNotNull(meta.getCreator());
		Assert.assertNotNull(meta.getDcdate());
		Assert.assertTrue(meta.getEditingCycles()>0);
		Assert.assertNotNull(meta.getEditingDuration());
		Assert.assertNotNull(meta.getLanguage());
		textDoc.close();
	}

    private void writeAndReload() throws Exception {
        File persistedDocument = File.createTempFile(getClass().getName(), ".odt", ResourceUtilities.getTempTestDirectory());
        persistedDocument.deleteOnExit();
	doc.save(persistedDocument);
	Thread.sleep(100);
	doc = (TextDocument) TextDocument.loadDocument(persistedDocument);
	metadom = doc.getMetaDom();
	fMetadata = new Meta(metadom);
    }

}
