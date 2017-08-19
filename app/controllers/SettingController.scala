package controllers

import java.util.Date
import javax.inject._

import actions.AuthAction
import play.api.mvc._
import models.Blog
import play.cache.CacheApi
import utils.{Security, UserCache}
import forms.BlogEditForm
import play.api.i18n.{I18nSupport, MessagesApi}

@Singleton
class SettingController @Inject()(cache: CacheApi, val messagesApi: MessagesApi) extends Controller with I18nSupport {
    def index = Action {
      Ok(views.html.settings.index())
    }

    def blogList = AuthAction( cache,
        Action { implicit request =>
            val user_uuid = Security.getSessionUUID(request)
            val user = UserCache.get(cache, user_uuid)

            val b = Blog.find(user.get.id)
            Ok(views.html.settings.blog_list(cache, user_uuid, b))
        })

    def blogCreate = AuthAction( cache,
        Action { implicit request =>
            val user_uuid = Security.getSessionUUID(request)

            Ok(views.html.settings.edit(cache, user_uuid, null, BlogEditForm.form))
        }
    )

    // TODO
    def blogInsert = AuthAction( cache,
        Action { implicit request =>
            val f = BlogEditForm.form.bindFromRequest
            val user_uuid = Security.getSessionUUID(request)
            val user = UserCache.get(cache, user_uuid)

            val id = Security.generateUUID()
            val blog = Blog(id, user.get.id, 1, f.data("name"), f.data("url"), new Date(0))

            Blog.insert(blog)

            Redirect(routes.SettingController.blogList())
        }
    )

    def blogEdit(id: String) = AuthAction( cache,
        Action { implicit request =>
            val user_uuid = Security.getSessionUUID(request)
            val user = UserCache.get(cache, user_uuid)

            val blog = Blog.findById(id, user.get.id)
            blog match {
                case Some(b) => Ok(views.html.settings.edit(cache, user_uuid, b, BlogEditForm.form))
                case None => NotFound("Not Found")
            }
        }
    )

    def blogUpdate(id: String) = AuthAction( cache,
        Action { implicit request =>
            val f = BlogEditForm.form.bindFromRequest

            Blog.update(id, f.get.name)

            Redirect(routes.SettingController.blogList())
        }
    )
}
