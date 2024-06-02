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

import java.util.List;

import java.io.File;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.ValidationEventLocator;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;

public class Unmarshal {
    private File schemaPath;
    private File inputPath;
    private String gensrcPath;
    private String tmpPath;
    private List<Linenumber> linenumbers;

    public Unmarshal(File schemaPath, File inputPath, String gensrcPath, String tmpPath) {
        this.schemaPath = schemaPath;
        this.inputPath = inputPath;
        this.gensrcPath = gensrcPath;
        this.tmpPath = tmpPath;
    }

    private boolean localEventHandler(ValidationEvent ve) {
        // ignore warnings
        if (ve.getSeverity() != ValidationEvent.WARNING) {
            ValidationEventLocator vel = ve.getLocator();
            Linenumber ln = linenumbers.get(vel.getLineNumber() - 1);
            System.err.println("ERROR IN FILE: " + ln.getFile());
            System.err.println( "  LINE NUMBER: " + ln.getLinenumber());
            String temp = ln.getLine();
            if (temp != null) {
                System.err.println( "       SOURCE: " + temp);
            }
            System.err.println("      MESSAGE: " + ve.getMessage());
        }
        return true;
    }

    public Object unmarshal() throws FactoryException {

        try {
            // create a JAXBContext capable of handling classes generated
            JAXBContext jc = JAXBContext.newInstance(gensrcPath);

            // create an Unmarshaller
            Unmarshaller u = jc.createUnmarshaller();
            SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);

            try {
                Schema schema = sf.newSchema(schemaPath);
                u.setSchema(schema);
                u.setEventHandler(
                    new ValidationEventHandler() {
                        // allow unmarshalling to continue even if there are errors
                        public boolean handleEvent(ValidationEvent ve) {
                            return (localEventHandler(ve));
                        }
                    }
                );
            } catch (org.xml.sax.SAXException se) {
                System.err.println( "ERROR: Unable to validate");
                throw new FactoryException(se);
            }

            // Expand the xinclude's
            String username = System.getenv("USER");
            File out = new File(tmpPath + "/" + username + ".xml");
            XIncludeFilter xif = new XIncludeFilter(out);
            linenumbers = xif.filter(inputPath, true);
            xif.close();
            JAXBElement<?> element = (JAXBElement<?>)u.unmarshal(out);
            xif.cleanup(out);
            return element.getValue();
        } catch (UnmarshalException ue) {
            System.err.println( "ERROR: Caught UnmarshalException");
            throw new FactoryException(ue);
        } catch (JAXBException je) {
            System.err.println("ERROR: Caught JAXBException");
            throw new FactoryException(je);
        } // try
    } // unmarshal
}
