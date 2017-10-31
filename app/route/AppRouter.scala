package route

import javax.inject.Inject

import play.api.cache.AsyncCacheApi
import play.api.mvc.{Action, Results}
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class AppRouter @Inject()(implicit cache: AsyncCacheApi) extends SimpleRouter {
    def routes: Routes = {
        case GET(p"/0") => Action {
            Results.Ok("")
        }
    }
}
