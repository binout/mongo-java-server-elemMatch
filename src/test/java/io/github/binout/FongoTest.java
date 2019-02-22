package io.github.binout;

import com.github.fakemongo.Fongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.assertj.core.api.WithAssertions;
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FongoTest implements WithAssertions {

    private MongoCollection<Document> collection;

    @BeforeAll
    void setUp() {
        collection = new Fongo("testdb").getDatabase("testdb").getCollection("testcollection");
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
