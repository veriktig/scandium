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

package tcl.lang.channel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclIO;
import tcl.lang.TclPosixException;
import tcl.lang.TclRuntimeError;

/**
 * Subclass of the abstract class Channel. It implements all of the methods to
 * perform read, write, open, close, etc on a file.
 */

public class OsgiChannel extends Channel {

	/**
	 * Resource files are read only.
	 */

	private InputStream file = null;

	/**
	 * Open a resource with the read/write permissions determined by modeFlags.
	 * This method must be called before any other methods will function
	 * properly.
	 * 
	 * @param interp
	 *            currrent interpreter.
	 * @param fileName
	 *            the absolute path of the resource to open
	 * @param modeFlags
	 *            modes used to open a file for reading, writing, etc
	 * @return the channelId of the file.
	 * @exception TclException
	 *                is thrown when the modeFlags is anything other than RDONLY
	 *                or the resource doesn't exists
	 * @exception IOException
	 *                is thrown when an IO error occurs that was not correctly
	 *                tested for. Most cases should be caught.
	 */

    public String open(Interp interp, String script) throws IOException, TclException {

    	mode = TclIO.RDONLY;
    	
		if (script == null) {
			throw new TclPosixException(interp, TclPosixException.ENOENT, true,
					"ResourceChannel.open: null script");
		}

		// In standard Tcl fashion, set the channelId to be "resource" + the
		// value of the current FileDescriptor.

		String fName = TclIO.getNextDescriptor(interp, "stream");
		setChanName(fName);
		return fName;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see tcl.lang.channel.Channel#implClose()
	 */
	@Override
	void implClose() throws IOException {
		file = null;
	}

	@Override
	String getChanType() {
		return "stream";
	}

	@Override
	protected InputStream getInputStream() throws IOException {
		return null;
	}

	@Override
	protected OutputStream getOutputStream() throws IOException {
		throw new IOException("ResourceChannel: output stream not available");
	}
}
