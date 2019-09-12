package com.cskardon;

import org.neo4j.kernel.extension.ExtensionType;
import org.neo4j.kernel.extension.KernelExtensionFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.impl.spi.KernelContext;
import org.neo4j.kernel.lifecycle.Lifecycle;
import org.neo4j.kernel.lifecycle.LifecycleAdapter;
import org.neo4j.logging.internal.LogService;

/**
 * FunctionsKernelExtensionFactory
 * Registers/Unregisters the functions event handler with the Neo4j kernel.
 */
public class FunctionsKernelExtensionFactory extends KernelExtensionFactory<FunctionsKernelExtensionFactory.Dependencies> {
    private static final String KEY = "functions";
    public FunctionsKernelExtensionFactory() {
        super(ExtensionType.DATABASE, KEY);
    }
    public interface Dependencies {
        /**
         * Provides access to the Neo4j database.
         *
         * @return the database
         */
        GraphDatabaseService getGraphDatabaseService();

        /**
         * Provides access to the Neo4j database configuration.
         *
         * @return the configuration
         */
        Config getConfig();

        /**
         * Provides access to the Neo4j log service
         *
         * @return the log service
         */
        LogService getLogService();
    }

    @Override
    public Lifecycle newInstance(KernelContext kernelContext, Dependencies dependencies) {
        return new LifecycleAdapter() {
            @Override
            public void start() throws Throwable {
                LogService logService = dependencies.getLogService();
                logService.getUserLog(getClass()).info("Registering Encryption Functions"); // neo4j.log
                logService.getInternalLog(getClass()).info("Registering Encryption Functions"); // debug.log

                Functions.Initialize(dependencies.getConfig());
            }

            @Override
            public void shutdown() throws Throwable {
                LogService logService = dependencies.getLogService();
                logService.getUserLog(getClass()).info("Unregistering Encryption Functions"); // neo4j.log
                logService.getInternalLog(getClass()).info("Unregistering Encryption Functions"); // debug.log
            }

        };
    }
}