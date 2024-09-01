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

package com.veriktig.scandium.api.help;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.veriktig.scandium.api.SCAPI;

/**
 * A formatter of help messages for command line options.
 *
 * <p>Example:</p>
 * 
 * <pre>
 * Options options = new Options();
 * options.addOption(OptionBuilder.withLongOpt("file")
 *                                .withDescription("The file to be processed")
 *                                .hasArg()
 *                                .withArgName("FILE")
 *                                .isRequired()
 *                                .create('f'));
 * options.addOption(OptionBuilder.withLongOpt("version")
 *                                .withDescription("Print the version of the application")
 *                                .create('v'));
 * options.addOption(OptionBuilder.withLongOpt("help").create('h'));
 * 
 * String header = "Do something useful with an input file\n\n";
 * String footer = "\nPlease report issues at http://example.com/issues";
 * 
 * HelpFormatter formatter = new HelpFormatter();
 * formatter.printHelp("myapp", header, options, footer, true);
 * </pre>
 * 
 * This produces the following output:
 * 
 * <pre>
 * usage: myapp -f &lt;FILE&gt; [-h] [-v]
 * Do something useful with an input file
 * 
 *  -f,--file &lt;FILE&gt;   The file to be processed
 *  -h,--help
 *  -v,--version       Print the version of the application
 * 
 * Please report issues at http://example.com/issues
 * </pre>
 * 
 * @version $Id$
 */
public class ScHelpFormatter extends HelpFormatter
{

    /**
     * Appends the usage clause for an Option to a StringBuffer.  
     *
     * @param buff the StringBuffer to append to
     * @param option the Option to append
     * @param required whether the Option is required or not
     */
    @Override
    public void appendOption(StringBuilder buff, Option option, boolean required)
    {
        if (!required)
        {
            buff.append("[");
        }

        if (!option.getOpt().equals(SCAPI.NULL_OPTION))
        {
            if (option.getOpt() != null)
            {
                buff.append("-").append(option.getOpt());
            }
            else 
            {
                buff.append("--").append(option.getLongOpt());
            }
        }
        
        // if the Option has a value and a non blank argname
        if (option.hasArg() && (option.getArgName() == null || option.getArgName().length() != 0))
        {
            buff.append(option.getOpt() == null ? longOptSeparator : " ");
            buff.append("<").append(option.getArgName() != null ? option.getArgName() : getArgName()).append(">");
        }
        
        // if the Option is not a required option
        if (!required)
        {
            buff.append("]");
        }
    }

    /**
     * Render the specified Options and return the rendered Options
     * in a StringBuffer.
     *
     * @param sb The StringBuffer to place the rendered Options into.
     * @param width The number of characters to display per line
     * @param options The command line Options
     * @param leftPad the number of characters of padding to be prefixed
     * to each line
     * @param descPad the number of characters of padding to be prefixed
     * to each description line
     *
     * @return the StringBuffer with the rendered Options contents.
     */
    @Override
    public StringBuffer renderOptions(StringBuffer sb, int width, Options options, int leftPad, int descPad)
    {
        final String lpad = createPadding(leftPad);
        final String dpad = createPadding(descPad);

        // first create list containing only <lpad>-a,--aaa where 
        // -a is opt and --aaa is long opt; in parallel look for 
        // the longest opt string this list will be then used to 
        // sort options ascending
        int max = 0;
        List<StringBuffer> prefixList = new ArrayList<StringBuffer>();

        List<Option> optList = options.helpOptions();

        if (getOptionComparator() != null)
        {
            Collections.sort(optList, getOptionComparator());
        }

        for (Option option : optList)
        {
            StringBuffer optBuf = new StringBuffer();

            if (option.getOpt().equals(SCAPI.NULL_OPTION))
            {
                optBuf.append(lpad);
            } 
            else if (option.getOpt() == null)
            {
                optBuf.append(lpad).append("   ").append(getLongOptPrefix()).append(option.getLongOpt());
            }
            else
            {
                optBuf.append(lpad).append(getOptPrefix()).append(option.getOpt());

                if (option.hasLongOpt())
                {
                    optBuf.append(',').append(getLongOptPrefix()).append(option.getLongOpt());
                }
            }

            if (option.hasArg())
            {
                String argName = option.getArgName();
                if (argName != null && argName.length() == 0)
                {
                    // if the option has a blank argname
                    optBuf.append(' ');
                }
                else
                {
                    optBuf.append(option.hasLongOpt() ? longOptSeparator : " ");
                    optBuf.append("<").append(argName != null ? option.getArgName() : getArgName()).append(">");
                }
            }

            prefixList.add(optBuf);
            max = optBuf.length() > max ? optBuf.length() : max;
        }

        int x = 0;

        for (Iterator<Option> it = optList.iterator(); it.hasNext();)
        {
            Option option = it.next();
            StringBuilder optBuf = new StringBuilder(prefixList.get(x++).toString());

            if (optBuf.length() < max)
            {
                optBuf.append(createPadding(max - optBuf.length()));
            }

            optBuf.append(dpad);

            int nextLineTabStop = max + descPad;

            if (option.getDescription() != null)
            {
                optBuf.append(option.getDescription());
            }

            renderWrappedText(sb, width, nextLineTabStop, optBuf.toString());

            if (it.hasNext())
            {
                sb.append(getNewLine());
            }
        }

        return sb;
    }
}
