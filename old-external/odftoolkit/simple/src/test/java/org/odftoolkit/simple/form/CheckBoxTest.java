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

package org.odftoolkit.simple.form;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.draw.ControlStyleHandler;
import org.odftoolkit.simple.draw.FrameRectangle;
import org.odftoolkit.simple.form.FormTypeDefinition.FormCheckboxState;
import org.odftoolkit.simple.style.GraphicProperties;
import org.odftoolkit.simple.style.StyleTypeDefinitions.AnchorType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameHorizontalPosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameVerticalPosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalRelative;
import org.odftoolkit.simple.style.StyleTypeDefinitions.SupportedLinearMeasure;
import org.odftoolkit.simple.style.StyleTypeDefinitions.VerticalRelative;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class CheckBoxTest {
	private static final FrameRectangle CHECK_BOX_RTG = new FrameRectangle(
			0.7972, 1.2862, 2.4441, 0.2669, SupportedLinearMeasure.IN);

    private static final Logger LOG = Logger.getLogger(CheckBoxTest.class.getName());

	public void createForm(String testFileName) {
		try {
            LOG.log(Level.INFO, "Creation of ''{0}'' started!", testFileName);
			TextDocument doc = TextDocument.newTextDocument();
			Form form = doc.createForm("Test Form");

			// checkbox 1
			form.createCheckBox(doc, CHECK_BOX_RTG, "CheckBox 1", "CheckBox 1", "1");

			// checkbox 2
			Paragraph para = doc.addParagraph("CheckBox2 anchor to heading.");
			para.applyHeading();
			CheckBox checkbox = (CheckBox) form.createCheckBox(para, CHECK_BOX_RTG, "CheckBox 2", "CheckBox 2",
					"2");

			// checkbox 3
			para = doc.addParagraph("Insert checkbox3 here, as_char.");
			checkbox = (CheckBox) form.createCheckBox(para, CHECK_BOX_RTG,
					"CheckBox 3", "CheckBox 3", "3");
			checkbox.setCurrentState(FormCheckboxState.CHECKED);
			checkbox.setAnchorType(AnchorType.AS_CHARACTER);

			Table table1 = Table.newTable(doc, 2, 2);
			Cell cell = table1.getCellByPosition("B1");
			para = cell.addParagraph("Insert a check box here.");
			form.createCheckBox(para, CHECK_BOX_RTG, "CheckBox 4", "CheckBox 4",
					"4");
			doc.save(ResourceUtilities.newTestOutputFile(testFileName));

            LOG.log(Level.INFO, "Creation of ''{0}'' succeded!", testFileName);
		} catch (Exception e) {
			Logger.getLogger(CheckBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testCreateCheckBox() {
		try {
            String testFileName = "CreatedCheckBox";
            createForm(testFileName + ".odt");
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(testFileName + ".odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = CheckBox.getSimpleIterator(form);
			int count = 0;
			while (iterator.hasNext()) {
				CheckBox btn = (CheckBox) iterator.next();
				Assert.assertNotNull(btn);
				Assert.assertEquals("CheckBox " + (++count), btn.getName());
				Assert.assertEquals("CheckBox " + (count), btn.getLabel());
			}
		} catch (Exception e) {
			Logger.getLogger(CheckBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	@Test
	public void testRemoveCheckBox() {
		try {
            String testFileName = "testRemoveCheckBox";
            createForm(testFileName + ".odt");
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(testFileName + ".odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = CheckBox.getSimpleIterator(form);
			while (iterator.hasNext()) {
				CheckBox btn = (CheckBox) iterator.next();
				if (btn.getName().equals("CheckBox 2")) {
					iterator.remove();
					break;
				}
			}
			CheckBox find = null;
			while (iterator.hasNext()) {
				CheckBox btn = (CheckBox) iterator.next();
				if (btn.getName().equals("CheckBox 2")) {
					find = btn;
					break;
				}
			}
			Assert.assertNull(find);
			textDoc.save(ResourceUtilities
					.newTestOutputFile(testFileName + "_OUT.odt"));
		} catch (Exception e) {
			Logger.getLogger(CheckBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testSetCheckBoxRectangle() {
		try {
            String testFileName = "testSetCheckBoxRectangle";
            createForm(testFileName + ".odt");
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(testFileName + ".odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = CheckBox.getSimpleIterator(form);
			CheckBox find = null;
			while (iterator.hasNext()) {
				CheckBox btn = (CheckBox) iterator.next();
				if (btn.getName().equals("CheckBox 2")) {
					find = btn;
					break;
				}
			}
			Assert.assertNotNull(find);
			// change the bounding box
			find.setRectangle(new FrameRectangle(2.25455, 5, 3, 0.5,
					SupportedLinearMeasure.IN));
			Assert.assertEquals(3.0, find.getRectangle().getWidth());
			Assert.assertEquals(0.5, find.getRectangle().getHeight());
			Assert.assertEquals(5.0, find.getRectangle().getY());
			Assert.assertEquals(2.2546, find.getRectangle().getX());
			textDoc.save(ResourceUtilities
					.newTestOutputFile(testFileName + "_OUT.odt"));
		} catch (Exception e) {
			Logger.getLogger(CheckBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testSetCheckBoxAnchorType() {
		try {
            String testFileName = "TestSetCheckBoxAnchorType";
            createForm(testFileName + ".odt");
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(testFileName + ".odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = CheckBox.getSimpleIterator(form);
			CheckBox find = null;
			while (iterator.hasNext()) {
				CheckBox btn = (CheckBox) iterator.next();
				if (btn.getName().equals("CheckBox 3")) {
					find = btn;
					break;
				}
			}
			Assert.assertNotNull(find);
			// change the bounding box
			find.setAnchorType(AnchorType.TO_CHARACTER);
			// validate
			ControlStyleHandler frameStyleHandler = find.getDrawControl()
					.getStyleHandler();
			GraphicProperties graphicPropertiesForWrite = frameStyleHandler
					.getGraphicPropertiesForWrite();
			Assert.assertEquals(VerticalRelative.PARAGRAPH,
					graphicPropertiesForWrite.getVerticalRelative());
			Assert.assertEquals(FrameVerticalPosition.TOP,
					graphicPropertiesForWrite.getVerticalPosition());
			Assert.assertEquals(HorizontalRelative.PARAGRAPH,
					graphicPropertiesForWrite.getHorizontalRelative());
			Assert.assertEquals(FrameHorizontalPosition.CENTER,
					graphicPropertiesForWrite.getHorizontalPosition());

			textDoc.save(ResourceUtilities
                .newTestOutputFile(testFileName + "_OUT.odt"));
		} catch (Exception e) {
			Logger.getLogger(CheckBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testSetCheckBoxLabel() {
            String testFileName = "TestSetCheckBoxLabel";
            createForm(testFileName + ".odt");
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(testFileName + ".odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = CheckBox.getSimpleIterator(form);
			CheckBox find = null;
			while (iterator.hasNext()) {
				CheckBox btn = (CheckBox) iterator.next();
				if (btn.getName().equals("CheckBox 4")) {
					find = btn;
					break;
				}
			}
			Assert.assertNotNull(find);
			// set new label
			String newLabel = "Change the content of CheckBox 4.";
			find.setLabel(newLabel);
			Assert.assertEquals(newLabel, find.getLabel());
			// set null value
			find.setLabel(null);
			Assert.assertEquals("", find.getLabel());

			textDoc.save(ResourceUtilities
					.newTestOutputFile(testFileName + "_OUT.odt"));
		} catch (Exception e) {
			Logger.getLogger(CheckBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testCheckedState() {
		try {
            String testFileName = "TestSetCheckBoxState";
            createForm(testFileName + ".odt");
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(testFileName + ".odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = CheckBox.getSimpleIterator(form);
			CheckBox find = null;
			while (iterator.hasNext()) {
				CheckBox btn = (CheckBox) iterator.next();
				if (btn.getName().equals("CheckBox 3")) {
					find = btn;
					break;
				}
			}
			Assert.assertNotNull(find);
			// validate
			Assert.assertEquals(FormCheckboxState.CHECKED, find
					.getCurrentState());

			find.setCurrentState(FormCheckboxState.UNKNOWN);
			Assert.assertEquals(FormCheckboxState.UNKNOWN, find
					.getCurrentState());

			textDoc.save(ResourceUtilities
					.newTestOutputFile(testFileName + "_OUT.odt"));
		} catch (Exception e) {
			Logger.getLogger(CheckBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testSetValue() {
		try {
            String testFileName = "TestSetCheckBoxValue";
            createForm(testFileName + ".odt");
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(testFileName + ".odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = CheckBox.getSimpleIterator(form);
			CheckBox find = null;
			while (iterator.hasNext()) {
				CheckBox btn = (CheckBox) iterator.next();
				if (btn.getName().equals("CheckBox 1")) {
					find = btn;
					break;
				}
			}
			Assert.assertNotNull(find);
			// validate
			Assert.assertEquals("1", find.getValue());

			find.setValue("15");
			Assert.assertEquals("15", find.getValue());

			textDoc.save(ResourceUtilities
                .newTestOutputFile(testFileName + "_OUT.odt"));
		} catch (Exception e) {
			Logger.getLogger(CheckBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

}
