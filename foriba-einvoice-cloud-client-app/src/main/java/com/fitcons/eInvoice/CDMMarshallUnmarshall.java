package com.fitcons.eInvoice;


import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.fitcons.einvoice.InvoiceListType;
import com.fitcons.einvoice.InvoiceType;

public class CDMMarshallUnmarshall {

	// JAXBContext is thread safe, Marshaller and Unmarshaller are not.
	// Creating a JAXBContext is the expensive operation, M & UM is quick.
	// Reuse JAXBContext and create M & UM each time you need it
	// https://jaxb.java.net/guide/Performance_and_thread_safety.html
	private static JAXBContext ctx = null;
	private static synchronized JAXBContext getContext() throws JAXBException {
		if(ctx == null) {
			ctx = JAXBContext.newInstance(
											InvoiceListType.class, 
											InvoiceType.class
										);
		}
		return ctx;
	}

	public static <T> T unmarshallCDM(Class<T> t, String inputXML) throws JAXBException, ClassNotFoundException {

		StringReader sr = null;
		try {
			Unmarshaller um = getContext().createUnmarshaller();
			sr = new StringReader(inputXML);
			Source src = new StreamSource(sr);
			JAXBElement<T> jaxbElement = um.unmarshal(src, t);
			return jaxbElement.getValue();
		}
		finally {
			UtilsIO.closeStream(sr);
		}
	}

	public static <T> String marshallCDM(T t) throws JAXBException {

		StringWriter sw = null;
		try {
			Marshaller m = getContext().createMarshaller();
			m.setProperty(Marshaller.JAXB_ENCODING, ParamRuntime.UTF8_CHARSET);
			sw = new StringWriter();
			m.marshal(t, sw);

			return sw.toString();
		}
		finally {
			UtilsIO.closeStream(sw);
		}
	}

}
