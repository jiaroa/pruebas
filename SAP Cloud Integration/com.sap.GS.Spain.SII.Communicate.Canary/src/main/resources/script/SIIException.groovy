// (C) SAP SE 2017
// {C}
// This script is a part of the standard shipment


import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import org.apache.cxf.binding.soap.SoapFault;
import org.apache.commons.lang.StringEscapeUtils;
import javax.xml.namespace.QName;

def Message processData(Message message) {
    
    def oException = message.getProperty('CamelExceptionCaught');

    if ( oException instanceof SoapFault )
        return message;
        
       
    def oQName = new QName(
        'http://schemas.xmlsoap.org/soap/envelope/',
        'Server'
        );     
        
    
    def oSoapFault = new SoapFault( 
            'Integration exception: ' + oException.getClass().toString() + ': ' + StringEscapeUtils.escapeXml(oException.getMessage()),           
            oQName
        );
    
    oSoapFault.setStackTrace( oException.getStackTrace() );
    oSoapFault.initCause( oException.getCause() );
	    
    message.setProperty('CamelExceptionCaught', oSoapFault );
    message.setHeader( 'CamelHttpResponseCode', 500 );
    
    
    return message;
}