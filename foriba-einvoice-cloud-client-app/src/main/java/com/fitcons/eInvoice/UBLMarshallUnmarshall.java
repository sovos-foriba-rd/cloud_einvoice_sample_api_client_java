package com.fitcons.eInvoice;


import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.hr_xml._3.CancelUserAccountType;
import org.hr_xml._3.ProcessUserAccountType;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_2.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNoteType;
//import oasis.names.specification.ubl.schema.xsd.despatchadvice_2.DespatchAdviceType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;



public class UBLMarshallUnmarshall {

	private static final boolean USE_FORMATTED_OUTPUT = true; 

	private static JAXBContext ctxFull = null;
	private static JAXBContext ctxSBD = null;
	private static JAXBContext ctxInv = null;
	private static JAXBContext ctxAR = null;
	private static JAXBContext ctxCN = null;
	private static JAXBContext ctxDA = null;
	private static JAXBContext ctxPUA = null;
	private static JAXBContext ctxCUA = null;

	private static synchronized JAXBContext getFullContext() throws JAXBException {
		if(ctxFull == null) {
			ctxFull = JAXBContext.newInstance(
											StandardBusinessDocument.class, 
											InvoiceType.class, 
											ApplicationResponseType.class, 
											CreditNoteType.class,
											ProcessUserAccountType.class, 
											CancelUserAccountType.class
										);
		}
		return ctxFull;
	}

	private static synchronized JAXBContext getSBDContext() throws JAXBException {
		if(ctxSBD == null) {
			ctxSBD = JAXBContext.newInstance(StandardBusinessDocument.class);
		}
		return ctxSBD;
	}

	private static synchronized JAXBContext getInvContext() throws JAXBException {
		if(ctxInv == null) {
			ctxInv = JAXBContext.newInstance(InvoiceType.class);
		}
		return ctxInv;
	}

	private static synchronized JAXBContext getARContext() throws JAXBException {
		if(ctxAR == null) {
			ctxAR = JAXBContext.newInstance(ApplicationResponseType.class);
		}
		return ctxAR;
	}
	
	private static synchronized JAXBContext getCNContext() throws JAXBException {
		if(ctxCN == null) {
			ctxCN = JAXBContext.newInstance(CreditNoteType.class);
		}
		return ctxCN;
	}
	

	private static synchronized JAXBContext getPUAContext() throws JAXBException {
		if(ctxPUA == null) {
			ctxPUA = JAXBContext.newInstance(ProcessUserAccountType.class);
		}
		return ctxPUA;
	}

	private static synchronized JAXBContext getCUAContext() throws JAXBException {
		if(ctxCUA == null) {
			ctxCUA = JAXBContext.newInstance(CancelUserAccountType.class);
		}
		return ctxCUA;
	}

	public static StandardBusinessDocument unmarshallSBD(String inputXML) throws JAXBException {

		StringReader sr = null;
		try {
			Unmarshaller umSBD = getFullContext().createUnmarshaller();
			sr = new StringReader(inputXML);
			Source src = new StreamSource(sr);
			JAXBElement<StandardBusinessDocument> jaxbElement = umSBD.unmarshal(src, StandardBusinessDocument.class);

			return jaxbElement.getValue();
		}
		finally {
			UtilsIO.closeStream(sr);
		}
	}

	public static <T> T unmarshall(Class<T> t, String inputXML) throws JAXBException {

		StringReader sr = null;
		try {
			Unmarshaller umChildren = getFullContext().createUnmarshaller();
			sr = new StringReader(inputXML);
			Source src = new StreamSource(sr);
			// Source src = new StreamSource(new StringReader(inputXML));
			JAXBElement<T> jaxbElement = umChildren.unmarshal(src, t);

			return jaxbElement.getValue();
		}
		finally {
			UtilsIO.closeStream(sr);
		}
	}
	
	public static String marshallSBD(StandardBusinessDocument sbd) throws JAXBException {

		List<Object> oList = new ArrayList<Object>(sbd.getPackage().getElements().get(0).getElementList().getAny());

		// Backup and remove all embedded objects from envelope
		tr.gov.efatura.package_namespace.Package.Elements.ElementList el = sbd.getPackage().getElements().get(0).getElementList();
		sbd.getPackage().getElements().get(0).setElementList(null);

		// Marshall envelope only
		String ublXML = marshall(sbd);

		// Marshall previously copied elements
		StringBuilder elementsXML = new StringBuilder();
		for(Object o : oList) {
			elementsXML.append(marshall(o) + "\n");
		}

		// Insert marshalled elements into envelope
		int idx = ublXML.indexOf("</Elements>");
		ublXML = ublXML.substring(0, idx) + "<ElementList>\n" + elementsXML.toString() + "</ElementList>\n" + ublXML.substring(idx, ublXML.length());

		// Restore element list
		sbd.getPackage().getElements().get(0).setElementList(el);

		return ublXML;
	}



	public static <T> String marshall(T t) throws JAXBException {

		StringWriter sw = null;
		try {
			sw = new StringWriter();
			// Marshalling SBD
			if(t instanceof StandardBusinessDocument) {
				Marshaller mSBD = getSBDContext().createMarshaller();
				mSBD.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, ConstantsUBL.UBL_ENV_SCHEMA_LOCATION);
				setDefaultMarshallerProperties(mSBD, true);
				mSBD.marshal(t, sw);
			}
			// Marshalling children
			else {
				JAXBContext ctx = null;
				Marshaller mChild = null;
				if(t instanceof InvoiceType) {
					ctx = getInvContext();
					mChild = ctx.createMarshaller();
					setDefaultMarshallerProperties(mChild, false);
					mChild.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, ConstantsUBL.UBL_INV_SCHEMA_LOCATION);
				}
				else if(t instanceof ApplicationResponseType) {
					ctx = getARContext();
					mChild = ctx.createMarshaller();
					setDefaultMarshallerProperties(mChild, false);
					mChild.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, ConstantsUBL.UBL_AR_SCHEMA_LOCATION);
				}
				else if(t instanceof CreditNoteType) {
					ctx = getCNContext();
					mChild = ctx.createMarshaller();
					setDefaultMarshallerProperties(mChild, false);
					mChild.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, ConstantsUBL.UBL_CN_SCHEMA_LOCATION);
				}
				else if(t instanceof ProcessUserAccountType) {
					ctx = getPUAContext();
					mChild = ctx.createMarshaller();
					setDefaultMarshallerProperties(mChild, false);
					//mChild.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, ConstantsUBL.UBL_UA_SCHEMA_LOCATION);
				}
				else if(t instanceof CancelUserAccountType) {
					ctx = getCUAContext();
					mChild = ctx.createMarshaller();
					setDefaultMarshallerProperties(mChild, false);
					//mChild.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, ConstantsUBL.UBL_UA_SCHEMA_LOCATION);
				}
				else {
					throw new RuntimeException("Hatalı sınıf: " + t.getClass().getName());
				}
				mChild.marshal(t, sw);
			}
			return sw.toString();
		}
		finally {
			UtilsIO.closeStream(sw);
		}
	}
	
	private static Marshaller setDefaultMarshallerProperties(Marshaller m, boolean isEnvelope) throws PropertyException {
		m.setProperty(Marshaller.JAXB_ENCODING, ParamRuntime.UTF8_CHARSET);
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, USE_FORMATTED_OUTPUT);
		if(!isEnvelope) {
			m.setProperty("jaxb.fragment", Boolean.TRUE);
			m.setProperty("com.sun.xml.bind.namespacePrefixMapper", new UBLNamespacePrefixMapper());
		}
		return m;
	}
}
