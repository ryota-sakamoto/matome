package route

import javax.inject.Inject
import controllers.ApiController
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class ApiRouter @Inject()(apiController: ApiController) extends SimpleRouter {
    def routes: Routes = {
        case GET(p"/blogs/${id}/articles") => apiController.getArticle(id)
        case POST(p"/aggregate/article") => apiController.aggregateArticle
    }
}