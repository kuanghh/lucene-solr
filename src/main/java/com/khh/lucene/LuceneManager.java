package com.khh.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;

import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.junit.Test;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Created by Administrator on 2017/7/3.
 * 索引维护
 * 增加(FirstLucene.java)
 * 删除
 * 修改
 * 查询
 *
 */
public class LuceneManager {

    public IndexWriter getIndexWriter() throws Exception{
        URL indexes = LuceneManager.class.getClassLoader().getResource("indexes");
        Path path = Paths.get(indexes.toURI());
        Directory directory = FSDirectory.open(path);
        IndexWriter indexWriter = new IndexWriter(directory,new IndexWriterConfig(new StandardAnalyzer()));
        return indexWriter;
    }

    //全删除
    @Test
    public void testDeleteAll() throws Exception{
        IndexWriter indexWriter = getIndexWriter();
        indexWriter.deleteAll();
        indexWriter.close();
    }

    //根据条件删除
    @Test
    public void testDeleteFromQuery() throws Exception{
        IndexWriter indexWriter = getIndexWriter();
        Query query = new TermQuery(new Term("fileContent","java"));//把fileContent域中含有java的删掉
        indexWriter.deleteDocuments(query);
        indexWriter.close();
    }

    //修改索引(先删除后修改)
    @Test
    public void testUpdateTerm() throws Exception{
        IndexWriter indexWriter = getIndexWriter();
        //要删除的term
        Term term = new Term("fileContent","java");
        Document doc = new Document();
        Field fieldN = new TextField("fileN","springframework", Field.Store.YES);
        Field fieldC = new TextField("fileC","springmvc and hibernate", Field.Store.YES);
        doc.add(fieldN);
        doc.add(fieldC);

        indexWriter.updateDocument(term,doc);
        indexWriter.close();
    }


    public IndexSearcher getIndexSearcher() throws Exception{

        URL indexes = LuceneManager.class.getClassLoader().getResource("indexes");
        Path path = Paths.get(indexes.toURI());
        Directory directory = FSDirectory.open(path);
        IndexReader open = DirectoryReader.open(directory);
        return new IndexSearcher(open);
    }

    public void print(IndexSearcher indexSearcher,Query query) throws Exception{

        TopDocs topDocs = indexSearcher.search(query, 20);//查询前20条;
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for(ScoreDoc sdoc : scoreDocs){
            int doc = sdoc.doc;
            Document document  = indexSearcher.doc(doc);
            //文件名称
            String fileName = document.get("fileName");
            System.out.println("filename :" + fileName);
            //文件路径
            String filePath = document.get("filePath");
            System.out.println("filePath :" + filePath);
            //文件大小
            String fileSize = document.get("fileSize");
            System.out.println("fileSize :" + fileSize);

            System.out.println("-------------------------------------");
        }
    }


    //查询所有
    @Test
    public void testFindAll() throws Exception{
        IndexSearcher indexSearcher = getIndexSearcher();
        Query query = new MatchAllDocsQuery();
        print(indexSearcher,query);
        indexSearcher.getIndexReader().close();
    }

    //根据数值范围查询
    @Test
    public void testNumericRangeQuery() throws Exception{
        IndexSearcher indexSearcher = getIndexSearcher();
        Query query = LongPoint.newRangeQuery("fileSize", 100L, 200L);
        print(indexSearcher,query);
        indexSearcher.getIndexReader().close();
    }


    //根据解析语法查询
    @Test
    public void testPaserQuery() throws Exception{
        IndexSearcher indexSearcher = getIndexSearcher();

        //参数一：默认查询的域
        //参数二：分词器
        QueryParser queryParser = new QueryParser("fileName",new StandardAnalyzer());

        //这里的参数写语法
        Query query = queryParser.parse("fileName:java.txt");

        print(indexSearcher,query);
        indexSearcher.getIndexReader().close();
    }


    //多个默认域查询
    @Test
    public void testMultifieldQueryParser() throws Exception{
        IndexSearcher indexSearcher = getIndexSearcher();

        String[] fields = {"fileName","fileContent"};
        MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(fields,new StandardAnalyzer());
        Query query = multiFieldQueryParser.parse("java");
        print(indexSearcher,query);
        indexSearcher.getIndexReader().close();
    }

}
