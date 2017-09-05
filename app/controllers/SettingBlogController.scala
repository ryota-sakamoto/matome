package controllers

import java.util.Date
import javax.inject._

import actions.AuthAction
import play.api.mvc._
import models.{Blog, BlogImpl, BlogTypeImpl}
import play.cache.CacheApi
import utils.{Security, UserCache}
import forms.BlogEditForm
import play.api.i18n.{I18nSupport, MessagesApi}

@Singleton
class SettingBlogController @Inject()(implicit cache: CacheApi, val messagesApi: MessagesApi, blog: BlogImpl, blog_type: BlogTypeImpl) extends Controller with I18nSupport {
    def blogList = AuthAction( cache,
        Action { implicit request =>
            val user_uuid = Security.getSessionUUID(request)
            val user = UserCache.get(user_uuid)

            val b = blog.find(user.get.id)
            Ok(views.html.settings.blog_list(cache, user_uuid, b))
        })

    def blogCreate = AuthAction( cache,
        Action { implicit request =>
            val user_uuid = Security.getSessionUUID(request)
            val blog_type_list = blog_type.list

            Ok(views.html.settings.edit(cache, user_uuid, null, blog_type_list, BlogEditForm.form))
        }
    )

    def blogInsert = AuthAction( cache,
        Action { implicit request =>
            val f = BlogEditForm.form.bindFromRequest
            val user_uuid = Security.getSessionUUID(request)
            val user = UserCache.get(user_uuid)

            val id = Security.generateUUID()
            val b = Blog(id, user.get.id, f.get.blog_type_id, f.get.name, f.get.url, f.get.notification, new Date(0))

            blog.insert(b)

            Redirect(routes.SettingBlogController.blogList())
        }
    )

    def blogEdit(id: String) = AuthAction( cache,
        Action { implicit request =>
            val user_uuid = Security.getSessionUUID(request)
            val user = UserCache.get(user_uuid)

            val b = blog.findById(id, user.get.id)
            val blog_type_list = blog_type.list
            b match {
                case Some(b) => Ok(views.html.settings.edit(cache, user_uuid, b, blog_type_list, BlogEditForm.form))
                case None => NotFound(views.html.template.notfound())
            }
        }
    )

    def blogUpdate(id: String) = AuthAction( cache,
        Action { implicit request =>
            val f = BlogEditForm.form.bindFromRequest

            blog.update(id, f.get.blog_type_id, f.get.name, f.get.url, f.get.notification)

            Redirect(routes.SettingBlogController.blogList())
        }
    )

    def blogDelete(id: String) = AuthAction( cache,
        Action { implicit request =>
            val result = blog.delete(id)

            result match {
                case 1 => Ok("")
                case _ => NotFound("")
            }
        }
    )
}
