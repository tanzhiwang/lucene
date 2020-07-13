package com.itheima.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

public class LuceneFirst {
    @Test
    public void createIndex() throws Exception{
        //1.创建一个Directory对象，指定索引库保存的位置
        //把索引库保存在内存中
        //Directory directory=new RAMDirectory();
        //把索引库保存在磁盘上
        Directory directory= FSDirectory
        .open(new File("E:\\U盘转移\\it黑马\\00 讲义+笔记+资料\\流行框架\\61.会员版(2.0)-就业课(2.0)-Lucene\\lucene\\02.参考资料\\index").toPath());
        //2.基于Directory对象创建一个IndexWriter对象
        IndexWriterConfig config=new IndexWriterConfig(new IKAnalyzer());
        IndexWriter indexWriter=new IndexWriter(directory,config);
        //3.读取磁盘上的文件，对应每个文件创建一个文档对象
        File dir=new File("E:\\U盘转移\\it黑马\\00 讲义+笔记+资料\\流行框架\\61.会员版(2.0)-就业课(2.0)-Lucene\\lucene\\02.参考资料\\searchsource");
        File[] files = dir.listFiles();
        for (File f : files) {
            //取文件名
            String fileName = f.getName();
            //文件路径
            String filePath = f.getPath();
            //文件的内容
            String fileContent = FileUtils.readFileToString(f, "utf-8");
            //文件的大小
            long fileSize = FileUtils.sizeOf(f);
            //创建Filed(域)
            //参数1：域的名称，参数2：域的内容，参数3：是否存储
            Field fieldName=new TextField("name",fileName,Field.Store.YES);
            //Field fieldPath=new TextField("path",filePath, Field.Store.YES);
            Field fieldPath=new StoredField("path",filePath);
            Field fieldContent=new TextField("content",fileContent, Field.Store.YES);
            //Field fieldSize=new TextField("size",fileSize+"", Field.Store.YES);
            Field fieldSizeValue=new LongPoint("size",fileSize);
            Field fieldSizeStore=new StoredField("size",fileSize);
            //创建文档对象
            Document document=new Document();
            //向文档对象中添加域
            document.add(fieldName);
            document.add(fieldPath);
            document.add(fieldContent);
            //document.add(fieldSize);
            document.add(fieldSizeValue);
            document.add(fieldSizeStore);
            //把文档对象写入索引库
            indexWriter.addDocument(document);
        }
        //关闭indexwriter对象
        indexWriter.close();

    }

    @Test
    public void searchIndex()throws Exception{
        //1.创建一个Directory对象，指定索引库的位置
        Directory directory= FSDirectory
                .open(new File("E:\\U盘转移\\it黑马\\00 讲义+笔记+资料\\流行框架\\61.会员版(2.0)-就业课(2.0)-Lucene\\lucene\\02.参考资料\\index").toPath());
        //2.创建一个IndexReader对象
        IndexReader indexReader= DirectoryReader.open(directory);
        //3.创建一个IndexSearch对象，构造方法中的参数
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //4.创建一个Query对象，TermQuery
        Query query=new TermQuery(new Term("name","spring"));
        //5.执行查询，得到一个TopDoc对象
        //参数1：查询对象 参数2:查询结果返回最大记录数
        TopDocs topDocs = indexSearcher.search(query, 10);
        //6.取查询结果的总记录数
        System.out.println("查询总记录数："+topDocs.totalHits);
        //7.取文档列表
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        //8.打印文档中的内容
        for (ScoreDoc scoreDoc : scoreDocs) {
            //取文档id
            int docId = scoreDoc.doc;
            //根据id来取文档对象
            Document document = indexSearcher.doc(docId);
            System.out.println(document.get("name"));
            System.out.println(document.get("path"));
            System.out.println(document.get("size"));
            //System.out.println(document.get("content"));
            System.out.println("---------------------------");
        }
        //9.关闭IndexReader对象
        indexReader.close();
    }

    @Test
    public void testTokenStream() throws Exception{
        //1.创建一个Analyzer对象，StandardAnalyzer对象
        //Analyzer analyzer=new StandardAnalyzer();
        Analyzer analyzer=new IKAnalyzer();
        //2.使用分析器的tokenStream方法获得第一个tokenStream对象
        TokenStream tokenStream = analyzer.tokenStream("","lucene_大型免费公开课，传智播客国家郭嘉高质量java分布式架构视频教程，加入java交流社群共同提升进步，交流技术难点问题。");
        //3.向tokenStream对象中设置一个引用，相当于设一个指针
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        //4.调用tokenStream对象的reset方法，不调用会抛异常，
        tokenStream.reset();
        //5.使用while循环遍历TokenStream对象
        while (tokenStream.incrementToken()){
            System.out.println(charTermAttribute.toString());
        }
        //6.关闭tokenStream对象
        tokenStream.close();
    }


}
