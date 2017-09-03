package modules

import actors.{AggregationActor, MailActor, RegisterActor}
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class ActorModule extends AbstractModule with AkkaGuiceSupport {
    def configure(): Unit = {
        bindActor[AggregationActor]("aggregationActor")
        bindActor[RegisterActor]("registerActor")
        bindActor[MailActor]("mailActor")
    }
}