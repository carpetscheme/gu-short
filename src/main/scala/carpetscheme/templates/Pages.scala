package carpetscheme.templates

import scalatags.Text.all._
import scalatags.Text.tags2.{main => mainHtml}
import scalatags.Text.tags2.{title => titleHead}
import scalatags.Text.tags2.{article => articleTag}
import scalatags.Text.tags2.{section => sectionTag}
import scalatags.Text.tags2.time
import carpetscheme.templates.Partials._
import carpetscheme.Guardian.{Article, Profile}
import carpetscheme.Utils.makeQueryParams

object Pages {

  def homepage(headlines: Frag, writersUrl: String, settingsUrl: String) =
    index(
      frag(
        h1("The short Guardian"),
        hr,
        sectionTag(h2("Headlines"), headlines),
        sectionTag(
          id                 := "writers-section",
          attr("hx-get")     := writersUrl,
          attr("hx-trigger") := "load"
        ),
        footerPartial(Some(settingsUrl))
      )
    )

  def article(article: Article, homepageUrl: String, settingsUrl: String) =
    index(
      frag(
        h4(small(a("← Short Guardian", href := homepageUrl))),
        articleTag(
          header(
            h1(article.headline),
            time(article.date),
            h2(article.trail),
            h3(article.byline),
            hr()
          ),
          article.body
        ),
        footerPartial(Some(settingsUrl), Some(homepageUrl))
      )
    )

  def settings(profiles: List[Profile], currentWriters: List[String], homepageUrl: String) =
    index(
      frag(
        h4(small(a("← Short Guardian", href := homepageUrl))),
        sectionTag(
          h1("Settings"),
          hr,
          form(
            action := homepageUrl,
            input(`type` := "submit", value := "Your Homepage", style := "margin-left: auto; margin-right: auto;")
          ),
          h2(small("Your writers:"))
        ),
        ul(
          profiles.map(profile =>
            li(
              a(
                href := "/settings" + makeQueryParams(currentWriters.filterNot(_ == profile.id)),
                profile.name,
                span(style := "padding-left: 0.5em; font-size: 0.8em", "❌")
              )
            )
          )
        ),
        profileSearch,
        footerPartial(homepageUrl = Some(homepageUrl))
      )
    )

}
