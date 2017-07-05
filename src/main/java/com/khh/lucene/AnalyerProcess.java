package com.khh.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * Created by Administrator on 2017/7/3.
 * 查看分析器的流程
 */
public class AnalyerProcess {

    @Test
    public void test() throws Exception{
        //创建一个分析器
//        Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
        //获得tokenStream对象
        //第一个参数：域名，可以随便给一个
        //第二个参数：要分析文本的内容

        TokenStream tokenStream = analyzer.tokenStream("test","对于输入字符串中的每个字符");

        //添加一个引用，可以获得每个关键字
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

        //添加一个引用偏移量，记录了关键词的开始位置以及结束位置
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);

        //将指针调整到列表的头部
        tokenStream.reset();
        //遍历关键词列表，通过incrementToken方法判断列表是否结束
        while(tokenStream.incrementToken()){
            //关键词的起始位置
            System.out.println("start->" + offsetAttribute.startOffset());
            //获取关键字
            System.out.println(charTermAttribute);
            //结束位置
            System.out.println("end->" + offsetAttribute.endOffset());
        }
        tokenStream.close();

    }
}
