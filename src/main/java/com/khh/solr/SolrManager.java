package com.khh.solr;



import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/4.
 * 单机版
 */
public class SolrManager {

    //添加
    @Test
    public void testSolrAdd() throws Exception{

        String url = "http://192.168.1.113:8080/solr/my_solr";

        SolrClient solrClient = new HttpSolrClient.Builder(url).build();


        SolrInputDocument doc = new SolrInputDocument();

        doc.setField("id","aaa");
        doc.setField("p_name","what is happens");

        solrClient.add(doc);

        //批量增加用
        //solrClient.add(Collection<SolrInputDocument> docs)

        solrClient.commit();

        solrClient.close();
    }

    // 删除
    @Test
    public void testSolrDelete() throws Exception{

        String url = "http://192.168.1.113:8080/solr/my_solr";

        SolrClient solrClient = new HttpSolrClient.Builder(url).build();

        //在1秒后执行删除所有数据
        solrClient.deleteByQuery("*:*",1000);

        solrClient.commit();

        solrClient.close();
    }

    //修改 = 删除+添加
    // 修改和添加一致，只是添加的时候，把id覆盖掉，solr会自动删除旧的信息，再添加新的进去




    //查询
    @Test
    public void testSolrSearch() throws Exception{

        String url = "http://192.168.1.113:8080/solr/my_solr";

        SolrClient solrClient = new HttpSolrClient.Builder(url).build();

        SolrQuery solrQuery = new SolrQuery();

        //根据语法查询,两个方法如下
        //solrQuery.set("q", "id:aaa");
        solrQuery.setQuery("p_name:商品");

        //过滤,两个方法如下
        //solrQuery.set("fq","p_price:[10 TO 25]");
        solrQuery.setFilterQueries("p_price:[10 TO 25]");

        //设置排序,两个方法如下
        //solrQuery.set("sort","p_price desc");
        solrQuery.addSort("p_price", SolrQuery.ORDER.desc);

        //设置分页(原理和mysql的limit一样)
        int start = 0;  //从第几行开始查询
        int rows = 10;  //每页有多少行
        solrQuery.setStart(start);
        solrQuery.setRows(rows);

        //设置所想显示结果的域
        solrQuery.setFields("id","p_name","p_price");

        //设置高亮
        solrQuery.setHighlight(true);//打开高亮
        solrQuery.addHighlightField("p_name");//设置高亮的域
        solrQuery.setHighlightSimplePre("<span style='color:red'>");//设置前缀
        solrQuery.setHighlightSimplePost("</span>");//设置后缀

        //执行查询
        QueryResponse response = solrClient.query(solrQuery);
        //文档结果集
        SolrDocumentList docs = response.getResults();

        Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();//获取高亮容器
        //第一个Map  Key---> id  value----->Map
        //第二个Map  key--->field  value---->List---->就是结构


        //获取总条数(这个值是获取一共存在几条数目，而不是返回的数据有多少条)
        long numFound = docs.getNumFound();
        System.out.println("一共："+numFound+"条");
        for (int i = 0; i < rows; i++) {
            SolrDocument solrDocument = docs.get(i);
            System.out.println("第" + (i+1) + "条:");
            System.out.println("id == " + solrDocument.get("id"));
            System.out.println("p_name == " + solrDocument.get("p_name"));
            System.out.println("p_price == " + solrDocument.get("p_price"));
            System.out.println("p_content == " + solrDocument.get("p_content"));
            Map<String, List<String>> id = highlighting.get(solrDocument.get("id"));
            List<String> list = id.get("p_name");
            System.out.println("高亮的是：" + list.get(0));
            System.out.println("------------------------------------------------------");
        }

        solrClient.close();
    }


}
