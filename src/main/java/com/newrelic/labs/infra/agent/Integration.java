package com.newrelic.labs.infra.agent;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.newrelic.labs.infra.output.infra.InfraPublisher;
import com.newrelic.labs.infra.output.insights.ClientConnectionConfiguration;
import com.newrelic.labs.infra.output.insights.InsightsClient;
import com.newrelic.labs.infra.output.insights.InsightsPublisher;
import com.newrelic.labs.infra.output.insights.MultiThreadedInsightsClient;

public abstract class Integration<T extends Arguments> {
	
	private String integrationName;
	private String integrationVersion;
	private Publisher publisher;
	private List<Entity<T>> entities;
	private long pollInterval = 30;
	private boolean verbose = false;
	
	public Integration(String integrationName, String integrationVersion) {
		this.integrationName = integrationName;
		this.integrationVersion = integrationVersion;
		entities = new ArrayList<Entity<T>>();
	}
	
	public String getIntegrationName() {
		return integrationName;
	}
	
	public String getIntegrationVersion() {
		return integrationVersion;
	}

	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}

	public abstract Entity<T> newEntity(String name) throws Exception;
	
	public abstract void init() throws Exception;

	private void poll() throws Exception {
		for (Entity<T> entity: entities) {
			entity.poll();
		}
	}

	public void shutdown() {
		for (Entity<T> entity: entities) {
			entity.cleanup();
		}
		publisher.shutdown();
	}
	
	protected void addEntity(Entity<T> e) {
		entities.add(e);
		e.setIntegration(this);
	}

	protected void publish(String entityName, String entityType, Collection<MetricSet> metricSets) {
		publisher.publish(entityName, entityType, metricSets);
	}

	public void run(T argsBean) throws Exception {		
		// argsBean contains parsed arguments in case of infra mode or when args are specified on command line params
		// otherwise they contain the default bean properties
		
		// MODE and VERBOSE must always be specified as ENV variable or CMD line parameters.
		String mode = System.getenv("MODE");
		if (mode == null) {
			mode = argsBean.getMode();
			if (mode == null) {
				System.err.println("Error: Please specify a valid mode argument");
				System.exit(-1);
			}
		}
		
		String verboseEnv = System.getenv("VERBOSE");
		if (verboseEnv == null) {
			setVerbose(argsBean.getVerbose());
		} else {
			if (verboseEnv.equalsIgnoreCase("true")) {
				setVerbose(true);
				System.out.println("Setting verbose mode to true");
			}
		}
		
		if (mode.equalsIgnoreCase("infra")) {
			initalizeInfraOutput();
			init();
			
			Entity<T> entity = newEntity("");
			addEntity(entity);
			entity.validate(argsBean);
			entity.init(argsBean);
		} else {
			String configFile = System.getenv("CONFIG_FILE");
			if (configFile == null) {
				configFile = argsBean.getConfigFile();
				if (configFile == null) {
					System.err.println("Error: Please specify a valid config_file argument");
					System.exit(-1);
				}
			}
			String interval = System.getenv("POLL_INTERVAL");
			if (interval != null) {
				try {
					this.pollInterval = Integer.parseInt(interval);
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}

			init();
			
			File configFolder = new File(configFile).getAbsoluteFile().getParentFile();
			File nrConfigFile = new File(configFolder, "newrelic-insights.yml");
			initalizeInsightsOutput(nrConfigFile);
			
			EntityConnectionInfo entityConnectionInfo = loadIIBConfigFile(new File(configFile));
			
			for (EntityInstanceInfo instanceInfo: entityConnectionInfo.getInstances()) {
				if (instanceInfo.getName() == null) {
					System.err.println("Instance name cannot be null or empty. Please specify a valid name");
				}
				Entity<T> entity = newEntity(instanceInfo.getName());
				addEntity(entity);
				Map<String, Object> iProps = instanceInfo.getArguments();
				T argumentsBean = entity.populateBean(iProps);
				entity.validate(argumentsBean);
				entity.init(argumentsBean);
			}
		}
		
		Thread hook = new Thread(() -> {
			shutdown();
		});
		Runtime.getRuntime().addShutdownHook(hook);
		
		if (mode.equalsIgnoreCase("infra")) {
			poll();
		} else {	
			ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
			ScheduledFuture<?> future = executor.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					try {
						poll();
					} catch (Throwable t) {
						System.err.println(t.getMessage());
					}
				}
				
			}, 0, pollInterval, TimeUnit.SECONDS); 
			
			try {
				// getting the future's response will block forever unless an exception is thrown
				future.get();
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			} catch (CancellationException e) {
				System.err.println("integration poll cancelled");
			} catch (ExecutionException e) {
				// ExecutionException will wrap any java.lang.Error from the polling thread that
				// we should not catch there (e.g. OutOfMemoryError)
				throw e;
			} finally {
				// clean up
				future.cancel(true);
				executor.shutdown();
			}
		}

	}
	
	private void initalizeInfraOutput() throws Exception {
		setPublisher(new InfraPublisher());
	}

	private void initalizeInsightsOutput(File nrConfigFile) throws Exception {
		try {
			InsightsConnectionInfo nrConfig = loadNewRelicYaml(nrConfigFile);

			String insightsAPIInsertKey = nrConfig.getInsights_insert_key();
			String insightsAPIUrl = nrConfig.getCollector_url();
			String proxyHost = nrConfig.getProxy_host();
			Integer proxyPort = nrConfig.getProxy_port();
			String proxyUser = nrConfig.getProxy_user();
			String proxyPassword = nrConfig.getProxy_password();

			InsightsClient insightsClient = new MultiThreadedInsightsClient(insightsAPIUrl, insightsAPIInsertKey);
			ClientConnectionConfiguration httpConfig = new ClientConnectionConfiguration();
			httpConfig.setMaximumConnectionsPerRoute(3);
			httpConfig.setMaximumConnections(3);
			if (proxyHost != null) {
				httpConfig.setUseProxy(true);
				httpConfig.setProxyScheme("http");
				httpConfig.setProxyHost(proxyHost);
				httpConfig.setProxyPort(proxyPort);
			}
			if (proxyUser != null) {
				httpConfig.setProxyUsername(proxyUser);
			}
			if (proxyPassword != null) {
				httpConfig.setProxyPassword(proxyPassword);
			}
			insightsClient.init(httpConfig);
			setPublisher(new InsightsPublisher(insightsClient, verbose));
		} catch (Exception e) {
			throw e;
		}
	}
	
	private InsightsConnectionInfo loadNewRelicYaml(File nrConfigYamlFile) throws Exception {
		try {
			Yaml yaml = new Yaml(new Constructor(InsightsConnectionInfo.class));
			InsightsConnectionInfo newrelicYaml = yaml.load(new FileInputStream(nrConfigYamlFile));
			return newrelicYaml;
		} catch (Exception e) {
			System.err.println("Unable to parse configuration from " + nrConfigYamlFile);
			throw e;
		}
	}
	
	private EntityConnectionInfo loadIIBConfigFile(File iibConfigFile) throws Exception {
		try {
			Yaml yaml = new Yaml(new Constructor(EntityConnectionInfo.class));
			EntityConnectionInfo iibYaml = yaml.load(new FileInputStream(iibConfigFile));
			return iibYaml;
		} catch (Exception e) {
			System.err.println("Unable to parse configuration from " + iibConfigFile);
			throw e;
		}
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
}
