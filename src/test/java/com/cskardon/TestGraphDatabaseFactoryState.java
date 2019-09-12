package com.cskardon;
import org.neo4j.graphdb.factory.GraphDatabaseFactoryState;
import org.neo4j.io.fs.FileSystemAbstraction;
import org.neo4j.logging.LogProvider;
import org.neo4j.time.SystemNanoClock;

public class TestGraphDatabaseFactoryState extends GraphDatabaseFactoryState
{
    private FileSystemAbstraction fileSystem;
    private LogProvider internalLogProvider;
    private SystemNanoClock clock;

    public TestGraphDatabaseFactoryState()
    {
        fileSystem = null;
        internalLogProvider = null;
    }

    public TestGraphDatabaseFactoryState( TestGraphDatabaseFactoryState previous )
    {
        super( previous );
        fileSystem = previous.fileSystem;
        internalLogProvider = previous.internalLogProvider;
        clock = previous.clock;
    }

    public FileSystemAbstraction getFileSystem()
    {
        return fileSystem;
    }

    public void setFileSystem( FileSystemAbstraction fileSystem )
    {
        this.fileSystem = fileSystem;
    }

    public LogProvider getInternalLogProvider()
    {
        return internalLogProvider;
    }

    public void setInternalLogProvider( LogProvider logProvider )
    {
        this.internalLogProvider = logProvider;
    }

    public SystemNanoClock clock()
    {
        return clock;
    }

    public void setClock( SystemNanoClock clock )
    {
        this.clock = clock;
    }
}

