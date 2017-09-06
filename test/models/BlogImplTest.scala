package models

import java.util.Date

import org.scalatest.BeforeAndAfterAll
import org.scalatest.Matchers._
import org.scalatestplus.play._
import play.api.db.Databases
import play.api.db.evolutions._

class BlogImplTest extends PlaySpec with BeforeAndAfterAll {
    val db = Databases(
        driver = "com.mysql.jdbc.Driver",
        url = "jdbc:mysql://localhost/test",
        config = Map(
            "user" -> "root"
        )
    )
    Evolutions.applyEvolutions(db)

    "BlogImpl" should {
        val blog_impl = new BlogImpl(db)

        val blog_update_date = new Date(0L)
        val blog_id = "a70eb8faa134599b5cbb25208fd54a77"
        val blog_1 = Blog(
            blog_id, 1, 1, "blog_1", "http://example.com", notification = true, blog_update_date
        )
        val new_blog_type = 2
        val new_blog_name = "new_name"
        val new_blog_url = "http://example.com/new"
        val new_notification = false

        "insert method" in {
            val insert_count = blog_impl.insert(blog_1)

            insert_count should be (1)
        }

        "findAll method" in {
            val seq = blog_impl.findAll()

            seq.length should be (1)
            seq.head.id should be (blog_id)
        }

        "update method (update_date)" in {
            val new_update_date = new Date()
            val update_count = blog_impl.update(blog_id, new_update_date)

            update_count should be (1)
            db.withConnection { implicit c =>
                val stat = c.createStatement()
                val rs = stat.executeQuery("select * from blog where id = '%s'".format(blog_id))

                rs.next() should be (true)
                rs.getString("update_date") should be ("%tY-%<tm-%<td %<tH:%<tM:%<tS.0".format(new_update_date))
            }
        }

        "update method" in {
            val update_count = blog_impl.update(blog_id, 2, "new_name", "http://example.com/new", notification = false)

            update_count should be (1)
        }

        "findById method" in {
            val blog_opt = blog_impl.findById(blog_id, blog_1.user_id)

            blog_opt should not be empty
            blog_opt match {
                case Some(b) => {
                    b.id should be (blog_id)
                    b.blog_type_id should be (new_blog_type)
                    b.url should be (new_blog_url)
                    b.notification should be (new_notification)
                }
            }
        }

        "findByUserId method" in {
            val blog_seq = blog_impl.findByUserId(1)

            blog_seq.length should be (1)
            blog_seq.head.id should be (blog_id)
        }

        "delete method" in {
            val delete_count = blog_impl.delete(blog_id)
            delete_count should be (1)
            db.withConnection { implicit c =>
                val stat = c.createStatement()
                val rs = stat.executeQuery("select * from blog where id = '%s'".format(blog_id))

                rs.next() should be (false)
            }
        }
    }

    override def afterAll(): Unit = {
        Evolutions.cleanupEvolutions(db)
    }
}