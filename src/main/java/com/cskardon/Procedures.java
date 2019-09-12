package com.cskardon;

import com.cskardon.results.StringResult;
import org.neo4j.graphdb.*;
import org.neo4j.logging.*;
import org.neo4j.procedure.*;

import java.util.stream.*;


public class Procedures {

    // This field declares that we need a GraphDatabaseService
    // as context when any procedure in this class is invoked
    @Context
    public GraphDatabaseService db;

    // This gives us a log instance that outputs messages to the
    // standard log, normally found under `data/log/neo4j.log`
    @Context
    public Log log;


    @Procedure(name = "com.cskardon.echo", mode = Mode.READ)
    @Description("CALL com.cskardon.echo(String said)")
    public Stream<StringResult> echo(@Name("said") String said) {
        return Stream.of(new StringResult(said));
    }

}
