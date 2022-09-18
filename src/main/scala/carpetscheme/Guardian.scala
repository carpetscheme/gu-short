package carpetscheme

import com.gu.contentapi.client.GuardianContentClient
import scala.concurrent.ExecutionContext.Implicits.global
import com.gu.contentapi.client.model._
import com.gu.contentapi.client.model.v1.ContentFields
import scala.concurrent.Future
import com.gu.contentapi.client.ContentApiClient
import cats.syntax.all._
import scalatags.Text

object Guardian {

  case class Article(headline: String, date: String, trail: String, byline: String, body: Text.RawFrag)

  case class Profile(id: String, name: String)

  private val client = new GuardianContentClient(sys.env.get("GUARDIAN_API_KEY").get)

  private val profilePrefix = "profile/"

  private def makeQuery(query: SearchQuery): Future[List[v1.Content]] = client
    .getResponse(query)
    .map(_.results.toList)

  private val headlinesQuery: SearchQuery = ContentApiClient.search
    .page(1)
    .pageSize(10)
    .orderBy("newest")
    .tag("theguardian/mainsection/topstories")

  def headlines: Future[List[v1.Content]] = makeQuery(headlinesQuery)

  def article(id: String): Future[Article] = client
    .getResponse(
      ItemQuery(id)
        .showFields("all")
    )
    .map { res =>
      val fields = res.content.get.fields.get
      Article(
        fields.headline.get,
        res.content.get.webPublicationDate.get.iso8601.take(10),
        fields.trailText.get,
        fields.byline.get,
        fields.body.map(Utils.stringToCleanFrag).get
      )
    }

  def profileSearch(query: String): Future[List[Profile]] =
    client
      .getResponse(
        ContentApiClient.tags
          .pageSize(5)
          .tagType("contributor")
          .q(query)
      )
      .map(
        _.results.toList.map(p => Profile(p.id.stripPrefix(profilePrefix), p.webTitle))
      )

  def getProfile(id: String): Future[Profile] =
    client
      .getResponse(
        ContentApiClient.tags
          .pageSize(5)
          .tagType("contributor")
          .q(profilePrefix + id)
      )
      .map(
        _.results.toList.headOption
          .map(p => Profile(p.id.stripPrefix(profilePrefix), p.webTitle))
          .getOrElse(Profile(id, id))
      )

  private def profileArticlesQuery(id: String): SearchQuery =
    ContentApiClient.search
      .page(1)
      .pageSize(2)
      .orderBy("newest")
      .tag(profilePrefix + id)
      .showFields("headline,byline")

  def profileArticles(ids: List[String]): Future[List[v1.Content]] =
    ids
      .map(profileArticlesQuery)
      .flatTraverse(makeQuery)
      .map(_.take(10).sortBy(a => a.webPublicationDate.map(-_.dateTime)))

  def getProfiles(ids: List[String]): Future[List[Profile]] = ids.traverse(getProfile)

}
