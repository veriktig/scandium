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

package com.veriktig.documentation.variables;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;

import com.veriktig.documentation.CommonStyles;
import com.veriktig.documentation.FactoryException;
import com.veriktig.documentation.FileFactory;
import com.veriktig.documentation.variables.generated.Description;
import com.veriktig.documentation.variables.generated.SeeAlso;
import com.veriktig.documentation.variables.generated.Variable;

public class ODFileFactory extends FileFactory {
    private static OdfContentDom contentDom;
    private static OdfTextDocument outputDocument;
    private static int currentParagraph = 0;
    private static boolean indented = false;
    private static OfficeTextElement officeText = null;

    public static void make(File file, List<Variable> list) throws FactoryException {
        try {
            outputDocument = OdfTextDocument.newTextDocument();
            officeText = outputDocument.getContentRoot();
            CommonStyles.init(outputDocument);
            contentDom = outputDocument.getContentDom();
            CommonStyles.addStyles();
            CommonStyles.cleanUp();
            // Sort the list by command name
            Collections.sort(list, (o1, o2) -> o1.getName().compareTo(o2.getName()));
            Iterator<Variable> iter = list.iterator();
            while (iter.hasNext()) {
                output(iter.next());
                indented = CommonStyles.unindent();
            }
            CommonStyles.finish(file);
        } catch (Exception e) {
            throw new FactoryException(e);
        }
        return;
    }

    private static void output(Variable var) {
        currentParagraph = CommonStyles.createSection(currentParagraph, false, var.getName());
        indented = CommonStyles.indent();
        
        currentParagraph = CommonStyles.createName(currentParagraph, "", var.getHelp());
        currentParagraph = CommonStyles.addEmptyParagraph(currentParagraph);
        currentParagraph = CommonStyles.createHeader(currentParagraph, "TYPE");
        createType(var.getType().value());
        currentParagraph = CommonStyles.addEmptyParagraph(currentParagraph);
        currentParagraph = CommonStyles.createHeader(currentParagraph, "DEFAULT VALUE");
        createDefault(var.getDefault());
        currentParagraph = CommonStyles.addEmptyParagraph(currentParagraph);
        List<Description> descs = var.getDesc();
        if (descs.size() > 0) {
            currentParagraph = CommonStyles.createHeader(currentParagraph, "DESCRIPTION");
            Iterator<Description> iter2 = descs.iterator();
            while (iter2.hasNext()) {
                currentParagraph = CommonStyles.createDescription(currentParagraph, iter2.next().getP());
            }
        }    
        currentParagraph = CommonStyles.addEmptyParagraph(currentParagraph);
        List<SeeAlso> see_alsos = var.getSeeAlso();
        if (see_alsos.size() > 0) {
            currentParagraph = CommonStyles.createHeader(currentParagraph, "SEE ALSO");
            Iterator<SeeAlso> iter3 = see_alsos.iterator();
            while (iter3.hasNext()) {
                currentParagraph = CommonStyles.createSeeAlso(currentParagraph, iter3.next().getName());
            }
        }
        return;
    }

    private static void createType(String type) {
        TextPElement tpe = new TextPElement(contentDom);
        tpe.setStyleName(indented ? "P_DEFAULT_INDENTED" : "P_DEFAULT");
        TextSpanElement tse = tpe.newTextSpanElement();
        tse.setTextContent(type);
        tse.setStyleName("T_NORMAL");
        officeText.appendChild(tpe);
    }

    private static void createDefault(String default_value) {
        TextPElement tpe = new TextPElement(contentDom);
        tpe.setStyleName(indented ? "P_DEFAULT_INDENTED" : "P_DEFAULT");
        TextSpanElement tse = tpe.newTextSpanElement();
        tse.setTextContent(default_value);
        tse.setStyleName("T_NORMAL");
        officeText.appendChild(tpe);
    }
}
