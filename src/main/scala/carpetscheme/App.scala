package carpetscheme

object App extends cask.MainRoutes {

  @cask.get("/")
  def index(writer: Seq[String] = Seq()) = Views.homepage(writer.toList)

  @cask.get("/article/", subpath = true)
  def article(request: cask.Request, writer: Seq[String] = Seq()) = {
    val id = request.remainingPathSegments.mkString("/")
    Views.article(id, writer.toList)
  }

  @cask.get("/settings")
  def settings(writer: Seq[String] = Seq()) = Views.settings(writer.toList)

  @cask.get("/writers")
  def writers(id: Seq[String] = Seq()) = Views.writersSection(id.toList)

  @cask.postForm("/writers")
  def writerSearch(query: cask.FormValue, request: cask.Request) = {
    val currentUrl: Option[String] = request.headers.get("hx-current-url").flatMap(_.headOption)
    Views.profileSearch(query.value, currentUrl)
  }

  @cask.staticFiles("/stylesheets", headers = Seq("Content-Type" -> "text/css"))
  def stylesheets() = "/static/css"

  @cask.staticFiles("/js", headers = Seq("Content-Type" -> "application/javascript"))
  def scripts() = "/static/js"

  initialize()
}
