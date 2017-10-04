package models.elasticsearch

import java.util.Date
import javax.inject.Inject

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.HttpClient
import com.sksamuel.elastic4s.http.search.SearchResponse
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import models.Article
import play.api.Configuration
import utils.Aggregation

case class SearchResult(score: Double, title: String, url: String, update_date: Date)

class ElasticSearch @Inject()(config: Configuration) {
    private val host = config.get[String]("elasticsearch.host")
    private val port = config.get[Int]("elasticsearch.port")
    val client = HttpClient(ElasticsearchClientUri(host, port))

    def searchByKeyword(key: String): Seq[SearchResult] = {
        val result: SearchResponse = client.execute {
            search("matome").matchQuery("title", key)
        }.await

        result.hits.hits.map { hit =>
            SearchResult(hit.score, hit.sourceAsMap("title").toString, hit.sourceAsMap("url").toString, Aggregation.convertToDate(hit.sourceAsMap("update_date").toString))
        }
    }

    def index(blog_name: String, data: Article) = {
        client.execute {
            bulk(
                indexInto("matome" / "articles").fields("blog_name" -> blog_name, "title" -> data.title, "url" -> data.url, "update_date" -> data.update_date)
            ).refresh(RefreshPolicy.WAIT_UNTIL)
        }.await
    }
}