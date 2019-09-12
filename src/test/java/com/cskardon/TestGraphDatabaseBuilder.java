package com.cskardon;

import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;

public class TestGraphDatabaseBuilder extends GraphDatabaseBuilder
{
    public TestGraphDatabaseBuilder( DatabaseCreator creator )
    {
        super( creator );
        super.config.put( GraphDatabaseSettings.pagecache_memory.name(), "8m" );
    }
}