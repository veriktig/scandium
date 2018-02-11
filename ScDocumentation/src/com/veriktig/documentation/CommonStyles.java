/*
 * Copyright 2018 Veriktig, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.veriktig.documentation;

import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.text.Paragraph;

import java.io.File;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.element.office.OfficeAutomaticStylesElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeDocumentStylesElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeFontFaceDeclsElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeMasterStylesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFontFaceElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.odftoolkit.odfdom.dom.element.style.StylePageLayoutElement;
import org.odftoolkit.odfdom.dom.element.style.StylePageLayoutPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleParagraphPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.text.TextHElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;

import org.w3c.dom.Node;

public class CommonStyles {
	private static final String FONT = "Source Sans Pro";
	private static final String CODE = "Source Code Pro";
	private static TextDocument outputDocument = null;
	private static OdfContentDom contentDom = null;
	private static OdfStylesDom stylesDom = null;
	private static boolean indented = false;
	
	public static void init(TextDocument doc) {
		outputDocument = doc;
		try {
			contentDom = outputDocument.getContentDom();
			stylesDom = outputDocument.getStylesDom();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean indent() {
		indented = true;
		return indented;
	}
	
	public static boolean unindent() {
		indented = false;
		return indented;
	}

    public static void addStyles() {
        // Replace font-face-decl in content.xml
        OfficeFontFaceDeclsElement offde = new OfficeFontFaceDeclsElement(contentDom);
        StyleFontFaceElement sffe = offde.newStyleFontFaceElement(FONT);
        sffe.setSvgFontFamilyAttribute(FONT);
        sffe = offde.newStyleFontFaceElement(CODE);
        sffe.setSvgFontFamilyAttribute(CODE);
        sffe.setStyleFontPitchAttribute("fixed");
        Node documentContent = contentDom.getFirstChild();
        Node fontFaceDecl = OdfElement.findFirstChildNode(offde.getClass(), documentContent);
        documentContent.replaceChild(offde, fontFaceDecl);


        // Replace font-face-decl in styles.xml
        offde = new OfficeFontFaceDeclsElement(stylesDom);
        sffe = offde.newStyleFontFaceElement(FONT);
        sffe.setSvgFontFamilyAttribute(FONT);
        sffe = offde.newStyleFontFaceElement(CODE);
        sffe.setSvgFontFamilyAttribute(CODE);
        sffe.setStyleFontPitchAttribute("fixed");
        documentContent = stylesDom.getFirstChild();
        fontFaceDecl = OdfElement.findFirstChildNode(offde.getClass(), documentContent);
        documentContent.replaceChild(offde, fontFaceDecl);
        
        OdfOfficeAutomaticStyles contentAutoStyles = contentDom.getOrCreateAutomaticStyles();

        addPageStyles(stylesDom);
        addParagraphStyles(contentAutoStyles);
        addTextStyles(contentAutoStyles);
    }

    public static void addPageStyles(OdfStylesDom stylesDom) {
    	StylePageLayoutElement style;
    	
    	Node documentContent = stylesDom.getFirstChild();
        OfficeDocumentStylesElement styleRoot = stylesDom.getRootElement();
        
        OfficeAutomaticStylesElement styleAutoStyles = styleRoot.newOfficeAutomaticStylesElement();
        Node autoNode = OdfElement.findFirstChildNode(styleAutoStyles.getClass(), documentContent);
        styleAutoStyles = (OfficeAutomaticStylesElement) autoNode;
        
        style = styleAutoStyles.newStylePageLayoutElement("LetterLayout");
    	style.setProperty(StylePageLayoutPropertiesElement.MarginTop, "0.54in");
    	style.setProperty(StylePageLayoutPropertiesElement.MarginBottom, "0.54in");
    	style.setProperty(StylePageLayoutPropertiesElement.MarginLeft, "0.96in");
    	style.setProperty(StylePageLayoutPropertiesElement.MarginRight, "1.04in");
    	style.setProperty(StylePageLayoutPropertiesElement.PageHeight, "11.0in");
    	style.setProperty(StylePageLayoutPropertiesElement.PageWidth, "8.5in");
    	style.setProperty(StylePageLayoutPropertiesElement.PrintOrientation, "portrait");
    	
        style = styleAutoStyles.newStylePageLayoutElement("LetterLayoutLeft");
        style.setStylePageUsageAttribute("left");
    	style.setProperty(StylePageLayoutPropertiesElement.MarginTop, "0.54in");
    	style.setProperty(StylePageLayoutPropertiesElement.MarginBottom, "0.54in");
    	style.setProperty(StylePageLayoutPropertiesElement.MarginLeft, "0.96in");
    	style.setProperty(StylePageLayoutPropertiesElement.MarginRight, "1.04in");
    	style.setProperty(StylePageLayoutPropertiesElement.PageHeight, "11.0in");
    	style.setProperty(StylePageLayoutPropertiesElement.PageWidth, "8.5in");
    	style.setProperty(StylePageLayoutPropertiesElement.PrintOrientation, "portrait");
    	 
        style = styleAutoStyles.newStylePageLayoutElement("LetterLayoutRight");
        style.setStylePageUsageAttribute("right");
    	style.setProperty(StylePageLayoutPropertiesElement.MarginTop, "0.54in");
    	style.setProperty(StylePageLayoutPropertiesElement.MarginBottom, "0.54in");
    	style.setProperty(StylePageLayoutPropertiesElement.MarginLeft, "1.04in");
    	style.setProperty(StylePageLayoutPropertiesElement.MarginRight, "0.86in");
    	style.setProperty(StylePageLayoutPropertiesElement.PageHeight, "11.0in");
    	style.setProperty(StylePageLayoutPropertiesElement.PageWidth, "8.5in");
    	style.setProperty(StylePageLayoutPropertiesElement.PrintOrientation, "portrait");
    	
    	OfficeMasterStylesElement masterStyles = styleRoot.newOfficeMasterStylesElement();
    	Node masterNode = OdfElement.findFirstChildNode(masterStyles.getClass(), documentContent);
    	masterStyles = (OfficeMasterStylesElement) masterNode;
    	
    	StyleMasterPageElement standard = masterStyles.newStyleMasterPageElement("", "");
    	Node standardNode = OdfElement.findFirstChildNode(standard.getClass(), masterNode);
    	standard  = (StyleMasterPageElement) standardNode;
    	String name = standard.getStyleNameAttribute();
    	if (name.equals("Standard")) {
    		standard.setStylePageLayoutNameAttribute("LetterLayout");
    	} else {
    		System.err.println("Standard is not the first child of master-style.");
    	}
    	
    	// XXX Need Header and Footer support here too.
    	
    	// Need to change Left Page and Right Page?
    	//master = masterStyles.newStyleMasterPageElement("Left Page", "LetterLayoutLeft");
    	//master = masterStyles.newStyleMasterPageElement("Right Page", "LetterLayoutRight");
    }
    
    public static void addParagraphStyles(OdfOfficeAutomaticStyles contentAutoStyles) {
    	OdfStyle style;

        style = contentAutoStyles.newStyle(OdfStyleFamily.Paragraph);
        style.setStyleNameAttribute("P_NAME_HEADER_PAGE");
        style.setProperty(StyleParagraphPropertiesElement.BreakBefore, "page");
        style.setProperty(StyleParagraphPropertiesElement.PageNumber, "auto");
        style.setProperty(StyleParagraphPropertiesElement.Padding, "0.025in");
        style.setProperty(StyleParagraphPropertiesElement.BorderLeft, "none");
        style.setProperty(StyleParagraphPropertiesElement.BorderRight, "none");
        style.setProperty(StyleParagraphPropertiesElement.BorderTop, "0.02in solid #000000");
        style.setProperty(StyleParagraphPropertiesElement.BorderBottom, "none");
        style.setProperty(StyleParagraphPropertiesElement.Shadow, "none");
        style.setProperty(StyleTextPropertiesElement.FontName, FONT);
        style.setProperty(StyleTextPropertiesElement.FontWeight, "bold");
        style.setProperty(StyleTextPropertiesElement.FontWeightAsian, "bold");
        style.setProperty(StyleTextPropertiesElement.FontWeightComplex, "bold");
        style.setProperty(StyleTextPropertiesElement.FontSize, "14pt");
        style.setProperty(StyleTextPropertiesElement.FontSizeAsian, "14pt");
        style.setProperty(StyleTextPropertiesElement.FontSizeComplex, "14pt");        

        style = contentAutoStyles.newStyle(OdfStyleFamily.Paragraph);
        style.setStyleNameAttribute("P_NAME_HEADER_PARAGRAPH");
        style.setProperty(StyleParagraphPropertiesElement.BreakBefore, "paragraph");
        style.setProperty(StyleParagraphPropertiesElement.PageNumber, "auto");
        style.setProperty(StyleParagraphPropertiesElement.Padding, "0.025in");
        style.setProperty(StyleParagraphPropertiesElement.BorderLeft, "none");
        style.setProperty(StyleParagraphPropertiesElement.BorderRight, "none");
        style.setProperty(StyleParagraphPropertiesElement.BorderTop, "0.02in solid #000000");
        style.setProperty(StyleParagraphPropertiesElement.BorderBottom, "none");
        style.setProperty(StyleParagraphPropertiesElement.Shadow, "none");
        style.setProperty(StyleTextPropertiesElement.FontName, FONT);
        style.setProperty(StyleTextPropertiesElement.FontWeight, "bold");
        style.setProperty(StyleTextPropertiesElement.FontWeightAsian, "bold");
        style.setProperty(StyleTextPropertiesElement.FontWeightComplex, "bold");
        style.setProperty(StyleTextPropertiesElement.FontSize, "14pt");
        style.setProperty(StyleTextPropertiesElement.FontSizeAsian, "14pt");
        style.setProperty(StyleTextPropertiesElement.FontSizeComplex, "14pt");
      
        style = contentAutoStyles.newStyle(OdfStyleFamily.Paragraph);
        style.setStyleNameAttribute("P_HEADER");
        style.setProperty(StyleTextPropertiesElement.FontName, FONT);
        style.setProperty(StyleTextPropertiesElement.FontWeight, "bold");
        style.setProperty(StyleTextPropertiesElement.FontWeightAsian, "bold");
        style.setProperty(StyleTextPropertiesElement.FontWeightComplex, "bold");
        style.setProperty(StyleTextPropertiesElement.FontSize, "14pt");
        style.setProperty(StyleTextPropertiesElement.FontSizeAsian, "14pt");
        style.setProperty(StyleTextPropertiesElement.FontSizeComplex, "14pt");
        
        style = contentAutoStyles.newStyle(OdfStyleFamily.Paragraph);
        style.setStyleNameAttribute("P_HEADER_INDENTED");
        style.setProperty(StyleParagraphPropertiesElement.MarginLeft, "0.4925in");
        style.setProperty(StyleParagraphPropertiesElement.MarginRight, "0in");
        style.setProperty(StyleParagraphPropertiesElement.TextIndent, "0in");
        style.setProperty(StyleParagraphPropertiesElement.AutoTextIndent, "false");
        style.setProperty(StyleTextPropertiesElement.FontName, FONT);
        style.setProperty(StyleTextPropertiesElement.FontWeight, "bold");
        style.setProperty(StyleTextPropertiesElement.FontWeightAsian, "bold");
        style.setProperty(StyleTextPropertiesElement.FontWeightComplex, "bold");
        style.setProperty(StyleTextPropertiesElement.FontSize, "14pt");
        style.setProperty(StyleTextPropertiesElement.FontSizeAsian, "14pt");
        style.setProperty(StyleTextPropertiesElement.FontSizeComplex, "14pt");

        
        style = contentAutoStyles.newStyle(OdfStyleFamily.Paragraph);
        style.setStyleNameAttribute("P_DEFAULT");
        style.setProperty(StyleTextPropertiesElement.FontName, FONT);
        style.setProperty(StyleTextPropertiesElement.FontSize, "10pt");
        style.setProperty(StyleTextPropertiesElement.FontSizeAsian, "10pt");
        style.setProperty(StyleTextPropertiesElement.FontSizeComplex, "10pt");
        
        style = contentAutoStyles.newStyle(OdfStyleFamily.Paragraph);
        style.setStyleNameAttribute("P_DEFAULT_INDENTED");
        style.setProperty(StyleParagraphPropertiesElement.MarginLeft, "0.4925in");
        style.setProperty(StyleParagraphPropertiesElement.MarginRight, "0in");
        style.setProperty(StyleParagraphPropertiesElement.TextIndent, "0in");
        style.setProperty(StyleParagraphPropertiesElement.AutoTextIndent, "false");
        style.setProperty(StyleTextPropertiesElement.FontName, FONT);
        style.setProperty(StyleTextPropertiesElement.FontSize, "10pt");
        style.setProperty(StyleTextPropertiesElement.FontSizeAsian, "10pt");
        style.setProperty(StyleTextPropertiesElement.FontSizeComplex, "10pt");

        style = contentAutoStyles.newStyle(OdfStyleFamily.Paragraph);
        style.setStyleNameAttribute("P_FIXED");
        style.setProperty(StyleTextPropertiesElement.FontName, CODE);
        style.setProperty(StyleTextPropertiesElement.FontSize, "10pt");
        style.setProperty(StyleTextPropertiesElement.FontSizeAsian, "10pt");
        style.setProperty(StyleTextPropertiesElement.FontSizeComplex, "10pt");

        style = contentAutoStyles.newStyle(OdfStyleFamily.Paragraph);
        style.setStyleNameAttribute("P_FIXED_INDENTED");
        style.setProperty(StyleParagraphPropertiesElement.MarginLeft, "0.4925in");
        style.setProperty(StyleParagraphPropertiesElement.MarginRight, "0in");
        style.setProperty(StyleParagraphPropertiesElement.TextIndent, "0in");
        style.setProperty(StyleParagraphPropertiesElement.AutoTextIndent, "false");
        style.setProperty(StyleTextPropertiesElement.FontName, CODE);
        style.setProperty(StyleTextPropertiesElement.FontSize, "10pt");
        style.setProperty(StyleTextPropertiesElement.FontSizeAsian, "10pt");
        style.setProperty(StyleTextPropertiesElement.FontSizeComplex, "10pt");
    }
     
    public static void addTextStyles(OdfOfficeAutomaticStyles contentAutoStyles) {
    	OdfStyle style;
    	
        style = contentAutoStyles.newStyle(OdfStyleFamily.Text);
        style.setStyleNameAttribute("T_BOLD");
        style.setProperty(StyleTextPropertiesElement.FontWeight, "bold");
        style.setProperty(StyleTextPropertiesElement.FontWeightAsian, "bold");
        style.setProperty(StyleTextPropertiesElement.FontWeightComplex, "bold");

        style = contentAutoStyles.newStyle(OdfStyleFamily.Text);
        style.setStyleNameAttribute("T_NORMAL");
        style.setProperty(StyleTextPropertiesElement.FontWeight, "normal");
        style.setProperty(StyleTextPropertiesElement.FontWeightAsian, "normal");
        style.setProperty(StyleTextPropertiesElement.FontWeightComplex, "normal");

        style = contentAutoStyles.newStyle(OdfStyleFamily.Text);
        style.setStyleNameAttribute("T_ITALIC");
        style.setProperty(StyleTextPropertiesElement.FontStyle, "italic");
        style.setProperty(StyleTextPropertiesElement.FontStyleAsian, "italic");
        style.setProperty(StyleTextPropertiesElement.FontStyleComplex, "italic");

        style = contentAutoStyles.newStyle(OdfStyleFamily.Text);
        style.setStyleNameAttribute("T_BOLDITALIC");
        style.setProperty(StyleTextPropertiesElement.FontWeight, "bold");
        style.setProperty(StyleTextPropertiesElement.FontWeightAsian, "bold");
        style.setProperty(StyleTextPropertiesElement.FontWeightComplex, "bold");
        style.setProperty(StyleTextPropertiesElement.FontStyle, "italic");
        style.setProperty(StyleTextPropertiesElement.FontStyleAsian, "italic");
        style.setProperty(StyleTextPropertiesElement.FontStyleComplex, "italic");

        style = contentAutoStyles.newStyle(OdfStyleFamily.Text);
        style.setStyleNameAttribute("T_FIXEDBOLD");
        style.setProperty(StyleTextPropertiesElement.FontName, CODE);
        style.setProperty(StyleTextPropertiesElement.FontWeight, "bold");
        style.setProperty(StyleTextPropertiesElement.FontWeightAsian, "bold");
        style.setProperty(StyleTextPropertiesElement.FontWeightComplex, "bold");
    }

    public static int createSection(int currentParagraph, boolean pagebreak, String section) {
        TextHElement the = new TextHElement(contentDom);
        the.setTextContent(section);
        the.setTextOutlineLevelAttribute(1);
        if (pagebreak) {
        	the.setStyleName("P_NAME_HEADER_PAGE");
        } else {
        	the.setStyleName("P_NAME_HEADER_PARAGRAPH");
        }
        Paragraph paragraph = Paragraph.getInstanceof(the);
        currentParagraph = CommonStyles.addParagraph(currentParagraph, paragraph);
        currentParagraph = CommonStyles.addEmptyParagraph(currentParagraph);
        return currentParagraph;
    }
    
    public static int createHeader(int currentParagraph, String heading) {
        TextHElement the = new TextHElement(contentDom);
        the.setTextContent(heading);
        the.setTextOutlineLevelAttribute(2);
        the.setStyleName(indented ? "P_HEADER_INDENTED" : "P_HEADER");
        Paragraph paragraph = Paragraph.getInstanceof(the);
        currentParagraph = CommonStyles.addParagraph(currentParagraph, paragraph);
        currentParagraph = CommonStyles.addEmptyParagraph(currentParagraph);
        return currentParagraph;
    }
    
    public static int createName(int currentParagraph, String name, String summary) {
        TextPElement tpe = new TextPElement(contentDom);
        tpe.setStyleName(indented ? "P_DEFAULT_INDENTED" : "P_DEFAULT");
        TextSpanElement tse = tpe.newTextSpanElement();
        tse.setTextContent(name);
        tse.setStyleName("T_BOLD");
        TextSpanElement tse2 = tpe.newTextSpanElement();
        tse2.setTextContent(summary);
        tse2.setStyleName("T_NORMAL");
        Paragraph paragraph = Paragraph.getInstanceof(tpe);
        currentParagraph = CommonStyles.addParagraph(currentParagraph, paragraph);
        return currentParagraph;
    }
    
    public static int createDescription(int currentParagraph, String desc) {
        TextPElement tpe = new TextPElement(contentDom);
        tpe.setStyleName(indented ? "P_DEFAULT_INDENTED" : "P_DEFAULT");
        TextSpanElement tse = tpe.newTextSpanElement();
        tse.setTextContent(desc);
        tse.setStyleName("T_NORMAL");
        Paragraph paragraph = Paragraph.getInstanceof(tpe);
        currentParagraph = CommonStyles.addParagraph(currentParagraph, paragraph);
        return currentParagraph;
    }

    public static int createSeeAlso(int currentParagraph, String see_also) {
        TextPElement tpe = new TextPElement(contentDom);
        tpe.setStyleName(indented ? "P_DEFAULT_INDENTED" : "P_DEFAULT");
        TextSpanElement tse = tpe.newTextSpanElement();
        tse.setTextContent(see_also);
        tse.setStyleName("T_BOLD");
        Paragraph paragraph = Paragraph.getInstanceof(tpe);
        currentParagraph = CommonStyles.addParagraph(currentParagraph, paragraph);
        return currentParagraph;
    }
    
    public static int addParagraph(int currentParagraph, Paragraph pp) {
        Paragraph ref = outputDocument.getParagraphByIndex(currentParagraph, false);
        outputDocument.insertParagraph(ref, pp, false);
        return ++currentParagraph;
    }

    public static int addEmptyParagraph(int currentParagraph) {
        TextPElement tpe = new TextPElement(contentDom);
        tpe.setStyleName("P_DEFAULT");
        tpe.setTextContent("");
        Paragraph paragraph = Paragraph.getInstanceof(tpe);
        return (addParagraph(currentParagraph, paragraph));
    }
    
    public static void finish(File file) throws FactoryException {
        // Hack to remove the empty first paragraph
        Paragraph ref = outputDocument.getParagraphByIndex(0, false);
        Node empty = ref.getOdfElement();
        Node parent = empty.getParentNode();
        parent.removeChild(empty);
        
        try {
            outputDocument.save(file);
        } catch (Exception e) {
            throw new FactoryException(e);
        }
    }
}
