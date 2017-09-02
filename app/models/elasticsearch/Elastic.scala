package models.elasticsearch

import java.util.Date

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.HttpClient
import com.sksamuel.elastic4s.http.search.SearchResponse
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import models.Article
import utils.Aggregation

case class SearchResult(score: Double, title: String, url: String, update_date: Date)

object Elastic {
    val client = HttpClient(ElasticsearchClientUri("localhost", 9200))

    def find(key: String) = {
        val result: SearchResponse = client.execute {
            search("matome").matchQuery("title", key)
        }.await

        result.hits.hits.map { hit =>
            SearchResult(hit.score, hit.sourceAsMap("title").toString, hit.sourceAsMap("url").toString, Aggregation.convertToDate(hit.sourceAsMap("update_date").toString))
        }
    }

    def insert(blog_name: String, data: Article) = {
        client.execute {
            bulk(
                indexInto("matome" / "articles").fields("blog_name" -> blog_name, "title" -> data.title, "url" -> data.url, "update_date" -> data.update_date)
            ).refresh(RefreshPolicy.WAIT_UNTIL)
        }.await
    }
}