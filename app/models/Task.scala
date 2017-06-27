package models
//import reactivemongo.api.MongoConnection
import play.api.data._
import play.api.data.Forms._
import reactivemongo.bson._

case class Task (id: Option[BSONObjectID], label: String, description: String)

object Task{
		
	val taskForm = Form(
		mapping(
			"id" -> ignored[Option[BSONObjectID]](None),
			"label" -> nonEmptyText,
      "description" -> nonEmptyText)(Task.apply)(Task.unapply))	

  //def all(): List[Task] = Nil

  implicit object TaskWriter extends BSONDocumentWriter[Task] {
    println("Writing tasks")
    def write(task: Task): BSONDocument = BSONDocument(
      "id" -> task.id.getOrElse(BSONObjectID.generate),
      "label" -> BSONString(task.label),
      "description" -> BSONString(task.description))
  }

  implicit object TaskReader extends BSONDocumentReader[Task] {
    println("Reading Tasks")
    def read(doc: BSONDocument): Task = {
      Task(
        doc.getAs[BSONObjectID]("id"),
        doc.getAs[BSONString]("label").get.value,
        doc.getAs[BSONString]("description").get.value)
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