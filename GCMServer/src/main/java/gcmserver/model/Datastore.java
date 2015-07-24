/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gcmserver.model;

import java.io.InputStream;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

/**
 * Simple implementation of a data store using XML files.
 */
public final class Datastore {

	private final static Logger logger = LoggerFactory
			.getLogger(Datastore.class);
	// Context is the name of package
	private final static String context = "gcmserver.model";

	private Datastore() {
		throw new UnsupportedOperationException();
	}

	// Create an Input Stream for the XML file
	// and pass the Stream as a method parameter
	public static Object unmarshalXMLData(InputStream is) {

		Object obj = null;

		try {
			// Create an instance of JAXB Context
			JAXBContext jContext = JAXBContext.newInstance(context);

			logger.debug("Unmarshalling XML file");
			// Unmarshal the data from InputStream
			obj = jContext.createUnmarshaller().unmarshal(is);
		} catch (Exception e) {
			logger.error("Unmarshalling from XML file failed: " + e.toString());
			e.printStackTrace();
		}

		return obj;
	}

	public static void marshalXML(PrintWriter out, Object object) {

		try {
			// Initialize JAXB Context
			JAXBContext jc = JAXBContext.newInstance(context);

			// Now Create JAXB XML Marshaler
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			logger.debug("Marshalling to XML file");
			// Write the XML File
			m.marshal((Devices) object, out);
		} catch (Exception e) {
			logger.error("Marshalling to XML file failed: " + e.toString());
			e.printStackTrace();
		}

	}
}
