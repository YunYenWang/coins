package com.cht.iot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
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
import org.apache.tika.Tika;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

	public static void main(String[] args) throws Exception {
		File indices = new File(".indices"); // TODO
		File path = new File("."); // TODO
		
		String action = "query";
		String query = "";
		int count = 100; // TODO
		
		List<String> exts = new ArrayList<>();
		
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			
			if ("index".equals(arg)) {
				action = "index";
				
			} else if ("query".equals(arg)) {
				action = "query";
				
			} else if ("-f".equals(arg)) {
				String ext = args[++i].toLowerCase();
				
				exts.add(ext); // file suffix
				
			} else if ("-q".equals(arg)) {
				query = args[++i];
				
			} else {
				log.info("usage: local-search index -f docx -f doc -f ppt -f pptx");
				log.info("usage: local-search query -q 'Hello'");				
				
				System.exit(0);
			}
		}
		
		if (indices.isDirectory() == false) {
			indices.mkdirs();
		}		
		
		if ("index".equals(action)) {
			if (exts.isEmpty()) {
				log.warn("'-f' must be given");
				System.exit(1);
				
			}
			
			index(indices, path, exts);
			
		} else if ("query".equals(action)) {
			if (query.isEmpty()) {
				log.warn("'-q' must be given");
				System.exit(1);
			}
			
			query(indices, query, count);
		}
	}
	
	// ======
	
	static void index(File indices, File path, List<String> exts) throws IOException {
		Tika tika = new Tika();
		
		Analyzer analyzer = new CJKAnalyzer();
		
		Path indexPath = Paths.get(indices.getAbsolutePath());
		if (Files.isDirectory(indexPath) == false) {
			indexPath = Files.createDirectories(indexPath);
		}
		
		Directory directory = FSDirectory.open(indexPath);
		
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		try (IndexWriter writer = new IndexWriter(directory, config)) {							
			Files.walkFileTree(Paths.get(path.getAbsolutePath()), new SimpleFileVisitor<Path>() {
				
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					File f = file.toFile();
					
					String filename = f.getAbsolutePath();
									
					if (isEndsWith(filename, exts)) {
						Document doc = new Document();
						
						log.info("{}", filename);
						
						doc.add(new Field("filename", filename, TextField.TYPE_STORED));
						
						try (FileInputStream fis = new FileInputStream(f)) {
							Reader fr = tika.parse(fis);
							
							doc.add(new Field("body", fr, TextField.TYPE_NOT_STORED));
							
							writer.addDocument(doc);
						}
					}
					
					return FileVisitResult.CONTINUE;
				}
			});
		}		
	}
	
	static boolean isEndsWith(String fn, List<String> exts) {
		fn = fn.toLowerCase();
		
		for (String ext : exts) {
			if (fn.endsWith(ext)) {
				return true;
			}
		}
		
		return false;
	}
	
	// ======
	
	static void query(File indices, String query, int count) throws IOException, ParseException {
		Analyzer analyzer = new CJKAnalyzer();
		
		Path indexPath = Paths.get(indices.getAbsolutePath());
		Directory directory = FSDirectory.open(indexPath);
		
		try (DirectoryReader reader = DirectoryReader.open(directory)) {
			IndexSearcher searcher = new IndexSearcher(reader);
			
			QueryParser parser = new QueryParser("body", analyzer);
			Query q = parser.parse(query);
			
			TopDocs docs = searcher.search(q, count);
			for (ScoreDoc sd : docs.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				
				String filename = doc.get("filename");
				
				log.info("Found in '{}'", filename);
			}
		}
	}
}
