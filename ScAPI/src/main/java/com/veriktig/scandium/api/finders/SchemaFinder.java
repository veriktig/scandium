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

package com.veriktig.scandium.api.finders;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import com.veriktig.scandium.api.annotations.Schema;
import com.veriktig.scandium.api.annotations.SchemaLoadOrder;

abstract public class SchemaFinder {

    public static Source[] findSchemas(Bundle bundle, String base_name) {
        BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
        String schema_pkg = new String(base_name + ".schemas");

        Collection<String> schemas = bundleWiring.listResources(schema_pkg.replace('.','/'), "*.xsd", BundleWiring.FINDENTRIES_RECURSE);
        int size = schemas.size();
        Source[] schemaSource = new Source[size];

        List<String> list = new ArrayList<String>();
        switch (size) {
        case 0:
            // No schemas found
            return null;
        case 1:
            // No ordering required since there is only one schema.
            for (String s : schemas) {
                list.add(s);
            }
            break;
        default:
            /* 
             * When using Source[] into Xerces, it wants the imported
             * schemas to precede the importers. 
             * I needed this order in core: Structure -> Interconnect -> Chip
             * Other modules should only use a single schema.
             */
            Collection<String> package_info = bundleWiring.listResources(schema_pkg.replace('.','/'), "package-info.class", BundleWiring.FINDENTRIES_RECURSE);
            String[] pkg = package_info.toArray(new String[0]);
            try {
                String klsName = pkg[0].replace(".class", "");
                String clsName = klsName.replace('/','.');
                Class<?> pkginfo = bundle.loadClass(clsName);
                if (pkginfo.isAnnotationPresent(SchemaLoadOrder.class)) {
                    Annotation[] annotations = pkginfo.getAnnotationsByType(Schema.class);
                    Map<Integer, String> m = new HashMap<Integer, String>();
                    for (Annotation a : annotations) {
                        Schema s = (Schema) a;
                        m.put(s.load_order(), s.name());
                    }
                    List<Integer> keys = new ArrayList<Integer>(m.keySet());
                    Collections.sort(keys);
                    for (Integer ii : keys) {
                        String s = new String(schema_pkg.replace('.','/') + "/" + m.get(ii));
                        list.add(s);
                    }
                } else {
                    if (size != 1) {
                        System.out.println("Found multiple schemas. @SchemaLoadOrder may be required in package-info.java");
                    }
                }
            } catch (ClassNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return schemaSource;
            }
        }
        
        int ii = 0;
        for (String schema : list) {
            URL url = bundle.getResource(schema);
            try {
                InputStream inputStream = url.openConnection().getInputStream();
                schemaSource[ii++] = new StreamSource(inputStream);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return schemaSource;
    }
    
}
