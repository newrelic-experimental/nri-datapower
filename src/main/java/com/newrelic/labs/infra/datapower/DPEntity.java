package com.newrelic.labs.infra.datapower;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.newrelic.labs.infra.agent.Entity;
import com.newrelic.labs.infra.agent.MetricSet;
import com.newrelic.labs.infra.agent.SourceType;
import com.newrelic.labs.infra.datapower.config.DPClass;
import com.newrelic.labs.infra.datapower.config.DPCollect;
import com.newrelic.labs.infra.datapower.config.DPMetric;
import com.newrelic.labs.infra.datapower.util.SOAPLoggingHandler;

public class DPEntity<T> extends Entity<DPArgs> {
	private static Logger logger = LoggerFactory.getLogger(DPEntity.class);

	private static final QName portQName = new QName("http://www.datapower.com/schemas/management", "xml-mgmt");
	private Service service = null;

	private String host;
	private int port;
	private String username;
	private String password;
	private String[] domains;

	private boolean VERBOSE = true;

	private DPCollect systemMetricDefs = null;
	private DPCollect domainMetricDefs = null;

	private static List<String> hosts = new ArrayList<String>();

	// This should probably not be needed if the certificates are issues properly
	// and match the hostname
	static {
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				if (hosts.contains(hostname))
					return true;
				return false;
			}
		});
	}

	private TransformerFactory factory = TransformerFactory.newInstance();
	
	protected DPEntity(String name, DPCollect systemMetricDefs, DPCollect domainMetricDefs) {
		super(name);
		this.systemMetricDefs = systemMetricDefs;
		this.domainMetricDefs = domainMetricDefs;
	}

	@Override
	public String getEntityType() {
		return "Datapower";
	}

	@Override
	protected DPArgs populateBean(Map<String, Object> instanceProperties) throws Exception {
		DPArgs argumentsBean = new DPArgs();
		if (instanceProperties.get("host") != null) {
			String host = (String) instanceProperties.get("host");
			argumentsBean.setHost(host);
		}

		if (instanceProperties.get("port") != null) {
			Integer port = (Integer) instanceProperties.get("port");
			argumentsBean.setPort(port);
		}

		if (instanceProperties.get("username") != null) {
			String username = (String) instanceProperties.get("username");
			argumentsBean.setUsername(username);
		}

		if (instanceProperties.get("password") != null) {
			String password = (String) instanceProperties.get("password");
			argumentsBean.setPassword(password);
		}
		
		if (instanceProperties.get("domains") != null) {
			String domains = (String) instanceProperties.get("domains");
			argumentsBean.setDomains(domains);
		}
		return argumentsBean;
	}
	
	@Override
	protected void validate(DPArgs argumentsBean) throws Exception {
		if (argumentsBean.getDomains() == null) {
			throw new Exception("Domains cannot be null");
		}
	}
	
	@Override
	protected void init(DPArgs argumentsBean) throws Exception {
		host = argumentsBean.getHost();
		port = argumentsBean.getPort();
		username = argumentsBean.getUsername();
		password = argumentsBean.getPassword();
		String domainsStr = argumentsBean.getDomains();
		String[] domainsArr = domainsStr.split(",");
		domains = new String[domainsArr.length];
		for (int i = 0; i < domainsArr.length; i++) {
			domains[i] = domainsArr[i].trim();
		}
		hosts.add(host);
		setUpDispatcher();
	}

	@Override
	public void poll() throws Exception {
		logger.debug("getting metrics from datapower manager " + this.getName());
		
		// fetch system metrics
		List<DPClass> dpSystemClasses = systemMetricDefs.getCollect();
		try {
			for (DPClass dpClass : dpSystemClasses) {
				fetchMetricsFromDatapower("default", dpClass.getMetric_class(), dpClass.getEvent_type(),
						dpClass.getMetrics());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		// fetch domain metrics
		for (String domainName: domains) {
			List<DPClass> dpDomainClasses = domainMetricDefs.getCollect();
			try {
				for (DPClass dpClass : dpDomainClasses) {
					fetchMetricsFromDatapower(domainName, dpClass.getMetric_class(), dpClass.getEvent_type(),
							dpClass.getMetrics());
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		this.publish();
	}

	@Override
	public void cleanup() {

	}

	public void fetchMetricsFromDatapower(String domainName, String datapowerClass, String eventType,
			List<DPMetric> metricDefs) throws Exception {
		String request = "<dp:request xmlns:dp=\"http://www.datapower.com/schemas/management\" domain=\"" + domainName
				+ "\"><dp:get-status class=\"" + datapowerClass + "\"/></dp:request>";

		Dispatch<Source> sourceDispatch = service.createDispatch(portQName, Source.class, Service.Mode.PAYLOAD);
		// Cast the proxy to a BindingProvider
		BindingProvider bindingProvider = (BindingProvider) sourceDispatch;
		// Get the request context
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		// Configure basic auth
		requestContext.put(BindingProvider.USERNAME_PROPERTY, username);
		requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);

		// Configure session preference
		// requestContext.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);

		// if logging is enabled
		if (VERBOSE) {
			javax.xml.ws.Binding binding = bindingProvider.getBinding();
			List<javax.xml.ws.handler.Handler> handlerList = binding.getHandlerChain();
			handlerList.add(new SOAPLoggingHandler());
			binding.setHandlerChain(handlerList);
		}

		Source result = sourceDispatch.invoke(new StreamSource(new StringReader(request)));

		DOMResult domResult = sourceToDOMResult(result);

		Node rootNode = domResult.getNode();

		Node responseNode = rootNode.getFirstChild();
		if (responseNode.getNodeType() == Node.ELEMENT_NODE) {
			Element responseElement = (Element) responseNode;
			NodeList responseElementChildren = responseElement.getChildNodes();
			for (int i = 0; i < responseElementChildren.getLength(); i++) {
				Node responseElementChildNode = responseElementChildren.item(i);
				if (responseElementChildNode.getLocalName().equalsIgnoreCase("status")) {
					NodeList instanceElements = responseElementChildNode.getChildNodes();

					for (int j = 0; j < instanceElements.getLength(); j++) {
						Map<String, String> instanceMetrics = new HashMap<String, String>();
						MetricSet metricset = newMetricSet(eventType);
						metricset.setAttribute("domain", domainName);

						Node instanceElement = instanceElements.item(j);
						// String instanceName = instanceElement.getLocalName();
						// System.out.println("I = " + instanceName);
						NodeList metricElements = instanceElement.getChildNodes();
						for (int k = 0; k < metricElements.getLength(); k++) {
							Node metricElement = metricElements.item(k);
							if (metricElement.getNodeType() == Node.ELEMENT_NODE) {
								instanceMetrics.put(metricElement.getLocalName(), metricElement.getTextContent());
							}
						}
						
						for (DPMetric metricDef : metricDefs) {
							String metricName = metricDef.getMetric_name();
							String sourceType = metricDef.getMetric_type();
							String metricValue = instanceMetrics.get(metricName);
							if (metricValue != null) {
								switch (sourceType) {
								case "attribute":
									metricset.setAttribute(metricName, metricValue);
									break;
								case "gauge":
									try {
										Float fv = Float.parseFloat(metricValue);
										metricset.setMetric(metricName, fv, SourceType.GAUGE);
									} catch (Throwable t) {
										logger.error(t.getMessage(), t);
									}
									break;
								case "delta":
									try {
										Float fv = Float.parseFloat(metricValue);
										metricset.setMetric(metricName, fv, SourceType.DELTA);
									} catch (Throwable t) {
										logger.error(t.getMessage(), t);
									}
									break;
								case "rate":
									try {
										Float fv = Float.parseFloat(metricValue);
										metricset.setMetric(metricName, fv, SourceType.RATE);
									} catch (Throwable t) {
										logger.error(t.getMessage(), t);
									}
									break;
								}
							} else {
								logger.error("metric value not found for " + datapowerClass + " // " + metricName);
							}
						}
					}
				}
			}
		}
	}

	private DOMResult sourceToDOMResult(Source result) {
		DOMResult xmlResult = new DOMResult();
		try {
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");

			transformer.transform(result, xmlResult);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return xmlResult;
	}

	protected void setUpDispatcher() throws Exception {
		logger.info("Initializing dispatcher");
		URL url = new URL("https", host, port, "/service/mgmt/current");
		QName serviceName = new QName("http://www.datapower.com/schemas/management", "xml-mgmt");
		service = Service.create(serviceName);
		service.addPort(portQName, SOAPBinding.SOAP11HTTP_BINDING, url.toString());
		logger.info("Setup complete for datapower-url: " + url.toString());
	}

}
