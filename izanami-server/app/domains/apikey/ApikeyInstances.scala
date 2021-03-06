package domains.apikey
import akka.NotUsed
import akka.stream.scaladsl.Flow
import cats.effect.Effect
import domains._
import store.Result.{AppErrors, ErrorMessage}

import scala.concurrent.ExecutionContext

object ApikeyInstances {

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  import play.api.libs.json.Reads._

  implicit val isAllowed: IsAllowed[Apikey] = new IsAllowed[Apikey] {
    override def isAllowed(value: Apikey)(auth: Option[AuthInfo]): Boolean =
      Key.isAllowed(Key(value.authorizedPattern))(auth)
  }

  private val reads: Reads[Apikey] = {
    import domains.AuthorizedPattern._
    (
      (__ \ 'clientId).read[String](pattern("^[@0-9\\p{L} .'-]+$".r)) and
      (__ \ 'name).read[String](pattern("^[@0-9\\p{L} .'-]+$".r)) and
      (__ \ 'clientSecret).read[String](pattern("^[@0-9\\p{L} .'-]+$".r)) and
      (__ \ 'authorizedPattern).read[AuthorizedPattern](AuthorizedPattern.reads)
    )(Apikey.apply _)
  }

  private val writes = {
    import domains.AuthorizedPattern._
    Json.writes[Apikey]
  }

  implicit val format = Format[Apikey](reads, writes)

}
