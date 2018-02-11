// Copyright (c) 2017 Veriktig.  All rights reserved.

package com.veriktig.documentation.help;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import com.veriktig.documentation.FactoryException;
import com.veriktig.documentation.FileFactory;

public class HelpClassFileFactory extends FileFactory {
    public static void make(File file, String package_name, String class_name, Set<Summary> set) throws FactoryException {
        String comma = null;
        
        FileFactory ff = new FileFactory();
        ff.createOut(file, false);
        ff.printIndented(0, FileFactory.copyright);
        ff.printIndented(0, "");
        ff.printIndented(0, FileFactory.doNotEdit);
        ff.printIndented(0, "");
        ff.printIndented(0, "package " + package_name + ";");
        ff.printIndented(0, "");
        ff.printIndented(0, "import java.util.ListResourceBundle;");
        ff.printIndented(0, "");
        ff.printIndented(0, "public class " + class_name + " extends ListResourceBundle {");
        ff.printIndented(1, "protected Object[][] getContents() {");
        ff.printIndented(2, "return new Object[][] {");
        Iterator<Summary> iter = set.iterator();
        while (iter.hasNext()) {
            Summary ss = iter.next();
            if (iter.hasNext()) {
                comma = new String(",");
            } else {
                comma = new String("");
            }
            ff.printIndented(3, "{\"" + ss.getName() + "\", \"" + ss.getSummary() + "\"}" + comma);
        }
        ff.printIndented(2, "};");
        ff.printIndented(1, "}");
        ff.printIndented(0, "}");
        ff.done();
    }
}
