package io.github.binout;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.assertj.core.api.WithAssertions;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.net.InetSocketAddress;
import java.util.Arrays;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoJavaServerTest implements WithAssertions {

    private MongoCollection<Document> collection;
    private MongoClient client;
    private MongoServer server;

    @BeforeAll
    void setUp() {
        server = new MongoServer(new MemoryBackend());
        client = new MongoClient(new ServerAddress(server.bind()));
        collection = client.getDatabase("testdb").getCollection("testcollection");
    }

    @AfterAll
    void tearDown() {
        client.close();
        server.shutdown();
    }

    @Test
    void testQueryWithElemMatch() {
        Document obj1 = new Document("_id", 1).append("materials", Arrays.asList(
            new Document("materialId", "A"),
            new Document("materialId", "B"),
            new Document("materialId", "C")));
        collection.insertOne(obj1);

        Document obj2 = new Document("_id", 2).append("materials", new Document("materialId", "B"));
        collection.insertOne(obj2);

        Document found = collection.find(Filters.elemMatch("materials", Filters.eq("materialId", "A"))).first();
        assertThat(found.get("_id")).isEqualTo(1);

    }

}
