package controllers

import java.util.Date
import javax.inject._

import actions.AuthAction
import play.api.mvc._
import models.{Blog, BlogImpl, BlogTypeImpl}
import utils.{Security, UserCache}
import forms.BlogEditForm
import play.api.cache.AsyncCacheApi
import play.api.i18n.{I18nSupport, MessagesApi}

@Singleton
class SettingBlogController @Inject()(implicit cache: AsyncCacheApi, val messagesApi: MessagesApi, blog: BlogImpl, blog_type: BlogTypeImpl) extends Controller with I18nSupport {
    def blogList = AuthAction( cache,
        Action { implicit request =>
            val user_uuid = Security.getSessionUUID(request)
            val user = UserCache.get(user_uuid)

            val b = blog.findByUserId(user.get.id)
            Ok(views.html.settings.blog_list(UserCache.get(user_uuid), b))
        })

    def blogCreate = AuthAction( cache,
        Action { implicit request =>
            val user_uuid = Security.getSessionUUID(request)
            val blog_type_list = blog_type.list

            Ok(views.html.settings.edit(UserCache.get(user_uuid), None, blog_type_list, BlogEditForm.form))
        }
    )

    def blogInsert = AuthAction( cache,
        Action { implicit request =>
            val f = BlogEditForm.form.bindFromRequest
            val user_uuid = Security.getSessionUUID(request)
            val user = UserCache.get(user_uuid)

            val id = Security.generateUUID()

            f.value match {
                case Some(form) => {
                    val b = Blog(id, user.get.id, form.blog_type_id, form.name, form.url, form.notification, new Date(0))

                    blog.insert(b)

                    Redirect(routes.SettingBlogController.blogList())
                }
                case None => BadRequest(views.html.template.notfound("BadRequest"))
            }
        }
    )

    def blogEdit(id: String) = AuthAction( cache,
        Action { implicit request: Request[AnyContent] =>
            val user_uuid = Security.getSessionUUID(request)
            val user = UserCache.get(user_uuid)

            val blog_opt = blog.findById(id, user.get.id)
            val blog_type_list = blog_type.list
            blog_opt match {
                case Some(b) => Ok(views.html.settings.edit(UserCache.get(user_uuid), Some(b), blog_type_list, BlogEditForm.form))
                case None => NotFound(views.html.template.notfound("Blog Not Found"))
            }
        }
    )

    def blogUpdate(id: String) = AuthAction( cache,
        Action { implicit request =>
            val f = BlogEditForm.form.bindFromRequest

            f.value match {
                case Some(form) => {
                    blog.update(id, form.blog_type_id, form.name, form.url, form.notification)

                    Redirect(routes.SettingBlogController.blogList())
                }
                case None => BadRequest(views.html.template.notfound("BadRequest"))
            }
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
