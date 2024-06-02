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

package com.veriktig.documentation.help;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.element.text.TextTabElement;

import com.veriktig.documentation.CommonStyles;
import com.veriktig.documentation.FactoryException;
import com.veriktig.documentation.FileFactory;
import com.veriktig.documentation.help.generated.Option;
import com.veriktig.documentation.help.generated.OptionGroup;
import com.veriktig.documentation.help.generated.Description;
import com.veriktig.documentation.help.generated.Example;
import com.veriktig.documentation.help.generated.Help;
import com.veriktig.documentation.help.generated.SeeAlso;

public class ODFileFactory extends FileFactory {
    private static final String NULL_OPTION = "null"; // SCAPI
    private static OdfContentDom contentDom;
    private static OdfTextDocument outputDocument;
    private static int currentParagraph = 0;
    private static boolean indented = false;
    private static OfficeTextElement officeText = null;

    public static void make(File file, List<Help> list) throws FactoryException {
        try {
            outputDocument = OdfTextDocument.newTextDocument();
            officeText = outputDocument.getContentRoot();
            CommonStyles.init(outputDocument);
            contentDom = outputDocument.getContentDom();
            CommonStyles.addStyles();
            CommonStyles.cleanUp();
            // Sort the list by command name
            Collections.sort(list, (o1, o2) -> o1.getName().compareTo(o2.getName()));
            Iterator<Help> iter = list.iterator();
            while (iter.hasNext()) {
                output(iter.next());
            }
            CommonStyles.finish(file);
        } catch (Exception e) {
            throw new FactoryException(e);
        }
        return;
    }

    private static void output(Help help) {
        currentParagraph = CommonStyles.createSection(currentParagraph, true, help.getName());
        indented = CommonStyles.indent();
        currentParagraph = CommonStyles.createName(currentParagraph, "", help.getHelp());
        currentParagraph = CommonStyles.addEmptyParagraph(currentParagraph);
        currentParagraph = CommonStyles.createHeader(currentParagraph, "SYNOPSIS");
        List<Option> option =  help.getOption();
        List<OptionGroup> option_group = help.getOptionGroup();
        createSynopsis(help.getRv(), help.getName(), option, option_group);
        currentParagraph = CommonStyles.addEmptyParagraph(currentParagraph);

        // Add option_group to option for ARGUMENTS
        List<Option> combined_options = new ArrayList<Option>(option);
        if (option_group.size() > 0) {
            Iterator<OptionGroup> iterOR = option_group.iterator();
            while (iterOR.hasNext()) {
                OptionGroup rargs = iterOR.next();
                List<Option> rList = rargs.getOption();
                Iterator<Option> iterArg = rList.iterator();
                while (iterArg.hasNext()) {
                    Option ra = iterArg.next();
                    Option temp = new Option();
                    temp.setArgName(ra.getArgName());
                    temp.setDesc(ra.getDesc());
                    temp.setOption(ra.getOption());
                    temp.setType(ra.getType());
                    temp.setValue(ra.getValue());
                    temp.setRequired(true);
                    combined_options.add(temp);
                }
            }
        }

        if (option.size() > 0) {
            currentParagraph = CommonStyles.createHeader(currentParagraph, "ARGUMENTS");
            Iterator<Option> iter = combined_options.iterator();
            while (iter.hasNext()) {
                Option arg = iter.next();
                createArgument(arg);
                if (iter.hasNext()) {
                    currentParagraph = CommonStyles.addEmptyParagraph(currentParagraph);
                }
            }
            currentParagraph = CommonStyles.addEmptyParagraph(currentParagraph);
        }
        List<Description> descs = help.getDesc();
        if (descs.size() > 0) {
            currentParagraph = CommonStyles.createHeader(currentParagraph, "DESCRIPTION");
            Iterator<Description> iter2 = descs.iterator();
            while (iter2.hasNext()) {
                currentParagraph = CommonStyles.createDescription(currentParagraph, iter2.next().getP());
            }
            currentParagraph = CommonStyles.addEmptyParagraph(currentParagraph);
        }    
        List<Example> examples = help.getExample();
        if (examples.size() > 0) {
            currentParagraph = CommonStyles.createHeader(currentParagraph, "EXAMPLES");
            Iterator<Example> iter2 = examples.iterator();
            while (iter2.hasNext()) {
                createExample(iter2.next());
            }
            currentParagraph = CommonStyles.addEmptyParagraph(currentParagraph);
        }
        List<SeeAlso> see_alsos = help.getSeeAlso();
        if (see_alsos.size() > 0) {
            currentParagraph = CommonStyles.createHeader(currentParagraph, "SEE ALSO");
            Iterator<SeeAlso> iter3 = see_alsos.iterator();
            while (iter3.hasNext()) {
                currentParagraph = CommonStyles.createSeeAlso(currentParagraph, iter3.next().getName());
            }
        }
        return;
    }

    private static void createSynopsis(String rv, String name, List<Option> option, List<OptionGroup> option_group) {
        TextPElement tpe;
        TextSpanElement tse;
        TextSpanElement tse2;
        TextSpanElement tse3;
        OdfTextParagraph paragraph;
        TextTabElement tab;

        tpe = new TextPElement(contentDom);
        tpe.setStyleName(indented ? "P_FIXED_INDENTED" : "P_FIXED");
        tse = tpe.newTextSpanElement();
        tse.setTextContent(rv + " ");
        tse.setStyleName("T_SUMMARY");
        tse2 = tpe.newTextSpanElement();
        tse2.setTextContent(name);
        tse2.setStyleName("T_BOLD");
        officeText.appendChild(tpe);

        // Single arguments
        Iterator<Option> iter = option.iterator();
        while (iter.hasNext()) {
            Option Arg = iter.next();
            if (Arg.getOption() != null) {
                tpe = new TextPElement(contentDom);
                tpe.setStyleName(indented ? "P_FIXED_INDENTED" : "P_FIXED");
                tab = tpe.newTextTabElement();
                tab.setTextTabRefAttribute(1);
                tse = tpe.newTextSpanElement();
                if (Arg.getArgName().equals("")) {
                    if (Arg.isRequired()) {
                        if (Arg.getOption().equals(NULL_OPTION)) {
                            tse.setTextContent("");
                        } else {
                            tse.setTextContent("-" + Arg.getOption() + " ");
                        }
                    } else {
                        if (Arg.getOption().equals(NULL_OPTION)) {
                            tse.setTextContent("[");
                        } else {
                            tse.setTextContent("[-" + Arg.getOption() + " ");
                        }
                    }
                } else {
                    if (Arg.isRequired()) {
                        if (Arg.getOption().equals(NULL_OPTION)) {
                            tse.setTextContent("");
                        } else {
                            tse.setTextContent("-" + Arg.getOption() + " ");
                        }
                    } else {
                        if (Arg.getOption().equals(NULL_OPTION)) {
                            tse.setTextContent("[");
                        } else {
                            tse.setTextContent("[-" + Arg.getOption() + " ");
                        }
                    }
                    tse.setStyleName("T_NORMAL");
                    tse2 = tpe.newTextSpanElement();
                    tse2.setTextContent(Arg.getArgName());
                    tse2.setStyleName("T_ITALIC");
                }

                if (!Arg.isRequired()) {
                    tse3 = tpe.newTextSpanElement();
                    tse3.setTextContent("]");
                    tse3.setStyleName("T_NORMAL");
                }
                
                officeText.appendChild(tpe);
            } else {
                tpe = new TextPElement(contentDom);
                tpe.setStyleName(indented ? "P_FIXED_INDENTED" : "P_FIXED");
                tab = tpe.newTextTabElement();
                tab.setTextTabRefAttribute(1);
                tse = tpe.newTextSpanElement();
                if (Arg.isRequired()) {
                    tse.setTextContent(Arg.getArgName());
                } else {
                    tse.setTextContent("[" + Arg.getArgName() + "]");
                }
                tse.setStyleName("T_ITALIC");
                officeText.appendChild(tpe);
            }
        }
        // OR'd arguments
        Iterator<OptionGroup> iter2 = option_group.iterator();
        while (iter2.hasNext()) {
            OptionGroup rargs = iter2.next();
            List<Option> rList = rargs.getOption();
            Iterator<Option> iter3 = rList.iterator();
            boolean first_time = true;
            while (iter3.hasNext()) {
                Option Arg = iter3.next();
                if (!Arg.getOption().equals("null")) {
                    tpe = new TextPElement(contentDom);
                    tpe.setStyleName(indented ? "P_FIXED_INDENTED" : "P_FIXED");
                    tab = tpe.newTextTabElement();
                    tab.setTextTabRefAttribute(1);
                    tse = tpe.newTextSpanElement();
                    if (!first_time) {
                        tse.setTextContent("|-" + Arg.getOption() + " ");
                    } else {
                        tse.setTextContent("[-" + Arg.getOption() + " ");
                    }
                    if (!Arg.getArgName().equals("")) {
                        tse.setStyleName("T_NORMAL");
                        tse2 = tpe.newTextSpanElement();
                        tse2.setTextContent(Arg.getArgName());
                        tse2.setStyleName("T_ITALIC");
                    }
                    
                    if (!iter3.hasNext()) {
                        tse3 = tpe.newTextSpanElement();
                        tse3.setTextContent("]");
                        tse3.setStyleName("T_NORMAL");
                    }
                    
                    officeText.appendChild(tpe);
                    first_time = false;
                } else {
                    String close = null;
                    tpe = new TextPElement(contentDom);
                    tpe.setStyleName(indented ? "P_FIXED_INDENTED" : "P_FIXED");
                    tab = tpe.newTextTabElement();
                    tab.setTextTabRefAttribute(1);
                    tse = tpe.newTextSpanElement();
                    tse.setStyleName("T_NORMAL");
                    if (first_time) {
                        tse.setTextContent("[");
                    } else {
                        tse.setTextContent("|");
                    }
                    tse2 = tpe.newTextSpanElement();
                    tse2.setStyleName("T_ITALIC");
                    tse2.setTextContent(Arg.getArgName());

                    if (!first_time) {
                        close = new String("");
                        if (!iter3.hasNext()) {
                            close = new String("]");
                        }
                    }

                    tse3 = tpe.newTextSpanElement();
                    tse3.setTextContent(close);
                    tse3.setStyleName("T_NORMAL");
                    
                    officeText.appendChild(tpe);
                    first_time = false;
                }
            }
        }
    }

    private static void createArgument(Option option) {
        TextPElement tpe;
        TextSpanElement tse;
        TextSpanElement tse2;
        OdfTextParagraph paragraph;

        tpe = new TextPElement(contentDom);
        tpe.setStyleName(indented ? "P_DEFAULT_INDENTED" : "P_DEFAULT");
        tse = tpe.newTextSpanElement();
 
        if (option.getArgName().equals("")) {
            tse.setTextContent("-" + option.getOption());
            tse.setStyleName("T_BOLD");
        } else {
            tse.setTextContent(option.getArgName() + " ");
            tse.setStyleName("T_BOLDITALIC");
            tse2 = tpe.newTextSpanElement();
            tse2.setTextContent("(" + option.getType() + ")");
            tse2.setStyleName("T_FIXEDBOLD");
        }
        officeText.appendChild(tpe);

        tpe = new TextPElement(contentDom);
        tpe.setStyleName(indented ? "P_DEFAULT_INDENTED" : "P_DEFAULT");
        tpe.setTextContent(option.getDesc());
        officeText.appendChild(tpe);
    }

    private static void createExample(Example ex) {
        TextPElement tpe;
        TextSpanElement tse;
        OdfTextParagraph paragraph;

        tpe = new TextPElement(contentDom);
        tpe.setStyleName(indented ? "P_DEFAULT_INDENTED" : "P_DEFAULT");
        tse = tpe.newTextSpanElement();
        tse.setTextContent(ex.getDesc());
        tse.setStyleName("T_NORMAL");
        officeText.appendChild(tpe);

        tpe = new TextPElement(contentDom);
        tpe.setStyleName(indented ? "P_FIXED_INDENTED" : "P_FIXED");
        tse = tpe.newTextSpanElement();
        tse.setTextContent(ex.getVerbatim());
        tse.setStyleName("T_FIXEDBOLD");
        officeText.appendChild(tpe);
    }
}
