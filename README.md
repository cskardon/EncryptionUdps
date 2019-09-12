# Encryption UDP

Encryption in UDPs
Featuring: AES-128


# Procedures

Instructions
------------ 

This project uses maven, to build a jar-file with the procedure in this
project, simply package the project with maven:

    mvn clean package

This will produce a jar-file, `target/procedures-1.0-SNAPSHOT.jar`,
that can be copied to the `plugin` directory of your Neo4j instance.

    cp target/procedures-1.0-SNAPSHOT.jar neo4j-enterprise-3.5.8/plugins/.
    

Restart your Neo4j Server. 

Usage:

```
//Generate a salt
RETURN security.generateSalt(128) //16 probs ok

//Create a node, store the IV (Initialization Vector) with it
CREATE (n:Node {id: 1, iv: com.cskardon.getIV(), cryptstring: com.cskardon.encrypt("plaintext")})

//Decrypt that node:
MATCH (n:Node {id:1})
RETURN n, com.cskardon.decrypt(n.cryptstring, n.iv) AS decrypted
```

^ Store in config 

```
CREATE (n:Node {iv: security.getIV(), cryptstring: security.encrypt("plaintext")})
```

Example Config 
```
security.encryption.password=chrispassword
security.encryption.salt=7f060d2efe845b8266fb267b58de1135
```

