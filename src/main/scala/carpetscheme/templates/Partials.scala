package carpetscheme.templates

import scalatags.Text.all._
import scalatags.Text.tags2.{main => mainHtml}
import scalatags.Text.tags2.{title => titleHead}
import scalatags.Text.tags2.{article => articleTag}
import scalatags.Text.tags2.{section => sectionTag}
import scalatags.Text.tags2.{style => styleTag}
import scalatags.Text.tags2.time
import com.gu.contentapi.client.model.v1.ContentFields
import com.gu.contentapi.client.model.v1.Content
import carpetscheme.Guardian.Profile
import carpetscheme.Utils.makeQueryParams
import scalatags.Text
import java.time.LocalDate

object Partials {

  def index(mainFrag: Frag) =
    doctype("html")(
      html(
        head(
          meta(charset   := "utf-8"),
          meta(httpEquiv := "X-UA-Compatible", content := "IE=edge"),
          meta(name      := "viewport", content        := "width=device-width, initial-scale=1"),
          titleHead("gu-short"),
          link(rel   := "stylesheet", href := "/stylesheets/dark.css"),
          script(src := "/js/htmx.min.js")
        ),
        body(
          mainHtml(attr("hx-boost") := "true")(
            mainFrag
          )
        )
      )
    )

  def footerPartial(settingsUrl: Option[String] = None, homepageUrl: Option[String] = None) = {
    val defaultLinks: List[Text.TypedTag[String]] = List(
      a(href := "https://github.com/carpetscheme/gu-short", "Source on GitHub"),
      span(" · "),
      a(href := "https://open-platform.theguardian.com", "Guardian Open Platform"),
      p(
        style := "text-align:center",
        small("All content © " + LocalDate.now.getYear + " Guardian News & Media Limited")
      )
    )
    val settingsLink = settingsUrl.map(l => List(a(href := l, "Settings"), span(" · "))).getOrElse(Nil)
    val homepageLink = homepageUrl.map(l => List(a(href := l, "← Home"), span(" · "))).getOrElse(Nil)

    footer(homepageLink ::: settingsLink ::: defaultLinks)
  }

  def headlines(articles: List[Content], queryParams: String): Frag =
    articles.map(article => p(a(href := "article/" + article.id + queryParams, article.webTitle)))

  def writers(articles: List[Content], queryParams: String): Frag = {
    val section = articles.map { article =>
      val title  = article.fields.flatMap(_.headline).getOrElse(article.webTitle)
      val byline = article.fields.flatMap(_.byline).getOrElse("")
      val date   = article.webPublicationDate.get.iso8601.take(10)
      p(
        small(style := "display:block")(time(date), " | ", byline),
        a(href := "article/" + article.id + queryParams, title)
      )
    }

    section.isEmpty match {
      case true  => frag()
      case false => frag(h2(small("Writers")), section)
    }
  }

  def profileSearch = sectionTag(
    h2(
      small("Writer search"),
      small(
        `class` := "htmx-indicator"
      )(" Loading...")
    ),
    input(
      `style`              := "width: 100%;",
      `type`               := "search",
      name                 := "query",
      placeholder          := "Start typing to search for writers",
      attr("hx-post")      := "/writers",
      attr("hx-trigger")   := "keyup changed delay:500ms, search",
      attr("hx-target")    := "#search-results",
      attr("hx-indicator") := ".htmx-indicator"
    ),
    ul(id := "search-results")
  )

  def profileSearchResults(profiles: List[Profile], currentWriters: List[String]): Frag =
    frag(
      profiles.map(profile =>
        li(
          a(
            profile.name,
            span(style := "padding-left: 0.5em; color: #57D900;", "+"),
            href := makeQueryParams(profile.id :: currentWriters)
          )
        )
      )
    )

}
