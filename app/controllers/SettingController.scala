package controllers

import javax.inject._
import play.api.mvc._

import models.Blog

@Singleton
class SettingController @Inject() extends Controller {
    def index = Action {
      Ok(views.html.settings.index())
    }

    def blogList = Action {
        val b = Blog.find()
        Ok(views.html.settings.blog_list(b))
    }

    // TODO use form and refactoring
    def updateBlogList = Action { implicit request =>
        val name_key_regex = """name\[(\d+)\]""".r
        val url_key_regex = """url\[(.+)\]""".r
        val data = request.body.asInstanceOf[AnyContentAsFormUrlEncoded].data
        var m = Map[String, (String, String)]()
        data.foreach { s =>
            s._1 match {
                case name_key_regex(x) => {
                    if (m.isDefinedAt(x)) {
                        val d = m(x)
                        m = m.updated(x, (s._2.head, d._2))
                    } else {
                        m = m + (x -> (s._2.head, ""))
                    }
                }
                case url_key_regex(x) => {
                    if (m.isDefinedAt(x)) {
                        val d = m(x)
                        m = m.updated(x, (d._1, s._2.head))
                    } else {
                        m = m + (x -> ("", s._2.head))
                    }
                }
            }
        }
        Blog.delete()
        m.foreach { s =>
            Blog.insert(Blog(0, s._2._1, s._2._2, new java.util.Date()))
        }

        Redirect(routes.SettingController.blogList())
    }
}
