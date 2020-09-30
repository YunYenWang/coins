package coins.es;

import java.time.Instant;

import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import coins.es.doc.Log;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class ElasticsearchTests {
	
	@Autowired
	ElasticsearchTemplate es;

	String indexName = "0930";
	int shards = 20;
	int replicas = 0;
	
	@BeforeEach
	void init() {
		if (es.indexExists(indexName) == false) {
			es.createIndex(
					indexName,
					String.format("{\"index\":{\"number_of_shards\":\"%d\",\"number_of_replicas\": \"%d\"}}", shards, replicas)
					);		
		
			log.info("Index '{}' is created", indexName);
		}
	}
	
	@Test
	void indexLog() {
		Log log = new Log();
		log.setFrom("HES");
		log.setTimestamp(Instant.now().toString());
		log.setLevel("INFO");
		log.setMessage("The system is ready.");
		
		IndexQuery iq = new IndexQuery();
		iq.setIndexName(indexName);
		iq.setObject(log);
		
		es.index(iq);
	}

	@Test
	void searchLog() {
		NativeSearchQuery qry = new NativeSearchQueryBuilder()
				.withIndices(indexName)
				.withQuery(new QueryStringQueryBuilder("message: ready"))
				.build();
		
		es.stream(qry, Log.class).forEachRemaining((l) -> {
			log.info("{}", l);
			
		});
	}	
}
