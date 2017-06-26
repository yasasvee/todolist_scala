package controllers

import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import models.Task
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import reactivemongo.bson._
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json._, collection._
import play.modules.reactivemongo._
import javax.inject._
import reactivemongo.api.collections.bson
import reactivemongo.api.collections.bson.BSONCollection
import play.api.Play.current
import reactivemongo.api.Cursor
import reactivemongo.api.ReadPreference

import scala.concurrent._
import ExecutionContext.Implicits.global
import models._
import reactivemongo.core.commands.GetLastError
import scala.util.{ Failure, Success }
import scala.util.Try

//import scala.concurrent.{ ExecutionContext, Future }
//import reactivemongo.api.ReadPreference
//import reactivemongo.play.json._, collection._

class HomeController @Inject() (val reactiveMongoApi: ReactiveMongoApi)
    extends Controller with MongoController with ReactiveMongoComponents {

  def collection: BSONCollection = db.collection[BSONCollection]("tasks")
  //db.collection[JSONCollection]("tasks")

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
 /* def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
*/
  def index = Action {
  	Ok("Hello World")
  }

  def tasks = Action.async {
    import Task.TaskReader
    val taskForm = Task.taskForm
/*      val found = collection.find(BSONDocument()).cursor[Task]
      found.toList.map {
        tasks => Ok(views.html.index(tasks, taskForm))
      }
*/
     val cursor: Cursor[Task] = collection.
          find(BSONDocument()).
          cursor[Task](ReadPreference.primary)

        val futureTaskList: Future[List[Task]] = cursor.collect[List]()
        
        futureTaskList.map { tasks => Ok(views.html.index(tasks, taskForm)) }

  	//Ok(views.html.index(Task.all(), taskForm))
  }

  def newTask = Action { implicit request =>
    import Task.TaskWriter
    val taskForm = Task.taskForm
  	taskForm.bindFromRequest.fold(
  		errors => BadRequest(views.html.index(Task.all(), errors)),
  		task => { 
        println("Adding a task")
        //Task.create(label)
        val id = BSONObjectID.generate
        collection.insert(new Task(Option(id), task.label))
        Redirect(routes.HomeController.tasks)
  		}
  	)
  }

  def deleteTask(id: Option[BSONObjectID]) = TODO
}