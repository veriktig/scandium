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

package com.veriktig.documentation.errors;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.odftoolkit.simple.TextDocument;

import com.veriktig.documentation.CommonStyles;
import com.veriktig.documentation.FactoryException;
import com.veriktig.documentation.FileFactory;
import com.veriktig.documentation.errors.generated.Description;
import com.veriktig.documentation.errors.generated.WhatNext;
import com.veriktig.documentation.errors.generated.SeeAlso;

public class ODFileFactory extends FileFactory {
    private static TextDocument outputDocument;
    private static int currentParagraph = 0;

    public static void make(File file, List<SError> list) throws FactoryException {
        try {
            outputDocument = TextDocument.newTextDocument();
            CommonStyles.init(outputDocument);
            CommonStyles.addStyles();
            // Sort the list by command name
            Collections.sort(list, (o1, o2) -> o1.getName().compareTo(o2.getName()));
            Iterator<SError> iter = list.iterator();
            while (iter.hasNext()) {
                output(iter.next());
                CommonStyles.unindent();
            }
            CommonStyles.finish(file);
        } catch (Exception e) {
            throw new FactoryException(e);
        }
        return;
    }

    private static void output(SError error) {
        currentParagraph = CommonStyles.createSection(currentParagraph, false, error.getName());
        CommonStyles.indent();
        String ss = new String("(" + error.getSeverity().value() + ") ");
        currentParagraph = CommonStyles.createName(currentParagraph, ss, error.getHelp());
        currentParagraph = CommonStyles.addEmptyParagraph(currentParagraph);
        List<Description> descs = error.getDesc();
        if (descs.size() > 0) {
            currentParagraph = CommonStyles.createHeader(currentParagraph, "DESCRIPTION");
            Iterator<Description> iter2 = descs.iterator();
            while (iter2.hasNext()) {
                currentParagraph = CommonStyles.createDescription(currentParagraph, iter2.next().getP());
            }
            currentParagraph = CommonStyles.addEmptyParagraph(currentParagraph);
        }    
        List<WhatNext> examples = error.getWhatNext();
        if (examples.size() > 0) {
            currentParagraph = CommonStyles.createHeader(currentParagraph, "WHAT NEXT");
            Iterator<WhatNext> iter2 = examples.iterator();
            while (iter2.hasNext()) {
                currentParagraph = CommonStyles.createDescription(currentParagraph, iter2.next().getP());
            }
            currentParagraph = CommonStyles.addEmptyParagraph(currentParagraph);
        }
        List<SeeAlso> see_alsos = error.getSeeAlso();
        if (see_alsos.size() > 0) {
            currentParagraph = CommonStyles.createHeader(currentParagraph, "SEE ALSO");
            Iterator<SeeAlso> iter3 = see_alsos.iterator();
            while (iter3.hasNext()) {
                currentParagraph = CommonStyles.createSeeAlso(currentParagraph, iter3.next().getName());
            }
        }
        return;
    }
}
