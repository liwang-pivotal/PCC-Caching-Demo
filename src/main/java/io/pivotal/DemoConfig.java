package io.pivotal;

import java.net.URI;
import java.util.List;
import java.util.Properties;

import io.pivotal.auth.EnvParser;
import io.pivotal.domain.Customer;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.support.GemfireCacheManager;

@Configuration
@EnableCaching
public class DemoConfig {
	
//	public ServiceConnectorConfig createGemfireConnectorConfig() {
//
//        GemfireServiceConnectorConfig gemfireConfig = new GemfireServiceConnectorConfig();
//        gemfireConfig.setPoolSubscriptionEnabled(true);
//        gemfireConfig.setPdxSerializer(new ReflectionBasedAutoSerializer(".*"));
//        gemfireConfig.setPdxReadSerialized(false);
//
//        return gemfireConfig;
//    }
//    
//	@Bean(name = "gemfireCache")
//    public ClientCache getGemfireClientCache() throws Exception {		
//		
//		Cloud cloud = new CloudFactory().getCloud();
//		ClientCache clientCache = cloud.getServiceConnector("test-pcc", ClientCache.class,  createGemfireConnectorConfig());
//
//        return clientCache;
//    }
	
	
	@Bean(name = "gemfireCache")
	public ClientCache clientCache() throws Exception {
        Properties props = new Properties();
        props.setProperty("security-client-auth-init", "io.pivotal.auth.ClientAuthInitialize.create");
        
        ClientCacheFactory ccf = new ClientCacheFactory(props);
		
        List<URI> locatorList = EnvParser.getInstance().getLocators();
		for (URI locator : locatorList) {
			ccf.addPoolLocator(locator.getHost(), locator.getPort());
		}

        ccf.setPdxSerializer(new ReflectionBasedAutoSerializer(".*"));
		ccf.setPdxReadSerialized(false);
		ccf.setPoolSubscriptionEnabled(true);
        
		ClientCache clientCache = ccf.create();

		return clientCache;
	}


	@Bean(name = "customer")
	public Region<String, Customer> customerRegion(@Autowired ClientCache clientCache) {
		ClientRegionFactory<String, Customer> customerRegionFactory = clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY);

		Region<String, Customer> customerRegion = customerRegionFactory.create("customer");

		return customerRegion;
	}
	
	@Bean(name="cacheManager")
	public GemfireCacheManager createGemfireCacheManager(@Autowired ClientCache gemfireCache) {

		GemfireCacheManager gemfireCacheManager = new GemfireCacheManager();
		gemfireCacheManager.setCache(gemfireCache);

		return gemfireCacheManager;
	}

}
