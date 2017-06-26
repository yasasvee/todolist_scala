package models
//import reactivemongo.api.MongoConnection
import scala.util.Try
import javax.inject.Inject
import scala.concurrent.Future
import play.api.data._
import play.api.data.Forms._
import reactivemongo.bson._
import scala.concurrent.ExecutionContext.Implicits.global

case class Task (id: Option[BSONObjectID], label: String)

object Task{
		
	val taskForm = Form(
		mapping(
			"id" -> ignored[Option[BSONObjectID]](None),
			"label" -> nonEmptyText)(Task.apply)(Task.unapply)
	)	

def all(): List[Task] = Nil
implicit object TaskWriter extends BSONDocumentWriter[Task] {
    //toBSON
    def write(task: Task): BSONDocument = BSONDocument(
      "id" -> task.id.getOrElse(BSONObjectID.generate),
      "label" -> BSONString(task.label))
  }

  implicit object TaskReader extends BSONDocumentReader[Task] {
    //fromBSON
    def read(doc: BSONDocument): Task = {
      Task(
        doc.getAs[BSONObjectID]("id"),
        doc.getAs[BSONString]("label").get.value)
    }
  }  
/*	val uri = "mongodb://localhost:27017/testdb"
	val driver1 = new reactivemongo.api.MongoDriver
	val connection3 = driver1.connection(List("localhost"))
	val db = connection3.database("testdb")

	def all(): List[Task] = Nil

	def create(label: String){	
        val future = collection.insert(document)
        future.onComplete {
          case Failure(e) => throw e
          case Success(lastError) => {
            println("successfully inserted document with lastError = " + lastError)
          }
        }

	}

	def delete(id: Long){
		val selector = BSONDocument("firstName" -> "Jack")
		val remove = col.remove(selector)
	}
*/
}