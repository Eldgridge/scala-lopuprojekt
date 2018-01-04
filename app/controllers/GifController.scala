package controllers

import play.api.mvc._
import play.api.libs.json._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.filters.headers.SecurityHeadersFilter


class GifController extends Controller {
  var content : String = "hackerman"

  case class gif_topic(topic: String)

  def doContentGet = Action { implicit request =>

    contentForm.bindFromRequest.fold(
      formWithErrors => {
        println("Bidning failed, sorry bob")
        Redirect(routes.GifController.index())

      },
      gif_topic => {
        println("Binding was a sucesss")

        content = gif_topic.topic.toString

        if(gif_topic.topic.toString().contains(" ")){
          content =  gif_topic.topic.toString().replace(" ", "_")
        }

        Redirect(routes.GifController.index())

      }
    )

  }


  val contentForm = Form(mapping("topic" -> text)
  (gif_topic.apply)(gif_topic.unapply)
  )



  def index = Action {
    println(s"The content we are showing is ${content}")
    Ok(views.html.gif_page(get_json()))
  }



  /**
    * Returns the text (content) from a URL as a String.
    * Warning: This method does not time out when the service is non-responsive.
    */
  def get(url: String) = scala.io.Source.fromURL(url).mkString


  def get_json() : List[(String, String)]= {
    val API_KEY = "hZa18UU4W2X8iMxIhBv4lPar2oXG5npr"
    val collection = collect_url(Json.parse(get("http://api.giphy.com/v1/gifs/search?q="+content+"&api_key="+API_KEY+"&limit=5")))
    return collection

  }


  def collect_url(json: JsValue) : List[(String, String)] = {

    var list_of: List[(String, String)] = List()


    if (((json \ "pagination" \ "total_count").get.toString()).equals("0")) {
      return (List.empty[(String, String)])
    }

    else {

      for (i <- 0 to 4) {
        var post = (json \ "data" \ i \ "images" \ "original" \ "url").as[String]
        var title = (json \ "data" \ i \ "title").as[String]
        println(title)

        list_of = list_of :+ (post, title.capitalize)
      }

      return list_of
    }
  }

}
