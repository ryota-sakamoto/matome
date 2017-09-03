package modules

import actors.AggregationActor
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class ActorModule extends AbstractModule with AkkaGuiceSupport {
    def configure(): Unit = {
        bindActor[AggregationActor]("aggregationActor")
    }
}