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

package org.odftoolkit.simple.presentation;

import java.awt.Rectangle;
import java.io.InputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.draw.DrawObjectElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageElement;
import org.odftoolkit.odfdom.dom.element.office.OfficePresentationElement;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.PresentationDocument;
import org.odftoolkit.simple.PresentationDocument.PresentationClass;
import org.odftoolkit.simple.chart.Chart;
import org.odftoolkit.simple.draw.Textbox;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.table.TableContainer;
import org.odftoolkit.simple.text.list.BulletDecorator;
import org.odftoolkit.simple.text.list.ImageDecorator;
import org.odftoolkit.simple.text.list.List;
import org.odftoolkit.simple.text.list.ListContainer;
import org.odftoolkit.simple.text.list.ListDecorator;
import org.odftoolkit.simple.text.list.NumberDecorator;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Test for Presentation Slide API, including get/move/insert/delete slide in
 * one presentation and the slides operation of two presentations.
 */
public class SlideTest {

	private static final Logger LOG = Logger.getLogger(SlideTest.class.getName());
	PresentationDocument doc;
	PresentationDocument doc2;
	final String TEST_PRESENTATION_FILE_MAIN = "Presentation1.odp";
	final String TEST_PRESENTATION_FILE_ANOTHER = "Presentation2.odp";
	final String TEST_PRESENTATION_FILE_OUT_PREFIX = "SlideResult";
	final String TEST_PRESENTATION_DOCUMENT1 = "SlideTest1.odp";
	final String TEST_PRESENTATION_DOCUMENT2 = "SlideTest2.odp";
	final String TEST_PRESENTATION_DOCUMENT3 = "SlideTest3.odp";

	/**
	 * Initialize the test case.
	 */
	public SlideTest() {
	}

	/**
	 * Test case for get presentation slide, including get the slide count, get
	 * the slide at the specified position or with the specified name, and get
	 * the collection of slide.
	 * <p>
	 * <b>Precondition</b> Load Presentation File at
	 * test\resources\performance\Presentation1.odp <b>Method</b> 1)By using
	 * PresentationDocument.getSlideCount() to Get the number of the slides in
	 * this presentation. 2)According to the slide count, get a slide at the
	 * specific position of the loaded presentation document using
	 * PresentationDocument.getSlideByIndex(int index), and the param "index"
	 * should be limited with the slide count. Use OdfDrawPage.getSlideIndex()
	 * method to verify the returned slide.
	 * 3)PresentationDocument.getSlideByName(String name) 4)Using
	 * dfPresentationDocument.getSlides() to get the slide iterator <b>Covered
	 * Element</b> &lt;office:presentation&gt;,&lt;draw:page&gt; <b>Note</b>
	 * PresentationDocument.getContentRoot override Document.getContentRoot
	 * method will return OfficePresentationElement, rather than
	 * OdfOfficePresentation because of the inheritance relationship broken up
	 * between dom and doc element And because OdfOfficePresentation is useless,
	 * so I delete this class <b>Code Coverage Result</b> 1)can not catch
	 * Exception because it is only used to catch the exception thrown by
	 * getContentRoot()
	 */
	@Test
	public void testGetSlide() {
		try {
			doc = PresentationDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_PRESENTATION_FILE_MAIN));
			int slideCount = doc.getSlideCount();
			Assert.assertTrue(10 == slideCount);
			Slide page2 = doc.getSlideByIndex(2);
			Assert.assertTrue(2 == page2.getSlideIndex());
			Slide slide3 = doc.getSlideByName("Slide 3");
			Assert.assertNull(slide3);
			slide3 = doc.getSlideByName("page3");
			Assert.assertEquals(page2, slide3);
			Iterator<Slide> slideIter = doc.getSlides();
			int i = 0;
			while (slideIter.hasNext()) {
				Slide slide = slideIter.next();
				Assert.assertTrue(i == slide.getSlideIndex());
				String name = "page" + (i + 1);
				Assert.assertTrue(name.equals(slide.getSlideName()));
				i++;
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}

		Slide slidesNoName = doc.getSlideByName(null);
		Assert.assertNull(slidesNoName);
		Slide nullSlide = doc.getSlideByIndex(20);
		Assert.assertNull(nullSlide);
		nullSlide = doc.getSlideByIndex(-1);
		Assert.assertNull(nullSlide);

	}

	/**
	 * Test case for get/set slide name to make sure the name is unique in the
	 * whole presentation document. If the loaded presentation document is not
	 * valid that contain the same slide name, we should modified the duplicate
	 * slide name to make them unique.
	 * <p>
	 * <b>Precondition</b> Load Presentation File at
	 * test\resources\performance\Presentation1.odp Using the dom method to make
	 * slide index 4 and 8 have the same name, and delete the "draw:name"
	 * attribute of slide 7. <b>Method</b> 1)By accessing any slide API to
	 * trigger the duplicate slide name check method. After this operation,
	 * slide 8 will change the slide name. 2)Trigger OdfDrawPage.getSlideName()
	 * to make the slide 7 has the unique slide name. 3)Using
	 * OdfDrawPage.setSlideName() to give the slide 1 with the new name.
	 */
	@Test
	public void testSlideName() {
		try {
			doc = PresentationDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_PRESENTATION_FILE_MAIN));
			OfficePresentationElement contentRoot = doc.getContentRoot();
			NodeList slideNodes = contentRoot.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
			DrawPageElement slideEle4 = (DrawPageElement) slideNodes.item(4);
			Assert.assertEquals(slideEle4.getDrawNameAttribute(), "page5");
			DrawPageElement slideEle8 = (DrawPageElement) slideNodes.item(8);
			slideEle8.setDrawNameAttribute("page5");
			Slide slide7 = doc.getSlideByIndex(7);
			DrawPageElement slideEle7 = (DrawPageElement) slideNodes.item(7);
			slideEle7.removeAttributeNS(OdfDocumentNamespace.DRAW.getUri(), "name");

			Slide slide4 = doc.getSlideByIndex(4);
			Assert.assertTrue(slide4.getSlideName().equals("page5"));
			Slide slide8 = doc.getSlideByIndex(8);
			Assert.assertFalse(slide8.getSlideName().equals("page5"));

			Assert.assertTrue(slide7.getSlideName().startsWith("page8"));
			Notes note7 = slide7.getNotesPage();
			note7.addText("This is slide at index" + slide7.getSlideIndex() + " named " + slide7.getSlideName());

			Slide slide1 = doc.getSlideByIndex(1);
			slide1.setSlideName("haha");
			slide1.setSlideName("page1");

		} catch (IllegalArgumentException ile) {
			Slide slide1 = doc.getSlideByIndex(1);
			Assert.assertTrue("the given name page1 is duplicate with the previous slide", slide1.getSlideName()
					.equals("haha"));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}

		try {
			doc.save(ResourceUtilities.newTestOutputFile(TEST_PRESENTATION_FILE_OUT_PREFIX + "SlideName.odp"));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	/**
	 * Test case for delete presentation slide, including delete slide at the
	 * specified position or with the specified name.
	 * <p>
	 * <b>Precondition</b> Load Presentation File at
	 * test\resources\performance\Presentation1.odp <b>Method</b> 1)By using
	 * PresentationDocument.deleteSlideByIndex(int index) to delete the slide at
	 * the specified position which is present by param "index", then index of
	 * the slide which after the delete slide will change. 2)Delete slide with
	 * the specified name by using PresentationDocument.deleteSlideByName(String
	 * name) <b>PostCondition</b> Save the modified document at
	 * SlideResultDelete.odp <b>Covered Element</b> 1)
	 * &lt;office:presentation&gt;,&lt;draw:page&gt; 2)All the element that
	 * contains "xlink:href" attribute, such as &lt;draw:object&gt;,
	 * &lt;draw:image&gt;, etc. 3)All the style name definition element, include
	 * &lt;style:style&gt;, &lt;text:list-style&gt;, &lt;number:time-style&gt;,
	 * &lt;number:date-style&gt;, &lt;number:boolean-style&gt;,
	 * &lt;number:number-style&gt;, &lt;number:currency-style&gt;,
	 * &lt;number:percentage-style&gt;, &lt;number:text-style&gt; 4)All the
	 * <code>OdfStylableElement</code> which have the style name reference, such
	 * as &lt;draw:frame&gt;, &lt;text:p&gt; etc. <b>Note</b> When the specific
	 * slide is delete, if it referred images, ole or styles are not used by
	 * other slide they will all be removed from the package.
	 */
	@Test
	public void testDeleteSlide() {
		try {
			doc = PresentationDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_PRESENTATION_FILE_MAIN));
			Slide slide2 = doc.getSlideByIndex(2);
			Slide slide3 = doc.getSlideByIndex(3);
			// this slide contains an embed document, remove this slide will
			// also remove the embed document
			int nEmbedDoc = doc.getEmbeddedDocuments().size();
			Assert.assertTrue(doc.deleteSlideByIndex(2));
			// slide3 is no longer exist
			Assert.assertTrue(-1 == slide2.getSlideIndex());
			Assert.assertTrue(2 == slide3.getSlideIndex());
			Assert.assertTrue(doc.getEmbeddedDocuments().size() == (nEmbedDoc - 1));
			Assert.assertTrue(doc.deleteSlideByName("page5"));
			int count = doc.getSlideCount();
			Assert.assertTrue(8 == count);
			// slide at index 9 contains two images and one embed document, the
			// embed document aslo have the object replacement image
			// remove this slide will also remove these three images
			String IMAGE_NAME_1 = "Pictures/10000000000002580000018FB151A5C8.jpg";
			String IMAGE_NAME_2 = "Pictures/1000000000000C80000004009305DCA3.jpg";
			String IMAGE_NAME_3 = "ObjectReplacements/Object 13";
			int nImageCnt = OdfDrawImage.getImageCount(doc);
			InputStream imageStream1 = doc.getPackage().getInputStream(IMAGE_NAME_1);
			Assert.assertNotNull(imageStream1);
			Assert.assertTrue(doc.deleteSlideByIndex(doc.getSlideCount() - 2));
			Assert.assertTrue(OdfDrawImage.getImageCount(doc) == (nImageCnt - 3));
			imageStream1 = doc.getPackage().getInputStream(IMAGE_NAME_1);
			Assert.assertNull(imageStream1);
			InputStream imageStream2 = doc.getPackage().getInputStream(IMAGE_NAME_2);
			Assert.assertNull(imageStream2);
			InputStream imageStream3 = doc.getPackage().getInputStream(IMAGE_NAME_3);
			Assert.assertNull(imageStream3);
			// slide at index 2 at doc2 contains one image, but it also referred
			// by other slides
			// so the image of this slide will not be removed
			doc2 = PresentationDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_PRESENTATION_FILE_ANOTHER));
			nImageCnt = OdfDrawImage.getImageCount(doc2);
			Assert.assertTrue(doc2.deleteSlideByIndex(2));
			Assert.assertTrue(OdfDrawImage.getImageCount(doc2) == (nImageCnt - 1));
			imageStream1 = doc2.getPackage().getInputStream(IMAGE_NAME_1);
			Assert.assertNotNull(imageStream1);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}

		try {
			Assert.assertTrue(doc.deleteSlideByIndex(20));
			Assert.assertTrue(false);
		} catch (IndexOutOfBoundsException iobe) {
			Assert.assertTrue("slide index is out of bounds", true);
		}

		try {
			doc.save(ResourceUtilities.newTestOutputFile(TEST_PRESENTATION_FILE_OUT_PREFIX + "Delete.odp"));
			doc2.save(ResourceUtilities.newTestOutputFile(TEST_PRESENTATION_FILE_OUT_PREFIX + "Delete2.odp"));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	/**
	 * Test case for create new slide at the specified position, with the
	 * specified name and slide template.
	 * <p>
	 * <b>Precondition</b> Load Presentation File at
	 * test\resources\performance\Presentation1.odp <b>Method</b>
	 * PresentationDocument.newSlide(int index, String name,
	 * OdfDrawPage.SlideLayout slideLayout) is used to new a slide at the
	 * specified position with the specified name, and use the specified slide
	 * template. <b>Postcondition</b> Save the modified document at
	 * SlideResultNew.odp <b>Covered Element</b> 1)
	 * &lt;style:presentation-page-layout&gt; referred by
	 * "presentation:presentation-page-layout-name" attribute of
	 * &lt;draw:page&gt; 2) &lt;style:master-page&gt; referred by
	 * "draw:master-page-name" attribute of &lt;draw:page&gt; 3) The placeholder
	 * element of &lt;draw:page&gt;, such as title, outline placeholder
	 * <b>Note</b> The new slide will use the same master page style of the
	 * previous slide, and add the placeholder on the slide according to the
	 * slide template parameter.
	 */
	@Test
	public void testNewSlide() {
		try {
			doc = PresentationDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_PRESENTATION_FILE_MAIN));
			Slide slide5 = doc.getSlideByIndex(5);
			Slide newSlide1 = doc.newSlide(2, "Slide 2 new", Slide.SlideLayout.BLANK);
			Assert.assertTrue(2 == newSlide1.getSlideIndex());
			Slide newSlide2 = doc.newSlide(0, "", Slide.SlideLayout.TITLE_ONLY);
			Assert.assertTrue(newSlide2.getSlideName().equals(""));
			Assert.assertTrue(7 == slide5.getSlideIndex());
			doc.newSlide(3, Slide.SlideLayout.TITLE_PLUS_TEXT.toString(), Slide.SlideLayout.enumValueOf("title_text"));
			Slide newSlide4 = doc.newSlide(4, null, Slide.SlideLayout.TITLE_PLUS_2_TEXT_BLOCK);
			Assert.assertTrue(newSlide4.getOdfElement().getDrawNameAttribute() != null);
			Slide.SlideLayout outlineType = Slide.SlideLayout.TITLE_OUTLINE;
			doc.newSlide(14, Slide.SlideLayout.toString(outlineType), outlineType);
			doc.newSlide(15, "Default", null);
			int count = doc.getSlideCount();
			Assert.assertTrue(16 == count);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}

		try {
			doc.newSlide(20, "Slide 20 new", Slide.SlideLayout.BLANK);
		} catch (IndexOutOfBoundsException iobe) {
			Assert.assertTrue("slide index is out of bounds", true);
		}

		try {
			doc.save(ResourceUtilities.newTestOutputFile(TEST_PRESENTATION_FILE_OUT_PREFIX + "New.odp"));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	/**
	 * Test case for move the slide from one position to another, or copy a
	 * specified slide and insert it to another specified position.
	 * <p>
	 * <b>Precondition</b> Load Presentation File at
	 * test\resources\performance\Presentation1.odp
	 * test\resources\performance\Presentation1.odp <b>Method</b> 1)Using
	 * PresentationDocument.moveSlide(int current, int destination) to move a
	 * slide at the current position to the destination position.
	 * 2)PresentationDocument.copySlide(int source, int dest, String newName) is
	 * used to make a copy of the slide at a specified position to another
	 * position in this presentation. <b>Postcondition</b> Save the modified
	 * document at SlideResultMoveAndCopy.odp <b>Covered Element</b> 1)
	 * &lt;office:presentation&gt;,&lt;draw:page&gt; 2)
	 * &lt;presentation:notes&gt; of each slide. <b>Note</b> Each slide has its
	 * own notes page to show its notes view, while the notes page has the
	 * "draw:page-number" attribute to show the notes view of which slide so
	 * when the slide is moved or copied, the index of this slide will be
	 * change, the notes page have to change the "draw:page-number" value.
	 */
	@Test
	public void testMoveAndCopySlide() {
		try {
			doc = PresentationDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_PRESENTATION_FILE_MAIN));
			Slide lastSlide = doc.getSlideByIndex(9);
			Slide firstSlide = doc.getSlideByIndex(0);

			Slide copyFirstToLastSlide = doc.copySlide(0, 10, firstSlide.getSlideName() + "(copy)");
			Assert.assertTrue(10 == copyFirstToLastSlide.getSlideIndex());

			Slide copyLastToFirstSlide = doc.copySlide(9, 0, lastSlide.getSlideName() + "(copy)");
			Assert.assertTrue(0 == copyLastToFirstSlide.getSlideIndex());

			doc.moveSlide(11, 0);
			doc.moveSlide(1, 12);
			Assert.assertTrue(1 == firstSlide.getSlideIndex());
			Assert.assertTrue(10 == lastSlide.getSlideIndex());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}

		try {
			doc.moveSlide(20, 0);
		} catch (IndexOutOfBoundsException iobe) {
			Assert.assertTrue("slide index is out of bounds", true);
		}

		try {
			doc.moveSlide(-1, 0);
		} catch (IndexOutOfBoundsException iobe) {
			Assert.assertTrue("slide index is out of bounds", true);
		}

		try {
			doc.copySlide(20, 0, "outofbounds");
		} catch (IndexOutOfBoundsException iobe) {
			Assert.assertTrue("slide index is out of bounds", true);
		}

		try {
			doc.copySlide(-1, 0, "outofbounds");
		} catch (IndexOutOfBoundsException iobe) {
			Assert.assertTrue("slide index is out of bounds", true);
		}

		try {
			doc.save(ResourceUtilities.newTestOutputFile(TEST_PRESENTATION_FILE_OUT_PREFIX + "MoveAndCopy.odp"));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	/**
	 * Test case for copy a slide which comes from the other presentation to the
	 * specified position of the current presentation.
	 * <p>
	 * <b>Precondition</b> Load Presentation File at
	 * test\resources\performance\Presentation1.odp &
	 * test\resources\performance\Presentation2.odp <b>Method</b> Using
	 * PresentationDocument.copyForeignSlide(int destIndex,PresentationDocument
	 * srcDoc, int srcIndex) to copy a slide which locates at "srcIndex"
	 * position of the "srcDoc" presentation document and insert it to the
	 * current presentation document at "destIndex" position.
	 * <b>Postcondition</b> Save the modified document at
	 * SlideResultCopyForeignSlide.odp <b>Covered Element</b> 1)
	 * &lt;office:presentation&gt;,&lt;draw:page&gt; 2) All the element that
	 * contains "xlink:href" attribute, such as &lt;draw:object&gt;,
	 * &lt;draw:image&gt;, etc. 3) Each style definition element of
	 * &lt;draw:page&gt; and its all child elements such as layout style, master
	 * page style, object style and text style the style definition element must
	 * contain "style:name" or "draw:name" attribute, include
	 * &lt;style:mater-page
	 * &gt;,&lt;style:page-layout&gt;,&lt;style:presentation-page-layout&gt;,
	 * &lt;style:style&gt;, &lt;text:list-style&gt;, &lt;number:time-style&gt;,
	 * &lt;number:date-style&gt;, &lt;number:boolean-style&gt;,
	 * &lt;number:number-style&gt;, &lt;number:currency-style&gt;,
	 * &lt;number:percentage-style&gt;, &lt;number:text-style&gt;,
	 * &lt;text:outline-style&gt;,&lt;style:font-face&gt;, 4) the element which
	 * refer the style name, that is &lt;draw:page&gt; and all the child
	 * elements. <b>Note</b> If the copied style/image/ole name is duplicated
	 * with destination presentation document, the name definition element and
	 * reference element must be renamed then insert to the dest document.
	 */
	@Test
	public void testCopyForeignSlide() {
		try {
			doc = PresentationDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_PRESENTATION_FILE_MAIN));
			doc2 = PresentationDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_PRESENTATION_FILE_ANOTHER));

			// copy slide at index 2 of doc to the index 2 of doc2
			int nEmbedDoc = doc2.getEmbeddedDocuments().size();
			String embedDocName = "Object 3/";
			OdfFileEntry fileEntry = doc2.getPackage().getFileEntry(embedDocName);
			Assert.assertNull(fileEntry);
			Slide newPage1 = doc2.copyForeignSlide(2, doc, 2);
			Assert.assertTrue(2 == newPage1.getSlideIndex());
			// slide at index 2 of doc contains an embedded document called
			// "Object 3"
			Document embedDoc = doc2.getEmbeddedDocument(embedDocName);
			Assert.assertNotNull(embedDoc);
			int size = doc2.getEmbeddedDocuments().size();
			Assert.assertTrue(size == (nEmbedDoc + 1));
			// the copied slide also have an bitmap background, and the image
			// bullet
			// they should all be copied
			String BACKGROUND_IMAGE_NAME = "Pictures/1000000000000C80000004009305DCA3.jpg";
			String BULLET_IMAGE_NAME = "Pictures/10000000000002580000018FB151A5C8.jpg";
			InputStream backgroundImage = doc2.getPackage().getInputStream(BACKGROUND_IMAGE_NAME);
			Assert.assertNotNull(backgroundImage);
			// copy the slide at index 2 of doc to the end of doc2
			Slide newPage2 = doc2.copyForeignSlide(101, doc, 2);
			Assert.assertNotNull(doc2.getPackage().getFileEntry(BULLET_IMAGE_NAME));
			Assert.assertFalse(newPage1.getSlideName().equals(newPage2.getSlideName()));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
		try {
			doc2.copyForeignSlide(200, doc, 0);
		} catch (IndexOutOfBoundsException iobe) {
			Assert.assertTrue("slide index is out of bounds", true);
		}

		try {
			doc2.copyForeignSlide(-1, doc, 0);
		} catch (IndexOutOfBoundsException iobe) {
			Assert.assertTrue("slide index is out of bounds", true);
		}

		try {
			doc2.save(ResourceUtilities.newTestOutputFile(TEST_PRESENTATION_FILE_OUT_PREFIX + "CopyForeignSlide2.odp"));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	/**
	 * Test case for appending all the slide of one presentation to the end of
	 * another presentation.
	 * <p>
	 * <b>Precondition</b> Load Presentation File at
	 * test\resources\performance\Presentation1.odp &
	 * test\resources\performance\Presentation2.odp <b>Method</b> Using
	 * PresentationDocument.appendPresentation(PresentationDocument aDoc) to
	 * append the specified presentation to the current presentation.
	 * <b>Postcondition</b> Save the modified document at SlideResultMerge.odp
	 * <b>Covered Element</b> Same with <code>CopyForeignSlide</code> method,
	 * except that it covers all the slide element of the document,rather than
	 * specific slide. <b>Note</b> 1)You'd better check the generated document
	 * to look at if there are any object/style is lost. 2) The generated file
	 * size will be larger than we append by using OpenOffice/Symphony, that is
	 * because the styles and images from the different document might have the
	 * same content, but I still append them rather than use one copy, because
	 * compare the odf element content and the image stream might cost much
	 * time.
	 */
	@Test
	public void testAppendPresentation() {
		try {
			doc = PresentationDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_PRESENTATION_FILE_MAIN));
			doc2 = PresentationDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_PRESENTATION_FILE_ANOTHER));
			int slideCount = doc.getSlideCount();
			int slideCount2 = doc2.getSlideCount();
			// doc, doc2 both have the embed document named "Object 2"
			// with different content, so after appendPresentation,
			// the embedded doc "Object 2" of doc will be renamed
			String EMBEDDOC_NAME = "Object 2";
			Assert.assertNotNull(doc.getEmbeddedDocument(EMBEDDOC_NAME));
			Assert.assertNotNull(doc2.getEmbeddedDocument(EMBEDDOC_NAME));
			doc2.appendPresentation(doc);
			Assert.assertTrue((slideCount + slideCount2) == doc2.getSlideCount());
			// slide at index 3 of doc contains "Object 2", "Object 6"
			// after appendPresentation, let's check the slide at index 103 of
			// merged document
			// which is corresponding to the slide at index 3 of doc
			Slide slide = doc2.getSlideByIndex(103);
			DrawPageElement slideEle = slide.getOdfElement();
			NodeList objectList = slideEle.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "object");
			Assert.assertTrue(objectList.getLength() == 2);
			DrawObjectElement object1 = (DrawObjectElement) objectList.item(0);
			String linkPath = object1.getXlinkHrefAttribute();
			Assert.assertTrue(linkPath.startsWith("./Object 2") && !linkPath.equals("./Object 2"));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}

		try {
			doc2.save(ResourceUtilities.newTestOutputFile(TEST_PRESENTATION_FILE_OUT_PREFIX + "Merge2.odp"));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testAnotherMergeDoc() {
		try {
			doc = PresentationDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_PRESENTATION_FILE_MAIN));
			doc2 = PresentationDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_PRESENTATION_FILE_ANOTHER));
			doc.appendPresentation(doc2);
			Assert.assertTrue(doc.getSlideCount() == 110);
			doc.save(ResourceUtilities.newTestOutputFile(TEST_PRESENTATION_FILE_OUT_PREFIX + "Merge1.odp"));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	/**
	 * Test case for merge the three presentation document
	 * <p>
	 * <b>Precondition</b> Load Presentation File at
	 * test\resources\performance\SlideTest1.odp,
	 * test\resources\performance\SlideTest2.odp and
	 * test\resources\performance\SlideTest3.odp <b>Method</b> copy the slide
	 * from SlideTest2.odp and SlideTest3.odp to SlideTest1.odp
	 * <b>Postcondition</b> Save the modified document at
	 * SlideResultCopyThreeDoc.odp <b>Notice</b> This test case is used to show
	 * that the style name can be renamed if they define the different style.
	 */
	@Test
	public void testCopyThreeDoc() {
		try {
			// testdoc1 contain "dp1" for draw page style
			PresentationDocument testdoc1 = PresentationDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_PRESENTATION_DOCUMENT1));
			// testdoc1 contain "dp1" for draw page style
			PresentationDocument testdoc2 = PresentationDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_PRESENTATION_DOCUMENT2));
			// testdoc1 contain "dp1" for draw page style
			PresentationDocument testdoc3 = PresentationDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_PRESENTATION_DOCUMENT3));
			testdoc1.copyForeignSlide(1, testdoc2, 0);
			testdoc1.copyForeignSlide(2, testdoc3, 0);
			// after copy foreign slide, the each slide should has its own draw
			// page style
			DrawPageElement slide1 = testdoc1.getSlideByIndex(0).getOdfElement();
			DrawPageElement slide2 = testdoc1.getSlideByIndex(1).getOdfElement();
			DrawPageElement slide3 = testdoc1.getSlideByIndex(2).getOdfElement();
			String slideStyle1 = slide1.getDrawNameAttribute();
			String slideStyle2 = slide2.getDrawNameAttribute();
			String slideStyle3 = slide3.getDrawNameAttribute();
			LOG.info(slideStyle1);
			LOG.info(slideStyle2);
			LOG.info(slideStyle3);
			testdoc1.save(ResourceUtilities.newTestOutputFile(TEST_PRESENTATION_FILE_OUT_PREFIX + "CopyThreeDoc.odp"));
			Assert.assertNotSame(slideStyle1, slideStyle2);
			Assert.assertNotSame(slideStyle2, slideStyle3);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testAddRemoveIterateSubList() {
		try {
			doc = PresentationDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_PRESENTATION_FILE_MAIN));
			ListDecorator bulletDecorator = new BulletDecorator(doc);
			ListDecorator numberDecorator = new NumberDecorator(doc);
			ListDecorator imageDecorator = new ImageDecorator(doc, ResourceUtilities.getURI("image_list_item.png"));
			String[] numberItemContents = { "number item 1", "number item 2", "number item 3" };

			// add list.
			ListContainer container = doc.newSlide(0, "test0", Slide.SlideLayout.TITLE_OUTLINE);
			org.odftoolkit.simple.text.list.List bulletList = container.addList(bulletDecorator);
			bulletList.addItems(numberItemContents);
			container = doc.newSlide(1, "test1", Slide.SlideLayout.TITLE_PLUS_2_TEXT_BLOCK);
			org.odftoolkit.simple.text.list.List numberList = container.addList(numberDecorator);
			numberList.addItems(numberItemContents);
			container = doc.newSlide(2, "test2", Slide.SlideLayout.TITLE_PLUS_TEXT);
			org.odftoolkit.simple.text.list.List imageList = container.addList(imageDecorator);
			imageList.addItems(numberItemContents);
			// iterate list
			Assert.assertTrue(container.getListIterator().hasNext());
			// remove list
			container.clearList();
			Assert.assertFalse(container.getListIterator().hasNext());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testAddTable() {
		try {
			doc = PresentationDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_PRESENTATION_FILE_MAIN));
			// add table.
			TableContainer container = doc.newSlide(2, "title_plus_text", Slide.SlideLayout.TITLE_PLUS_TEXT);
			Table table = container.addTable();
			table.setTableName("SlideTable");
			String slideTableCellValue = "SlideTable Cell String";
			table.getCellByPosition(0, 0).setStringValue(slideTableCellValue);
			doc.save(ResourceUtilities.newTestOutputFile("SlideTableOutput.odp"));
			
			//load 
			doc = PresentationDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("SlideTableOutput.odp"));
			Assert.assertEquals(slideTableCellValue, table.getCellByPosition(0, 0).getStringValue());

		} catch (Exception e) {
			LOG.log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testNewSlideTitle_subtitle() {
		try {
			//Creates an empty presentation document.
			doc = PresentationDocument.newPresentationDocument();
			
			TableContainer container = doc.newSlide(0, "testlayout", Slide.SlideLayout.TITLE_SUBTITLE);
			Table table = container.addTable();
			table.setTableName("SlideTable");
			String slideTableCellValue = " Cell[0,0]";
			table.getCellByPosition(0, 0).setStringValue(slideTableCellValue);
			String slideTableCellValue1 = " Cell[0,1]";
			table.getCellByPosition(0, 1).setStringValue(slideTableCellValue1);
			doc.save(ResourceUtilities.newTestOutputFile("slidetitlelayout.odp"));
			
			//load 
			doc = PresentationDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("slidetitlelayout.odp"));
			Slide slide = doc.getSlideByIndex(0);
			Slide slide1 = doc.getSlideByName("testlayout");

			//validate
			Node nodetitle = slide1.getOdfElement().getFirstChild();
			Assert.assertEquals("frame", nodetitle.getLocalName());
			NamedNodeMap map = nodetitle.getAttributes();
			Assert.assertEquals("layout", map.item(0).getNodeValue());
			Assert.assertEquals("title", map.item(1).getNodeValue());
			
			//
			Node nodetitle1 = slide1.getOdfElement().getFirstChild().getNextSibling();
			Assert.assertEquals("frame", nodetitle1.getLocalName());
			NamedNodeMap map1 = nodetitle1.getAttributes();
			Assert.assertEquals("layout", map1.item(0).getNodeValue());
			Assert.assertEquals("subtitle", map1.item(1).getNodeValue());
			
			if(slide.equals(slide1))
				Assert.assertEquals(slide, slide1);

			Assert.assertEquals(slideTableCellValue, table.getCellByPosition(0, 0).getStringValue());
			Assert.assertEquals(slideTableCellValue1, table.getCellByPosition(0, 1).getStringValue());

		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}

	}
	
	
	@Test
	public void testNewSlideTitle_left_chart_right_outline() {
		try {
			doc = PresentationDocument.newPresentationDocument();
			
			Slide slidepre = doc.newSlide(0, "testlayout", Slide.SlideLayout.TITLE_LEFT_CHART_RIGHT_OUTLINE);
			Textbox titleBox = slidepre.getTextboxByUsage(PresentationClass.TITLE).get(0);
		    titleBox.setTextContent("This is the title");

			Textbox outline = slidepre.getTextboxByUsage(PresentationClass.OUTLINE).get(0);
		    List txtList = outline.addList();
		    txtList.addItem("List Item1");
		    txtList.addItem("List Item2");

			doc.save(ResourceUtilities.newTestOutputFile("slidetitlelayout.odp"));
			
			//load 
			doc = PresentationDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("slidetitlelayout.odp"));
			Slide slide1 = doc.getSlideByName("testlayout");

			//validate
			Node nodetitle2 = slide1.getOdfElement().getFirstChild();
			Assert.assertEquals("frame", nodetitle2.getLocalName());
			NamedNodeMap map2 = nodetitle2.getAttributes();
			Assert.assertEquals("layout", map2.item(0).getNodeValue());
			Assert.assertEquals("title", map2.item(1).getNodeValue());
			
			Node nodetitle = slide1.getOdfElement().getFirstChild().getNextSibling();
			Assert.assertEquals("frame", nodetitle.getLocalName());
			NamedNodeMap map = nodetitle.getAttributes();
			Assert.assertEquals("layout", map.item(0).getNodeValue());
			Assert.assertEquals("chart", map.item(2).getNodeValue());
			
			Node nodetitle1 = slide1.getOdfElement().getFirstChild().getNextSibling().getNextSibling();
			Assert.assertEquals("frame", nodetitle1.getLocalName());
			NamedNodeMap map1 = nodetitle1.getAttributes();
			Assert.assertEquals("layout", map1.item(0).getNodeValue());
			Assert.assertEquals("outline", map1.item(1).getNodeValue());
			
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	
	@Test
	public void testNewSlideTitle_plus_chart() {
		try {
			doc = PresentationDocument.newPresentationDocument();
			Slide slidepre = doc.newSlide(0, "testlayout", Slide.SlideLayout.TITLE_PLUS_CHART);
			Textbox titleBox = slidepre.getTextboxByUsage(PresentationClass.TITLE).get(0);
		    titleBox.setTextContent("This is the Presentation title");
		    //chart
		    String title = "new Chart";
			String[] lables = new String[] { "Anna", "Daisy", "Tony", "MingFei" };
			String[] legends = new String[] { "Day1", "Day2", "Day3" };
			double[][] data = new double[][] { { 1, 6, 3, 8 }, { 9, 1, 0, 5 }, { 3, 7, 8, 1 } };
			Rectangle rect = new Rectangle();
			rect.x = 2000;
			rect.y = 3300;
			rect.width = 21000;
			rect.height = 12000;
			Chart newChart = slidepre.createChart(title, lables, legends, data, rect);

			doc.save(ResourceUtilities.newTestOutputFile("slidetitlelayout.odp"));
			
			//load 
			doc = PresentationDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("slidetitlelayout.odp"));
			Slide slide1 = doc.getSlideByName("testlayout");

			//validate
			Node nodetitle2 = slide1.getOdfElement().getFirstChild();
			Assert.assertEquals("frame", nodetitle2.getLocalName());
			NamedNodeMap map2 = nodetitle2.getAttributes();
			Assert.assertEquals("layout", map2.item(0).getNodeValue());
			Assert.assertEquals("title", map2.item(1).getNodeValue());
			
			Node nodetitle = slide1.getOdfElement().getFirstChild().getNextSibling();
			Assert.assertEquals("frame", nodetitle.getLocalName());
			NamedNodeMap map = nodetitle.getAttributes();
			Assert.assertEquals("layout", map.item(0).getNodeValue());
			Assert.assertEquals("chart", map.item(2).getNodeValue());
			
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	
	@Test
	public void testNewSlideTitle_plus_2_chart() {
		try {
			doc = PresentationDocument.newPresentationDocument();
			Slide slidepre = doc.newSlide(0, "testlayout", Slide.SlideLayout.TITLE_PLUS_2_CHART);
			Textbox titleBox = slidepre.getTextboxByUsage(PresentationClass.TITLE).get(0);
		    titleBox.setTextContent("This is the Presentation title");
		    //chart1
		    String title = "Chart one";
			String[] lables = new String[] { "AAA", "XXX", "KKK" };
			String[] legends = new String[] { "condition1", "condition2", "condition3" };
			double[][] data = new double[][] { { 3, 3, 2 }, { 2, 1, 5 }, { 1, 4, 0 } };
			Rectangle rect = new Rectangle();
			rect.x = 2000;
			rect.y = 3300;
			rect.width = 22000;
			rect.height = 8000;
			//char2
			String title1 = "Chart two";
			String[] lables1 = new String[] { "Anna", "Daisy", "Tony", "MingFei" };
			String[] legends1 = new String[] { "Day1", "Day2", "Day3" };
			double[][] data1 = new double[][] { { 1, 5, 3, 2 }, { 2, 1, 0, 5 }, { 3, 3, 4, 1 } };
			Rectangle rect1 = new Rectangle();
			rect1.x = 2000;
			rect1.y = 11300;
			rect1.width = 22000;
			rect1.height = 8000;
			Chart newChart = slidepre.createChart(title, lables, legends, data, rect);
			Chart newChart1 = slidepre.createChart(title1, lables1, legends1, data1, rect1);

			doc.save(ResourceUtilities.newTestOutputFile("slidetitlelayout.odp"));
			
			//load 
			doc = PresentationDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("slidetitlelayout.odp"));
			Slide slide1 = doc.getSlideByName("testlayout");

			//validate
			Node nodetitle2 = slide1.getOdfElement().getFirstChild();
			Assert.assertEquals("frame", nodetitle2.getLocalName());
			NamedNodeMap map2 = nodetitle2.getAttributes();
			Assert.assertEquals("layout", map2.item(0).getNodeValue());
			Assert.assertEquals("title", map2.item(1).getNodeValue());
			
			Node nodetitle = slide1.getOdfElement().getFirstChild().getNextSibling();
			Assert.assertEquals("frame", nodetitle.getLocalName());
			NamedNodeMap map = nodetitle.getAttributes();
			Assert.assertEquals("layout", map.item(0).getNodeValue());
			Assert.assertEquals("chart", map.item(2).getNodeValue());
			
			Node nodetitle1 = slide1.getOdfElement().getFirstChild().getNextSibling().getNextSibling();
			Assert.assertEquals("frame", nodetitle1.getLocalName());
			NamedNodeMap map1 = nodetitle1.getAttributes();
			Assert.assertEquals("layout", map1.item(0).getNodeValue());
			Assert.assertEquals("chart", map1.item(2).getNodeValue());
			
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	
	@Test
	public void testNewSlideTitle_plus_3_object() {
		try {
			doc = PresentationDocument.newPresentationDocument();
			Slide slidepre = doc.newSlide(0, "testlayout", Slide.SlideLayout.TITLE_PLUS_3_OBJECT);
			Textbox titleBox = slidepre.getTextboxByUsage(PresentationClass.TITLE).get(0);
		    titleBox.setTextContent("This is the Presentation title");
		    //chart1
		    String title = "Chart one";
			String[] lables = new String[] { "AAA", "XXX", "KKK" };
			String[] legends = new String[] { "condition1", "condition2", "condition3" };
			double[][] data = new double[][] { { 3, 3, 2 }, { 2, 1, 5 }, { 1, 4, 0 } };
			Rectangle rect = new Rectangle();
			rect.x = 2000;
			rect.y = 3300;
			rect.width = 10000;
			rect.height = 8000;
			//char2
			String title1 = "Chart two";
			String[] lables1 = new String[] { "Anna", "Daisy", "Tony", "MingFei" };
			String[] legends1 = new String[] { "Day1", "Day2", "Day3" };
			double[][] data1 = new double[][] { { 1, 5, 3, 2 }, { 2, 1, 0, 5 }, { 3, 3, 4, 1 } };
			Rectangle rect1 = new Rectangle();
			rect1.x = 2000;
			rect1.y = 11300;
			rect1.width = 10000;
			rect1.height = 8000;
			Chart newChart = slidepre.createChart(title, lables, legends, data, rect);
			
			Textbox outline = slidepre.getTextboxByUsage(PresentationClass.OUTLINE).get(0);
		    List txtList = outline.addList();
		    txtList.addItem("List Item1");
		    txtList.addItem("List Item2");
			
			Chart newChart1 = slidepre.createChart(title1, lables1, legends1, data1, rect1);

			doc.save(ResourceUtilities.newTestOutputFile("slidetitlelayout.odp"));
			
			//load 
			doc = PresentationDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("slidetitlelayout.odp"));
			Slide slide1 = doc.getSlideByName("testlayout");

			//validate
			Node nodetitle2 = slide1.getOdfElement().getFirstChild();
			Assert.assertEquals("frame", nodetitle2.getLocalName());
			NamedNodeMap map2 = nodetitle2.getAttributes();
			Assert.assertEquals("layout", map2.item(0).getNodeValue());
			Assert.assertEquals("title", map2.item(1).getNodeValue());
			
			Node nodetitle = slide1.getOdfElement().getFirstChild().getNextSibling();
			Assert.assertEquals("frame", nodetitle.getLocalName());
			NamedNodeMap map = nodetitle.getAttributes();
			Assert.assertEquals("layout", map.item(0).getNodeValue());
			Assert.assertEquals("chart", map.item(2).getNodeValue());
			
			Node nodetitle1 = slide1.getOdfElement().getFirstChild().getNextSibling().getNextSibling();
			Assert.assertEquals("frame", nodetitle1.getLocalName());
			NamedNodeMap map1 = nodetitle1.getAttributes();
			Assert.assertEquals("layout", map1.item(0).getNodeValue());
			Assert.assertEquals("outline", map1.item(1).getNodeValue());
			
			Node nodetitle3 = slide1.getOdfElement().getFirstChild().getNextSibling().getNextSibling().getNextSibling();
			Assert.assertEquals("frame", nodetitle3.getLocalName());
			NamedNodeMap map3 = nodetitle3.getAttributes();
			Assert.assertEquals("layout", map3.item(0).getNodeValue());
			Assert.assertEquals("chart", map3.item(2).getNodeValue());
			
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	
	@Test
	public void testNewSlideTitle_plus_4_object() {
		try {
			doc = PresentationDocument.newPresentationDocument();
			Slide slidepre = doc.newSlide(0, "testlayout", Slide.SlideLayout.TITLE_PLUS_4_OBJECT);
			Textbox titleBox = slidepre.getTextboxByUsage(PresentationClass.TITLE).get(0);
		    titleBox.setTextContent("This is the Presentation title");
		    //chart1
		    String title = "Chart one";
			String[] lables = new String[] { "AAA", "XXX", "KKK" };
			String[] legends = new String[] { "condition1", "condition2", "condition3" };
			double[][] data = new double[][] { { 3, 3, 2 }, { 2, 1, 5 }, { 1, 4, 0 } };
			Rectangle rect = new Rectangle();
			rect.x = 2000;
			rect.y = 2300;
			rect.width = 5000;
			rect.height = 6000;
			//char2
			String title1 = "Chart two";
			String[] lables1 = new String[] { "Anna", "Daisy", "Tony", "MingFei" };
			String[] legends1 = new String[] { "Day1", "Day2", "Day3" };
			double[][] data1 = new double[][] { { 1, 5, 3, 2 }, { 2, 1, 0, 5 }, { 3, 3, 4, 1 } };
			Rectangle rect1 = new Rectangle();
			rect1.x = 2000;
			rect1.y = 11300;
			rect1.width = 5000;
			rect1.height = 6000;
			//char3
			String title2 = "Chart three";
			String[] lables2 = new String[] { "Anna", "Daisy", "Tony", "MingFei" };
			String[] legends2 = new String[] { "Day1", "Day2", "Day3" };
			double[][] data2 = new double[][] { { 1, 5, 3, 2 }, { 2, 1, 0, 5 }, { 3, 3, 4, 1 } };
			Rectangle rect2 = new Rectangle();
			rect2.x = 2000;
			rect2.y = 16300;
			rect2.width = 5000;
			rect2.height = 6000;
			
			Chart newChart = slidepre.createChart(title, lables, legends, data, rect);
			Chart newChart1 = slidepre.createChart(title1, lables1, legends1, data1, rect1);
			Textbox outline = slidepre.getTextboxByUsage(PresentationClass.OUTLINE).get(0);
		    List txtList = outline.addList();
		    txtList.addItem("List Item1");
		    txtList.addItem("List Item2");
		    Chart newChart2 = slidepre.createChart(title2, lables2, legends2, data2, rect2);
			
			doc.save(ResourceUtilities.newTestOutputFile("slidetitlelayout.odp"));
			
			//load 
			doc = PresentationDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("slidetitlelayout.odp"));
			Slide slide1 = doc.getSlideByName("testlayout");

			//validate
			Node nodetitle2 = slide1.getOdfElement().getFirstChild();
			Assert.assertEquals("frame", nodetitle2.getLocalName());
			NamedNodeMap map2 = nodetitle2.getAttributes();
			Assert.assertEquals("layout", map2.item(0).getNodeValue());
			Assert.assertEquals("title", map2.item(1).getNodeValue());
			
			Node nodetitle = slide1.getOdfElement().getFirstChild().getNextSibling();
			Assert.assertEquals("frame", nodetitle.getLocalName());
			NamedNodeMap map = nodetitle.getAttributes();
			Assert.assertEquals("layout", map.item(0).getNodeValue());
			Assert.assertEquals("chart", map.item(2).getNodeValue());
			
			Node nodetitle1 = slide1.getOdfElement().getFirstChild().getNextSibling().getNextSibling();
			Assert.assertEquals("frame", nodetitle1.getLocalName());
			NamedNodeMap map1 = nodetitle1.getAttributes();
			Assert.assertEquals("layout", map1.item(0).getNodeValue());
			Assert.assertEquals("outline", map1.item(1).getNodeValue());
			
			Node nodetitle3 = slide1.getOdfElement().getFirstChild().getNextSibling().getNextSibling().getNextSibling();
			Assert.assertEquals("frame", nodetitle3.getLocalName());
			NamedNodeMap map3 = nodetitle3.getAttributes();
			Assert.assertEquals("layout", map3.item(0).getNodeValue());
			Assert.assertEquals("chart", map3.item(2).getNodeValue());
			
			Node nodetitle4 = slide1.getOdfElement().getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling();
			Assert.assertEquals("frame", nodetitle4.getLocalName());
			NamedNodeMap map4 = nodetitle4.getAttributes();
			Assert.assertEquals("layout", map4.item(0).getNodeValue());
			Assert.assertEquals("chart", map4.item(2).getNodeValue());
			
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
}
