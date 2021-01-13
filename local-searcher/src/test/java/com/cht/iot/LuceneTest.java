package com.cht.iot;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LuceneTest {
	
	String path = "..";
	String suffix = ".java";
	
	String index = "/tmp/index";
	
	String keyword = "ManagedResource";

	@Test
	void buildIndex() throws IOException {
		Analyzer analyzer = new StandardAnalyzer();
		
		Path indexPath = Paths.get(index);
		if (Files.isDirectory(indexPath) == false) {
			indexPath = Files.createDirectories(indexPath);
		}
		
		Directory directory = FSDirectory.open(indexPath);
		
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter writer = new IndexWriter(directory, config);
						
		Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {
			
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				File f = file.toFile();
				
				String filename = f.getAbsolutePath();
				
				if (f.getName().endsWith(suffix)) {
					Document doc = new Document();
					
					doc.add(new Field("filename", filename, TextField.TYPE_STORED));
					
					try (FileReader fr = new FileReader(f)) {					
						doc.add(new Field("body", fr, TextField.TYPE_NOT_STORED));
						
						writer.addDocument(doc);
					}
					
					log.info("{} is indexed.", filename);
				}
				
				return FileVisitResult.CONTINUE;
			}
		});
		
		writer.close();
	}
	
	@Test
	void searchIt() throws IOException, ParseException {
		Analyzer analyzer = new StandardAnalyzer();
		
		Path indexPath = Paths.get(index);
		Directory directory = FSDirectory.open(indexPath);
		
		DirectoryReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		QueryParser parser = new QueryParser("body", analyzer);
		Query query = parser.parse(keyword);
		
		TopDocs docs = searcher.search(query, 10);
		for (ScoreDoc sd : docs.scoreDocs) {
			Document doc = searcher.doc(sd.doc);
			
			String filename = doc.get("filename");
			
			log.info("Found keyword in '{}'", filename);
		}
		
		reader.close();
	}
	
	@Test
	void linearSearch() throws IOException {
		Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {
			
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				File f = file.toFile();
				
				if (f.getName().endsWith(suffix)) {
					try (Stream<String> stream = Files.lines(file)) {
						stream
							.filter(line -> line.contains(keyword))
							.forEach(line -> log.info("Found keyword in '{}'", file));
						
					} catch (Exception e) {
						log.error("Error in {}", file, e);
					}
				}
				
				return FileVisitResult.CONTINUE;
			}
		});
	}
}
