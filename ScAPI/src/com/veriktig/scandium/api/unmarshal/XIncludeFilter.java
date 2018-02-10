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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.veriktig.scandium.api.errors.ScException;
import com.veriktig.scandium.api.state.InternalState;

import tcl.lang.Interp;

import java.util.ArrayList;

class XIncludeFilter {
	private Interp interp;
    private PrintWriter out;
    private int recursion_level;
    private List<Linenumber> linenumbers = new ArrayList<Linenumber>();

    public XIncludeFilter(File fdout) throws ScException {
    	this.interp = InternalState.getInterp();
        try {
            if (fdout.exists()) {
                fdout.delete();
            }
            out = new PrintWriter(new BufferedWriter(new FileWriter(fdout)));
        } catch (IOException ioe) {
          	Object[] objs = new Object[1];
        	objs[0] = fdout.getAbsolutePath();
        	throw new ScException("API-1010", objs);
        }
        recursion_level = 0;
    }

    public List<Linenumber> filter(File fd, boolean bootstrap) throws ScException {
        int linenumber;
        String inputLine;
        String includeLine;
        boolean comment;
        String filename_r;
        BufferedReader in = null;
        BufferedReader include = null;

        if (!fd.exists()) {
          	Object[] objs = new Object[1];
        	objs[0] = fd.getAbsolutePath();
        	throw new ScException("API-1003", objs);
        }

        recursion_level++;
        comment = false;
        filename_r = new String("");
        linenumber = 0;
        try {
            in = new BufferedReader(new FileReader(fd));
            while ((inputLine = in.readLine()) != null) {
                linenumber++;
                // Strip the xml tag from lower layers
                if (inputLine.contains("<?xml")) {
                    if (recursion_level > 1) {
                        continue;
                    }
                }
                // Strip empty lines from lower layers
                if (inputLine.equals("") && recursion_level > 1) {
                    continue;
                }
                if (inputLine.contains("<!--")) {
                    // This is a start of a comment
                    comment = true;
                }
                if (inputLine.contains("-->")) {
                    comment = false;
                    continue;
                }
                if (comment) {
                    continue;
                }

                if (inputLine.contains("<xi:include")) {
                    // Find the quoted file reference
                    int begin = inputLine.indexOf("\"");
                    int end = inputLine.lastIndexOf("\"");
                    String xfile = fd.getParent() + "/" + inputLine.substring(++begin, end);
                    File xFile = new File(xfile);
                    if (!xFile.exists() && bootstrap) {
                        continue;
                    }
                    include = new BufferedReader(new FileReader(xfile));
                    int line_count = 0;
                    while ((includeLine = include.readLine()) != null) {
                        if (includeLine.contains("<xi:include")) {
                            int begin_r = includeLine.indexOf("\"");
                            int end_r = includeLine.lastIndexOf("\"");
                            filename_r = xFile.getParent() + "/" + includeLine.substring(++begin_r, end_r);
                            filter(new File(filename_r), bootstrap);
                            continue;
                        }
                        if (line_count > 1) {
                            writePipe(includeLine, out, fd, linenumber);
                        }
                        line_count++;
                    }
                    include.close();
                } else if (inputLine.contains("xmlns:xi")) {
                    // Drop the Namespace line for XInclude, since there won't be any after filtering
                    continue;
                } else {
                    writePipe(inputLine, out, fd, linenumber);
                }
            }
            in.close();
        } catch (IOException e) {
          	Object[] objs = new Object[1];
        	objs[0] = fd.getAbsolutePath();
        	throw new ScException("API-1003", objs);
        }
        finally {
        	if (in != null) {
        		try {
					in.close();
				} catch (IOException e) {
		          	Object[] objs = new Object[1];
		        	objs[0] = fd.getAbsolutePath();
					throw new ScException("API-1011", objs);
				}
        	}
        	if (include != null) {
        		try {
					include.close();
				} catch (IOException e) {
		          	Object[] objs = new Object[1];
		        	objs[0] = filename_r;
					throw new ScException("API-1011", objs);
				}
        	}
        }
        recursion_level--;
        return (linenumbers);
    }

    protected void writePipe(String inputLine, PrintWriter out, File fd, int linenumber) {
        out.println(inputLine);
        out.flush();
        linenumbers.add(new Linenumber(fd, linenumber));
        //System.out.println(xi_linenumber + " " + fd.getName() + ": " + linenumber);
    }

    public void close() {
        out.flush();
        out.close();
    }

    public void cleanup(File out) {
        out.delete();
    }
}
