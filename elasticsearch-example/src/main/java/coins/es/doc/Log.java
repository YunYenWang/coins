package coins.es.doc;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Data;

@Document(indexName = "xxxx", createIndex = false)
@Data
public class Log {
	@Id
	String id;
	
	String from;
	
	@Field(type = FieldType.Date)
	String timestamp;
	
	String level;
	
	String message;
}
