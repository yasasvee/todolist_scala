package controllers

import models.Task
import play.api.mvc._
import reactivemongo.bson._
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo._
import javax.inject._
import reactivemongo.api.collections.bson.BSONCollection
import play.api.Play.current
import reactivemongo.api.Cursor

//i18n Error
import play.api.i18n.Messages.Implicits._

//Cannot find implicit execution context
import scala.concurrent._
import reactivemongo.api.ReadPreference
import ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }
import reactivemongo.api.commands.FindAndModifyCommand
//import collection.BatchCommands.FindAndModifyCommand.FindAndModifyResult
//import reactivemongo.api.collections.bson.BSONCollection.FindAndModifyResult
 //type mismatch;
 //found   : scala.util.Try[reactivemongo.bson.BSONObjectID]
 //required: reactivemongo.bson.BSONObjectID
import scala.util.Try

//As of Play 2.4 and above directly inheriting MongoController is not supported, need to inject as below
//Inject is just flagging or declaring a dependancy
class HomeController @Inject() (val reactiveMongoApi: ReactiveMongoApi)
    extends Controller with MongoController with ReactiveMongoComponents {

//Data is inserted/read from DB using collw=ections BSON->Binary JSON
  def collection: BSONCollection = db.collection[BSONCollection]("tasks")
  
 /* def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
*/
  def index = Action {
  	Ok("Please Navigate to /tasks to add TODO's")
  }

  def tasks = Action.async {
    import Task.TaskReader
    println("Inside Tasks")
    val taskForm = Task.taskForm
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
    println("Fecthed")
  	taskForm.bindFromRequest.fold(
	    errors => Redirect(routes.HomeController.tasks),
 		task => { 
        	println("Adding a task")
        	//Task.create(label)
        	val id = BSONObjectID.generate
        	collection.insert(new Task(Option(id), task.label, task.description))//Mark active button, bold tag/strikethrough, disable
        	Redirect(routes.HomeController.tasks)
  		}
  	)
    //Empty task name fix
    Redirect(routes.HomeController.tasks)
  }

  def deleteTask(id: String) = Action{ implicit request =>
    println("Deleting the Task")
    //To Make sure the task exists just in case
    val maybeOID: Try[BSONObjectID] = BSONObjectID.parse(id)
    if (maybeOID.isSuccess) {
      val futureRemove = collection.remove(BSONDocument("id" -> BSONObjectID(id))).onComplete {
        case Failure(e) => throw e
        case Success(_) => println("successfully removed document")
      }
    }
    Redirect(routes.HomeController.tasks)    
  }

  def markTaskActive(id: String) = Action{ implicit request =>
  	println("Marking the Task Active")
    import Task.TaskReader
    val taskForm = Task.taskForm
	val maybeOID: Try[BSONObjectID] = BSONObjectID.parse(id)
	if(maybeOID.isSuccess){	
		val selector = BSONDocument("id" -> BSONObjectID(id))
		val mod = BSONDocument("$set" -> BSONDocument("markActive" -> "Active"))
		val futureUpdate = collection.update(selector, mod, multi = true)
		futureUpdate.onComplete {
		  case Failure(e) => throw e
		  case Success(_) => println("successfully updated document")
	    Redirect(routes.HomeController.tasks)    
		}
	}
	Redirect(routes.HomeController.tasks)	
  }
  def markTaskDone(id: String) = TODO
  def markTaskDisabled(id: String) = TODO
}