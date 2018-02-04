/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.felix.http.base.internal.logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * This abstract class adds support for using a LogService
 */
public class LogServiceEnabledLogger
{
    // name of the LogService class (this is a string to not create a reference to the class)
    // With R7, LogService is deprecated but extends the newer LoggerFactory
    private static final String LOGSERVICE_CLASS = "org.osgi.service.log.LogService";

    private static final String JUL_LOGGER = "org.apache.felix.http.log.jul";

    // the log service to log messages to
    protected final ServiceTracker<Object, Object> logServiceTracker;

    private volatile InternalLogger currentLogger;

    protected volatile int trackingCount = -2;

    private final InternalLogger defaultLogger;

    public LogServiceEnabledLogger(final BundleContext bundleContext)
    {
        Object julLogOpt = bundleContext.getProperty(JUL_LOGGER);
        if ( julLogOpt == null ) {
            julLogOpt = System.getProperty(JUL_LOGGER);
        }
        if ( julLogOpt != null ) {
            this.defaultLogger = new JDK14Logger();
        } else {
            this.defaultLogger = new ConsoleLogger();
        }

        // Start a tracker for the log service
        // we only track a single log service which in reality should be enough
        logServiceTracker = new ServiceTracker<>( bundleContext, LOGSERVICE_CLASS, new ServiceTrackerCustomizer<Object, Object>()
        {
            private volatile boolean hasService = false;

            @Override
            public Object addingService(final ServiceReference<Object> reference)
            {
                if ( !hasService )
                {
                    final Object logService = bundleContext.getService(reference);
                    if ( logService != null )
                    {
                        hasService = true;
                        final LogServiceSupport lsl = new LogServiceSupport(logService);
                        return lsl;
                    }
                }
                return null;
            }

            @Override
            public void modifiedService(final ServiceReference<Object> reference, final Object service)
            {
                // nothing to do
            }

            @Override
            public void removedService(final ServiceReference<Object> reference, final Object service)
            {
                hasService = false;
                bundleContext.ungetService(reference);
            }
        } );
        logServiceTracker.open();
    }

    /**
     * Close the logger
     */
    public void close()
    {
        // stop the tracker
        logServiceTracker.close();
    }

    /**
     * Returns {@code true} if logging for the given level is enabled.
     */
    public boolean isLogEnabled(final int level)
    {
        return getLogger().isLogEnabled(level);
    }

    /**
     * Method to actually emit the log message. If the LogService is available,
     * the message will be logged through the LogService. Otherwise the message
     * is logged to stdout (or stderr in case of LOG_ERROR level messages),
     *
     * @param level The log level of the messages. This corresponds to the log
     *          levels defined by the OSGi LogService.
     * @param message The message to print
     * @param ex The <code>Throwable</code> causing the message to be logged.
     */
    public void log(final int level, final String message, final Throwable ex)
    {
        if ( isLogEnabled( level ) )
        {
            getLogger().log(level, message, ex);
        }
    }

    /**
     * Get the internal logger
     * @return The internal logger
     */
    InternalLogger getLogger()
    {
        if ( this.trackingCount < this.logServiceTracker.getTrackingCount() )
        {
            final Object logServiceSupport = this.logServiceTracker.getService();
            if ( logServiceSupport == null )
            {
                this.currentLogger = this.getDefaultLogger();
            }
            else
            {
                this.currentLogger = ((LogServiceSupport)logServiceSupport).getLogger();
            }
            this.trackingCount = this.logServiceTracker.getTrackingCount();
        }
        return currentLogger;
    }

    InternalLogger getDefaultLogger()
    {
        return this.defaultLogger;
    }
}