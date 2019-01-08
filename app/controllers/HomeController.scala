package controllers

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import akka.stream.Materializer
import com.google.gson.Gson
import com.mohiva.play.silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.se.twothousandfortyeight.controller.turnBaseImpl.Turn
import de.htwg.se.twothousandfortyeight.controller.TurnMade
import de.htwg.se.twothousandfortyeight.model.gameModel.gameBaseImpl.Game
import javax.inject._
import play.api.libs.streams.ActorFlow
import play.api.mvc.{ AbstractController, AnyContent, ControllerComponents, _ }
import utils.auth.DefaultEnv
import javax.inject.Inject
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.{ LogoutEvent, Silhouette }
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import utils.auth.DefaultEnv

import scala.concurrent.Future
import scala.concurrent.Future
import scala.swing.Reactor

@Singleton
class HomeController @Inject() (cc: ControllerComponents, silhouette: Silhouette[DefaultEnv])(implicit system: ActorSystem, mat: Materializer, webJarsUtil: WebJarsUtil, assets: AssetsFinder) extends AbstractController(cc) with I18nSupport {
  val game = new Game
  val turn = new Turn
  val gson = new Gson

  //  def index() = Action {
  //    Ok(views.html.index())
  //  }

  def start() = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>

    turn.makeTurn(game, de.htwg.se.twothousandfortyeight.util.Utils.processKey('r'), Math.random(), Math.random())

    Future.successful(Ok(views.html.game(request.identity)))
  }

  def up() = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    turn.makeTurn(game, de.htwg.se.twothousandfortyeight.util.Utils.processKey('w'), Math.random(), Math.random())

    Future.successful(Ok(views.html.game(request.identity)))
  }

  def down() = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    turn.makeTurn(game, de.htwg.se.twothousandfortyeight.util.Utils.processKey('s'), Math.random(), Math.random())

    Future.successful(Ok(views.html.game(request.identity)))
  }

  def left() = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    turn.makeTurn(game, de.htwg.se.twothousandfortyeight.util.Utils.processKey('a'), Math.random(), Math.random())

    Future.successful(Ok(views.html.game(request.identity)))
  }

  def right() = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    turn.makeTurn(game, de.htwg.se.twothousandfortyeight.util.Utils.processKey('d'), Math.random(), Math.random())

    Future.successful(Ok(views.html.game(request.identity)))
  }

  def reset() = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    turn.makeTurn(game, de.htwg.se.twothousandfortyeight.util.Utils.processKey('r'), Math.random(), Math.random())

    Future.successful(Ok(views.html.game(request.identity)))
  }

  def undo() = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    turn.makeTurn(game, de.htwg.se.twothousandfortyeight.util.Utils.processKey('q'), Math.random(), Math.random())

    Future.successful(Ok(views.html.game(request.identity)))
  }

  def gameToJsonAjax() = Action {
    Ok(gson.toJson(game))
  }

  def gameToJsonWebSocket() = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef { out =>
      println("Connect received")
      WebSocketActorFactory.create(out)
    }
  }

  object WebSocketActorFactory {
    def create(out: ActorRef) = {
      Props(new WebSocketActor(out))
    }
  }

  class WebSocketActor(out: ActorRef) extends Actor with Reactor {
    listenTo(turn)

    out ! (gson.toJson(game))

    reactions += {
      case event: TurnMade => sendJsonToClient
    }

    def sendJsonToClient() = {
      println("Received event from Controller")
      out ! (gson.toJson(game))
      println("Sent Json to Client")
    }

    def receive = {
      case msg: String => {
        println("Got message from Client: " + msg)
      }
    }
  }

}
