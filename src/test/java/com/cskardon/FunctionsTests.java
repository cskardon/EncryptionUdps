package com.cskardon;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;

import static org.junit.Assert.*;

public class FunctionsTests {
    private static GraphDatabaseService _db;

    @BeforeClass
    public static void setUp() throws Exception {
        _db = new TestGraphDatabaseFactory().newImpermanentDatabase();
        TestUtil.registerProcedure(_db, Functions.class);
    }

    @AfterClass
    public static void tearDown(){
        _db.shutdown();
    }

    @Test
    public void testEncrypt() throws Exception {
        String plainText = "plainText";
        TestUtil.testCall(
                _db,
                "RETURN com.cskardon.encrypt('"+ plainText +"') AS value",
                r -> {
                    Object value = r.get("value");
                    assertNotNull(value);
                    assertNotEquals(plainText, value);
                });
    }

    @Test
    public void testGetIv() throws Exception{
        TestUtil.testCall(
                _db,
                "RETURN com.cskardon.getIV() AS value",
                r -> assertNotNull(r.get("value"))
        );
    }

    @Test
    public void testDecrypt() throws Exception {
        String plainText = "plainText";
        String cypher = "WITH com.cskardon.getIV() AS iv\n" +
                "WITH com.cskardon.encrypt('"+ plainText + "') AS encrypted, iv\n" +
                "RETURN com.cskardon.decrypt(encrypted, iv) AS decrypted, encrypted";

        TestUtil.testCall(
                _db,
                cypher,
                r -> {
                    String decrypted = r.get("decrypted").toString();
                    String encrypted = r.get("encrypted").toString();

                    assertEquals(plainText, decrypted);
                    assertNotEquals(plainText, encrypted);
                }
        );
    }
}
