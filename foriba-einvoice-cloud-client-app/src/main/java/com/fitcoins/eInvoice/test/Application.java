package com.fitcoins.eInvoice.test;

import java.io.IOException;

import com.fitcons.eInvoice.EInvoiceClient;
import com.fitcons.eInvoice.ParamServices;
import com.fitcons.eInvoice.ParamServices.Direction;
import com.fitcons.eInvoice.ParamServices.UBLDocumentType;
import com.fitcons.eInvoice.ParamServices.UserRole;
import com.fitcons.eInvoice.ParamServices.ViewDocType;



public class Application {

	private static String vkntckn = "123456789"; 
	private static String uuid = "1f03d513-0dba-41a0-9e53-08c2faaa1426";

	private static String custInvID = "";
	private static String identifierpk = "urn:mail:testpk@test.com";
	private static String filterVKNTCKN = "123456789";
	private static String identifier = "urn:mail:testgb@test.com";

	private static Direction type = ParamServices.Direction.OUTBOUND;
	private static ViewDocType viewtype = ParamServices.ViewDocType.HTML;
	private static UBLDocumentType doctype = ParamServices.UBLDocumentType.ENVELOPE;
	private static UserRole role = ParamServices.UserRole.PK;

	private static String registerAfter = "2019-07-01T12:31:24.000+02:00";
	private static String fromDate 		= "2019-11-25T12:31:24.000+02:00";
	private static String toDate 		= "2019-11-26T12:31:24.000+02:00";

	public static void main(String[] args) throws IOException {
		 
		
		/* sendUBL-Application_Response Method */
// 		EInvoiceClient.sendUBLAR(vkntckn,doctype,identifier,identifierpk);

		
		/* sendUBL-Invoice Method */
//		EInvoiceClient.sendUBL(vkntckn, doctype, identifier, identifierpk);

		
		/* getUBLList Method */
		//2 farklı parametre verilebilir.(identifier,vkntckn,uuid,doctype,type)||(identifier,vkntckn,doctype,type,fromDate,toDate)
//		EInvoiceClient.getUBLList(identifier,vkntckn,doctype,type,fromDate,toDate);

		
		/* getEnvelopeStatus Method */
//		EInvoiceClient.getEnvelopeStatus(identifier, vkntckn, uuid);

		
		/* getUBL Method */
//		EInvoiceClient.getUBL(identifier,vkntckn,uuid,doctype,type);

		
		/* getInvoiceView Method */
		//2 farklı parametre verilebilir.(vkntckn,uuid,identifier,type,viewtype)||(vkntckn,identifier,type,viewtype,custInvID)
//		EInvoiceClient.getInvoiceView(vkntckn,uuid,identifier,type,viewtype);
		 
		
		/* getInvResponses Method */
//		EInvoiceClient.getInvResponses(identifier,vkntckn,uuid,type);
		
		
		/* getRawUserList Method */
//		EInvoiceClient.getRawUserList(identifier, vkntckn, role);
	}
}
