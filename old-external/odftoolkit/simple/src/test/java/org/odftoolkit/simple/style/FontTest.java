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

package org.odftoolkit.simple.style;

import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.TextLinePosition;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.CellStyleHandler;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class FontTest {

	static final String filename = "testGetCellAt.ods";

	@Test
	public void testGetSetFont() {
		Font font1Base = new Font("Arial", FontStyle.ITALIC, 10, Color.BLACK, TextLinePosition.THROUGH);
		Font font2Base = new Font("'Times New Roman'", FontStyle.REGULAR, (float) 13.95, new Color("#ff3333"),
				TextLinePosition.THROUGHUNDER);
		Font font3Base = new Font("SimSun", FontStyle.BOLD, 8, Color.BLACK, TextLinePosition.REGULAR);
		Font font4Base = new Font("Arial", FontStyle.REGULAR, 10, Color.BLACK, TextLinePosition.UNDER);
		try {
			SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(filename));
			Table table = doc.getTableByName("A");
			Cell cell1 = table.getCellByPosition("A2");
			CellStyleHandler handler1 = cell1.getStyleHandler();
			Font font1 = handler1.getFont(Document.ScriptType.WESTERN);
			Assert.assertEquals(font1Base, font1);

			Cell cell2 = table.getCellByPosition("A3");
			CellStyleHandler handler2 = cell2.getStyleHandler();
			Font font2 = handler2.getFont(Document.ScriptType.WESTERN);
			Assert.assertEquals(font2Base, font2);

			Cell cell3 = table.getCellByPosition("A4");
			CellStyleHandler handler3 = cell3.getStyleHandler();
			Font font3 = handler3.getFont(Document.ScriptType.CJK);
			Assert.assertEquals(font3Base, font3);
			Font font4 = handler3.getFont(Document.ScriptType.WESTERN);
			Assert.assertEquals(font4Base, font4);

			Cell cell5 = table.getCellByPosition("B2");
			cell5.getStyleHandler().setFont(font1Base);
			cell5.setStringValue("Arial Italic black 10");
			Assert.assertEquals(font1Base, cell5.getStyleHandler().getFont(Document.ScriptType.WESTERN));

			Cell cell6 = table.getCellByPosition("B3");
			// font2Base.setLocale();
			cell6.getStyleHandler()
					.setFont(font2Base, new Locale(Locale.ENGLISH.getLanguage(), Locale.US.getCountry()));
			cell6.setStringValue("Times New Roman, Regular, 13.9, Red");
			Assert.assertEquals(font2Base, cell6.getStyleHandler().getFont(Document.ScriptType.WESTERN));

			Cell cell7 = table.getCellByPosition("B4");
			cell7.getStyleHandler().setFont(font3Base,
					new Locale(Locale.CHINESE.getLanguage(), Locale.CHINA.getCountry()));
			cell7.getStyleHandler()
					.setFont(font4Base, new Locale(Locale.ENGLISH.getLanguage(), Locale.US.getCountry()));
			cell7.setStringValue("SimSun BOLD 8 BLACK");
			Assert.assertEquals(font3Base, cell7.getStyleHandler().getFont(Document.ScriptType.CJK));
			Assert.assertEquals(font4Base, cell7.getStyleHandler().getFont(Document.ScriptType.WESTERN));

			doc.save(ResourceUtilities.newTestOutputFile("testFontOutput.ods"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testGetSetFontInODT() throws Exception {
		TextDocument doc = TextDocument.newTextDocument();
		Table table = Table.newTable(doc);
		Font font = table.getCellByPosition(0, 0).getFont();
		Assert.assertNotNull(font);
	}

	// public StyleTypeDefinitions.SimpleFontStyle getFontStyle(
	// Document.ScriptType type);
	//
	// public void setFontStyle(StyleTypeDefinitions.SimpleFontStyle style);
	//
	// public void setFontStyle(StyleTypeDefinitions.SimpleFontStyle style,
	// Document.ScriptType type);
	//
	// public int getFontSizeInPoint();
	//
	// public int getFontSizeInPoint(Document.ScriptType type);
	//
	// public void setFontSizeInPoint(int size);
	//
	// public void setFontSizeInPoint(int size, Document.ScriptType type);
	//
	// public String getLanguage();
	//
	// public String getLanguage(Document.ScriptType type);
	//
	// public void setLanguage(String language);
	//
	// public void setLanguage(String language, Document.ScriptType type);
	//
	// public String getCountry();
	//
	// public String getCountry(Document.ScriptType type);
	//
	// public void setCountry(String country);
	//
	// public void setCountry(String country, Document.ScriptType type);
	//
	// public Color getFontColor();
	//
	// public void setFontColor(Color fontColor);
	//
	// public String getFontName();
	//
	// public String getFontName(Document.ScriptType type);
	//
	// public void setFontName(String fontName);
	//
	// public void setFontName(String fontName, Document.ScriptType type);
	//
	// public Font getFont();
	//
	// public Font getFont(Document.ScriptType type);
	//
	// public void setFont(Font font);
	//
	// public String getFontFamilyName(Document.ScriptType type);
	
	@Test
	public void testNewFont1() {
		try {
			SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
			Table table = document.getTableByName("Sheet1");
			Font font = new Font("Arial", StyleTypeDefinitions.FontStyle.ITALIC, 17.5);
			Cell cell = table.getCellByPosition("A1");
			cell.setFont(font);
			cell.setStringValue("Hello world .");
			
			//save
			document.save(ResourceUtilities.newTestOutputFile("testFontOutput1.ods"));
			
			//validate
			SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("testFontOutput1.ods"));
			Table table1 = doc.getTableByName("Sheet1");
			Cell cell1 = table1.getCellByPosition("A1");
			Font font1 = cell1.getFont();
			Assert.assertEquals(font.getFamilyName(), font1.getFamilyName());
			Assert.assertEquals(font.getSize(), font1.getSize());
			Assert.assertEquals(font.getFontStyle(), font1.getFontStyle());
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	
	@Test
	public void testNewFont2() {
		try {
			SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
			Table table = document.getTableByName("Sheet1");
			Font font = new Font("Arial", StyleTypeDefinitions.FontStyle.ITALIC, 17.5, Color.BLUE);
			Cell cell = table.getCellByPosition("A1");
			cell.setFont(font);
			cell.setStringValue("Hello world .");
			
			//save
			document.save(ResourceUtilities.newTestOutputFile("testFontOutput1.ods"));
			
			//validate
			SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("testFontOutput1.ods"));
			Table table1 = doc.getTableByName("Sheet1");
			Cell cell1 = table1.getCellByPosition("A1");
			Font font1 = cell1.getFont();
			Assert.assertEquals(font.getFamilyName(), font1.getFamilyName());
			Assert.assertEquals(font.getSize(), font1.getSize());
			Assert.assertEquals(font.getFontStyle(), font1.getFontStyle());
			Assert.assertEquals(font.getColor().toString(), font1.getColor().toString());
			Assert.assertEquals(font, font1);
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	
	@Test
	public void testNewFont3() {
		try {
			SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
			Table table = document.getTableByName("Sheet1");
			Font font = new Font("Arial", StyleTypeDefinitions.FontStyle.ITALIC, 17.5, Locale.ENGLISH);
			Cell cell = table.getCellByPosition("A1");
			cell.setFont(font);
			cell.setStringValue("Hi World.");
			
			//save
			document.save(ResourceUtilities.newTestOutputFile("testFontOutput1.ods"));
			
			//validate
			SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("testFontOutput1.ods"));
			Table table1 = doc.getTableByName("Sheet1");
			Cell cell1 = table1.getCellByPosition("A1");
			Font font1 = cell1.getFont();
			Assert.assertEquals(font.getFamilyName(), font1.getFamilyName());
			Assert.assertEquals(font.getSize(), font1.getSize());
			Assert.assertEquals(font.getFontStyle(), font1.getFontStyle());
			Assert.assertEquals(font.getTextLinePosition(), font1.getTextLinePosition());
		//	Assert.assertEquals(font, font1);
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	
	@Test
	public void testNewFont4() {
		try {
			SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
			Table table = document.getTableByName("Sheet1");
			Font font = new Font("Arial", StyleTypeDefinitions.FontStyle.ITALIC, 17.5, Color.GREEN, Locale.ENGLISH);
			Cell cell = table.getCellByPosition("A1");
			cell.setFont(font);
			cell.setStringValue("Hi World.");
			
			//save
			document.save(ResourceUtilities.newTestOutputFile("testFontOutput1.ods"));
			
			//validate
			SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("testFontOutput1.ods"));
			Table table1 = doc.getTableByName("Sheet1");
			Cell cell1 = table1.getCellByPosition("A1");
			Font font1 = cell1.getFont();
			Assert.assertEquals(font.getFamilyName(), font1.getFamilyName());
			Assert.assertEquals(font.getSize(), font1.getSize());
			Assert.assertEquals(font.getFontStyle(), font1.getFontStyle());
			Assert.assertEquals(font.getTextLinePosition(), font1.getTextLinePosition());
			Assert.assertEquals(font.getColor().toString(), font1.getColor().toString());
			Assert.assertEquals(font, font1);
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	
	@Test
	public void testNewFont5() {
		try {
			SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
			Table table = document.getTableByName("Sheet1");
			Font font = new Font("Arial", StyleTypeDefinitions.FontStyle.ITALIC, 17.5, Color.GREEN, StyleTypeDefinitions.TextLinePosition.REGULAR, Locale.ENGLISH);
			Cell cell = table.getCellByPosition("A1");
			cell.setFont(font);
			cell.setStringValue("Hi World.");
			
			//save
			document.save(ResourceUtilities.newTestOutputFile("testFontOutput1.ods"));
			
			//validate
			SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("testFontOutput1.ods"));
			Table table1 = doc.getTableByName("Sheet1");
			Cell cell1 = table1.getCellByPosition("A1");
			Font font1 = cell1.getFont();
			Assert.assertEquals(font.getFamilyName(), font1.getFamilyName());
			Assert.assertEquals(font.getSize(), font1.getSize());
			Assert.assertEquals(font.getFontStyle(), font1.getFontStyle());
			Assert.assertEquals(font.getTextLinePosition(), font1.getTextLinePosition());
			Assert.assertEquals(font.getColor().toString(), font1.getColor().toString());
			Assert.assertEquals(font, font1);
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	
	@Test
	public void testEquals() {
		try {
			SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
			Table table = document.getTableByName("Sheet1");
			Font font = new Font("Arial", StyleTypeDefinitions.FontStyle.ITALIC, 17.5, Color.GREEN, StyleTypeDefinitions.TextLinePosition.REGULAR, Locale.ENGLISH);
			Cell cell = table.getCellByPosition("A1");
			cell.setFont(font);
			cell.setStringValue("Hi World.");
			
			//save
			document.save(ResourceUtilities.newTestOutputFile("testFontOutput1.ods"));
			
			//validate
			SpreadsheetDocument doc = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("testFontOutput1.ods"));
			Table table1 = doc.getTableByName("Sheet1");
			Cell cell1 = table1.getCellByPosition("A1");
			Font font1 = cell1.getFont();
			
			Assert.assertTrue(font.equals(font1));
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	
}
