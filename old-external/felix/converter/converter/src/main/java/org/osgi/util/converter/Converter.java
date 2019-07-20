/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.util.converter;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The Converter service is used to start a conversion. The service is obtained
 * from the service registry. The conversion is then completed via the
 * Converting interface that has methods to specify the target type.
 *
 * @author $Id$
 * @ThreadSafe
 */
@ProviderType
public interface Converter {
	/**
	 * Start a conversion for the given object.
	 *
	 * @param obj The object that should be converted.
	 * @return A {@link Converting} object to complete the conversion.
	 */
	Converting convert(Object obj);

	/**
	 * Start defining a function that can perform given conversions.
	 *
	 * @return A {@link Functioning} object to complete the definition.
	 */
	Functioning function();

	/**
	 * Obtain a builder to create a modified converter based on this converter.
	 * For more details see the {@link ConverterBuilder} interface.
	 *
	 * @return A new Converter Builder.
	 */
	ConverterBuilder newConverterBuilder();
}
