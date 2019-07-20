/************************************************************************
 *
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
 ************************************************************************/
package org.odftoolkit.odfdom.pkg;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LoadSaveTest {

    private static final String SOURCE = "not-only-odf.odt";
    private static final String TARGET = "loadsavetest.odt";
	private static final String FOREIGN_ATTRIBUTE_NAME = "foreignAttribute";
	private static final String FOREIGN_ATTRIBUTE_VALUE = "foreignAttributeValue";
	private static final String FOREIGN_ELEMENT_TEXT = "foreignText";

    public LoadSaveTest() {
    }

    @Test
    public void testLoadSave() {
        try {
            OdfPackageDocument odfDocument = OdfPackageDocument.loadDocument(ResourceUtilities.getAbsolutePath(SOURCE));
            Assert.assertTrue(odfDocument.getPackage().contains("content.xml"));
            String baseURI = odfDocument.getPackage().getBaseURI();
//            Assert.assertTrue(ResourceUtilities.getURI(SOURCE).toString().compareToIgnoreCase(baseURI) == 0);
            System.out.println("SOURCE URI1:"+ResourceUtilities.getURI(SOURCE).toString());
			System.out.println("SOURCE URI2:"+baseURI);

            Document odfContent = odfDocument.getFileDom("content.xml");
            NodeList lst = odfContent.getElementsByTagNameNS("urn:oasis:names:tc:opendocument:xmlns:text:1.0", "p");
            Node node = lst.item(0);
            String oldText = "Changed!!!";
            node.setTextContent(oldText);

            //Added to reproduce the bug "xlmns:null=""" is added to namespace.
            OdfFileDom dom = odfDocument.getFileDom("content.xml");
			Map<String, String> nsByUri = dom.getMapNamespacePrefixByUri();
			for (Entry<String, String> entry : nsByUri.entrySet()) {
				Assert.assertNotNull(entry.getValue());
				Assert.assertFalse(entry.getValue().length()==0);
				Assert.assertNotNull(entry.getKey());
				Assert.assertFalse(entry.getKey().length()==0);
			}
			
            odfDocument.save(ResourceUtilities.newTestOutputFile(TARGET));
            odfDocument = OdfPackageDocument.loadDocument(ResourceUtilities.getAbsolutePath(TARGET));

            odfContent = odfDocument.getFileDom("content.xml");
            lst = odfContent.getElementsByTagNameNS("urn:oasis:names:tc:opendocument:xmlns:text:1.0", "p");
            node = lst.item(0);
            String newText = node.getTextContent();
            Assert.assertTrue(newText.equals(oldText));

			node = lst.item(1);
			//check foreign attribute without namespace
			Element foreignElement = (Element) node.getChildNodes().item(0);
            String foreignText = foreignElement.getTextContent();
            Assert.assertTrue(foreignText.equals(FOREIGN_ELEMENT_TEXT));

			//check foreign element without namespace
			Attr foreignAttr = (Attr) node.getAttributes().getNamedItem(FOREIGN_ATTRIBUTE_NAME);
			String foreignAttrValue = foreignAttr.getValue();
            Assert.assertTrue(foreignAttrValue.equals(FOREIGN_ATTRIBUTE_VALUE));



        } catch (Exception e) {
        	Logger.getLogger(LoadSaveTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }
}
