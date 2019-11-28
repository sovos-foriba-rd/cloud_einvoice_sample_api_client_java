package com.fitcons.eInvoice;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Properties;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;

import org.apache.commons.codec.binary.Base64;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import com.fitcoins.eInvoice.test.Application;
import com.fitcons.eInvoice.ParamServices.Direction;
import com.fitcons.eInvoice.ParamServices.UBLDocumentType;
import com.fitcons.eInvoice.ParamServices.UserRole;
import com.fitcons.eInvoice.ParamServices.ViewDocType;
import com.fitcons.eInvoice.UtilsIO.ZipFile;
import com.fitcons.einvoice.ClientEInvoiceServices;
import com.fitcons.einvoice.ClientEInvoiceServicesPort;
import com.fitcons.einvoice.GetEnvelopeStatusFaultMessage;
import com.fitcons.einvoice.GetEnvelopeStatusRequest;
import com.fitcons.einvoice.GetEnvelopeStatusResponse;
import com.fitcons.einvoice.GetEnvelopeStatusResponseType;
import com.fitcons.einvoice.GetInvResponsesFaultMessage;
import com.fitcons.einvoice.GetInvResponsesRequest;
import com.fitcons.einvoice.GetInvResponsesResponse;
import com.fitcons.einvoice.GetInvResponsesResponseInvResponsesType;
import com.fitcons.einvoice.GetInvResponsesResponseType;
import com.fitcons.einvoice.GetInvoiceViewFaultMessage;
import com.fitcons.einvoice.GetInvoiceViewRequest;
import com.fitcons.einvoice.GetInvoiceViewResponse;
import com.fitcons.einvoice.GetRAWUserListFaultMessage;
import com.fitcons.einvoice.GetRAWUserListRequest;
import com.fitcons.einvoice.GetRAWUserListResponse;
import com.fitcons.einvoice.GetUBLFaultMessage;
import com.fitcons.einvoice.GetUBLListFaultMessage;
import com.fitcons.einvoice.GetUBLListRequest;
import com.fitcons.einvoice.GetUBLListResponse;
import com.fitcons.einvoice.GetUBLListResponseType;
import com.fitcons.einvoice.GetUBLRequest;
import com.fitcons.einvoice.GetUBLResponse;
import com.fitcons.einvoice.SendUBLFaultMessage;
import com.fitcons.einvoice.SendUBLRequest;
import com.fitcons.einvoice.SendUBLResponse;
import com.fitcons.einvoice.SendUBLResponseType;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_2.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.UUIDType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;


public class EInvoiceClient {

	private static String userNameProp;
	private static String passwordProp;
	private static String endpointProp;

	static {
		try {
			FileReader reader = new FileReader("src\\main\\resources\\user.properties");
			Properties p = new Properties();
			p.load(reader);
			userNameProp = p.getProperty("username");
			passwordProp = p.getProperty("password");
			endpointProp = p.getProperty("endPoint");
		} catch (Exception e) {
		}
	}

	private static int count = 1;
	private static int childCount = 1;
	private static boolean ID_AUTO_GENERATE = false;
	
	
	/**
	 * @param identifier    Gönderici Birim(GB) ya da Posta Kutusu(PK) etiketi 
	 * @param vkntckn       VKN/TCKN bilgisi 
	 * @param role          Kullanıcı Tipi (GB/PK) 
	 */

	public static void getRawUserList(String identifier, String vkntckn, UserRole role)
			throws IOException {

		try {
			ClientEInvoiceServices clientWS = new ClientEInvoiceServices();
			ClientEInvoiceServicesPort port = clientWS.getClientEInvoiceServicesPort();
			BindingProvider provider = (BindingProvider) port;
			Map<String, Object> reqContext = provider.getRequestContext();
			reqContext.put(BindingProvider.USERNAME_PROPERTY, userNameProp);
			reqContext.put(BindingProvider.PASSWORD_PROPERTY, passwordProp);
			reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointProp);
			long avg = 0;
			for (int i = 1; i <= count; i++) {

				GetRAWUserListRequest req = new GetRAWUserListRequest();
				req.setIdentifier(identifier);
				req.setVKNTCKN(vkntckn);
				req.setRole(role.name());

				long duration = 0;
				try {					
					long start = System.currentTimeMillis();
					GetRAWUserListResponse resp = port.getRAWUserList(req);
					duration = (System.currentTimeMillis() - start);
					avg = ((avg * (i - 1)) + duration) / i;
					
						String userList=new String(Base64.encodeBase64(resp.getDocData()));
						System.out.println(userList);
					System.out.println("\n"+i + " - Duration: " + duration + ", Avg: " + avg);
				} catch (GetRAWUserListFaultMessage fm) {
					System.out.println("FM: " + fm.getFaultInfo().getCode() + " - " + fm.getFaultInfo().getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}
	
	
	/**
	 * @param identifier Faturanın göndericisine/alıcısına ait ait Gönderici
	 *                   Birim(GB) ya da Posta Kutusu(PK) etiketi
	 * @param vkntckn    Faturanın göndericisine/alıcısına ait VKN/TCKN
	 * @param uuid       Fatura ETTN
	 * @param type       Gelen/Gönderilen Belge (OUTBOUND, INBOUND)
	 * @throws IOException
	 */
	public static void getInvResponses(String identifier, String vkntckn, String uuid, Direction type)
			throws IOException {

		try {
			ClientEInvoiceServices clientWS = new ClientEInvoiceServices();
			ClientEInvoiceServicesPort port = clientWS.getClientEInvoiceServicesPort();
			BindingProvider provider = (BindingProvider) port;
			Map<String, Object> reqContext = provider.getRequestContext();
			reqContext.put(BindingProvider.USERNAME_PROPERTY, userNameProp);
			reqContext.put(BindingProvider.PASSWORD_PROPERTY, passwordProp);
			reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointProp);

			long avg = 0;
			for (int i = 1; i <= count; i++) {

				GetInvResponsesRequest req = new GetInvResponsesRequest();
				req.setIdentifier(identifier);
				req.setVKNTCKN(vkntckn);
				req.getUUID().add(uuid);
				req.setType(type.name());

				long duration = 0;
				try {
					long start = System.currentTimeMillis();
					GetInvResponsesResponse resp = port.getInvResponses(req);
					for (GetInvResponsesResponseType iterable_element : resp.getResponse()) {
						for (GetInvResponsesResponseInvResponsesType iterable_element2 : iterable_element.getInvResponses()) {
							iterable_element2.getEnvUUID();
							System.out.println("RAENVUUID:"+iterable_element2.getEnvUUID());
						}
					}
					duration = (System.currentTimeMillis() - start);
					avg = ((avg * (i - 1)) + duration) / i;
					String srSBD = new String("InvoiceUUID:" + resp.getResponse().get(0).getInvoiceUUID());
					
					System.out.println(srSBD);
					
					System.out.println(i + " - Duration: " + duration + ", Avg: " + avg);
				} catch (GetInvResponsesFaultMessage fm) {
					System.out.println("FM: " + fm.getFaultInfo().getCode() + " - " + fm.getFaultInfo().getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done");

	}

	/**
	 * @param uuid       Zarf ETTN 
	 * @param identifier Zarfın göndericisine ait GB/PK etiketi 
	 * @param vkntckn    Zarfın göndericisine ait VKN/TCKN
	 * @throws IOException
	 */
	public static void getEnvelopeStatus(String identifier, String vkntckn, String uuid) throws IOException {
		try {
			
			ClientEInvoiceServices clientWS = new ClientEInvoiceServices();
			ClientEInvoiceServicesPort port = clientWS.getClientEInvoiceServicesPort();
			BindingProvider provider = (BindingProvider) port;
			Map<String, Object> reqContext = provider.getRequestContext();
			reqContext.put(BindingProvider.USERNAME_PROPERTY, userNameProp);
			reqContext.put(BindingProvider.PASSWORD_PROPERTY, passwordProp);
			reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointProp);

			long avg = 0;
			for (int i = 1; i <= count; i++) {

				GetEnvelopeStatusRequest req = new GetEnvelopeStatusRequest();
				req.getUUID().add(uuid);
				req.setIdentifier(identifier);
				req.setVKNTCKN(vkntckn);
				req.getParameters().add("DOC_DATA");

				long duration = 0;
				try {
					long start = System.currentTimeMillis();
					GetEnvelopeStatusResponse resp = port.getEnvelopeStatus(req);
					duration = (System.currentTimeMillis() - start);
					avg = ((avg * (i - 1)) + duration) / i;

					for (GetEnvelopeStatusResponseType respt : resp.getResponse()) {
						System.out.println("UUID:" + respt.getUUID() + "\nIssueDate:" + respt.getIssueDate()
								+ "\nDocumentTypeCode :" + respt.getDocumentTypeCode() + "\nDocumentType :"
								+ respt.getDocumentType() + "\nResponseCode :" + respt.getResponseCode()
								+ "\nDescription :" + respt.getDescription());
						System.out.println(i + " - Duration: " + duration + ", Avg: " + avg);

					}
				} catch (GetEnvelopeStatusFaultMessage fm) {
					System.out.println("FM: " + fm.getFaultInfo().getCode() + " - " + fm.getFaultInfo().getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}

	/**
	 * @param uuid       Fatura ETTN 
	 * @param identifier Faturanın gönderici ya da alıcısına ait Gönderici Birim(GB) ya da Posta Kutusu(PK) etiketi 
	 * @param vkntckn    Faturanın gönderici ya da alıcısına ait VKN/TCKN
	 * @param type       Gelen/Gönderilen Fatura (OUTBOUND, INBOUND) 
	 * @param viewtype    Doküman Türü (HTML, PDF, XSLT, HTML_DEFAULT, PDF_DEFAULT
	 */
	public static void getInvoiceView(String vkntckn,String uuid, String identifier, Direction type,
			ViewDocType viewtype) throws IOException {

		try {
			ClientEInvoiceServices clientWS = new ClientEInvoiceServices();
			ClientEInvoiceServicesPort port = clientWS.getClientEInvoiceServicesPort();
			BindingProvider provider = (BindingProvider) port;
			Map<String, Object> reqContext = provider.getRequestContext();
			reqContext.put(BindingProvider.USERNAME_PROPERTY, userNameProp);
			reqContext.put(BindingProvider.PASSWORD_PROPERTY, passwordProp);
			reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointProp);

			long avg = 0;
			for (int i = 1; i <= count; i++) {

				GetInvoiceViewRequest req = new GetInvoiceViewRequest();
				req.setUUID(uuid);
				req.setIdentifier(identifier);
				req.setVKNTCKN(vkntckn);
				req.setType(type.name());
				req.setDocType(viewtype.name());

				long duration = 0;
				try {
					long start = System.currentTimeMillis();
					GetInvoiceViewResponse resp = port.getInvoiceView(req);
					duration = (System.currentTimeMillis() - start);
					avg = ((avg * (i - 1)) + duration) / i;
					String srSBD = new String(resp.getDocData());

					String str = srSBD;
					File file = new File("Invoiceview." + viewtype);
					if (!file.exists()) {
						file.createNewFile();
					}
					FileWriter fileWriter = new FileWriter(file, false);
					BufferedWriter bWriter = new BufferedWriter(fileWriter);
					bWriter.write(str);
					bWriter.close();

					System.out.println(i + " - Duration: " + duration + ", Avg: " + avg);
				} catch (GetInvoiceViewFaultMessage fm) {
					System.out.println("FM: " + fm.getFaultInfo().getCode() + " - " + fm.getFaultInfo().getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done");

	}
	
	
	/**
	 * @param custInvID  Müşteri Fatura ID 
	 * @param identifier Faturanın gönderici ya da alıcısına ait Gönderici Birim(GB) ya da Posta Kutusu(PK) etiketi 
	 * @param vkntckn    Faturanın gönderici ya da alıcısına ait VKN/TCKN
	 * @param type       Gelen/Gönderilen Fatura (OUTBOUND, INBOUND) 
	 * @param viewtype    Doküman Türü (HTML, PDF, XSLT, HTML_DEFAULT, PDF_DEFAULT
	 */
	public static void getInvoiceView(String vkntckn , String identifier, Direction type,
			ViewDocType viewtype,String custInvID) throws IOException {

		try {
			ClientEInvoiceServices clientWS = new ClientEInvoiceServices();
			ClientEInvoiceServicesPort port = clientWS.getClientEInvoiceServicesPort();
			BindingProvider provider = (BindingProvider) port;
			Map<String, Object> reqContext = provider.getRequestContext();
			reqContext.put(BindingProvider.USERNAME_PROPERTY, userNameProp);
			reqContext.put(BindingProvider.PASSWORD_PROPERTY, passwordProp);
			reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointProp);

			long avg = 0;
			for (int i = 1; i <= count; i++) {

				GetInvoiceViewRequest req = new GetInvoiceViewRequest();
				req.setCustInvID(custInvID);
				req.setIdentifier(identifier);
				req.setVKNTCKN(vkntckn);
				req.setType(type.name());
				req.setDocType(viewtype.name());

				long duration = 0;
				try {
					long start = System.currentTimeMillis();
					GetInvoiceViewResponse resp = port.getInvoiceView(req);
					duration = (System.currentTimeMillis() - start);
					avg = ((avg * (i - 1)) + duration) / i;
					String srSBD = new String(resp.getDocData());

					String str = srSBD;
					File file = new File("Invoiceview." + viewtype);
					if (!file.exists()) {
						file.createNewFile();
					}
					FileWriter fileWriter = new FileWriter(file, false);
					BufferedWriter bWriter = new BufferedWriter(fileWriter);
					bWriter.write(str);
					bWriter.close();

					System.out.println(i + " - Duration: " + duration + ", Avg: " + avg);
				} catch (GetInvoiceViewFaultMessage fm) {
					System.out.println("FM: " + fm.getFaultInfo().getCode() + " - " + fm.getFaultInfo().getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done");

	}

	/**
	 * @param identifier Belgenin gönderici ya da alıcısına ait Gönderici Birim(GB) ya da Posta Kutusu(PK) etiketi
	 * @param vkntckn    Belgenin gönderici ya da alıcısına ait VKN/TCKN 
	 * @param uuid       Belge ETTN
	 * @param type       Gelen/Gönderilen Belge (OUTBOUND, INBOUND)
	 * @param docType    Belge Türü (ENVELOPE, INVOICE, APP_RESP, SYS_RESP) 
	 * @throws IOException
	 */
	public static void getUBL(String identifier, String vkntckn, String uuid, UBLDocumentType doctype, Direction type)
			throws IOException {
		try {
			ClientEInvoiceServices clientWS = new ClientEInvoiceServices();
			ClientEInvoiceServicesPort port = clientWS.getClientEInvoiceServicesPort();
			BindingProvider provider = (BindingProvider) port;
			Map<String, Object> reqContext = provider.getRequestContext();
			reqContext.put(BindingProvider.USERNAME_PROPERTY, userNameProp);
			reqContext.put(BindingProvider.PASSWORD_PROPERTY, passwordProp);
			reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointProp);

			long avg = 0;
			for (int i = 1; i <= count; i++) {

				GetUBLRequest req = new GetUBLRequest();
				req.setIdentifier(identifier);
				req.setVKNTCKN(vkntckn);
				req.getUUID().add(uuid);
				req.setDocType(doctype.name());
				req.setType(type.name());

				long duration = 0;
				try {
					long start = System.currentTimeMillis();
					GetUBLResponse resp = port.getUBL(req);
					duration = (System.currentTimeMillis() - start);
					avg = ((avg * (i - 1)) + duration) / i;
					String srSBD = new String(resp.getDocData().get(0));
					System.out.println(srSBD);
					System.out.println(i + " - Duration: " + duration + ", Avg: " + avg);
				} catch (GetUBLFaultMessage fm) {
					System.out.println("FM: " + fm.getFaultInfo().getCode() + " - " + fm.getFaultInfo().getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}

	/**
	 * @param identifier Belgenin gönderici ya da alıcısına ait Gönderici Birim(GB) ya da Posta Kutusu(PK) etiketi 
	 * @param vkntckn    Belgenin gönderici ya da alıcısına ait VKN/TCKN 
	 * @param uuid       Belge ETTN listesi 
	 * @param docType    Belge Türü (ENVELOPE, INVOICE, APP_RESP, SYS_RESP
	 * @param type       Gelen/Gönderilen Belge (OUTBOUND, INBOUND) 
	 * @throws IOException
	 */
	public static void getUBLList(String identifier, String vkntckn, String uuid, UBLDocumentType docType,
			Direction type) throws IOException {
		try {
			ClientEInvoiceServices clientWS = new ClientEInvoiceServices();
			ClientEInvoiceServicesPort port = clientWS.getClientEInvoiceServicesPort();
			BindingProvider provider = (BindingProvider) port;
			Map<String, Object> reqContext = provider.getRequestContext();
			reqContext.put(BindingProvider.USERNAME_PROPERTY, userNameProp);
			reqContext.put(BindingProvider.PASSWORD_PROPERTY, passwordProp);
			reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointProp);

			long avg = 0;
			for (int i = 1; i <= count; i++) {

				GetUBLListRequest req = new GetUBLListRequest();
				req.setIdentifier(identifier);
				req.setVKNTCKN(vkntckn);
				req.getUUID().add(uuid);
				req.setDocType(docType.name());
				req.setType(type.name());

				long duration = 0;
				try {
					long start = System.currentTimeMillis();
					GetUBLListResponse resp = port.getUBLList(req);
					duration = (System.currentTimeMillis() - start);
					for (GetUBLListResponseType iterable_element : resp.getUBLList()) {
						String getUblListResponse = "UUID:" + iterable_element.getUUID() + "\nidentifier:"
								+ iterable_element.getIdentifier() + "\nVKN_TCKN:"
								+ iterable_element.getVKNTCKN() + "\nEnvUUID:"
								+ iterable_element.getEnvUUID() + "\nID:" + iterable_element.getID()
								+ "\nCustInvID:" + iterable_element.getCustInvID() + "\nInsertDateTime:"
								+ iterable_element.getInsertDateTime();
						System.out.println(getUblListResponse);
					}
					avg = ((avg * (i - 1)) + duration) / i;
					System.out.println(i + " - Duration: " + duration + ", Avg: " + avg);
				} catch (GetUBLListFaultMessage fm) {
					System.out.println("FM: " + fm.getFaultInfo().getCode() + " - " + fm.getFaultInfo().getMessage());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done");

	}
	/**
	 * @param identifier Belgenin gönderici ya da alıcısına ait Gönderici Birim(GB) ya da Posta Kutusu(PK) etiketi 
	 * @param vkntckn    Belgenin gönderici ya da alıcısına ait VKN/TCKN 
	 * @param docType    Belge Türü (ENVELOPE, INVOICE, APP_RESP, SYS_RESP
	 * @param type       Gelen/Gönderilen Belge (OUTBOUND, INBOUND) 
	 * @param FromDate   Başlangıç tarihi ve zamanı
	 * @param ToDate     Bitiş tarihi ve zamanı 
	 * @throws IOException
	 */
	public static void getUBLList(String identifier, String vkntckn, UBLDocumentType docType, Direction type,
			String FromDate, String ToDate) throws IOException {
		try {
			ClientEInvoiceServices clientWS = new ClientEInvoiceServices();
			ClientEInvoiceServicesPort port = clientWS.getClientEInvoiceServicesPort();
			BindingProvider provider = (BindingProvider) port;
			Map<String, Object> reqContext = provider.getRequestContext();
			reqContext.put(BindingProvider.USERNAME_PROPERTY, userNameProp);
			reqContext.put(BindingProvider.PASSWORD_PROPERTY, passwordProp);
			reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointProp);
			XMLGregorianCalendar fromDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(FromDate);
			XMLGregorianCalendar toDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(ToDate);
			
			long avg = 0;

			for (int i = 1; i <= count; i++) {

				GetUBLListRequest req = new GetUBLListRequest();
				req.setIdentifier(identifier);
				req.setVKNTCKN(vkntckn);
				req.setDocType(docType.name());
				req.setType(type.name());
				req.setFromDate(fromDate);
				req.setToDate(toDate);

				long duration = 0;
				try {
					long start = System.currentTimeMillis();
					GetUBLListResponse resp = port.getUBLList(req);
					duration = (System.currentTimeMillis() - start);
					avg = ((avg * (i - 1)) + duration) / i;
					for (GetUBLListResponseType iterable_element : resp.getUBLList()) {
						String getUblListResponse = "UUID:" + iterable_element.getUUID() + "\nidentifier:"
								+ iterable_element.getIdentifier() + "\nVKN_TCKN:"
								+ iterable_element.getVKNTCKN() + "\nEnvUUID:"
								+ iterable_element.getEnvUUID() + "\nID:" + iterable_element.getID()
								+ "\nCustInvID:" + iterable_element.getCustInvID() + "\nInsertDateTime:"
								+ iterable_element.getInsertDateTime()+"\n";
						System.out.println(getUblListResponse);
					}
					System.out.println(i + " - Duration: " + duration + ", Avg: " + avg);
				} catch (GetUBLListFaultMessage fm) {
					System.out.println("FM: " + fm.getFaultInfo().getCode() + " - " + fm.getFaultInfo().getMessage());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done");

	}

	/**
	 * @param vkntckn            VKN/TCKN bilgisi
	 * @param doctype            Belge tipi(envelope)
	 * @param senderIdentifier   Gönderici etiketi
	 * @param receiverIdentifier Alıcı etiketi
	 * @throws IOException
	 */

	public static void sendUBLAR(String vkntckn, UBLDocumentType doctype, String senderIdentifier,
			String receiverIdentifier) throws IOException {

		try {

			ClientEInvoiceServices clientWS = new ClientEInvoiceServices();
			ClientEInvoiceServicesPort port = clientWS.getClientEInvoiceServicesPort();
			BindingProvider provider = (BindingProvider) port;
			Map<String, Object> reqContext = provider.getRequestContext();
			reqContext.put(BindingProvider.USERNAME_PROPERTY, userNameProp);
			reqContext.put(BindingProvider.PASSWORD_PROPERTY, passwordProp);
			reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointProp);
			String ublXML = UtilsIO.readStringFromInputStream(Application.class.getResourceAsStream("\\envAR.xml"));
			StandardBusinessDocument sbd = UBLMarshallUnmarshall.unmarshallSBD(ublXML);

			long avg = 0;
			for (int i = 1; i <= count; i++) {
				ApplicationResponseType art = (ApplicationResponseType) sbd.getPackage().getElements().get(0)
						.getElementList().getAny().get(0);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(art);
				ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
				ObjectInputStream ois = new ObjectInputStream(bais);
				art = (ApplicationResponseType) ois.readObject();
				UUIDType ut = new UUIDType();
				String UUID = UtilsUUID.generateRandomUUID();
				ut.setValue(UUID);
				art.setUUID(ut);
				String ubl = UBLMarshallUnmarshall.marshall(art);
				ZipFile zipFile = new ZipFile();
				zipFile.name = UUID + ".xml";
				zipFile.data = ubl.getBytes(ParamRuntime.UTF8_CHARSET);
				byte[] zipData = UtilsIO.zip(zipFile);
				SendUBLRequest req = new SendUBLRequest();
				req.setVKNTCKN(vkntckn);
				req.setDocType(doctype.name());
				req.setSenderIdentifier(senderIdentifier);
				req.setReceiverIdentifier(receiverIdentifier);
				req.setDocData(zipData);
				long duration = 0;
				try {
					long start = System.currentTimeMillis();
					SendUBLResponse resp = port.sendUBL(req);
					duration = (System.currentTimeMillis() - start);
					avg = ((avg * (i - 1)) + duration) / i;
					for (SendUBLResponseType respt : resp.getResponse()) {
						System.out.println("Resp: " + respt.getEnvUUID() + " - " + respt.getUUID() + " - "
								+ respt.getID() + " - " + respt.getCustInvID());
					}
					System.out.println(i + " - Duration: " + duration + ", Avg: " + avg);
				} catch (SendUBLFaultMessage fm) {
					System.out.println("FM: " + fm.getFaultInfo().getCode() + " - " + fm.getFaultInfo().getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Done");
	}

	/**
	 * @param vkntckn            Gönderici VKN/TCKN 
	 * @param doctype            Belge Türü (ENVELOPE, INVOICE) 
	 * @param senderIdentifier   Gönderici Etiketi (zarfsız gönderimlerde zorunludur.)
	 * @param receiverIdentifier Alıcı Etiketi (zarfsız gönderimlerde zorunludur.) 
	 * @throws IOException
	 */

	public static void sendUBL(String vkntckn, UBLDocumentType doctype, String senderIdentifier,
			String receiverIdentifier) throws IOException {

		try {
			ClientEInvoiceServices clientWS = new ClientEInvoiceServices();
			ClientEInvoiceServicesPort port = clientWS.getClientEInvoiceServicesPort();
			BindingProvider provider = (BindingProvider) port;
			Map<String, Object> reqContext = provider.getRequestContext();
			reqContext.put(BindingProvider.USERNAME_PROPERTY, userNameProp);
			reqContext.put(BindingProvider.PASSWORD_PROPERTY, passwordProp);
			reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointProp);
			String ublXML = UtilsIO
					.readStringFromInputStream(Application.class.getResourceAsStream("\\envInvoice.xml"));
			StandardBusinessDocument sbd = UBLMarshallUnmarshall.unmarshallSBD(ublXML);

			long avg = 0;
			for (int i = 1; i <= count; i++) {

				String envUUID = UtilsUUID.generateRandomUUID();
				sbd.getStandardBusinessDocumentHeader().getDocumentIdentification().setInstanceIdentifier(envUUID);
				InvoiceType it = (InvoiceType) sbd.getPackage().getElements().get(0).getElementList().getAny().get(0);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(it);
				sbd.getPackage().getElements().get(0).getElementList().getAny().clear();

				for (int j = 1; j <= childCount; j++) {
					ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
					ObjectInputStream ois = new ObjectInputStream(bais);
					it = (InvoiceType) ois.readObject();
					UUIDType ut = new UUIDType();
					ut.setValue(UtilsUUID.generateRandomUUID());
					it.setUUID(ut);
					IDType invIDType = new IDType();
					invIDType.setValue(UtilsUUID.generateRandomID("TST"));
					if (!ID_AUTO_GENERATE) {
						it.setID(invIDType);
					} else {
						it.getAdditionalDocumentReference().get(0).setID(invIDType);
					}
					sbd.getPackage().getElements().get(0).getElementList().getAny().add(it);
				}
				sbd.getPackage().getElements().get(0).setElementCount(childCount);

				String ubl = UBLMarshallUnmarshall.marshallSBD(sbd);
				byte[] zipData = UBLPackageManager.packageSBDXMLasBinaryData(envUUID, ubl.getBytes("UTF-8"));

				SendUBLRequest req = new SendUBLRequest();
				req.setVKNTCKN(vkntckn);
				req.setDocType(doctype.name());
				req.setSenderIdentifier(senderIdentifier);
				req.setReceiverIdentifier(receiverIdentifier);
				req.setDocData(zipData);

				long duration = 0;
				try {
					long start = System.currentTimeMillis();
					SendUBLResponse resp = port.sendUBL(req);
					duration = (System.currentTimeMillis() - start);
					avg = ((avg * (i - 1)) + duration) / i;
					for (SendUBLResponseType respt : resp.getResponse()) {
						System.out.println("EnvUUID: " + respt.getEnvUUID() + " UUID: " + respt.getUUID() + " ID: "
								+ respt.getID() + " - " + respt.getCustInvID());
					}
					System.out.println(i + " - Duration: " + duration + ", Avg: " + avg);
				} catch (SendUBLFaultMessage fm) {
					System.out.println("FM: " + fm.getFaultInfo().getCode() + " - " + fm.getFaultInfo().getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}
	
	
}
