package carpetscheme

import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import org.jsoup.nodes.Document
import urldsl.language.QueryParameters.simpleParamErrorImpl._
import urldsl.errors.SimpleParamMatchingError
import scalatags.Text.all.raw
import scalatags.Text

object Utils {

  def parseIdsFromUrl(url: String): Option[List[String]] =
    listParam[String]("writer")
      .matchRawUrl(url)
      .toOption

  def makeQueryParams(ids: List[String], paramName: String = "writer"): String =
    if (ids.isEmpty) ""
    else
      ids.distinct.map(id => paramName + "=" + id).mkString("?", "&", "")

  def stringToCleanFrag(content: String): Text.RawFrag =
    raw(Jsoup.clean(content, Safelist.basic()))

}
