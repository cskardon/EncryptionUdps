package com.cskardon;


import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.*;
import org.neo4j.harness.junit.Neo4jRule;


import static org.junit.Assert.*;
import static org.neo4j.driver.v1.Values.parameters;

public class FunctionsTestsUsingRules {
    @Rule
    public final Neo4jRule neo4j = new Neo4jRule()
            .withFunction(Functions.class);

    @Test
    public void shouldEncrypt(){
        try( Driver driver = GraphDatabase.driver( neo4j.boltURI() ,
                Config.build().withoutEncryption().toConfig() ) )
        {
            String plainText = "plaintText";
            Session session = driver.session();

            StatementResult result = session.run( "RETURN com.cskardon.encrypt($something) AS value",
                    parameters( "something", plainText ) );

            Object value = result.single().get("value");
            assertNotEquals(value.toString(), plainText);

        }

    }
}
