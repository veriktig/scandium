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

package org.junit.internal.matchers;

import org.hamcrest.Description;
//import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import org.junit.internal.Throwables;

/**
 * A matcher that delegates to throwableMatcher and in addition appends the
 * stacktrace of the actual Throwable in case of a mismatch.
 *
 * @deprecated use {@code org.hamcrest.junit.JunitMatchers.isThrowable()}
 * or {@code org.hamcrest.junit.JunitMatchers.isException()}
 */
@Deprecated
public class StacktracePrintingMatcher<T extends Throwable> extends
        org.hamcrest.TypeSafeMatcher<T> {

    private final Matcher<T> throwableMatcher;

    public StacktracePrintingMatcher(Matcher<T> throwableMatcher) {
        this.throwableMatcher = throwableMatcher;
    }

    public void describeTo(Description description) {
        throwableMatcher.describeTo(description);
    }

    @Override
    protected boolean matchesSafely(T item) {
        return throwableMatcher.matches(item);
    }

    @Override
    protected void describeMismatchSafely(T item, Description description) {
        throwableMatcher.describeMismatch(item, description);
        description.appendText("\nStacktrace was: ");
        description.appendText(readStacktrace(item));
    }

    private String readStacktrace(Throwable throwable) {
        return Throwables.getStacktrace(throwable);
    }

    //@Factory
    public static <T extends Throwable> Matcher<T> isThrowable(
            Matcher<T> throwableMatcher) {
        return new StacktracePrintingMatcher<T>(throwableMatcher);
    }

    //@Factory
    public static <T extends Exception> Matcher<T> isException(
            Matcher<T> exceptionMatcher) {
        return new StacktracePrintingMatcher<T>(exceptionMatcher);
    }
}
