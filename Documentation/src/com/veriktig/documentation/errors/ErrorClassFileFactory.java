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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.veriktig.documentation.FactoryException;
import com.veriktig.documentation.FileFactory;

public class ErrorClassFileFactory extends FileFactory {
	public static void make(File file, String package_name, String class_name, Set<Summary> set) throws FactoryException {
		String comma = null;

        // Sort by error code
        List<Summary> list = new ArrayList<Summary>(set);
        Collections.sort(list, (o1, o2) -> o1.getName().compareTo(o2.getName()));
		
		FileFactory ff = new FileFactory();
		ff.createOut(file, false);
		ff.printIndented(0, FileFactory.copyright);
		ff.printIndented(0, "");
		ff.printIndented(0,  FileFactory.doNotEdit);
		ff.printIndented(0,  "");
		ff.printIndented(0, "package " + package_name + ";");
		ff.printIndented(0,  "");
		ff.printIndented(0,  "import java.util.ListResourceBundle;");
		ff.printIndented(0,  "");
		ff.printIndented(0,  "import com.veriktig.scandium.api.errors.ErrorMessage;");
		ff.printIndented(0,  "import com.veriktig.scandium.api.SCAPI;");
		ff.printIndented(0,  "");
		ff.printIndented(0,  "public class " + class_name + " extends ListResourceBundle {");
		ff.printIndented(1,  "protected Object[][] getContents() {");
		ff.printIndented(2,  "return new Object[][] {");
        Iterator<Summary> iter = list.iterator();
        while (iter.hasNext()) {
            Summary ss	 = iter.next();
            if (iter.hasNext()) {
            	comma = new String(",");
            } else {
            	comma = new String("");
            }
            ff.printIndented(3, "{\"" + ss.getName() + "\", new ErrorMessage(SCAPI.ErrorType." + ss.getSeverity().toUpperCase() + ", \"" + ss.getSummary() + "\")}" + comma);
        }
        ff.printIndented(2,  "};");
		ff.printIndented(1,  "}");
		ff.printIndented(0,  "}");
        ff.done();
	}
}
