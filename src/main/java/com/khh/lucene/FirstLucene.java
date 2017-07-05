package com.khh.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Administrator on 2017/7/1.
 *
 * Lucene 入门
 * 创建索引
 * 查询索引
 */
public class FirstLucene {

    //创建索引库
    @Test
    public void testIndex() throws Exception{
//        第一步：创建java工程，并导入jar包
//        第二步：创建一个indexwriter对象
//          (1)指定索引库的存放位置Directory对象
//          (2)指定一个分析器，对文档内容进行分析
        //索引存放的路径
        URL files = FirstLucene.class.getClassLoader().getResource("indexes");
        Path path = Paths.get(files.toURI());
        Directory directory = FSDirectory.open(path);
        //指定分析器
        Analyzer analyzer = new StandardAnalyzer();//官方推荐分析器

        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);

        //原始文档存放的路径
        URL filesUrl = FirstLucene.class.getClassLoader().getResource("files");
        File file = new File(filesUrl.getFile());
        File[] listFiles = file.listFiles();

        for (File f : listFiles) {

            //第三步：创建field对象，将field添加到document对象中
            Document document = new Document();
            String file_name = f.getName();
            Field fileNameField = new TextField("fileName",file_name, Field.Store.YES);

            long file_size = FileUtils.sizeOf(f);
            System.out.println("fileSize = "+file_size);
            Field fileSizeField = new LongPoint("fileSize",file_size);

            String file_path = f.getPath();
            Field filePathField = new StoredField("filePath",file_path);


            String file_content = FileUtils.readFileToString(f,"GBK"); // 因为这里的文件是GBK编码的
            Field fileContentField = new TextField("fileContent",file_content, Field.Store.YES);

            document.add(fileNameField);
            document.add(fileSizeField);
            document.add(filePathField);
            document.add(fileContentField);
//        第四步：使用indexwriter对象将document写入索引库中，此过程进行索引的创建，并将索引和document对象写入索引库
            indexWriter.addDocument(document);
        }
//        第五步：关闭IndexWriter对象
        indexWriter.close();
    }

    /**
     * 搜索索引
     * @throws Exception
     */
    @Test
    public void test() throws Exception{
//        第一步：创建一个Directory对象，也就是索引库存放的位置
        //索引存放的路径
        URL files = FirstLucene.class.getClassLoader().getResource("indexes");
        Path path = Paths.get(files.toURI());
        Directory directory = FSDirectory.open(path);

//        第二步：创建一个indexReader对象，需要指定Directory对象
        IndexReader indexReader = DirectoryReader.open(directory);
//        第三步：创建一个indexSearcher对象，需要指定IndexReader对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
//        第四步：创建一个TermQuery对象，指定查询的域和查询的关键字
        Query query = new TermQuery(new Term("fileContent","java"));
//        第五步：执行查询
        TopDocs topDocs = indexSearcher.search(query, 10);//10代表前10个
//        第六步：返回查询结果，遍历查询结果并输出
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for(ScoreDoc sdoc : scoreDocs){
            int doc = sdoc.doc;
            Document document  = indexSearcher.doc(doc);
            //文件名称
            String fileName = document.get("fileName");
            System.out.println("filename :" + fileName);
            //文件大小
            String fileSize = document.get("fileSize");
            System.out.println("fileSize :" + fileSize);
            //文件路径
            String filePath = document.get("filePath");
            System.out.println("filePath :" + filePath);
            //文件内容
            String fileContent = document.get("fileContent");
            System.out.println("fileContent :" + fileContent);
            System.out.println("-------------------------------------");
        }
//        第七步：关闭IndexReader对象
        indexReader.close();
    }

}
