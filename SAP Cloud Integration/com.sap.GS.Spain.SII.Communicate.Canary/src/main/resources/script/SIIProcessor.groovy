import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import groovy.transform.Field;
import java.lang.Exception;
import java.text.Normalizer;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import java.net.URI;

// {C} 2017
// SAP SE
// 17.10.2017
// this script is a part of standard HCI flow for Spain



// constants

public interface SIICommonConstants {
	Integer firstPartLength  = 2000;
	Integer secondPartLength = 6000;
	String operationAlias    = 'currentOperation';
	String byRequest = 'byrequest';
};

public interface SIIServerType {
	String test       = 'test';
	String production = 'production';
};

public interface SIITaxRegion {
	String Spain      = 'spain';
};

public interface SIIServiceType {
	String IncomingInvoices = 'IncomingInvoices';
	String OutgoingInvoices = 'OutgoingInvoices';
	String OutgoingPayments = 'OutgoingPayments';
	String IncomingCash     = 'IncomingCash';		
};

public interface SIIBoolean {
	String Yes = 'yes';	
	String No  = 'no';	
};

public interface SIIConfigurationProperty {
	String soapEnvelope        = 'soapEnvelope';
	String provideLength       = 'provideLength';
	String servers             = 'serverTypes';
	String mapping             = 'namespaceMapping';
	String required            = 'required';
	String commonPath          = 'commonPath';
	String urlPrefix           = 'urlPrefix';
	String rules               = 'rules';
	String implementation      = 'implementationType';
	String implementedServices = 'implementedServices';	
	String allServers          = 'all';
	String fallbacktoCentral   = 'fallback';
};

public interface SIIMessageProperty {
	String erpTag                  = 'ERPTag';
	String reportTo                = 'reportTo';
	String canonicalReportTo       = 'canonicalReportTo';
	String responseTag             = 'responseTag';
	String responseInnerTag        = 'responseInnerTag';
	String responseNamespace       = 'responseNamespace';
	String namespaceMappingEnabled = 'namespaceMappingEnabled';
	String serviceType             = 'serviceType';
	String usageMode               = 'usageMode';
	String canonicalUsageMode      = 'canonicalUsageMode';	
	String logMode                 = 'loggingEnabled';
	String canonicalLogMode        = 'canonicalLoggingEnabled';
	String fullTagMatch            = 'fullTagMatch';
	String NIF                     = 'NIF';
	String keyAliasSuffix          = 'keyAliasSuffix';
	String addNiftoKeyAlias        = 'addNiftoKeyAlias';
	String privateKeyAlias         = 'privateKeyAlias';
	String canonicaladdNIF         = 'canonicaladdNIF';
}


public interface SIIConfigurationValue {
	String full     = '@full';
	String custom   = '@custom';
	String fallback = '@FALLBACK';
	String partial  = '@partial';
}

public interface SIIExceptionPrefix {
	String config = 'SII integration flow configuration error: ';
	String process = 'SII integration flow processing error: '
}


// aliases

@Field regionAliasMap = [
    (SIITaxRegion.Spain)      : [ 'spain', 'default', 'es', 'espana', 'aeat', 'central'  ],

];

@Field usageModeAliasMap = [
	(SIIServerType.test)       : [ 'test', 'testing' ],
	(SIIServerType.production) : [ 'production', 'prod', 'productive' ],
];

@Field byRequestAliasMap = [
	(SIICommonConstants.byRequest) : [ 'byrequest',  'query', 'request',  'from-request',  'by-request',  '@by-request', 
	'@query-parameter', 'query-parameter', 'query-param', 'from-request', 'from-url' ]
];

@Field logYesAliasMap = 
[
	(SIIBoolean.Yes) : [  "YES", "TRUE", "ENABLED", "ON", "USE", "ALWAYS" ]
];

@Field addNifAliasMap = 
[
	(SIIBoolean.Yes) : [  "YES" ],
	(SIIBoolean.No) : [  "NO" ]
];

@Field standardUrlMap = [
	(SIIServiceType.IncomingInvoices) : '/sii/SiiFactFRV1SOAP',
	(SIIServiceType.OutgoingInvoices) : '/sii/SiiFactFEV1SOAP',
	(SIIServiceType.OutgoingPayments) : '/sii/SiiFactPAGV1SOAP',
	(SIIServiceType.IncomingCash)     : '/sii/SiiFactCMV1SOAP'
];


@Field standardNamespaceMap = [
  'ConsultaLR.xsd'           : 'https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/igic/ws/ConsultaLR.xsd',
  'RespuestaConsultaLR.xsd'  : 'https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/igic/ws/RespuestaConsultaLR.xsd',
  'RespuestaSuministro.xsd'  : 'https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/igic/ws/RespuestaSuministro.xsd',
  'SuministroInformacion.xsd': 'https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/igic/ws/SuministroInformacion.xsd',
  'SuministroLR.xsd'         : 'https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/igic/ws/SuministroLR.xsd'
    
];


// service cast configuration map

@Field serviceConfigurationMap = [
	'RegisterOutgoingInvoicesRequest':
	[
		(SIIMessageProperty.responseTag)      : 'RegisterOutgoingInvoicesResponse',
		(SIIMessageProperty.responseInnerTag) : 'RespuestaLRFEmitidas',
		(SIIMessageProperty.serviceType)      : SIIServiceType.OutgoingInvoices
	],
	
	'DeRegisterOutgoingInvoicesRequest':
	[
		(SIIMessageProperty.responseTag)      : 'DeRegisterOutgoingInvoicesResponse',
		(SIIMessageProperty.responseInnerTag) : 'RespuestaLRBajaFEmitidas',
		(SIIMessageProperty.serviceType)      : SIIServiceType.OutgoingInvoices
	],
	
	'RegisterIncomingInvoicesRequest':
	[
		(SIIMessageProperty.responseTag)      : 'RegisterIncomingInvoicesResponse',
		(SIIMessageProperty.responseInnerTag) : 'RespuestaLRFRecibidas',
		(SIIMessageProperty.serviceType)      : SIIServiceType.IncomingInvoices
	],
	'DeRegisterIncomingInvoicesRequest':
	[
		(SIIMessageProperty.responseTag)      : 'DeRegisterIncomingInvoicesResponse',
		(SIIMessageProperty.responseInnerTag) : 'RespuestaLRBajaFRecibidas',
		(SIIMessageProperty.serviceType)      : SIIServiceType.IncomingInvoices					
	],
	'RegisterOutgoingPaymentsVOCVendorsRequest':
	[
		(SIIMessageProperty.responseTag)      : 'RegisterOutgoingPaymentsVOCVendorsResponse',
		(SIIMessageProperty.responseInnerTag) : 'RespuestaLRBajaFRecibidasPagos',
		(SIIMessageProperty.serviceType)      : SIIServiceType.OutgoingPayments		
	],
	'RegisterIncomingCashPaymentsRequest':
	[
	    (SIIMessageProperty.responseTag)      : 'RegisterIncomingCashPaymentsResponse',
	    (SIIMessageProperty.responseInnerTag) : 'RespuestaLRCobrosMetalico',
	    (SIIMessageProperty.serviceType)      : SIIServiceType.IncomingCash
	],
	'DeRegisterIncomingCashPaymentsRequest':
	[
	    (SIIMessageProperty.responseTag)      : 'DeRegisterIncomingCashPaymentsResponse',
	    (SIIMessageProperty.responseInnerTag) : 'RespuestaLRBajaCobrosMetalico',
	    (SIIMessageProperty.serviceType)      : SIIServiceType.IncomingCash
	],
	
		
];

// endpoint / data conversion map

@Field flowConfigurationMap = [
	(SIITaxRegion.Spain):
	[			
	    (SIIConfigurationProperty.soapEnvelope):
	        [
	            (SIIConfigurationProperty.required)      : SIIBoolean.No,
				(SIIConfigurationProperty.provideLength) : SIIBoolean.Yes
	        ],
        (SIIConfigurationProperty.mapping): 
            [
				(SIIConfigurationProperty.required)   : SIIBoolean.Yes,				
                (SIIConfigurationProperty.commonPath) : 'https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/igic/ws/'
            ],
        (SIIConfigurationProperty.servers):
	    [
		    (SIIServerType.test):
		    [
			    (SIIConfigurationProperty.implementation) : SIIConfigurationValue.full,
			    (SIIConfigurationProperty.urlPrefix)      : 'https://sede.gobiernodecanarias.org/tributos/middlewarecaut/services'
		    ],
		    (SIIServerType.production):
		    [
			    (SIIConfigurationProperty.implementation) : SIIConfigurationValue.full,
			    (SIIConfigurationProperty.urlPrefix)      : 'https://sede.gobiernodecanarias.org/tributos/middleware/services',
		    ],
		]
	], 
		
];
		

def isByRequest ( String alias ) {
	def norm = getNormalizedName( alias, byRequestAliasMap );
	return ( norm == SIICommonConstants.byRequest ) ? true : false;
}


def String getNormalizedName( String alias, Map map ) {
    	
	def aliasLc = Normalizer.normalize( alias ? alias : "", Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
    return map.find{ prop -> prop.key.equalsIgnoreCase( aliasLc) ||  prop.value.contains( aliasLc ) }?.key;
}


def mapHasChain( Map aMap, List chain )
{
	def obj = aMap;
	chain.each{ key ->
		if ( obj instanceof Map && obj.containsKey(key) )
			obj = obj.get(key);
		else
			obj = false;
	}					
	return true;
}

def mapGetChain ( Map aMap, List chain )
{
	def obj = aMap;
	
	chain.each{ key ->		
		if ( obj instanceof Map && obj.containsKey(key) )
			obj = obj.get(key);
		else
			obj = false;
	}
	
	return obj;
}

def getMapValue( Map aMap, String property )
{
	return aMap.containsKey(property) ? aMap.get( property) : '';
}




def isYesValue( Map aMap, prop )
{
	def val = ( prop instanceof List ) ? mapGetChain( aMap, prop ) : getMapValue( aMap, prop );	
	return SIIBoolean.Yes.equalsIgnoreCase( val ); 
}


def getQueryParam( Message message, String param )
{
	def headers = message.getHeaders();
	
	def query = getMapValue( headers, Exchange.HTTP_QUERY ).trim();
	
	if ( query.indexOf('=') < 0 )
		return '';
    
    def params = URLEncodedUtils.parse( new URI ( '?' + query), 'UTF-8' );
	
    if ( params )
        return params.find { it -> it.getName().equalsIgnoreCase( param ) }?.getValue();
    
    return '';
    
}


// methods callable from iflow

def Message processData(Message message) {
		throw new Exception( SIIExceptionPrefix.process + 'processData is not implemented');
}


def Message preprocessMessage( Message message ) {
	def ERPTag = '';
	def responseNamespace = '';
	def pattern = '';
	def closingTag = '';
	def len = 0;
	
	def body = message.getBody( java.lang.String ) as String;
	
	
	def bodyPart = ( body.length() > SIICommonConstants.firstPartLength ) ? body.substring( 0, SIICommonConstants.firstPartLength ) : body;
	def bodyRest = '';
			
		//coment line 2.0.4
	//def fullTagMatcher = ( bodyPart =~ /^<(([A-Za-z0-9]+)|(([A-Za-z0-9]+):([A-Za-z0-9]+)))(\s+([A-Za-z0-9:]+="[^"]+"))+>/ );
	
	def fullTagMatcher = ( bodyPart =~ /^<(([A-Za-z0-9-]+)|(([A-Za-z0-9-]+):([A-Za-z0-9-]+)))(\s+([A-Za-z0-9:-]+="[^"]+"))+>/ );
	
	
	if ( !fullTagMatcher)
		throw new Exception( SIIExceptionPrefix.process + 'the request message is not a valid XML');
		
	if ( fullTagMatcher[0][5] )
	{
		ERPTag = fullTagMatcher[0][5];
		pattern = 'xmlns:' + fullTagMatcher[0][4] + '="([^"]+)"';
	} else
	{
		ERPTag = fullTagMatcher[0][2];
		pattern = 'xmlns="([^"]+)"';
	}
		
	def namespaceMatcher = ( fullTagMatcher[0][0] =~ pattern );
	
	responseNamespace = namespaceMatcher[0][1];
	
	message.setProperty( SIIMessageProperty.erpTag, ERPTag );
	message.setProperty( SIIMessageProperty.responseNamespace, responseNamespace);
	message.setProperty( SIIMessageProperty.fullTagMatch, fullTagMatcher[0][0] );
	
	closingTag = '</' + fullTagMatcher[0][1] + '>';
	
	body = body.substring( fullTagMatcher[0][0].length() );	
	
	if ( body.length() > SIICommonConstants.firstPartLength ) {
		len = body.length();
		bodyRest = body.substring( len - SIICommonConstants.firstPartLength  );
		body     = body.substring( 0, len - SIICommonConstants.firstPartLength );
	} else {
		bodyRest = body;
		body = '';
	}
		
	len = bodyRest.indexOf( '</' + fullTagMatcher[0][1] );
	if ( len < 0 )
		throw new Exception( 'Mailformed XML!' );
	body = body + bodyRest.substring( 0, len );
	
	message = setupProcessingProperties( message );
	
	message.setBody( body );
	return message;
	
}


def Message processRequestNamespace( Message message ) {
	
	def body = message.getBody( java.lang.String ) as String;
	def region = message.getProperty( SIIMessageProperty.canonicalReportTo );
	def props = message.getProperties();
	
			
	if ( isYesValue( props, SIIMessageProperty.namespaceMappingEnabled) )
	{
		def localMapping = [:];
		
		def mappingConf = flowConfigurationMap.get(region).get(SIIConfigurationProperty.mapping);
		
		def fullTagMatcher = message.getProperty( SIIMessageProperty.fullTagMatch );
		def fnMatcher = null;
		
		def i = 0;
		def fileName = '';
		def tagName  = '';
		def bodyPart = '';
		def bodyRest = '';
		def replFile = '';
		def commonPath = '';
											
		def nsMatcher = ( fullTagMatcher =~ /xmlns:([^=]*)="([^"]*)"/ );
		
		if ( nsMatcher ) {
			for( i = 0; i < nsMatcher.size(); i++ ) {

				fnMatcher = ( nsMatcher[i][2] =~ /\/([^\/]*)$/ );
				if ( !fnMatcher )
					continue;
			
				fileName = fnMatcher[0][1];
	
				if ( standardNamespaceMap.containsKey( fileName ) && !localMapping.containsKey( fileName) ) {
					if ( mappingConf.containsKey( SIIConfigurationProperty.commonPath ) ) {
						localMapping.put( fileName, 'xmlns:' + nsMatcher[i][1].toString() + '="' + mappingConf.get( SIIConfigurationProperty.commonPath ) + fileName + '"');
					} else {
						if ( mapHasChain( mappingConf, [ SIIConfigurationProperty.rules, fileName ] ) ) {
							localMapping.put( fileName, 'xmlns:' + nsMatcher[i][1] + '="' + mappingConf.get(SIIConfigurationProperty.rules).get( fileName ) + '"');
						}
					}
				}
			}
		}
	
		if ( body.length() > SIICommonConstants.firstPartLength ) {
			bodyPart = body.substring( 0, SIICommonConstants.firstPartLength   );
			bodyRest = body.substring( SIICommonConstants.firstPartLength );
		} else {
			bodyPart = body;
			bodyRest = '';
		}
	 
		def tagMatcher = ( bodyPart =~ /^(<([A-Za-z0-9]+)|(<([A-Za-z0-9]+):([A-Za-z0-9]+)))(?:\s+|>)/ );
	
		if ( tagMatcher )
			localMapping.each { key, value -> bodyPart = bodyPart.replace( tagMatcher[0][1], tagMatcher[0][1] + ' ' + value ) };
			
		if ( bodyRest.length() > SIICommonConstants.secondPartLength )
		{
			body = bodyPart + bodyRest.substring( 0, SIICommonConstants.secondPartLength );
			bodyRest = bodyRest.substring( SIICommonConstants.secondPartLength );			
		} else {
			body = bodyPart + bodyRest;
			bodyRest = '';
		}
	
		i = -1;
		standardNamespaceMap.each{ key, value -> 
			i = ( i > -1 ) ? i : StringUtils.indexOf( body, '"' + value + '"' );
		};
	
		body = body + bodyRest;
		
		if ( i > -1 ) { // patch for XML which retains namespaces within the nodes.
			
			commonPath = mapGetChain( mappingConf, [ SIIConfigurationProperty.commonPath  ] );
						
			standardNamespaceMap.each{ file, fullPath ->
				replFile = mapGetChain( mappingConf, [ SIIConfigurationProperty.rules, file ] );
				replFile = replFile ? replFile : ( commonPath + file );
				body = StringUtils.replace( body,  '"' + fullPath + '"', '"' + replFile + '"' );
			}				
		}
	}
				
	
	def soapConf    = flowConfigurationMap.get(region).get(SIIConfigurationProperty.soapEnvelope);
		
	if ( isYesValue( soapConf, SIIConfigurationProperty.required ) )
		body = '<?xml version="1.0" encoding="UTF-8"?><soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" ><soapenv:Body>'  + body + '</soapenv:Body></soapenv:Envelope>';

	if ( isYesValue( soapConf, SIIConfigurationProperty.provideLength ) )
		message.setHeader( Exchange.CONTENT_LENGTH, body.getBytes("UTF-8").length );
		
	message.setBody( body );

	return message;

}

def Message postprocessMessage( Message message ) {
	
	def props = message.getProperties();
	
	if ( !isYesValue( props, SIIMessageProperty.namespaceMappingEnabled ) )
		return message;

		
	def region = message.getProperty( SIIMessageProperty.canonicalReportTo);
	
	def mappingConf = mapGetChain( flowConfigurationMap, [ region, SIIConfigurationProperty.mapping ] );
	
	def body = message.getBody( java.lang.String ) as String;
	
	def replFile = '';
	
	def commonPath = getMapValue( mappingConf, SIIConfigurationProperty.commonPath );
	
	standardNamespaceMap.each{ file, fullPath ->
		replFile = mapGetChain( mappingConf, [ SIIConfigurationProperty.rules, file ] );
		replFile = replFile ? replFile : ( commonPath + file );
		body = StringUtils.replace( body,  '"' + replFile + '"', '"' + fullPath + '"' );
	}
	
	message.setBody(body);
	
	return message;
	
}


// auxiliary methods

def Message setERPTag( Message message )
{
	
}


def Message setupConfiguration( Message message ) {
    
	def props = message.getProperties();
	
	def region = props.get( SIIMessageProperty.reportTo );	
	def region_normalized = '';

	def usageMode = props.get( SIIMessageProperty.usageMode );	
	def usageMode_normalized = '';
	
//3.0.0
//Add dinmayc private key alias
    def Nif = props.get( SIIMessageProperty.NIF );
    def kSuffix = props.get( SIIMessageProperty.keyAliasSuffix );	
    def addNif = props.get( SIIMessageProperty.addNiftoKeyAlias );	
    def addNif_normalized = '';
        
    addNif_normalized = getNormalizedName( addNif, addNifAliasMap );
    
    if ( addNif_normalized == '' )
		throw new Exception( SIIExceptionPrefix.config + 'Add NIF to private key Alias cannot be empty');
    
   if( addNif_normalized == SIIBoolean.No ) {
    
     message.setProperty( SIIMessageProperty.privateKeyAlias, kSuffix ); 
      }
      else{
      
     message.setProperty( SIIMessageProperty.privateKeyAlias, kSuffix + '_' + Nif.toLowerCase()  ); //Nif
      }
   message.setProperty( 'SAP_DisablePassportTransmission', 'true');
   message.setProperty( 'SAP_CorrelateMPLs', 'false');   
//3.0.0
	
	if ( isByRequest( region ) ) {
		region = getQueryParam( message, SIIMessageProperty.reportTo );
		if ( isByRequest( region ) )
			throw new Exception( SIIExceptionPrefix.config + "recursion in reporting authority definition via query parameter!");
	}
				
	region = region ? region : SIITaxRegion.Spain;
	region_normalized = getNormalizedName( region, regionAliasMap );
	
	if ( isByRequest( usageMode ) ) {
		usageMode = getQueryParam( message, SIIMessageProperty.usageMode );
		if ( isByRequest( usageMode ) )
			throw new Exception( SIIExceptionPrefix.config + "recursion in usage mode definition via query parameter!");
	}
	
	usageMode_normalized = getNormalizedName( usageMode , usageModeAliasMap );
	
	def config = getConfiguration( message );
	
	def serviceType = config.get( SIIMessageProperty.serviceType );
	   
	if ( region_normalized == '' || !flowConfigurationMap.containsKey( region_normalized ) )
		throw new Exception( SIIExceptionPrefix.config + "tax authority of " + ( region ? region : '<empty string>' ) + " is not supported");
		
	if ( usageMode_normalized == '' )
		throw new Exception( SIIExceptionPrefix.config + 'Processing mode cannot be empty');
		
	if ( ! mapHasChain( flowConfigurationMap, [ region_normalized,  SIIConfigurationProperty.servers ] ) )
			throw new Exception( SIIExceptionPrefix.config + "Servers are not defined for tax authority of " + region );

	if ( !mapHasChain( flowConfigurationMap, [ region_normalized, SIIConfigurationProperty.servers, usageMode_normalized ] ) )
			throw new Exception( SIIExceptionPrefix.config + "Processing mode " + usageMode + " is not supported");
			
    def impl = mapGetChain ( flowConfigurationMap, [ region_normalized, SIIConfigurationProperty.servers, usageMode_normalized ] );
					
	def requestUrl = getServiceUrl( impl, serviceType );
	
	if ( requestUrl.equalsIgnoreCase( SIIConfigurationValue.fallback ) ) {
	    impl = flowConfigurationMap.get( SIITaxRegion.Spain ).get(usageMode_normalized);
	    requestUrl = getServiceUrl( impl, serviceType );
	}
	
		
	message.setProperty( SIIMessageProperty.canonicalReportTo, region_normalized );
	message.setProperty( SIIMessageProperty.canonicalUsageMode, usageMode_normalized );
	
	message.setProperty( SIIMessageProperty.canonicaladdNIF, addNif_normalized );

	def logMode = message.getProperty( SIIMessageProperty.logMode );
	def logMode_normalized = SIIBoolean.No;
	
	if ( isByRequest( logMode ) ) {
		logMode = getQueryParam( message, SIIMessageProperty.logMode );
		if ( isByRequest( logMode ) )
			logMode = SIIBoolean.No; // here recursion leads to immediate no
	}
	
	// for logging purposes only, visible if logging mode is on
	logMode_normalized = getNormalizedName( logMode, logYesAliasMap );
	logMode_normalized = logMode_normalized ? logMode_normalized : SIIBoolean.No;		
	message.setProperty( SIIMessageProperty.canonicalLogMode,   logMode_normalized );
	
	if (  isYesValue( flowConfigurationMap, [ region_normalized, SIIConfigurationProperty.mapping, SIIConfigurationProperty.required ] )  )
       message.setProperty( SIIMessageProperty.namespaceMappingEnabled, SIIBoolean.Yes );
        
	message.setHeader( Exchange.DESTINATION_OVERRIDE_URL, requestUrl );
	message.setProperty( 'url', requestUrl );
	return message;
					
}

def String getServiceUrl( HashMap impl, String serviceType ) {
	
	def urlPrefix = '';
	def url  = '';
	
	
    switch( getMapValue(impl, SIIConfigurationProperty.implementation ) )
    {      
		case SIIConfigurationValue.full:
		    if (!impl.containsKey(SIIConfigurationProperty.urlPrefix))
			    throw new Exception(  SIIExceptionPrefix.config + 'host + path prefix are not defined for service call ' + serviceType );
		    return impl.get(SIIConfigurationProperty.urlPrefix) + standardUrlMap.get(serviceType);

		// services must contain list of services available, error if service is unavailable	       
		case SIIConfigurationValue.partial: 
			if (!impl.containsKey(SIIConfigurationProperty.urlPrefix) )
				throw new Exception(  SIIExceptionPrefix.config + 'host + path prefix are not defined for service call ' + serviceType );
			
			if (!impl.containsKey(SIIConfigurationProperty.urlPrefix) )
				throw new Exception(  SIIExceptionPrefix.config + 'services not counted for partial implementation');
		
			if (!impl.get(SIIConfigurationProperty.servers).contains(serviceType)) {
				if ( !isYesValueValue( impl, SIIConfigurationProperty.fallbacktoCentral ) )   
					throw new Exception(  SIIExceptionPrefix.config + 'service ' + serviceType + ' call is not implemented for input parameters');

		     // if service does not exist on region level, use central level
				return SIIConfigurationValue.fallback;
			}
		
			return impl.get(SIIConfigurationProperty.urlPrefix) + standardUrlMap.get(serviceType);
		    
		//for custom services each service must have a full URL provided
	    case SIIConfigurationValue.custom:
			if (!impl.containsKey(SIIConfigurationProperty.servers) )
				throw new Exception( SIIExceptionPrefix.config + 'services not counted for partial implementation of ' + serviceType);
		
				if (!impl.get(SIIConfigurationProperty.servers).containsKey(serviceType) 
					&& !impl.get(SIIConfigurationProperty.servers).containsKey(SIIConfigurationProperty.allServers) 
				) {
					if ( !isYesValueValue( impl, SIIConfigurationProperty.fallbacktoCentral ) )
						throw new Exception(  SIIExceptionPrefix.config + 'service ' + serviceType + ' call is not implemented for input parameters');

					// if service does not exist on region level, use central level
					return SIIConfigurationValue.fallback;
				}
			urlPrefix = getMapValue( impl, SIIConfigurationProperty.urlPrefix );
			return urlPrefix + \
			(   mapHasChain ( impl, [ SIIConfigurationProperty.servers, SIIConfigurationProperty.allServers ] )  \
				? mapGetChain ( impl, [ SIIConfigurationProperty.servers, SIIConfigurationProperty.allServers ] ) \
				: mapGetChain ( impl, [ SIIConfigurationProperty.servers, serviceType  ] ) \
			);
			 
		default:
			throw new Exception(  SIIExceptionPrefix.config + 'implementation for ' + serviceType + ' is not known of missing!');
	}	
	
}


// this function adds properties from serviceConfigurationMapp[<erptag>] to the properties of the message,
// so they can be accessed directly on the next steps

def Message setupProcessingProperties( Message message ) {
		
	def config = getConfiguration( message );
				
	config.each{ prop ->
		message.setProperty(prop.key, prop.value);
	}
	
	return message;
		
}


def Map getConfiguration( Message message ) {
	def props = message.getProperties();
	def erptag = props.get(SIIMessageProperty.erpTag);
	
	if (!serviceConfigurationMap.containsKey(erptag) )
		throw new Exception( SIIExceptionPrefix.config + 'XML Tag ' + ( erptag ? erptag: '"empty tag"') + ' is not known');
		
	return serviceConfigurationMap.get(erptag);	
	
}




	

	
