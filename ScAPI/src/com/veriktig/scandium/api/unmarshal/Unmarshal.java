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

package com.veriktig.scandium.api.unmarshal;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.validation.Schema;

import com.veriktig.scandium.api.unmarshal.ClassResolver;
import com.veriktig.scandium.api.errors.ScException;

import tcl.lang.Interp;

public class Unmarshal {
    private Interp interp;
    private Schema schema;
    private File inputPath;
    private String gensrcPath;
    private ClassLoader classLoader;
    private boolean bootstrap;
    private List<Linenumber> linenumbers;

    public Unmarshal(Interp interp, ClassLoader ldr, Schema schema, File inputPath, String gensrcPath, boolean bootstrap) {
        this.interp = interp;
        this.schema = schema;
        this.inputPath = inputPath;
        this.gensrcPath = gensrcPath;
        this.bootstrap = bootstrap;
        this.classLoader = ldr;
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
        return false;
    }

    public Object unmarshal() throws ScException {
        try {
            // create a JAXBContext capable of handling classes generated
            JAXBContext jc = JAXBContext.newInstance(gensrcPath, classLoader);

            // create an Unmarshaller
            Unmarshaller u = jc.createUnmarshaller();
            u.setProperty(ClassResolver.class.getName(), new ClassResolver());
            u.setSchema(schema);
            u.setEventHandler(
                new ValidationEventHandler() {
                    // allow unmarshalling to continue even if there are errors
                    public boolean handleEvent(ValidationEvent ve) {
                        return (localEventHandler(ve));
                    }
                }
            );

            // Expand the xinclude's
            String username = System.getenv("USER");
            File out = new File("/tmp/" + username + "-SOS.xml");
            XIncludeFilter xif = new XIncludeFilter(out);
            linenumbers = xif.filter(inputPath, bootstrap);
            xif.close();
            
            JAXBElement<?> element = (JAXBElement<?>)u.unmarshal(out);
            // TODO Condition this for debug use
            //xif.cleanup(out);
            return element.getValue();
        } catch (Exception ee) {
            Object[] args = new Object[2];
            args[0] = inputPath.getAbsolutePath();
            args[1] = ee.getMessage();
            throw new ScException("API-1002", args);
        }
    }
}
