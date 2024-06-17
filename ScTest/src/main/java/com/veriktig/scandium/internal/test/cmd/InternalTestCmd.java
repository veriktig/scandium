// Copyright (c) 2017 Veriktig.  All rights reserved.

package com.veriktig.scandium.internal.test.cmd;


import tcl.lang.Command;
import tcl.lang.Interp;
import tcl.lang.TclObject;

import java.util.Collection;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.osgi.framework.Bundle;

import com.veriktig.scandium.api.SCAPI;
import com.veriktig.scandium.api.annotations.TclCommandName;
import com.veriktig.scandium.api.errors.ScException;
import com.veriktig.scandium.api.help.ScHelpFormatter;
import com.veriktig.scandium.api.help.ScParser;
import com.veriktig.scandium.internal.test.state.TestInternalState;

@TclCommandName("internal_test")
public class InternalTestCmd implements Command {
    @Override
    public void cmdProc(Interp interp, TclObject[] argv) throws ScException {
// START AUTOMATICALLY GENERATED SECTION
// END AUTOMATICALLY GENERATED SECTION
        Bundle bundle = TestInternalState.getBundle();
        // Find all the tests to run and construct a Class[]
        Collection<String> classes = TestInternalState.getTestClasses();
        Class<?>[] class_array = new Class[classes.size()];
        int ii = 0;
        try {
            for (String klass : classes) {
                String klsName = klass.replaceAll(".class", "");
                String clsName = klsName.replace('/', '.');
                Class<?> cc = bundle.loadClass(clsName);
                if (cc != null) {
                    class_array[ii++] = cc;
                    //System.out.println(clsName);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            interp.setResult(SCAPI.ERROR);
        }

        Result result = JUnitCore.runClasses(class_array);

        for (Failure failure : result.getFailures()) {
           System.out.println(failure.toString());
        }

        if (result.wasSuccessful()) {
            System.out.println("PASS");
            interp.setResult(SCAPI.SUCCESS);
        } else {
            System.out.println("FAIL");
            interp.setResult(SCAPI.ERROR);
        }
    }
}
