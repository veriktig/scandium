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

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class Linenumber {
    private File fd;
    private int line;

    public Linenumber (File fd, int line) {
        this.fd = fd;
        this.line = line;
    }

    public String getFile() {
        return (fd.toString());
    }

    public String getLine() {
        String inputLine;
        int linenumber = 0;
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(fd));
            while ((inputLine = in.readLine()) != null) {
                linenumber++;
                if (linenumber == line) {
                    return (inputLine);
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR: IOException processing " + fd.getName());
            e.printStackTrace();
            return (null);
        }
        finally {
        	if (in != null) {
        		try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
        return (null);
    }

    public int getLinenumber() {
        return (line);
    }
}
