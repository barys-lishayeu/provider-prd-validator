package com.tsm.prd;

import com.tsm.prd.config.AppConfig;
import com.tsm.prd.config.ConfigUtil;
import com.tsm.prd.config.ProviderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private Main() {
    }

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        final AppConfig appConfig = ConfigUtil.getInstance().load();

        for (ProviderConfig providerConfig : appConfig.getProviderConfigs()) {
            final Class providerClass = Class.forName(providerConfig.getProviderClass());
            final PrdComparator prdComparator = (PrdComparatorImpl) providerClass.newInstance();
            prdComparator.run(providerConfig, appConfig.getRootPath());
        }
    }
}
