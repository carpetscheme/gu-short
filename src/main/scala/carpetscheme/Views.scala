package carpetscheme

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.concurrent.Future
import com.gu.contentapi.client.model.v1.Content
import carpetscheme.Utils._
import scalatags.Text.all.doctype
import com.gu.contentapi.client.model.v1.ContentFields

object Views {

  def homepage(writers: List[String]): doctype = {
    val queryParams = makeQueryParams(writers)
    val writersUrl  = "/writers" + makeQueryParams(writers, "id")
    val settingsUrl = "/settings" + queryParams

    val headlines: List[Content] = Await.result(Guardian.headlines, 30.seconds)
    val headlinesPartial         = templates.Partials.headlines(headlines, queryParams)

    templates.Pages.homepage(headlinesPartial, writersUrl, settingsUrl)
  }

  def article(id: String, writers: List[String]): doctype = {
    val queryParams               = makeQueryParams(writers)
    val homepageUrl               = "/" + queryParams
    val settingsUrl               = "/settings" + queryParams
    val article: Guardian.Article = Await.result(Guardian.article(id), 30.seconds)

    templates.Pages.article(article, homepageUrl, settingsUrl)
  }

  def settings(writers: List[String]) = {
    val homepageUrl                      = "/" + makeQueryParams(writers)
    val profiles: List[Guardian.Profile] = Await.result(Guardian.getProfiles(writers), 30.seconds)

    templates.Pages.settings(profiles, writers, homepageUrl)
  }

  def writersSection(ids: List[String]) = {
    val articles: List[Content] = Await.result(Guardian.profileArticles(ids), 30.seconds)

    templates.Partials.writers(articles, makeQueryParams(ids)).render
  }

  def profileSearch(query: String, currentUrl: Option[String]) = {
    val currentWriters: List[String]           = currentUrl.flatMap(parseIdsFromUrl).getOrElse(List())
    val profileResults: List[Guardian.Profile] = Await.result(Guardian.profileSearch(query), 30.seconds)

    templates.Partials.profileSearchResults(profileResults, currentWriters).render
  }

}
