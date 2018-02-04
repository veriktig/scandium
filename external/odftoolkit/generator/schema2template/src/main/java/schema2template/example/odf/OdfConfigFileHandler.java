/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2009, 2010 Oracle and/or its affiliates. All rights reserved.
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
package schema2template.example.odf;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


 // ToDo: Shall we keep one config file or shall we split them?!
 // Java specific + other separating??

/**
 * Often Process the custom configuration data XML
 * Reads only the config.xml
 * Handler for existing config.xml
 */
class OdfConfigFileHandler extends DefaultHandler {

    private boolean inConfig = false;
    private boolean inElements = false;
    private boolean inElement = false;
    private boolean inDatatypes = false;
    private boolean inData = false;
    private boolean inAttributes = false;
    private boolean inAttribute = false;
    private Locator mLocator;
    private Map<String, String> mElementBaseNames;
    private Map<String, List<String>> mElementStyleFamilies;
    private Set<String> mProcessedElements;
    private Map<String, String[]> mDatatypeValueConversion; // Datatype -> {value-type, conversion-classname}
    private Map<String, OdfModel.AttributeDefaults> mAttributeDefaultMap;     // Attributename -> {elementname or null, defaultvalue}
    private Set<String> mProcessedDatatypes;

    public OdfConfigFileHandler(Map<String, String> elementBaseNames, Map<String, OdfModel.AttributeDefaults> attributeDefaultMap,
            Map<String, List<String>> elementStyleFamilies, Map<String, String[]> datatypeValueConversion) {
        mElementBaseNames = elementBaseNames;
        mAttributeDefaultMap = attributeDefaultMap;
        mDatatypeValueConversion = datatypeValueConversion;
        mElementStyleFamilies = elementStyleFamilies;
        mProcessedElements = new HashSet<String>();
        mProcessedDatatypes = new HashSet<String>();
    }

    private void createElementConfig(Attributes attrs) throws SAXException {
        String name = attrs.getValue("name");
        if (name == null) {
            throw new SAXException("Invalid element line " + mLocator.getLineNumber());
        }
        if (mProcessedElements.contains(name)) {
            throw new SAXException("Multiple definition of element in line " + mLocator.getLineNumber());
        }
        mProcessedElements.add(name);
        String base = attrs.getValue("base");
        if (base != null && base.length() > 0) {
            mElementBaseNames.put(name, base);
        }
        String commaSeparatedStyleFamilies = attrs.getValue("family");
        if (commaSeparatedStyleFamilies != null) {
            StringTokenizer tok = new StringTokenizer(commaSeparatedStyleFamilies, ",");
            List<String> families = new ArrayList<String>();
            while (tok.hasMoreElements()) {
                String family = tok.nextToken();
                if (family.length() > 0) {
                    families.add(family);
                }
            }
            if (families.size() > 0) {
                mElementStyleFamilies.put(name, families);
            }
        }
    }

    private void createDatatypeConfig(Attributes attrs) throws SAXException {
        String name = attrs.getValue("name");
        if (name == null) {
            throw new SAXException("Invalid datatype line " + mLocator.getLineNumber());
        }
        if (mProcessedDatatypes.contains(name)) {
            throw new SAXException("Multiple definition of datatype in line " + mLocator.getLineNumber());
        }
        mProcessedDatatypes.add(name);
        String[] tuple = new String[2];
        tuple[0] = attrs.getValue("value-type");
        tuple[1] = attrs.getValue("conversion-type");
        mDatatypeValueConversion.put(name, tuple);
    }

    private void createAttributeConfig(Attributes attrs) throws SAXException {
        String name = attrs.getValue("name");
        if (name == null) {
            throw new SAXException("Invalid attribute line " + mLocator.getLineNumber());
        }

        String elementname = attrs.getValue("element");
        String defaultvalue = attrs.getValue("defaultValue");
        OdfModel.AttributeDefaults defaults = mAttributeDefaultMap.get(name);
        if (defaults == null) {
            defaults = new OdfModel.AttributeDefaults();
            mAttributeDefaultMap.put(name, defaults);
        }
        defaults.addDefault(elementname, defaultvalue);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("config") && !inConfig) {
            inConfig = true;
            return;
        }
        if (qName.equals("elements") && inConfig && !inElements) {
            inElements = true;
            return;
        }
        if (qName.equals("element") && inElements && !inElement) {
            inElement = true;
            createElementConfig(attributes);
            return;
        }
        if (qName.equals("attributes") && inConfig && !inAttributes) {
            inAttributes = true;
            return;
        }
        if (qName.equals("attribute") && inAttributes && !inAttribute) {
            inAttribute = true;
            createAttributeConfig(attributes);
            return;
        }
        if (qName.equals("data-types") && inConfig && !inDatatypes) {
            inDatatypes = true;
            return;
        }
        if (qName.equals("data") && inDatatypes && !inData) {
            inData = true;
            createDatatypeConfig(attributes);
            return;
        }

        throw new SAXException("Malformed config.xml in line " + mLocator.getLineNumber());
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("config") && inConfig) {
            inConfig = false;
            return;
        }
        if (qName.equals("elements") && inElements) {
            inElements = false;
            return;
        }
        if (qName.equals("element") && inElement) {
            inElement = false;
            return;
        }
        if (qName.equals("attributes") && inAttributes) {
            inAttributes = false;
            return;
        }
        if (qName.equals("attribute") && inAttribute) {
            inAttribute = false;
            return;
        }
        if (qName.equals("data-types") && inDatatypes) {
            inDatatypes = false;
            return;
        }
        if (qName.equals("data") && inData) {
            inData = false;
            return;
        }

        throw new SAXException("Malformed config.xml in line " + mLocator.getLineNumber());
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        mLocator = locator;
    }

    /**
     * Read config.xml. Input Convention: Input empty Maps, Maps will be filled.
     *
     * @param cf Config file
     */
    public static void readConfigFile(File cf, Map<String, String> elementBaseNames,
            Map<String, OdfModel.AttributeDefaults> attributeDefaults, Map<String, List<String>> elementStyleFamilies,
            Map<String, String[]> datatypeValueConversion) throws Exception {

        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(cf, new OdfConfigFileHandler(elementBaseNames, attributeDefaults, elementStyleFamilies, datatypeValueConversion));

    }

}
